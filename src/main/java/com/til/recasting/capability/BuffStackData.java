package com.til.recasting.capability;

import com.til.recasting.handler.BuffStackEventHandler;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.registry.RecastingBuffTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Buff叠加数据实现类
 * 使用 Map<BuffType, BuffEntry> 存储多个buff类型
 * 每个buff类型包含等级和最后更新时间
 */
public class BuffStackData implements IBuffStackData, INBTSerializable<CompoundTag> {
    
    private static final String KEY_BUFFS = "Buffs";
    private static final String KEY_LEVEL = "Level";
    private static final String KEY_LAST_UPDATE_TIME = "LastUpdateTime";

    private final Map<BuffType, BuffEntry> buffs = new HashMap<>();
    @Nullable
    private LivingEntity entity;

    /**
     * 设置关联的实体
     */
    public void setEntity(@Nullable LivingEntity entity) {
        this.entity = entity;
    }

    /**
     * 通知更新
     */
    private void notifyUpdate(BuffType buffType) {
        if (entity != null && !entity.level().isClientSide()) {
            BuffStackEventHandler.markForSync(entity, buffType);
        }
    }

    @Override
    public int getLevel(BuffType buffType, Level world) {
        if (buffType == null || world == null) {
            return 0;
        }
        
        BuffEntry entry = buffs.get(buffType);
        if (entry == null) {
            return 0;
        }
        
        // 如果不需要衰减，直接返回等级
        int decayPerTick = buffType.getDefaultDecayPerTick();
        if (decayPerTick <= 0) {
            return entry.getLevel();
        }
        
        // 计算衰减后的等级
        long currentTime = world.getGameTime();
        long lastUpdateTime = entry.getLastUpdateTime();
        if (lastUpdateTime <= 0 || currentTime <= lastUpdateTime) {
            return entry.getLevel();
        }
        
        long timePassed = currentTime - lastUpdateTime;
        // 计算经过的tick数，每decayPerTick个tick减少1级
        int decayAmount = (int) (timePassed / decayPerTick);
        int currentLevel = Math.max(0, entry.getLevel() - decayAmount);
        
        return currentLevel;
    }

    @Override
    public void setLevel(BuffType buffType, int level, Level world) {
        if (buffType == null || world == null) {
            return;
        }
        
        // 应用最大等级限制
        level = buffType.applyMaxLevel(Math.max(0, level));
        
        long currentTime = world.getGameTime();
        
        // 创建或更新条目，即使level为0也保留
        BuffEntry entry = buffs.get(buffType);
        if (entry == null) {
            buffs.put(buffType, new BuffEntry(level, currentTime));
        } else {
            entry.setLevel(level);
            entry.setLastUpdateTime(currentTime);
        }
        
        notifyUpdate(buffType);
    }

    @Override
    public Set<BuffType> getAllBuffTypes() {
        return buffs.keySet();
    }

    @Override
    @Nullable
    public BuffEntry getEntry(BuffType buffType) {
        if (buffType == null) {
            return null;
        }
        return buffs.get(buffType);
    }

    @Override
    public void setEntry(BuffType buffType, BuffEntry entry) {
        if (buffType == null) {
            return;
        }
        if (entry == null) {
            // 只有entry为null时才移除
            buffs.remove(buffType);
        } else {
            // 即使level为0也保留条目
            buffs.put(buffType, entry);
        }
        
        notifyUpdate(buffType);
    }

    @Override
    public void remove(BuffType buffType) {
        if (buffType != null) {
            buffs.remove(buffType);
            notifyUpdate(buffType);
        }
    }

    @Override
    public void clear() {
        // 清除时，需要同步所有被移除的buff类型
        Set<BuffType> removedTypes = new HashSet<>(buffs.keySet());
        buffs.clear();
        // 为每个被移除的buff类型发送同步消息
        for (BuffType buffType : removedTypes) {
            notifyUpdate(buffType);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        CompoundTag buffsTag = new CompoundTag();
        
        for (Map.Entry<BuffType, BuffEntry> entry : buffs.entrySet()) {
            ResourceLocation key = entry.getKey().getKey();
            if (key != null) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putInt(KEY_LEVEL, entry.getValue().getLevel());
                entryTag.putLong(KEY_LAST_UPDATE_TIME, entry.getValue().getLastUpdateTime());
                buffsTag.put(key.toString(), entryTag);
            }
        }
        
        tag.put(KEY_BUFFS, buffsTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag == null) {
            clear();
            return;
        }
        
        buffs.clear();
        
        if (tag.contains(KEY_BUFFS)) {
            CompoundTag buffsTag = tag.getCompound(KEY_BUFFS);
            var registry = RecastingBuffTypes.REGISTRY.get();
            if (registry != null) {
                for (String key : buffsTag.getAllKeys()) {
                    ResourceLocation buffTypeKey = ResourceLocation.tryParse(key);
                    if (buffTypeKey != null) {
                        BuffType buffType = registry.getValue(buffTypeKey);
                        if (buffType != null) {
                            CompoundTag entryTag = buffsTag.getCompound(key);
                            int level = entryTag.getInt(KEY_LEVEL);
                            long lastUpdateTime = entryTag.getLong(KEY_LAST_UPDATE_TIME);
                            // 即使level为0也保留条目
                            buffs.put(buffType, new BuffEntry(level, lastUpdateTime));
                        }
                    }
                }
            }
        }
    }
}

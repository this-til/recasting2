package com.til.recasting.capability;

import com.til.recasting.handler.BuffStackEventHandler;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import lombok.AllArgsConstructor;
import lombok.Data;
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
 * Buff叠加数据接口
 * 用于记录多个buff类型的叠加和随时间减少的效果
 * 使用 Map<BuffType, (level, lastTime)> 结构存储
 */
public interface IBuffStackData {

    /**
     * Buff条目，包含等级和最后更新时间
     */
    @Data
    @AllArgsConstructor
    class BuffEntry {
        private int level;
        private long lastUpdateTime;
        @Nullable
        private CompoundTag customData;

        public BuffEntry(int level, long lastUpdateTime) {
            this(level, lastUpdateTime, null);
        }
    }

    /**
     * 获取指定buff类型的等级
     */
    int getLevel(BuffType buffType, Level world);

    /**
     * 设置指定buff类型的等级
     */
    void setLevel(BuffType buffType, int level, Level world);

    /**
     * 获取所有buff类型的集合
     *
     * @return 所有buff类型的集合
     */
    Set<BuffType> getAllBuffTypes();

    /**
     * 获取指定buff类型的条目
     *
     * @param buffType BuffType对象
     * @return BuffEntry，如果不存在则返回null
     */
    @Nullable
    BuffEntry getEntry(BuffType buffType);

    /**
     * 获取指定buff类型的自定义数据，为空时创建并返回
     *
     * @param buffType BuffType对象
     * @param world    当前世界
     * @return 自定义数据标签
     */
    CompoundTag getOrCreateCustomData(BuffType buffType, Level world);

    /**
     * 直接设置指定buff类型的条目
     *
     * @param buffType BuffType对象
     * @param entry    BuffEntry对象
     */
    void setEntry(BuffType buffType, BuffEntry entry);

    /**
     * 移除指定buff类型
     *
     * @param buffType BuffType对象
     */
    void remove(BuffType buffType);

    /**
     * 清除所有数据
     */
    void clear();

    /**
     * Buff叠加数据实现类
     * 使用 Map<BuffType, BuffEntry> 存储多个buff类型
     * 每个buff类型包含等级和最后更新时间
     */
    class BuffStackData implements IBuffStackData, INBTSerializable<CompoundTag> {

        private static final String KEY_BUFFS = "Buffs";
        private static final String KEY_LEVEL = "Level";
        private static final String KEY_LAST_UPDATE_TIME = "LastUpdateTime";
        private static final String KEY_CUSTOM_DATA = "CustomData";

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
            int decayInterval = buffType.getDecayInterval();
            if (decayInterval <= 0) {
                return entry.getLevel();
            }

            // 计算衰减后的等级
            long currentTime = world.getGameTime();
            long lastUpdateTime = entry.getLastUpdateTime();
            if (lastUpdateTime <= 0 || currentTime <= lastUpdateTime) {
                return entry.getLevel();
            }

            long timePassed = currentTime - lastUpdateTime;
            // 计算经过的tick数，每decayInterval个tick减少1级
            int decayAmount = (int) (timePassed / decayInterval);

            return Math.max(0, entry.getLevel() - decayAmount);
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
        public CompoundTag getOrCreateCustomData(BuffType buffType, Level world) {
            if (buffType == null || world == null) {
                return new CompoundTag();
            }

            BuffEntry entry = buffs.get(buffType);
            if (entry == null) {
                entry = new BuffEntry(0, world.getGameTime(), new CompoundTag());
                buffs.put(buffType, entry);
                notifyUpdate(buffType);
                return entry.getCustomData();
            }

            CompoundTag customData = entry.getCustomData();
            if (customData == null) {
                customData = new CompoundTag();
                entry.setCustomData(customData);
                notifyUpdate(buffType);
            }
            return customData;
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
            for(BuffType buffType : removedTypes) {
                notifyUpdate(buffType);
            }
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            CompoundTag buffsTag = new CompoundTag();

            for(Map.Entry<BuffType, BuffEntry> entry : buffs.entrySet()) {
                ResourceLocation key = entry.getKey().getKey();
                if (key == null) {
                    continue;
                }
                CompoundTag entryTag = new CompoundTag();
                entryTag.putInt(KEY_LEVEL, entry.getValue().getLevel());
                entryTag.putLong(KEY_LAST_UPDATE_TIME, entry.getValue().getLastUpdateTime());
                if (entry.getValue().getCustomData() != null) {
                    entryTag.put(KEY_CUSTOM_DATA, entry.getValue().getCustomData());
                }
                buffsTag.put(key.toString(), entryTag);
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

            if (!tag.contains(KEY_BUFFS)) {
                return;
            }
            CompoundTag buffsTag = tag.getCompound(KEY_BUFFS);
            var registry = RecastingBuffTypes.REGISTRY.get();
            if (registry == null) {
                return;
            }
            for(String key : buffsTag.getAllKeys()) {
                ResourceLocation buffTypeKey = ResourceLocation.tryParse(key);
                if (buffTypeKey == null) {
                    continue;
                }
                BuffType buffType = registry.getValue(buffTypeKey);
                if (buffType == null) {
                    continue;
                }
                CompoundTag entryTag = buffsTag.getCompound(key);
                int level = entryTag.getInt(KEY_LEVEL);
                long lastUpdateTime = entryTag.getLong(KEY_LAST_UPDATE_TIME);
                CompoundTag customData = null;
                if (entryTag.contains(KEY_CUSTOM_DATA)) {
                    customData = entryTag.getCompound(KEY_CUSTOM_DATA);
                }
                // 即使level为0也保留条目
                buffs.put(buffType, new BuffEntry(level, lastUpdateTime, customData));
            }
        }
    }
}

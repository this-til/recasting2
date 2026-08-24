package com.til.recasting.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.til.recasting.handler.BuffStackEventHandler;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Buff叠加数据接口
 * 用于记录多个buff类型的叠加和随时间减少的效果
 * 使用 Map&lt;BuffType, (level, lastTime)&gt; 结构存储
 */
public interface IBuffStackData {

    /**
     * Buff条目，包含等级和最后更新时间
     */
    @Data
    @AllArgsConstructor
    class BuffEntry {
        public static final Codec<BuffEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        Codec.INT.fieldOf("Level").forGetter(BuffEntry::getLevel),
                        Codec.LONG.fieldOf("LastUpdateTime").forGetter(BuffEntry::getLastUpdateTime),
                        CompoundTag.CODEC.optionalFieldOf("CustomData")
                                .forGetter(entry -> Optional.ofNullable(entry.getCustomData())))
                .apply(instance, (level, lastUpdateTime, customData) ->
                        new BuffEntry(level, lastUpdateTime, customData.orElse(null))));

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
     * 使用 Map&lt;BuffType, BuffEntry&gt; 存储多个buff类型
     * 每个buff类型包含等级和最后更新时间
     */
    class BuffStackData implements IBuffStackData {

        public static final Codec<BuffStackData> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, BuffEntry.CODEC)
                .xmap(BuffStackData::fromSerializedMap, BuffStackData::toSerializedMap);

        private final Map<BuffType, BuffEntry> buffs = new HashMap<>();
        @Nullable
        private LivingEntity entity;

        private static BuffStackData fromSerializedMap(Map<ResourceLocation, BuffEntry> map) {
            BuffStackData data = new BuffStackData();
            for (Map.Entry<ResourceLocation, BuffEntry> entry : map.entrySet()) {
                BuffType buffType = RecastingBuffTypes.REGISTRY.get(entry.getKey());
                if (buffType != null) {
                    data.buffs.put(buffType, entry.getValue());
                }
            }
            return data;
        }

        private Map<ResourceLocation, BuffEntry> toSerializedMap() {
            Map<ResourceLocation, BuffEntry> map = new HashMap<>();
            for (Map.Entry<BuffType, BuffEntry> entry : buffs.entrySet()) {
                ResourceLocation key = entry.getKey().getKey();
                if (key != null) {
                    map.put(key, entry.getValue());
                }
            }
            return map;
        }

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

            int decayInterval = buffType.getDecayInterval();
            if (decayInterval <= 0) {
                return entry.getLevel();
            }

            long currentTime = world.getGameTime();
            long lastUpdateTime = entry.getLastUpdateTime();
            if (lastUpdateTime <= 0 || currentTime <= lastUpdateTime) {
                return entry.getLevel();
            }

            long timePassed = currentTime - lastUpdateTime;
            int decayAmount = (int) (timePassed / decayInterval);

            return Math.max(0, entry.getLevel() - decayAmount);
        }

        @Override
        public void setLevel(BuffType buffType, int level, Level world) {
            if (buffType == null || world == null) {
                return;
            }

            level = buffType.applyMaxLevel(Math.max(0, level));

            long currentTime = world.getGameTime();

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
                buffs.remove(buffType);
            } else {
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
            Set<BuffType> removedTypes = new HashSet<>(buffs.keySet());
            buffs.clear();
            for (BuffType buffType : removedTypes) {
                notifyUpdate(buffType);
            }
        }
    }
}

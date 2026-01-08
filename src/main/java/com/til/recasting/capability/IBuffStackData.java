package com.til.recasting.capability;

import com.til.recasting.registry.instance.BuffType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

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
     * @return 所有buff类型的集合
     */
    Set<BuffType> getAllBuffTypes();

    /**
     * 获取指定buff类型的条目
     * @param buffType BuffType对象
     * @return BuffEntry，如果不存在则返回null
     */
    @Nullable
    BuffEntry getEntry(BuffType buffType);

    /**
     * 直接设置指定buff类型的条目
     * @param buffType BuffType对象
     * @param entry BuffEntry对象
     */
    void setEntry(BuffType buffType, BuffEntry entry);

    /**
     * 移除指定buff类型
     * @param buffType BuffType对象
     */
    void remove(BuffType buffType);

    /**
     * 清除所有数据
     */
    void clear();
}

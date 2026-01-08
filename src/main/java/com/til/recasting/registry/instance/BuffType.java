package com.til.recasting.registry.instance;

import com.til.recasting.registry.RecastingBuffTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Buff类型
 * 用于标识不同类型的buff，支持扩展能力
 */
public class BuffType {
    
    /**
     * 每过多少tick减少一级（默认值，可以在注册时覆盖）
     * 例如：值为1表示每1tick减少1级，值为10表示每10tick减少1级
     * 0表示不衰减
     */
    private final int defaultDecayPerTick;
    
    /**
     * 最大等级（0表示无限制）
     */
    private final int maxLevel;

    /**
     * 创建BuffType，使用默认值
     */
    public BuffType() {
        this(0, 0);
    }

    /**
     * 创建BuffType
     * 
     * @param defaultDecayPerTick 每过多少tick减少一级，0表示不衰减
     * @param maxLevel 最大等级，0表示无限制
     */
    public BuffType(int defaultDecayPerTick, int maxLevel) {
        this.defaultDecayPerTick = Math.max(0, defaultDecayPerTick);
        this.maxLevel = Math.max(0, maxLevel);
    }

    /**
     * 获取默认的衰减间隔（每过多少tick减少一级）
     */
    public int getDefaultDecayPerTick() {
        return defaultDecayPerTick;
    }

    /**
     * 获取最大等级
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * 检查是否有最大等级限制
     */
    public boolean hasMaxLevel() {
        return maxLevel > 0;
    }

    /**
     * 应用等级限制
     * 
     * @param level 原始等级
     * @return 限制后的等级
     */
    public int applyMaxLevel(int level) {
        if (hasMaxLevel()) {
            return Math.min(level, maxLevel);
        }
        return level;
    }

    /**
     * 获取BuffType的ResourceLocation key
     */
    @Nullable
    public ResourceLocation getKey() {
        IForgeRegistry<BuffType> registry = RecastingBuffTypes.REGISTRY.get();
        if (registry != null) {
            return registry.getKey(this);
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BuffType other = (BuffType) obj;
        ResourceLocation thisKey = this.getKey();
        ResourceLocation otherKey = other.getKey();
        if (thisKey == null || otherKey == null) {
            return false;
        }
        return thisKey.equals(otherKey);
    }

    @Override
    public int hashCode() {
        ResourceLocation key = getKey();
        return key != null ? key.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        ResourceLocation key = getKey();
        if (key != null) {
            return key.toString();
        }
        return super.toString();
    }
}


package com.til.recasting.registry.instance;

import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Buff类型
 * 用于标识不同类型的buff，支持扩展能力
 */
@Getter
@AllArgsConstructor
public class BuffType {

    /**
     * 衰减间隔（每过多少tick减少一级）
     * 例如：值为1表示每1tick减少1级，值为10表示每10tick减少1级
     * 0表示不衰减
     */
    private final int decayInterval;

    /**
     * 最大等级（0表示无限制）
     */
    private final int maxLevel;

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

    public String getDescriptionId() {
        ResourceLocation key = Objects.requireNonNull(getKey());
        return "buff." + key.getNamespace() + "." + key.getPath();
    }

    public String toString() {
        return Objects.requireNonNull((RecastingBuffTypes.REGISTRY.get()).getKey(this)).toString();
    }


}


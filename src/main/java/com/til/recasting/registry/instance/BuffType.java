package com.til.recasting.registry.instance;

import com.til.recasting.registry.RecastingBuffTypes;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Buff 类型：可配置衰减/上限，构造期注册到游戏事件总线供子类实例监听。
 */
@Getter
@Setter
@Accessors(chain = true)
public class BuffType {

    /**
     * 衰减间隔（每过多少 tick 减少一级）；0 表示不衰减
     */
    protected int decayInterval;

    /**
     * 最大等级；0 表示无上限
     */
    protected int maxLevel;

    public BuffType() {
        decayInterval = 0;
        maxLevel = 0;
        NeoForge.EVENT_BUS.register(this);
    }

    public boolean hasMaxLevel() {
        return maxLevel > 0;
    }

    public int applyMaxLevel(int level) {
        if (hasMaxLevel()) {
            return Math.min(level, maxLevel);
        }
        return level;
    }

    @Nullable
    public ResourceLocation getKey() {
        return RecastingBuffTypes.REGISTRY.getKey(this);
    }

    public String getDescriptionId() {
        ResourceLocation key = Objects.requireNonNull(getKey());
        return "buff." + key.getNamespace() + "." + key.getPath();
    }

    @Override
    public String toString() {
        return Objects.requireNonNull(RecastingBuffTypes.REGISTRY.getKey(this)).toString();
    }
}

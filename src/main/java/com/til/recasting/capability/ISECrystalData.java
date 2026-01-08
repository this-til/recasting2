package com.til.recasting.capability;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * SE Crystal 数据接口
 * 用于存储 SE 结晶的特殊效果类型和等级信息
 */
public interface ISECrystalData {
    /**
     * 获取特殊效果类型
     * @return 特殊效果的 ResourceLocation，如果未设置则返回 null
     */
    @Nullable
    ResourceLocation getSpecialEffectType();

    /**
     * 设置特殊效果类型
     * @param specialEffectType 特殊效果的 ResourceLocation
     */
    void setSpecialEffectType(@Nullable ResourceLocation specialEffectType);

    /**
     * 获取特殊效果等级
     * @return 特殊效果等级，默认为 0
     */
    int getSpecialEffectLevel();

    /**
     * 设置特殊效果等级
     * @param level 特殊效果等级
     */
    void setSpecialEffectLevel(int level);

    /**
     * 检查是否已设置特殊效果
     * @return 如果已设置特殊效果类型则返回 true
     */
    default boolean hasSpecialEffect() {
        return getSpecialEffectType() != null;
    }

    /**
     * 清除所有数据
     */
    default void clear() {
        setSpecialEffectType(null);
        setSpecialEffectLevel(0);
    }
}


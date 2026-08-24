package com.til.recasting.item;

import com.til.recasting.util.Gradient;
import net.minecraft.world.item.ItemStack;

/**
 * 提供渐变颜色和时间缩放的接口。
 */
public interface IGradientColorProvider {

    Gradient getGradient(ItemStack itemStack, int level);

    float getTimeScale();
}

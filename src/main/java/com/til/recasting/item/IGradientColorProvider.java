package com.til.recasting.item;

import com.til.recasting.util.Gradient;
import net.minecraft.world.item.ItemStack;

/**
 * 提供渐变颜色和时间缩放的接口
 * 实现此接口的物品可以自定义渐变效果和时间缩放
 */
public interface IGradientColorProvider {

    /**
     * 获取渐变对象
     *
     * @return Gradient 对象，如果返回 null 则不使用渐变
     */
    Gradient getGradient(ItemStack itemStack, int level);

    /**
     * 获取时间缩放
     *
     * @return 时间缩放（游戏刻数，控制渐变速度。例如：20.0 = 1秒一个完整周期）
     */
    float getTimeScale();
}


package com.til.recasting.client.handler;

import com.til.recasting.Recasting;
import com.til.recasting.item.IGradientColorProvider;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.util.Gradient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

/**
 * 物品颜色处理器
 * 为不同颜色的魂火物品设置颜色，支持渐变和时间缩放
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ItemColorHandler {

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        // 注册所有支持渐变的物品
        // 使用统一的 ItemColor 处理器，从物品实例中读取 gradient 和 timeScale
        RecastingItems.getAllItems().stream()
                .map(RegistryObject::get)
                .filter(item -> item instanceof IGradientColorProvider)
                .forEach(item -> event.register(createGradientItemColor(), item));
    }

    /**
     * 创建使用渐变的 ItemColor 实例
     * 从物品实例中读取 gradient 和 timeScale
     * @return ItemColor 实例
     */
    private static ItemColor createGradientItemColor() {
        return (stack, tintIndex) -> {
            // tintIndex 0 通常是物品的主要纹理层
            if (tintIndex == 0) {
                Item item = stack.getItem();
                if (item instanceof IGradientColorProvider provider) {
                    Gradient gradient = provider.getGradient();
                    if (gradient != null) {
                        float timeScale = provider.getTimeScale();
                        
                        // 获取游戏时间
                        Level level = Minecraft.getInstance().level;
                        if (level != null) {
                            // 计算时间值（0.0-1.0），使用时间缩放
                            // timeScale 是一个完整周期所需的游戏刻数
                            long gameTime = level.getGameTime();
                            float time = (float) (gameTime % (long) timeScale) / timeScale;
                            
                            // 评估渐变并返回 RGB 颜色值
                            return gradient.evaluateToRGB(time);
                        }
                        // 如果没有世界，返回渐变的中间值
                        return gradient.evaluateToRGB(0.5f);
                    }
                }
            }
            // 默认返回白色（不染色）
            return 0xFFFFFF;
        };
    }
}


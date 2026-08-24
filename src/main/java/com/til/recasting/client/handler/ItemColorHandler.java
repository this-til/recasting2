package com.til.recasting.client.handler;

import com.til.recasting.Recasting;
import com.til.recasting.item.IGradientColorProvider;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.util.Gradient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

/**
 * 物品颜色处理器
 * 为不同颜色的魂火物品设置颜色，支持渐变和时间缩放
 */
@EventBusSubscriber(modid = Recasting.MODID, value = Dist.CLIENT)
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
     *
     * @return ItemColor 实例
     */
    private static ItemColor createGradientItemColor() {
        return (stack, tintIndex) -> {
            Item item = stack.getItem();
            if (!(item instanceof IGradientColorProvider provider)) {
                return 0xFFFFFF;
            }
            Gradient gradient = provider.getGradient(stack, tintIndex);
            if (gradient == null) {
                return 0xFFFFFF;
            }
            float timeScale = provider.getTimeScale();

            // 获取游戏时间
            Level level = Minecraft.getInstance().level;
            if (level == null) {
                return gradient.evaluateToRGB(0.5f);
            }

            long gameTime = level.getGameTime();
            float time = (float) (gameTime % (long) timeScale) / timeScale;
            return gradient.evaluateToRGB(time);
        };
    }
}


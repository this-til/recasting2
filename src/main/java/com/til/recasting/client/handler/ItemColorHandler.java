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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

/**
 * 物品颜色处理器，为支持渐变的魂火物品设置颜色。
 */
@EventBusSubscriber(modid = Recasting.MODID, value = Dist.CLIENT)
public final class ItemColorHandler {

    private ItemColorHandler() {
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        RecastingItems.getAllItems().stream()
                .map(holder -> holder.get())
                .filter(item -> item instanceof IGradientColorProvider)
                .forEach(item -> event.register(createGradientItemColor(), item));
    }

    private static ItemColor createGradientItemColor() {
        return (stack, tintIndex) -> {
            Item item = stack.getItem();
            if (!(item instanceof IGradientColorProvider provider)) {
                return 0xFFFFFFFF;
            }
            Gradient gradient = provider.getGradient(stack, tintIndex);
            if (gradient == null) {
                return 0xFFFFFFFF;
            }
            float timeScale = provider.getTimeScale();

            Level level = Minecraft.getInstance().level;
            if (level == null) {
                // ItemColor 按 ARGB 采样；evaluateToRGB 的 alpha=0 会使带 TintIndex 的 OBJ 全透明
                return gradient.evaluateToARGB(0.5f);
            }

            long gameTime = level.getGameTime();
            float time = (float) (gameTime % (long) timeScale) / timeScale;
            return gradient.evaluateToARGB(time);
        };
    }
}

package com.til.recasting.client;

import com.til.recasting.event.SlashBladeItemBarCollectEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.common.NeoForge;

import java.util.List;

/**
 * 统筹拔刀剑物品栏进度条：发收集事件后按注入顺序自下而上叠画。
 */
public final class SlashBladeItemBarDecorator implements IItemDecorator {

    public static final SlashBladeItemBarDecorator INSTANCE = new SlashBladeItemBarDecorator();

    private SlashBladeItemBarDecorator() {
    }

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        if (!(stack.getItem() instanceof ItemSlashBlade)) {
            return false;
        }

        SlashBladeItemBarCollectEvent event = new SlashBladeItemBarCollectEvent(stack);
        NeoForge.EVENT_BUS.post(event);

        List<SlashBladeItemBarCollectEvent.Bar> bars = event.getBars();
        if (bars.isEmpty()) {
            return false;
        }

        int x = xOffset + 2;
        int stackBottom = yOffset + 13;
        for (int i = 0; i < bars.size(); i++) {
            SlashBladeItemBarCollectEvent.Bar bar = bars.get(i);
            int y = stackBottom - i;
            int barWidth = (int) Math.round(13.0d * bar.progress());
            // 同层 1px：未填充段黑底，填充段着色；层间距 1px，避免层间黑线
            guiGraphics.fill(RenderType.guiOverlay(), x, y, x + 13, y + 1, 0xFF000000);
            if (barWidth > 0) {
                guiGraphics.fill(RenderType.guiOverlay(), x, y, x + barWidth, y + 1, bar.colorRgb() | 0xFF000000);
            }
        }
        // 仅最底层下方保留 1px 阴影（对齐原版耐久条）
        guiGraphics.fill(RenderType.guiOverlay(), x, stackBottom + 1, x + 13, stackBottom + 2, 0xFF000000);
        return false;
    }
}

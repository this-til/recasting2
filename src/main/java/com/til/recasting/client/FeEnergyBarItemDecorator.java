package com.til.recasting.client;

import com.til.recasting.handler.FeEnergyHelper;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;

/**
 * 在耐久条上方绘制 FE 能量条。
 */
public final class FeEnergyBarItemDecorator implements IItemDecorator {

    public static final FeEnergyBarItemDecorator INSTANCE = new FeEnergyBarItemDecorator();

    private FeEnergyBarItemDecorator() {
    }

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        if (!(stack.getItem() instanceof ItemSlashBlade)) {
            return false;
        }
        if (!FeEnergyHelper.hasFeCapacity(stack)) {
            return false;
        }

        int barWidth = FeEnergyHelper.getBarWidth(stack);
        int x = xOffset + 2;
        // 有耐久条时画在其上方（原版耐久在 y+13）；否则落在原版条位置
        int y = stack.isBarVisible() ? yOffset + 11 : yOffset + 13;

        guiGraphics.fill(RenderType.guiOverlay(), x, y, x + 13, y + 2, 0xFF000000);
        guiGraphics.fill(RenderType.guiOverlay(), x, y, x + barWidth, y + 1, FeEnergyHelper.FE_BAR_COLOR | 0xFF000000);
        return false;
    }
}

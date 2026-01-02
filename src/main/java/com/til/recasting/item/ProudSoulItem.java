package com.til.recasting.item;

import com.til.recasting.util.Gradient;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProudSoulItem extends Item implements IGradientColorProvider {

    private final Gradient gradient;
    private final float timeScale;

    public ProudSoulItem(Properties properties, @Nullable Gradient gradient, float timeScale) {
        super(properties);
        this.gradient = gradient;
        this.timeScale = timeScale;
    }

    @Override
    public @Nullable Gradient getGradient() {
        return gradient;
    }

    @Override
    public float getTimeScale() {
        return timeScale;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack item) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        // 添加介绍文本（从翻译系统获取）
        String descKey = stack.getDescriptionId() + ".desc";
        Component descComponent = Component.translatable(descKey);
        
        if (!descComponent.getString().equals(descKey)) {
            tooltip.add(descComponent.copy().withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }

        super.appendHoverText(stack, level, tooltip, flag);
    }
}

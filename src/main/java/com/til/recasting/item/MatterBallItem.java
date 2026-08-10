package com.til.recasting.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 物质球：无尽贪婪式仓储；右键将内容倒入玩家背包。
 */
public class MatterBallItem extends Item {

    public MatterBallItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack ball = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResultHolder.success(ball);
        }
        if (MatterBallStorage.isEmpty(ball)) {
            return InteractionResultHolder.pass(ball);
        }
        MatterBallStorage.extractToPlayer(ball, player);
        return InteractionResultHolder.consume(ball);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @Nullable Level level,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        String descKey = stack.getDescriptionId() + ".desc";
        Component descComponent = Component.translatable(descKey);
        if (!descComponent.getString().equals(descKey)) {
            tooltip.add(descComponent.copy().withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
        List<MatterBallStorage.StoredEntry> entries = MatterBallStorage.list(stack);
        if (!entries.isEmpty()) {
            tooltip.add(Component.translatable(
                    stack.getDescriptionId() + ".contents",
                    entries.size(),
                    MatterBallStorage.totalCount(stack)
            ).withStyle(ChatFormatting.DARK_AQUA));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }
}

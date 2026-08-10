package com.til.recasting.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 物质球：无尽贪婪式仓储；右键尽量填入背包，Shift+右键填入容器，空则消失；获取时与已有球合并。
 */
public class MatterBallItem extends Item {

    private static final int TOOLTIP_PREVIEW_LIMIT = 9;

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
            ball.setCount(0);
            return InteractionResultHolder.consume(ball);
        }
        MatterBallStorage.extractToPlayer(ball, player);
        consumeIfEmpty(ball);
        return InteractionResultHolder.consume(ball);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        Level level = context.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        if (blockEntity == null) {
            return InteractionResult.PASS;
        }
        Direction face = context.getClickedFace();
        IItemHandler handler = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, face).orElse(null);
        if (handler == null) {
            handler = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).orElse(null);
        }
        if (handler == null) {
            return InteractionResult.PASS;
        }
        ItemStack ball = context.getItemInHand();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (MatterBallStorage.isEmpty(ball)) {
            ball.setCount(0);
            return InteractionResult.CONSUME;
        }
        MatterBallStorage.extractToHandler(ball, handler);
        consumeIfEmpty(ball);
        return InteractionResult.CONSUME;
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
        int preview = Math.min(TOOLTIP_PREVIEW_LIMIT, entries.size());
        for(int i = 0; i < preview; i++) {
            MatterBallStorage.StoredEntry entry = entries.get(i);
            tooltip.add(Component.empty()
                    .append(entry.template().getHoverName())
                    .append("x" + entry.count())
                    .withStyle(ChatFormatting.DARK_AQUA));
        }
        if (entries.size() > TOOLTIP_PREVIEW_LIMIT) {
            tooltip.add(Component.translatable(
                    stack.getDescriptionId() + ".contents",
                    entries.size(),
                    MatterBallStorage.totalCount(stack)
            ).withStyle(ChatFormatting.DARK_GRAY));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    private static void consumeIfEmpty(@NotNull ItemStack ball) {
        if (MatterBallStorage.isEmpty(ball)) {
            ball.setCount(0);
        }
    }
}

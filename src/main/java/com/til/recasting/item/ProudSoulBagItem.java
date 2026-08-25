package com.til.recasting.item;

import com.til.recasting.inventory.ProudSoulBagMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 耀魂背包：存储 {@code recasting:proud_souls} 标签物品，右键打开终端式界面。
 */
public class ProudSoulBagItem extends Item {

    public ProudSoulBagItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack bag = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(
                    new SimpleMenuProvider(
                            (containerId, inventory, p) -> new ProudSoulBagMenu(containerId, inventory, hand),
                            bag.getHoverName()
                    ),
                    buf -> ProudSoulBagMenu.writeOpenData(buf, hand, bag, serverPlayer)
            );
        }
        return InteractionResultHolder.sidedSuccess(bag, level.isClientSide());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull Item.TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        String descKey = stack.getDescriptionId() + ".desc";
        Component descComponent = Component.translatable(descKey);
        if (!descComponent.getString().equals(descKey)) {
            tooltip.add(descComponent.copy().withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }
}

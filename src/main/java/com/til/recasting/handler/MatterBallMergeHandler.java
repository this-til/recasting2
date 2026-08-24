package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.item.MatterBallItem;
import com.til.recasting.item.MatterBallStorage;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.common.util.TriState;

/**
 * 拾取物质球时并入背包中已有的物质球。
 */
@EventBusSubscriber(modid = Recasting.MODID)
public final class MatterBallMergeHandler {

    private MatterBallMergeHandler() {
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onItemPickupPre(ItemEntityPickupEvent.Pre event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide()) {
            return;
        }
        ItemEntity itemEntity = event.getItemEntity();
        ItemStack stack = itemEntity.getItem();
        if (!(stack.getItem() instanceof MatterBallItem)) {
            return;
        }
        if (!MatterBallStorage.absorbIntoExisting(player, stack)) {
            return;
        }
        event.setCanPickup(TriState.FALSE);
        itemEntity.discard();
    }
}

package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.item.MatterBallItem;
import com.til.recasting.item.MatterBallStorage;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 拾取物质球时并入背包中已有的物质球。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MatterBallMergeHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityItemPickup(EntityItemPickupEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        ItemEntity itemEntity = event.getItem();
        ItemStack stack = itemEntity.getItem();
        if (!(stack.getItem() instanceof MatterBallItem)) {
            return;
        }
        if (!MatterBallStorage.absorbIntoExisting(player, stack)) {
            return;
        }
        event.setCanceled(true);
        itemEntity.discard();
    }
}

package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.inventory.ProudSoulBagMenu;
import com.til.recasting.item.ProudSoulBagStorage;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 拾取耀魂类物品时优先吸入耀魂背包。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ProudSoulBagCollectHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityItemPickup(EntityItemPickupEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        ItemEntity itemEntity = event.getItem();
        ItemStack stack = itemEntity.getItem();
        if (!ProudSoulBagStorage.isProudSoul(stack)) {
            return;
        }
        ItemStack bag = ProudSoulBagHelper.findBag(player);
        if (bag.isEmpty()) {
            return;
        }
        int before = stack.getCount();
        ProudSoulBagStorage.insert(bag, stack);
        if (stack.getCount() != before && player.containerMenu instanceof ProudSoulBagMenu menu) {
            menu.syncContentsToClient(player);
        }
        if (stack.isEmpty()) {
            event.setCanceled(true);
            itemEntity.discard();
            return;
        }
        if (stack.getCount() != before) {
            itemEntity.setItem(stack);
        }
    }
}

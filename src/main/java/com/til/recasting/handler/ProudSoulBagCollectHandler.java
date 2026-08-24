package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.inventory.ProudSoulBagMenu;
import com.til.recasting.item.ProudSoulBagStorage;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

/**
 * 拾取耀魂类物品时优先吸入耀魂背包。
 */
@EventBusSubscriber(modid = Recasting.MODID)
public final class ProudSoulBagCollectHandler {

    private ProudSoulBagCollectHandler() {
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onItemPickupPre(ItemEntityPickupEvent.Pre event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide()) {
            return;
        }
        ItemEntity itemEntity = event.getItemEntity();
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
            itemEntity.discard();
            return;
        }
        if (stack.getCount() != before) {
            itemEntity.setItem(stack);
        }
    }
}

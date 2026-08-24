package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.constant.RecastingLanguageKeys;
import com.til.recasting.constant.RecastingSlashBladeKeys;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.requir.SlashBladeItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.AnimalTameEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.UUID;

/**
 * 结缘剑「犬」：驯服狼并现实陪伴满 6 小时后，对其喂食耀魂碎片一次性领取。
 */
@EventBusSubscriber(modid = Recasting.MODID)
public final class JieYuanDogBondHandler {

    private static final int PROGRESS_CHECK_INTERVAL = 20;

    private JieYuanDogBondHandler() {
    }

    @SubscribeEvent
    public static void onAnimalTame(AnimalTameEvent event) {
        if (!(event.getAnimal() instanceof Wolf wolf)) {
            return;
        }
        if (!(event.getTamer() instanceof ServerPlayer player)) {
            return;
        }
        if (player.level().isClientSide()) {
            return;
        }

        var bond = RecastingAttachments.jieYuanDogBond(player);
        if (bond.isClaimed()) {
            return;
        }
        if (bond.isBondFulfilled()) {
            return;
        }
        bond.beginBond(wolf.getUUID(), System.currentTimeMillis());
        player.displayClientMessage(
                Component.translatable(RecastingLanguageKeys.MESSAGE_JIE_YUAN_DOG_BOND_STARTED),
                true
        );
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Wolf wolf)) {
            return;
        }
        if (!(wolf.getOwner() instanceof ServerPlayer player)) {
            return;
        }
        if (player.level().isClientSide()) {
            return;
        }

        var bond = RecastingAttachments.jieYuanDogBond(player);
        if (bond.isClaimed()) {
            return;
        }
        UUID bondedWolfUuid = bond.getBondedWolfUuid();
        if (bondedWolfUuid == null || !bondedWolfUuid.equals(wolf.getUUID())) {
            return;
        }
        if (bond.isBondFulfilled()) {
            bond.clearActiveBond();
            return;
        }
        bond.clearActiveBond();
        player.displayClientMessage(
                Component.translatable(RecastingLanguageKeys.MESSAGE_JIE_YUAN_DOG_BOND_LOST),
                true
        );
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (event.getEntity().tickCount % PROGRESS_CHECK_INTERVAL != 0) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        var bond = RecastingAttachments.jieYuanDogBond(player);
        if (bond.isClaimed() || bond.isBondFulfilled() || !bond.hasActiveBond()) {
            return;
        }
        long nowMillis = System.currentTimeMillis();
        if (!bond.isSurvivalComplete(nowMillis)) {
            return;
        }
        Wolf wolf = findBondedWolf(player, bond.getBondedWolfUuid());
        if (wolf == null || !wolf.isAlive()) {
            return;
        }
        bond.setBondFulfilled(true);
        player.displayClientMessage(
                Component.translatable(RecastingLanguageKeys.MESSAGE_JIE_YUAN_DOG_BOND_READY),
                true
        );
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (!(event.getTarget() instanceof Wolf wolf)) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!wolf.isTame() || !player.getUUID().equals(wolf.getOwnerUUID())) {
            return;
        }

        InteractionHand hand = event.getHand();
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(SlashBladeItems.PROUDSOUL.get())) {
            return;
        }

        var bond = RecastingAttachments.jieYuanDogBond(player);
        if (bond.isClaimed()) {
            player.displayClientMessage(
                    Component.translatable(RecastingLanguageKeys.MESSAGE_JIE_YUAN_DOG_ALREADY_CLAIMED),
                    true
            );
            event.setCanceled(true);
            return;
        }
        if (!bond.isBondFulfilled()) {
            if (!bond.hasActiveBond()) {
                player.displayClientMessage(
                        Component.translatable(RecastingLanguageKeys.MESSAGE_JIE_YUAN_DOG_NEED_BOND),
                        true
                );
                event.setCanceled(true);
                return;
            }
            long remainingMillis = bond.remainingSurvivalMillis(System.currentTimeMillis());
            player.displayClientMessage(
                    formatRemainingTime(remainingMillis),
                    true
            );
            event.setCanceled(true);
            return;
        }

        ItemStack blade = SlashBladeRegistryHelper.getBladeStack(player.level(), RecastingSlashBladeKeys.JIE_YUAN_DOG.location())
                .orElse(ItemStack.EMPTY);
        if (blade.isEmpty()) {
            return;
        }

        if (!player.isCreative()) {
            stack.shrink(1);
        }
        bond.setClaimed(true);
        bond.clearActiveBond();
        ItemHandlerHelper.giveItemToPlayer(player, blade.copy());
        player.level().playSound(
                null,
                wolf.blockPosition(),
                SoundEvents.WOLF_HOWL,
                SoundSource.NEUTRAL,
                1.0F,
                0.8F
        );
        player.displayClientMessage(
                Component.translatable(RecastingLanguageKeys.MESSAGE_JIE_YUAN_DOG_CLAIMED),
                true
        );
        event.setCanceled(true);
    }

    private static Wolf findBondedWolf(ServerPlayer player, UUID wolfUuid) {
        if (wolfUuid == null) {
            return null;
        }
        Entity entity = player.serverLevel().getEntity(wolfUuid);
        if (entity instanceof Wolf wolf
                && wolf.isAlive()
                && player.getUUID().equals(wolf.getOwnerUUID())) {
            return wolf;
        }
        return null;
    }

    private static Component formatRemainingTime(long remainingMillis) {
        long totalSeconds = (remainingMillis + 999L) / 1000L;
        long hours = totalSeconds / 3600L;
        long minutes = (totalSeconds % 3600L) / 60L;
        long seconds = totalSeconds % 60L;
        if (hours > 0L && minutes > 0L) {
            return Component.translatable(
                    RecastingLanguageKeys.MESSAGE_JIE_YUAN_DOG_REMAINING_HM,
                    hours,
                    minutes
            );
        }
        if (hours > 0L) {
            return Component.translatable(RecastingLanguageKeys.MESSAGE_JIE_YUAN_DOG_REMAINING_H, hours);
        }
        if (minutes > 0L && seconds > 0L) {
            return Component.translatable(
                    RecastingLanguageKeys.MESSAGE_JIE_YUAN_DOG_REMAINING_MS,
                    minutes,
                    seconds
            );
        }
        if (minutes > 0L) {
            return Component.translatable(RecastingLanguageKeys.MESSAGE_JIE_YUAN_DOG_REMAINING_M, minutes);
        }
        return Component.translatable(RecastingLanguageKeys.MESSAGE_JIE_YUAN_DOG_REMAINING_S, seconds);
    }
}

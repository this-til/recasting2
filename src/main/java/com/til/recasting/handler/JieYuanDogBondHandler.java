package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.capability.IJieYuanDogBond;
import com.til.recasting.capability.provider.JieYuanDogBondProvider;
import com.til.recasting.constant.RecastingLanguageKeys;
import com.til.recasting.constant.RecastingSlashBladeKeys;
import com.til.recasting.registry.requir.SlashBladeItems;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.UUID;

/**
 * 结缘剑「犬」：驯服狼并现实陪伴满 6 小时后，对其喂食耀魂碎片一次性领取。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class JieYuanDogBondHandler {

    private static final int PROGRESS_CHECK_INTERVAL = 20;

    @SubscribeEvent
    public static void onAttachCapabilities(net.minecraftforge.event.AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player)) {
            return;
        }
        JieYuanDogBondProvider provider = new JieYuanDogBondProvider();
        event.addCapability(Recasting.prefix("jie_yuan_dog_bond"), provider);
        event.addListener(provider::invalidate);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        LazyOptional<IJieYuanDogBond> oldOptional =
                event.getOriginal().getCapability(CapabilityRegistryHandler.JIE_YUAN_DOG_BOND);
        LazyOptional<IJieYuanDogBond> newOptional =
                event.getEntity().getCapability(CapabilityRegistryHandler.JIE_YUAN_DOG_BOND);
        oldOptional.ifPresent(oldData -> newOptional.ifPresent(newData -> newData.copyFrom(oldData)));
        event.getOriginal().invalidateCaps();
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

        player.getCapability(CapabilityRegistryHandler.JIE_YUAN_DOG_BOND).ifPresent(bond -> {
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
        });
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

        player.getCapability(CapabilityRegistryHandler.JIE_YUAN_DOG_BOND).ifPresent(bond -> {
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
        });
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (event.player.level().isClientSide()) {
            return;
        }
        if (event.player.tickCount % PROGRESS_CHECK_INTERVAL != 0) {
            return;
        }
        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }

        player.getCapability(CapabilityRegistryHandler.JIE_YUAN_DOG_BOND).ifPresent(bond -> {
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
        });
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

        player.getCapability(CapabilityRegistryHandler.JIE_YUAN_DOG_BOND).ifPresent(bond -> {
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

            ItemStack blade = SlashBladeRegistryHelper.getDefinition(player.level(), RecastingSlashBladeKeys.JIE_YUAN_DOG.location())
                    .map(SlashBladeDefinition::getBlade)
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
        });
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

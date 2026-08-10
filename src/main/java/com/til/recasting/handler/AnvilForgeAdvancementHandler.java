package com.til.recasting.handler;

import com.til.recasting.advancement.ForgeSeAction;
import com.til.recasting.advancement.RecastingCriteriaTriggers;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.se.ExtendedSpecialEffect;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

import static com.til.recasting.Recasting.MODID;

/**
 * 铁砧完成 SE 相关操作后授予锻造成就。
 */
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AnvilForgeAdvancementHandler {

    private AnvilForgeAdvancementHandler() {
    }

    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        ItemStack output = event.getOutput();
        if (left.isEmpty() || right.isEmpty() || output.isEmpty()) {
            return;
        }

        if (right.is(RecastingItems.GATHERING_PARTING_VARIANT.get())
                && left.getItem() instanceof ItemSlashBlade
                && output.is(RecastingItems.SE_CRYSTAL.get())) {
            trigger(player, ForgeSeAction.EXTRACT_SPECIAL);
            return;
        }

        if (right.is(RecastingItems.ABYSS_FLAME.get()) && left.getItem() instanceof ItemSlashBlade) {
            Optional<BladeSpecialEffectHelper.EffectEntry> before =
                    BladeSpecialEffectHelper.findFirstSpecialEffect(left);
            Optional<BladeSpecialEffectHelper.EffectEntry> after =
                    BladeSpecialEffectHelper.findFirstSpecialEffect(output);
            if (before.isPresent() && (after.isEmpty() || !before.get().id().equals(after.get().id()))) {
                trigger(player, ForgeSeAction.ERASE_SE);
            }
            return;
        }

        if (!right.is(RecastingItems.SE_CRYSTAL.get()) || !(left.getItem() instanceof ItemSlashBlade)) {
            return;
        }

        right.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA).ifPresent(crystalData -> {
            if (!crystalData.hasSpecialEffect()) {
                return;
            }
            ResourceLocation seLocation = crystalData.getSpecialEffectType();
            if (seLocation == null) {
                return;
            }
            SpecialEffect specialEffect = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get()
                    .getValue(seLocation);
            if (!(specialEffect instanceof ExtendedSpecialEffect extendedSE)) {
                return;
            }

            int crystalLevel = crystalData.getSpecialEffectLevel();
            if (crystalLevel == 0) {
                trigger(player, ForgeSeAction.ERASE_SE);
                return;
            }

            if (extendedSE.isSpecial()) {
                Optional<BladeSpecialEffectHelper.EffectEntry> beforeSpecial =
                        BladeSpecialEffectHelper.findFirstSpecialEffect(left);
                if (beforeSpecial.isPresent() && !beforeSpecial.get().id().equals(seLocation)) {
                    trigger(player, ForgeSeAction.SWAP_SPECIAL);
                }
            }

            trigger(player, ForgeSeAction.ENGRAVE_ANY);

            if (!extendedSE.isSpecial() && crystalLevel >= extendedSE.getMaxLevel()) {
                trigger(player, ForgeSeAction.ENGRAVE_MAX_NORMAL);
            }

            if (BladeSpecialEffectHelper.matchesFourNormalOneSpecial(output)) {
                trigger(player, ForgeSeAction.LAYOUT_FOUR_NORMAL_ONE_SPECIAL);
            }
            if (BladeSpecialEffectHelper.matchesFourMaxNormalOneSpecial(output)) {
                trigger(player, ForgeSeAction.LAYOUT_FOUR_MAX_NORMAL_ONE_SPECIAL);
            }
        });
    }

    private static void trigger(ServerPlayer player, ForgeSeAction action) {
        RecastingCriteriaTriggers.FORGE_SE_ACTION.trigger(player, action);
    }
}

package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.registry.SpecialEffectsRegistry;
import com.til.recasting.registry.se.EmperorLineStats;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 屠巫 / 屠巫λ / 人皇 / 人皇λ 同一条强度阶梯。
 */
@EventBusSubscriber(modid = Recasting.MODID)
public final class EmperorLineSeHelper {

    public record ActiveLine(
            ItemStack blade,
            ISlashBladeState state,
            SpecialEffect effect,
            EmperorLineStats stats
    ) {
        public boolean isEmperor() {
            return stats.getLineGrade() >= 2;
        }
    }

    private EmperorLineSeHelper() {
    }

    @Nullable
    public static ActiveLine resolveHighest(LivingEntity entity) {
        InventorySlashBladeSeHelper.BladeSeHit hit = InventorySlashBladeSeHelper.findFirstInInventory(
                entity,
                SpecialEffectsRegistry.HUMAN_EMPEROR_DOMAIN_LAMBDA.getId(),
                SpecialEffectsRegistry.HUMAN_EMPEROR_DOMAIN.getId(),
                SpecialEffectsRegistry.TU_WU_BLOOD_CURSE_LAMBDA.getId(),
                SpecialEffectsRegistry.TU_WU_BLOOD_CURSE.getId()
        );
        if (hit == null) {
            return null;
        }

        ResourceLocation effectId = hit.effectId();
        if (SpecialEffectsRegistry.HUMAN_EMPEROR_DOMAIN_LAMBDA.getId().equals(effectId)) {
            return toActive(hit.blade(), hit.state(), SpecialEffectsRegistry.HUMAN_EMPEROR_DOMAIN_LAMBDA.get());
        }
        if (SpecialEffectsRegistry.HUMAN_EMPEROR_DOMAIN.getId().equals(effectId)) {
            return toActive(hit.blade(), hit.state(), SpecialEffectsRegistry.HUMAN_EMPEROR_DOMAIN.get());
        }
        if (SpecialEffectsRegistry.TU_WU_BLOOD_CURSE_LAMBDA.getId().equals(effectId)) {
            return toActive(hit.blade(), hit.state(), SpecialEffectsRegistry.TU_WU_BLOOD_CURSE_LAMBDA.get());
        }
        if (SpecialEffectsRegistry.TU_WU_BLOOD_CURSE.getId().equals(effectId)) {
            return toActive(hit.blade(), hit.state(), SpecialEffectsRegistry.TU_WU_BLOOD_CURSE.get());
        }
        return null;
    }

    @Nullable
    private static ActiveLine toActive(ItemStack blade, ISlashBladeState state, SpecialEffect effect) {
        if (!(effect instanceof EmperorLineStats stats)) {
            return null;
        }
        return new ActiveLine(blade, state, effect, stats);
    }

    @Nullable
    public static ActiveLine resolveHighestEmperor(LivingEntity entity) {
        ActiveLine active = resolveHighest(entity);
        if (active == null || !active.isEmperor()) {
            return null;
        }
        return active;
    }

    public static boolean isActiveEmperorEffect(LivingEntity entity, SpecialEffect effect) {
        ActiveLine active = resolveHighestEmperor(entity);
        return active != null && active.effect() == effect;
    }

    @SubscribeEvent
    public static void onAttackAmplifier(AttackAmplifierEvent event) {
        if (event.getAttacker().level().isClientSide()) {
            return;
        }
        ActiveLine active = resolveHighest(event.getAttacker());
        if (active == null) {
            return;
        }
        event.addModifiedRatioAmplifier(active.stats().getDamageAmplifier());
    }

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }
        if (entity.level().isClientSide()) {
            return;
        }
        ActiveLine active = resolveHighest(entity);
        if (active == null) {
            return;
        }
        if (entity instanceof Player player) {
            tryRestoreFood(player, active);
        }
    }

    private static void tryRestoreFood(Player player, ActiveLine active) {
        int cost = active.stats().getFoodProudCost();
        int restore = active.stats().getFoodRestore();
        if (cost <= 0 || restore <= 0) {
            return;
        }
        ISlashBladeState state = active.state();
        if (state.getProudSoulCount() < cost) {
            return;
        }
        FoodData foodData = player.getFoodData();
        int foodLevel = foodData.getFoodLevel();
        if (foodLevel >= 20) {
            return;
        }
        foodData.setFoodLevel(Math.min(20, foodLevel + restore));
        state.setProudSoulCount(state.getProudSoulCount() - cost);
    }
}

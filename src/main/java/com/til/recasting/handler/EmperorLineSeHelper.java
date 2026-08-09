package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.registry.SpecialEffectsRegistry;
import com.til.recasting.registry.se.EmperorLineStats;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

/**
 * 屠巫 / 屠巫λ / 人皇 / 人皇λ 同一条强度阶梯。
 * <p>
 * 阶梯（高优先）：轩辕λ &gt; 轩辕 &gt; 屠巫λ &gt; 屠巫。
 * 具体数值在 {@link SpecialEffectsRegistry} 注册时写入各 SE 实例。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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

    /**
     * 解析当前实体可用的最高阶梯（轩辕背包优先于屠巫手持）。
     */
    @Nullable
    public static ActiveLine resolveHighest(LivingEntity entity) {
        InventorySlashBladeSeHelper.BladeSeHit emperorLambda = InventorySlashBladeSeHelper.findFirstInInventory(
                entity,
                SpecialEffectsRegistry.HUMAN_EMPEROR_DOMAIN_LAMBDA
        );
        if (emperorLambda != null) {
            return toActive(emperorLambda.blade(), emperorLambda.state(), SpecialEffectsRegistry.HUMAN_EMPEROR_DOMAIN_LAMBDA.get());
        }

        InventorySlashBladeSeHelper.BladeSeHit emperor = InventorySlashBladeSeHelper.findFirstInInventory(
                entity,
                SpecialEffectsRegistry.HUMAN_EMPEROR_DOMAIN
        );
        if (emperor != null) {
            return toActive(emperor.blade(), emperor.state(), SpecialEffectsRegistry.HUMAN_EMPEROR_DOMAIN.get());
        }

        ItemStack main = entity.getMainHandItem();
        if (!main.isEmpty() && main.getItem() instanceof ItemSlashBlade) {
            if (InventorySlashBladeSeHelper.hasSpecialEffect(main, SpecialEffectsRegistry.TU_WU_BLOOD_CURSE_LAMBDA)) {
                ISlashBladeState state = main.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
                if (state != null) {
                    return toActive(main, state, SpecialEffectsRegistry.TU_WU_BLOOD_CURSE_LAMBDA.get());
                }
            }
            if (InventorySlashBladeSeHelper.hasSpecialEffect(main, SpecialEffectsRegistry.TU_WU_BLOOD_CURSE)) {
                ISlashBladeState state = main.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
                if (state != null) {
                    return toActive(main, state, SpecialEffectsRegistry.TU_WU_BLOOD_CURSE.get());
                }
            }
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
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Player player = event.player;
        if (player.level().isClientSide()) {
            return;
        }
        ActiveLine active = resolveHighest(player);
        if (active == null) {
            return;
        }
        tryRestoreFood(player, active);
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

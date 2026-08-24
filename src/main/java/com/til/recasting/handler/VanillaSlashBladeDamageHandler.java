package com.til.recasting.handler;

import com.til.recasting.Config;
import com.til.recasting.Recasting;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.mixin.DamageSourcesAccessor;
import com.til.recasting.registry.RecastingAttackTypes;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * 原版 SlashBlade 伤害加成事件监听器
 * 将原版的伤害计算逻辑（各种加成的乘区）独立处理
 */
@EventBusSubscriber(modid = Recasting.MODID)
public final class VanillaSlashBladeDamageHandler {

    private VanillaSlashBladeDamageHandler() {
    }

    @SubscribeEvent
    public static void onAttackAmplifier(AttackAmplifierEvent event) {

        ItemStack weapon = event.getAttacker().getMainHandItem();

        if (event.getTarget() instanceof LivingEntity living) {
            int smiteLevel = MathHelper.getEnchantmentLevel(weapon, Enchantments.SMITE);
            if (smiteLevel > 0 && living.getType().is(EntityTypeTags.SENSITIVE_TO_SMITE)) {
                event.addModifiedRatioAmplifier((float) (smiteLevel * Config.SMITE_ATTACK_BONUS.get()));
            }

            int baneOfArthropodsLevel = MathHelper.getEnchantmentLevel(weapon, Enchantments.BANE_OF_ARTHROPODS);
            if (baneOfArthropodsLevel > 0 && living.getType().is(EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS)) {
                event.addModifiedRatioAmplifier((float) (baneOfArthropodsLevel * Config.BANE_OF_ARTHROPODS_ATTACK_BONUS.get()));
            }
        }

        float rankLevel = event.getAttacker()
                .getData(CapabilityConcentrationRank.RANK_POINT.get())
                .getRankLevel(event.getAttacker().level().getGameTime());

        float rankMaxBonus = Config.RANK_MAX_BONUS.get().floatValue();
        float rankBonus = Math.min(rankLevel / 6.0f * rankMaxBonus, rankMaxBonus);
        if (rankBonus > 0) {
            event.addModifiedRatioAmplifier(rankBonus);
        }

        Entity target = event.getTarget();
        boolean isSlash = event.getAttackTypeList().contains(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get());
        boolean isSummonedSword = event.getAttackTypeList().contains(RecastingAttackTypes.SUMMOND_SWORD_ATTACK.get());
        if (isSlash && !target.fireImmune()) {
            int fireAspect = MathHelper.getEnchantmentLevel(weapon, Enchantments.FIRE_ASPECT);
            if (fireAspect > 0) {
                DamageSourcesAccessor accessor = (DamageSourcesAccessor) event.getAttacker().damageSources();
                DamageSource damageSource = accessor.callSource(DamageTypes.ON_FIRE, target, event.getAttacker());
                event.addDamageSourceInfo(
                        damageSource,
                        new com.til.recasting.util.DamageStructure(0.0f, (float) (Config.FIRE_ASPECT_DAMAGE.get() * fireAspect))
                );
            }
        }

        if (isSummonedSword && !target.fireImmune()) {
            int flameLevel = MathHelper.getEnchantmentLevel(weapon, Enchantments.FLAME);
            if (flameLevel > 0) {
                DamageSourcesAccessor accessor = (DamageSourcesAccessor) event.getAttacker().damageSources();
                DamageSource damageSource = accessor.callSource(DamageTypes.ON_FIRE, target, event.getAttacker());
                event.addDamageSourceInfo(
                        damageSource,
                        new com.til.recasting.util.DamageStructure(0.0f, (float) (Config.FLAME_ARROWS_DAMAGE.get() * flameLevel))
                );
            }
        }

        int powerLevel = MathHelper.getEnchantmentLevel(weapon, Enchantments.POWER);
        if (powerLevel > 0 && isSummonedSword) {
            event.addModifiedRatioAmplifier((float) (Config.POWER_ATTACK_BONUS.get() * powerLevel));
        }

        BladeStateAccess.of(event.getItem()).ifPresent(state -> {
            int refine = state.getRefine();
            if (refine > 0) {
                double max = Config.REFINE_ATTACK_BONUS_MAX.get();
                double half = Config.REFINE_ATTACK_BONUS_HALF.get();
                event.addModifiedRatioAmplifier((float) (max * refine / (refine + half)));
            }

            int killCount = state.getKillCount();
            if (killCount > 1000) {
                event.addMechanismModifiedRatioAmplifier(Config.THOUSAND_KILL_ATTACK_BONUS.get().floatValue());
            }
            if (killCount > 10000) {
                event.addMechanismModifiedRatioAmplifier(Config.TEN_THOUSAND_KILL_ATTACK_BONUS.get().floatValue());
            }
            if (killCount > 100000) {
                event.addMechanismModifiedRatioAmplifier(Config.HUNDRED_THOUSAND_KILL_ATTACK_BONUS.get().floatValue());
            }
            if (killCount > 1000000) {
                event.addMechanismModifiedRatioAmplifier(Config.MILLION_KILL_ATTACK_BONUS.get().floatValue());
            }

            if (refine > 1000) {
                event.addMechanismModifiedRatioAmplifier(Config.THOUSAND_REFINE_ATTACK_BONUS.get().floatValue());
            }
            if (refine > 10000) {
                event.addMechanismModifiedRatioAmplifier(Config.TEN_THOUSAND_REFINE_ATTACK_BONUS.get().floatValue());
            }
        });

        double configDamageMultiplier = SlashBladeConfig.SLASHBLADE_DAMAGE_MULTIPLIER.get();
        if (!MathHelper.epsilonEquals(configDamageMultiplier, 1.0)) {
            event.addMechanismModifiedRatioAmplifier((float) (configDamageMultiplier - 1));
        }
    }
}

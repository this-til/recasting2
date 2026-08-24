package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.registry.se.*;
import com.til.recasting.util.NumberPack;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Special Effects (SE) 注册表。
 */
public final class SpecialEffectsRegistry {

    public static final DeferredRegister<SpecialEffect> SPECIAL_EFFECT =
            DeferredRegister.create(SpecialEffect.REGISTRY_KEY, Recasting.MODID);

    public static final DeferredHolder<SpecialEffect, SpecialEffect> COOPERATE_WITH =
            registerExtendedSE("cooperate_with", CooperateWithSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> CROSS_CHOP =
            registerExtendedSE("cross_chop", CrossChopSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> DRIVE_RELEASE =
            registerExtendedSE("drive_release", DriveReleaseSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> GROWTH =
            registerExtendedSE("growth", GrowthSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> LIFE_STEAL =
            registerExtendedSE("life_steal", LifeStealSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> REGRESSION =
            registerExtendedSE("regression", RegressionSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> JUDGEMENT =
            registerExtendedSE("judgement", JudgementSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> THUNDERSTORM =
            registerExtendedSE("thunderstorm", ThunderstormSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> THUNDER_GODS_WRATH =
            registerExtendedSE("thunder_gods_wrath", ThunderGodsWrathSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> IONIZATION =
            registerExtendedSE("ionization", IonizationSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> ENERGY_STORAGE =
            registerExtendedSE("energy_storage", EnergyStorageSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> THUNDER_CLOUD =
            registerExtendedSE("thunder_cloud", ThunderCloudSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> IMPACT =
            registerExtendedSE("impact", ImpactSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> OVERLOAD =
            registerExtendedSE("overload", OverloadSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> RESIST =
            registerExtendedSE("resist", ResistSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> SEVER_BREAK =
            registerExtendedSE("sever_break", SeverBreakSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> STORM =
            registerExtendedSE("storm", StormSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> STORM_VARIANT =
            registerExtendedSE("storm_variant", StormVariantSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> SPLIT =
            registerExtendedSE("split", SplitSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> SPIRAL =
            registerExtendedSE("spiral", SpiralSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> FRAGMENT =
            registerExtendedSE("fragment", FragmentSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> TEAR =
            registerExtendedSE("tear", TearSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> WHIRLWIND =
            registerExtendedSE("whirlwind", WhirlwindSpecialEffect::new);
    public static final DeferredHolder<SpecialEffect, SpecialEffect> ANNIHILATION =
            registerExtendedSE("annihilation", AnnihilationSpecialEffect::new);

    public static final DeferredHolder<SpecialEffect, SpecialEffect> GREAT_VOID =
            registerExtendedSE("great_void", () -> new AttackAmplifierSpecialEffect(
                    RecastingAttackTypes.SUMMOND_SWORD_ATTACK, new NumberPack(0.1f, 0.1f)));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> SHARP_BLADE =
            registerExtendedSE("sharp_blade", () -> new AttackAmplifierSpecialEffect(
                    RecastingAttackTypes.SLASH_EFFECT_ATTACK, new NumberPack(0.1f, 0.1f)));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> SHOCK =
            registerExtendedSE("shock", () -> new AttackAmplifierSpecialEffect(
                    RecastingAttackTypes.JUDGEMENT_CUT_ATTACK, new NumberPack(0.2f, 0.15f)));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> SWORD_QI_MASTERY =
            registerExtendedSE("sword_qi_mastery", () -> new AttackAmplifierSpecialEffect(
                    RecastingAttackTypes.DRIVE_ATTACK, new NumberPack(0.2f, 0.15f)));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> THUNDER_STRIKE =
            registerExtendedSE("thunder_strike", () -> new AttackAmplifierSpecialEffect(
                    RecastingAttackTypes.LIGHTNING_ATTACK, new NumberPack(0.2f, 0.15f)));

    public static final DeferredHolder<SpecialEffect, SpecialEffect> BLACK_ROSE =
            registerExtendedSE("black_rose", () -> new BlackRoseSpecialEffect().setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> STAR_BLINK =
            registerExtendedSE("star_blink", () -> new StarBlinkSpecialEffect().setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> STAR_BLINK_LAMBDA =
            registerExtendedSE("star_blink_lambda", () -> new StarBlinkSpecialEffect().setAddLevel(2).setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> COLOR_DYE =
            registerExtendedSE("color_dye", () -> new ColorDyeSpecialEffect().setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> RESOLVE =
            registerExtendedSE("resolve", () -> new ResolveSpecialEffect().setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> RESOLVE_LAMBDA =
            registerExtendedSE("resolve_lambda", () -> new ResolveSpecialEffect().setDamageRatio(1.5f).setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> FLAME_FOAM =
            registerExtendedSE("flame_foam", () -> new FlameFoamSpecialEffect().setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> FLAME_FOAM_LAMBDA =
            registerExtendedSE("flame_foam_lambda", () -> new FlameFoamSpecialEffect()
                    .setHealthDamageRatio(0.015f)
                    .setAddSoulBurnProbability(0.2f)
                    .setMaxLevel(1)
                    .setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> GOLDEN_HALBERD =
            registerExtendedSE("golden_halberd", () -> new GoldenHalberdSpecialEffect()
                    .setMaxStacks(12).setBurstRatio(1.5f).setStacksPerHit(1).setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> GOLDEN_HALBERD_LAMBDA =
            registerExtendedSE("golden_halberd_lambda", () -> new GoldenHalberdSpecialEffect()
                    .setMaxStacks(12).setBurstRatio(2.0f).setStacksPerHit(2).setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> TEA_AROMA =
            registerExtendedSE("tea_aroma", () -> new TeaAromaSpecialEffect()
                    .setStoreRatio(0.2f).setDelayTicks(30).setDriveBonusStacks(10).setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> TEA_AROMA_LAMBDA =
            registerExtendedSE("tea_aroma_lambda", () -> new TeaAromaSpecialEffect()
                    .setStoreRatio(0.3f).setDelayTicks(30).setDriveBonusStacks(15).setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> PHOTON_SCAR =
            registerExtendedSE("photon_scar", () -> new PhotonScarSpecialEffect().setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> LONG_SKY_SUNSET =
            registerExtendedSE("long_sky_sunset", () -> new LongSkySunsetSpecialEffect().setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> STUN =
            registerExtendedSE("stun", () -> new StunSpecialEffect().setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> FERTILIZE =
            registerExtendedSE("fertilize", () -> new FertilizeSpecialEffect().setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> EAT =
            registerExtendedSE("eat", () -> new EatSpecialEffect().setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> ENLARGE =
            registerExtendedSE("enlarge", () -> new EnlargeSpecialEffect().setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> STATIC_AFTERGLOW =
            registerExtendedSE("static_afterglow", () -> new StaticAfterglowSpecialEffect().setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> STATIC_AFTERGLOW_LAMBDA =
            registerExtendedSE("static_afterglow_lambda", () -> new StaticAfterglowSpecialEffect()
                    .setLightningDamageRatio(0.4f)
                    .setDamageCooldownTicks(3)
                    .setChainChance(0.15f)
                    .setChainCooldownTicks(10)
                    .setMaxLevel(1)
                    .setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> TREASURE_BARRAGE =
            registerExtendedSE("treasure_barrage", () -> new TreasureBarrageSpecialEffect().setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> TREASURE_BARRAGE_LAMBDA =
            registerExtendedSE("treasure_barrage_lambda", () -> new TreasureBarrageSpecialEffect()
                    .setCooldownTicks(40)
                    .setMaxLevel(1)
                    .setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> COMMAND_MAPPING =
            registerExtendedSE("command_mapping", () -> new CommandMappingSpecialEffect()
                    .setExtraTriggers(2)
                    .setDelayTicks(20)
                    .setMaxLevel(1)
                    .setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> COMMAND_MAPPING_LAMBDA =
            registerExtendedSE("command_mapping_lambda", () -> new CommandMappingSpecialEffect()
                    .setExtraTriggers(3)
                    .setDelayTicks(20)
                    .setMaxLevel(1)
                    .setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> TU_WU_BLOOD_CURSE =
            registerExtendedSE("tu_wu_blood_curse", () -> new TuWuBloodCurseSpecialEffect()
                    .setLineGrade(0)
                    .setDamageAmplifier(0.33f)
                    .setProudPerDamage(200)
                    .setMaxProudPerHit(5000)
                    .setProtectThreshold(5000)
                    .setFoodProudCost(500)
                    .setFoodRestore(1)
                    .setMaxLevel(1)
                    .setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> TU_WU_BLOOD_CURSE_LAMBDA =
            registerExtendedSE("tu_wu_blood_curse_lambda", () -> new TuWuBloodCurseSpecialEffect()
                    .setLineGrade(1)
                    .setDamageAmplifier(0.396f)
                    .setProudPerDamage(134)
                    .setMaxProudPerHit(4333)
                    .setProtectThreshold(4333)
                    .setFoodProudCost(500)
                    .setFoodRestore(1)
                    .setMaxLevel(1)
                    .setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> HUMAN_EMPEROR_DOMAIN =
            registerExtendedSE("human_emperor_domain", () -> new HumanEmperorDomainSpecialEffect()
                    .setLineGrade(2)
                    .setDamageAmplifier(0.4752f)
                    .setProudPerDamage(90)
                    .setMaxProudPerHit(3667)
                    .setProtectThreshold(3667)
                    .setHealPerTick(0.3f)
                    .setRepairProudCost(135)
                    .setFoodProudCost(500)
                    .setFoodRestore(1)
                    .setMaxLevel(1)
                    .setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> HUMAN_EMPEROR_DOMAIN_LAMBDA =
            registerExtendedSE("human_emperor_domain_lambda", () -> new HumanEmperorDomainSpecialEffect()
                    .setLineGrade(3)
                    .setDamageAmplifier(0.57024f)
                    .setProudPerDamage(60)
                    .setMaxProudPerHit(3000)
                    .setProtectThreshold(3000)
                    .setHealPerTick(0.5f)
                    .setRepairProudCost(90)
                    .setFoodProudCost(500)
                    .setFoodRestore(1)
                    .setMaxLevel(1)
                    .setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> DOG_BOND =
            registerExtendedSE("dog_bond", () -> new DogBondSpecialEffect().setMaxLevel(1).setSpecial(true));
    public static final DeferredHolder<SpecialEffect, SpecialEffect> FOCUSED_ENERGY_BLADE =
            registerExtendedSE("focused_energy_blade", () -> new FocusedEnergyBladeSpecialEffect().setMaxLevel(1).setSpecial(true));

    private SpecialEffectsRegistry() {
    }

    public static DeferredHolder<SpecialEffect, SpecialEffect> registerExtendedSE(
            String name,
            Supplier<SpecialEffect> factory
    ) {
        return SPECIAL_EFFECT.register(name, factory);
    }
}

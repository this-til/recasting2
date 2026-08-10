package com.til.recasting.generated;

import com.til.recasting.Recasting;
import com.til.recasting.constant.RecastingSlashBladeKeys;
import com.til.recasting.registry.SlashArtsRegistry;
import com.til.recasting.registry.SpecialEffectsRegistry;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 刀 / SE 成长成就树拓扑，与配方前置一一对应；并含杀敌 / 附魔 / 精炼分支常量。
 */
public final class GrowthAdvancementGraph {

    private GrowthAdvancementGraph() {
    }

    /** 与伤害加成判定一致：{@code killCount > threshold}。 */
    public static final int KILL_MILESTONE_1 = 1000;
    public static final int KILL_MILESTONE_2 = 10000;
    public static final int KILL_MILESTONE_3 = 100000;
    public static final int KILL_MILESTONE_4 = 1000000;

    /** 与伤害加成判定一致：{@code refine > threshold}。 */
    public static final int REFINE_MILESTONE_1 = 1000;
    public static final int REFINE_MILESTONE_2 = 10000;

    /**
     * [回到未来计划] SA 直线链，顺序对齐 {@code Config.SLASH_ARTS_DROP_WHITELIST} 默认值。
     */
    public static final List<ResourceLocation> BACK_TO_FUTURE_SLASH_ARTS = List.of(
            SlashArtsRegistry.TIME_BEYOND.getId(),
            SlashArtsRegistry.IMPRISONMENT.getId(),
            SlashArtsRegistry.PHASE_FRACTURE.getId(),
            SlashArtsRegistry.ETERNAL_GUARD.getId(),
            SlashArtsRegistry.AZURE_HAZE.getId(),
            SlashArtsRegistry.MORTAL_DUST.getId(),
            SlashArtsRegistry.TIDAL_SURGE.getId(),
            SlashArtsRegistry.CELESTIAL_DRIVE.getId(),
            SlashArtsRegistry.STARFALL.getId(),
            SlashArtsRegistry.SKY_SEIZE.getId(),
            SlashArtsRegistry.DIVINE_SLASH.getId(),
            SlashArtsRegistry.VERDICT.getId(),
            SlashArtsRegistry.INFINITE_BLOOM.getId(),
            SlashArtsRegistry.BLISTERING_QI.getId(),
            SlashArtsRegistry.HEAVY_PAYLOAD.getId()
    );

    /**
     * 项目运行时消费的附魔加成直线链。
     */
    public static final List<Enchantment> ENCHANT_BONUS_CHAIN = List.of(
            Enchantments.SMITE,
            Enchantments.BANE_OF_ARTHROPODS,
            Enchantments.FIRE_ASPECT,
            Enchantments.FLAMING_ARROWS,
            Enchantments.POWER_ARROWS,
            Enchantments.SWEEPING_EDGE
    );

    public record BladeNode(
            ResourceKey<SlashBladeDefinition> blade,
            @Nullable ResourceKey<SlashBladeDefinition> parent,
            @Nullable ResourceLocation recipeId,
            boolean lambda,
            boolean menu
    ) {
    }

    public record SeNode(
            ResourceLocation effectId,
            @Nullable ResourceLocation parentEffectId,
            ResourceLocation recipeId
    ) {
    }

    private static BladeNode blade(ResourceKey<SlashBladeDefinition> blade,
                                   @Nullable ResourceKey<SlashBladeDefinition> parent,
                                   @Nullable String recipePath,
                                   boolean lambda) {
        return blade(blade, parent, recipePath, lambda, false);
    }

    private static BladeNode menuBlade(ResourceKey<SlashBladeDefinition> blade,
                                       @Nullable String recipePath) {
        return blade(blade, null, recipePath, false, true);
    }

    private static BladeNode blade(ResourceKey<SlashBladeDefinition> blade,
                                   @Nullable ResourceKey<SlashBladeDefinition> parent,
                                   @Nullable String recipePath,
                                   boolean lambda,
                                   boolean menu) {
        ResourceLocation recipeId = recipePath == null ? null : Recasting.prefix(recipePath);
        return new BladeNode(blade, parent, recipeId, lambda, menu);
    }

    private static SeNode se(ResourceLocation effectId, @Nullable ResourceLocation parentEffectId, String recipePath) {
        return new SeNode(effectId, parentEffectId, Recasting.prefix(recipePath));
    }

    public static final List<BladeNode> BLADES = List.of(
            blade(RecastingSlashBladeKeys.BROADSWORD_WOOD, null, "broadsword_wood_recipe", false),
            blade(RecastingSlashBladeKeys.GREEN_BLADE_WOOD, null, "green_blade_wood_recipe", false),
            menuBlade(RecastingSlashBladeKeys.DHARMA_STICK, "dharma_stick_recipe"),
            menuBlade(RecastingSlashBladeKeys.HOE, "hoe_recipe"),
            menuBlade(RecastingSlashBladeKeys.PHYSICS_SWORD, "physics_sword_recipe"),

            blade(RecastingSlashBladeKeys.BROADSWORD_IRON, RecastingSlashBladeKeys.BROADSWORD_WOOD, "broadsword_iron_recipe", false),
            blade(RecastingSlashBladeKeys.GREEN_BLADE_IRON, RecastingSlashBladeKeys.GREEN_BLADE_WOOD, "green_blade_iron_recipe", false),
            blade(RecastingSlashBladeKeys.DHARMA_STICK_LAMBDA, RecastingSlashBladeKeys.DHARMA_STICK, "dharma_stick_lambda_recipe", true),

            blade(RecastingSlashBladeKeys.SHINE_TEA, RecastingSlashBladeKeys.BROADSWORD_IRON, "shine_tea_recipe", false),
            blade(RecastingSlashBladeKeys.SHINE_TEA_LAMBDA, RecastingSlashBladeKeys.SHINE_TEA, "shine_tea_lambda_recipe", true),
            blade(RecastingSlashBladeKeys.BRILLIANT_TEA, RecastingSlashBladeKeys.SHINE_TEA_LAMBDA, "brilliant_tea_recipe", false),
            blade(RecastingSlashBladeKeys.BRILLIANT_TEA_LAMBDA, RecastingSlashBladeKeys.BRILLIANT_TEA, "brilliant_tea_lambda_recipe", true),
            blade(RecastingSlashBladeKeys.BRIARLAND, RecastingSlashBladeKeys.BRILLIANT_TEA_LAMBDA, "briarland_recipe", false),
            blade(RecastingSlashBladeKeys.BRIARLAND_LAMBDA, RecastingSlashBladeKeys.BRIARLAND, "briarland_lambda_recipe", true),

            blade(RecastingSlashBladeKeys.ART_KNIFE, RecastingSlashBladeKeys.BROADSWORD_IRON, "art_knife_recipe", false),
            blade(RecastingSlashBladeKeys.BLACK, RecastingSlashBladeKeys.BROADSWORD_IRON, "black_recipe", false),

            blade(RecastingSlashBladeKeys.SHINE_GOLD, RecastingSlashBladeKeys.GREEN_BLADE_IRON, "shine_gold_recipe", false),
            blade(RecastingSlashBladeKeys.SHINE_GOLD_LAMBDA, RecastingSlashBladeKeys.SHINE_GOLD, "shine_gold_lambda_recipe", true),
            blade(RecastingSlashBladeKeys.BRILLIANT_GOLD, RecastingSlashBladeKeys.SHINE_GOLD_LAMBDA, "brilliant_gold_recipe", false),
            blade(RecastingSlashBladeKeys.BRILLIANT_GOLD_LAMBDA, RecastingSlashBladeKeys.BRILLIANT_GOLD, "brilliant_gold_lambda_recipe", true),
            blade(RecastingSlashBladeKeys.STAR_THREAD, RecastingSlashBladeKeys.BRILLIANT_GOLD_LAMBDA, "star_thread_recipe", false),
            blade(RecastingSlashBladeKeys.STAR_THREAD_LAMBDA, RecastingSlashBladeKeys.STAR_THREAD, "star_thread_lambda_recipe", true),
            blade(RecastingSlashBladeKeys.BROKEN_WHITE, RecastingSlashBladeKeys.GREEN_BLADE_IRON, "broken_white_recipe", false),

            blade(RecastingSlashBladeKeys.BA_GUA, RecastingSlashBladeKeys.BROKEN_WHITE, "ba_gua_recipe", false),
            blade(RecastingSlashBladeKeys.BA_GUA_BIG, RecastingSlashBladeKeys.BA_GUA, "ba_gua_big_recipe", false),
            blade(RecastingSlashBladeKeys.BA_GUA_BIG_LAMBDA, RecastingSlashBladeKeys.BA_GUA_BIG, "ba_gua_big_lambda_recipe", true),
            blade(RecastingSlashBladeKeys.SUPREME_POLE, RecastingSlashBladeKeys.BA_GUA_BIG_LAMBDA, "supreme_pole_recipe", false),
            blade(RecastingSlashBladeKeys.SUPREME_POLE_LAMBDA, RecastingSlashBladeKeys.SUPREME_POLE, "supreme_pole_lambda_recipe", true),

            blade(RecastingSlashBladeKeys.DRAGON_SCALE, RecastingSlashBladeKeys.BA_GUA, "dragon_scale_recipe", false),
            blade(RecastingSlashBladeKeys.DRAGON_SCALE_LAMBDA, RecastingSlashBladeKeys.DRAGON_SCALE, "dragon_scale_lambda_recipe", true),
            blade(RecastingSlashBladeKeys.DRAGON, RecastingSlashBladeKeys.DRAGON_SCALE_LAMBDA, "dragon_recipe", false),
            blade(RecastingSlashBladeKeys.DRAGON_LAMBDA, RecastingSlashBladeKeys.DRAGON, "dragon_lambda_recipe", true),
            blade(RecastingSlashBladeKeys.VAULT_SCALE, RecastingSlashBladeKeys.DRAGON_LAMBDA, "vault_scale_recipe", false),
            blade(RecastingSlashBladeKeys.VAULT_SCALE_LAMBDA, RecastingSlashBladeKeys.VAULT_SCALE, "vault_scale_lambda_recipe", true),
            blade(RecastingSlashBladeKeys.AZURE_VISTA, RecastingSlashBladeKeys.DRAGON_SCALE_LAMBDA, "azure_vista_recipe", false),
            blade(RecastingSlashBladeKeys.AZURE_VISTA_LAMBDA, RecastingSlashBladeKeys.AZURE_VISTA, "azure_vista_lambda_recipe", true),
            blade(RecastingSlashBladeKeys.WIND_CLOUD, RecastingSlashBladeKeys.AZURE_VISTA_LAMBDA, "wind_cloud_recipe", false),
            blade(RecastingSlashBladeKeys.WIND_CLOUD_LAMBDA, RecastingSlashBladeKeys.WIND_CLOUD, "wind_cloud_lambda_recipe", true),

            blade(RecastingSlashBladeKeys.BLUE_CLOUD, RecastingSlashBladeKeys.BROKEN_WHITE, "blue_cloud_recipe", false),
            blade(RecastingSlashBladeKeys.BLUE_CLOUD_LAMBDA, RecastingSlashBladeKeys.BLUE_CLOUD, "blue_cloud_lambda_recipe", true),
            blade(RecastingSlashBladeKeys.COOL_MINT, RecastingSlashBladeKeys.BLUE_CLOUD_LAMBDA, "cool_mint_recipe", false),
            blade(RecastingSlashBladeKeys.COOL_MINT_LAMBDA, RecastingSlashBladeKeys.COOL_MINT, "cool_mint_lambda_recipe", true),
            blade(RecastingSlashBladeKeys.STARTLED_SWAN, RecastingSlashBladeKeys.COOL_MINT_LAMBDA, "startled_swan_recipe", false),
            blade(RecastingSlashBladeKeys.STARTLED_SWAN_LAMBDA, RecastingSlashBladeKeys.STARTLED_SWAN, "startled_swan_lambda_recipe", true),

            blade(RecastingSlashBladeKeys.UMBRELLA, RecastingSlashBladeKeys.BLACK, "umbrella_recipe", false),
            blade(RecastingSlashBladeKeys.UMBRELLA_LAMBDA, RecastingSlashBladeKeys.UMBRELLA, "umbrella_lambda_recipe", true),
            blade(RecastingSlashBladeKeys.ONIKIRI, RecastingSlashBladeKeys.UMBRELLA_LAMBDA, "onikiri_recipe", false),
            blade(RecastingSlashBladeKeys.ONIKIRI_LAMBDA, RecastingSlashBladeKeys.ONIKIRI, "onikiri_lambda_recipe", true),

            blade(RecastingSlashBladeKeys.VOID_1, RecastingSlashBladeKeys.BLACK, "void_1_recipe", false),
            blade(RecastingSlashBladeKeys.VOID_2, RecastingSlashBladeKeys.VOID_1, "void_2_recipe", false),
            blade(RecastingSlashBladeKeys.VOID_3, RecastingSlashBladeKeys.VOID_2, "void_3_recipe", false),
            blade(RecastingSlashBladeKeys.FINAL_GLOW, RecastingSlashBladeKeys.VOID_3, "final_glow_recipe", false),
            blade(RecastingSlashBladeKeys.FINAL_GLOW_LAMBDA, RecastingSlashBladeKeys.FINAL_GLOW, "final_glow_lambda_recipe", true),

            blade(RecastingSlashBladeKeys.OBLITERATE, RecastingSlashBladeKeys.BLACK, "obliterate_recipe", false),
            blade(RecastingSlashBladeKeys.OBLITERATE_LAMBDA, RecastingSlashBladeKeys.OBLITERATE, "obliterate_lambda_recipe", true),
            blade(RecastingSlashBladeKeys.SOULBLADE, RecastingSlashBladeKeys.OBLITERATE_LAMBDA, "soulblade_recipe", false),
            blade(RecastingSlashBladeKeys.SOULBLADE_LAMBDA, RecastingSlashBladeKeys.SOULBLADE, "soulblade_lambda_recipe", true),

            blade(RecastingSlashBladeKeys.STAR_1, RecastingSlashBladeKeys.BLACK, "star_1_recipe", false),
            blade(RecastingSlashBladeKeys.STAR_2, RecastingSlashBladeKeys.STAR_1, "star_2_recipe", false),
            blade(RecastingSlashBladeKeys.STAR_3, RecastingSlashBladeKeys.STAR_2, "star_3_recipe", false),
            blade(RecastingSlashBladeKeys.STAR_4, RecastingSlashBladeKeys.STAR_3, "star_4_recipe", false),
            blade(RecastingSlashBladeKeys.STAR_4_LAMBDA, RecastingSlashBladeKeys.STAR_4, "star_4_lambda_recipe", true),

            blade(RecastingSlashBladeKeys.LASER_1, RecastingSlashBladeKeys.BLACK, "laser_1_recipe", false),
            blade(RecastingSlashBladeKeys.LASER_2, RecastingSlashBladeKeys.LASER_1, "laser_2_recipe", false),
            blade(RecastingSlashBladeKeys.LASER_3, RecastingSlashBladeKeys.LASER_2, "laser_3_recipe", false),
            blade(RecastingSlashBladeKeys.LASER_3_LAMBDA, RecastingSlashBladeKeys.LASER_3, "laser_3_lambda_recipe", true),

            blade(RecastingSlashBladeKeys.MAGNETIC_STORM, RecastingSlashBladeKeys.BLACK, "magnetic_storm_recipe", false),
            blade(RecastingSlashBladeKeys.MAGNETIC_STORM_LAMBDA, RecastingSlashBladeKeys.MAGNETIC_STORM, "magnetic_storm_lambda_recipe", true),
            blade(RecastingSlashBladeKeys.MAGNETIC_STORM_LIMITS, RecastingSlashBladeKeys.MAGNETIC_STORM_LAMBDA, "magnetic_storm_limits_recipe", false),
            blade(RecastingSlashBladeKeys.MAGNETIC_STORM_LIMITS_LAMBDA, RecastingSlashBladeKeys.MAGNETIC_STORM_LIMITS, "magnetic_storm_limits_lambda_recipe", true),

            blade(RecastingSlashBladeKeys.SILVER_WING, RecastingSlashBladeKeys.BLACK, "silver_wing_recipe", false),
            blade(RecastingSlashBladeKeys.SILVER_WING_LAMBDA, RecastingSlashBladeKeys.SILVER_WING, "silver_wing_lambda_recipe", true),
            blade(RecastingSlashBladeKeys.COLOR_WING, RecastingSlashBladeKeys.SILVER_WING_LAMBDA, "color_wing_recipe", false),
            blade(RecastingSlashBladeKeys.COLOR_WING_LAMBDA, RecastingSlashBladeKeys.COLOR_WING, "color_wing_lambda_recipe", true),

            blade(RecastingSlashBladeKeys.LONG_SKY_SUNSET, RecastingSlashBladeKeys.BLACK, "long_sky_sunset_recipe", false),
            blade(RecastingSlashBladeKeys.LONG_SKY_SUNSET_LAMBDA, RecastingSlashBladeKeys.LONG_SKY_SUNSET, "long_sky_sunset_lambda_recipe", true),

            blade(RecastingSlashBladeKeys.TU_WU, RecastingSlashBladeKeys.SOULBLADE_LAMBDA, "tu_wu_recipe", false),
            blade(RecastingSlashBladeKeys.TU_WU_LAMBDA, RecastingSlashBladeKeys.TU_WU, "tu_wu_lambda_recipe", true),
            blade(RecastingSlashBladeKeys.XUAN_YUAN_LIBERATED, RecastingSlashBladeKeys.TU_WU_LAMBDA, "xuan_yuan_liberated_recipe", false),
            blade(RecastingSlashBladeKeys.XUAN_YUAN_LIBERATED_LAMBDA, RecastingSlashBladeKeys.XUAN_YUAN_LIBERATED, "xuan_yuan_liberated_lambda_recipe", true)
    );

    /**
     * 荧光系列：成就树合并为一节点，任一刀即可解锁。
     */
    public static final List<BladeNode> FLUORESCENCE_SERIES = List.of(
            blade(RecastingSlashBladeKeys.FLUORESCENCE_1, RecastingSlashBladeKeys.GREEN_BLADE_WOOD, "fluorescence_1_recipe", false),
            blade(RecastingSlashBladeKeys.FLUORESCENCE_2, RecastingSlashBladeKeys.GREEN_BLADE_WOOD, "fluorescence_2_recipe", false),
            blade(RecastingSlashBladeKeys.FLUORESCENCE_3, RecastingSlashBladeKeys.GREEN_BLADE_WOOD, "fluorescence_3_recipe", false),
            blade(RecastingSlashBladeKeys.FLUORESCENCE_4, RecastingSlashBladeKeys.GREEN_BLADE_WOOD, "fluorescence_4_recipe", false),
            blade(RecastingSlashBladeKeys.FLUORESCENCE_5, RecastingSlashBladeKeys.GREEN_BLADE_WOOD, "fluorescence_5_recipe", false),
            blade(RecastingSlashBladeKeys.FLUORESCENCE_6, RecastingSlashBladeKeys.GREEN_BLADE_WOOD, "fluorescence_6_recipe", false),
            blade(RecastingSlashBladeKeys.FLUORESCENCE_7, RecastingSlashBladeKeys.GREEN_BLADE_WOOD, "fluorescence_7_recipe", false),
            blade(RecastingSlashBladeKeys.FLUORESCENCE_8, RecastingSlashBladeKeys.GREEN_BLADE_WOOD, "fluorescence_8_recipe", false)
    );

    public static final List<SeNode> SPECIAL_EFFECTS = List.of(
            se(SpecialEffectsRegistry.GREAT_VOID.getId(), null, "great_void_se_crystal_recipe"),
            se(SpecialEffectsRegistry.SHARP_BLADE.getId(), null, "sharp_blade_se_crystal_recipe"),
            se(SpecialEffectsRegistry.SHOCK.getId(), null, "shock_se_crystal_recipe"),
            se(SpecialEffectsRegistry.SWORD_QI_MASTERY.getId(), null, "sword_qi_mastery_se_crystal_recipe"),
            se(SpecialEffectsRegistry.THUNDER_STRIKE.getId(), null, "thunder_strike_se_crystal_recipe"),

            se(SpecialEffectsRegistry.COOPERATE_WITH.getId(), SpecialEffectsRegistry.SHARP_BLADE.getId(), "cooperate_with_se_crystal_recipe"),
            se(SpecialEffectsRegistry.CROSS_CHOP.getId(), SpecialEffectsRegistry.COOPERATE_WITH.getId(), "cross_chop_se_crystal_recipe"),
            se(SpecialEffectsRegistry.RESIST.getId(), SpecialEffectsRegistry.CROSS_CHOP.getId(), "resist_se_crystal_recipe"),
            se(SpecialEffectsRegistry.GROWTH.getId(), SpecialEffectsRegistry.COOPERATE_WITH.getId(), "growth_se_crystal_recipe"),
            se(SpecialEffectsRegistry.REGRESSION.getId(), SpecialEffectsRegistry.GROWTH.getId(), "regression_se_crystal_recipe"),
            se(SpecialEffectsRegistry.LIFE_STEAL.getId(), SpecialEffectsRegistry.REGRESSION.getId(), "life_steal_se_crystal_recipe"),

            se(SpecialEffectsRegistry.FRAGMENT.getId(), SpecialEffectsRegistry.GREAT_VOID.getId(), "fragment_se_crystal_recipe"),
            se(SpecialEffectsRegistry.SPIRAL.getId(), SpecialEffectsRegistry.FRAGMENT.getId(), "spiral_se_crystal_recipe"),
            se(SpecialEffectsRegistry.SPLIT.getId(), SpecialEffectsRegistry.FRAGMENT.getId(), "split_se_crystal_recipe"),
            se(SpecialEffectsRegistry.IMPACT.getId(), SpecialEffectsRegistry.SPLIT.getId(), "impact_se_crystal_recipe"),

            se(SpecialEffectsRegistry.TEAR.getId(), SpecialEffectsRegistry.SHOCK.getId(), "tear_se_crystal_recipe"),
            se(SpecialEffectsRegistry.OVERLOAD.getId(), SpecialEffectsRegistry.TEAR.getId(), "overload_se_crystal_recipe"),
            se(SpecialEffectsRegistry.WHIRLWIND.getId(), SpecialEffectsRegistry.OVERLOAD.getId(), "whirlwind_se_crystal_recipe"),
            se(SpecialEffectsRegistry.SEVER_BREAK.getId(), SpecialEffectsRegistry.TEAR.getId(), "sever_break_se_crystal_recipe"),
            se(SpecialEffectsRegistry.ANNIHILATION.getId(), SpecialEffectsRegistry.SEVER_BREAK.getId(), "annihilation_se_crystal_recipe"),
            se(SpecialEffectsRegistry.JUDGEMENT.getId(), SpecialEffectsRegistry.SHOCK.getId(), "judgement_se_crystal_recipe"),
            se(SpecialEffectsRegistry.STORM.getId(), SpecialEffectsRegistry.JUDGEMENT.getId(), "storm_se_crystal_recipe"),
            se(SpecialEffectsRegistry.STORM_VARIANT.getId(), SpecialEffectsRegistry.STORM.getId(), "storm_variant_se_crystal_recipe"),

            se(SpecialEffectsRegistry.IONIZATION.getId(), SpecialEffectsRegistry.THUNDER_STRIKE.getId(), "ionization_se_crystal_recipe"),
            se(SpecialEffectsRegistry.THUNDER_GODS_WRATH.getId(), SpecialEffectsRegistry.IONIZATION.getId(), "thunder_gods_wrath_se_crystal_recipe"),
            se(SpecialEffectsRegistry.THUNDERSTORM.getId(), SpecialEffectsRegistry.IONIZATION.getId(), "thunderstorm_se_crystal_recipe"),
            se(SpecialEffectsRegistry.THUNDER_CLOUD.getId(), SpecialEffectsRegistry.THUNDERSTORM.getId(), "thunder_cloud_se_crystal_recipe"),
            se(SpecialEffectsRegistry.ENERGY_STORAGE.getId(), SpecialEffectsRegistry.THUNDERSTORM.getId(), "energy_storage_se_crystal_recipe"),

            se(SpecialEffectsRegistry.DRIVE_RELEASE.getId(), SpecialEffectsRegistry.SWORD_QI_MASTERY.getId(), "drive_release_se_crystal_recipe")
    );
}

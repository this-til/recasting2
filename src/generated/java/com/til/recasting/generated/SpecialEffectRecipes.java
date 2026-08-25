package com.til.recasting.generated;

import com.til.recasting.Recasting;
import com.til.recasting.recipe.SpecialEffectCrystalIngredient;
import com.til.recasting.recipe.SpecialEffectCrystalShapedRecipeBuilder;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.SpecialEffectsRegistry;
import com.til.recasting.registry.requir.SlashBladeItems;
import com.til.recasting.registry.se.ExtendedSpecialEffect;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SpecialEffectRecipes extends RecipeProvider {

    public SpecialEffectRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        saveCrystalRecipe(output, "great_void_se_crystal_recipe", SpecialEffectsRegistry.GREAT_VOID, RecastingItems.MIRAGE_FLAME.get(), Items.AMETHYST_SHARD, Ingredient.of(RecastingItems.GATHERING_PARTING_VARIANT.get()), "has_gathering_parting_variant", RecastingItems.GATHERING_PARTING_VARIANT.get());
        saveCrystalRecipe(output, "sharp_blade_se_crystal_recipe", SpecialEffectsRegistry.SHARP_BLADE, RecastingItems.OBSESSION_FLAME.get(), Items.NETHERITE_SCRAP, Ingredient.of(RecastingItems.GATHERING_PARTING_VARIANT.get()), "has_gathering_parting_variant", RecastingItems.GATHERING_PARTING_VARIANT.get());
        saveCrystalRecipe(output, "shock_se_crystal_recipe", SpecialEffectsRegistry.SHOCK, RecastingItems.CHAOS_FLAME.get(), Items.ENDER_PEARL, Ingredient.of(RecastingItems.GATHERING_PARTING_VARIANT.get()), "has_gathering_parting_variant", RecastingItems.GATHERING_PARTING_VARIANT.get());
        saveCrystalRecipe(output, "sword_qi_mastery_se_crystal_recipe", SpecialEffectsRegistry.SWORD_QI_MASTERY, RecastingItems.HOLY_FLAME.get(), Items.BLAZE_ROD, Ingredient.of(RecastingItems.GATHERING_PARTING_VARIANT.get()), "has_gathering_parting_variant", RecastingItems.GATHERING_PARTING_VARIANT.get());
        saveCrystalRecipe(output, "thunder_strike_se_crystal_recipe", SpecialEffectsRegistry.THUNDER_STRIKE, RecastingItems.ROYAL_FLAME.get(), Items.PRISMARINE_SHARD, Ingredient.of(RecastingItems.GATHERING_PARTING_VARIANT.get()), "has_gathering_parting_variant", RecastingItems.GATHERING_PARTING_VARIANT.get());

        saveCrystalRecipe(output, "cooperate_with_se_crystal_recipe", SpecialEffectsRegistry.COOPERATE_WITH, RecastingItems.MIRROR_FLAME.get(), Items.GOLDEN_CARROT, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SHARP_BLADE.getId(), 1), "has_sharp_blade_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "cross_chop_se_crystal_recipe", SpecialEffectsRegistry.CROSS_CHOP, RecastingItems.MIRROR_FLAME.get(), Items.GOLDEN_SWORD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.COOPERATE_WITH.getId(), 1), "has_cooperate_with_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "growth_se_crystal_recipe", SpecialEffectsRegistry.GROWTH, RecastingItems.CRADLE_FLAME.get(), Items.EMERALD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.COOPERATE_WITH.getId(), 1), "has_cooperate_with_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "regression_se_crystal_recipe", SpecialEffectsRegistry.REGRESSION, RecastingItems.CRADLE_FLAME.get(), Items.ANVIL, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.GROWTH.getId(), 1), "has_growth_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "life_steal_se_crystal_recipe", SpecialEffectsRegistry.LIFE_STEAL, RecastingItems.SIN_FLAME.get(), Items.GOLDEN_APPLE, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.REGRESSION.getId(), 1), "has_regression_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "resist_se_crystal_recipe", SpecialEffectsRegistry.RESIST, RecastingItems.CRAFTSMAN_FLAME.get(), Items.SHIELD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.CROSS_CHOP.getId(), 1), "has_cross_chop_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "fragment_se_crystal_recipe", SpecialEffectsRegistry.FRAGMENT, RecastingItems.MIRAGE_FLAME.get(), Items.AMETHYST_SHARD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.GREAT_VOID.getId(), 1), "has_great_void_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "spiral_se_crystal_recipe", SpecialEffectsRegistry.SPIRAL, RecastingItems.ABYSS_FLAME.get(), Items.ENDER_PEARL, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.FRAGMENT.getId(), 1), "has_fragment_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "tear_se_crystal_recipe", SpecialEffectsRegistry.TEAR, RecastingItems.CHAOS_FLAME.get(), Items.DIAMOND, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SHOCK.getId(), 1), "has_shock_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "whirlwind_se_crystal_recipe", SpecialEffectsRegistry.WHIRLWIND, RecastingItems.TIDE_FLAME.get(), Items.ENDER_EYE, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.OVERLOAD.getId(), 1), "has_overload_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "annihilation_se_crystal_recipe", SpecialEffectsRegistry.ANNIHILATION, RecastingItems.OTHER_SHORE_FLAME.get(), Items.BEACON, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SEVER_BREAK.getId(), 1), "has_sever_break_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "overload_se_crystal_recipe", SpecialEffectsRegistry.OVERLOAD, RecastingItems.TIDE_FLAME.get(), Items.REDSTONE, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.TEAR.getId(), 1), "has_tear_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "sever_break_se_crystal_recipe", SpecialEffectsRegistry.SEVER_BREAK, RecastingItems.OTHER_SHORE_FLAME.get(), Items.DIAMOND_SWORD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.TEAR.getId(), 1), "has_tear_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "storm_se_crystal_recipe", SpecialEffectsRegistry.STORM, RecastingItems.CHAOS_FLAME.get(), Items.BLAZE_ROD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.JUDGEMENT.getId(), 1), "has_judgement_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "storm_variant_se_crystal_recipe", SpecialEffectsRegistry.STORM_VARIANT, RecastingItems.CHAOS_FLAME.get(), Items.TRIDENT, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.STORM.getId(), 1), "has_storm_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "ionization_se_crystal_recipe", SpecialEffectsRegistry.IONIZATION, RecastingItems.MEMORY_FLAME.get(), Items.LAPIS_LAZULI, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.THUNDER_STRIKE.getId(), 1), "has_thunder_strike_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "thunder_gods_wrath_se_crystal_recipe", SpecialEffectsRegistry.THUNDER_GODS_WRATH, RecastingItems.MEMORY_FLAME.get(), Items.GOLD_INGOT, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.IONIZATION.getId(), 1), "has_ionization_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "thunderstorm_se_crystal_recipe", SpecialEffectsRegistry.THUNDERSTORM, RecastingItems.MEMORY_FLAME.get(), Items.TRIDENT, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.IONIZATION.getId(), 1), "has_ionization_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "thunder_cloud_se_crystal_recipe", SpecialEffectsRegistry.THUNDER_CLOUD, RecastingItems.POETRY_ASH_FLAME.get(), Items.PRISMARINE_SHARD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.THUNDERSTORM.getId(), 1), "has_thunderstorm_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "energy_storage_se_crystal_recipe", SpecialEffectsRegistry.ENERGY_STORAGE, RecastingItems.TIDE_FLAME.get(), Items.REDSTONE, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.THUNDERSTORM.getId(), 1), "has_thunderstorm_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "drive_release_se_crystal_recipe", SpecialEffectsRegistry.DRIVE_RELEASE, RecastingItems.HOLY_FLAME.get(), Items.IRON_SWORD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SWORD_QI_MASTERY.getId(), 1), "has_sword_qi_mastery_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "judgement_se_crystal_recipe", SpecialEffectsRegistry.JUDGEMENT, RecastingItems.CHAOS_FLAME.get(), Items.DIAMOND_SWORD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SHOCK.getId(), 1), "has_shock_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "split_se_crystal_recipe", SpecialEffectsRegistry.SPLIT, RecastingItems.MIRAGE_FLAME.get(), Items.BLAZE_ROD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.FRAGMENT.getId(), 1), "has_fragment_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(output, "impact_se_crystal_recipe", SpecialEffectsRegistry.IMPACT, RecastingItems.MIRROR_FLAME.get(), Items.IRON_SWORD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SPLIT.getId(), 1), "has_split_se_crystal", RecastingItems.SE_CRYSTAL.get());

        saveUpgradeRecipes(output, "cooperate_with_upgrade_recipes", SpecialEffectsRegistry.COOPERATE_WITH);
        saveUpgradeRecipes(output, "cross_chop_upgrade_recipes", SpecialEffectsRegistry.CROSS_CHOP);
        saveUpgradeRecipes(output, "drive_release_upgrade_recipes", SpecialEffectsRegistry.DRIVE_RELEASE);
        saveUpgradeRecipes(output, "growth_upgrade_recipes", SpecialEffectsRegistry.GROWTH);
        saveUpgradeRecipes(output, "life_steal_upgrade_recipes", SpecialEffectsRegistry.LIFE_STEAL);
        saveUpgradeRecipes(output, "regression_upgrade_recipes", SpecialEffectsRegistry.REGRESSION);
        saveUpgradeRecipes(output, "judgement_upgrade_recipes", SpecialEffectsRegistry.JUDGEMENT);
        saveUpgradeRecipes(output, "thunderstorm_upgrade_recipes", SpecialEffectsRegistry.THUNDERSTORM);
        saveUpgradeRecipes(output, "thunder_gods_wrath_upgrade_recipes", SpecialEffectsRegistry.THUNDER_GODS_WRATH);
        saveUpgradeRecipes(output, "ionization_upgrade_recipes", SpecialEffectsRegistry.IONIZATION);
        saveUpgradeRecipes(output, "energy_storage_upgrade_recipes", SpecialEffectsRegistry.ENERGY_STORAGE);
        saveUpgradeRecipes(output, "thunder_cloud_upgrade_recipes", SpecialEffectsRegistry.THUNDER_CLOUD);
        saveUpgradeRecipes(output, "impact_upgrade_recipes", SpecialEffectsRegistry.IMPACT);
        saveUpgradeRecipes(output, "overload_upgrade_recipes", SpecialEffectsRegistry.OVERLOAD);
        saveUpgradeRecipes(output, "resist_upgrade_recipes", SpecialEffectsRegistry.RESIST);
        saveUpgradeRecipes(output, "sever_break_upgrade_recipes", SpecialEffectsRegistry.SEVER_BREAK);
        saveUpgradeRecipes(output, "storm_upgrade_recipes", SpecialEffectsRegistry.STORM);
        saveUpgradeRecipes(output, "storm_variant_upgrade_recipes", SpecialEffectsRegistry.STORM_VARIANT);
        saveUpgradeRecipes(output, "split_upgrade_recipes", SpecialEffectsRegistry.SPLIT);
        saveUpgradeRecipes(output, "spiral_upgrade_recipes", SpecialEffectsRegistry.SPIRAL);
        saveUpgradeRecipes(output, "fragment_upgrade_recipes", SpecialEffectsRegistry.FRAGMENT);
        saveUpgradeRecipes(output, "tear_upgrade_recipes", SpecialEffectsRegistry.TEAR);
        saveUpgradeRecipes(output, "whirlwind_upgrade_recipes", SpecialEffectsRegistry.WHIRLWIND);
        saveUpgradeRecipes(output, "annihilation_upgrade_recipes", SpecialEffectsRegistry.ANNIHILATION);
        saveUpgradeRecipes(output, "great_void_upgrade_recipes", SpecialEffectsRegistry.GREAT_VOID);
        saveUpgradeRecipes(output, "sharp_blade_upgrade_recipes", SpecialEffectsRegistry.SHARP_BLADE);
        saveUpgradeRecipes(output, "shock_upgrade_recipes", SpecialEffectsRegistry.SHOCK);
        saveUpgradeRecipes(output, "sword_qi_mastery_upgrade_recipes", SpecialEffectsRegistry.SWORD_QI_MASTERY);
        saveUpgradeRecipes(output, "thunder_strike_upgrade_recipes", SpecialEffectsRegistry.THUNDER_STRIKE);
    }

    private void saveCrystalRecipe(
            RecipeOutput output,
            String path,
            DeferredHolder<SpecialEffect, SpecialEffect> effect,
            ItemLike flame,
            ItemLike accent,
            Ingredient center,
            String unlockName,
            ItemLike unlockItem
    ) {
        SpecialEffectCrystalShapedRecipeBuilder.shaped(effect, 1)
                .pattern("FAF")
                .pattern("AMA")
                .pattern("FAF")
                .define('F', flame)
                .define('A', accent)
                .define('M', center)
                .unlockedBy(unlockName, has(unlockItem))
                .save(output, Recasting.prefix(path));
    }

    private void saveUpgradeRecipes(
            RecipeOutput output,
            String baseId,
            DeferredHolder<SpecialEffect, SpecialEffect> seType
    ) {
        SpecialEffect se = seType.get();
        if (!(se instanceof ExtendedSpecialEffect extendedSE)) {
            return;
        }

        ResourceLocation seLocation = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.getKey(se);
        if (seLocation == null) {
            return;
        }

        int index = 0;
        if (!extendedSE.isSpecial() && extendedSE.getMaxLevel() >= 1) {
            saveDemoteRecipe(output, baseId, index, seType, seLocation);
            index++;
        }

        for (int currentLevel = 1; currentLevel < extendedSE.getMaxLevel(); currentLevel++) {
            saveUpgradeRecipe(output, baseId, index, seType, seLocation, currentLevel, currentLevel + 1);
            index++;
        }
    }

    private void saveDemoteRecipe(
            RecipeOutput output,
            String baseId,
            int index,
            DeferredHolder<SpecialEffect, SpecialEffect> seType,
            ResourceLocation seLocation
    ) {
        SpecialEffectCrystalShapedRecipeBuilder.shaped(seType, 0)
                .pattern(" U ")
                .pattern("USU")
                .pattern(" U ")
                .define('U', RecastingItems.ABYSS_FLAME.get())
                .define('S', SpecialEffectCrystalIngredient.of(seLocation, 1))
                .unlockedBy("has_se_crystal", has(RecastingItems.SE_CRYSTAL.get()))
                .save(output, Recasting.prefix(baseId + "_" + index));
    }

    private void saveUpgradeRecipe(
            RecipeOutput output,
            String baseId,
            int index,
            DeferredHolder<SpecialEffect, SpecialEffect> seType,
            ResourceLocation seLocation,
            int level,
            int nextLevel
    ) {
        SpecialEffectCrystalShapedRecipeBuilder.shaped(seType, nextLevel)
                .pattern(" VS")
                .pattern("VUV")
                .pattern("SV ")
                .define('V', SlashBladeItems.PROUDSOUL.get())
                .define('U', getUpgradeVariantForLevel(level))
                .define('S', SpecialEffectCrystalIngredient.of(seLocation, level))
                .unlockedBy("has_se_crystal", has(RecastingItems.SE_CRYSTAL.get()))
                .save(output, Recasting.prefix(baseId + "_" + index));
    }

    private static ItemLike getUpgradeVariantForLevel(int level) {
        return switch (level) {
            case 1 -> RecastingItems.UPGRADE_VARIANT.get();
            case 2 -> RecastingItems.UPGRADE_VARIANT_2.get();
            case 3 -> RecastingItems.UPGRADE_VARIANT_3.get();
            case 4 -> RecastingItems.UPGRADE_VARIANT_4.get();
            default -> RecastingItems.UPGRADE_VARIANT.get();
        };
    }
}

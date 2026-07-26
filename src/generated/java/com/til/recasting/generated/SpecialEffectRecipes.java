package com.til.recasting.generated;

import com.til.recasting.Recasting;
import com.til.recasting.mixin.RecipeProviderMixin;
import com.til.recasting.recipe.SpecialEffectCrystalIngredient;
import com.til.recasting.recipe.SpecialEffectCrystalShapedRecipeBuilder;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.SpecialEffectsRegistry;
import com.til.recasting.registry.requir.SlashBladeItems;
import com.til.recasting.registry.se.ExtendedSpecialEffect;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SpecialEffectRecipes extends RecipeProvider {

    public SpecialEffectRecipes(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        // 一级 SE 结晶：以火焰素材 + 聚散变体作为中心材料，直接解锁基础效果。
        saveCrystalRecipe(consumer, "great_void_se_crystal_recipe", SpecialEffectsRegistry.GREAT_VOID, RecastingItems.MIRAGE_FLAME.get(), Items.AMETHYST_SHARD, Ingredient.of(RecastingItems.GATHERING_PARTING_VARIANT.get()), "has_gathering_parting_variant", RecastingItems.GATHERING_PARTING_VARIANT.get());
        saveCrystalRecipe(consumer, "sharp_blade_se_crystal_recipe", SpecialEffectsRegistry.SHARP_BLADE, RecastingItems.OBSESSION_FLAME.get(), Items.NETHERITE_SCRAP, Ingredient.of(RecastingItems.GATHERING_PARTING_VARIANT.get()), "has_gathering_parting_variant", RecastingItems.GATHERING_PARTING_VARIANT.get());
        saveCrystalRecipe(consumer, "shock_se_crystal_recipe", SpecialEffectsRegistry.SHOCK, RecastingItems.CHAOS_FLAME.get(), Items.ENDER_PEARL, Ingredient.of(RecastingItems.GATHERING_PARTING_VARIANT.get()), "has_gathering_parting_variant", RecastingItems.GATHERING_PARTING_VARIANT.get());
        saveCrystalRecipe(consumer, "sword_qi_mastery_se_crystal_recipe", SpecialEffectsRegistry.SWORD_QI_MASTERY, RecastingItems.HOLY_FLAME.get(), Items.BLAZE_ROD, Ingredient.of(RecastingItems.GATHERING_PARTING_VARIANT.get()), "has_gathering_parting_variant", RecastingItems.GATHERING_PARTING_VARIANT.get());
        saveCrystalRecipe(consumer, "thunder_strike_se_crystal_recipe", SpecialEffectsRegistry.THUNDER_STRIKE, RecastingItems.ROYAL_FLAME.get(), Items.PRISMARINE_SHARD, Ingredient.of(RecastingItems.GATHERING_PARTING_VARIANT.get()), "has_gathering_parting_variant", RecastingItems.GATHERING_PARTING_VARIANT.get());

        // 进阶 SE 结晶：以上一级 SE 结晶或派生结晶作为中心材料，沿效果链继续解锁。
        saveCrystalRecipe(consumer, "cooperate_with_se_crystal_recipe", SpecialEffectsRegistry.COOPERATE_WITH, RecastingItems.MIRROR_FLAME.get(), Items.GOLDEN_CARROT, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SHARP_BLADE.getId(), 1), "has_sharp_blade_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "cross_chop_se_crystal_recipe", SpecialEffectsRegistry.CROSS_CHOP, RecastingItems.MIRROR_FLAME.get(), Items.GOLDEN_SWORD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.COOPERATE_WITH.getId(), 1), "has_cooperate_with_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "growth_se_crystal_recipe", SpecialEffectsRegistry.GROWTH, RecastingItems.CRADLE_FLAME.get(), Items.EMERALD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.COOPERATE_WITH.getId(), 1), "has_cooperate_with_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "regression_se_crystal_recipe", SpecialEffectsRegistry.REGRESSION, RecastingItems.CRADLE_FLAME.get(), Items.ANVIL, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.GROWTH.getId(), 1), "has_growth_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "life_steal_se_crystal_recipe", SpecialEffectsRegistry.LIFE_STEAL, RecastingItems.SIN_FLAME.get(), Items.GOLDEN_APPLE, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.REGRESSION.getId(), 1), "has_regression_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "resist_se_crystal_recipe", SpecialEffectsRegistry.RESIST, RecastingItems.CRAFTSMAN_FLAME.get(), Items.SHIELD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.CROSS_CHOP.getId(), 1), "has_cross_chop_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "fragment_se_crystal_recipe", SpecialEffectsRegistry.FRAGMENT, RecastingItems.MIRAGE_FLAME.get(), Items.AMETHYST_SHARD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.GREAT_VOID.getId(), 1), "has_great_void_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "spiral_se_crystal_recipe", SpecialEffectsRegistry.SPIRAL, RecastingItems.ABYSS_FLAME.get(), Items.ENDER_PEARL, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.FRAGMENT.getId(), 1), "has_fragment_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "tear_se_crystal_recipe", SpecialEffectsRegistry.TEAR, RecastingItems.CHAOS_FLAME.get(), Items.DIAMOND, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SHOCK.getId(), 1), "has_shock_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "whirlwind_se_crystal_recipe", SpecialEffectsRegistry.WHIRLWIND, RecastingItems.TIDE_FLAME.get(), Items.ENDER_EYE, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.OVERLOAD.getId(), 1), "has_overload_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "annihilation_se_crystal_recipe", SpecialEffectsRegistry.ANNIHILATION, RecastingItems.OTHER_SHORE_FLAME.get(), Items.BEACON, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SEVER_BREAK.getId(), 1), "has_sever_break_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "overload_se_crystal_recipe", SpecialEffectsRegistry.OVERLOAD, RecastingItems.TIDE_FLAME.get(), Items.REDSTONE, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.TEAR.getId(), 1), "has_tear_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "sever_break_se_crystal_recipe", SpecialEffectsRegistry.SEVER_BREAK, RecastingItems.OTHER_SHORE_FLAME.get(), Items.DIAMOND_SWORD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.TEAR.getId(), 1), "has_tear_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "storm_se_crystal_recipe", SpecialEffectsRegistry.STORM, RecastingItems.CHAOS_FLAME.get(), Items.BLAZE_ROD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.JUDGEMENT.getId(), 1), "has_judgement_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "storm_variant_se_crystal_recipe", SpecialEffectsRegistry.STORM_VARIANT, RecastingItems.CHAOS_FLAME.get(), Items.TRIDENT, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.STORM.getId(), 1), "has_storm_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "ionization_se_crystal_recipe", SpecialEffectsRegistry.IONIZATION, RecastingItems.MEMORY_FLAME.get(), Items.LAPIS_LAZULI, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.THUNDER_STRIKE.getId(), 1), "has_thunder_strike_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "thunder_gods_wrath_se_crystal_recipe", SpecialEffectsRegistry.THUNDER_GODS_WRATH, RecastingItems.MEMORY_FLAME.get(), Items.GOLD_INGOT, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.IONIZATION.getId(), 1), "has_ionization_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "thunderstorm_se_crystal_recipe", SpecialEffectsRegistry.THUNDERSTORM, RecastingItems.MEMORY_FLAME.get(), Items.TRIDENT, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.IONIZATION.getId(), 1), "has_ionization_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "thunder_cloud_se_crystal_recipe", SpecialEffectsRegistry.THUNDER_CLOUD, RecastingItems.POETRY_ASH_FLAME.get(), Items.PRISMARINE_SHARD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.THUNDERSTORM.getId(), 1), "has_thunderstorm_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "energy_storage_se_crystal_recipe", SpecialEffectsRegistry.ENERGY_STORAGE, RecastingItems.TIDE_FLAME.get(), Items.REDSTONE, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.THUNDERSTORM.getId(), 1), "has_thunderstorm_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "drive_release_se_crystal_recipe", SpecialEffectsRegistry.DRIVE_RELEASE, RecastingItems.HOLY_FLAME.get(), Items.IRON_SWORD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SWORD_QI_MASTERY.getId(), 1), "has_sword_qi_mastery_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "judgement_se_crystal_recipe", SpecialEffectsRegistry.JUDGEMENT, RecastingItems.CHAOS_FLAME.get(), Items.DIAMOND_SWORD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SHOCK.getId(), 1), "has_shock_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "split_se_crystal_recipe", SpecialEffectsRegistry.SPLIT, RecastingItems.MIRAGE_FLAME.get(), Items.BLAZE_ROD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.FRAGMENT.getId(), 1), "has_fragment_se_crystal", RecastingItems.SE_CRYSTAL.get());
        saveCrystalRecipe(consumer, "impact_se_crystal_recipe", SpecialEffectsRegistry.IMPACT, RecastingItems.MIRROR_FLAME.get(), Items.IRON_SWORD, SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SPLIT.getId(), 1), "has_split_se_crystal", RecastingItems.SE_CRYSTAL.get());

        // 升级链：普通效果先补 1 级 -> 0 级拆解，再依次生成 level -> level + 1 的升格配方。
        saveUpgradeRecipes(consumer, "cooperate_with_upgrade_recipes", SpecialEffectsRegistry.COOPERATE_WITH);
        saveUpgradeRecipes(consumer, "cross_chop_upgrade_recipes", SpecialEffectsRegistry.CROSS_CHOP);
        saveUpgradeRecipes(consumer, "drive_release_upgrade_recipes", SpecialEffectsRegistry.DRIVE_RELEASE);
        saveUpgradeRecipes(consumer, "growth_upgrade_recipes", SpecialEffectsRegistry.GROWTH);
        saveUpgradeRecipes(consumer, "life_steal_upgrade_recipes", SpecialEffectsRegistry.LIFE_STEAL);
        saveUpgradeRecipes(consumer, "regression_upgrade_recipes", SpecialEffectsRegistry.REGRESSION);
        saveUpgradeRecipes(consumer, "judgement_upgrade_recipes", SpecialEffectsRegistry.JUDGEMENT);
        saveUpgradeRecipes(consumer, "thunderstorm_upgrade_recipes", SpecialEffectsRegistry.THUNDERSTORM);
        saveUpgradeRecipes(consumer, "thunder_gods_wrath_upgrade_recipes", SpecialEffectsRegistry.THUNDER_GODS_WRATH);
        saveUpgradeRecipes(consumer, "ionization_upgrade_recipes", SpecialEffectsRegistry.IONIZATION);
        saveUpgradeRecipes(consumer, "energy_storage_upgrade_recipes", SpecialEffectsRegistry.ENERGY_STORAGE);
        saveUpgradeRecipes(consumer, "thunder_cloud_upgrade_recipes", SpecialEffectsRegistry.THUNDER_CLOUD);
        saveUpgradeRecipes(consumer, "impact_upgrade_recipes", SpecialEffectsRegistry.IMPACT);
        saveUpgradeRecipes(consumer, "overload_upgrade_recipes", SpecialEffectsRegistry.OVERLOAD);
        saveUpgradeRecipes(consumer, "resist_upgrade_recipes", SpecialEffectsRegistry.RESIST);
        saveUpgradeRecipes(consumer, "sever_break_upgrade_recipes", SpecialEffectsRegistry.SEVER_BREAK);
        saveUpgradeRecipes(consumer, "storm_upgrade_recipes", SpecialEffectsRegistry.STORM);
        saveUpgradeRecipes(consumer, "storm_variant_upgrade_recipes", SpecialEffectsRegistry.STORM_VARIANT);
        saveUpgradeRecipes(consumer, "split_upgrade_recipes", SpecialEffectsRegistry.SPLIT);
        saveUpgradeRecipes(consumer, "spiral_upgrade_recipes", SpecialEffectsRegistry.SPIRAL);
        saveUpgradeRecipes(consumer, "fragment_upgrade_recipes", SpecialEffectsRegistry.FRAGMENT);
        saveUpgradeRecipes(consumer, "tear_upgrade_recipes", SpecialEffectsRegistry.TEAR);
        saveUpgradeRecipes(consumer, "whirlwind_upgrade_recipes", SpecialEffectsRegistry.WHIRLWIND);
        saveUpgradeRecipes(consumer, "annihilation_upgrade_recipes", SpecialEffectsRegistry.ANNIHILATION);
        saveUpgradeRecipes(consumer, "great_void_upgrade_recipes", SpecialEffectsRegistry.GREAT_VOID);
        saveUpgradeRecipes(consumer, "sharp_blade_upgrade_recipes", SpecialEffectsRegistry.SHARP_BLADE);
        saveUpgradeRecipes(consumer, "shock_upgrade_recipes", SpecialEffectsRegistry.SHOCK);
        saveUpgradeRecipes(consumer, "sword_qi_mastery_upgrade_recipes", SpecialEffectsRegistry.SWORD_QI_MASTERY);
        saveUpgradeRecipes(consumer, "thunder_strike_upgrade_recipes", SpecialEffectsRegistry.THUNDER_STRIKE);
    }

    /**
     * 生成单个 SE 结晶配方。
     * 外圈固定为对应火焰与点缀素材，中间材料决定该结晶所在的解锁链。
     */
    private void saveCrystalRecipe(
            Consumer<FinishedRecipe> consumer,
            String path,
            RegistryObject<SpecialEffect> effect,
            ItemLike flame,
            ItemLike accent,
            Ingredient center,
            String unlockName,
            ItemLike unlockItem
    ) {
        ResourceLocation recipeId = Recasting.prefix(path);
        SpecialEffectCrystalShapedRecipeBuilder.shaped(effect, 1)
                .pattern("FAF")
                .pattern("AMA")
                .pattern("FAF")
                .define('F', flame)
                .define('A', accent)
                .define('M', center)
                .unlockedBy(unlockName, RecipeProviderMixin.invokeHas(unlockItem))
                .save(consumer, recipeId);
    }

    /**
     * 为一个 SE 生成完整升级链。
     * 普通效果会额外生成 1 级结晶拆回 0 级的逆向配方。
     */
    private void saveUpgradeRecipes(Consumer<FinishedRecipe> consumer, String baseId, RegistryObject<SpecialEffect> seType) {
        SpecialEffect se = seType.get();
        if (!(se instanceof ExtendedSpecialEffect extendedSE)) {
            return;
        }

        ResourceLocation seLocation = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getKey(se);
        if (seLocation == null) {
            return;
        }

        int index = 0;
        if (!extendedSE.isSpecial() && extendedSE.getMaxLevel() >= 1) {
            saveDemoteRecipe(consumer, baseId, index, seType, seLocation);
            index++;
        }

        for (int currentLevel = 1; currentLevel < extendedSE.getMaxLevel(); currentLevel++) {
            saveUpgradeRecipe(consumer, baseId, index, seType, seLocation, currentLevel, currentLevel + 1);
            index++;
        }
    }

    /**
     * 生成 1 级 SE 结晶的降级配方。
     * 该配方只在非 special 效果上存在，用于回退到 0 级结晶。
     */
    private void saveDemoteRecipe(
            Consumer<FinishedRecipe> consumer,
            String baseId,
            int index,
            RegistryObject<SpecialEffect> seType,
            ResourceLocation seLocation
    ) {
        ResourceLocation recipeId = Recasting.prefix(baseId + "_" + index);
        SpecialEffectCrystalShapedRecipeBuilder.shaped(seType, 0)
                .pattern(" U ")
                .pattern("USU")
                .pattern(" U ")
                .define('U', RecastingItems.ABYSS_FLAME.get())
                .define('S', SpecialEffectCrystalIngredient.of(seLocation, 1))
                .unlockedBy("has_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                .save(consumer, recipeId);
    }

    /**
     * 生成 level -> nextLevel 的 SE 升级配方。
     * 左右使用对应等级的升格变体，中间消耗当前等级的同类结晶。
     */
    private void saveUpgradeRecipe(
            Consumer<FinishedRecipe> consumer,
            String baseId,
            int index,
            RegistryObject<SpecialEffect> seType,
            ResourceLocation seLocation,
            int level,
            int nextLevel
    ) {
        ResourceLocation recipeId = Recasting.prefix(baseId + "_" + index);
        SpecialEffectCrystalShapedRecipeBuilder.shaped(seType, nextLevel)
                .pattern(" VS")
                .pattern("VUV")
                .pattern("SV ")
                .define('V', SlashBladeItems.PROUDSOUL.get())
                .define('U', getUpgradeVariantForLevel(level))
                .define('S', SpecialEffectCrystalIngredient.of(seLocation, level))
                .unlockedBy("has_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                .save(consumer, recipeId);
    }

    /**
     * 按当前等级选择升格材料。
     */
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

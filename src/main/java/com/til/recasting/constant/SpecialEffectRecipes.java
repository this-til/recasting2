package com.til.recasting.constant;

import com.til.recasting.generated.RecipeBuilderWrapper;
import com.til.recasting.mixin.RecipeProviderMixin;
import com.til.recasting.recipe.SpecialEffectCrystalShapedRecipeBuilder;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.SpecialEffectsRegistry;
import net.minecraft.world.item.Items;

/**
 * Special Effect (SE) 配方定义常量类
 * 所有 SE 配方定义都在这里，通过反射自动读取并生成
 */
public class SpecialEffectRecipes {

    // ==================== 攻击类型增幅 SE ====================

    /**
     * 太虚 SE 结晶配方 - 幻影剑增幅
     * 主题：虚无、幻影、剑
     * F = 蜃楼火（虚幻主题）
     * A = 紫水晶碎片（幻影感）
     * M = GATHERING_PARTING_VARIANT（聚散变体）
     * 输出：等级1的太虚结晶
     */
    public static final RecipeBuilderWrapper GREAT_VOID_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.GREAT_VOID)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.MIRAGE_FLAME.get())
                    .define('A', Items.AMETHYST_SHARD)
                    .define('M', RecastingItems.GATHERING_PARTING_VARIANT.get())
                    .unlockedBy("has_gathering_parting_variant", RecipeProviderMixin.invokeHas(RecastingItems.GATHERING_PARTING_VARIANT.get()))
                    .save(consumer, recipeId);

    /**
     * 利刃 SE 结晶配方 - 斩击增幅
     * 主题：斩击、精准、锋利
     * F = 执念火（执念般的精通）
     * A = 下界合金碎片（锋利坚韧）
     * M = GATHERING_PARTING_VARIANT（聚散变体）
     * 输出：等级1的斩击精通结晶
     */
    public static final RecipeBuilderWrapper SHARP_BLADE_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.SHARP_BLADE)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.OBSESSION_FLAME.get())
                    .define('A', Items.NETHERITE_SCRAP)
                    .define('M', RecastingItems.GATHERING_PARTING_VARIANT.get())
                    .unlockedBy("has_gathering_parting_variant", RecipeProviderMixin.invokeHas(RecastingItems.GATHERING_PARTING_VARIANT.get()))
                    .save(consumer, recipeId);

    /**
     * 震荡 SE 结晶配方 - 次元斩增幅
     * 主题：次元、震荡、空间
     * F = 混沌火（混乱的次元能量）
     * A = 末影珍珠（次元传送）
     * M = GATHERING_PARTING_VARIANT（聚散变体）
     * 输出：等级1的震荡结晶
     */
    public static final RecipeBuilderWrapper SHOCK_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.SHOCK)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.CHAOS_FLAME.get())
                    .define('A', Items.ENDER_PEARL)
                    .define('M', RecastingItems.GATHERING_PARTING_VARIANT.get())
                    .unlockedBy("has_gathering_parting_variant", RecipeProviderMixin.invokeHas(RecastingItems.GATHERING_PARTING_VARIANT.get()))
                    .save(consumer, recipeId);

    /**
     * 剑气纵横 SE 结晶配方 - 剑气增幅
     * 主题：剑气、能量、飞行
     * F = 圣愿火（纯净的剑气能量）
     * A = 烈焰棒（剑气的能量源）
     * M = GATHERING_PARTING_VARIANT（聚散变体）
     * 输出：等级1的剑气纵横结晶
     */
    public static final RecipeBuilderWrapper SWORD_QI_MASTERY_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.SWORD_QI_MASTERY)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.HOLY_FLAME.get())
                    .define('A', Items.BLAZE_ROD)
                    .define('M', RecastingItems.GATHERING_PARTING_VARIANT.get())
                    .unlockedBy("has_gathering_parting_variant", RecipeProviderMixin.invokeHas(RecastingItems.GATHERING_PARTING_VARIANT.get()))
                    .save(consumer, recipeId);

    /**
     * 雷霆万钧 SE 结晶配方 - 闪电增幅
     * 主题：雷电、破坏、天威
     * F = 王权火（天威般的王权）
     * A = 三叉戟（雷电武器）
     * M = GATHERING_PARTING_VARIANT（聚散变体）
     * 输出：等级1的雷霆万钧结晶
     */
    public static final RecipeBuilderWrapper THUNDER_STRIKE_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.THUNDER_STRIKE)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.ROYAL_FLAME.get())
                    .define('A', Items.TRIDENT)
                    .define('M', RecastingItems.GATHERING_PARTING_VARIANT.get())
                    .unlockedBy("has_gathering_parting_variant", RecipeProviderMixin.invokeHas(RecastingItems.GATHERING_PARTING_VARIANT.get()))
                    .save(consumer, recipeId);

}


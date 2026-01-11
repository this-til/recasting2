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
 * <p>
 * 使用方式：
 * 使用 lambda 表达式定义 RecipeBuilderWrapper，配方ID会自动使用字段名转小写
 * <p>
 * 配方格式：
 * 字段名 GREAT_VOID_SE_CRYSTAL_RECIPE -> 配方ID: recasting:great_void_se_crystal_recipe
 * <p>
 * 攻击类型增幅 SE 配方布局：
 * <pre>
 *   T M T
 *   F G F
 *   F B F
 * </pre>
 * T = 顶部物品（特色物品）
 * M = 中间物品（GATHERING_PARTING_VARIANT）
 * F = 火焰（左下和左上相同）
 * B = 底部物品（特色物品）
 * G = 特殊物品（根据 SE 特性）
 */
public class SpecialEffectRecipes {

    // ==================== 攻击类型增幅 SE ====================

    /**
     * 太虚 SE 结晶配方 - 幻影剑增幅
     * 主题：虚无、幻影、剑
     * T = 紫水晶碎片（幻影感）
     * M = GATHERING_PARTING_VARIANT（聚散变体）
     * F = 蜃楼火（虚幻主题）
     * G = 幻翼膜（幻影主题）
     * B = 铁剑（剑的代表）
     * 输出：等级1的太虚结晶
     */
    public static final RecipeBuilderWrapper GREAT_VOID_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.GREAT_VOID)
                    .pattern("TMT")
                    .pattern("FGF")
                    .pattern("FBF")
                    .define('T', Items.AMETHYST_SHARD)
                    .define('M', RecastingItems.GATHERING_PARTING_VARIANT.get())
                    .define('F', RecastingItems.MIRAGE_FLAME.get())
                    .define('G', Items.PHANTOM_MEMBRANE)
                    .define('B', Items.IRON_SWORD)
                    .unlockedBy("has_gathering_parting_variant", RecipeProviderMixin.invokeHas(RecastingItems.GATHERING_PARTING_VARIANT.get()))
                    .save(consumer, recipeId);

    /**
     * 斩击精通 SE 结晶配方 - 斩击增幅
     * 主题：斩击、精准、锋利
     * T = 烈焰粉（斩击的力量）
     * M = GATHERING_PARTING_VARIANT（聚散变体）
     * F = 执念火（执念般的精通）
     * G = 下界合金碎片（锋利坚韧）
     * B = 钻石剑（斩击武器）
     * 输出：等级1的斩击精通结晶
     */
    public static final RecipeBuilderWrapper SLASH_MASTERY_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.SLASH_MASTERY)
                    .pattern("TMT")
                    .pattern("FGF")
                    .pattern("FBF")
                    .define('T', Items.BLAZE_POWDER)
                    .define('M', RecastingItems.GATHERING_PARTING_VARIANT.get())
                    .define('F', RecastingItems.OBSESSION_FLAME.get())
                    .define('G', Items.NETHERITE_SCRAP)
                    .define('B', Items.DIAMOND_SWORD)
                    .unlockedBy("has_gathering_parting_variant", RecipeProviderMixin.invokeHas(RecastingItems.GATHERING_PARTING_VARIANT.get()))
                    .save(consumer, recipeId);

    /**
     * 震荡 SE 结晶配方 - 次元斩增幅
     * 主题：次元、震荡、空间
     * T = 末影珍珠（次元传送）
     * M = GATHERING_PARTING_VARIANT（聚散变体）
     * F = 混沌火（混乱的次元能量）
     * G = 紫颂果（末地次元）
     * B = 黑曜石（稳定次元）
     * 输出：等级1的震荡结晶
     */
    public static final RecipeBuilderWrapper SHOCK_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.SHOCK)
                    .pattern("TMT")
                    .pattern("FGF")
                    .pattern("FBF")
                    .define('T', Items.ENDER_PEARL)
                    .define('M', RecastingItems.GATHERING_PARTING_VARIANT.get())
                    .define('F', RecastingItems.CHAOS_FLAME.get())
                    .define('G', Items.CHORUS_FRUIT)
                    .define('B', Items.OBSIDIAN)
                    .unlockedBy("has_gathering_parting_variant", RecipeProviderMixin.invokeHas(RecastingItems.GATHERING_PARTING_VARIANT.get()))
                    .save(consumer, recipeId);

    /**
     * 剑气纵横 SE 结晶配方 - 剑气增幅
     * 主题：剑气、能量、飞行
     * T = 羽毛（飞行）
     * M = GATHERING_PARTING_VARIANT（聚散变体）
     * F = 圣愿火（纯净的剑气能量）
     * G = 烈焰棒（剑气的能量源）
     * B = 金剑（贵重的剑气）
     * 输出：等级1的剑气纵横结晶
     */
    public static final RecipeBuilderWrapper SWORD_QI_MASTERY_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.SWORD_QI_MASTERY)
                    .pattern("TMT")
                    .pattern("FGF")
                    .pattern("FBF")
                    .define('T', Items.FEATHER)
                    .define('M', RecastingItems.GATHERING_PARTING_VARIANT.get())
                    .define('F', RecastingItems.HOLY_FLAME.get())
                    .define('G', Items.BLAZE_ROD)
                    .define('B', Items.GOLDEN_SWORD)
                    .unlockedBy("has_gathering_parting_variant", RecipeProviderMixin.invokeHas(RecastingItems.GATHERING_PARTING_VARIANT.get()))
                    .save(consumer, recipeId);

    /**
     * 雷霆万钧 SE 结晶配方 - 闪电增幅
     * 主题：雷电、破坏、天威
     * T = 萤石粉（闪电的光芒）
     * M = GATHERING_PARTING_VARIANT（聚散变体）
     * F = 王权火（天威般的王权）
     * G = 海晶砂粒（雷电之力）
     * B = 三叉戟（雷电武器）
     * 输出：等级1的雷霆万钧结晶
     */
    public static final RecipeBuilderWrapper THUNDER_STRIKE_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.THUNDER_STRIKE)
                    .pattern("TMT")
                    .pattern("FGF")
                    .pattern("FBF")
                    .define('T', Items.GLOWSTONE_DUST)
                    .define('M', RecastingItems.GATHERING_PARTING_VARIANT.get())
                    .define('F', RecastingItems.ROYAL_FLAME.get())
                    .define('G', Items.PRISMARINE_CRYSTALS)
                    .define('B', Items.TRIDENT)
                    .unlockedBy("has_gathering_parting_variant", RecipeProviderMixin.invokeHas(RecastingItems.GATHERING_PARTING_VARIANT.get()))
                    .save(consumer, recipeId);

}


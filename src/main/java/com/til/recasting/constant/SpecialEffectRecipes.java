package com.til.recasting.constant;

import com.til.recasting.generated.RecipeBuilderWrapper;
import com.til.recasting.mixin.RecipeProviderMixin;
import com.til.recasting.recipe.SpecialEffectCrystalIngredient;
import com.til.recasting.recipe.SpecialEffectCrystalShapedRecipeBuilder;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.SpecialEffectsRegistry;
import com.til.recasting.registry.requir.SlashBladeItems;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

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
     * A = 海晶石碎片（海洋与雷电的共鸣）
     * M = GATHERING_PARTING_VARIANT（聚散变体）
     * 输出：等级1的雷霆万钧结晶
     */
    public static final RecipeBuilderWrapper THUNDER_STRIKE_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.THUNDER_STRIKE)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.ROYAL_FLAME.get())
                    .define('A', Items.PRISMARINE_SHARD)
                    .define('M', RecastingItems.GATHERING_PARTING_VARIANT.get())
                    .unlockedBy("has_gathering_parting_variant", RecipeProviderMixin.invokeHas(RecastingItems.GATHERING_PARTING_VARIANT.get()))
                    .save(consumer, recipeId);

    /**
     * 创建 SE 升级配方（含等级 0 制作）
     * 合成表格式：
     * " VS"
     * "VUV"
     * "SV "
     * V = 耀魂碎片；U = 升格变体（升级）或渊寂火（1 级降为 0 级）；S = 当前等级的 SE 结晶
     *
     * @param seType SE 类型
     * @return 先为「1 级 + 渊寂火 → 0 级」（若 maxLevel ≥ 1），再为各档升级配方
     */
    public static List<RecipeBuilderWrapper> createSEUpgradeRecipes(RegistryObject<SpecialEffect> seType) {
        List<RecipeBuilderWrapper> recipes = new ArrayList<>();
        
        // 获取 SE 的扩展信息
        SpecialEffect se = seType.get();
        if (!(se instanceof SpecialEffectsRegistry.ExtendedSpecialEffect extendedSE)) {
            return recipes; // 如果不是扩展 SE，返回空列表
        }
        
        // 获取SE的ResourceLocation
        ResourceLocation seLocation = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getKey(se);
        if (seLocation == null) {
            return recipes;
        }
        
        int maxLevel = extendedSE.getMaxLevel();

        // 等级 1 结晶 + 渊寂火 → 等级 0 结晶（与升格同形，中间为渊寂火）
        if (maxLevel >= 1) {
            final ResourceLocation demoteSeLocation = seLocation;
            RecipeBuilderWrapper toLevel0 = (consumer, recipeId) ->
                    SpecialEffectCrystalShapedRecipeBuilder.shaped(seType, 0)
                            .pattern(" U ")
                            .pattern("USU")
                            .pattern(" U ")
                            .define('U', RecastingItems.ABYSS_FLAME.get())
                            .define('S', SpecialEffectCrystalIngredient.of(demoteSeLocation, 1))
                            .unlockedBy("has_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                            .save(consumer, recipeId);
            recipes.add(toLevel0);
        }
        
        // 为每个等级创建升级配方（从 1 升到 2，从 2 升到 3，...）
        for (int currentLevel = 1; currentLevel < maxLevel; currentLevel++) {
            final int level = currentLevel;
            final int nextLevel = currentLevel + 1;
            final ResourceLocation finalSeLocation = seLocation;
            
            // 根据等级选择对应的升格变体
            ItemLike upgradeVariant = getUpgradeVariantForLevel(level);
            
            RecipeBuilderWrapper recipe = (consumer, recipeId) ->
                    SpecialEffectCrystalShapedRecipeBuilder.shaped(seType, nextLevel)
                            .pattern(" VS")
                            .pattern("VUV")
                            .pattern("SV ")
                            .define('V', SlashBladeItems.PROUDSOUL.get())                     // 耀魂碎片
                            .define('U', upgradeVariant)                                            // 升格变体
                            .define('S', SpecialEffectCrystalIngredient.of(finalSeLocation, level)) // SE 结晶（当前等级）
                            .unlockedBy("has_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                            .save(consumer, recipeId);
            
            recipes.add(recipe);
        }
        
        return recipes;
    }
    
    /**
     * 根据等级获取对应的升格变体
     * 
     * @param level 等级
     * @return 对应的升格变体物品
     */
    private static ItemLike getUpgradeVariantForLevel(int level) {
        return switch (level) {
            case 1 -> RecastingItems.UPGRADE_VARIANT.get();
            case 2 -> RecastingItems.UPGRADE_VARIANT_2.get();
            case 3 -> RecastingItems.UPGRADE_VARIANT_3.get();
            case 4 -> RecastingItems.UPGRADE_VARIANT_4.get();
            default -> RecastingItems.UPGRADE_VARIANT.get(); // 默认返回等级 1 的变体
        };
    }

    // ==================== SE 升级配方 ====================
    // 以下字段会自动被 RecastingRecipeProvider 扫描并生成配方
    // 排除特殊刀SE：BLACK_ROSE, STAR_BLINK, STAR_BLINK_LAMBDA, COLOR_DYE

    /**
     * 协同 SE 结晶配方 - 从利刃升级
     * 主题：协同、配合、协作、双重
     * F = 镜火（镜像般的协同）
     * A = 金萝卜（协作的象征）
     * M = 利刃 SE结晶（中间，等级1）
     * 输出：等级1的协同结晶
     */
    public static final RecipeBuilderWrapper COOPERATE_WITH_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.COOPERATE_WITH, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.MIRROR_FLAME.get())
                    .define('A', Items.GOLDEN_CARROT)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SHARP_BLADE.getId(), 1))
                    .unlockedBy("has_sharp_blade_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 十字斩 SE 结晶配方 - 从协同升级
     * 主题：十字、斩击、交叉、精准
     * F = 镜火（与协同一致）
     * A = 金剑（斩击的象征）
     * M = 协同 SE结晶（中间，等级1）
     * 输出：等级1的十字斩结晶
     */
    public static final RecipeBuilderWrapper CROSS_CHOP_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.CROSS_CHOP, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.MIRROR_FLAME.get())
                    .define('A', Items.GOLDEN_SWORD)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.COOPERATE_WITH.getId(), 1))
                    .unlockedBy("has_cooperate_with_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 生长 SE 结晶配方 - 从协同升级
     * 主题：生长、恢复、生命、治愈
     * F = 摇篮火（生命的摇篮）
     * A = 绿宝石（生命的象征）
     * M = 协同 SE结晶（中间，等级1）
     * 输出：等级1的生长结晶
     */
    public static final RecipeBuilderWrapper GROWTH_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.GROWTH, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.CRADLE_FLAME.get())
                    .define('A', Items.EMERALD)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.COOPERATE_WITH.getId(), 1))
                    .unlockedBy("has_cooperate_with_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 回溯 SE 结晶配方 - 从生长升级
     * 主题：回溯、恢复、耐久、循环
     * F = 摇篮火（与生长一致）
     * A = 铁砧（修复的象征）
     * M = 生长 SE结晶（中间，等级1）
     * 输出：等级1的回溯结晶
     */
    public static final RecipeBuilderWrapper REGRESSION_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.REGRESSION, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.CRADLE_FLAME.get())
                    .define('A', Items.ANVIL)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.GROWTH.getId(), 1))
                    .unlockedBy("has_growth_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 吸血转化 SE 结晶配方 - 从回溯升级
     * 主题：吸血、转化、生命汲取
     * F = 罪火（汲取的罪孽）
     * A = 金苹果（生命的转化）
     * M = 回溯 SE结晶（中间，等级1）
     * 输出：等级1的吸血转化结晶
     */
    public static final RecipeBuilderWrapper LIFE_STEAL_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.LIFE_STEAL, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.SIN_FLAME.get())
                    .define('A', Items.GOLDEN_APPLE)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.REGRESSION.getId(), 1))
                    .unlockedBy("has_regression_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 抵抗 SE 结晶配方 - 从十字斩升级
     * 主题：抵抗、防御、坚韧
     * F = 工匠火（坚韧的防御）
     * A = 盾牌（防御的象征）
     * M = 十字斩 SE结晶（中间，等级1）
     * 输出：等级1的抵抗结晶
     */
    public static final RecipeBuilderWrapper RESIST_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.RESIST, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.CRAFTSMAN_FLAME.get())
                    .define('A', Items.SHIELD)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.CROSS_CHOP.getId(), 1))
                    .unlockedBy("has_cross_chop_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 破片 SE 结晶配方 - 从太虚升级
     * 主题：破片、碎片、幻影剑
     * F = 蜃楼火（与太虚一致）
     * A = 紫水晶碎片（碎片感）
     * M = 太虚 SE结晶（中间，等级1）
     * 输出：等级1的破片结晶
     */
    public static final RecipeBuilderWrapper FRAGMENT_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.FRAGMENT, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.MIRAGE_FLAME.get())
                    .define('A', Items.AMETHYST_SHARD)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.GREAT_VOID.getId(), 1))
                    .unlockedBy("has_great_void_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 回旋 SE 结晶配方 - 从破片升级
     * 主题：回旋、螺旋、剑势
     * F = 深渊火（螺旋的深渊）
     * A = 末影珍珠（回旋的空间）
     * M = 破片 SE结晶（中间，等级1）
     * 输出：等级1的回旋结晶
     */
    public static final RecipeBuilderWrapper SPIRAL_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.SPIRAL, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.ABYSS_FLAME.get())
                    .define('A', Items.ENDER_PEARL)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.FRAGMENT.getId(), 1))
                    .unlockedBy("has_fragment_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 撕裂 SE 结晶配方 - 从震荡升级
     * 主题：撕裂、次元斩、层数叠加
     * F = 混沌火（与震荡一致）
     * A = 钻石（撕裂的力量）
     * M = 震荡 SE结晶（中间，等级1）
     * 输出：等级1的撕裂结晶
     */
    public static final RecipeBuilderWrapper TEAR_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.TEAR, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.CHAOS_FLAME.get())
                    .define('A', Items.DIAMOND)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SHOCK.getId(), 1))
                    .unlockedBy("has_shock_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 旋风 SE 结晶配方 - 从过载升级
     * 主题：旋风、重复伤害、次元斩
     * F = 潮火（与过载一致）
     * A = 末影之眼（次元的循环）
     * M = 过载 SE结晶（中间，等级1）
     * 输出：等级1的旋风结晶
     */
    public static final RecipeBuilderWrapper WHIRLWIND_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.WHIRLWIND, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.TIDE_FLAME.get())
                    .define('A', Items.ENDER_EYE)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.OVERLOAD.getId(), 1))
                    .unlockedBy("has_overload_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 断灭 SE 结晶配方 - 从断却升级
     * 主题：断灭、巨型次元斩、毁灭
     * F = 彼岸火（与断却一致）
     * A = 信标（毁灭的象征）
     * M = 断却 SE结晶（中间，等级1）
     * 输出：等级1的断灭结晶
     */
    public static final RecipeBuilderWrapper ANNIHILATION_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.ANNIHILATION, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.OTHER_SHORE_FLAME.get())
                    .define('A', Items.BEACON)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SEVER_BREAK.getId(), 1))
                    .unlockedBy("has_sever_break_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 过载 SE 结晶配方 - 从撕裂升级
     * 主题：过载、概率触发、次元斩
     * F = 潮火（过载的潮涌）
     * A = 红石（能量的过载）
     * M = 撕裂 SE结晶（中间，等级1）
     * 输出：等级1的过载结晶
     */
    public static final RecipeBuilderWrapper OVERLOAD_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.OVERLOAD, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.TIDE_FLAME.get())
                    .define('A', Items.REDSTONE)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.TEAR.getId(), 1))
                    .unlockedBy("has_tear_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 断却 SE 结晶配方 - 从撕裂升级
     * 主题：断却、大伤害、大范围
     * F = 彼岸火（断却的彼岸）
     * A = 钻石剑（大伤害的象征）
     * M = 撕裂 SE结晶（中间，等级1）
     * 输出：等级1的断却结晶
     */
    public static final RecipeBuilderWrapper SEVER_BREAK_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.SEVER_BREAK, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.OTHER_SHORE_FLAME.get())
                    .define('A', Items.DIAMOND_SWORD)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.TEAR.getId(), 1))
                    .unlockedBy("has_tear_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 风暴 SE 结晶配方 - 从断罪升级
     * 主题：风暴、幻影剑、审判
     * F = 混沌火（与断罪一致）
     * A = 烈焰棒（风暴的能量）
     * M = 断罪 SE结晶（中间，等级1）
     * 输出：等级1的风暴结晶
     */
    public static final RecipeBuilderWrapper STORM_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.STORM, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.CHAOS_FLAME.get())
                    .define('A', Items.BLAZE_ROD)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.JUDGEMENT.getId(), 1))
                    .unlockedBy("has_judgement_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 风暴变体 SE 结晶配方 - 从风暴升级
     * 主题：风暴变体、上方召唤、幻影剑
     * F = 混沌火（与断罪一致）
     * A = 三叉戟（上方的召唤）
     * M = 风暴 SE结晶（中间，等级1）
     * 输出：等级1的风暴变体结晶
     */
    public static final RecipeBuilderWrapper STORM_VARIANT_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.STORM_VARIANT, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.CHAOS_FLAME.get())
                    .define('A', Items.TRIDENT)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.STORM.getId(), 1))
                    .unlockedBy("has_storm_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 电离 SE 结晶配方 - 从雷霆万钧升级
     * 主题：电离、增伤、雷电伤害
     * F = 记忆火（电离的记忆）
     * A = 青金石（电离的能量）
     * M = 雷霆万钧 SE结晶（中间，等级1）
     * 输出：等级1的电离结晶
     */
    public static final RecipeBuilderWrapper IONIZATION_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.IONIZATION, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.MEMORY_FLAME.get())
                    .define('A', Items.LAPIS_LAZULI)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.THUNDER_STRIKE.getId(), 1))
                    .unlockedBy("has_thunder_strike_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 雷神之怒 SE 结晶配方 - 从电离升级
     * 主题：雷神之怒、击杀、强力闪电
     * F = 记忆火（与电离一致）
     * A = 金锭（神怒的象征）
     * M = 电离 SE结晶（中间，等级1）
     * 输出：等级1的雷神之怒结晶
     */
    public static final RecipeBuilderWrapper THUNDER_GODS_WRATH_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.THUNDER_GODS_WRATH, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.MEMORY_FLAME.get())
                    .define('A', Items.GOLD_INGOT)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.IONIZATION.getId(), 1))
                    .unlockedBy("has_ionization_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 雷暴 SE 结晶配方 - 从电离升级
     * 主题：雷暴、多道闪电、SA触发
     * F = 记忆火（与电离一致）
     * A = 三叉戟（雷电的象征）
     * M = 电离 SE结晶（中间，等级1）
     * 输出：等级1的雷暴结晶
     */
    public static final RecipeBuilderWrapper THUNDERSTORM_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.THUNDERSTORM, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.MEMORY_FLAME.get())
                    .define('A', Items.TRIDENT)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.IONIZATION.getId(), 1))
                    .unlockedBy("has_ionization_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 雷云 SE 结晶配方 - 从雷暴升级
     * 主题：雷云、雷光buff、附加伤害
     * F = 诗灰火（雷云的诗意）
     * A = 海晶石碎片（雷云的象征）
     * M = 雷暴 SE结晶（中间，等级1）
     * 输出：等级1的雷云结晶
     */
    public static final RecipeBuilderWrapper THUNDER_CLOUD_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.THUNDER_CLOUD, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.POETRY_ASH_FLAME.get())
                    .define('A', Items.PRISMARINE_SHARD)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.THUNDERSTORM.getId(), 1))
                    .unlockedBy("has_thunderstorm_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 蓄能 SE 结晶配方 - 从雷暴升级
     * 主题：蓄能、层数叠加、闪电攻击
     * F = 潮火（能量的潮涌）
     * A = 红石（能量的蓄积）
     * M = 雷暴 SE结晶（中间，等级1）
     * 输出：等级1的蓄能结晶
     */
    public static final RecipeBuilderWrapper ENERGY_STORAGE_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.ENERGY_STORAGE, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.TIDE_FLAME.get())
                    .define('A', Items.REDSTONE)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.THUNDERSTORM.getId(), 1))
                    .unlockedBy("has_thunderstorm_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 剑气释放 SE 结晶配方 - 从剑气纵横升级
     * 主题：剑气释放、概率触发、剑气
     * F = 圣愿火（与剑气纵横一致）
     * A = 铁剑（剑气的释放）
     * M = 剑气纵横 SE结晶（中间，等级1）
     * 输出：等级1的剑气释放结晶
     */
    public static final RecipeBuilderWrapper DRIVE_RELEASE_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.DRIVE_RELEASE, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.HOLY_FLAME.get())
                    .define('A', Items.IRON_SWORD)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SWORD_QI_MASTERY.getId(), 1))
                    .unlockedBy("has_sword_qi_mastery_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 断罪 SE 结晶配方 - 从震荡升级
     * 主题：断罪、次元斩、SA触发
     * F = 混沌火（与震荡一致）
     * A = 钻石剑（断罪的象征）
     * M = 震荡 SE结晶（中间，等级1）
     * 输出：等级1的断罪结晶
     */
    public static final RecipeBuilderWrapper JUDGEMENT_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.JUDGEMENT, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.CHAOS_FLAME.get())
                    .define('A', Items.DIAMOND_SWORD)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SHOCK.getId(), 1))
                    .unlockedBy("has_shock_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 分裂 SE 结晶配方 - 从破片升级
     * 主题：分裂、幻影剑、辅助攻击
     * F = 蜃楼火（与太虚一致）
     * A = 烈焰棒（分裂的能量）
     * M = 破片 SE结晶（中间，等级1）
     * 输出：等级1的分裂结晶
     */
    public static final RecipeBuilderWrapper SPLIT_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.SPLIT, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.MIRAGE_FLAME.get())
                    .define('A', Items.BLAZE_ROD)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.FRAGMENT.getId(), 1))
                    .unlockedBy("has_fragment_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 冲击 SE 结晶配方 - 从分裂升级
     * 主题：冲击、幻影剑、瞬间伤害
     * F = 镜火（与协同一致）
     * A = 铁剑（冲击的象征）
     * M = 分裂 SE结晶（中间，等级1）
     * 输出：等级1的冲击结晶
     */
    public static final RecipeBuilderWrapper IMPACT_SE_CRYSTAL_RECIPE = (consumer, recipeId) ->
            SpecialEffectCrystalShapedRecipeBuilder.shaped(SpecialEffectsRegistry.IMPACT, 1)
                    .pattern("FAF")
                    .pattern("AMA")
                    .pattern("FAF")
                    .define('F', RecastingItems.MIRROR_FLAME.get())
                    .define('A', Items.IRON_SWORD)
                    .define('M', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SPLIT.getId(), 1))
                    .unlockedBy("has_split_se_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    // 普通 SE 升级配方
    public static final List<RecipeBuilderWrapper> COOPERATE_WITH_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.COOPERATE_WITH);
    public static final List<RecipeBuilderWrapper> CROSS_CHOP_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.CROSS_CHOP);
    public static final List<RecipeBuilderWrapper> DRIVE_RELEASE_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.DRIVE_RELEASE);
    public static final List<RecipeBuilderWrapper> GROWTH_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.GROWTH);
    public static final List<RecipeBuilderWrapper> LIFE_STEAL_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.LIFE_STEAL);
    public static final List<RecipeBuilderWrapper> REGRESSION_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.REGRESSION);
    public static final List<RecipeBuilderWrapper> JUDGEMENT_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.JUDGEMENT);
    public static final List<RecipeBuilderWrapper> THUNDERSTORM_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.THUNDERSTORM);
    public static final List<RecipeBuilderWrapper> THUNDER_GODS_WRATH_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.THUNDER_GODS_WRATH);
    public static final List<RecipeBuilderWrapper> IONIZATION_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.IONIZATION);
    public static final List<RecipeBuilderWrapper> ENERGY_STORAGE_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.ENERGY_STORAGE);
    public static final List<RecipeBuilderWrapper> THUNDER_CLOUD_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.THUNDER_CLOUD);
    public static final List<RecipeBuilderWrapper> IMPACT_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.IMPACT);
    public static final List<RecipeBuilderWrapper> OVERLOAD_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.OVERLOAD);
    public static final List<RecipeBuilderWrapper> RESIST_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.RESIST);
    public static final List<RecipeBuilderWrapper> SEVER_BREAK_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.SEVER_BREAK);
    public static final List<RecipeBuilderWrapper> STORM_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.STORM);
    public static final List<RecipeBuilderWrapper> STORM_VARIANT_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.STORM_VARIANT);
    public static final List<RecipeBuilderWrapper> SPLIT_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.SPLIT);
    public static final List<RecipeBuilderWrapper> SPIRAL_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.SPIRAL);
    public static final List<RecipeBuilderWrapper> FRAGMENT_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.FRAGMENT);
    public static final List<RecipeBuilderWrapper> TEAR_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.TEAR);
    public static final List<RecipeBuilderWrapper> WHIRLWIND_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.WHIRLWIND);
    public static final List<RecipeBuilderWrapper> ANNIHILATION_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.ANNIHILATION);

    // 攻击类型增幅 SE 升级配方
    public static final List<RecipeBuilderWrapper> GREAT_VOID_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.GREAT_VOID);
    public static final List<RecipeBuilderWrapper> SHARP_BLADE_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.SHARP_BLADE);
    public static final List<RecipeBuilderWrapper> SHOCK_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.SHOCK);
    public static final List<RecipeBuilderWrapper> SWORD_QI_MASTERY_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.SWORD_QI_MASTERY);
    public static final List<RecipeBuilderWrapper> THUNDER_STRIKE_UPGRADE_RECIPES = createSEUpgradeRecipes(SpecialEffectsRegistry.THUNDER_STRIKE);

}


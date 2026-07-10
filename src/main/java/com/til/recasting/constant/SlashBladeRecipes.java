package com.til.recasting.constant;

import com.til.recasting.generated.RecipeBuilderWrapper;
import com.til.recasting.mixin.RecipeProviderMixin;
import com.til.recasting.recipe.SpecialEffectCrystalIngredient;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.SpecialEffectsRegistry;
import com.til.recasting.registry.requir.SlashBladeItems;
import mods.flammpfeil.slashblade.recipe.RequestDefinition;
import mods.flammpfeil.slashblade.recipe.SlashBladeIngredient;
import mods.flammpfeil.slashblade.recipe.SlashBladeShapedRecipeBuilder;
import mods.flammpfeil.slashblade.registry.slashblade.EnchantmentDefinition;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Recasting 模组的刀配方定义常量类
 * 所有刀配方定义都在这里，通过反射自动读取并生成
 */
public class SlashBladeRecipes {


    /**
     * 阔刃（木）配方：基础配方，无前置刀
     * 材料：木棍2个 + 木板3个
     * S=木棍, W=木板
     */
    public static final RecipeBuilderWrapper BROADSWORD_WOOD_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.BROADSWORD_WOOD.getName())
                    .pattern("  W")
                    .pattern(" W ")
                    .pattern("SP ")
                    .define('S', Items.STICK)
                    .define('W', Items.OAK_PLANKS)
                    .define('P', SlashBladeItems.PROUDSOUL.get())
                    .unlockedBy("has_stick", RecipeProviderMixin.invokeHas(Items.STICK))
                    .save(consumer, recipeId);

    /**
     * 青锋（木）配方：基础配方，无前置刀
     * 材料：木棍2个 + 木板3个
     * S=木棍, W=木板
     */
    public static final RecipeBuilderWrapper GREEN_BLADE_WOOD_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.GREEN_BLADE_WOOD.getName())
                    .pattern(" W ")
                    .pattern(" W ")
                    .pattern(" SP")
                    .define('S', Items.STICK)
                    .define('W', Items.OAK_PLANKS)
                    .define('P', SlashBladeItems.PROUDSOUL.get())
                    .unlockedBy("has_stick", RecipeProviderMixin.invokeHas(Items.STICK))
                    .save(consumer, recipeId);

    /**
     * 阔刃（铁）配方：从阔刃（木）升级
     * 要求：杀敌>50、锻造>5
     */
    public static final RecipeBuilderWrapper BROADSWORD_IRON_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.BROADSWORD_IRON.getName())
                    .pattern("  I")
                    .pattern(" I ")
                    .pattern("B  ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.BROADSWORD_WOOD.getName())
                                    .killCount(50)
                                    .refineCount(5)
                                    .build()))
                    .define('I', SlashBladeItems.PROUDSOUL_INGOT.get())
                    .unlockedBy("has_iron_ingot", RecipeProviderMixin.invokeHas(Items.IRON_INGOT))
                    .save(consumer, recipeId);


    /**
     * 青锋（铁）配方：从青锋（木）升级
     * 要求：杀敌>50、锻造>5
     */
    public static final RecipeBuilderWrapper GREEN_BLADE_IRON_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.GREEN_BLADE_IRON.getName())
                    .pattern(" I ")
                    .pattern(" I ")
                    .pattern(" B ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.GREEN_BLADE_WOOD.getName())
                                    .killCount(50)
                                    .refineCount(5)
                                    .build()))
                    .define('I', SlashBladeItems.PROUDSOUL_INGOT.get())
                    .unlockedBy("has_iron_ingot", RecipeProviderMixin.invokeHas(Items.IRON_INGOT))
                    .save(consumer, recipeId);

    /**
     * 闪茶配方：从阔刃（铁）升级
     * 要求：杀敌1000、锻造500
     * I=荣耀锭, B=阔刃（铁）
     */
    public static final RecipeBuilderWrapper SHINE_TEA_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.SHINE_TEA.getName())
                    .pattern("  I")
                    .pattern(" I ")
                    .pattern("B  ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.BROADSWORD_IRON.getName())
                                    .killCount(1000)
                                    .refineCount(500)
                                    .build()))
                    .define('I', SlashBladeItems.PROUDSOUL_INGOT.get())
                    .unlockedBy("has_iron_ingot", RecipeProviderMixin.invokeHas(Items.IRON_INGOT))
                    .save(consumer, recipeId);

    /**
     * 闪茶 Lambda 配方：闪茶 → ^闪茶
     * 要求：杀敌2000、锻造1000
     * I=荣耀锭, B=闪茶
     */
    public static final RecipeBuilderWrapper SHINE_TEA_LAMBDA_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.SHINE_TEA_LAMBDA.getName())
                    .pattern("  I")
                    .pattern(" I ")
                    .pattern("B  ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.SHINE_TEA.getName())
                                    .killCount(2000)
                                    .refineCount(1000)
                                    .build()))
                    .define('I', SlashBladeItems.PROUDSOUL_INGOT.get())
                    .unlockedBy("has_iron_ingot", RecipeProviderMixin.invokeHas(Items.IRON_INGOT))
                    .save(consumer, recipeId);

    /**
     * 灿茶配方：^闪茶 → 灿茶（链：闪 → ^闪 → 灿 → ^灿）
     * 要求：杀敌4000、锻造2000
     * I=荣耀锭, B=闪茶 Lambda
     */
    public static final RecipeBuilderWrapper BRILLIANT_TEA_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.BRILLIANT_TEA.getName())
                    .pattern("  I")
                    .pattern(" I ")
                    .pattern("B  ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.SHINE_TEA_LAMBDA.getName())
                                    .killCount(4000)
                                    .refineCount(2000)
                                    .build()))
                    .define('I', SlashBladeItems.PROUDSOUL_INGOT.get())
                    .unlockedBy("has_iron_ingot", RecipeProviderMixin.invokeHas(Items.IRON_INGOT))
                    .save(consumer, recipeId);

    /**
     * 灿茶 Lambda 配方：灿茶 → ^灿茶
     * 要求：杀敌8000、锻造4000
     * I=荣耀锭, B=灿茶
     */
    public static final RecipeBuilderWrapper BRILLIANT_TEA_LAMBDA_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.BRILLIANT_TEA_LAMBDA.getName())
                    .pattern("  I")
                    .pattern(" I ")
                    .pattern("B  ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.BRILLIANT_TEA.getName())
                                    .killCount(8000)
                                    .refineCount(4000)
                                    .build()))
                    .define('I', SlashBladeItems.PROUDSOUL_INGOT.get())
                    .unlockedBy("has_iron_ingot", RecipeProviderMixin.invokeHas(Items.IRON_INGOT))
                    .save(consumer, recipeId);

    /**
     * 闪金配方：从青锋（铁）升级
     * 要求：杀敌1000、荣耀50000
     * I=荣耀锭, B=青锋（铁）
     */
    public static final RecipeBuilderWrapper SHINE_GOLD_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.SHINE_GOLD.getName())
                    .pattern(" I ")
                    .pattern(" I ")
                    .pattern(" B ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.GREEN_BLADE_IRON.getName())
                                    .killCount(1000)
                                    .proudSoul(50000)
                                    .build()))
                    .define('I', SlashBladeItems.PROUDSOUL_INGOT.get())
                    .unlockedBy("has_iron_ingot", RecipeProviderMixin.invokeHas(Items.IRON_INGOT))
                    .save(consumer, recipeId);

    /**
     * 闪金 Lambda 配方：闪金 → ^闪金
     * 要求：杀敌2000、荣耀100000
     * I=荣耀锭, B=闪金
     */
    public static final RecipeBuilderWrapper SHINE_GOLD_LAMBDA_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.SHINE_GOLD_LAMBDA.getName())
                    .pattern(" I ")
                    .pattern(" I ")
                    .pattern(" B ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.SHINE_GOLD.getName())
                                    .killCount(2000)
                                    .proudSoul(100000)
                                    .build()))
                    .define('I', SlashBladeItems.PROUDSOUL_INGOT.get())
                    .unlockedBy("has_iron_ingot", RecipeProviderMixin.invokeHas(Items.IRON_INGOT))
                    .save(consumer, recipeId);

    /**
     * 灿金配方：^闪金 → 灿金（链：闪 → ^闪 → 灿 → ^灿）
     * 要求：杀敌4000、荣耀200000
     * I=荣耀锭, B=闪金 Lambda
     */
    public static final RecipeBuilderWrapper BRILLIANT_GOLD_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.BRILLIANT_GOLD.getName())
                    .pattern(" I ")
                    .pattern(" I ")
                    .pattern(" B ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.SHINE_GOLD_LAMBDA.getName())
                                    .killCount(4000)
                                    .proudSoul(200000)
                                    .build()))
                    .define('I', SlashBladeItems.PROUDSOUL_INGOT.get())
                    .unlockedBy("has_iron_ingot", RecipeProviderMixin.invokeHas(Items.IRON_INGOT))
                    .save(consumer, recipeId);

    /**
     * 灿金 Lambda 配方：灿金 → ^灿金
     * 要求：杀敌8000、荣耀400000
     * I=荣耀锭, B=灿金
     */
    public static final RecipeBuilderWrapper BRILLIANT_GOLD_LAMBDA_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.BRILLIANT_GOLD_LAMBDA.getName())
                    .pattern(" I ")
                    .pattern(" I ")
                    .pattern(" B ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.BRILLIANT_GOLD.getName())
                                    .killCount(8000)
                                    .proudSoul(400000)
                                    .build()))
                    .define('I', SlashBladeItems.PROUDSOUL_INGOT.get())
                    .unlockedBy("has_iron_ingot", RecipeProviderMixin.invokeHas(Items.IRON_INGOT))
                    .save(consumer, recipeId);


    /**
     * 碎白配方：从青锋（铁）升级
     * 要求：杀敌300、锻造30
     * 材料：执念火2个
     * O=执念火, B=基础刀（青锋铁，满足要求）
     */
    public static final RecipeBuilderWrapper BROKEN_WHITE_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.BROKEN_WHITE.getName())
                    .pattern("  O")
                    .pattern(" B ")
                    .pattern("O  ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.GREEN_BLADE_IRON.getName())
                                    .killCount(300)
                                    .refineCount(30)
                                    .build()))
                    .define('O', RecastingItems.OBSESSION_FLAME.get())
                    .unlockedBy("has_obsession_flame", RecipeProviderMixin.invokeHas(RecastingItems.OBSESSION_FLAME.get()))
                    .save(consumer, recipeId);

    /**
     * 美工刀配方：从阔刃（铁）升级
     * 要求：杀敌50、锻造20、效率2附魔
     * 材料：匠魂火2个
     * C=匠魂火, B=基础刀（阔刃铁，满足要求）
     */
    public static final RecipeBuilderWrapper ART_KNIFE_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.ART_KNIFE.getName())
                    .pattern("  C")
                    .pattern(" B ")
                    .pattern("C  ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.BROADSWORD_IRON.getName())
                                    .killCount(50)
                                    .refineCount(20)
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLOCK_EFFICIENCY), 2))
                                    .build()))
                    .define('C', RecastingItems.CRAFTSMAN_FLAME.get())
                    .unlockedBy("has_craftsman_flame", RecipeProviderMixin.invokeHas(RecastingItems.CRAFTSMAN_FLAME.get()))
                    .save(consumer, recipeId);

    /**
     * 八卦剑配方：从碎白升级
     * 要求：杀敌500、力量2附魔、锋利2附魔
     */
    public static final RecipeBuilderWrapper BA_GUA_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.BA_GUA.getName())
                    .pattern("CPC")
                    .pattern("WSK")
                    .pattern("CPC")
                    .define('S',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.BROKEN_WHITE.getName())
                                    .killCount(500)
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SMITE), 2))
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 2))
                                    .build()))
                    .define('P', RecastingItems.POETRY_ASH_FLAME.get())
                    .define('W', Items.WHITE_WOOL)
                    .define('K', Items.BLACK_WOOL)
                    .define('C', RecastingItems.CHAOS_FLAME.get())
                    .unlockedBy("has_poetry_ash_flame", RecipeProviderMixin.invokeHas(RecastingItems.POETRY_ASH_FLAME.get()))
                    .save(consumer, recipeId);

    /**
     * 八卦剑大配方：从八卦剑升级
     * 要求：杀敌2000、锻造100、力量3附魔、锋利3附魔
     * 材料：诗烬火2个、混沌火2个
     * P=诗烬火, C=混沌火, B=基础刀（八卦剑，满足要求）
     */
    public static final RecipeBuilderWrapper BA_GUA_BIG_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.BA_GUA_BIG.getName())
                    .pattern("CPC")
                    .pattern("PBP")
                    .pattern("CPC")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.BA_GUA.getName())
                                    .killCount(2000)
                                    .refineCount(100)
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SMITE), 3))
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 3))
                                    .build()))
                    .define('P', RecastingItems.POETRY_ASH_FLAME.get())
                    .define('C', RecastingItems.CHAOS_FLAME.get())
                    .unlockedBy("has_poetry_ash_flame", RecipeProviderMixin.invokeHas(RecastingItems.POETRY_ASH_FLAME.get()))
                    .save(consumer, recipeId);

    /**
     * 八卦剑大 Lambda 配方：从八卦剑大升级
     * 要求：杀敌5000、锻造300、力量5附魔、锋利5附魔
     * 材料：古铜色的庸魂立方体6个
     * SE结晶：冲击l1 2个
     * C=古铜色的庸魂立方体, B=基础刀（八卦剑大，满足要求）, I=冲击l1 SE结晶
     */
    public static final RecipeBuilderWrapper BA_GUA_BIG_LAMBDA_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.BA_GUA_BIG_LAMBDA.getName())
                    .pattern("CCC")
                    .pattern("IBI")
                    .pattern("CCC")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.BA_GUA_BIG.getName())
                                    .killCount(5000)
                                    .refineCount(300)
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SMITE), 5))
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 5))
                                    .build()))
                    .define('C', RecastingItems.COPPER_MEDIUM_SOUL_CUBE.get())
                    .define('I', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.IMPACT.getId(), 1))
                    .unlockedBy("has_copper_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.COPPER_MEDIUM_SOUL_CUBE.get()))
                    .save(consumer, recipeId);

    /***
     * 黑刃
     */
    public static final RecipeBuilderWrapper BLACK_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.BLACK.getName())
                    .pattern("  S")
                    .pattern(" B ")
                    .pattern("O  ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.BROADSWORD_IRON.getName())
                                    .killCount(200)
                                    .refineCount(20)
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.UNBREAKING), 3))
                                    .build()))
                    .define('O', Items.OBSIDIAN)
                    .define('S', RecastingItems.SIN_FLAME.get())
                    .unlockedBy("has_sin_flame", RecipeProviderMixin.invokeHas(RecastingItems.SIN_FLAME.get()))
                    .save(consumer, recipeId);

    /**
     * 伞配方：从黑刃升级
     * 要求：杀敌500、锻造100
     * 材料：漆黑的庸魂立方体2个
     * SE结晶：斩断l1 2个
     * C=漆黑的庸魂立方体, B=基础刀（黑刃，满足要求）, S=斩断l1 SE结晶
     */
    public static final RecipeBuilderWrapper UMBRELLA_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.UMBRELLA.getName())
                    .pattern("  S")
                    .pattern("CBC")
                    .pattern("S  ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.BLACK.getName())
                                    .killCount(500)
                                    .refineCount(100)
                                    .build()))
                    .define('C', RecastingItems.NETHERITE_MEDIUM_SOUL_CUBE.get())
                    .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SEVER_BREAK.getId(), 1))
                    .unlockedBy("has_netherite_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.NETHERITE_MEDIUM_SOUL_CUBE.get()))
                    .save(consumer, recipeId);

    /**
     * 伞 Lambda 配方：从伞升级
     * 材料：漆黑的庸魂立方体4个
     * SE结晶：风暴l1 2个、风暴变体l1 2个
     * C=漆黑的庸魂立方体, B=基础刀（伞，满足要求）, S=风暴l1 SE结晶, V=风暴变体l1 SE结晶
     */
    public static final RecipeBuilderWrapper UMBRELLA_LAMBDA_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.UMBRELLA_LAMBDA.getName())
                    .pattern("CSC")
                    .pattern("VBV")
                    .pattern("CSC")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.UMBRELLA.getName())
                                    .build()))
                    .define('C', RecastingItems.NETHERITE_MEDIUM_SOUL_CUBE.get())
                    .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.STORM.getId(), 1))
                    .define('V', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.STORM_VARIANT.getId(), 1))
                    .unlockedBy("has_netherite_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.NETHERITE_MEDIUM_SOUL_CUBE.get()))
                    .save(consumer, recipeId);

    /**
     * 青云配方：从碎白升级
     * 要求：杀敌500、锻造20、锋利3附魔
     * 材料：翠绿的庸魂立方体2个
     * SE结晶：协同l1、十字斩l1
     * E=翠绿的庸魂立方体, B=基础刀（碎白，满足要求）, C=协同l1 SE结晶
     */
    public static final RecipeBuilderWrapper BLUE_CLOUD_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.BLUE_CLOUD.getName())
                    .pattern(" E ")
                    .pattern("CBC")
                    .pattern(" E ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.BROKEN_WHITE.getName())
                                    .killCount(500)
                                    .refineCount(20)
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 3))
                                    .build()))
                    .define('E', RecastingItems.EMERALD_MEDIUM_SOUL_CUBE.get())
                    .define('C', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.COOPERATE_WITH.getId(), 1))
                    .unlockedBy("has_emerald_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.EMERALD_MEDIUM_SOUL_CUBE.get()))
                    .save(consumer, recipeId);

    /**
     * 青云 Lambda 配方：从青云升级
     * 要求：杀敌750、锻造125、锋利5附魔
     * 材料：翠绿的庸魂立方体6个
     * SE结晶：十字斩l1 2个
     * E=翠绿的庸魂立方体, B=基础刀（青云，满足要求）, X=十字斩l1 SE结晶
     */
    public static final RecipeBuilderWrapper BLUE_CLOUD_LAMBDA_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.BLUE_CLOUD_LAMBDA.getName())
                    .pattern("EEE")
                    .pattern("XBX")
                    .pattern("EEE")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.BLUE_CLOUD.getName())
                                    .killCount(750)
                                    .refineCount(125)
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 5))
                                    .build()))
                    .define('E', RecastingItems.EMERALD_MEDIUM_SOUL_CUBE.get())
                    .define('X', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.CROSS_CHOP.getId(), 1))
                    .unlockedBy("has_emerald_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.EMERALD_MEDIUM_SOUL_CUBE.get()))
                    .save(consumer, recipeId);

    /**
     * 冰薄荷配方：从青云 Lambda 升级
     * 要求：杀敌1000、锻造300、锋利5附魔、抢夺2附魔、耐久3附魔、横扫2附魔
     * 材料：天蓝色的庸魂立方体4个
     * SE结晶：生长l2、回溯l2
     * D=天蓝色的庸魂立方体, B=基础刀（青云 Lambda，满足要求）, G=生长l2 SE结晶, R=回溯l2 SE结晶
     */
    public static final RecipeBuilderWrapper COOL_MINT_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.COOL_MINT.getName())
                    .pattern(" DR")
                    .pattern("DBD")
                    .pattern("GD ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.BLUE_CLOUD_LAMBDA.getName())
                                    .killCount(1000)
                                    .refineCount(300)
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 5))
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.MOB_LOOTING), 2))
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.UNBREAKING), 3))
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SWEEPING_EDGE), 2))
                                    .build()))
                    .define('D', RecastingItems.DIAMOND_MEDIUM_SOUL_CUBE.get())
                    .define('G', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.GROWTH.getId(), 1))
                    .define('R', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.REGRESSION.getId(), 1))
                    .unlockedBy("has_diamond_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.DIAMOND_MEDIUM_SOUL_CUBE.get()))
                    .save(consumer, recipeId);

    /**
     * 冰薄荷 Lambda 配方：从冰薄荷升级
     * 要求：杀敌200、锻造500
     * 材料：天蓝色的庸魂立方体4个
     * SE结晶：剑气释放l2 2个
     * D=天蓝色的庸魂立方体, B=基础刀（冰薄荷，满足要求）, C=剑气释放l2 SE结晶
     */
    public static final RecipeBuilderWrapper COOL_MINT_LAMBDA_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.COOL_MINT_LAMBDA.getName())
                    .pattern(" DC")
                    .pattern("DBD")
                    .pattern("CD ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.COOL_MINT.getName())
                                    .killCount(200)
                                    .refineCount(500)
                                    .build()))
                    .define('D', RecastingItems.DIAMOND_MEDIUM_SOUL_CUBE.get())
                    .define('C', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.DRIVE_RELEASE.getId(), 1))
                    .unlockedBy("has_diamond_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.DIAMOND_MEDIUM_SOUL_CUBE.get()))
                    .save(consumer, recipeId);

    /**
     * 龙鳞配方：从八卦剑升级
     * 要求：杀敌300、锻造50、力量3附魔、耐久2附魔
     * SE结晶：分裂l1 2个
     * B=基础刀（八卦剑，满足要求）, I=分裂l1 SE结晶
     */
    public static final RecipeBuilderWrapper DRAGON_SCALE_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.DRAGON_SCALE.getName())
                    .pattern("  I")
                    .pattern(" B ")
                    .pattern("I  ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.BA_GUA.getName())
                                    .killCount(300)
                                    .refineCount(50)
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SMITE), 3))
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.UNBREAKING), 2))
                                    .build()))
                    .define('I', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SPLIT.getId(), 1))
                    .unlockedBy("has_split_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * 龙鳞 Lambda 配方：从龙鳞升级
     * 要求：杀敌500、锻造200、力量5附魔
     * 材料：古铜色的庸魂立方体6个
     * SE结晶：冲击l1 2个
     * C=古铜色的庸魂立方体, B=基础刀（龙鳞，满足要求）, I=冲击l1 SE结晶
     */
    public static final RecipeBuilderWrapper DRAGON_SCALE_LAMBDA_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.DRAGON_SCALE_LAMBDA.getName())
                    .pattern("CCC")
                    .pattern("IBI")
                    .pattern("CCC")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.DRAGON_SCALE.getName())
                                    .killCount(500)
                                    .refineCount(200)
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SMITE), 5))
                                    .build()))
                    .define('C', RecastingItems.COPPER_MEDIUM_SOUL_CUBE.get())
                    .define('I', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.IMPACT.getId(), 1))
                    .unlockedBy("has_copper_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.COPPER_MEDIUM_SOUL_CUBE.get()))
                    .save(consumer, recipeId);

    /**
     * 龙配方：从龙鳞 Lambda 升级
     * 要求：杀敌1000、锻造300
     * 材料：银白色的庸魂立方体4个
     * SE结晶：破片l2 4个
     * C=银白色的庸魂立方体, B=基础刀（龙鳞 Lambda，满足要求）, F=破片l2 SE结晶
     */
    public static final RecipeBuilderWrapper DRAGON_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.DRAGON.getName())
                    .pattern("CFC")
                    .pattern("FBF")
                    .pattern("CFC")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.DRAGON_SCALE_LAMBDA.getName())
                                    .killCount(1000)
                                    .refineCount(300)
                                    .build()))
                    .define('C', RecastingItems.IRON_MEDIUM_SOUL_CUBE.get())
                    .define('F', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.FRAGMENT.getId(), 1))
                    .unlockedBy("has_iron_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.IRON_MEDIUM_SOUL_CUBE.get()))
                    .save(consumer, recipeId);

    /**
     * 龙 Lambda 配方：从龙升级
     * 要求：杀敌2000、锻造500
     * 材料：金黄色的庸魂立方体4个
     * SE结晶：螺旋l2 4个
     * C=金黄色的庸魂立方体, B=基础刀（龙，满足要求）, S=螺旋l2 SE结晶
     */
    public static final RecipeBuilderWrapper DRAGON_LAMBDA_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.DRAGON_LAMBDA.getName())
                    .pattern("CSC")
                    .pattern("SBS")
                    .pattern("CSC")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.DRAGON.getName())
                                    .killCount(2000)
                                    .refineCount(500)
                                    .build()))
                    .define('C', RecastingItems.GOLD_MEDIUM_SOUL_CUBE.get())
                    .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SPIRAL.getId(), 1))
                    .unlockedBy("has_gold_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.GOLD_MEDIUM_SOUL_CUBE.get()))
                    .save(consumer, recipeId);

    /**
     * 法棍配方：基础配方，无前置刀
     * 材料：面包3个 + 耀魂1个
     * B=面包, P=耀魂
     */
    public static final RecipeBuilderWrapper DHARMA_STICK_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.DHARMA_STICK.getName())
                    .pattern("  B")
                    .pattern(" B ")
                    .pattern("BP ")
                    .define('B', Items.BREAD)
                    .define('P', SlashBladeItems.PROUDSOUL.get())
                    .unlockedBy("has_bread", RecipeProviderMixin.invokeHas(Items.BREAD))
                    .save(consumer, recipeId);

    /**
     * 锄头配方：基础配方，无前置刀
     * 材料：耀魂铁锭2个 + 木棍2个
     * H=耀魂铁锭, S=木棍
     */
    public static final RecipeBuilderWrapper HOE_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.HOE.getName())
                    .pattern(" HH")
                    .pattern(" S ")
                    .pattern(" S ")
                    .define('H', SlashBladeItems.PROUDSOUL_INGOT.get())
                    .define('S', Items.STICK)
                    .unlockedBy("has_stick", RecipeProviderMixin.invokeHas(Items.STICK))
                    .save(consumer, recipeId);

    /**
     * VOID_1 配方：从黑刃升级
     * 要求：杀敌3000、锻造500、耐久3附魔、力量5附魔、锋利5附魔、截肢杀手2附魔
     * SE结晶：震荡l3 2个
     * 材料：银白色庸魂立方体4个
     * C=银白色庸魂立方体, S=震荡l3 SE结晶, B=基础刀（黑刃，满足要求）
     */
    public static final RecipeBuilderWrapper VOID_1_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.VOID_1.getName())
                    .pattern(" SC")
                    .pattern("CBC")
                    .pattern("CS ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.BLACK.getName())
                                    .killCount(1500)
                                    .refineCount(100)
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.UNBREAKING), 3))
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SMITE), 5))
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 5))
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BANE_OF_ARTHROPODS), 2))
                                    .build()))
                    .define('C', RecastingItems.IRON_MEDIUM_SOUL_CUBE.get())
                    .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SHOCK.getId(), 1))
                    .unlockedBy("has_iron_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.IRON_MEDIUM_SOUL_CUBE.get()))
                    .save(consumer, recipeId);

    /**
     * VOID_2 配方：从 VOID_1 升级
     * 要求：杀敌3000、锻造250
     * SE结晶：生长l3 2个
     * 材料：漆黑庸魂立方体4个
     * C=漆黑庸魂立方体, G=生长l3 SE结晶, B=基础刀（VOID_1，满足要求）
     */
    public static final RecipeBuilderWrapper VOID_2_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.VOID_2.getName())
                    .pattern(" GC")
                    .pattern("CBC")
                    .pattern("CG ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.VOID_1.getName())
                                    .killCount(3000)
                                    .refineCount(250)
                                    .build()))
                    .define('C', RecastingItems.NETHERITE_MEDIUM_SOUL_CUBE.get())
                    .define('G', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.GROWTH.getId(), 1))
                    .unlockedBy("has_netherite_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.NETHERITE_MEDIUM_SOUL_CUBE.get()))
                    .save(consumer, recipeId);

    /**
     * VOID_3 配方：从 VOID_2 升级
     * 要求：杀敌5000、锻造500
     * SE结晶：吸血转化l3 2个
     * 材料：赤红庸魂立方体4个
     * C=赤红庸魂立方体, L=吸血转化l3 SE结晶, B=基础刀（VOID_2，满足要求）
     */
    public static final RecipeBuilderWrapper VOID_3_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.VOID_3.getName())
                    .pattern(" LC")
                    .pattern("CBC")
                    .pattern("CL ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.VOID_2.getName())
                                    .killCount(5000)
                                    .refineCount(500)
                                    .build()))
                    .define('C', RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE.get())
                    .define('L', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.LIFE_STEAL.getId(), 1))
                    .unlockedBy("has_redstone_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE.get()))
                    .save(consumer, recipeId);

    /**
     * OBLITERATE 配方：从黑刃升级
     * 要求：杀敌1000、荣耀50000、火焰附加2附魔
     * SE结晶：回溯l1 2个
     * 材料：赤红庸魂立方体4个
     * C=赤红庸魂立方体, R=回溯l1 SE结晶, B=基础刀（黑刃，满足要求）
     */
    public static final RecipeBuilderWrapper OBLITERATE_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.OBLITERATE.getName())
                    .pattern(" CR")
                    .pattern("CBC")
                    .pattern("RC ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.BLACK.getName())
                                    .killCount(1000)
                                    .proudSoul(50000)
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_ASPECT), 2))
                                    .build()))
                    .define('C', RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE.get())
                    .define('R', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.REGRESSION.getId(), 1))
                    .unlockedBy("has_redstone_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE.get()))
                    .save(consumer, recipeId);

    /**
     * OBLITERATE Lambda 配方：从 OBLITERATE 升级
     * 要求：杀敌2000、荣耀100000
     * SE结晶：回溯l2 2个
     * 材料：赤红庸魂立方体4个
     * C=赤红庸魂立方体, R=回溯l2 SE结晶, B=基础刀（OBLITERATE，满足要求）
     */
    public static final RecipeBuilderWrapper OBLITERATE_LAMBDA_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.OBLITERATE_LAMBDA.getName())
                    .pattern(" CR")
                    .pattern("CBC")
                    .pattern("RC ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.OBLITERATE.getName())
                                    .killCount(2000)
                                    .proudSoul(100000)
                                    .build()))
                    .define('C', RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE.get())
                    .define('R', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.REGRESSION.getId(), 1))
                    .unlockedBy("has_redstone_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE.get()))
                    .save(consumer, recipeId);

    /**
     * SOULBLADE 配方：从 OBLITERATE Lambda 升级
     * 要求：杀敌4000、荣耀200000
     * SE结晶：回溯l3 2个
     * 材料：赤红庸魂立方体4个
     * C=赤红庸魂立方体, R=回溯l3 SE结晶, B=基础刀（OBLITERATE Lambda，满足要求）
     */
    public static final RecipeBuilderWrapper SOULBLADE_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.SOULBLADE.getName())
                    .pattern(" CR")
                    .pattern("CBC")
                    .pattern("RC ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.OBLITERATE_LAMBDA.getName())
                                    .killCount(4000)
                                    .proudSoul(200000)
                                    .build()))
                    .define('C', RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE.get())
                    .define('R', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.REGRESSION.getId(), 1))
                    .unlockedBy("has_redstone_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE.get()))
                    .save(consumer, recipeId);

    /**
     * STAR_1 配方：从黑刃升级
     * 要求：杀敌1000、锻造200
     * 材料：银白色的庸魂立方体4个
     * SE结晶：撕裂l2 4个
     * C=银白色的庸魂立方体, B=基础刀（黑刃，满足要求）, T=撕裂l2 SE结晶
     */
    public static final RecipeBuilderWrapper STAR_1_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.STAR_1.getName())
                    .pattern("TCT")
                    .pattern("CBC")
                    .pattern("TCT")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.BLACK.getName())
                                    .killCount(1000)
                                    .refineCount(200)
                                    .build()))
                    .define('C', RecastingItems.IRON_MEDIUM_SOUL_CUBE.get())
                    .define('T', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.TEAR.getId(), 1))
                    .unlockedBy("has_iron_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.IRON_MEDIUM_SOUL_CUBE.get()))
                    .save(consumer, recipeId);

    /**
     * STAR_2 配方：从 STAR_1 升级
     * 要求：杀敌2000、锻造400
     * SE结晶：旋风l2 4个
     * W=旋风l2 SE结晶, B=基础刀（STAR_1，满足要求）
     */
    public static final RecipeBuilderWrapper STAR_2_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.STAR_2.getName())
                    .pattern(" W ")
                    .pattern("WBW")
                    .pattern(" W ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.STAR_1.getName())
                                    .killCount(2000)
                                    .refineCount(400)
                                    .build()))
                    .define('W', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.WHIRLWIND.getId(), 1))
                    .unlockedBy("has_whirlwind_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * STAR_3 配方：从 STAR_2 升级
     * 要求：杀敌4000、锻造600
     * SE结晶：断却l1 4个
     * A=断却l1 SE结晶, B=基础刀（STAR_2，满足要求）
     */
    public static final RecipeBuilderWrapper STAR_3_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.STAR_3.getName())
                    .pattern(" A ")
                    .pattern("ABA")
                    .pattern(" A ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.STAR_2.getName())
                                    .killCount(4000)
                                    .refineCount(600)
                                    .build()))
                    .define('A', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SEVER_BREAK.getId(), 1))
                    .unlockedBy("has_sever_break_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * STAR_4 配方：从 STAR_3 升级
     * 要求：杀敌6000、锻造800
     * SE结晶：断灭l1 2个
     * S=断灭l1 SE结晶, B=基础刀（STAR_3，满足要求）
     */
    public static final RecipeBuilderWrapper STAR_4_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.STAR_4.getName())
                    .pattern("  S")
                    .pattern(" B ")
                    .pattern("S  ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.STAR_3.getName())
                                    .killCount(6000)
                                    .refineCount(800)
                                    .build()))
                    .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.ANNIHILATION.getId(), 1))
                    .unlockedBy("has_annihilation_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * STAR_4_LAMBDA 配方：从 STAR_4 升级
     * 要求：杀敌12000、锻造1600
     * SE结晶：风暴l3 2个、风暴变体l3 2个
     * S=风暴l3 SE结晶, V=风暴变体l3 SE结晶, B=基础刀（STAR_4，满足要求）
     */
    public static final RecipeBuilderWrapper STAR_4_LAMBDA_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.STAR_4_LAMBDA.getName())
                    .pattern(" V ")
                    .pattern("SBS")
                    .pattern(" V ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.STAR_4.getName())
                                    .killCount(12000)
                                    .refineCount(1600)
                                    .build()))
                    .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.STORM.getId(), 1))
                    .define('V', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.STORM_VARIANT.getId(), 1))
                    .unlockedBy("has_storm_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                    .save(consumer, recipeId);

    /**
     * FLUORESCENCE_1 配方：从青锋（木）升级
     * 材料：青锋（木）1个（中间）+ 荧光墨囊1个（左上）
     * G=荧光墨囊, B=青锋（木）
     */
    public static final RecipeBuilderWrapper FLUORESCENCE_1_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.FLUORESCENCE_1.getName())
                    .pattern("G  ")
                    .pattern(" B ")
                    .pattern("   ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.GREEN_BLADE_WOOD.getName())
                                    .build()))
                    .define('G', Items.GLOW_INK_SAC)
                    .unlockedBy("has_green_blade_wood", RecipeProviderMixin.invokeHas(Items.GLOW_INK_SAC))
                    .save(consumer, recipeId);

    /**
     * FLUORESCENCE_2 配方：从青锋（木）升级
     * 材料：青锋（木）1个（中间）+ 荧光墨囊1个（上中）
     * G=荧光墨囊, B=青锋（木）
     */
    public static final RecipeBuilderWrapper FLUORESCENCE_2_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.FLUORESCENCE_2.getName())
                    .pattern(" G ")
                    .pattern(" B ")
                    .pattern("   ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.GREEN_BLADE_WOOD.getName())
                                    .build()))
                    .define('G', Items.GLOW_INK_SAC)
                    .unlockedBy("has_green_blade_wood", RecipeProviderMixin.invokeHas(Items.GLOW_INK_SAC))
                    .save(consumer, recipeId);

    /**
     * FLUORESCENCE_3 配方：从青锋（木）升级
     * 材料：青锋（木）1个（中间）+ 荧光墨囊1个（右上）
     * G=荧光墨囊, B=青锋（木）
     */
    public static final RecipeBuilderWrapper FLUORESCENCE_3_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.FLUORESCENCE_3.getName())
                    .pattern("  G")
                    .pattern(" B ")
                    .pattern("   ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.GREEN_BLADE_WOOD.getName())
                                    .build()))
                    .define('G', Items.GLOW_INK_SAC)
                    .unlockedBy("has_green_blade_wood", RecipeProviderMixin.invokeHas(Items.GLOW_INK_SAC))
                    .save(consumer, recipeId);

    /**
     * FLUORESCENCE_4 配方：从青锋（木）升级
     * 材料：青锋（木）1个（中间）+ 荧光墨囊1个（左中）
     * G=荧光墨囊, B=青锋（木）
     */
    public static final RecipeBuilderWrapper FLUORESCENCE_4_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.FLUORESCENCE_4.getName())
                    .pattern("   ")
                    .pattern("GB ")
                    .pattern("   ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.GREEN_BLADE_WOOD.getName())
                                    .build()))
                    .define('G', Items.GLOW_INK_SAC)
                    .unlockedBy("has_green_blade_wood", RecipeProviderMixin.invokeHas(Items.GLOW_INK_SAC))
                    .save(consumer, recipeId);

    /**
     * FLUORESCENCE_5 配方：从青锋（木）升级
     * 材料：青锋（木）1个（中间）+ 荧光墨囊1个（右中）
     * G=荧光墨囊, B=青锋（木）
     */
    public static final RecipeBuilderWrapper FLUORESCENCE_5_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.FLUORESCENCE_5.getName())
                    .pattern("   ")
                    .pattern(" BG")
                    .pattern("   ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.GREEN_BLADE_WOOD.getName())
                                    .build()))
                    .define('G', Items.GLOW_INK_SAC)
                    .unlockedBy("has_green_blade_wood", RecipeProviderMixin.invokeHas(Items.GLOW_INK_SAC))
                    .save(consumer, recipeId);

    /**
     * FLUORESCENCE_6 配方：从青锋（木）升级
     * 材料：青锋（木）1个（中间）+ 荧光墨囊1个（左下）
     * G=荧光墨囊, B=青锋（木）
     */
    public static final RecipeBuilderWrapper FLUORESCENCE_6_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.FLUORESCENCE_6.getName())
                    .pattern("   ")
                    .pattern(" B ")
                    .pattern("G  ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.GREEN_BLADE_WOOD.getName())
                                    .build()))
                    .define('G', Items.GLOW_INK_SAC)
                    .unlockedBy("has_green_blade_wood", RecipeProviderMixin.invokeHas(Items.GLOW_INK_SAC))
                    .save(consumer, recipeId);

    /**
     * FLUORESCENCE_7 配方：从青锋（木）升级
     * 材料：青锋（木）1个（中间）+ 荧光墨囊1个（下中）
     * G=荧光墨囊, B=青锋（木）
     */
    public static final RecipeBuilderWrapper FLUORESCENCE_7_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.FLUORESCENCE_7.getName())
                    .pattern("   ")
                    .pattern(" B ")
                    .pattern(" G ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.GREEN_BLADE_WOOD.getName())
                                    .build()))
                    .define('G', Items.GLOW_INK_SAC)
                    .unlockedBy("has_green_blade_wood", RecipeProviderMixin.invokeHas(Items.GLOW_INK_SAC))
                    .save(consumer, recipeId);

    /**
     * FLUORESCENCE_8 配方：从青锋（木）升级
     * 材料：青锋（木）1个（中间）+ 荧光墨囊1个（右下）
     * G=荧光墨囊, B=青锋（木）
     */
    public static final RecipeBuilderWrapper FLUORESCENCE_8_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.FLUORESCENCE_8.getName())
                    .pattern("   ")
                    .pattern(" B ")
                    .pattern("  G")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.GREEN_BLADE_WOOD.getName())
                                    .build()))
                    .define('G', Items.GLOW_INK_SAC)
                    .unlockedBy("has_green_blade_wood", RecipeProviderMixin.invokeHas(Items.GLOW_INK_SAC))
                    .save(consumer, recipeId);

}


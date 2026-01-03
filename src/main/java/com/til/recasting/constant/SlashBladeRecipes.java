package com.til.recasting.constant;

import com.til.recasting.generated.RecipeBuilderWrapper;
import com.til.recasting.mixin.RecipeProviderMixin;
import com.til.recasting.registry.RecastingItems;
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
 * <p>
 * 使用方式：
 * 使用 lambda 表达式定义 RecipeBuilderWrapper，配方ID会自动使用字段名转小写
 * <p>
 * 示例（刀合成）：
 * 字段名 BLACK_RECIPE -> 配方ID: recasting:black_recipe
 * public static final RecipeBuilderWrapper BLACK_RECIPE = (consumer, recipeId) ->
 * SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.BLACK.getName())
 * .pattern("  O")
 * .pattern(" B ")
 * .pattern("S  ")
 * .define('B', SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
 * .killCount(50)
 * .refineCount(10)
 * .addEnchantment(new EnchantmentDefinition(getEnchantmentID(Enchantments.UNBREAKING), 3))
 * .build()))
 * .define('O', Items.OBSIDIAN)
 * .define('S', RecastingItems.SIN_FLAME.get())
 * .unlockedBy("has_sin_flame", RecipeProviderMixin.invokeHas(RecastingItems.SIN_FLAME.get()))
 * .save(consumer, recipeId);
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
                    .pattern(" I ")
                    .pattern(" I ")
                    .pattern(" B ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.BROADSWORD_WOOD.getName())
                                    .killCount(51)
                                    .refineCount(6)
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
                    .pattern("  I")
                    .pattern(" I ")
                    .pattern("B  ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .name(SlashBladeDefinitions.GREEN_BLADE_WOOD.getName())
                                    .killCount(51)
                                    .refineCount(6)
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
     * 材料：诗烬火6个、白羊毛1个、黑羊毛1个
     * P=诗烬火, W=白羊毛, K=黑羊毛, S=基础刀（碎白，满足要求）
     */
    public static final RecipeBuilderWrapper BA_GUA_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.BA_GUA.getName())
                    .pattern("PPP")
                    .pattern("WSK")
                    .pattern("PPP")
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
                    .unlockedBy("has_poetry_ash_flame", RecipeProviderMixin.invokeHas(RecastingItems.POETRY_ASH_FLAME.get()))
                    .save(consumer, recipeId);

    public static final RecipeBuilderWrapper BLACK_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.BLACK.getName())
                    .pattern("  O")
                    .pattern(" B ")
                    .pattern("S  ")
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

}


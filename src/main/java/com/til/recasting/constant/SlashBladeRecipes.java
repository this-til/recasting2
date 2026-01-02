package com.til.recasting.constant;

import com.til.recasting.generated.RecipeBuilderWrapper;
import com.til.recasting.mixin.RecipeProviderMixin;
import com.til.recasting.registry.RecastingItems;
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
 * 
 * 使用方式：
 * 使用 lambda 表达式定义 RecipeBuilderWrapper，配方ID会自动使用字段名转小写
 * 
 * 示例（刀合成）：
 * 字段名 BLACK_RECIPE -> 配方ID: recasting:black_recipe
 * public static final RecipeBuilderWrapper BLACK_RECIPE = (consumer, recipeId) -> 
 *     SlashBladeShapedRecipeBuilder.shaped(SlashBladeDefinitions.BLACK.getName())
 *         .pattern("  O")
 *         .pattern(" B ")
 *         .pattern("S  ")
 *         .define('B', SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
 *             .killCount(50)
 *             .refineCount(10)
 *             .addEnchantment(new EnchantmentDefinition(getEnchantmentID(Enchantments.UNBREAKING), 3))
 *             .build()))
 *         .define('O', Items.OBSIDIAN)
 *         .define('S', RecastingItems.SIN_FLAME.get())
 *         .unlockedBy("has_sin_flame", RecipeProviderMixin.invokeHas(RecastingItems.SIN_FLAME.get()))
 *         .save(consumer, recipeId);
 */
public class SlashBladeRecipes {
    
    /**
     * BLACK 刀配方：从大太刀升级
     * 要求：50 杀敌数、10 锻造次数、耐久3附魔
     * 材料：黑曜石1个 + 罪孽火1个
     * 布局：
     *   O
     *  B 
     * S  
     * O=黑曜石, B=基础刀（大太刀，满足要求）, S=罪孽火
     */
    public static final RecipeBuilderWrapper BLACK_RECIPE = (consumer, recipeId) ->
            SlashBladeShapedRecipeBuilder.shaped(R.Slashblade.black)
                    .pattern("  O")
                    .pattern(" B ")
                    .pattern("S  ")
                    .define('B',
                            SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                    .killCount(50)
                                    .refineCount(10)
                                    .addEnchantment(new EnchantmentDefinition(
                                            ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.UNBREAKING), 3))
                                    .build()))
                    .define('O', Items.OBSIDIAN)
                    .define('S', RecastingItems.SIN_FLAME.get())
                    .unlockedBy("has_sin_flame", RecipeProviderMixin.invokeHas(RecastingItems.SIN_FLAME.get()))
                    .save(consumer, recipeId);

}


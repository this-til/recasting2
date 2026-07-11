package com.til.recasting.gametest.support;

import com.google.gson.JsonObject;
import mods.flammpfeil.slashblade.recipe.RequestDefinition;
import mods.flammpfeil.slashblade.recipe.SlashBladeIngredient;
import mods.flammpfeil.slashblade.registry.slashblade.EnchantmentDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * 配方诉求附魔等级不得超过该附魔 {@link Enchantment#getMaxLevel()}。
 */
public final class EnchantmentConstraintAssert {

    private EnchantmentConstraintAssert() {
    }

    public static List<String> collectRequestEnchantmentViolations(ResourceLocation recipeId, Recipe<?> recipe) {
        List<String> violations = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (!(ingredient instanceof SlashBladeIngredient slashBladeIngredient)) {
                continue;
            }
            RequestDefinition request = readRequest(slashBladeIngredient);
            for (EnchantmentDefinition definition : request.enchantments()) {
                ResourceLocation enchantmentId = definition.getEnchantmentID();
                int requestedLevel = definition.getEnchantmentLevel();
                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(enchantmentId);
                if (enchantment == null) {
                    violations.add(recipeId + ": unknown enchantment " + enchantmentId
                            + " at requested level " + requestedLevel);
                    continue;
                }
                int maxLevel = enchantment.getMaxLevel();
                if (requestedLevel > maxLevel) {
                    violations.add(recipeId + ": " + enchantmentId
                            + " requested level " + requestedLevel
                            + " exceeds max level " + maxLevel);
                }
            }
        }
        return violations;
    }

    private static RequestDefinition readRequest(SlashBladeIngredient ingredient) {
        JsonObject json = ingredient.toJson().getAsJsonObject();
        return RequestDefinition.fromJSON(json.getAsJsonObject("request"));
    }
}

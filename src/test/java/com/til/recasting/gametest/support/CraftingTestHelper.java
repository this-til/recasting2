package com.til.recasting.gametest.support;

import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.MathHelper;
import com.til.recasting.recipe.SpecialEffectCrystalShapedRecipe;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.recipe.SlashBladeShapedRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * 合成网格搭建与结果断言。
 */
public final class CraftingTestHelper {

    private CraftingTestHelper() {
    }

    public static CraftingContainer newEmptyGrid() {
        return new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
            @Override
            public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean stillValid(@NotNull Player player) {
                return false;
            }
        }, 3, 3);
    }

    public static ItemStack assembleMatching(Level level, CraftingRecipe recipe) {
        if (!(recipe instanceof ShapedRecipe shaped)) {
            throw new IllegalArgumentException("Expected ShapedRecipe: " + recipe.getId());
        }
        CraftingContainer grid = newEmptyGrid();
        IngredientStackFactory.fillMatching(grid, shaped);
        if (!recipe.matches(grid, level)) {
            throw new IllegalStateException("Recipe did not match with synthesized ingredients: " + recipe.getId());
        }
        return recipe.assemble(grid, level.registryAccess());
    }

    public static void assertResultMatchesExpected(Recipe<?> recipe, ItemStack actual, RegistryAccess access) {
        ItemStack expected = recipe.getResultItem(access).copy();
        if (recipe instanceof SpecialEffectCrystalShapedRecipe seRecipe) {
            assertSeCrystalResult(seRecipe, actual);
            return;
        }
        if (recipe instanceof SlashBladeShapedRecipe) {
            if (!(actual.getItem() instanceof ItemSlashBlade)) {
                throw new AssertionError("Blade recipe result is not a slash blade: " + recipe.getId());
            }
            return;
        }
        if (!ItemStack.isSameItemSameTags(expected, actual) && !ItemStack.isSameItem(expected, actual)) {
            if (expected.getItem() != actual.getItem() || expected.getCount() != actual.getCount()) {
                throw new AssertionError("Result mismatch for " + recipe.getId()
                        + " expected=" + expected + " actual=" + actual);
            }
        }
    }

    private static void assertSeCrystalResult(SpecialEffectCrystalShapedRecipe recipe, ItemStack actual) {
        IngredientStackFactory.restoreSeCrystalCapability(actual);
        ResourceLocation expectedType = recipe.getSpecialEffectType();
        int expectedLevel = recipe.getLevel();
        var data = actual.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA)
                .orElseThrow(() -> new AssertionError("SE crystal missing capability: " + recipe.getId()));
        ResourceLocation actualType = data.getSpecialEffectType();
        int actualLevel = data.getSpecialEffectLevel();
        if (expectedType != null && !expectedType.equals(actualType)) {
            throw new AssertionError("SE type mismatch for " + recipe.getId()
                    + " expected=" + expectedType + " actual=" + actualType);
        }
        if (expectedLevel >= 0 && expectedLevel != actualLevel) {
            throw new AssertionError("SE level mismatch for " + recipe.getId()
                    + " expected=" + expectedLevel + " actual=" + actualLevel);
        }
    }

    public static void assertFloatEquals(String label, float expected, float actual) {
        if (!MathHelper.epsilonEquals((double) expected, (double) actual)) {
            throw new AssertionError(label + " expected=" + expected + " actual=" + actual);
        }
    }
}

package com.til.recasting.gametest;

import com.til.recasting.Recasting;
import com.til.recasting.gametest.support.BladeResultAssert;
import com.til.recasting.gametest.support.CraftingTestHelper;
import com.til.recasting.gametest.support.EnchantmentConstraintAssert;
import com.til.recasting.gametest.support.IngredientStackFactory;
import com.til.recasting.gametest.support.RecipeIdCatalog;
import mods.flammpfeil.slashblade.recipe.SlashBladeShapedRecipe;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.Rotation;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 全量 recasting 配方正/负向合成，以及刀成品与定义一致性。
 */
@GameTestHolder(Recasting.MODID)
@PrefixGameTestTemplate(false)
public final class RecipeGameTests {

    private static final String STRUCTURE = Recasting.MODID + ":empty";
    private static final String BATCH = "recastingRecipes";
    private static final int TIMEOUT_TICKS = 100;

    private RecipeGameTests() {
    }

    @GameTestGenerator
    public static Collection<TestFunction> generateRecipeTests() {
        List<TestFunction> tests = new ArrayList<>();
        List<ResourceLocation> ids = RecipeIdCatalog.allExpectedIds();
        tests.add(new TestFunction(
                BATCH,
                "recipe_catalog_loaded",
                STRUCTURE,
                Rotation.NONE,
                TIMEOUT_TICKS,
                0L,
                true,
                RecipeGameTests::assertCatalogLoaded
        ));
        tests.add(new TestFunction(
                BATCH,
                "recipe_request_enchantments_within_max_level",
                STRUCTURE,
                Rotation.NONE,
                TIMEOUT_TICKS,
                0L,
                true,
                RecipeGameTests::assertRequestEnchantmentsWithinMaxLevel
        ));
        for (ResourceLocation id : ids) {
            String safe = sanitize(id.getPath());
            tests.add(new TestFunction(
                    BATCH,
                    "recipe_pos_" + safe,
                    STRUCTURE,
                    Rotation.NONE,
                    TIMEOUT_TICKS,
                    0L,
                    true,
                    helper -> runPositive(helper, id)
            ));
            tests.add(new TestFunction(
                    BATCH,
                    "recipe_neg_" + safe,
                    STRUCTURE,
                    Rotation.NONE,
                    TIMEOUT_TICKS,
                    0L,
                    true,
                    helper -> runNegative(helper, id)
            ));
            tests.add(new TestFunction(
                    BATCH,
                    "recipe_blade_def_" + safe,
                    STRUCTURE,
                    Rotation.NONE,
                    TIMEOUT_TICKS,
                    0L,
                    true,
                    helper -> runBladeDefinitionIfApplicable(helper, id)
            ));
        }
        return tests;
    }

    private static void assertCatalogLoaded(GameTestHelper helper) {
        List<ResourceLocation> expected = RecipeIdCatalog.allExpectedIds();
        if (expected.isEmpty()) {
            helper.fail("Expected recipe catalog is empty");
            return;
        }
        for (ResourceLocation id : expected) {
            Optional<? extends Recipe<?>> holder = helper.getLevel().getRecipeManager().byKey(id);
            if (holder.isEmpty()) {
                helper.fail("Missing loaded recipe: " + id);
                return;
            }
        }
        helper.succeed();
    }

    /**
     * 配方中刀材料 {@code RequestDefinition} 的附魔诉求等级不得超过该附魔最大可获取等级。
     */
    private static void assertRequestEnchantmentsWithinMaxLevel(GameTestHelper helper) {
        List<String> violations = new ArrayList<>();
        for (ResourceLocation id : RecipeIdCatalog.allExpectedIds()) {
            Recipe<?> recipe = requireRecipe(helper, id);
            violations.addAll(EnchantmentConstraintAssert.collectRequestEnchantmentViolations(id, recipe));
        }
        if (!violations.isEmpty()) {
            helper.fail("Recipe request enchantment level exceeds max:\n" + String.join("\n", violations));
            return;
        }
        helper.succeed();
    }

    private static void runPositive(GameTestHelper helper, ResourceLocation id) {
        Recipe<?> recipe = requireRecipe(helper, id);
        if (!(recipe instanceof CraftingRecipe craftingRecipe)) {
            helper.fail("Not a crafting recipe: " + id);
            return;
        }
        if (!(recipe instanceof ShapedRecipe shaped)) {
            helper.fail("Not a shaped recipe: " + id);
            return;
        }
        CraftingContainer grid = CraftingTestHelper.newEmptyGrid();
        IngredientStackFactory.fillMatching(grid, shaped);
        if (!craftingRecipe.matches(grid, helper.getLevel())) {
            helper.fail("Expected match for " + id);
            return;
        }
        ItemStack result = craftingRecipe.assemble(grid, helper.getLevel().registryAccess());
        try {
            CraftingTestHelper.assertResultMatchesExpected(recipe, result, helper.getLevel().registryAccess());
        } catch (AssertionError error) {
            helper.fail(error.getMessage());
            return;
        }
        helper.succeed();
    }

    private static void runNegative(GameTestHelper helper, ResourceLocation id) {
        Recipe<?> recipe = requireRecipe(helper, id);
        if (!(recipe instanceof CraftingRecipe craftingRecipe)) {
            helper.fail("Not a crafting recipe: " + id);
            return;
        }
        if (!(recipe instanceof ShapedRecipe shaped)) {
            helper.fail("Not a shaped recipe: " + id);
            return;
        }
        CraftingContainer grid = CraftingTestHelper.newEmptyGrid();
        IngredientStackFactory.fillMatching(grid, shaped);
        IngredientStackFactory.breakOneConstraint(grid, recipe);
        if (craftingRecipe.matches(grid, helper.getLevel())) {
            helper.fail("Expected NOT match for " + id);
            return;
        }
        helper.succeed();
    }

    private static void runBladeDefinitionIfApplicable(GameTestHelper helper, ResourceLocation id) {
        Recipe<?> recipe = requireRecipe(helper, id);
        if (!(recipe instanceof SlashBladeShapedRecipe bladeRecipe)) {
            helper.succeed();
            return;
        }
        try {
            BladeResultAssert.assertMatchesDefinition(helper.getLevel(), bladeRecipe);
        } catch (RuntimeException | AssertionError error) {
            helper.fail(error.getMessage() == null ? String.valueOf(error) : error.getMessage());
            return;
        }
        helper.succeed();
    }

    private static Recipe<?> requireRecipe(GameTestHelper helper, ResourceLocation id) {
        return helper.getLevel().getRecipeManager().byKey(id)
                .orElseThrow(() -> new IllegalStateException("Missing recipe: " + id));
    }

    private static String sanitize(String path) {
        return path.replace('/', '_').replace(':', '_');
    }
}

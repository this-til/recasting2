package com.til.recasting.gametest;

import com.til.recasting.Recasting;
import com.til.recasting.constant.SlashBladeDefinitions;
import com.til.recasting.gametest.support.CraftingTestHelper;
import com.til.recasting.gametest.support.TestItemFactory;
import com.til.recasting.registry.SpecialEffectsRegistry;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * 拔刀剑合成后特殊/普通 SE 继承与替换用例。
 */
@GameTestHolder(Recasting.MODID)
@PrefixGameTestTemplate(false)
public final class BladeSeInheritanceGameTests {

    private static final ResourceLocation SHINE_TEA_LAMBDA_RECIPE = Recasting.prefix("shine_tea_lambda_recipe");
    private static final ResourceLocation DRAGON_SCALE_LAMBDA_RECIPE = Recasting.prefix("dragon_scale_lambda_recipe");

    private BladeSeInheritanceGameTests() {
    }

    @GameTest(template = "empty", batch = "recastingBladeSeInheritance")
    public static void specialSe_inheritWhenOutputHasNoDefinitionSpecialSe(GameTestHelper helper) {
        Recipe<?> recipe = requireRecipe(helper, DRAGON_SCALE_LAMBDA_RECIPE);
        ItemStack input = TestItemFactory.bladeWithRequirements(
                helper.getLevel().registryAccess(),
                SlashBladeDefinitions.DRAGON_SCALE.getName(),
                500,
                200,
                SpecialEffectsRegistry.BLACK_ROSE.getId(),
                1,
                Enchantments.SMITE,
                5
        );

        try {
            ItemStack output = CraftingTestHelper.assembleMatchingWithBladeOverride(
                    helper.getLevel(),
                    (CraftingRecipe) recipe,
                    input
            );
            CraftingTestHelper.assertHasSpecialEffect(output, SpecialEffectsRegistry.BLACK_ROSE.getId(), 1);
        } catch (RuntimeException | AssertionError error) {
            helper.fail(error.getMessage() == null ? String.valueOf(error) : error.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "recastingBladeSeInheritance")
    public static void specialSe_replaceWhenOutputHasDefinitionSpecialSe(GameTestHelper helper) {
        Recipe<?> recipe = requireRecipe(helper, SHINE_TEA_LAMBDA_RECIPE);
        ItemStack input = TestItemFactory.bladeWithStats(
                helper.getLevel().registryAccess(),
                SlashBladeDefinitions.SHINE_TEA.getName(),
                2000,
                1000
        );

        try {
            ItemStack output = CraftingTestHelper.assembleMatchingWithBladeOverride(
                    helper.getLevel(),
                    (CraftingRecipe) recipe,
                    input
            );
            CraftingTestHelper.assertHasSpecialEffect(output, SpecialEffectsRegistry.TEA_AROMA_LAMBDA.getId(), 1);
            CraftingTestHelper.assertMissingSpecialEffect(output, SpecialEffectsRegistry.TEA_AROMA.getId());
        } catch (RuntimeException | AssertionError error) {
            helper.fail(error.getMessage() == null ? String.valueOf(error) : error.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "recastingBladeSeInheritance")
    public static void normalSe_inheritWhenOutputHasNoDefinitionSe(GameTestHelper helper) {
        Recipe<?> recipe = requireRecipe(helper, DRAGON_SCALE_LAMBDA_RECIPE);
        ItemStack input = TestItemFactory.bladeWithRequirements(
                helper.getLevel().registryAccess(),
                SlashBladeDefinitions.DRAGON_SCALE.getName(),
                500,
                200,
                SpecialEffectsRegistry.SHARP_BLADE.getId(),
                1,
                Enchantments.SMITE,
                5
        );

        try {
            ItemStack output = CraftingTestHelper.assembleMatchingWithBladeOverride(
                    helper.getLevel(),
                    (CraftingRecipe) recipe,
                    input
            );
            CraftingTestHelper.assertHasSpecialEffect(output, SpecialEffectsRegistry.SHARP_BLADE.getId(), 1);
        } catch (RuntimeException | AssertionError error) {
            helper.fail(error.getMessage() == null ? String.valueOf(error) : error.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "recastingBladeSeInheritance")
    public static void normalSe_higherLevelOverrides(GameTestHelper helper) {
        Recipe<?> recipe = requireRecipe(helper, DRAGON_SCALE_LAMBDA_RECIPE);
        ItemStack input = TestItemFactory.bladeWithRequirements(
                helper.getLevel().registryAccess(),
                SlashBladeDefinitions.DRAGON_SCALE.getName(),
                500,
                200,
                SpecialEffectsRegistry.SHARP_BLADE.getId(),
                3,
                Enchantments.SMITE,
                5
        );

        try {
            ItemStack output = CraftingTestHelper.assembleMatchingWithBladeOverride(
                    helper.getLevel(),
                    (CraftingRecipe) recipe,
                    input
            );
            CraftingTestHelper.assertHasSpecialEffect(output, SpecialEffectsRegistry.SHARP_BLADE.getId(), 3);
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
}

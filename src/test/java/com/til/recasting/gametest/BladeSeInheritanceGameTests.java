package com.til.recasting.gametest;

import com.til.recasting.Recasting;
import com.til.recasting.constant.RecastingSlashBladeKeys;
import com.til.recasting.gametest.support.CraftingTestHelper;
import com.til.recasting.gametest.support.TestItemFactory;
import com.til.recasting.registry.SpecialEffectsRegistry;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.Map;

/**
 * 拔刀剑合成后特殊/普通 SE 继承与替换用例。
 */
@GameTestHolder(Recasting.MODID)
@PrefixGameTestTemplate(false)
public final class BladeSeInheritanceGameTests {

    private static final ResourceLocation DRAGON_SCALE_LAMBDA_RECIPE = Recasting.prefix("dragon_scale_lambda_recipe");
    private static final ResourceLocation BRILLIANT_TEA_LAMBDA_RECIPE = Recasting.prefix("brilliant_tea_lambda_recipe");
    private static final Map<Enchantment, Integer> DRAGON_SCALE_LAMBDA_BLADE_ENCHANTMENTS = Map.of(
            Enchantments.FIRE_PROTECTION, 4,
            Enchantments.FIRE_ASPECT, 2,
            Enchantments.FLAMING_ARROWS, 1
    );
    private static final Map<Enchantment, Integer> BRILLIANT_TEA_LAMBDA_BLADE_ENCHANTMENTS = Map.ofEntries(
            Map.entry(Enchantments.FISHING_LUCK, 3),
            Map.entry(Enchantments.FISHING_SPEED, 3),
            Map.entry(Enchantments.SILK_TOUCH, 1),
            Map.entry(Enchantments.BLOCK_EFFICIENCY, 5),
            Map.entry(Enchantments.BLOCK_FORTUNE, 3),
            Map.entry(Enchantments.LOYALTY, 3),
            Map.entry(Enchantments.RIPTIDE, 1),
            Map.entry(Enchantments.MENDING, 1)
    );

    private BladeSeInheritanceGameTests() {
    }

    @GameTest(template = "empty", batch = "recastingBladeSeInheritance")
    public static void specialSe_inheritWhenOutputHasNoDefinitionSpecialSe(GameTestHelper helper) {
        Recipe<?> recipe = requireRecipe(helper, DRAGON_SCALE_LAMBDA_RECIPE);
        ItemStack input = TestItemFactory.bladeWithRequirements(
                helper.getLevel().registryAccess(),
                RecastingSlashBladeKeys.DRAGON_SCALE.location(),
                500,
                200,
                SpecialEffectsRegistry.BLACK_ROSE.getId(),
                1,
                DRAGON_SCALE_LAMBDA_BLADE_ENCHANTMENTS
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
        Recipe<?> recipe = requireRecipe(helper, BRILLIANT_TEA_LAMBDA_RECIPE);
        ItemStack input = TestItemFactory.bladeWithStatsAndEnchantments(
                helper.getLevel().registryAccess(),
                RecastingSlashBladeKeys.BRILLIANT_TEA.location(),
                8000,
                4000,
                BRILLIANT_TEA_LAMBDA_BLADE_ENCHANTMENTS
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
                RecastingSlashBladeKeys.DRAGON_SCALE.location(),
                500,
                200,
                SpecialEffectsRegistry.SHARP_BLADE.getId(),
                1,
                DRAGON_SCALE_LAMBDA_BLADE_ENCHANTMENTS
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
                RecastingSlashBladeKeys.DRAGON_SCALE.location(),
                500,
                200,
                SpecialEffectsRegistry.SHARP_BLADE.getId(),
                3,
                DRAGON_SCALE_LAMBDA_BLADE_ENCHANTMENTS
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

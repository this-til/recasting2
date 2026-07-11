package com.til.recasting.gametest.support;

import com.til.recasting.handler.MathHelper;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.recipe.SlashBladeShapedRecipe;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 校验刀配方产出与 {@link SlashBladeDefinition} 一致。
 */
public final class BladeResultAssert {

    private BladeResultAssert() {
    }

    public static void assertMatchesDefinition(Level level, CraftingRecipe recipe) {
        if (!(recipe instanceof SlashBladeShapedRecipe bladeRecipe)) {
            throw new AssertionError("Not a blade recipe: " + recipe.getId());
        }
        ItemStack actual = CraftingTestHelper.assembleMatching(level, recipe);
        assertMatchesDefinition(level.registryAccess(), bladeRecipe.getOutputBlade(), actual);
    }

    public static void assertMatchesDefinition(RegistryAccess access, ResourceLocation bladeId, ItemStack actual) {
        if (!(actual.getItem() instanceof ItemSlashBlade)) {
            throw new AssertionError("Result is not a slash blade for " + bladeId);
        }
        SlashBladeDefinition definition = access.registryOrThrow(SlashBladeDefinition.REGISTRY_KEY)
                .getOrThrow(ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, bladeId));
        ItemStack expected = definition.getBlade();

        var actualState = actual.getCapability(ItemSlashBlade.BLADESTATE)
                .orElseThrow(() -> new AssertionError("Missing BLADESTATE on result: " + bladeId));
        var expectedState = expected.getCapability(ItemSlashBlade.BLADESTATE)
                .orElseThrow(() -> new AssertionError("Missing BLADESTATE on definition blade: " + bladeId));

        if (!expectedState.getTranslationKey().equals(actualState.getTranslationKey())) {
            throw new AssertionError("Blade name mismatch for " + bladeId
                    + " expected=" + expectedState.getTranslationKey()
                    + " actual=" + actualState.getTranslationKey());
        }
        ResourceLocation expectedSa = expectedState.getSlashArtsKey();
        ResourceLocation actualSa = actualState.getSlashArtsKey();
        if (expectedSa == null ? actualSa != null : !expectedSa.equals(actualSa)) {
            throw new AssertionError("SlashArts mismatch for " + bladeId
                    + " expected=" + expectedSa + " actual=" + actualSa);
        }
        if (!MathHelper.epsilonEquals(
                (double) expectedState.getBaseAttackModifier(),
                (double) actualState.getBaseAttackModifier()
        )) {
            throw new AssertionError("Attack mismatch for " + bladeId
                    + " expected=" + expectedState.getBaseAttackModifier()
                    + " actual=" + actualState.getBaseAttackModifier());
        }

        List<ResourceLocation> expectedEffects = definition.getStateDefinition().getSpecialEffects();
        Set<ResourceLocation> actualEffects = new HashSet<>(actualState.getSpecialEffects());
        for (ResourceLocation se : expectedEffects) {
            if (!actualEffects.contains(se)) {
                throw new AssertionError("Missing SE " + se + " on crafted blade " + bladeId);
            }
        }
    }
}

package com.til.recasting.gametest;

import com.til.recasting.Recasting;
import com.til.recasting.constant.SlashBladeDefinitions;
import com.til.recasting.gametest.support.AnvilTestHelper;
import com.til.recasting.gametest.support.TestItemFactory;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.SpecialEffectsRegistry;
import com.til.recasting.registry.requir.SlashBladeItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * 铁砧 SE 铭刻、渊寂火去除/提取特殊 SE、SA 提取的正/负向用例。
 */
@GameTestHolder(Recasting.MODID)
@PrefixGameTestTemplate(false)
public final class AnvilGameTests {

    private AnvilGameTests() {
    }

    @GameTest(template = "empty", batch = "recastingAnvil")
    public static void engraveSe_upgrade(GameTestHelper helper) {
        ResourceLocation seId = SpecialEffectsRegistry.SHARP_BLADE.getId();
        ItemStack blade = TestItemFactory.bladeFromDefinition(
                helper.getLevel().registryAccess(),
                SlashBladeDefinitions.BROADSWORD_WOOD.getName()
        );
        ItemStack crystal = TestItemFactory.seCrystal(seId, 1);
        ItemStack output = AnvilTestHelper.preview(blade, crystal, helper.makeMockPlayer());
        try {
            AnvilTestHelper.assertHasOutput(output, "engraveSe_upgrade");
            boolean hasSe = output.getCapability(ItemSlashBlade.BLADESTATE)
                    .map(state -> state.hasSpecialEffect(seId))
                    .orElse(false);
            if (!hasSe) {
                helper.fail("Engraved blade missing SE " + seId);
                return;
            }
            int level = output.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                    .map(ext -> ext.getExtendedSpecialLevels(seId))
                    .orElse(0);
            if (level != 1) {
                helper.fail("Engraved SE level expected 1 but was " + level);
                return;
            }
        } catch (AssertionError error) {
            helper.fail(error.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "recastingAnvil")
    public static void engraveSe_levelTooLow_noOutput(GameTestHelper helper) {
        ResourceLocation seId = SpecialEffectsRegistry.SHARP_BLADE.getId();
        ItemStack blade = TestItemFactory.bladeWithSpecialEffect(
                helper.getLevel().registryAccess(),
                SlashBladeDefinitions.BROADSWORD_WOOD.getName(),
                seId,
                2
        );
        ItemStack crystal = TestItemFactory.seCrystal(seId, 1);
        ItemStack output = AnvilTestHelper.preview(blade, crystal, helper.makeMockPlayer());
        try {
            AnvilTestHelper.assertNoOutput(output, "engraveSe_levelTooLow_noOutput");
        } catch (AssertionError error) {
            helper.fail(error.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "recastingAnvil")
    public static void engraveSe_erase(GameTestHelper helper) {
        ResourceLocation seId = SpecialEffectsRegistry.SHARP_BLADE.getId();
        ItemStack blade = TestItemFactory.bladeWithSpecialEffect(
                helper.getLevel().registryAccess(),
                SlashBladeDefinitions.BROADSWORD_WOOD.getName(),
                seId,
                1
        );
        ItemStack crystal = TestItemFactory.seCrystal(seId, 0);
        ItemStack output = AnvilTestHelper.preview(blade, crystal, helper.makeMockPlayer());
        try {
            AnvilTestHelper.assertHasOutput(output, "engraveSe_erase");
            boolean stillHas = output.getCapability(ItemSlashBlade.BLADESTATE)
                    .map(state -> state.hasSpecialEffect(seId))
                    .orElse(true);
            if (stillHas) {
                helper.fail("SE should be erased: " + seId);
                return;
            }
        } catch (AssertionError error) {
            helper.fail(error.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "recastingAnvil")
    public static void removeSpecialSe_bladeThenAbyssFlame(GameTestHelper helper) {
        ResourceLocation seId = SpecialEffectsRegistry.BLACK_ROSE.getId();
        ItemStack blade = TestItemFactory.bladeWithSpecialEffect(
                helper.getLevel().registryAccess(),
                SlashBladeDefinitions.UMBRELLA.getName(),
                seId,
                1
        );
        ItemStack abyss = new ItemStack(RecastingItems.ABYSS_FLAME.get());
        ItemStack output = AnvilTestHelper.preview(blade, abyss, helper.makeMockPlayer());
        try {
            AnvilTestHelper.assertHasOutput(output, "removeSpecialSe_bladeThenAbyssFlame");
            if (!(output.getItem() instanceof ItemSlashBlade)) {
                helper.fail("Expected blade output after remove");
                return;
            }
            boolean stillHas = output.getCapability(ItemSlashBlade.BLADESTATE)
                    .map(state -> state.hasSpecialEffect(seId))
                    .orElse(true);
            if (stillHas) {
                helper.fail("Special SE should be removed: " + seId);
                return;
            }
        } catch (AssertionError error) {
            helper.fail(error.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "recastingAnvil")
    public static void extractSpecialSe_abyssFlameThenBlade(GameTestHelper helper) {
        ResourceLocation seId = SpecialEffectsRegistry.BLACK_ROSE.getId();
        ItemStack blade = TestItemFactory.bladeWithSpecialEffect(
                helper.getLevel().registryAccess(),
                SlashBladeDefinitions.UMBRELLA.getName(),
                seId,
                1
        );
        ItemStack abyss = new ItemStack(RecastingItems.ABYSS_FLAME.get());
        ItemStack output = AnvilTestHelper.preview(abyss, blade, helper.makeMockPlayer());
        try {
            AnvilTestHelper.assertHasOutput(output, "extractSpecialSe_abyssFlameThenBlade");
            if (!output.is(RecastingItems.SE_CRYSTAL.get())) {
                helper.fail("Expected SE crystal output");
                return;
            }
            boolean ok = output.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA)
                    .map(data -> seId.equals(data.getSpecialEffectType()) && data.getSpecialEffectLevel() == 1)
                    .orElse(false);
            if (!ok) {
                helper.fail("Extracted crystal SE mismatch");
                return;
            }
        } catch (AssertionError error) {
            helper.fail(error.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "recastingAnvil")
    public static void abyssFlame_withoutSpecial_noOutput(GameTestHelper helper) {
        ItemStack blade = TestItemFactory.bladeFromDefinition(
                helper.getLevel().registryAccess(),
                SlashBladeDefinitions.BROADSWORD_WOOD.getName()
        );
        ItemStack abyss = new ItemStack(RecastingItems.ABYSS_FLAME.get());
        ItemStack removeOutput = AnvilTestHelper.preview(blade, abyss, helper.makeMockPlayer());
        ItemStack extractOutput = AnvilTestHelper.preview(abyss, blade, helper.makeMockPlayer());
        try {
            AnvilTestHelper.assertNoOutput(removeOutput, "abyssFlame_withoutSpecial_remove");
            AnvilTestHelper.assertNoOutput(extractOutput, "abyssFlame_withoutSpecial_extract");
        } catch (AssertionError error) {
            helper.fail(error.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "recastingAnvil")
    public static void extractSa_toProudSoulSphere(GameTestHelper helper) {
        ItemStack sphere = new ItemStack(SlashBladeItems.PROUDSOUL_SPHERE.get());
        ItemStack blade = TestItemFactory.bladeFromDefinition(
                helper.getLevel().registryAccess(),
                SlashBladeDefinitions.BROADSWORD_WOOD.getName()
        );
        ItemStack output = AnvilTestHelper.preview(sphere, blade, helper.makeMockPlayer());
        try {
            AnvilTestHelper.assertHasOutput(output, "extractSa_toProudSoulSphere");
            CompoundTag tag = output.getTag();
            if (tag == null || !tag.contains("SpecialAttackType")) {
                helper.fail("Proud soul sphere missing SpecialAttackType");
                return;
            }
        } catch (AssertionError error) {
            helper.fail(error.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "recastingAnvil")
    public static void extractSa_noArts_noOutput(GameTestHelper helper) {
        ItemStack sphere = new ItemStack(SlashBladeItems.PROUDSOUL_SPHERE.get());
        ItemStack blade = TestItemFactory.bladeWithoutSlashArts(
                helper.getLevel().registryAccess(),
                SlashBladeDefinitions.BROADSWORD_WOOD.getName()
        );
        ItemStack output = AnvilTestHelper.preview(sphere, blade, helper.makeMockPlayer());
        try {
            AnvilTestHelper.assertNoOutput(output, "extractSa_noArts_noOutput");
        } catch (AssertionError error) {
            helper.fail(error.getMessage());
            return;
        }
        helper.succeed();
    }
}

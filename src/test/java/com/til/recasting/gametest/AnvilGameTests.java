package com.til.recasting.gametest;

import com.til.recasting.Recasting;
import com.til.recasting.constant.RecastingSlashBladeKeys;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.List;

/**
 * 铁砧 SE 铭刻、渊寂火去除特殊 SE、聚散变体提取特殊 SE、SA 提取的正/负向用例。
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
        RecastingSlashBladeKeys.BROADSWORD_WOOD.location()
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
        RecastingSlashBladeKeys.BROADSWORD_WOOD.location(),
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
        RecastingSlashBladeKeys.BROADSWORD_WOOD.location(),
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
        RecastingSlashBladeKeys.UMBRELLA.location(),
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
    public static void extractSpecialSe_bladeThenGatheringPartingVariant(GameTestHelper helper) {
        ResourceLocation seId = SpecialEffectsRegistry.BLACK_ROSE.getId();
        ItemStack blade = TestItemFactory.bladeWithSpecialEffect(
                helper.getLevel().registryAccess(),
        RecastingSlashBladeKeys.UMBRELLA.location(),
                seId,
                1
        );
        ItemStack variant = new ItemStack(RecastingItems.GATHERING_PARTING_VARIANT.get());
        ItemStack output = AnvilTestHelper.preview(blade, variant, helper.makeMockPlayer());
        try {
            AnvilTestHelper.assertHasOutput(output, "extractSpecialSe_bladeThenGatheringPartingVariant");
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
    public static void extractOrRemoveSpecialSe_withoutSpecial_noOutput(GameTestHelper helper) {
        ItemStack blade = TestItemFactory.bladeFromDefinition(
                helper.getLevel().registryAccess(),
        RecastingSlashBladeKeys.BROADSWORD_WOOD.location()
        );
        ItemStack abyss = new ItemStack(RecastingItems.ABYSS_FLAME.get());
        ItemStack variant = new ItemStack(RecastingItems.GATHERING_PARTING_VARIANT.get());
        ItemStack removeOutput = AnvilTestHelper.preview(blade, abyss, helper.makeMockPlayer());
        ItemStack extractOutput = AnvilTestHelper.preview(blade, variant, helper.makeMockPlayer());
        try {
            AnvilTestHelper.assertNoOutput(removeOutput, "abyssFlame_withoutSpecial_remove");
            AnvilTestHelper.assertNoOutput(extractOutput, "gatheringPartingVariant_withoutSpecial_extract");
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
        RecastingSlashBladeKeys.BROADSWORD_WOOD.location()
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
    public static void engraveSe_upgradeNoDuplicate(GameTestHelper helper) {
        ResourceLocation seId = SpecialEffectsRegistry.SHARP_BLADE.getId();
        ItemStack blade = TestItemFactory.bladeWithSpecialEffect(
                helper.getLevel().registryAccess(),
                RecastingSlashBladeKeys.BROADSWORD_WOOD.location(),
                seId,
                1
        );
        ItemStack crystal = TestItemFactory.seCrystal(seId, 5);
        ItemStack output = AnvilTestHelper.preview(blade, crystal, helper.makeMockPlayer());
        try {
            AnvilTestHelper.assertHasOutput(output, "engraveSe_upgradeNoDuplicate");
            List<ResourceLocation> seList = output.getCapability(ItemSlashBlade.BLADESTATE)
                    .map(state -> state.getSpecialEffects().stream().filter(seId::equals).toList())
                    .orElse(List.of());
            if (seList.size() != 1) {
                helper.fail("SE duplicated after upgrade: count=" + seList.size());
                return;
            }
            int level = output.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                    .map(ext -> ext.getExtendedSpecialLevels(seId))
                    .orElse(0);
            if (level != 5) {
                helper.fail("SE level expected 5 but was " + level);
                return;
            }
        } catch (AssertionError error) {
            helper.fail(error.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "recastingAnvil")
    public static void engraveSe_maxLevelTriggersAdvancement(GameTestHelper helper) {
        ResourceLocation seId = SpecialEffectsRegistry.SHARP_BLADE.getId();
        ItemStack blade = TestItemFactory.bladeFromDefinition(
                helper.getLevel().registryAccess(),
                RecastingSlashBladeKeys.BROADSWORD_WOOD.location()
        );
        var extendedSE = (com.til.recasting.registry.se.ExtendedSpecialEffect)
                mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getValue(seId);
        int maxLevel = extendedSE.getMaxLevel();
        ItemStack crystal = TestItemFactory.seCrystal(seId, maxLevel);
        ItemStack output = AnvilTestHelper.preview(blade, crystal, helper.makeMockPlayer());
        try {
            AnvilTestHelper.assertHasOutput(output, "engraveSe_maxLevelTriggersAdvancement");
            int level = output.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                    .map(ext -> ext.getExtendedSpecialLevels(seId))
                    .orElse(0);
            if (level != maxLevel) {
                helper.fail("SE level expected " + maxLevel + " but was " + level);
                return;
            }
        } catch (AssertionError error) {
            helper.fail(error.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "recastingAnvil")
    public static void advancement_engraveAny_granted(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        ResourceLocation seId = SpecialEffectsRegistry.SHARP_BLADE.getId();
        ItemStack blade = TestItemFactory.bladeFromDefinition(
                helper.getLevel().registryAccess(),
                RecastingSlashBladeKeys.BROADSWORD_WOOD.location()
        );
        ItemStack crystal = TestItemFactory.seCrystal(seId, 1);
        ItemStack output = AnvilTestHelper.preview(blade, crystal, player);
        AnvilTestHelper.simulateRepair(player, blade, crystal, output);
        ResourceLocation advId = Recasting.prefix("growth/forge/new_capability");
        if (!AnvilTestHelper.hasAdvancement(player, advId)) {
            helper.fail("Advancement new_capability not granted after engrave");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "recastingAnvil")
    public static void advancement_engraveMaxNormal_granted(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        ResourceLocation seId = SpecialEffectsRegistry.SHARP_BLADE.getId();
        var extendedSE = (com.til.recasting.registry.se.ExtendedSpecialEffect)
                mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getValue(seId);
        int maxLevel = extendedSE.getMaxLevel();
        ItemStack blade = TestItemFactory.bladeFromDefinition(
                helper.getLevel().registryAccess(),
                RecastingSlashBladeKeys.BROADSWORD_WOOD.location()
        );
        ItemStack crystal = TestItemFactory.seCrystal(seId, maxLevel);
        ItemStack output = AnvilTestHelper.preview(blade, crystal, player);
        AnvilTestHelper.simulateRepair(player, blade, crystal, output);
        ResourceLocation advId = Recasting.prefix("growth/forge/peak_effect");
        if (!AnvilTestHelper.hasAdvancement(player, advId)) {
            helper.fail("Advancement peak_effect not granted after max-level engrave");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "recastingAnvil")
    public static void advancement_eraseSe_granted(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        ResourceLocation seId = SpecialEffectsRegistry.SHARP_BLADE.getId();
        ItemStack blade = TestItemFactory.bladeWithSpecialEffect(
                helper.getLevel().registryAccess(),
                RecastingSlashBladeKeys.BROADSWORD_WOOD.location(),
                seId,
                1
        );
        ItemStack crystal = TestItemFactory.seCrystal(seId, 0);
        ItemStack output = AnvilTestHelper.preview(blade, crystal, player);
        AnvilTestHelper.simulateRepair(player, blade, crystal, output);
        ResourceLocation advId = Recasting.prefix("growth/forge/silence");
        if (!AnvilTestHelper.hasAdvancement(player, advId)) {
            helper.fail("Advancement silence not granted after erase SE");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "recastingAnvil")
    public static void advancement_extractSpecial_granted(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        ResourceLocation seId = SpecialEffectsRegistry.BLACK_ROSE.getId();
        ItemStack blade = TestItemFactory.bladeWithSpecialEffect(
                helper.getLevel().registryAccess(),
                RecastingSlashBladeKeys.UMBRELLA.location(),
                seId,
                1
        );
        ItemStack variant = new ItemStack(RecastingItems.GATHERING_PARTING_VARIANT.get());
        ItemStack output = AnvilTestHelper.preview(blade, variant, player);
        AnvilTestHelper.simulateRepair(player, blade, variant, output);
        ResourceLocation advId = Recasting.prefix("growth/forge/sacrifice");
        if (!AnvilTestHelper.hasAdvancement(player, advId)) {
            helper.fail("Advancement sacrifice not granted after extract special SE");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "recastingAnvil")
    public static void extractSa_noArts_noOutput(GameTestHelper helper) {
        ItemStack sphere = new ItemStack(SlashBladeItems.PROUDSOUL_SPHERE.get());
        ItemStack blade = TestItemFactory.bladeWithoutSlashArts(
                helper.getLevel().registryAccess(),
        RecastingSlashBladeKeys.BROADSWORD_WOOD.location()
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

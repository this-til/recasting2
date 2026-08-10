package com.til.recasting.generated;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.til.recasting.Recasting;
import com.til.recasting.advancement.BladeStatItemPredicate;
import com.til.recasting.advancement.BladeTranslationHelper;
import com.til.recasting.advancement.EnchantedSlashBladeItemPredicate;
import com.til.recasting.advancement.NamedSlashBladeItemPredicate;
import com.til.recasting.advancement.SeCrystalItemPredicate;
import com.til.recasting.advancement.SlashArtsSphereItemPredicate;
import com.til.recasting.constant.RecastingLanguageKeys;
import com.til.recasting.constant.RecastingSlashBladeKeys;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.SpecialEffectsRegistry;
import com.til.recasting.registry.requir.SlashBladeItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.RecipeCraftedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 生成「重铸之路」单栏成就树（刀 / SE / 杀敌 / 附魔 / 精炼）。
 */
public class RecastingAdvancementProvider extends AdvancementProvider {

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/advancements/backgrounds/stone.png");

    public RecastingAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, List.of(new GrowthAdvancements(output)));
    }

    private static final class GrowthAdvancements implements AdvancementSubProvider {

        private final Path dataPackRoot;

        private GrowthAdvancements(PackOutput output) {
            this.dataPackRoot = output.getOutputFolder(PackOutput.Target.DATA_PACK);
        }

        @Override
        public void generate(HolderLookup.Provider registries, Consumer<Advancement> writer) {
            Map<ResourceKey<SlashBladeDefinition>, Advancement> bladeAdvancements = new HashMap<>();
            Map<ResourceLocation, Advancement> seAdvancements = new HashMap<>();

            Advancement root = Advancement.Builder.advancement()
                    .display(
                            bladeIcon(registries, RecastingSlashBladeKeys.COOL_MINT),
                            Component.translatable(RecastingLanguageKeys.ADVANCEMENT_GROWTH_ROOT_TITLE),
                            Component.translatable(RecastingLanguageKeys.ADVANCEMENT_GROWTH_ROOT_DESC),
                            BACKGROUND,
                            FrameType.TASK,
                            false,
                            false,
                            false)
                    .rewards(new AdvancementRewards.Builder()
                            .addLootTable(Recasting.prefix("advancements/growth_root"))
                            .build())
                    .addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
                    .save(writer, Recasting.prefix("growth/root").toString());

            Advancement hubBlade = autoHub(
                    writer,
                    root,
                    "growth/hub/new_blade_smith",
                    bladeIcon(registries, RecastingSlashBladeKeys.BROADSWORD_WOOD),
                    RecastingLanguageKeys.ADVANCEMENT_HUB_NEW_BLADE_SMITH_TITLE,
                    RecastingLanguageKeys.ADVANCEMENT_HUB_NEW_BLADE_SMITH_DESC);
            Advancement hubSe = autoHub(
                    writer,
                    root,
                    "growth/hub/start_crystal",
                    seCrystalIcon(SpecialEffectsRegistry.SHARP_BLADE.getId()),
                    RecastingLanguageKeys.ADVANCEMENT_HUB_START_CRYSTAL_TITLE,
                    RecastingLanguageKeys.ADVANCEMENT_HUB_START_CRYSTAL_DESC);
            Advancement hubKill = autoHub(
                    writer,
                    root,
                    "growth/hub/just_kill",
                    new ItemStack(SlashBladeItems.PROUDSOUL.get()),
                    RecastingLanguageKeys.ADVANCEMENT_HUB_JUST_KILL_TITLE,
                    RecastingLanguageKeys.ADVANCEMENT_HUB_JUST_KILL_DESC);
            Advancement hubEnch = autoHub(
                    writer,
                    root,
                    "growth/hub/enchant_power",
                    enchantedBookIcon(GrowthAdvancementGraph.ENCHANT_BONUS_CHAIN.get(0)),
                    RecastingLanguageKeys.ADVANCEMENT_HUB_ENCHANT_POWER_TITLE,
                    RecastingLanguageKeys.ADVANCEMENT_HUB_ENCHANT_POWER_DESC);
            Advancement hubRefine = autoHub(
                    writer,
                    root,
                    "growth/hub/refine_again",
                    new ItemStack(SlashBladeItems.PROUDSOUL_INGOT.get()),
                    RecastingLanguageKeys.ADVANCEMENT_HUB_REFINE_AGAIN_TITLE,
                    RecastingLanguageKeys.ADVANCEMENT_HUB_REFINE_AGAIN_DESC);

            for (GrowthAdvancementGraph.BladeNode node : GrowthAdvancementGraph.BLADES) {
                Advancement parent = node.parent() == null
                        ? hubBlade
                        : bladeAdvancements.get(node.parent());
                if (parent == null) {
                    throw new IllegalStateException("Missing parent advancement for blade " + node.blade().location()
                            + " parent=" + (node.parent() == null ? "null" : node.parent().location()));
                }

                Advancement.Builder builder = Advancement.Builder.advancement()
                        .parent(parent)
                        .display(
                                bladeIcon(registries, node.blade()),
                                Component.translatable(BladeTranslationHelper.itemDescriptionId(node.blade().location())),
                                bladeDescription(node),
                                null,
                                node.lambda() ? FrameType.GOAL : FrameType.TASK,
                                true,
                                false,
                                false);

                builder.addCriterion(
                        "obtained",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                NamedSlashBladeItemPredicate.of(node.blade().location())));

                if (node.recipeId() != null) {
                    builder.addCriterion("crafted", RecipeCraftedTrigger.TriggerInstance.craftedItem(node.recipeId()));
                    builder.requirements(new String[][]{{"obtained", "crafted"}});
                }

                Advancement advancement = builder.save(
                        writer,
                        Recasting.prefix("growth/blades/" + node.blade().location().getPath()).toString());
                bladeAdvancements.put(node.blade(), advancement);
            }

            saveFluorescenceSeries(registries, writer, bladeAdvancements);

            for (GrowthAdvancementGraph.SeNode node : GrowthAdvancementGraph.SPECIAL_EFFECTS) {
                Advancement parent = node.parentEffectId() == null
                        ? hubSe
                        : seAdvancements.get(node.parentEffectId());
                if (parent == null) {
                    throw new IllegalStateException("Missing parent advancement for se " + node.effectId()
                            + " parent=" + node.parentEffectId());
                }

                Advancement.Builder builder = Advancement.Builder.advancement()
                        .parent(parent)
                        .display(
                                seCrystalIcon(node.effectId()),
                                Component.translatable(seDescriptionId(node.effectId())),
                                Component.translatable(seDescriptionId(node.effectId()) + ".desc"),
                                null,
                                FrameType.TASK,
                                true,
                                false,
                                false);

                builder.addCriterion(
                        "obtained",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                SeCrystalItemPredicate.of(node.effectId(), 1)));
                builder.addCriterion("crafted", RecipeCraftedTrigger.TriggerInstance.craftedItem(node.recipeId()));
                builder.requirements(new String[][]{{"obtained", "crafted"}});

                Advancement advancement = builder.save(
                        writer,
                        Recasting.prefix("growth/se/" + node.effectId().getPath()).toString());
                seAdvancements.put(node.effectId(), advancement);
            }

            saveBackToFutureChain(writer, hubKill);
            saveKillMilestones(writer, hubKill);
            saveEnchantChain(writer, hubEnch);
            saveRefineMilestones(writer, hubRefine);
        }

        private static Advancement autoHub(
                Consumer<Advancement> writer,
                Advancement parent,
                String path,
                ItemStack icon,
                String titleKey,
                String descKey
        ) {
            return Advancement.Builder.advancement()
                    .parent(parent)
                    .display(
                            icon,
                            Component.translatable(titleKey),
                            Component.translatable(descKey),
                            null,
                            FrameType.TASK,
                            false,
                            false,
                            false)
                    .addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
                    .save(writer, Recasting.prefix(path).toString());
        }

        private static void saveBackToFutureChain(Consumer<Advancement> writer, Advancement hubKill) {
            Advancement parent = hubKill;
            for (ResourceLocation saId : GrowthAdvancementGraph.BACK_TO_FUTURE_SLASH_ARTS) {
                ItemStack icon = slashArtsSphereIcon(saId);
                parent = Advancement.Builder.advancement()
                        .parent(parent)
                        .display(
                                icon,
                                Component.translatable(slashArtDescriptionId(saId)),
                                Component.translatable(RecastingLanguageKeys.ADVANCEMENT_DROP_BTF_DESC),
                                null,
                                FrameType.TASK,
                                true,
                                false,
                                false)
                        .addCriterion(
                                "obtained",
                                InventoryChangeTrigger.TriggerInstance.hasItems(
                                        SlashArtsSphereItemPredicate.of(saId)))
                        .save(writer, Recasting.prefix("growth/drop/btf/" + saId.getPath()).toString());
            }
        }

        private static void saveKillMilestones(Consumer<Advancement> writer, Advancement hubKill) {
            Advancement kill1 = Advancement.Builder.advancement()
                    .parent(hubKill)
                    .display(
                            new ItemStack(SlashBladeItems.PROUDSOUL_TINY.get()),
                            Component.translatable(RecastingLanguageKeys.ADVANCEMENT_DROP_KILL_1000_TITLE),
                            Component.translatable(RecastingLanguageKeys.ADVANCEMENT_DROP_KILL_1000_DESC),
                            null,
                            FrameType.GOAL,
                            true,
                            false,
                            false)
                    .addCriterion(
                            "obtained",
                            InventoryChangeTrigger.TriggerInstance.hasItems(
                                    BladeStatItemPredicate.minKill(GrowthAdvancementGraph.KILL_MILESTONE_1)))
                    .save(writer, Recasting.prefix("growth/drop/kill_1000").toString());

            Advancement kill2 = Advancement.Builder.advancement()
                    .parent(kill1)
                    .display(
                            new ItemStack(SlashBladeItems.PROUDSOUL.get()),
                            Component.translatable(RecastingLanguageKeys.ADVANCEMENT_DROP_KILL_10000_TITLE),
                            Component.translatable(RecastingLanguageKeys.ADVANCEMENT_DROP_KILL_10000_DESC),
                            null,
                            FrameType.GOAL,
                            true,
                            false,
                            false)
                    .addCriterion(
                            "obtained",
                            InventoryChangeTrigger.TriggerInstance.hasItems(
                                    BladeStatItemPredicate.minKill(GrowthAdvancementGraph.KILL_MILESTONE_2)))
                    .save(writer, Recasting.prefix("growth/drop/kill_10000").toString());

            Advancement kill3 = Advancement.Builder.advancement()
                    .parent(kill2)
                    .display(
                            new ItemStack(SlashBladeItems.PROUDSOUL_INGOT.get()),
                            Component.translatable(RecastingLanguageKeys.ADVANCEMENT_DROP_KILL_100000_TITLE),
                            Component.translatable(RecastingLanguageKeys.ADVANCEMENT_DROP_KILL_100000_DESC),
                            null,
                            FrameType.CHALLENGE,
                            true,
                            false,
                            false)
                    .addCriterion(
                            "obtained",
                            InventoryChangeTrigger.TriggerInstance.hasItems(
                                    BladeStatItemPredicate.minKill(GrowthAdvancementGraph.KILL_MILESTONE_3)))
                    .save(writer, Recasting.prefix("growth/drop/kill_100000").toString());

            Advancement.Builder.advancement()
                    .parent(kill3)
                    .display(
                            new ItemStack(SlashBladeItems.PROUDSOUL_TRAPEZOHEDRON.get()),
                            Component.translatable(RecastingLanguageKeys.ADVANCEMENT_DROP_KILL_1000000_TITLE),
                            Component.translatable(RecastingLanguageKeys.ADVANCEMENT_DROP_KILL_1000000_DESC),
                            null,
                            FrameType.CHALLENGE,
                            true,
                            false,
                            false)
                    .addCriterion(
                            "obtained",
                            InventoryChangeTrigger.TriggerInstance.hasItems(
                                    BladeStatItemPredicate.minKill(GrowthAdvancementGraph.KILL_MILESTONE_4)))
                    .save(writer, Recasting.prefix("growth/drop/kill_1000000").toString());
        }

        private static void saveEnchantChain(Consumer<Advancement> writer, Advancement hubEnch) {
            Advancement parent = hubEnch;
            for (Enchantment enchantment : GrowthAdvancementGraph.ENCHANT_BONUS_CHAIN) {
                ResourceLocation enchId = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
                if (enchId == null) {
                    throw new IllegalStateException("Unregistered enchantment in bonus chain: " + enchantment);
                }
                String path = enchId.getPath();
                parent = Advancement.Builder.advancement()
                        .parent(parent)
                        .display(
                                enchantedBookIcon(enchantment),
                                Component.translatable(enchantment.getDescriptionId()),
                                Component.translatable(enchantDescKey(path)),
                                null,
                                FrameType.TASK,
                                true,
                                false,
                                false)
                        .addCriterion(
                                "obtained",
                                InventoryChangeTrigger.TriggerInstance.hasItems(
                                        EnchantedSlashBladeItemPredicate.of(enchantment)))
                        .save(writer, Recasting.prefix("growth/enchant/" + path).toString());
            }
        }

        private static void saveRefineMilestones(Consumer<Advancement> writer, Advancement hubRefine) {
            Advancement refine1 = Advancement.Builder.advancement()
                    .parent(hubRefine)
                    .display(
                            new ItemStack(SlashBladeItems.PROUDSOUL_INGOT.get()),
                            Component.translatable(RecastingLanguageKeys.ADVANCEMENT_REFINE_1000_TITLE),
                            Component.translatable(RecastingLanguageKeys.ADVANCEMENT_REFINE_1000_DESC),
                            null,
                            FrameType.GOAL,
                            true,
                            false,
                            false)
                    .addCriterion(
                            "obtained",
                            InventoryChangeTrigger.TriggerInstance.hasItems(
                                    BladeStatItemPredicate.minRefine(GrowthAdvancementGraph.REFINE_MILESTONE_1)))
                    .save(writer, Recasting.prefix("growth/refine/1000").toString());

            Advancement.Builder.advancement()
                    .parent(refine1)
                    .display(
                            new ItemStack(SlashBladeItems.PROUDSOUL_TRAPEZOHEDRON.get()),
                            Component.translatable(RecastingLanguageKeys.ADVANCEMENT_REFINE_10000_TITLE),
                            Component.translatable(RecastingLanguageKeys.ADVANCEMENT_REFINE_10000_DESC),
                            null,
                            FrameType.CHALLENGE,
                            true,
                            false,
                            false)
                    .addCriterion(
                            "obtained",
                            InventoryChangeTrigger.TriggerInstance.hasItems(
                                    BladeStatItemPredicate.minRefine(GrowthAdvancementGraph.REFINE_MILESTONE_2)))
                    .save(writer, Recasting.prefix("growth/refine/10000").toString());
        }

        private void saveFluorescenceSeries(
                HolderLookup.Provider registries,
                Consumer<Advancement> writer,
                Map<ResourceKey<SlashBladeDefinition>, Advancement> bladeAdvancements
        ) {
            Advancement parent = bladeAdvancements.get(RecastingSlashBladeKeys.GREEN_BLADE_WOOD);
            if (parent == null) {
                throw new IllegalStateException("Missing green_blade_wood for fluorescence series");
            }

            GrowthAdvancementGraph.BladeNode iconNode = GrowthAdvancementGraph.FLUORESCENCE_SERIES.get(0);
            Advancement.Builder builder = Advancement.Builder.advancement()
                    .parent(parent)
                    .display(
                            bladeIcon(registries, iconNode.blade()),
                            Component.translatable(RecastingLanguageKeys.ADVANCEMENT_GROWTH_FLUORESCENCE_TITLE),
                            Component.translatable(
                                    RecastingLanguageKeys.ADVANCEMENT_GROWTH_FLUORESCENCE_DESC,
                                    Component.translatable(
                                            BladeTranslationHelper.itemDescriptionId(
                                                    RecastingSlashBladeKeys.GREEN_BLADE_WOOD.location()))),
                            null,
                            FrameType.TASK,
                            true,
                            false,
                            false);

            ArrayList<String> anyBlade = new ArrayList<>();
            for (GrowthAdvancementGraph.BladeNode node : GrowthAdvancementGraph.FLUORESCENCE_SERIES) {
                String path = node.blade().location().getPath().replace('/', '_');
                String obtainedId = "obtained_" + path;
                builder.addCriterion(
                        obtainedId,
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                NamedSlashBladeItemPredicate.of(node.blade().location())));
                anyBlade.add(obtainedId);
                if (node.recipeId() != null) {
                    String craftedId = "crafted_" + path;
                    builder.addCriterion(
                            craftedId,
                            RecipeCraftedTrigger.TriggerInstance.craftedItem(node.recipeId()));
                    anyBlade.add(craftedId);
                }
            }

            builder.requirements(new String[][]{anyBlade.toArray(String[]::new)});
            builder.save(writer, Recasting.prefix("growth/blades/slashblade/fluorescence").toString());
        }

        private ItemStack bladeIcon(HolderLookup.Provider registries, ResourceKey<SlashBladeDefinition> blade) {
            ItemStack fromDefinition = registries.lookup(SlashBladeDefinition.REGISTRY_KEY)
                    .flatMap(lookup -> lookup.get(blade))
                    .map(holder -> persistBladeState(holder.value().getBlade()))
                    .orElse(null);
            if (fromDefinition != null && fromDefinition.hasTag()
                    && fromDefinition.getTag().contains("bladeState")) {
                return fromDefinition;
            }
            return buildNamedBladeIcon(blade.location());
        }

        private ItemStack buildNamedBladeIcon(ResourceLocation bladeId) {
            ItemStack stack = new ItemStack(SlashBladeItems.SLASHBLADE.get());
            ResourceLocation model = ResourceLocation.fromNamespaceAndPath(
                    bladeId.getNamespace(), bladeId.getPath() + ".obj");
            ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                    bladeId.getNamespace(), bladeId.getPath() + ".png");

            JsonObject render = readNamedBladeRender(bladeId);
            if (render != null) {
                if (render.has("model")) {
                    model = ResourceLocation.parse(render.get("model").getAsString());
                }
                if (render.has("texture")) {
                    texture = ResourceLocation.parse(render.get("texture").getAsString());
                }
            }

            ResourceLocation finalModel = model;
            ResourceLocation finalTexture = texture;
            stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
                state.setTranslationKey(BladeTranslationHelper.itemDescriptionId(bladeId));
                state.setModel(finalModel);
                state.setTexture(finalTexture);
                if (render != null && render.has("summon_sword_color")) {
                    state.setColorCode(render.get("summon_sword_color").getAsInt());
                }
                stack.getOrCreateTag().put("bladeState", state.serializeNBT());
            });
            return stack;
        }

        @Nullable
        private JsonObject readNamedBladeRender(ResourceLocation bladeId) {
            Path jsonPath = dataPackRoot
                    .resolve(bladeId.getNamespace())
                    .resolve("slashblade")
                    .resolve("named_blades")
                    .resolve(bladeId.getPath() + ".json");
            if (!Files.isRegularFile(jsonPath)) {
                return null;
            }
            try (BufferedReader reader = Files.newBufferedReader(jsonPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                if (!root.has("render") || !root.get("render").isJsonObject()) {
                    return null;
                }
                return root.getAsJsonObject("render");
            } catch (Exception ignored) {
                return null;
            }
        }

        private static ItemStack persistBladeState(ItemStack stack) {
            if (stack.isEmpty()) {
                return stack;
            }
            stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state ->
                    stack.getOrCreateTag().put("bladeState", state.serializeNBT()));
            return stack;
        }

        private static ItemStack seCrystalIcon(ResourceLocation effectId) {
            ItemStack stack = new ItemStack(RecastingItems.SE_CRYSTAL.get());
            stack.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA).ifPresent(data -> {
                data.setSpecialEffectType(effectId);
                data.setSpecialEffectLevel(1);
                stack.getOrCreateTag().put("se_crystal_data", data.serializeNBT());
            });
            return stack;
        }

        private static ItemStack slashArtsSphereIcon(ResourceLocation saId) {
            ItemStack stack = new ItemStack(SlashBladeItems.PROUDSOUL_SPHERE.get());
            CompoundTag tag = stack.getOrCreateTag();
            tag.putString("SpecialAttackType", saId.toString());
            return stack;
        }

        private static ItemStack enchantedBookIcon(Enchantment enchantment) {
            ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
            stack.enchant(enchantment, 1);
            return stack;
        }

        private static Component bladeDescription(GrowthAdvancementGraph.BladeNode node) {
            if (node.parent() != null) {
                return Component.translatable(
                        RecastingLanguageKeys.ADVANCEMENT_GROWTH_BLADE_FROM_DESC,
                        Component.translatable(BladeTranslationHelper.itemDescriptionId(node.parent().location())));
            }
            if (node.menu()) {
                return Component.translatable(RecastingLanguageKeys.ADVANCEMENT_GROWTH_BLADE_MENU_DESC);
            }
            if (node.recipeId() == null) {
                return Component.translatable(RecastingLanguageKeys.ADVANCEMENT_GROWTH_BLADE_PENDING_DESC);
            }
            return Component.translatable(RecastingLanguageKeys.ADVANCEMENT_GROWTH_BLADE_START_DESC);
        }

        private static String seDescriptionId(ResourceLocation effectId) {
            SpecialEffect effect = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get()
                    .getValue(effectId);
            if (effect != null) {
                return effect.getDescriptionId();
            }
            return net.minecraft.Util.makeDescriptionId("se", effectId);
        }

        private static String slashArtDescriptionId(ResourceLocation saId) {
            mods.flammpfeil.slashblade.slasharts.SlashArts arts =
                    mods.flammpfeil.slashblade.registry.SlashArtsRegistry.REGISTRY.get().getValue(saId);
            if (arts != null) {
                return arts.getDescriptionId();
            }
            return net.minecraft.Util.makeDescriptionId("slash_art", saId);
        }

        private static String enchantDescKey(String path) {
            return switch (path) {
                case "smite" -> RecastingLanguageKeys.ADVANCEMENT_ENCHANT_SMITE_DESC;
                case "bane_of_arthropods" -> RecastingLanguageKeys.ADVANCEMENT_ENCHANT_BANE_DESC;
                case "fire_aspect" -> RecastingLanguageKeys.ADVANCEMENT_ENCHANT_FIRE_ASPECT_DESC;
                case "flame" -> RecastingLanguageKeys.ADVANCEMENT_ENCHANT_FLAME_DESC;
                case "power" -> RecastingLanguageKeys.ADVANCEMENT_ENCHANT_POWER_DESC;
                case "sweeping" -> RecastingLanguageKeys.ADVANCEMENT_ENCHANT_SWEEPING_DESC;
                default -> RecastingLanguageKeys.ADVANCEMENT_HUB_ENCHANT_POWER_DESC;
            };
        }
    }
}

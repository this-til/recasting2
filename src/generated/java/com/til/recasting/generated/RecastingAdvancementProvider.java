package com.til.recasting.generated;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.til.recasting.Recasting;
import com.til.recasting.advancement.BladeTranslationHelper;
import com.til.recasting.advancement.NamedSlashBladeItemPredicate;
import com.til.recasting.advancement.SeCrystalItemPredicate;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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
 * 生成刀成长与 SE 结晶成就树。
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

            // SE 独立成就栏（带 background 的根节点）
            Advancement seRoot = Advancement.Builder.advancement()
                    .display(
                            seCrystalIcon(SpecialEffectsRegistry.SHARP_BLADE.getId()),
                            Component.translatable(RecastingLanguageKeys.ADVANCEMENT_GROWTH_SE_ROOT_TITLE),
                            Component.translatable(RecastingLanguageKeys.ADVANCEMENT_GROWTH_SE_ROOT_DESC),
                            BACKGROUND,
                            FrameType.TASK,
                            false,
                            false,
                            false)
                    .addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
                    .save(writer, Recasting.prefix("growth/se/root").toString());

            for (GrowthAdvancementGraph.BladeNode node : GrowthAdvancementGraph.BLADES) {
                Advancement parent = node.parent() == null
                        ? root
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
                    // 同组内为 OR：合成或获得任一即可
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
                        ? seRoot
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
                                seDescription(node.parentEffectId()),
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
                // 同组内为 OR：合成或获得任一即可
                builder.requirements(new String[][]{{"obtained", "crafted"}});

                Advancement advancement = builder.save(
                        writer,
                        Recasting.prefix("growth/se/" + node.effectId().getPath()).toString());
                seAdvancements.put(node.effectId(), advancement);
            }
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

            // 同组内 OR：任意一把荧光刀（合成或获得）即可
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

        private static Component seDescription(@Nullable ResourceLocation parentEffectId) {
            if (parentEffectId == null) {
                return Component.translatable(RecastingLanguageKeys.ADVANCEMENT_GROWTH_SE_START_DESC);
            }
            return Component.translatable(
                    RecastingLanguageKeys.ADVANCEMENT_GROWTH_SE_FROM_DESC,
                    Component.translatable(seDescriptionId(parentEffectId)));
        }

        private static String seDescriptionId(ResourceLocation effectId) {
            SpecialEffect effect = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get()
                    .getValue(effectId);
            if (effect != null) {
                return effect.getDescriptionId();
            }
            // ponytail: datagen 早期注册表可能为空，回退约定 key
            return net.minecraft.Util.makeDescriptionId("se", effectId);
        }
    }
}

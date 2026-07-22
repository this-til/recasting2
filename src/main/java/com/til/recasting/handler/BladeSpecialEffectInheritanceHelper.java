package com.til.recasting.handler;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.registry.se.ExtendedSpecialEffect;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 拔刀剑合成时 SE 的继承与替换规则：
 * <ul>
 *   <li>产出刀定义已绑定特殊 SE → 使用定义 SE，不继承材料刀特殊 SE</li>
 *   <li>产出刀定义无特殊 SE → 从材料刀继承特殊 SE</li>
 *   <li>普通 SE：材料与定义合并；同 SE 取更高等级；最多 {@value #MAX_NORMAL_SPECIAL_EFFECTS} 个；
 *       超额时优先抛弃新刀定义上的普通 SE</li>
 * </ul>
 */
public final class BladeSpecialEffectInheritanceHelper {

    public static final int MAX_NORMAL_SPECIAL_EFFECTS = 4;

    private record EffectEntry(ResourceLocation seId, int level) {
    }

    private BladeSpecialEffectInheritanceHelper() {
    }

    public static void apply(
            ItemStack result,
            CraftingContainer container,
            RegistryAccess access,
            ResourceLocation outputBladeId
    ) {
        if (result.isEmpty() || !(result.getItem() instanceof ItemSlashBlade)) {
            return;
        }

        SlashBladeDefinition outputDefinition = access.registryOrThrow(SlashBladeDefinition.REGISTRY_KEY)
                .get(ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, outputBladeId));
        if (outputDefinition == null) {
            return;
        }

        IForgeRegistry<SpecialEffect> registry = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get();
        Map<ResourceLocation, Integer> definitionNormals = collectDefinitionNormalEffects(outputDefinition, registry);
        boolean definitionHasSpecial = definitionHasSpecialEffect(outputDefinition, registry);

        if (!definitionHasSpecial) {
            findInputSpecialEffect(container, registry).ifPresent(source ->
                    applySpecialEffect(result, registry, source.seId(), source.level())
            );
        }

        Map<ResourceLocation, Integer> inheritedNormals = findInputNormalEffects(container, registry);
        Map<ResourceLocation, Integer> mergedNormals = mergeNormalEffects(definitionNormals, inheritedNormals);
        applyNormalEffects(result, registry, mergedNormals);
    }

    /**
     * 合并普通 SE：先保留材料刀继承（与定义同 SE 时取高等级），再补定义独有 SE，超额抛弃定义侧。
     */
    private static Map<ResourceLocation, Integer> mergeNormalEffects(
            Map<ResourceLocation, Integer> definitionNormals,
            Map<ResourceLocation, Integer> inheritedNormals
    ) {
        Map<ResourceLocation, Integer> merged = new LinkedHashMap<>();

        for (Map.Entry<ResourceLocation, Integer> inherited : inheritedNormals.entrySet()) {
            if (merged.size() >= MAX_NORMAL_SPECIAL_EFFECTS) {
                break;
            }
            ResourceLocation seId = inherited.getKey();
            int level = inherited.getValue();
            Integer definitionLevel = definitionNormals.get(seId);
            if (definitionLevel != null) {
                level = Math.max(level, definitionLevel);
            }
            merged.put(seId, level);
        }

        for (Map.Entry<ResourceLocation, Integer> definition : definitionNormals.entrySet()) {
            ResourceLocation seId = definition.getKey();
            if (merged.containsKey(seId)) {
                continue;
            }
            if (merged.size() >= MAX_NORMAL_SPECIAL_EFFECTS) {
                break;
            }
            merged.put(seId, definition.getValue());
        }

        return merged;
    }

    private static void applyNormalEffects(
            ItemStack result,
            IForgeRegistry<SpecialEffect> registry,
            Map<ResourceLocation, Integer> mergedNormals
    ) {
        result.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(bladeState ->
                result.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION).ifPresent(extension -> {
                    List<ResourceLocation> toRemove = new ArrayList<>();
                    for (ResourceLocation existingSeId : bladeState.getSpecialEffects()) {
                        if (isSpecialEffect(registry, existingSeId)) {
                            continue;
                        }
                        if (!mergedNormals.containsKey(existingSeId)) {
                            toRemove.add(existingSeId);
                        }
                    }
                    for (ResourceLocation seToRemove : toRemove) {
                        bladeState.removeSpecialEffect(seToRemove);
                        extension.setExtendedSpecialLevels(seToRemove, 0);
                    }

                    for (Map.Entry<ResourceLocation, Integer> entry : mergedNormals.entrySet()) {
                        bladeState.addSpecialEffect(entry.getKey());
                        extension.setExtendedSpecialLevels(entry.getKey(), entry.getValue());
                    }

                    persistBladeState(result, bladeState);
                })
        );
    }

    private static Map<ResourceLocation, Integer> collectDefinitionNormalEffects(
            SlashBladeDefinition definition,
            IForgeRegistry<SpecialEffect> registry
    ) {
        Map<ResourceLocation, Integer> normals = new LinkedHashMap<>();
        PropertiesDefinitionExtension extension = null;
        if (definition instanceof com.til.recasting.mixin_api.ISlashBladeStateExtension stateExtension) {
            extension = stateExtension.getRecasting$propertiesDefinitionExtension();
        }
        for (ResourceLocation seId : definition.getStateDefinition().getSpecialEffects()) {
            if (isSpecialEffect(registry, seId)) {
                continue;
            }
            int level = 1;
            if (extension != null) {
                int extensionLevel = extension.getExtendedSpecialLevels(seId);
                if (extensionLevel > 0) {
                    level = extensionLevel;
                }
            }
            normals.put(seId, level);
        }
        return normals;
    }

    private static boolean definitionHasSpecialEffect(
            SlashBladeDefinition definition,
            IForgeRegistry<SpecialEffect> registry
    ) {
        for (ResourceLocation seId : definition.getStateDefinition().getSpecialEffects()) {
            if (isSpecialEffect(registry, seId)) {
                return true;
            }
        }
        return false;
    }

    private static Optional<EffectEntry> findInputSpecialEffect(
            CraftingContainer container,
            IForgeRegistry<SpecialEffect> registry
    ) {
        for (ItemStack stack : container.getItems()) {
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemSlashBlade)) {
                continue;
            }
            Optional<EffectEntry> inherited = findSpecialEffectOnBlade(stack, registry);
            if (inherited.isPresent()) {
                return inherited;
            }
        }
        return Optional.empty();
    }

    private static Map<ResourceLocation, Integer> findInputNormalEffects(
            CraftingContainer container,
            IForgeRegistry<SpecialEffect> registry
    ) {
        Map<ResourceLocation, Integer> inheritedEffects = new LinkedHashMap<>();
        for (ItemStack stack : container.getItems()) {
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemSlashBlade)) {
                continue;
            }
            for (EffectEntry entry : findNormalEffectsOnBlade(stack, registry)) {
                Integer existing = inheritedEffects.get(entry.seId());
                if (existing == null || entry.level() > existing) {
                    inheritedEffects.put(entry.seId(), entry.level());
                }
            }
        }
        return inheritedEffects;
    }

    private static Optional<EffectEntry> findSpecialEffectOnBlade(
            ItemStack blade,
            IForgeRegistry<SpecialEffect> registry
    ) {
        PropertiesDefinitionExtension extension = blade.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                .orElse(null);
        ISlashBladeState bladeState = blade.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
        if (extension == null || bladeState == null) {
            return Optional.empty();
        }

        for (ResourceLocation seId : bladeState.getSpecialEffects()) {
            if (!isSpecialEffect(registry, seId)) {
                continue;
            }
            int level = extension.getExtendedSpecialLevels(seId);
            if (level > 0) {
                return Optional.of(new EffectEntry(seId, level));
            }
        }
        return Optional.empty();
    }

    private static List<EffectEntry> findNormalEffectsOnBlade(
            ItemStack blade,
            IForgeRegistry<SpecialEffect> registry
    ) {
        List<EffectEntry> effects = new ArrayList<>();
        PropertiesDefinitionExtension extension = blade.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                .orElse(null);
        ISlashBladeState bladeState = blade.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
        if (extension == null || bladeState == null) {
            return effects;
        }

        for (ResourceLocation seId : bladeState.getSpecialEffects()) {
            if (isSpecialEffect(registry, seId)) {
                continue;
            }
            int level = extension.getExtendedSpecialLevels(seId);
            if (level <= 0) {
                level = SpecialEffect.getRequestLevel(seId);
            }
            if (level > 0) {
                effects.add(new EffectEntry(seId, level));
            }
        }
        return effects;
    }

    private static void applySpecialEffect(
            ItemStack result,
            IForgeRegistry<SpecialEffect> registry,
            ResourceLocation seId,
            int level
    ) {
        result.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(bladeState ->
                result.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION).ifPresent(extension -> {
                    removeOtherSpecialEffects(bladeState, extension, registry, seId);
                    bladeState.addSpecialEffect(seId);
                    extension.setExtendedSpecialLevels(seId, level);
                    persistBladeState(result, bladeState);
                })
        );
    }

    private static boolean isSpecialEffect(IForgeRegistry<SpecialEffect> registry, ResourceLocation seId) {
        SpecialEffect specialEffect = registry.getValue(seId);
        return specialEffect instanceof ExtendedSpecialEffect extendedSpecialEffect
                && extendedSpecialEffect.isSpecial();
    }

    private static void removeOtherSpecialEffects(
            ISlashBladeState bladeState,
            PropertiesDefinitionExtension extension,
            IForgeRegistry<SpecialEffect> registry,
            @Nullable ResourceLocation keep
    ) {
        List<ResourceLocation> toRemove = new ArrayList<>();
        for (ResourceLocation existingSeId : bladeState.getSpecialEffects()) {
            if (existingSeId.equals(keep)) {
                continue;
            }
            if (isSpecialEffect(registry, existingSeId)) {
                toRemove.add(existingSeId);
            }
        }
        for (ResourceLocation seToRemove : toRemove) {
            bladeState.removeSpecialEffect(seToRemove);
            extension.setExtendedSpecialLevels(seToRemove, 0);
        }
    }

    private static void persistBladeState(ItemStack stack, ISlashBladeState bladeState) {
        stack.getOrCreateTag().put("bladeState", bladeState.serializeNBT());
    }
}

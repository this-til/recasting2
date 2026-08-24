package com.til.recasting.handler;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.mixin_api.ISlashBladeStateExtension;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

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
            CraftingInput container,
            HolderLookup.Provider access,
            ResourceLocation outputBladeId
    ) {
        if (result.isEmpty() || !(result.getItem() instanceof ItemSlashBlade)) {
            return;
        }

        SlashBladeDefinition outputDefinition = access.lookupOrThrow(SlashBladeDefinition.REGISTRY_KEY)
                .get(ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, outputBladeId))
                .map(Holder::value)
                .orElse(null);
        if (outputDefinition == null) {
            return;
        }

        Map<ResourceLocation, Integer> definitionNormals = collectDefinitionNormalEffects(outputDefinition);
        boolean definitionHasSpecial = definitionHasSpecialEffect(outputDefinition);

        if (!definitionHasSpecial) {
            findInputSpecialEffect(container).ifPresent(source ->
                    applySpecialEffect(result, source.seId(), source.level())
            );
        }

        Map<ResourceLocation, Integer> inheritedNormals = findInputNormalEffects(container);
        Map<ResourceLocation, Integer> mergedNormals = mergeNormalEffects(definitionNormals, inheritedNormals);
        applyNormalEffects(result, mergedNormals);
    }

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
            Map<ResourceLocation, Integer> mergedNormals
    ) {
        ISlashBladeState bladeState = BladeStateAccess.of(result).orElse(null);
        if (bladeState == null) {
            return;
        }
        PropertiesDefinitionExtension extension = BladeSpecialEffectHelper.copyProperties(AttackHelper.propertiesOf(result));

        List<ResourceLocation> toRemove = new ArrayList<>();
        for (ResourceLocation existingSeId : bladeState.getSpecialEffects()) {
            if (BladeSpecialEffectHelper.isSpecialExtendedEffect(existingSeId)) {
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

        BladeSpecialEffectHelper.writeProperties(result, extension);
    }

    private static Map<ResourceLocation, Integer> collectDefinitionNormalEffects(
            SlashBladeDefinition definition
    ) {
        Map<ResourceLocation, Integer> normals = new LinkedHashMap<>();
        PropertiesDefinitionExtension extension = null;
        if (definition instanceof ISlashBladeStateExtension stateExtension) {
            extension = stateExtension.getRecasting$propertiesDefinitionExtension();
        }
        for (ResourceLocation seId : definition.getStateDefinition().getSpecialEffects()) {
            if (BladeSpecialEffectHelper.isSpecialExtendedEffect(seId)) {
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

    private static boolean definitionHasSpecialEffect(SlashBladeDefinition definition) {
        for (ResourceLocation seId : definition.getStateDefinition().getSpecialEffects()) {
            if (BladeSpecialEffectHelper.isSpecialExtendedEffect(seId)) {
                return true;
            }
        }
        return false;
    }

    private static Optional<EffectEntry> findInputSpecialEffect(CraftingInput container) {
        for (ItemStack stack : container.items()) {
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemSlashBlade)) {
                continue;
            }
            Optional<EffectEntry> inherited = findSpecialEffectOnBlade(stack);
            if (inherited.isPresent()) {
                return inherited;
            }
        }
        return Optional.empty();
    }

    private static Map<ResourceLocation, Integer> findInputNormalEffects(CraftingInput container) {
        Map<ResourceLocation, Integer> inheritedEffects = new LinkedHashMap<>();
        for (ItemStack stack : container.items()) {
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemSlashBlade)) {
                continue;
            }
            for (EffectEntry entry : findNormalEffectsOnBlade(stack)) {
                Integer existing = inheritedEffects.get(entry.seId());
                if (existing == null || entry.level() > existing) {
                    inheritedEffects.put(entry.seId(), entry.level());
                }
            }
        }
        return inheritedEffects;
    }

    private static Optional<EffectEntry> findSpecialEffectOnBlade(ItemStack blade) {
        return BladeSpecialEffectHelper.findFirstSpecialEffect(blade)
                .map(entry -> new EffectEntry(entry.id(), entry.level()));
    }

    private static List<EffectEntry> findNormalEffectsOnBlade(ItemStack blade) {
        List<EffectEntry> effects = new ArrayList<>();
        PropertiesDefinitionExtension extension = AttackHelper.propertiesOf(blade);
        ISlashBladeState bladeState = BladeStateAccess.of(blade).orElse(null);
        if (bladeState == null) {
            return effects;
        }

        for (ResourceLocation seId : bladeState.getSpecialEffects()) {
            if (BladeSpecialEffectHelper.isSpecialExtendedEffect(seId)) {
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
            ResourceLocation seId,
            int level
    ) {
        ISlashBladeState bladeState = BladeStateAccess.of(result).orElse(null);
        if (bladeState == null) {
            return;
        }
        PropertiesDefinitionExtension extension = BladeSpecialEffectHelper.copyProperties(AttackHelper.propertiesOf(result));
        BladeSpecialEffectHelper.removeSpecialEffectsExcept(
                bladeState,
                extension,
                seId
        );
        bladeState.addSpecialEffect(seId);
        extension.setExtendedSpecialLevels(seId, level);
        BladeSpecialEffectHelper.writeProperties(result, extension);
    }
}

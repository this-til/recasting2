package com.til.recasting.handler;

import org.junit.jupiter.api.Test;

import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class BladeSpecialEffectInheritanceHelperTest {

    @Test
    public void mergeNormalEffects_mergesDefinitionUniqueEffectsWhenBelowCap() {
        Map<ResourceLocation, Integer> definitionNormals = linkedEffects(
                entry("recasting:def_a", 1),
                entry("recasting:def_b", 2)
        );
        Map<ResourceLocation, Integer> inheritedNormals = linkedEffects(
                entry("recasting:mat_a", 3)
        );

        Map<ResourceLocation, Integer> merged = mergeNormalEffects(definitionNormals, inheritedNormals);

        assertEquals(3, merged.size());
        assertEquals(linkedEffects(
                entry("recasting:mat_a", 3),
                entry("recasting:def_a", 1),
                entry("recasting:def_b", 2)
        ), merged);
    }

    @Test
    public void mergeNormalEffects_prefersHigherLevelForSameEffect() {
        ResourceLocation sharedSe = resourceLocation("recasting", "shared");
        Map<ResourceLocation, Integer> definitionNormals = linkedEffects(entry(sharedSe, 2));
        Map<ResourceLocation, Integer> inheritedNormals = linkedEffects(entry(sharedSe, 5));

        Map<ResourceLocation, Integer> merged = mergeNormalEffects(definitionNormals, inheritedNormals);

        assertEquals(1, merged.size());
        assertEquals(5, merged.get(sharedSe));
    }

    @Test
    public void mergeNormalEffects_discardsDefinitionEffectsFirstWhenOverCap() {
        ResourceLocation sharedSe = resourceLocation("recasting", "shared");
        ResourceLocation droppedDefinitionSe = resourceLocation("recasting", "dropped_definition");
        Map<ResourceLocation, Integer> definitionNormals = linkedEffects(
                entry(sharedSe, 4),
                entry(droppedDefinitionSe, 2)
        );
        Map<ResourceLocation, Integer> inheritedNormals = linkedEffects(
                entry("recasting:mat_a", 1),
                entry("recasting:mat_b", 1),
                entry(sharedSe, 1),
                entry("recasting:mat_c", 1)
        );

        Map<ResourceLocation, Integer> merged = mergeNormalEffects(definitionNormals, inheritedNormals);

        assertEquals(BladeSpecialEffectInheritanceHelper.MAX_NORMAL_SPECIAL_EFFECTS, merged.size());
        assertEquals(4, merged.get(sharedSe));
        assertFalse(merged.containsKey(droppedDefinitionSe));
    }

    @SuppressWarnings("unchecked")
    private static Map<ResourceLocation, Integer> mergeNormalEffects(
            Map<ResourceLocation, Integer> definitionNormals,
            Map<ResourceLocation, Integer> inheritedNormals
    ) {
        try {
            Method method = BladeSpecialEffectInheritanceHelper.class.getDeclaredMethod(
                    "mergeNormalEffects",
                    Map.class,
                    Map.class
            );
            method.setAccessible(true);
            return (Map<ResourceLocation, Integer>) method.invoke(null, definitionNormals, inheritedNormals);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            throw new AssertionError("Unable to invoke mergeNormalEffects", exception);
        }
    }

    @SafeVarargs
    private static Map<ResourceLocation, Integer> linkedEffects(Map.Entry<ResourceLocation, Integer>... entries) {
        Map<ResourceLocation, Integer> effects = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, Integer> entry : entries) {
            effects.put(entry.getKey(), entry.getValue());
        }
        return effects;
    }

    private static Map.Entry<ResourceLocation, Integer> entry(String seId, int level) {
        return entry(resourceLocation(seId), level);
    }

    private static ResourceLocation resourceLocation(String seId) {
        return ResourceLocation.tryParse(seId);
    }

    private static ResourceLocation resourceLocation(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    private static Map.Entry<ResourceLocation, Integer> entry(ResourceLocation seId, int level) {
        return Map.entry(seId, level);
    }
}

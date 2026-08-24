package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.registry.se.ProbeSpecialEffect;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Special Effects (SE) 注册表。
 */
public final class SpecialEffectsRegistry {

    public static final DeferredRegister<SpecialEffect> SPECIAL_EFFECT =
            DeferredRegister.create(SpecialEffect.REGISTRY_KEY, Recasting.MODID);

    // TODO(P3): 从 1.20 批量移植其余 SE 注册项后删除探针
    public static final DeferredHolder<SpecialEffect, SpecialEffect> PROBE =
            registerExtendedSE("probe", ProbeSpecialEffect::new);

    private SpecialEffectsRegistry() {
    }

    public static DeferredHolder<SpecialEffect, SpecialEffect> registerExtendedSE(
            String name,
            Supplier<SpecialEffect> factory
    ) {
        return SPECIAL_EFFECT.register(name, factory);
    }
}

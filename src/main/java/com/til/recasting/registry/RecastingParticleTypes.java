package com.til.recasting.registry;

import com.til.recasting.Recasting;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Recasting 粒子类型注册表。
 * TODO(P5): 客户端 ParticleProvider 与自定义着色/环带逻辑。
 */
public final class RecastingParticleTypes {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, Recasting.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STAR_BLINK =
            PARTICLE_TYPES.register("star_blink", () -> new SimpleParticleType(true));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GOLDEN_HALBERD =
            PARTICLE_TYPES.register("golden_halberd", () -> new SimpleParticleType(true));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TEA_AROMA =
            PARTICLE_TYPES.register("tea_aroma", () -> new SimpleParticleType(true));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LIGHTNING_HIT =
            PARTICLE_TYPES.register("lightning_hit", () -> new SimpleParticleType(true));

    private RecastingParticleTypes() {
    }
}

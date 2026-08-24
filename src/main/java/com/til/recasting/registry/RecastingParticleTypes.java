package com.til.recasting.registry;

import com.til.recasting.Recasting;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Recasting 粒子类型注册表。
 */
public final class RecastingParticleTypes {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, Recasting.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DEFAULT_PARTICLE =
            PARTICLE_TYPES.register("default_particle", () -> new SimpleParticleType(true));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STAR_BLINK =
            PARTICLE_TYPES.register("star_blink", () -> new SimpleParticleType(true));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GOLDEN_HALBERD =
            PARTICLE_TYPES.register("golden_halberd", () -> new SimpleParticleType(true));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TEA_AROMA =
            PARTICLE_TYPES.register("tea_aroma", () -> new SimpleParticleType(true));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LIGHTNING_HIT =
            PARTICLE_TYPES.register("lightning_hit", () -> new SimpleParticleType(true));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MORTAL_DUST_TRAIL =
            PARTICLE_TYPES.register("mortal_dust_trail", () -> new SimpleParticleType(true));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MORTAL_DUST_HIT =
            PARTICLE_TYPES.register("mortal_dust_hit", () -> new SimpleParticleType(true));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BURST_RING =
            PARTICLE_TYPES.register("burst_ring", () -> new SimpleParticleType(true));

    private RecastingParticleTypes() {
    }
}

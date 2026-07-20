package com.til.recasting.registry;

import com.til.recasting.Recasting;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Recasting 模组的粒子类型注册表
 */
public class RecastingParticleTypes {
    
    /**
     * 粒子类型延迟注册器
     */
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = 
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Recasting.MODID);
    
    /**
     * 默认粒子类型
     * 支持自定义纹理、大小、颜色、旋转等效果
     */
    public static final RegistryObject<SimpleParticleType> DEFAULT_PARTICLE =
            PARTICLE_TYPES.register("default_particle", () -> new SimpleParticleType(true));

    /**
     * 星闪满层触发粒子
     */
    public static final RegistryObject<SimpleParticleType> STAR_BLINK =
            PARTICLE_TYPES.register("star_blink", () -> new SimpleParticleType(true));

    /**
     * 金戈满层引爆粒子（环状冲击）
     */
    public static final RegistryObject<SimpleParticleType> GOLDEN_HALBERD =
            PARTICLE_TYPES.register("golden_halberd", () -> new SimpleParticleType(true));

    /**
     * 茶韵延迟释放裂隙斩粒子（快速撕裂、缓慢愈合）
     */
    public static final RegistryObject<SimpleParticleType> TEA_AROMA =
            PARTICLE_TYPES.register("tea_aroma", () -> new SimpleParticleType(true));
}


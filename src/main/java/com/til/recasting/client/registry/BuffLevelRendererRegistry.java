package com.til.recasting.client.registry;

import com.til.recasting.Recasting;
import com.til.recasting.client.registry.instance.BuffLevelRenderConfig;
import com.til.recasting.registry.RecastingBuffTypes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Buff层数渲染器注册表
 * 用于标记哪些 BuffType 需要在实体名称上方显示层数
 */
public class BuffLevelRendererRegistry {

    /**
     * Buff层数渲染配置注册表键
     */
    public static final ResourceKey<Registry<BuffLevelRenderConfig>> BUFF_LEVEL_RENDER_CONFIG_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Recasting.prefix("buff_level_render_config"));

    /**
     * Buff层数渲染配置注册表
     */
    public static final DeferredRegister<BuffLevelRenderConfig> BUFF_LEVEL_RENDER_CONFIGS =
            DeferredRegister.create(BUFF_LEVEL_RENDER_CONFIG_REGISTRY_KEY, Recasting.MODID);

    /**
     * Buff层数渲染配置注册表实例
     */
    public static final Supplier<IForgeRegistry<BuffLevelRenderConfig>> REGISTRY =
            BUFF_LEVEL_RENDER_CONFIGS.makeRegistry(() -> new RegistryBuilder<BuffLevelRenderConfig>()
                    .setDefaultKey(Recasting.prefix("default"))
            );

    // ==================== 预定义的渲染配置 ====================

    /**
     * 灵魂燃烧 - 显示层数
     */
    public static final RegistryObject<BuffLevelRenderConfig> SOUL_BURN = BUFF_LEVEL_RENDER_CONFIGS.register(
            "soul_burn",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.SOUL_BURN, "buff.recasting.soul_burn")
    );
    /**
     * 剑势 - 显示层数
     */
    public static final RegistryObject<BuffLevelRenderConfig> FRAGMENT = BUFF_LEVEL_RENDER_CONFIGS.register(
            "sword_momentum",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.FRAGMENT, "buff.recasting.fragment")
    );

    /**
     * 电离 - 显示层数
     */
    public static final RegistryObject<BuffLevelRenderConfig> IONIZATION = BUFF_LEVEL_RENDER_CONFIGS.register(
            "ionization",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.IONIZATION, "buff.recasting.ionization")
    );

    /**
     * 蓄能 - 显示层数
     */
    public static final RegistryObject<BuffLevelRenderConfig> ENERGY_STORAGE = BUFF_LEVEL_RENDER_CONFIGS.register(
            "energy_storage",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.ENERGY_STORAGE, "buff.recasting.energy_storage")
    );

    /**
     * 撕裂 - 显示层数
     */
    public static final RegistryObject<BuffLevelRenderConfig> TEAR = BUFF_LEVEL_RENDER_CONFIGS.register(
            "tear",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.TEAR, "buff.recasting.tear")
    );

    /**
     * 光子灼痕 - 显示灼痕层数
     */
    public static final RegistryObject<BuffLevelRenderConfig> PHOTON_SCAR = BUFF_LEVEL_RENDER_CONFIGS.register(
            "photon_scar",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.PHOTON_SCAR, "buff.recasting.photon_scar")
    );

    /**
     * 光子灼烧 - 显示灼烧层数
     */
    public static final RegistryObject<BuffLevelRenderConfig> PHOTON_BURN = BUFF_LEVEL_RENDER_CONFIGS.register(
            "photon_burn",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.PHOTON_BURN, "buff.recasting.photon_burn")
    );

    /**
     * 日核 - 显示层数
     */
    public static final RegistryObject<BuffLevelRenderConfig> SUNSET_CORE = BUFF_LEVEL_RENDER_CONFIGS.register(
            "sunset_core",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.SUNSET_CORE, "buff.recasting.sunset_core")
    );

    /**
     * 叠晖 - 显示层数
     */
    public static final RegistryObject<BuffLevelRenderConfig> SUNSET_STACK = BUFF_LEVEL_RENDER_CONFIGS.register(
            "sunset_stack",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.SUNSET_STACK, "buff.recasting.sunset_stack")
    );

    /**
     * 金戈 - 显示层数
     */
    public static final RegistryObject<BuffLevelRenderConfig> GOLDEN_HALBERD = BUFF_LEVEL_RENDER_CONFIGS.register(
            "golden_halberd",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.GOLDEN_HALBERD, "buff.recasting.golden_halberd")
    );

    /**
     * 茶韵 - 显示延迟伤害单位（伤害 × 10）
     */
    public static final RegistryObject<BuffLevelRenderConfig> TEA_AROMA = BUFF_LEVEL_RENDER_CONFIGS.register(
            "tea_aroma",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.TEA_AROMA, "buff.recasting.tea_aroma")
    );

}

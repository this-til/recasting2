package com.til.recasting.client.registry;

import com.til.recasting.Recasting;
import com.til.recasting.client.constant.LanguageItems;
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
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.SOUL_BURN, LanguageItems.BUFF_SOUL_BURN)
    );
    /**
     * 剑势 - 显示层数
     */
    public static final RegistryObject<BuffLevelRenderConfig> FRAGMENT = BUFF_LEVEL_RENDER_CONFIGS.register(
            "sword_momentum",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.FRAGMENT, LanguageItems.BUFF_FRAGMENT)
    );

    /**
     * 电离 - 显示层数
     */
    public static final RegistryObject<BuffLevelRenderConfig> IONIZATION = BUFF_LEVEL_RENDER_CONFIGS.register(
            "ionization",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.IONIZATION, LanguageItems.BUFF_IONIZATION)
    );

    /**
     * 蓄能 - 显示层数
     */
    public static final RegistryObject<BuffLevelRenderConfig> ENERGY_STORAGE = BUFF_LEVEL_RENDER_CONFIGS.register(
            "energy_storage",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.ENERGY_STORAGE, LanguageItems.BUFF_ENERGY_STORAGE)
    );

    /**
     * 撕裂 - 显示层数
     */
    public static final RegistryObject<BuffLevelRenderConfig> TEAR = BUFF_LEVEL_RENDER_CONFIGS.register(
            "tear",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.TEAR, LanguageItems.BUFF_TEAR)
    );

    /**
     * 光子灼痕 - 显示灼痕层数
     */
    public static final RegistryObject<BuffLevelRenderConfig> PHOTON_SCAR = BUFF_LEVEL_RENDER_CONFIGS.register(
            "photon_scar",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.PHOTON_SCAR, LanguageItems.BUFF_PHOTON_SCAR)
    );

    /**
     * 光子灼烧 - 显示灼烧层数
     */
    public static final RegistryObject<BuffLevelRenderConfig> PHOTON_BURN = BUFF_LEVEL_RENDER_CONFIGS.register(
            "photon_burn",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.PHOTON_BURN, LanguageItems.BUFF_PHOTON_BURN)
    );

    /**
     * 日核 - 显示层数
     */
    public static final RegistryObject<BuffLevelRenderConfig> SUNSET_CORE = BUFF_LEVEL_RENDER_CONFIGS.register(
            "sunset_core",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.SUNSET_CORE, LanguageItems.BUFF_SUNSET_CORE)
    );

    /**
     * 叠晖 - 显示层数
     */
    public static final RegistryObject<BuffLevelRenderConfig> SUNSET_STACK = BUFF_LEVEL_RENDER_CONFIGS.register(
            "sunset_stack",
            () -> new BuffLevelRenderConfig(RecastingBuffTypes.SUNSET_STACK, LanguageItems.BUFF_SUNSET_STACK)
    );

    /**
     * 断灭 - 显示层数
     */
    //public static final RegistryObject<BuffLevelRenderConfig> ANNIHILATION = BUFF_LEVEL_RENDER_CONFIGS.register(
    //        "annihilation",
    //        () -> new BuffLevelRenderConfig(RecastingBuffTypes.ANNIHILATION, LanguageItems.BUFF_ANNIHILATION)
    //);
}

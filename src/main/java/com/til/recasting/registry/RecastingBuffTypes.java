package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.registry.instance.BuffType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

/**
 * Buff类型注册表
 * 用于注册和管理不同类型的buff，支持扩展能力
 */
public final class RecastingBuffTypes {

    public static final ResourceKey<Registry<BuffType>> BUFF_TYPE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Recasting.prefix("buff_type"));

    public static final DeferredRegister<BuffType> BUFF_TYPES =
            DeferredRegister.create(BUFF_TYPE_REGISTRY_KEY, Recasting.MODID);

    public static final Registry<BuffType> REGISTRY =
            new RegistryBuilder<>(BUFF_TYPE_REGISTRY_KEY).sync(true).create();

    // TODO(P3): 从 1.20 批量移植预定义 BuffType 注册项

    private RecastingBuffTypes() {
    }
}

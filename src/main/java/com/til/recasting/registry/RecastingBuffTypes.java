package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.registry.instance.BuffType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
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

    /** 穷观阵占用标记（实体移除时清零）；完整逻辑待 P3。 */
    public static final DeferredHolder<BuffType, BuffType> MATRIX = BUFF_TYPES.register(
            "matrix",
            () -> new BuffType().setDecayInterval(1)
    );

    /** 群星坠落阵占用标记（实体移除时清零）；完整逻辑待 P3。 */
    public static final DeferredHolder<BuffType, BuffType> STARFALL = BUFF_TYPES.register(
            "starfall",
            () -> new BuffType().setDecayInterval(1)
    );

    /** 末辉永恒守卫；完整逻辑待 P3。 */
    public static final DeferredHolder<BuffType, BuffType> ETERNAL_GUARD = BUFF_TYPES.register(
            "eternal_guard",
            () -> new BuffType().setDecayInterval(1)
    );

    // TODO(P3): 从 1.20 批量移植其余预定义 BuffType 注册项

    private RecastingBuffTypes() {
    }
}

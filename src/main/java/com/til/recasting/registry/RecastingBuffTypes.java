package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.registry.instance.BuffType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Buff类型注册表
 * 用于注册和管理不同类型的buff，支持扩展能力
 */
public class RecastingBuffTypes {

    /**
     * Buff类型注册表键
     */
    public static final ResourceKey<Registry<BuffType>> BUFF_TYPE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Recasting.prefix("buff_type"));

    /**
     * Buff类型注册表
     */
    public static final DeferredRegister<BuffType> BUFF_TYPES =
            DeferredRegister.create(BUFF_TYPE_REGISTRY_KEY, Recasting.MODID);

    /**
     * Buff类型注册表实例
     */
    public static final Supplier<IForgeRegistry<BuffType>> REGISTRY =
            BUFF_TYPES.makeRegistry(() -> new RegistryBuilder<BuffType>()
                    .setDefaultKey(Recasting.prefix("default"))
            );

    // ==================== 预定义的Buff类型 ====================

    /**
     * 默认Buff类型
     * - 不衰减
     * - 无最大等级限制
     */
    public static final RegistryObject<BuffType> DEFAULT = BUFF_TYPES.register("default",
            () -> new BuffType(0, 0)
    );

    /**
     * 临时Buff类型
     * - 每1tick减少1级
     * - 无最大等级限制
     * - 适用于需要随时间自动消失的buff
     */
    public static final RegistryObject<BuffType> TEMPORARY = BUFF_TYPES.register("temporary",
            () -> new BuffType(1, 0)
    );

    /**
     * 快速衰减Buff类型
     * - 每1tick减少1级（与TEMPORARY相同，但语义上表示快速衰减）
     * - 无最大等级限制
     * - 适用于快速消失的buff
     */
    public static final RegistryObject<BuffType> FAST_DECAY = BUFF_TYPES.register("fast_decay",
            () -> new BuffType(1, 0)
    );

    /**
     * 慢速衰减Buff类型
     * - 每10tick减少1级
     * - 无最大等级限制
     * - 适用于持续时间较长的buff
     */
    public static final RegistryObject<BuffType> SLOW_DECAY = BUFF_TYPES.register("slow_decay",
            () -> new BuffType(10, 0)
    );

    /**
     * 有上限的Buff类型
     * - 不衰减
     * - 最大等级为10
     * - 适用于有上限的叠加buff
     */
    public static final RegistryObject<BuffType> CAPPED = BUFF_TYPES.register("capped",
            () -> new BuffType(0, 10)
    );

    /**
     * 有上限且衰减的Buff类型
     * - 每1tick减少1级
     * - 最大等级为5
     * - 适用于有上限且会衰减的buff
     */
    public static final RegistryObject<BuffType> CAPPED_DECAY = BUFF_TYPES.register("capped_decay",
            () -> new BuffType(1, 5)
    );

    /**
     * 注册自定义Buff类型
     * 
     * @param name Buff类型名称
     * @param decayPerTick 每过多少tick减少一级，0表示不衰减
     * @param maxLevel 最大等级，0表示无限制
     * @return 注册的BuffType
     */
    public static RegistryObject<BuffType> register(String name, int decayPerTick, int maxLevel) {
        return BUFF_TYPES.register(name, () -> new BuffType(decayPerTick, maxLevel));
    }
}


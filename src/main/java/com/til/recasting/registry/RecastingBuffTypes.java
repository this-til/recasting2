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
     * 星闪
     * - 用于星闪特效的层数累积
     */
    public static final RegistryObject<BuffType> STAR_BLINK = BUFF_TYPES.register("star_blink",
            () -> new BuffType(0, 5)
    );

    /**
     * 演算
     * - 每层提供10%增伤
     */
    public static final RegistryObject<BuffType> CALCULUS = BUFF_TYPES.register("calculus",
            () -> new BuffType(60, 16)
    );

    /**
     * 灵魂燃烧
     * - 每层提供6%当前生命值损伤
     */
    public static final RegistryObject<BuffType> SOUL_BURN = BUFF_TYPES.register("soul_burn",
            () -> new BuffType(60, 24)
    );

    /**
     * 破片
     * - 用于破片特效的层数累积
     */
    public static final RegistryObject<BuffType> FRAGMENT = BUFF_TYPES.register("fragment",
            () -> new BuffType(0, 64)
    );

    /**
     * 剑势
     * - 用于回旋特效的层数累积
     */
    public static final RegistryObject<BuffType> SWORD_MOMENTUM = BUFF_TYPES.register("sword_momentum",
            () -> new BuffType(0, 12)
    );
}
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
     * 星闪Buff类型
     * - 不衰减
     * - 最大等级为5
     * - 用于星闪特效的层数累积
     */
    public static final RegistryObject<BuffType> STAR_BLINK = BUFF_TYPES.register("star_blink",
            () -> new BuffType(0, 5)
    );

    /**
     *  演算Buff类型
     * - 衰减间隔为60 ticks（每60 tick减少一级）
     * - 最大等级为16
     * - 受到伤害时每层增加，每层提供10%伤害衰减
     */
    public static final RegistryObject<BuffType> CALCULUS = BUFF_TYPES.register("calculus",
            () -> new BuffType(60, 16)
    );

    /**
     * 注册自定义Buff类型
     * 
     * @param name Buff类型名称
     * @param decayInterval 衰减间隔（每过多少tick减少一级），0表示不衰减
     * @param maxLevel 最大等级，0表示无限制
     * @return 注册的BuffType
     */
    public static RegistryObject<BuffType> register(String name, int decayInterval, int maxLevel) {
        return BUFF_TYPES.register(name, () -> new BuffType(decayInterval, maxLevel));
    }
}


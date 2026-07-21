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
            () -> new BuffType(0, 4)
    );

    /**
     * 演算
     * - 每层提供5%增伤
     */
    public static final RegistryObject<BuffType> CALCULUS = BUFF_TYPES.register("calculus",
            () -> new BuffType(20, 16)
    );

    /**
     * 灵魂燃烧
     * - 每秒造成当前6%生命值的火属性伤害
     */
    public static final RegistryObject<BuffType> SOUL_BURN = BUFF_TYPES.register("soul_burn",
            () -> new BuffType(20, 99)
    );

    /**
     * 破片
     * - 用于破片特效的层数累积
     */
    public static final RegistryObject<BuffType> FRAGMENT = BUFF_TYPES.register("fragment",
            () -> new BuffType(5, 12)
    );

    /**
     * 剑势
     * - 用于回旋特效的层数累积
     */
    public static final RegistryObject<BuffType> SWORD_MOMENTUM = BUFF_TYPES.register("sword_momentum",
            () -> new BuffType(10, 12)
    );

    /**
     * 电离
     * - 受到闪电伤害时叠加，每层提供1%增伤
     */
    public static final RegistryObject<BuffType> IONIZATION = BUFF_TYPES.register("ionization",
            () -> new BuffType(20, 64)
    );

    /**
     * 蓄能
     * - 受到伤害时叠加层数，达到48层时触发闪电攻击
     */
    public static final RegistryObject<BuffType> ENERGY_STORAGE = BUFF_TYPES.register("energy_storage",
            () -> new BuffType(20, 12)
    );

    /**
     * 雷光
     * - 受到雷电伤害后获得，持有雷光的实体受到伤害后附加闪电伤害
     */
    public static final RegistryObject<BuffType> THUNDER_LIGHT = BUFF_TYPES.register("thunder_light",
            () -> new BuffType(20, 8)
    );

    /**
     * 撕裂
     * - 用于撕裂特效的层数累积
     */
    public static final RegistryObject<BuffType> TEAR = BUFF_TYPES.register("tear",
            () -> new BuffType(20, 12)
    );

    /**
     * 断灭
     * - 用于断灭特效的次元斩计数累积
     */
    public static final RegistryObject<BuffType> ANNIHILATION = BUFF_TYPES.register("annihilation",
            () -> new BuffType(0, 7)
    );

    /**
     * 光子灼痕
     * - 衰减间隔 100 tick；最大 9 层
     * - 灼烧状态下叠层；满层释放短光束并清零
     */
    public static final RegistryObject<BuffType> PHOTON_SCAR = BUFF_TYPES.register("photon_scar",
            () -> new BuffType(100, 9)
    );

    /**
     * 光子灼烧
     * - 衰减间隔 60 tick；最大 50 层
     * - 激光叠层；持续火焰伤害与全伤害增伤均基于此层数
     */
    public static final RegistryObject<BuffType> PHOTON_BURN = BUFF_TYPES.register("photon_burn",
            () -> new BuffType(60, 50)
    );

    /**
     * 日核
     * - 不随时间衰减；最大 50 层
     * - 仅长空落日 SA 幻影剑命中叠加；其它幻影剑触发晖光时消耗
     */
    public static final RegistryObject<BuffType> SUNSET_CORE = BUFF_TYPES.register("sunset_core",
            () -> new BuffType(0, 50)
    );

    /**
     * 叠晖
     * - 衰减间隔 100 tick；最大 50 层
     * - 有日核时受幻影剑伤害叠加；满层幻影剑伤害翻倍
     */
    public static final RegistryObject<BuffType> SUNSET_STACK = BUFF_TYPES.register("sunset_stack",
            () -> new BuffType(100, 50)
    );

    /**
     * 金戈
     * - 衰减间隔 40 tick；最大 12 层
     * - 斩击命中叠加；满层引爆小范围伤害
     */
    public static final RegistryObject<BuffType> GOLDEN_HALBERD = BUFF_TYPES.register("golden_halberd",
            () -> new BuffType(40, 12)
    );

    /**
     * 茶韵
     * - 不随时间自动衰减（到期由 SE 的 TIME_RUN 结算并清零）；无层数上限
     * - 层数 = 延迟伤害 × 10（不足 1 记为 1）
     */
    public static final RegistryObject<BuffType> TEA_AROMA = BUFF_TYPES.register("tea_aroma",
            () -> new BuffType(0, 0)
    );

    /**
     * 静电余韵 · 附加伤害冷却
     * - 衰减间隔 1 tick；层数 = 剩余冷却 tick（记录在受击目标）
     */
    public static final RegistryObject<BuffType> STATIC_AFTERGLOW_DAMAGE_CD = BUFF_TYPES.register("static_afterglow_damage_cd",
            () -> new BuffType(1, 0)
    );

    /**
     * 静电余韵 · 闪电链冷却
     * - 衰减间隔 1 tick；层数 = 剩余冷却 tick（记录在起始目标）
     */
    public static final RegistryObject<BuffType> STATIC_AFTERGLOW_CHAIN_CD = BUFF_TYPES.register("static_afterglow_chain_cd",
            () -> new BuffType(1, 0)
    );

}
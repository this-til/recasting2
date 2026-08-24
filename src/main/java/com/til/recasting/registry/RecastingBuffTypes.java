package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.registry.buff.BuffSuppressBuffType;
import com.til.recasting.registry.buff.CalculusBuffType;
import com.til.recasting.registry.buff.JadeFireBuffType;
import com.til.recasting.registry.buff.MortalDustBuffType;
import com.til.recasting.registry.buff.PhotonBurnBuffType;
import com.til.recasting.registry.buff.PhotonScarBuffType;
import com.til.recasting.registry.buff.SoulBurnBuffType;
import com.til.recasting.registry.buff.SpiritSilenceBuffType;
import com.til.recasting.registry.buff.SunsetStackBuffType;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.registry.sa.TimeBeyondSlashArts;
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

    public static final ResourceKey<Registry<BuffType>> BUFF_TYPE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Recasting.prefix("buff_type"));

    public static final DeferredRegister<BuffType> BUFF_TYPES =
            DeferredRegister.create(BUFF_TYPE_REGISTRY_KEY, Recasting.MODID);

    public static final Supplier<IForgeRegistry<BuffType>> REGISTRY =
            BUFF_TYPES.makeRegistry(() -> new RegistryBuilder<BuffType>()
                    .setDefaultKey(Recasting.prefix("default"))
            );

    // ==================== 预定义的Buff类型 ====================

    /**
     * 星闪
     */
    public static final RegistryObject<BuffType> STAR_BLINK = BUFF_TYPES.register("star_blink",
            () -> new BuffType().setMaxLevel(4)
    );

    /**
     * 演算
     */
    public static final RegistryObject<CalculusBuffType> CALCULUS = BUFF_TYPES.register("calculus",
            CalculusBuffType::new
    );

    /**
     * 穷观阵（层=剩余 tick）
     */
    public static final RegistryObject<BuffType> MATRIX = BUFF_TYPES.register("matrix",
            () -> new BuffType().setDecayInterval(1)
    );

    /**
     * 灵魂燃烧
     */
    public static final RegistryObject<SoulBurnBuffType> SOUL_BURN = BUFF_TYPES.register("soul_burn",
            SoulBurnBuffType::new
    );

    /**
     * 破片
     */
    public static final RegistryObject<BuffType> FRAGMENT = BUFF_TYPES.register("fragment",
            () -> new BuffType().setDecayInterval(5).setMaxLevel(12)
    );

    /**
     * 剑势
     */
    public static final RegistryObject<BuffType> SWORD_MOMENTUM = BUFF_TYPES.register("sword_momentum",
            () -> new BuffType().setDecayInterval(10).setMaxLevel(12)
    );

    /**
     * 回旋冷却
     */
    public static final RegistryObject<BuffType> SPIRAL_COOLDOWN = BUFF_TYPES.register("spiral_cooldown",
            () -> new BuffType().setDecayInterval(1)
    );

    /**
     * 电离
     */
    public static final RegistryObject<BuffType> IONIZATION = BUFF_TYPES.register("ionization",
            () -> new BuffType().setDecayInterval(20).setMaxLevel(64)
    );

    /**
     * 蓄能
     */
    public static final RegistryObject<BuffType> ENERGY_STORAGE = BUFF_TYPES.register("energy_storage",
            () -> new BuffType().setDecayInterval(20).setMaxLevel(12)
    );

    /**
     * 雷光
     */
    public static final RegistryObject<BuffType> THUNDER_LIGHT = BUFF_TYPES.register("thunder_light",
            () -> new BuffType().setDecayInterval(20).setMaxLevel(8)
    );

    /**
     * 撕裂
     */
    public static final RegistryObject<BuffType> TEAR = BUFF_TYPES.register("tear",
            () -> new BuffType().setDecayInterval(20).setMaxLevel(12)
    );

    /**
     * 断灭
     */
    public static final RegistryObject<BuffType> ANNIHILATION = BUFF_TYPES.register("annihilation",
            () -> new BuffType().setDecayInterval(100).setMaxLevel(7)
    );

    /**
     * 光子灼痕
     */
    public static final RegistryObject<PhotonScarBuffType> PHOTON_SCAR = BUFF_TYPES.register("photon_scar",
            PhotonScarBuffType::new
    );

    /**
     * 光子灼烧
     */
    public static final RegistryObject<PhotonBurnBuffType> PHOTON_BURN = BUFF_TYPES.register("photon_burn",
            PhotonBurnBuffType::new
    );

    /**
     * 日核
     */
    public static final RegistryObject<BuffType> SUNSET_CORE = BUFF_TYPES.register("sunset_core",
            BuffType::new
    );

    /**
     * 叠晖
     */
    public static final RegistryObject<SunsetStackBuffType> SUNSET_STACK = BUFF_TYPES.register("sunset_stack",
            SunsetStackBuffType::new
    );

    /**
     * 金戈
     */
    public static final RegistryObject<BuffType> GOLDEN_HALBERD = BUFF_TYPES.register("golden_halberd",
            () -> new BuffType().setDecayInterval(40).setMaxLevel(12)
    );

    /**
     * 黑色玫瑰
     */
    public static final RegistryObject<BuffType> BLACK_ROSE = BUFF_TYPES.register("black_rose",
            BuffType::new
    );

    /**
     * 茶韵
     */
    public static final RegistryObject<BuffType> TEA_AROMA = BUFF_TYPES.register("tea_aroma",
            BuffType::new
    );

    /**
     * 云界领域（层=剩余 tick）
     */
    public static final RegistryObject<BuffType> JADE_DOMAIN = BUFF_TYPES.register("jade_domain",
            () -> new BuffType().setDecayInterval(1)
    );

    /**
     * 翠火
     */
    public static final RegistryObject<JadeFireBuffType> JADE_FIRE = BUFF_TYPES.register("jade_fire",
            JadeFireBuffType::new
    );

    /**
     * 静电余韵 · 附加伤害冷却
     */
    public static final RegistryObject<BuffType> STATIC_AFTERGLOW_DAMAGE_CD = BUFF_TYPES.register("static_afterglow_damage_cd",
            () -> new BuffType().setDecayInterval(1)
    );

    /**
     * 静电余韵 · 闪电链冷却
     */
    public static final RegistryObject<BuffType> STATIC_AFTERGLOW_CHAIN_CD = BUFF_TYPES.register("static_afterglow_chain_cd",
            () -> new BuffType().setDecayInterval(1)
    );

    /**
     * 宝具连发冷却
     */
    public static final RegistryObject<BuffType> TREASURE_BARRAGE_COOLDOWN = BUFF_TYPES.register("treasure_barrage_cooldown",
            () -> new BuffType().setDecayInterval(1)
    );

    /**
     * 犬缘冷却
     */
    public static final RegistryObject<BuffType> DOG_BOND_COOLDOWN = BUFF_TYPES.register("dog_bond_cooldown",
            () -> new BuffType().setDecayInterval(1)
    );

    /**
     * 咒令（层=剩余 tick）
     */
    public static final RegistryObject<BuffType> CURSE_DECREE = BUFF_TYPES.register("curse_decree",
            () -> new BuffType().setDecayInterval(1)
    );

    /**
     * 寂灭
     */
    public static final RegistryObject<SpiritSilenceBuffType> SPIRIT_SILENCE = BUFF_TYPES.register("spirit_silence",
            SpiritSilenceBuffType::new
    );

    /**
     * 增益压制
     */
    public static final RegistryObject<BuffSuppressBuffType> BUFF_SUPPRESS = BUFF_TYPES.register("buff_suppress",
            BuffSuppressBuffType::new
    );

    /**
     * 静滞（永恒守卫，层=剩余 tick）
     */
    public static final RegistryObject<BuffType> ETERNAL_GUARD = BUFF_TYPES.register("eternal_guard",
            () -> new BuffType().setDecayInterval(1)
    );

    /**
     * 红尘（红尘滚滚）
     */
    public static final RegistryObject<MortalDustBuffType> MORTAL_DUST = BUFF_TYPES.register("mortal_dust",
            MortalDustBuffType::new
    );

    /**
     * 红尘受击固伤冷却
     */
    public static final RegistryObject<BuffType> MORTAL_DUST_PROC_CD = BUFF_TYPES.register("mortal_dust_proc_cd",
            () -> new BuffType().setDecayInterval(1)
    );

    /**
     * 群星坠落阵（层=剩余 tick）
     */
    public static final RegistryObject<BuffType> STARFALL = BUFF_TYPES.register("starfall",
            () -> new BuffType().setDecayInterval(1)
    );

    /**
     * 电涌
     */
    public static final RegistryObject<BuffType> ELECTRIC_SURGE = BUFF_TYPES.register("electric_surge",
            () -> new BuffType().setDecayInterval(100).setMaxLevel(64)
    );

    /**
     * 时之彼端蓄力（层=已蓄力 tick，上限 MAX_CHARGE_TICKS）
     */
    public static final RegistryObject<BuffType> TIME_BEYOND_CHARGE = BUFF_TYPES.register("time_beyond_charge",
            () -> new BuffType().setMaxLevel(TimeBeyondSlashArts.MAX_CHARGE_TICKS)
    );

    /**
     * 异界斩切冷却
     */
    public static final RegistryObject<BuffType> OTHERWORLD_SLASH_CD = BUFF_TYPES.register("otherworld_slash_cd",
            () -> new BuffType().setDecayInterval(1)
    );

}

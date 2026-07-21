package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.registry.se.*;
import com.til.recasting.util.NumberPack;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Special Effects (SE) 注册表
 */
public class SpecialEffectsRegistry {
    /**
     * 创建 DeferredRegister，用于注册 Special Effects
     */
    public static final DeferredRegister<SpecialEffect> SPECIAL_EFFECT = DeferredRegister.create(SpecialEffect.REGISTRY_KEY, Recasting.MODID);

    // 协同 - 挥刀时概率额外挥刀
    public static final RegistryObject<SpecialEffect> COOPERATE_WITH = registerExtendedSE("cooperate_with", CooperateWithSpecialEffect::new);
    // 十字斩 - 挥刀时追加一道剑气
    public static final RegistryObject<SpecialEffect> CROSS_CHOP = registerExtendedSE("cross_chop", CrossChopSpecialEffect::new);
    // 剑气释放 - 挥刀时有概率发出剑气
    public static final RegistryObject<SpecialEffect> DRIVE_RELEASE = registerExtendedSE("drive_release", DriveReleaseSpecialEffect::new);
    // 生长 - 挥刀时恢复生命
    public static final RegistryObject<SpecialEffect> GROWTH = registerExtendedSE("growth", GrowthSpecialEffect::new);
    // 吸血转化 - 将攻击伤害的一部分转化为生命恢复
    public static final RegistryObject<SpecialEffect> LIFE_STEAL = registerExtendedSE("life_steal", LifeStealSpecialEffect::new);
    // 回溯 - 挥刀时恢复耐久
    public static final RegistryObject<SpecialEffect> REGRESSION = registerExtendedSE("regression", RegressionSpecialEffect::new);
    // 断罪 - 触发SA时追加次元斩攻击
    public static final RegistryObject<SpecialEffect> JUDGEMENT = registerExtendedSE("judgement", JudgementSpecialEffect::new);
    // 雷暴 - 触发SA时，在目标位置召唤多道闪电
    public static final RegistryObject<SpecialEffect> THUNDERSTORM = registerExtendedSE("thunderstorm", ThunderstormSpecialEffect::new);
    // 雷神之怒 - 击杀敌人时，在死亡位置召唤强力闪电
    public static final RegistryObject<SpecialEffect> THUNDER_GODS_WRATH = registerExtendedSE("thunder_gods_wrath", ThunderGodsWrathSpecialEffect::new);
    // 电离 - 受到雷电伤害时叠加电离buff，每层提供1%增伤
    public static final RegistryObject<SpecialEffect> IONIZATION = registerExtendedSE("ionization", IonizationSpecialEffect::new);
    // 蓄能 - 受到伤害后叠加层数，到48层时造成一道闪电攻击目标
    public static final RegistryObject<SpecialEffect> ENERGY_STORAGE = registerExtendedSE("energy_storage", EnergyStorageSpecialEffect::new);
    // 雷云 - 目标受到雷电伤害后获得4层雷光buff，持有雷光的实体受到伤害后附加闪电伤害
    public static final RegistryObject<SpecialEffect> THUNDER_CLOUD = registerExtendedSE("thunder_cloud", ThunderCloudSpecialEffect::new);
    // 冲击 - 造成伤害有几率召唤幻影剑造成瞬间伤害
    public static final RegistryObject<SpecialEffect> IMPACT = registerExtendedSE("impact", ImpactSpecialEffect::new);
    // 过载 - 挥刀时小概率触发次元斩
    public static final RegistryObject<SpecialEffect> OVERLOAD = registerExtendedSE("overload", OverloadSpecialEffect::new);
    // 抵抗 - 挥刀时获得伤害吸收
    public static final RegistryObject<SpecialEffect> RESIST = registerExtendedSE("resist", ResistSpecialEffect::new);
    // 断却 - 触发次元斩之后造成一次大伤害和大范围的劈砍
    public static final RegistryObject<SpecialEffect> SEVER_BREAK = registerExtendedSE("sever_break", SeverBreakSpecialEffect::new);
    // 风暴 - 触发审判时，召唤幻影剑进行攻击
    public static final RegistryObject<SpecialEffect> STORM = registerExtendedSE("storm", StormSpecialEffect::new);
    // 风暴.变体 - 触发审判时，从上方召唤幻影剑进行攻击
    public static final RegistryObject<SpecialEffect> STORM_VARIANT = registerExtendedSE("storm_variant", StormVariantSpecialEffect::new);
    // 分裂 - 挥刀时发射幻影剑进行辅助攻击
    public static final RegistryObject<SpecialEffect> SPLIT = registerExtendedSE("split", SplitSpecialEffect::new);
    // 回旋 - 幻影剑造成伤害后叠加剑势，达到一定层数后触发风暴幻影剑
    public static final RegistryObject<SpecialEffect> SPIRAL = registerExtendedSE("spiral", SpiralSpecialEffect::new);
    // 破片 - 幻影剑造成伤害时叠加层级，达到一定层级时额外造成一次大量的伤害
    public static final RegistryObject<SpecialEffect> FRAGMENT = registerExtendedSE("fragment", FragmentSpecialEffect::new);
    // 撕裂 - 次元斩造成伤害后叠加层数，满层级后造成额外的伤害
    public static final RegistryObject<SpecialEffect> TEAR = registerExtendedSE("tear", TearSpecialEffect::new);
    // 旋风 - 你的次元斩将允许造成重复的伤害
    public static final RegistryObject<SpecialEffect> WHIRLWIND = registerExtendedSE("whirlwind", WhirlwindSpecialEffect::new);
    // 断灭 - 召唤一定数量的次元斩之后额外召唤一个巨型次元斩
    public static final RegistryObject<SpecialEffect> ANNIHILATION = registerExtendedSE("annihilation", AnnihilationSpecialEffect::new);

    // ==================== 攻击类型增幅 SE ====================

    // 太虚 - 幻影剑增幅
    public static final RegistryObject<SpecialEffect> GREAT_VOID = registerExtendedSE("great_void", () -> new AttackAmplifierSpecialEffect(RecastingAttackTypes.SUMMOND_SWORD_ATTACK, new NumberPack(0.1f, 0.1f)));
    // 利刃 - 斩击增幅
    public static final RegistryObject<SpecialEffect> SHARP_BLADE = registerExtendedSE("sharp_blade", () -> new AttackAmplifierSpecialEffect(RecastingAttackTypes.SLASH_EFFECT_ATTACK, new NumberPack(0.1f, 0.1f)));
    // 震荡 - 次元斩增幅
    public static final RegistryObject<SpecialEffect> SHOCK = registerExtendedSE("shock", () -> new AttackAmplifierSpecialEffect(RecastingAttackTypes.JUDGEMENT_CUT_ATTACK, new NumberPack(0.2f, 0.15f)));
    // 剑气纵横 - 剑气增幅
    public static final RegistryObject<SpecialEffect> SWORD_QI_MASTERY = registerExtendedSE("sword_qi_mastery", () -> new AttackAmplifierSpecialEffect(RecastingAttackTypes.DRIVE_ATTACK, new NumberPack(0.2f, 0.15f)));
    // 雷霆万钧 - 闪电增幅
    public static final RegistryObject<SpecialEffect> THUNDER_STRIKE = registerExtendedSE("thunder_strike", () -> new AttackAmplifierSpecialEffect(RecastingAttackTypes.LIGHTNING_ATTACK, new NumberPack(0.2f, 0.15f)));

    // ==================== 特殊刀 SE ====================
    // 黑色玫瑰 - 叠加伤害，每 tick 造成伤害，伤害减半
    public static final RegistryObject<SpecialEffect> BLACK_ROSE = registerExtendedSE("black_rose", () -> new BlackRoseSpecialEffect().setMaxLevel(1).setSpecial(true));
    // 星闪 - 攻击目标叠加层数，达到最大层数时触发额外伤害并重置目标速度
    public static final RegistryObject<SpecialEffect> STAR_BLINK = registerExtendedSE("star_blink", () -> new StarBlinkSpecialEffect().setMaxLevel(1).setSpecial(true));
    public static final RegistryObject<SpecialEffect> STAR_BLINK_LAMBDA = registerExtendedSE("star_blink_lambda", () -> new StarBlinkSpecialEffect().setAddLevel(2).setMaxLevel(1).setSpecial(true));
    // 染色 - 挥刀时更改刀刃颜色为随机的
    public static final RegistryObject<SpecialEffect> COLOR_DYE = registerExtendedSE("color_dye", () -> new ColorDyeSpecialEffect().setMaxLevel(1).setSpecial(true));
    // 解算 - SE攻击带有演算buff的目标时消耗一层演算，造附加伤害
    public static final RegistryObject<SpecialEffect> RESOLVE = registerExtendedSE("resolve", () -> new ResolveSpecialEffect().setMaxLevel(1).setSpecial(true));
    public static final RegistryObject<SpecialEffect> RESOLVE_LAMBDA = registerExtendedSE("resolve_lambda", () -> new ResolveSpecialEffect().setDamageRatio(1.5f).setMaxLevel(1).setSpecial(true));
    // 燃沫 - 攻击处于灵魂燃烧的目标时，使其额外受到当前生命比值的额外伤害，并有概率增加一层灵魂燃烧
    public static final RegistryObject<SpecialEffect> FLAME_FOAM = registerExtendedSE("flame_foam", () -> new FlameFoamSpecialEffect().setMaxLevel(1).setSpecial(true));

    // 金戈 - 斩击命中叠加层数，满层引爆小范围额外伤害
    public static final RegistryObject<SpecialEffect> GOLDEN_HALBERD = registerExtendedSE("golden_halberd", () -> new GoldenHalberdSpecialEffect().setMaxStacks(12).setBurstRatio(1.5f).setStacksPerHit(1).setMaxLevel(1).setSpecial(true));
    public static final RegistryObject<SpecialEffect> GOLDEN_HALBERD_LAMBDA = registerExtendedSE("golden_halberd_lambda", () -> new GoldenHalberdSpecialEffect().setMaxStacks(12).setBurstRatio(2.0f).setStacksPerHit(2).setMaxLevel(1).setSpecial(true));
    // 茶韵 - 命中储存部分伤害延迟释放；剑气额外叠层；连续命中累加并刷新倒计时
    public static final RegistryObject<SpecialEffect> TEA_AROMA = registerExtendedSE("tea_aroma", () -> new TeaAromaSpecialEffect().setStoreRatio(0.2f).setDelayTicks(30).setDriveBonusStacks(10).setMaxLevel(1).setSpecial(true));
    public static final RegistryObject<SpecialEffect> TEA_AROMA_LAMBDA = registerExtendedSE("tea_aroma_lambda", () -> new TeaAromaSpecialEffect().setStoreRatio(0.3f).setDelayTicks(30).setDriveBonusStacks(15).setMaxLevel(1).setSpecial(true));

    // 光子灼痕 - SE 只叠灼烧；灼痕叠层与满层光束由 Handler 在灼烧状态下处理；三档冷却
    public static final RegistryObject<SpecialEffect> PHOTON_SCAR = registerExtendedSE("photon_scar", () -> new PhotonScarSpecialEffect().setMaxLevel(1).setSpecial(true));
    public static final RegistryObject<SpecialEffect> PHOTON_SCAR_2 = registerExtendedSE("photon_scar_2", () -> new PhotonScarSpecialEffect().setMaxLevel(1).setSpecial(true));
    public static final RegistryObject<SpecialEffect> PHOTON_SCAR_3 = registerExtendedSE("photon_scar_3", () -> new PhotonScarSpecialEffect().setMaxLevel(1).setSpecial(true));

    // 长空落日 - Shift 锁敌持续幻影剑；伤害随层数提升
    public static final RegistryObject<SpecialEffect> LONG_SKY_SUNSET = registerExtendedSE("long_sky_sunset", () -> new LongSkySunsetSpecialEffect().setMaxLevel(1).setSpecial(true));

    // 击晕 - 命中目标短暂击晕
    public static final RegistryObject<SpecialEffect> STUN = registerExtendedSE("stun", () -> new StunSpecialEffect().setMaxLevel(1).setSpecial(true));
    // 催熟 - 挥刀时对周围随机作物施加骨粉效果
    public static final RegistryObject<SpecialEffect> FERTILIZE = registerExtendedSE("fertilize", () -> new FertilizeSpecialEffect().setMaxLevel(1).setSpecial(true));
    // 吃 - 挥刀时消耗耐久，恢复饱和度
    public static final RegistryObject<SpecialEffect> EAT = registerExtendedSE("eat", () -> new EatSpecialEffect().setMaxLevel(1).setSpecial(true));
    // 变大！ - 挥刀时大幅增加攻击范围
    public static final RegistryObject<SpecialEffect> ENLARGE = registerExtendedSE("enlarge", () -> new EnlargeSpecialEffect().setMaxLevel(1).setSpecial(true));

    // 静电余韵 - 释放SA后一段时间内造成伤害附带雷电附加伤害，造成雷电伤害有概率触发闪电链
    public static final RegistryObject<SpecialEffect> STATIC_AFTERGLOW = registerExtendedSE("static_afterglow", () -> new StaticAfterglowSpecialEffect().setMaxLevel(1).setSpecial(true));
    public static final RegistryObject<SpecialEffect> STATIC_AFTERGLOW_LAMBDA = registerExtendedSE("static_afterglow_lambda", () -> new StaticAfterglowSpecialEffect()
            .setDurationTick(300)
            .setLightningDamageRatio(0.4f)
            .setDamageCooldownTick(3)
            .setChainChance(0.15f)
            .setChainCooldownTick(10)
            .setMaxLevel(1)
            .setSpecial(true));

    public static RegistryObject<SpecialEffect> registerExtendedSE(String name, Supplier<SpecialEffect> factory) {
        return SPECIAL_EFFECT.register(name, factory);
    }
}

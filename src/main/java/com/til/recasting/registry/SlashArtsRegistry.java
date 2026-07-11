package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.registry.sa.*;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Slash Arts (SA) 注册表
 */
public class SlashArtsRegistry {
    /**
     * 创建 DeferredRegister，用于注册 Slash Arts
     */
    public static final DeferredRegister<SlashArts> SLASH_ARTS = DeferredRegister.create(
            SlashArts.REGISTRY_KEY,
            Recasting.MODID
    );

    // 碎段
    public static final RegistryObject<ExtendedSlashArts> FRAGMENT = registerExtendedSA("fragment", new FragmentSlashArts());

    // 青芒
    public static final RegistryObject<ExtendedSlashArts> CYAN_GLOW = registerExtendedSA("cyan_glow", new CyanGlowSlashArts());
    public static final RegistryObject<ExtendedSlashArts> CYAN_GLOW_LAMBDA = registerExtendedSA("cyan_glow_lambda", new CyanGlowSlashArts());

    // 乱舞
    public static final RegistryObject<ExtendedSlashArts> FANATICAL_DANCE = registerExtendedSA("fanatical_dance", new FanaticalDanceSlashArts());
    public static final RegistryObject<ExtendedSlashArts> FANATICAL_DANCE_LAMBDA = registerExtendedSA("fanatical_dance_lambda", new FanaticalDanceSlashArts().setAttackNumber(21).setAttackDeviation(4).setHit(0.6f));

    // 风暴幻影剑
    public static final RegistryObject<ExtendedSlashArts> STORM_PHANTOM_SWORDS = registerExtendedSA("storm_phantom_swords", new StormPhantomSwordsSlashArts());
    public static final RegistryObject<ExtendedSlashArts> STORM_PHANTOM_SWORDS_LAMBDA = registerExtendedSA("storm_phantom_swords_lambda", new StormPhantomSwordsSlashArts().setNumber(24));

    // 剑雨
    public static final RegistryObject<ExtendedSlashArts> SWORD_RAIN = registerExtendedSA("sword_rain", new SwordRainSlashArts());
    public static final RegistryObject<ExtendedSlashArts> SWORD_RAIN_LAMBDA = registerExtendedSA("sword_rain_lambda", new SwordRainSlashArts().setConcentrate(true));

    // 拟似黑洞
    public static final RegistryObject<ExtendedSlashArts> VOID_HOLE = registerExtendedSA("void_hole", new VoidHoleSlashArts());
    public static final RegistryObject<ExtendedSlashArts> VOID_HOLE_PITCH_BLACK = registerExtendedSA("void_hole_pitch_black", new VoidHoleSlashArts().setLife(40).setRange(45).setPower(0.02f));
    public static final RegistryObject<ExtendedSlashArts> VOID_HOLE_FISHY_RED = registerExtendedSA("void_hole_fishy_red", new VoidHoleSlashArts().setLife(80).setRange(64).setPower(0.02f));

    // 多重次元斩·决
    public static final RegistryObject<ExtendedSlashArts> MULTIPLE_JUDGEMENT_CUT = registerExtendedSA("multiple_judgement_cut", new MultipleJudgementCutSlashArts());
    // 无限次元斩
    public static final RegistryObject<ExtendedSlashArts> INFINITE_JUDGEMENT_CUT = registerExtendedSA("infinite_judgement_cut", new InfiniteJudgementCutSlashArts());

    // 星
    public static final RegistryObject<ExtendedSlashArts> STAR_1 = registerExtendedSA("star_1", new StarSlashArts());
    public static final RegistryObject<ExtendedSlashArts> STAR_2 = registerExtendedSA("star_2", new StarSlashArts().setAttackNumber(8).setRange(16));
    public static final RegistryObject<ExtendedSlashArts> STAR_3 = registerExtendedSA("star_3", new StarSlashArts().setAttackNumber(12).setRange(20));
    public static final RegistryObject<ExtendedSlashArts> STAR_4 = registerExtendedSA("star_4", new StarSlashArts().setAttackNumber(16).setRange(32));
    public static final RegistryObject<ExtendedSlashArts> STAR_4_LAMBDA = registerExtendedSA("star_4_lambda", new StarSlashArts().setAttackNumber(24).setRange(32).setZoneNumber(5));

    // 多重剑气
    public static final RegistryObject<ExtendedSlashArts> MULTIPLE_DRIVE = registerExtendedSA("multiple_drive", new MultipleDriveSlashArts());
    public static final RegistryObject<ExtendedSlashArts> MULTIPLE_DRIVE_LAMBDA = registerExtendedSA("multiple_drive_lambda", new MultipleDriveSlashArts().setAttack(8).setAttack(0.2f));

    // 引雷
    public static final RegistryObject<ExtendedSlashArts> LIGHTNING_CALL = registerExtendedSA("lightning_call", new LightningCallSlashArts());

    // 苍穹十二连
    public static final RegistryObject<ExtendedSlashArts> HEAVEN_TWELVE_HIT = registerExtendedSA("heaven_twelve_hit", new HeavenTwelveHitSlashArts());
    public static final RegistryObject<ExtendedSlashArts> HEAVEN_TWELVE_HIT_LAMBDA = registerExtendedSA("heaven_twelve_hit_lambda", new HeavenTwelveHitSlashArts().setLightningNumber(18).setLightningAttack(1.3f).setAttack(0.5f));

    // 云轮
    public static final RegistryObject<ExtendedSlashArts> CLOUD_WHEEL = registerExtendedSA("cloud_wheel", new CloudWheelSlashArts().setLightningNumber(0));
    // 云轮风暴
    public static final RegistryObject<ExtendedSlashArts> CLOUD_WHEEL_STORM = registerExtendedSA("cloud_wheel_storm", new CloudWheelSlashArts().setLightningNumber(7).setAttackNumber(10));

    //星旋
    public static final RegistryObject<ExtendedSlashArts> STELLAR_ROTATION = registerExtendedSA("stellar_rotation", new StellarRotationSlashArts());

    // 急行幻影剑
    public static final RegistryObject<ExtendedSlashArts> RAPID_PHANTOM_SWORDS = registerExtendedSA("rapid_phantom_swords", new RapidPhantomSwordsSlashArts());

    // 穷观阵
    public static final RegistryObject<ExtendedSlashArts> MATRIX = registerExtendedSA("matrix", new MatrixSlashArts());
    public static final RegistryObject<ExtendedSlashArts> MATRIX_LAMBDA = registerExtendedSA("matrix_lambda", new MatrixSlashArts().setAttackInterval(5));

    // 幻影爆破
    public static final RegistryObject<ExtendedSlashArts> PHANTOM_EXPLOSION = registerExtendedSA("phantom_explosion", new PhantomExplosionSlashArts());
    public static final RegistryObject<ExtendedSlashArts> PHANTOM_EXPLOSION_LAMBDA = registerExtendedSA("phantom_explosion_lambda", new PhantomExplosionSlashArts().setGroupCount(5));

    // 无限剑制
    public static final RegistryObject<ExtendedSlashArts> UNLIMITED_BLADE_WORKS = registerExtendedSA("unlimited_blade_works", new UnlimitedBladeWorksSlashArts());
    public static final RegistryObject<ExtendedSlashArts> UNLIMITED_BLADE_WORKS_LAMBDA = registerExtendedSA("unlimited_blade_works_lambda", new UnlimitedBladeWorksSlashArts().setAttack(0.06f));

    // 剑刃风暴
    public static final RegistryObject<ExtendedSlashArts> BLADE_STORM = registerExtendedSA("blade_storm", new BladeStormSlashArts());
    public static final RegistryObject<ExtendedSlashArts> BLADE_STORM_LAMBDA = registerExtendedSA("blade_storm_lambda", new BladeStormSlashArts().setTotalSwords(256));

    // 斩铁式·极
    public static final RegistryObject<ExtendedSlashArts> ZANTETSUDEN_MAX = registerExtendedSA("zantetsuden_max", new ZantetsudenMaxSlashArts());
    public static final RegistryObject<ExtendedSlashArts> ZANTETSUDEN_MAX_LAMBDA = registerExtendedSA("zantetsuden_max_lambda", new ZantetsudenMaxSlashArts().setAttackNumber(40));

    // 斩铁式·行
    public static final RegistryObject<ExtendedSlashArts> ZANTETSUDEN_ROW = registerExtendedSA("zantetsuden_row", new ZantetsudenRowSlashArts());
    public static final RegistryObject<ExtendedSlashArts> ZANTETSUDEN_ROW_LAMBDA = registerExtendedSA("zantetsuden_row_lambda", new ZantetsudenRowSlashArts().setDriveNumber(40));

    // 业火
    public static final RegistryObject<ExtendedSlashArts> INFERNO = registerExtendedSA("inferno", new InfernoSlashArts());
    public static final RegistryObject<ExtendedSlashArts> INFERNO_LAMBDA = registerExtendedSA("inferno_lambda", new InfernoSlashArts().setSoulBurnLevel(6));

    // 光棱（红警2 光棱坦克：延迟脉冲 + 头顶发射/自动索敌 + 锁定散射）
    public static final RegistryObject<ExtendedSlashArts> LASER_1 = registerExtendedSA(
            "laser_1",
            new LaserBeamSlashArts().setBeamCount(1).setScatterCount(5).setAttack(0.5f).setScatterAttack(0.15f)
    );
    public static final RegistryObject<ExtendedSlashArts> LASER_2 = registerExtendedSA(
            "laser_2",
            new LaserBeamSlashArts()
                    .setBeamCount(3)
                    .setDelay(4)
                    .setRange(48f)
                    .setScatterCount(5)
                    .setSecondaryScatterCount(3)
                    .setAttack(0.35f)
                    .setScatterAttack(0.125f)
    );
    public static final RegistryObject<ExtendedSlashArts> LASER_3 = registerExtendedSA(
            "laser_3",
            new LaserBeamSlashArts()
                    .setBeamCount(5)
                    .setDelay(3)
                    .setRange(72f)
                    .setScatterCount(5)
                    .setSecondaryScatterCount(3)
                    .setAttack(0.25f)
                    .setScatterAttack(0.14f)
    );

    /**
     * 注册扩展的 SlashArts，自动关联 ComboState
     */
    private static RegistryObject<ExtendedSlashArts> registerExtendedSA(String name, ExtendedSlashArts supplier) {

        RegistryObject<ExtendedSlashArts> slashArtsRegistryObject = SLASH_ARTS.register(
                name,
                () -> supplier
        );

        RecastingComboStateRegistry.COMBO_STATE.register(
                name,
                supplier::createComboState
        );

        return slashArtsRegistryObject;
    }
}

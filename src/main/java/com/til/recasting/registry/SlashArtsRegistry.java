package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.registry.sa.*;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

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
    public static final RegistryObject<ExtendedSlashArts> FRAGMENT = registerExtendedSA("fragment", FragmentSlashArts::new);

    // 青芒
    public static final RegistryObject<ExtendedSlashArts> CYAN_GLOW = registerExtendedSA("cyan_glow", CyanGlowSlashArts::new);
    public static final RegistryObject<ExtendedSlashArts> CYAN_GLOW_LAMBDA = registerExtendedSA("cyan_glow_lambda", CyanGlowSlashArts::new);

    // 乱舞
    public static final RegistryObject<ExtendedSlashArts> FANATICAL_DANCE = registerExtendedSA("fanatical_dance", FanaticalDanceSlashArts::new);
    public static final RegistryObject<ExtendedSlashArts> FANATICAL_DANCE_LAMBDA = registerExtendedSA(
            "fanatical_dance_lambda",
            () -> new FanaticalDanceSlashArts().setAttackNumber(21).setAttackDeviation(4).setHit(0.6f)
    );

    // 风暴幻影剑
    public static final RegistryObject<ExtendedSlashArts> STORM_PHANTOM_SWORDS = registerExtendedSA("storm_phantom_swords", StormPhantomSwordsSlashArts::new);
    public static final RegistryObject<ExtendedSlashArts> STORM_PHANTOM_SWORDS_LAMBDA = registerExtendedSA("storm_phantom_swords_lambda", () -> new StormPhantomSwordsSlashArts().setNumber(24));

    // 剑雨
    public static final RegistryObject<ExtendedSlashArts> SWORD_RAIN = registerExtendedSA("sword_rain", SwordRainSlashArts::new);
    public static final RegistryObject<ExtendedSlashArts> SWORD_RAIN_LAMBDA = registerExtendedSA("sword_rain_lambda", () -> new SwordRainSlashArts().setConcentrate(true));

    // 拟似黑洞
    public static final RegistryObject<ExtendedSlashArts> VOID_HOLE = registerExtendedSA("void_hole", VoidHoleSlashArts::new);
    public static final RegistryObject<ExtendedSlashArts> VOID_HOLE_PITCH_BLACK = registerExtendedSA("void_hole_pitch_black", () -> new VoidHoleSlashArts().setLifeTicks(40).setRange(45).setPower(0.02f));
    public static final RegistryObject<ExtendedSlashArts> VOID_HOLE_FISHY_RED = registerExtendedSA("void_hole_fishy_red", () -> new VoidHoleSlashArts().setLifeTicks(80).setRange(64).setPower(0.02f));

    // 多重次元斩·决
    public static final RegistryObject<ExtendedSlashArts> MULTIPLE_JUDGEMENT_CUT = registerExtendedSA("multiple_judgement_cut", MultipleJudgementCutSlashArts::new);
    // 无限次元斩
    public static final RegistryObject<ExtendedSlashArts> INFINITE_JUDGEMENT_CUT = registerExtendedSA("infinite_judgement_cut", InfiniteJudgementCutSlashArts::new);

    // 星
    public static final RegistryObject<ExtendedSlashArts> STAR_1 = registerExtendedSA("star_1", StarSlashArts::new);
    public static final RegistryObject<ExtendedSlashArts> STAR_2 = registerExtendedSA("star_2", () -> new StarSlashArts().setAttackNumber(8).setRange(16));
    public static final RegistryObject<ExtendedSlashArts> STAR_3 = registerExtendedSA("star_3", () -> new StarSlashArts().setAttackNumber(12).setRange(20));
    public static final RegistryObject<ExtendedSlashArts> STAR_4 = registerExtendedSA("star_4", () -> new StarSlashArts().setAttackNumber(16).setRange(32));
    public static final RegistryObject<ExtendedSlashArts> STAR_4_LAMBDA = registerExtendedSA("star_4_lambda", () -> new StarSlashArts().setAttackNumber(24).setRange(32).setZoneNumber(5));

    // 多重剑气
    public static final RegistryObject<ExtendedSlashArts> MULTIPLE_DRIVE = registerExtendedSA("multiple_drive", MultipleDriveSlashArts::new);
    public static final RegistryObject<ExtendedSlashArts> MULTIPLE_DRIVE_LAMBDA = registerExtendedSA("multiple_drive_lambda", () -> new MultipleDriveSlashArts().setAttack(8).setAttack(0.2f));

    // 引雷
    public static final RegistryObject<ExtendedSlashArts> LIGHTNING_CALL = registerExtendedSA("lightning_call", LightningCallSlashArts::new);

    // 闪电链（对齐光棱：脉冲连射；1/2/3 级：脉冲数 / 跳数 / 范围；3 级允许重复跳跃）
    public static final RegistryObject<ExtendedSlashArts> LIGHTNING_CHAIN_1 = registerExtendedSA(
            "lightning_chain_1",
            () -> new LightningChainSlashArts()
                    .setChainCount(1)
                    .setMaxHops(8)
                    .setHopRange(8f)
                    .setSeedRadius(5.0f)
    );
    public static final RegistryObject<ExtendedSlashArts> LIGHTNING_CHAIN_2 = registerExtendedSA(
            "lightning_chain_2",
            () -> new LightningChainSlashArts()
                    .setChainCount(3)
                    .setDelayTicks(5)
                    .setMaxHops(8)
                    .setHopRange(11f)
                    .setSeedRadius(6.0f)
                    .setFirstAttack(0.4f)
                    .setChainAttack(0.3f)
    );
    public static final RegistryObject<ExtendedSlashArts> LIGHTNING_CHAIN_3 = registerExtendedSA(
            "lightning_chain_3",
            () -> new LightningChainSlashArts()
                    .setChainCount(6)
                    .setDelayTicks(5)
                    .setMaxHops(12)
                    .setHopRange(15f)
                    .setSeedRadius(7.0f)
                    .setAllowRepeatJump(true)
                    .setFirstAttack(0.3f)
                    .setChainAttack(0.25f)
    );
    public static final RegistryObject<ExtendedSlashArts> LIGHTNING_CHAIN_3_LAMBDA = registerExtendedSA(
            "lightning_chain_3_lambda",
            () -> new LightningChainSlashArts()
                    .setChainCount(8)
                    .setDelayTicks(5)
                    .setMaxHops(14)
                    .setHopRange(15f)
                    .setSeedRadius(8.0f)
                    .setAllowRepeatJump(true)
                    .setFirstAttack(0.3f)
                    .setChainAttack(0.25f)
    );

    // 苍穹十二连
    public static final RegistryObject<ExtendedSlashArts> HEAVEN_TWELVE_HIT = registerExtendedSA("heaven_twelve_hit", HeavenTwelveHitSlashArts::new);
    public static final RegistryObject<ExtendedSlashArts> HEAVEN_TWELVE_HIT_LAMBDA = registerExtendedSA("heaven_twelve_hit_lambda", () -> new HeavenTwelveHitSlashArts().setLightningNumber(18).setLightningAttack(1.3f).setAttack(0.5f));

    // 云轮
    public static final RegistryObject<ExtendedSlashArts> CLOUD_WHEEL = registerExtendedSA("cloud_wheel", () -> new CloudWheelSlashArts().setLightningNumber(0));
    // 云轮风暴
    public static final RegistryObject<ExtendedSlashArts> CLOUD_WHEEL_STORM = registerExtendedSA("cloud_wheel_storm", () -> new CloudWheelSlashArts().setLightningNumber(7).setAttackNumber(10));

    //星旋
    public static final RegistryObject<ExtendedSlashArts> STELLAR_ROTATION = registerExtendedSA("stellar_rotation", StellarRotationSlashArts::new);

    // 急行幻影剑
    public static final RegistryObject<ExtendedSlashArts> RAPID_PHANTOM_SWORDS = registerExtendedSA("rapid_phantom_swords", RapidPhantomSwordsSlashArts::new);
    // 急行幻影剑[密]
    public static final RegistryObject<ExtendedSlashArts> RAPID_PHANTOM_SWORDS_DENSE = registerExtendedSA(
            "rapid_phantom_swords_dense",
            () -> new RapidPhantomSwordsSlashArts().setNumber(36)
    );
    public static final RegistryObject<ExtendedSlashArts> RAPID_PHANTOM_SWORDS_DENSE_LAMBDA = registerExtendedSA(
            "rapid_phantom_swords_dense_lambda",
            () -> new RapidPhantomSwordsSlashArts().setNumber(48)
    );

    // 穷观阵
    public static final RegistryObject<ExtendedSlashArts> MATRIX = registerExtendedSA("matrix", MatrixSlashArts::new);
    public static final RegistryObject<ExtendedSlashArts> MATRIX_LAMBDA = registerExtendedSA("matrix_lambda", () -> new MatrixSlashArts().setAttackIntervalTicks(5));

    // 断魄
    public static final RegistryObject<ExtendedSlashArts> SOUL_SEVER = registerExtendedSA("soul_sever", SoulSeverSlashArts::new);
    public static final RegistryObject<ExtendedSlashArts> SOUL_SEVER_LAMBDA = registerExtendedSA(
            "soul_sever_lambda",
            () -> new SoulSeverSlashArts()
                    .setSlashAttack(4.3225f)
                    .setSlashSize(4.0f)
                    .setGiantJudgementCutAttack(1.995f)
                    .setGiantJudgementCutSize(8.0f)
                    .setGiantJudgementCutCount(7)
    );

    // 幻影爆破
    public static final RegistryObject<ExtendedSlashArts> PHANTOM_EXPLOSION = registerExtendedSA("phantom_explosion", PhantomExplosionSlashArts::new);
    public static final RegistryObject<ExtendedSlashArts> PHANTOM_EXPLOSION_LAMBDA = registerExtendedSA("phantom_explosion_lambda", () -> new PhantomExplosionSlashArts().setGroupCount(3));

    // 无限剑制
    public static final RegistryObject<ExtendedSlashArts> UNLIMITED_BLADE_WORKS = registerExtendedSA("unlimited_blade_works", UnlimitedBladeWorksSlashArts::new);
    public static final RegistryObject<ExtendedSlashArts> UNLIMITED_BLADE_WORKS_LAMBDA = registerExtendedSA("unlimited_blade_works_lambda", () -> new UnlimitedBladeWorksSlashArts().setAttack(0.06f));

    // 剑刃风暴
    public static final RegistryObject<ExtendedSlashArts> BLADE_STORM = registerExtendedSA("blade_storm", BladeStormSlashArts::new);
    public static final RegistryObject<ExtendedSlashArts> BLADE_STORM_LAMBDA = registerExtendedSA("blade_storm_lambda", () -> new BladeStormSlashArts().setTotalSwords(256));

    // 斩铁式·极
    public static final RegistryObject<ExtendedSlashArts> ZANTETSUDEN_MAX = registerExtendedSA("zantetsuden_max", ZantetsudenMaxSlashArts::new);
    public static final RegistryObject<ExtendedSlashArts> ZANTETSUDEN_MAX_LAMBDA = registerExtendedSA("zantetsuden_max_lambda", () -> new ZantetsudenMaxSlashArts().setAttackNumber(40));

    // 斩铁式·行
    public static final RegistryObject<ExtendedSlashArts> ZANTETSUDEN_ROW = registerExtendedSA("zantetsuden_row", ZantetsudenRowSlashArts::new);
    public static final RegistryObject<ExtendedSlashArts> ZANTETSUDEN_ROW_LAMBDA = registerExtendedSA("zantetsuden_row_lambda", () -> new ZantetsudenRowSlashArts().setDriveNumber(40));

    // 业火
    public static final RegistryObject<ExtendedSlashArts> INFERNO = registerExtendedSA("inferno", InfernoSlashArts::new);
    public static final RegistryObject<ExtendedSlashArts> INFERNO_LAMBDA = registerExtendedSA("inferno_lambda", () -> new InfernoSlashArts().setSoulBurnLevel(6));

    // 光棱（红警2 光棱坦克：延迟脉冲 + 头顶发射/自动索敌 + 锁定散射）
    // scatterRange：相对基准散射盒，每级 +33%（1 / 1.33 / 1.66）
    public static final RegistryObject<ExtendedSlashArts> LASER_1 = registerExtendedSA(
            "laser_1",
            () -> new LaserBeamSlashArts()
                    .setBeamCount(1)
                    .setScatterCount(5)
                    .setScatterRange(1.0f)
                    .setAttack(0.5f)
                    .setScatterAttack(0.15f)
    );
    public static final RegistryObject<ExtendedSlashArts> LASER_2 = registerExtendedSA(
            "laser_2",
            () -> new LaserBeamSlashArts()
                    .setBeamCount(4)
                    .setDelayTicks(5)
                    .setRange(48f)
                    .setScatterRange(1.33f)
                    .setScatterCount(5)
                    .setSecondaryScatterCount(3)
                    .setAttack(0.3f)
                    .setScatterAttack(0.125f)
    );
    public static final RegistryObject<ExtendedSlashArts> LASER_3 = registerExtendedSA(
            "laser_3",
            () -> new LaserBeamSlashArts()
                    .setBeamCount(5)
                    .setDelayTicks(5)
                    .setRange(72f)
                    .setScatterRange(1.66f)
                    .setScatterCount(5)
                    .setSecondaryScatterCount(3)
                    .setAttack(0.2f)
                    .setScatterAttack(0.14f)
    );
    public static final RegistryObject<ExtendedSlashArts> LASER_3_LAMBDA = registerExtendedSA(
            "laser_3_lambda",
            () -> new LaserBeamSlashArts()
                    .setBeamCount(6)
                    .setDelayTicks(5)
                    .setRange(72f)
                    .setScatterRange(1.66f)
                    .setScatterCount(5)
                    .setSecondaryScatterCount(3)
                    .setTertiaryScatterCount(2)
                    .setAttack(0.2f)
                    .setScatterAttack(0.14f)
    );

    // 长空落日
    public static final RegistryObject<ExtendedSlashArts> LONG_SKY_SUNSET = registerExtendedSA(
            "long_sky_sunset",
            LongSkySunsetSlashArts::new
    );
    public static final RegistryObject<ExtendedSlashArts> LONG_SKY_SUNSET_LAMBDA = registerExtendedSA(
            "long_sky_sunset_lambda",
            () -> new LongSkySunsetSlashArts().setSwordCount(16)
    );

    // 云界
    public static final RegistryObject<ExtendedSlashArts> JADE_DOMAIN = registerExtendedSA(
            "jade_domain",
            JadeDomainSlashArts::new
    );
    public static final RegistryObject<ExtendedSlashArts> JADE_DOMAIN_LAMBDA = registerExtendedSA(
            "jade_domain_lambda",
            () -> new JadeDomainSlashArts()
                    .setJudgementCutAttack(0.1197f)
                    .setPhantomSwordAttack(0.00798f)
                    .setMinPhantomSwordCount(9)
                    .setMaxPhantomSwordCount(12)
                    .setInitialBladeReleaseCount(13)
    );

    // 裂岚
    public static final RegistryObject<ExtendedSlashArts> RIFT_GALE = registerExtendedSA(
            "rift_gale",
            RiftGaleSlashArts::new
    );
    public static final RegistryObject<ExtendedSlashArts> RIFT_GALE_LAMBDA = registerExtendedSA(
            "rift_gale_lambda",
            () -> new RiftGaleSlashArts()
                    .setDriveCount(30)
                    .setDriveDurationTicks(30)
                    .setDriveAttack(0.133f)
                    .setDriveLifeTicks(15)
                    .setCrossAttack(1.7955f)
                    .setCrossSize(4.655f)
    );

    // 掠影
    public static final RegistryObject<ExtendedSlashArts> FLEETING_SHADOW = registerExtendedSA(
            "fleeting_shadow",
            FleetingShadowSlashArts::new
    );
    public static final RegistryObject<ExtendedSlashArts> FLEETING_SHADOW_LAMBDA = registerExtendedSA(
            "fleeting_shadow_lambda",
            () -> new FleetingShadowSlashArts()
                    .setJumpCount(26)
                    .setJumpInterval(2)
                    .setSlashTicksPerJump(1)
                    .setSlashesPerTick(2)
                    .setSearchRange(48.0f)
                    .setSlashHit(0.1206f)
    );

    // 万灵寂灭
    public static final RegistryObject<ExtendedSlashArts> MYRIAD_SILENCE = registerExtendedSA(
            "myriad_silence",
            MyriadSilenceSlashArts::new
    );
    public static final RegistryObject<ExtendedSlashArts> MYRIAD_SILENCE_LAMBDA = registerExtendedSA(
            "myriad_silence_lambda",
            () -> new MyriadSilenceSlashArts()
                    .setDecreeTicks(39 * 20)
                    .setVoidRatio(0.22f)
    );

    // 万象归元
    public static final RegistryObject<ExtendedSlashArts> PHENOMENAL_RETURN = registerExtendedSA(
            "phenomenal_return",
            PhenomenalReturnSlashArts::new
    );
    public static final RegistryObject<ExtendedSlashArts> PHENOMENAL_RETURN_LAMBDA = registerExtendedSA(
            "phenomenal_return_lambda",
            () -> new PhenomenalReturnSlashArts()
                    .setDurationTicks(104)
                    .setCenterRange(41.6f)
                    .setDispelRange(166.4f)
                    .setAttackRatio(0.104f)
                    .setSuppressTicks(12 * 20)
                    .setLifeTicksMin(13)
                    .setLifeTicksMax(26)
                    .setTargetedChance(0.52f)
    );

    // 终焉超新星爆
    public static final RegistryObject<ExtendedSlashArts> FINAL_SUPERNOVA = registerExtendedSA("final_supernova", FinalSupernovaSlashArts::new);
    public static final RegistryObject<ExtendedSlashArts> FINAL_SUPERNOVA_LAMBDA = registerExtendedSA(
            "final_supernova_lambda",
            () -> new FinalSupernovaSlashArts()
                    .setDamageRatio(7.6125f)
                    .setHorizonStart(21.28f)
                    .setParticleRadiusStart(31.92f)
                    .setEffectRange(85.12f)
                    .setAbsorbRadius(2.66f)
                    .setDamageFalloffStart(21.28f)
    );

    // 犬咬
    public static final RegistryObject<ExtendedSlashArts> DOG_BITE = registerExtendedSA("dog_bite", DogBiteSlashArts::new);

    // 回到未来计划 · 时之彼端
    public static final RegistryObject<ExtendedSlashArts> TIME_BEYOND = registerExtendedSA("time_beyond", TimeBeyondSlashArts::new);

    // 回到未来计划 · 禁锢
    public static final RegistryObject<ExtendedSlashArts> IMPRISONMENT = registerExtendedSA("imprisonment", ImprisonmentSlashArts::new);

    // 回到未来计划 · 相位碎裂
    public static final RegistryObject<ExtendedSlashArts> PHASE_FRACTURE = registerExtendedSA("phase_fracture", PhaseFractureSlashArts::new);

    // 回到未来计划 · 永恒守卫
    public static final RegistryObject<ExtendedSlashArts> ETERNAL_GUARD = registerExtendedSA("eternal_guard", EternalGuardSlashArts::new);

    // 回到未来计划 · 青茫熳天摇
    public static final RegistryObject<ExtendedSlashArts> AZURE_HAZE = registerExtendedSA("azure_haze", AzureHazeSlashArts::new);

    // 回到未来计划 · 红尘滚滚
    public static final RegistryObject<ExtendedSlashArts> MORTAL_DUST = registerExtendedSA("mortal_dust", MortalDustSlashArts::new);

    // 回到未来计划 · 撼海潮涌
    public static final RegistryObject<ExtendedSlashArts> TIDAL_SURGE = registerExtendedSA("tidal_surge", TidalSurgeSlashArts::new);

    // 回到未来计划 · 星辰斗转
    public static final RegistryObject<ExtendedSlashArts> CELESTIAL_DRIVE = registerExtendedSA("celestial_drive", CelestialDriveSlashArts::new);

    // 回到未来计划 · 群星坠落
    public static final RegistryObject<ExtendedSlashArts> STARFALL = registerExtendedSA("starfall", StarfallSlashArts::new);

    // 回到未来计划 · 擒苍决
    public static final RegistryObject<ExtendedSlashArts> SKY_SEIZE = registerExtendedSA("sky_seize", SkySeizeSlashArts::new);

    // 回到未来计划 · 神斩
    public static final RegistryObject<ExtendedSlashArts> DIVINE_SLASH = registerExtendedSA("divine_slash", DivineSlashSlashArts::new);

    // 回到未来计划 · 断罪
    public static final RegistryObject<ExtendedSlashArts> VERDICT = registerExtendedSA("verdict", VerdictSlashArts::new);

    // 回到未来计划 · 无限开花决
    public static final RegistryObject<ExtendedSlashArts> INFINITE_BLOOM = registerExtendedSA("infinite_bloom", InfiniteBloomSlashArts::new);

    // 回到未来计划 · 暴烈剑气
    public static final RegistryObject<ExtendedSlashArts> BLISTERING_QI = registerExtendedSA("blistering_qi", BlisteringQiSlashArts::new);

    // 回到未来计划 · 大包弹
    public static final RegistryObject<ExtendedSlashArts> HEAVY_PAYLOAD = registerExtendedSA("heavy_payload", HeavyPayloadSlashArts::new);

    // 异界斩切
    public static final RegistryObject<ExtendedSlashArts> OTHERWORLD_SLASH = registerExtendedSA(
            "otherworld_slash",
            OtherworldSlashSlashArts::new
    );



    /**
     * 注册扩展的 SlashArts，自动关联 ComboState
     */
    private static RegistryObject<ExtendedSlashArts> registerExtendedSA(String name, Supplier<ExtendedSlashArts> factory) {
        Supplier<ExtendedSlashArts> slashArtsSupplier = memoize(factory);
        RegistryObject<ExtendedSlashArts> slashArtsRegistryObject = SLASH_ARTS.register(name, slashArtsSupplier);

        RecastingComboStateRegistry.COMBO_STATE.register(
                name,
                () -> slashArtsSupplier.get().createComboState()
        );

        return slashArtsRegistryObject;
    }

    private static <T> Supplier<T> memoize(Supplier<T> factory) {
        AtomicReference<T> reference = new AtomicReference<>();
        return () -> {
            T cached = reference.get();
            if (cached != null) {
                return cached;
            }

            T created = factory.get();
            if (reference.compareAndSet(null, created)) {
                return created;
            }
            return reference.get();
        };
    }
}

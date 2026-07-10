package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.constant.R;
import com.til.recasting.entity.*;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.mixin.SlashArtsAccessor;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.client.UserPoseOverrider;
import mods.flammpfeil.slashblade.event.handler.FallHandler;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import mods.flammpfeil.slashblade.util.AttackManager;
import mods.flammpfeil.slashblade.util.KnockBacks;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.util.DamageStructure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

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

    // 激光
    public static final RegistryObject<ExtendedSlashArts> LASER_1 = registerExtendedSA("laser_1", new LaserBeamSlashArts().setBeamCount(1).setAttack(1.0f));
    public static final RegistryObject<ExtendedSlashArts> LASER_2 = registerExtendedSA("laser_2", new LaserBeamSlashArts().setBeamCount(3).setDelay(3).setAttack(0.55f));
    public static final RegistryObject<ExtendedSlashArts> LASER_3 = registerExtendedSA("laser_3", new LaserBeamSlashArts().setBeamCount(7).setDelay(2).setAttack(0.35f));

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

    /**
     * 扩展的 SlashArts 类
     * 自动关联一个 ComboState
     */
    public static abstract class ExtendedSlashArts extends SlashArts {

        public ExtendedSlashArts() {
            super(e -> SlashBlade.prefix("none"));
            SlashArtsAccessor slashArtsAccessor = (SlashArtsAccessor) this;
            slashArtsAccessor.setComboState(e -> getComboStateName());
            slashArtsAccessor.setComboStateJust(e -> getComboStateName());
        }

        public ResourceLocation getComboStateName() {
            return SlashArts.getRegistryKey(this);
        }

        public ComboState createComboState() {
            return ComboState.Builder.newInstance()
                    .startAndEnd(1923, 1928)
                    .speed(0.5F)
                    .priority(50)
                    .next(entity -> SlashBlade.prefix("judgement_cut_slash_air"))
                    .nextOfTimeout(entity -> SlashBlade.prefix("judgement_cut_sheath_air"))
                    .addTickAction(ComboState.TimeLineTickAction.getBuilder().put(0, e -> {
                        if (e.level().isClientSide()) {
                            return;
                        }

                        ItemStack mainHandItem = e.getMainHandItem();
                        if (mainHandItem.isEmpty()) {
                            return;
                        }

                        LazyOptional<ISlashBladeState> slashBladeStateLazyOptional = mainHandItem.getCapability(ItemSlashBlade.BLADESTATE);
                        LazyOptional<PropertiesDefinitionExtension> propertiesDefinitionExtensionLazyOptional = mainHandItem.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION);
                        LazyOptional<RenderDefinitionExtension> renderDefinitionExtensionLazyOptional = mainHandItem.getCapability(CapabilityRegistryHandler.RENDER_DEFINITION_EXTENSION);

                        if (slashBladeStateLazyOptional.isPresent() && propertiesDefinitionExtensionLazyOptional.isPresent() && renderDefinitionExtensionLazyOptional.isPresent()) {
                            //noinspection DataFlowIssue
                            trigger(e, mainHandItem, slashBladeStateLazyOptional.orElse(null), renderDefinitionExtensionLazyOptional.orElse(null), propertiesDefinitionExtensionLazyOptional.orElse(null));
                        }
                    }).build())
                    .addTickAction(FallHandler::fallResist)
                    .addTickAction(UserPoseOverrider::resetRot)
                    .addHitEffect(StunManager::setStun).build();
        }

        public abstract void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension);


        public String getDescId() {
            return getDescriptionId() + ".desc";
        }
    }

    /**
     * 乱舞 Slash Arts
     * 快速连续发动多次随机角度的斩击
     */
    @Setter
    @Accessors(chain = true)
    public static class FanaticalDanceSlashArts extends ExtendedSlashArts {

        int attackNumber = 15;
        int attackDeviation = 3;
        float hit = 0.4f;
        int delay = 1;
        int offset = 3;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            // 计算总攻击次数
            int number = attackNumber + livingEntity.getRandom().nextInt(attackDeviation + 1);

            // 获取实体的定时器
            LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);

            timeRunOptional.ifPresent(timeRun -> {
                for(int i = 0; i < number; i++) {
                    int _delay = delay * i;

                    timeRun.addTimerCell(
                            () -> {
                                // 随机角度 (0-360度)
                                float randomRoll = livingEntity.getRandom().nextFloat() * 360;

                                // 随机偏移向量
                                Vec3 randomOffset = new Vec3(
                                        livingEntity.getRandom().nextFloat() - 0.5f,
                                        livingEntity.getRandom().nextFloat() - 0.5f,
                                        0
                                ).scale(offset);

                                // 执行斩击
                                AttackManager.doSlash(
                                        livingEntity,
                                        randomRoll,
                                        randomOffset,
                                        false,  // mute
                                        true,   // critical
                                        hit     // comboRatio
                                );
                            },
                            _delay
                    );
                }
            });
        }
    }

    /**
     * 风暴幻影剑 Slash Arts
     * 在实体周围召唤多把幻影剑并发射
     */
    @Setter
    @Accessors(chain = true)
    public static class StormPhantomSwordsSlashArts extends ExtendedSlashArts {

        float attack = 0.15f;
        int number = 12;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            Level worldIn = livingEntity.level();

            // 获取攻击目标位置
            Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

            // 计算生成位置（玩家眼睛位置 + 侧向偏移）
            Vec3 pos = livingEntity.getEyePosition(1.0f)
                    .add(mods.flammpfeil.slashblade.util.VectorHelper.getVectorForRotation(
                            0.0f,
                            livingEntity.getYRot() + 90
                    ).scale(livingEntity.getRandom().nextBoolean()
                            ? 1
                            : -1));

            // 创建多把召唤剑
            for(int i = 0; i < number; i++) {
                SummondSwordEntity summonedSword =
                        new SummondSwordEntity(
                                RecastingEntities.SUMMOND_SWORD.get(),
                                worldIn,
                                livingEntity
                        );

                // 设置位置
                summonedSword.setPos(pos.x, pos.y, pos.z);

                // 设置朝向（朝向攻击目标）
                summonedSword.lookAt(attackPos, false);

                // 设置属性
                summonedSword.setColor(slashBladeState.getColorCode());
                summonedSword.setStartDelay(i);  // 延迟发射
                summonedSword.setRoll(livingEntity.getRandom().nextFloat() * 360.0f);
                summonedSword.setModifiedRatio(attack);

                // 添加到世界
                worldIn.addFreshEntity(summonedSword);
            }

            // 播放音效
            livingEntity.playSound(
                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                    0.2F,
                    1.45F
            );
        }
    }

    /**
     * 剑雨 Slash Arts
     * 在目标区域上方召唤大量剑雨攻击
     */
    @Setter
    @Accessors(chain = true)
    public static class SwordRainSlashArts extends ExtendedSlashArts {

        float attack = 0.05f;
        int attackNumber = 150;
        float range = 5;
        boolean concentrate = false;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            Level worldIn = livingEntity.level();

            // 获取攻击目标位置
            Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

            // 计算基础生成位置（在实体上方）
            Vec3 basePos = livingEntity.position().add(0, range / 2, 0);

            net.minecraft.util.RandomSource random = livingEntity.getRandom();

            // 创建大量召唤剑
            for(int i = 0; i < attackNumber; i++) {
                SummondSwordEntity summonedSword =
                        new SummondSwordEntity(
                                RecastingEntities.SUMMOND_SWORD.get(),
                                worldIn,
                                livingEntity
                        );

                // 在圆形范围内随机生成位置
                Vec3 randomOffset = PosHelper.getRandomVectorInCircle(random, range);
                Vec3 pos = basePos.add(randomOffset);

                // 设置位置
                summonedSword.setPos(pos.x, pos.y, pos.z);

                // 设置大小
                summonedSword.setSize(0.6f);

                // 设置属性
                summonedSword.setColor(slashBladeState.getColorCode());
                summonedSword.setModifiedRatio(attack);
                summonedSword.setStartDelay(random.nextInt(60));  // 随机延迟发射
                summonedSword.setRoll(random.nextFloat() * 360.0f);

                // 初始化飞行方向：构造函数内 lookAt(getDeltaMovement()) 在速度为零时无法得到正确朝向，必须在 setPos 后重设
                if (concentrate) {
                    summonedSword.lookAt(attackPos, false);
                } else {
                    summonedSword.setRot(livingEntity.getYRot(), livingEntity.getXRot(), true);
                    summonedSword.updateMotion();
                }

                // 添加到世界
                worldIn.addFreshEntity(summonedSword);
            }

            // 播放音效
            livingEntity.playSound(
                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                    0.2F,
                    1.45F
            );
        }
    }

    /**
     * 青芒 Slash Arts
     * 以玩家为中心，按360度均匀分布发动多次斩击
     */
    @Setter
    @Accessors(chain = true)
    public static class CyanGlowSlashArts extends ExtendedSlashArts {

        int attackNumber = 8;
        float hit = 0.3f;
        int delay = 3;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            // 计算每次攻击的角度间隔
            float angleStep = 360f / attackNumber;

            // 获取实体的定时器
            LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);

            timeRunOptional.ifPresent(timeRun -> {
                for(int i = 0; i < attackNumber; i++) {
                    int _delay = delay * i;
                    int finalI = i;

                    timeRun.addTimerCell(
                            () -> {
                                // 计算均匀分布的角度
                                float angle = angleStep * finalI;

                                // 执行斩击
                                AttackManager.doSlash(
                                        livingEntity,
                                        angle,
                                        Vec3.ZERO,  // 无偏移
                                        false,      // mute
                                        false,      // critical
                                        hit         // comboRatio
                                );
                            },
                            _delay
                    );
                }
            });
        }
    }

    /**
     * 拟似黑洞 Slash Arts
     * 创建一个次元斩，吸引范围内的所有实体向中心
     */
    @Setter
    @Accessors(chain = true)
    public static class VoidHoleSlashArts extends ExtendedSlashArts {

        float attack = 0.05f;
        int life = 20;
        float size = 4;
        float range = 32;
        float power = 0.02f;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            Level worldIn = livingEntity.level();

            // 获取目标位置
            Vec3 pos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

            // 创建自定义的黑洞次元斩
            JudgementCutEntity jc = new JudgementCutEntity(RecastingEntities.JUDGEMENT_CUT.get(), worldIn, livingEntity) {

                @Override
                public void tick() {
                    super.tick();

                    // 每 tick 执行吸引效果
                    if (!this.level().isClientSide()) {
                        Vec3 centerPos = this.position();


                        // 获取范围内的所有实体
                        List<Entity> entities = EntityHelper.getTargettableEntitiesWithinAABB(
                                level(),
                                getShooter(),
                                pos,
                                range
                        );

                        // 对每个实体施加吸引力
                        for(Entity entity : entities) {
                            Vec3 direction = centerPos.subtract(entity.position());
                            double length = direction.length();

                            if (length > range || length < 0.1) {
                                continue;
                            }

                            // 计算力度：距离越近力度越大（平方衰减）
                            double lengthRatio = length / range;
                            double strength = (1 - lengthRatio) * (1 - lengthRatio);
                            double _power = power * range;

                            // 应用吸引力
                            Vec3 currentMotion = entity.getDeltaMovement();
                            entity.setDeltaMovement(currentMotion.add(
                                    (direction.x / length) * strength * _power,
                                    (direction.y / length) * strength * _power,
                                    (direction.z / length) * strength * _power
                            ));
                        }
                    }
                }
            };

            jc.setPos(pos.x, pos.y, pos.z);

            jc.setColor(slashBladeState.getColorCode());


            // 设置伤害倍率
            jc.setModifiedRatio(attack);

            // 设置生命时间
            jc.setMaxLifeTime(life);

            jc.setColor(slashBladeState.getColorCode());

            jc.setSize(size);

            // 添加到世界
            worldIn.addFreshEntity(jc);

            jc.setRepeatedAttack(true);

            // 播放音效
            worldIn.playSound(null, jc.getX(), jc.getY(), jc.getZ(),
                    SoundEvents.ENDERMAN_TELEPORT,
                    net.minecraft.sounds.SoundSource.PLAYERS, 0.5F,
                    0.8F / (livingEntity.getRandom().nextFloat() * 0.4F + 0.8F));
        }

    }

    /**
     * 多重次元斩·决 Slash Arts
     * 在同一位置连续发动多次次元斩，每次都在前一次的位置
     */
    @Setter
    @Accessors(chain = true)
    public static class MultipleJudgementCutSlashArts extends ExtendedSlashArts {

        int attackNumber = 4;
        float hit = 0.3f;
        int delay = 4;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            // 用于记录上一次次元斩的位置
            final Vec3[] lastPos = {null};

            // 获取实体的定时器
            LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);

            timeRunOptional.ifPresent(timeRun -> {
                for(int i = 0; i < attackNumber; i++) {
                    int _delay = delay * i;

                    timeRun.addTimerCell(
                            () -> {
                                Level worldIn = livingEntity.level();

                                // 确定生成位置：如果有上次的位置，使用上次的位置，否则使用目标位置
                                Vec3 pos;
                                if (lastPos[0] != null) {
                                    pos = lastPos[0];
                                } else {
                                    pos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);
                                }

                                // 创建次元斩
                                JudgementCutEntity jc =
                                        new JudgementCutEntity(
                                                RecastingEntities.JUDGEMENT_CUT.get(),
                                                worldIn,
                                                livingEntity
                                        );

                                jc.setPos(pos.x, pos.y, pos.z);

                                // 设置颜色
                                itemStack.getCapability(mods.flammpfeil.slashblade.item.ItemSlashBlade.BLADESTATE)
                                        .ifPresent(state -> jc.setColor(state.getColorCode()));

                                // 设置伤害倍率
                                jc.setModifiedRatio(hit);

                                // 设置生命时间
                                jc.setColor(slashBladeState.getColorCode());

                                // 添加到世界
                                worldIn.addFreshEntity(jc);

                                // 更新位置记录
                                lastPos[0] = pos;

                                // 播放音效
                                worldIn.playSound(null, jc.getX(), jc.getY(), jc.getZ(),
                                        SoundEvents.ENDERMAN_TELEPORT,
                                        net.minecraft.sounds.SoundSource.PLAYERS, 0.5F,
                                        0.8F / (livingEntity.getRandom().nextFloat() * 0.4F + 0.8F));
                            },
                            _delay
                    );
                }
            });
        }
    }

    /**
     * 无限次元斩 Slash Arts
     * 在范围内的敌人位置随机发动大量次元斩
     */
    @Setter
    @Accessors(chain = true)
    public static class InfiniteJudgementCutSlashArts extends ExtendedSlashArts {

        float attackRange = 12f;
        int attackNumber = 12;
        float hit = 0.5f;
        int delay = 2;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            Level worldIn = livingEntity.level();

            // 获取攻击目标位置
            Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

            // 获取范围内的所有敌对实体
            List<LivingEntity> attackEntities = new java.util.ArrayList<>(EntityHelper.getTargettableLivingEntityWithinAABB(
                    livingEntity.level(),
                    livingEntity,
                    attackPos,
                    attackRange
            ));

            // 获取实体的定时器
            LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);

            timeRunOptional.ifPresent(timeRun -> {
                for(int i = 0; i < attackNumber; i++) {
                    int _delay = delay * i;

                    timeRun.addTimerCell(
                            () -> {
                                Vec3 targetPos;

                                // 如果有敌人，随机选择一个存活的敌人
                                if (!attackEntities.isEmpty()) {
                                    Entity target = null;

                                    // 尝试找到一个存活的目标
                                    for(int attempt = 0; attempt < 10 && !attackEntities.isEmpty(); attempt++) {
                                        Entity candidate = attackEntities.get(livingEntity.getRandom().nextInt(attackEntities.size()));

                                        if (candidate.isAlive()) {
                                            target = candidate;
                                            break;
                                        } else {
                                            attackEntities.remove(candidate);
                                        }
                                    }

                                    if (target != null) {
                                        targetPos = new Vec3(
                                                target.getX(),
                                                target.getY() + target.getEyeHeight() * 0.5,
                                                target.getZ()
                                        );
                                    } else {
                                        targetPos = attackPos;
                                    }
                                } else {
                                    // 如果没有敌人，在原位置发动
                                    targetPos = attackPos;
                                }

                                // 创建次元斩
                                JudgementCutEntity jc =
                                        new JudgementCutEntity(
                                                RecastingEntities.JUDGEMENT_CUT.get(),
                                                worldIn,
                                                livingEntity
                                        );

                                jc.setPos(targetPos.x, targetPos.y, targetPos.z);

                                // 设置颜色
                                jc.setColor(slashBladeState.getColorCode());

                                // 设置伤害倍率
                                jc.setModifiedRatio(hit);

                                // 设置生命时间
                                jc.setMaxLifeTime(10);

                                // 添加到世界
                                worldIn.addFreshEntity(jc);

                                // 播放音效
                                worldIn.playSound(null, jc.getX(), jc.getY(), jc.getZ(),
                                        SoundEvents.ENDERMAN_TELEPORT,
                                        net.minecraft.sounds.SoundSource.PLAYERS, 0.5F,
                                        0.8F / (livingEntity.getRandom().nextFloat() * 0.4F + 0.8F));
                            },
                            _delay
                    );
                }
            });
        }

    }

    /**
     * 星流 Slash Arts
     * 发射多把追踪召唤剑，击中后产生次元斩
     * 可选：在周围生成持续的次元斩阵地，定期发射召唤剑
     */
    @Setter
    @Accessors(chain = true)
    public static class StarSlashArts extends ExtendedSlashArts {

        int attackNumber = 6;
        float attack = 0.25f;
        float judgementCutAttack = 0.5f;
        float range = 12;
        float zoneNumber = 0;
        int zonerRange = 12;
        int zoneTime = 160;
        float attackProbability = 1 / 20f;
        float summondSwordAttack = 0.02f;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            Level worldIn = livingEntity.level();

            // 获取攻击目标位置
            Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

            List<LivingEntity> entityList = EntityHelper.getTargettableLivingEntityWithinAABB(
                    livingEntity.level(),
                    livingEntity,
                    attackPos,
                    range
            );

            // 第一阶段：发射初始召唤剑
            for(int i = 0; i < attackNumber; i++) {
                // 使用自定义召唤剑，击中后产生次元斩
                StarSummonedSword summonedSword = new StarSummonedSword(
                        RecastingEntities.SUMMOND_SWORD.get(),
                        worldIn,
                        livingEntity,
                        judgementCutAttack
                );

                // 设置属性
                summonedSword.setColor(slashBladeState.getColorCode());
                summonedSword.setModifiedRatio(attack);
                summonedSword.setStartDelay(10 + livingEntity.getRandom().nextInt(10));

                // 设置朝向：如果有敌人，朝向随机敌人，否则朝向攻击位置
                Vec3 targetPos;
                if (!entityList.isEmpty()) {
                    Entity target = entityList.get(livingEntity.getRandom().nextInt(entityList.size()));
                    targetPos = new Vec3(target.getX(), target.getY() + target.getEyeHeight() * 0.5, target.getZ());
                } else {
                    targetPos = attackPos;
                }

                summonedSword.lookAt(targetPos, false);

                // 添加到世界
                worldIn.addFreshEntity(summonedSword);
            }

            // 播放音效
            livingEntity.playSound(
                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                    0.2F,
                    1.45F
            );

            // 第二阶段：生成持续的次元斩阵地（如果 zoneNumber > 0）
            if (zoneNumber > 0) {
                for(int i = 0; i < zoneNumber; i++) {
                    // 在玩家周围随机位置生成次元斩
                    Vec3 randomOffset = PosHelper.getRandomVectorInCircle(livingEntity.getRandom(), zonerRange);
                    Vec3 zonePos = livingEntity.position().add(randomOffset);

                    // 创建持续存在的次元斩
                    JudgementCutEntity starJC = new JudgementCutEntity(
                            RecastingEntities.JUDGEMENT_CUT.get(),
                            worldIn,
                            livingEntity
                    ) {
                        @Override
                        public void tick() {
                            super.tick();

                            if (!this.level().isClientSide() && this.random.nextFloat() < attackProbability) {
                                Vec3 pos = this.position();

                                // 实时获取目标位置
                                Vec3 currentTargetPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

                                // 使用自定义召唤剑，击中后产生次元斩
                                StarSummonedSword summonedSword = new StarSummonedSword(
                                        RecastingEntities.SUMMOND_SWORD.get(),
                                        this.level(),
                                        livingEntity,
                                        judgementCutAttack
                                );

                                summonedSword.setPos(pos.x, pos.y, pos.z);
                                summonedSword.setColor(slashBladeState.getColorCode());
                                summonedSword.setModifiedRatio(summondSwordAttack);
                                summonedSword.setStartDelay(10);

                                // 朝向实时获取的目标位置
                                summonedSword.lookAt(currentTargetPos, false);

                                this.level().addFreshEntity(summonedSword);

                                // 播放音效
                                this.level().playSound(null, pos.x, pos.y, pos.z,
                                        SoundEvents.CHORUS_FRUIT_TELEPORT,
                                        net.minecraft.sounds.SoundSource.PLAYERS, 0.2F, 1.45F);
                            }
                        }
                    };

                    starJC.setPos(zonePos.x, zonePos.y, zonePos.z);
                    starJC.setColor(slashBladeState.getColorCode());
                    starJC.setMaxLifeTime(zoneTime);

                    worldIn.addFreshEntity(starJC);
                }
            }
        }

        /**
         * 自定义召唤剑 - 击中后产生次元斩
         */
        public static class StarSummonedSword extends SummondSwordEntity {

            public StarSummonedSword(EntityType<? extends SummondSwordEntity> entityTypeIn, Level worldIn, LivingEntity shooting, float judgementCutAttack) {
                super(entityTypeIn, worldIn, shooting);

                attackActionCallbackPoint.register(e -> {
                    Vec3 jcPos = e.position().add(0, e.getEyeHeight() * 0.5, 0);

                    // 创建次元斩
                    JudgementCutEntity jc =
                            new JudgementCutEntity(
                                    RecastingEntities.JUDGEMENT_CUT.get(),
                                    this.level(),
                                    getShooter()
                            );

                    jc.setPos(jcPos.x, jcPos.y, jcPos.z);
                    jc.setColor(this.getColor());

                    // 设置伤害倍率
                    jc.setModifiedRatio(judgementCutAttack);
                    jc.setModifiedRatio(0);

                    // 设置生命时间
                    jc.setMaxLifeTime(10);

                    // 添加到世界
                    this.level().addFreshEntity(jc);

                    // 播放音效
                    this.level().playSound(null, jcPos.x, jcPos.y, jcPos.z,
                            SoundEvents.ENDERMAN_TELEPORT,
                            net.minecraft.sounds.SoundSource.PLAYERS, 0.5F,
                            0.8F / (this.level().getRandom().nextFloat() * 0.4F + 0.8F));
                });

            }

        }


    }

    /**
     * 多重剑气 Slash Arts
     * 向前发射多个驱动剑气，每个剑气有随机的尺寸和旋转角度
     */
    @Setter
    @Accessors(chain = true)
    public static class MultipleDriveSlashArts extends ExtendedSlashArts {

        float attack = 0.15f;
        int attackNumber = 4;
        int life = 80;
        float range = 1;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            Level worldIn = livingEntity.level();
            Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

            // 生成多个驱动剑气
            for(int i = 0; i < attackNumber; i++) {
                DriveEntity driveEntity =
                        new DriveEntity(
                                RecastingEntities.DRIVE.get(),
                                worldIn,
                                livingEntity
                        );

                // 设置属性
                driveEntity.setColor(slashBladeState.getColorCode());

                // 设置尺寸：随机尺寸 * 攻击距离
                float randomSize = livingEntity.getRandom().nextFloat() * range;
                driveEntity.setSize(randomSize * propertiesDefinitionExtension.attackDistance());

                // 设置伤害
                driveEntity.setModifiedRatio(attack);

                // 设置生命时间
                driveEntity.setMaxLifeTime(life);

                // 设置随机旋转角度（Roll）
                driveEntity.setRoll(livingEntity.getRandom().nextInt(360));

                // 设置速度
                driveEntity.setSeep(0.45f);

                // 向前发射（使用玩家的视线方向）
                driveEntity.lookAt(attackPos, false);

                // 添加到世界
                worldIn.addFreshEntity(driveEntity);
            }

            // 播放音效
            livingEntity.playSound(
                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                    0.2F,
                    1.45F
            );
        }
    }

    /**
     * 苍穹十二连 Slash Arts
     * 发射多把召唤剑，击中敌人后在敌人位置生成闪电
     */
    @Setter
    @Accessors(chain = true)
    public static class HeavenTwelveHitSlashArts extends ExtendedSlashArts {

        int lightningNumber = 12;
        float attack = 0.1f;
        float lightningAttack = 0.3f;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            Level worldIn = livingEntity.level();

            // 获取攻击目标位置
            Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

            // 创建多把召唤剑
            for(int i = 0; i < lightningNumber; i++) {
                LightningSummonedSword summonedSword = new LightningSummonedSword(
                        RecastingEntities.SUMMOND_SWORD.get(),
                        worldIn,
                        livingEntity,
                        slashBladeState.getColorCode(),
                        lightningAttack
                );


                // 设置属性
                summonedSword.setColor(slashBladeState.getColorCode());
                summonedSword.setModifiedRatio(attack);

                // 设置大小
                summonedSword.setSize(1.25f);

                // 朝向攻击目标位置
                summonedSword.lookAt(attackPos, false);

                // 添加到世界
                worldIn.addFreshEntity(summonedSword);
            }

            // 播放音效
            livingEntity.playSound(
                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                    0.2F,
                    1.45F
            );
        }
    }

    /**
     * 云轮 Slash Arts
     * 在目标位置上方生成多把召唤剑，击中后生成闪电
     */
    @Setter
    @Accessors(chain = true)
    public static class CloudWheelSlashArts extends ExtendedSlashArts {

        float attack = 0.2f;
        int attackNumber = 6;
        float lightningAttack = 0.4f;
        int lightningNumber = 10;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            Level worldIn = livingEntity.level();
            net.minecraft.util.RandomSource random = livingEntity.getRandom();

            // 获取攻击目标位置
            Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

            // 第一阶段：在目标位置上方5格，圆形范围内生成召唤剑
            for(int i = 0; i < attackNumber; i++) {
                SummondSwordEntity summonedSword = new SummondSwordEntity(
                        RecastingEntities.SUMMOND_SWORD.get(),
                        worldIn,
                        livingEntity
                );

                // 在圆形范围内随机生成位置
                Vec3 randomOffset = PosHelper.getRandomVectorInCircle(random, 2.5f);
                Vec3 pos = attackPos.add(0, 5, 0).add(randomOffset);
                summonedSword.setPos(pos.x, pos.y, pos.z);

                // 设置属性
                summonedSword.setColor(slashBladeState.getColorCode());
                summonedSword.setModifiedRatio(attack);
                summonedSword.setStartDelay(5);
                summonedSword.setRoll(random.nextInt(360));

                summonedSword.lookAt(attackPos, false);

                // 添加到世界
                worldIn.addFreshEntity(summonedSword);
            }

            // 第二阶段：在目标位置上方7格生成一把黄色召唤剑
            {
                LightningSummonedSword summonedSword = new LightningSummonedSword(
                        RecastingEntities.SUMMOND_SWORD.get(),
                        worldIn,
                        livingEntity,
                        0xFFFF00, // 黄色
                        lightningAttack
                );

                Vec3 pos = attackPos.add(0, 7, 0);
                summonedSword.setPos(pos.x, pos.y, pos.z);

                // 设置属性
                summonedSword.setColor(0xFFFF00); // 黄色
                summonedSword.setModifiedRatio(attack);
                summonedSword.setStartDelay(10);

                // 设置大小
                summonedSword.setSize(1.25f);

                // 朝向攻击目标位置
                summonedSword.lookAt(attackPos, false);

                // 添加到世界
                worldIn.addFreshEntity(summonedSword);
            }

            // 第三阶段：生成多把黄色召唤剑（如果 lightningNumber > 0）
            if (lightningNumber > 0) {
                for(int i = 0; i < lightningNumber; i++) {
                    LightningSummonedSword summonedSword = new LightningSummonedSword(
                            RecastingEntities.SUMMOND_SWORD.get(),
                            worldIn,
                            livingEntity,
                            0xFFFF00, // 黄色
                            lightningAttack
                    );

                    // 设置属性
                    summonedSword.setColor(0xFFFF00); // 黄色
                    summonedSword.setModifiedRatio(attack);
                    summonedSword.setStartDelay(i * 2);

                    // 设置大小
                    summonedSword.setSize(1.25f);

                    // 朝向攻击目标位置
                    summonedSword.lookAt(attackPos, false);

                    // 添加到世界
                    worldIn.addFreshEntity(summonedSword);
                }
            }

            livingEntity.playSound(
                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                    0.2F,
                    1.45F
            );
        }
    }

    @Setter
    @Accessors(chain = true)
    public static class StellarRotationSlashArts extends ExtendedSlashArts {
        float attack = 0.01f;
        float moveRange = 32;
        float size = 3;
        int attackInterval = 1;
        int life = 60;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {
            Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

            List<LivingEntity> entityList = EntityHelper.getTargettableLivingEntityWithinAABB(livingEntity.level(), livingEntity, attackPos, moveRange);

            for(Entity entity : entityList) {
                entity.setPos(attackPos);
            }

            StellarRotationEntity jc = new StellarRotationEntity(
                    RecastingEntities.STELLAR_ROTATION.get(),
                    livingEntity.level(),
                    livingEntity
            );
            jc.setPos(attackPos.x, attackPos.y, attackPos.z);
            jc.setColor(slashBladeState.getColorCode());
            jc.setModifiedRatio(attack);
            jc.setMaxLifeTime(life);
            jc.setAttackInterval(attackInterval);
            jc.setSize(size);

            jc.attackActionCallbackPoint.register(e -> e.setDeltaMovement(Vec3.ZERO));

            jc.level().addFreshEntity(jc);
            livingEntity.playSound(
                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                    0.2F,
                    1.45F
            );
        }
    }

    /**
     * 急行幻影剑 Slash Arts
     * 在目标位置周围召唤多把幻影剑
     */
    @Setter
    @Accessors(chain = true)
    public static class RapidPhantomSwordsSlashArts extends ExtendedSlashArts {

        float attack = 0.15f;
        int number = 12;
        float range = 12f;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            Level worldIn = livingEntity.level();

            // 获取攻击目标位置
            Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

            // 获取范围内的所有实体
            List<Entity> entityList = EntityHelper.getTargettableEntitiesWithinAABB(
                    worldIn,
                    livingEntity,
                    attackPos,
                    range
            );

            // 创建多把召唤剑
            for(int i = 0; i < number; i++) {
                SummondSwordEntity summonedSword = new SummondSwordEntity(
                        RecastingEntities.SUMMOND_SWORD.get(),
                        worldIn,
                        livingEntity
                );

                // 设置属性
                summonedSword.setColor(slashBladeState.getColorCode());
                summonedSword.setModifiedRatio(attack);
                summonedSword.setStartDelay(livingEntity.getRandom().nextInt(10));


                // 设置朝向：如果有敌人，朝向随机敌人，否则朝向攻击位置
                if (!entityList.isEmpty()) {
                    Entity target = entityList.get(livingEntity.getRandom().nextInt(entityList.size()));
                    Vec3 targetPos = new Vec3(
                            target.getX(),
                            target.getY() + target.getEyeHeight() * 0.5,
                            target.getZ()
                    );
                    summonedSword.lookAt(targetPos, false);
                } else {
                    summonedSword.lookAt(attackPos, false);
                }

                // 添加到世界
                worldIn.addFreshEntity(summonedSword);
            }

            // 播放音效
            livingEntity.playSound(
                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                    0.2F,
                    1.45F
            );
        }
    }

    /**
     * 碎段 Slash Arts
     * 发动一次斩击，设置重复攻击和取消击退
     */
    @Setter
    @Accessors(chain = true)
    public static class FragmentSlashArts extends ExtendedSlashArts {

        float attack = 0.3f;
        int life = 10;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {


            // 调用 AttackHelper.doSlash
            SlashEffectEntity slashEffectEntity = AttackHelper.doSlash(
                    livingEntity,
                    135f,  // roll
                    slashBladeState.getColorCode(),
                    Vec3.ZERO,
                    false,  // mute
                    true,   // critical
                    new DamageStructure(attack, 0),
                    propertiesDefinitionExtension.attackDistance(),
                    KnockBacks.cancel
            );

            if (slashEffectEntity == null) {
                return;
            }

            slashEffectEntity.setMaxLifeTime(life);
            slashEffectEntity.setRepeatedAttack(true);
        }
    }

    /**
     * 引雷 Slash Arts
     * 在目标位置召唤一道闪电
     */
    @Setter
    @Accessors(chain = true)
    public static class LightningCallSlashArts extends ExtendedSlashArts {

        float attack = 0.5f;
        int life = 20;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            Level worldIn = livingEntity.level();

            // 获取攻击目标位置
            Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

            // 创建闪电实体
            LightningEntity lightningEntity = new LightningEntity(
                    RecastingEntities.LIGHTNING.get(),
                    worldIn,
                    livingEntity
            );

            // 设置位置
            lightningEntity.setPos(attackPos.x, attackPos.y, attackPos.z);

            // 设置属性
            lightningEntity.setModifiedRatio(attack);
            lightningEntity.setMaxLifeTime(life);

            // 添加到世界
            worldIn.addFreshEntity(lightningEntity);
        }
    }

    /**
     * 穷观阵 Slash Arts
     * 在目标位置创建一个穷观阵，持续造成伤害，同时叠加演算buff层数，演算使目标受到的伤害更高
     */
    @Setter
    @Accessors(chain = true)
    public static class MatrixSlashArts extends ExtendedSlashArts {

        static final Map<Entity, MatrixEntity> matrixEntity = new WeakHashMap<>();

        float attack = 0.02f;
        int attackInterval = 10;
        int life = 200;
        float size = 16;

        ResourceLocation saTexture = R.Models.Special.matrix$png;
        ResourceLocation saModel = R.Models.Special.matrix$obj;

        /**
         * 从 Map 中移除指定实体的穷观阵记录
         * 当 MatrixEntity 被移除时调用此方法清理记录
         */
        public static void removeMatrixEntity(Entity entity) {
            matrixEntity.remove(entity);
        }

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {
            // 如果已存在穷观阵，先移除旧的
            if (matrixEntity.containsKey(livingEntity)) {
                MatrixEntity matrix = matrixEntity.get(livingEntity);
                if (matrix.isAlive()) {
                    matrix.remove(RemovalReason.DISCARDED);
                }
            }

            Level worldIn = livingEntity.level();
            if (worldIn.isClientSide()) {
                return;
            }

            // 获取攻击目标位置
            Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

            // 创建穷观阵实体
            MatrixEntity matrix = new MatrixEntity(
                    RecastingEntities.MATRIX.get(),
                    worldIn,
                    livingEntity
            ) {
                @Override
                public void remove(@NotNull RemovalReason reason) {
                    super.remove(reason);
                    LivingEntity shooter = getShooter();
                    if (shooter != null) {
                        removeMatrixEntity(shooter);
                    }
                }
            };

            // 设置位置
            matrix.setPos(attackPos.x, attackPos.y + 0.01, attackPos.z);

            // 设置模型和纹理
            matrix.setModel(saModel);
            matrix.setTexture(saTexture);

            // 设置属性
            matrix.setMaxLifeTime(life);
            matrix.setAttackInterval(attackInterval);

            matrix.setModifiedRatio(attack);
            matrix.setColor(slashBladeState.getColorCode());
            matrix.setSize(size);

            // 添加攻击类型
            matrix.setAttackTypeModelList(List.of(RecastingAttackTypes.MATRIX.get()));

            // 添加攻击回调：命中时给目标添加混乱层buff
            matrix.attackActionCallbackPoint.register(hitEntity -> hitEntity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                    buffStackData -> {
                        Level world = hitEntity.level();
                        BuffType chaosLayerBuffType = RecastingBuffTypes.CALCULUS.get();

                        // 获取当前层数
                        int currentLevel = buffStackData.getLevel(chaosLayerBuffType, world);

                        // 增加层数
                        int newLevel = currentLevel + 1;
                        buffStackData.setLevel(chaosLayerBuffType, newLevel, world);

                        KnockBacks.cancel.action.accept(hitEntity);
                    }
            ));

            // 添加到世界
            worldIn.addFreshEntity(matrix);
            matrixEntity.put(livingEntity, matrix);
        }
    }

    /**
     * 幻影爆破 Slash Arts
     * 在目标周围产生多组螺旋幻影剑，围绕目标旋转
     */
    @Setter
    @Accessors(chain = true)
    public static class PhantomExplosionSlashArts extends ExtendedSlashArts {

        float attack = 0.02f;
        int minCount = 12;
        int maxCount = 24;
        float minTiltAngle = 0f;
        float maxTiltAngle = 30f;
        int groupCount = 3;
        int groupInterval = 5;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {
            Level worldIn = livingEntity.level();

            // 获取目标实体
            Entity targetEntity = slashBladeState.getTargetEntity(worldIn);
            LivingEntity target;

            // 如果有锁定目标且有效，使用锁定目标
            if (targetEntity != null && targetEntity.isAlive() && !targetEntity.isRemoved() && targetEntity instanceof LivingEntity) {
                target = (LivingEntity) targetEntity;
            } else {
                // 如果没有锁定目标，从看向位置附近选择最近的敌人
                Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

                // 获取看向位置附近的可攻击敌人
                List<LivingEntity> nearbyEntities = EntityHelper.getTargettableLivingEntityWithinAABB(
                        worldIn,
                        livingEntity,
                        attackPos,
                        16f  // 搜索范围
                );

                // 选择最近的敌人
                if (!nearbyEntities.isEmpty()) {
                    target = nearbyEntities.stream()
                            .min((e1, e2) -> {
                                double dist1 = e1.position().distanceToSqr(attackPos);
                                double dist2 = e2.position().distanceToSqr(attackPos);
                                return Double.compare(dist1, dist2);
                            })
                            .orElse(null);
                } else {
                    target = null;
                }
            }

            // 如果没有找到目标，直接返回
            if (target == null) {
                return;
            }

            // 获取实体的定时器
            LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);

            timeRunOptional.ifPresent(timeRun -> {
                // 生成多组，每组间隔 groupInterval tick
                for(int group = 0; group < groupCount; group++) {
                    int delay = group * groupInterval;

                    timeRun.addTimerCell(
                            () -> {
                                // 检查目标是否还活着，如果已死亡则取消后续进程
                                if (!target.isAlive() || target.isRemoved()) {
                                    return;
                                }

                                // 生成一组幻影剑
                                spawnPhantomSwordsGroup(livingEntity, target, slashBladeState, worldIn);
                            },
                            delay
                    );
                }
            });

        }

        /**
         * 生成一组幻影剑
         */
        private void spawnPhantomSwordsGroup(LivingEntity livingEntity, LivingEntity target, ISlashBladeState slashBladeState, Level worldIn) {
            // 随机生成数量（12~24）
            int count = livingEntity.getRandom().nextInt(maxCount - minCount + 1) + minCount;

            // 随机初始角度偏移
            float off = livingEntity.getRandom().nextFloat() * 360;

            for(int i = 0; i < count; i++) {
                SummondSpiralSwordEntity ss = new SummondSpiralSwordEntity(
                        RecastingEntities.SUMMOND_SPIRAL_SWORD.get(),
                        worldIn,
                        livingEntity
                );

                // 设置旋转中心为目标
                ss.setCenterEntity(target);

                // 设置旋转参数（使用辅助方法自动计算修饰参数）
                ss.setRadiusExpansion(2.5f, 12.0f, 30);
                ss.setSpeedDecay(32.0f, 0.3f, 30);
                ss.setRotationAngle(off + (360.0f / count * i));

                // 设置旋转轴：Y 轴，稍微倾斜 10~30°
                float tiltAngle = livingEntity.getRandom().nextFloat() * (maxTiltAngle - minTiltAngle) + minTiltAngle;
                float tiltRad = (float) Math.toRadians(tiltAngle);

                // 在 XZ 平面上随机选择一个方向
                float horizontalAngle = livingEntity.getRandom().nextFloat() * 360f;
                float horizontalRad = (float) Math.toRadians(horizontalAngle);

                // 计算倾斜后的旋转轴向量
                // Y 分量 = cos(θ)
                // X 分量 = sin(θ) * cos(φ)
                // Z 分量 = sin(θ) * sin(φ)
                double y = Math.cos(tiltRad);
                double x = Math.sin(tiltRad) * Math.cos(horizontalRad);
                double z = Math.sin(tiltRad) * Math.sin(horizontalRad);

                ss.setRotationAxis(new Vec3(x, y, z));
                ss.setRotationDirectionOutward(false);
                ss.setIgnoringBlock(true);

                // 设置基本属性
                ss.setModifiedRatio(attack);
                ss.setColor(slashBladeState.getColorCode());
                ss.setRoll(0);
                ss.setStartDelay(30);

                ss.addAttackType(RecastingAttackTypes.NO_SPIRAL_SPECIAL_RECURSION_ATTACK.get());

                worldIn.addFreshEntity(ss);


            }

            livingEntity.playSound(
                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                    0.2F,
                    1.45F
            );
        }
    }

    /**
     * 无限剑制 Slash Arts
     * 在目标位置上方的半球面上生成大量幻影剑，均匀分布并延迟发射
     */
    @Setter
    @Accessors(chain = true)
    public static class UnlimitedBladeWorksSlashArts extends ExtendedSlashArts {

        float attack = 0.04f;
        int totalSwords = 1024;
        int spawnDuration = 40;  // 生成持续时间（tick）
        float sphereRadius = 64f;  // 半球半径
        float targetOffsetRange = 8f;  // 命中点偏移范围
        int maxLaunchDelay = 40;  // 最大发射延迟

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {
            Level worldIn = livingEntity.level();

            // 获取目标位置
            Vec3 targetPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

            // 获取实体的定时器
            LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);

            timeRunOptional.ifPresent(timeRun -> {
                // 计算每个tick需要生成多少把剑
                int swordsPerTick = (int) Math.ceil((double) totalSwords / spawnDuration);

                // 在40 tick内逐步生成剑
                for(int tick = 0; tick < spawnDuration; tick++) {
                    int finalTick = tick;

                    timeRun.addTimerCell(
                            () -> {
                                // 计算这个tick要生成的剑的数量
                                int startIndex = finalTick * swordsPerTick;
                                int endIndex = Math.min(startIndex + swordsPerTick, totalSwords);

                                // 生成这个tick的剑
                                for(int i = startIndex; i < endIndex; i++) {
                                    spawnSwordOnHemisphere(
                                            livingEntity,
                                            worldIn,
                                            targetPos,
                                            i,
                                            totalSwords,
                                            slashBladeState
                                    );
                                }
                            },
                            tick
                    );
                }
            });

            // 播放音效
            livingEntity.playSound(
                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                    0.5F,
                    1.2F
            );
        }

        /**
         * 在半球面上生成一把剑
         */
        private void spawnSwordOnHemisphere(
                LivingEntity livingEntity,
                Level worldIn,
                Vec3 targetPos,
                int index,
                int total,
                ISlashBladeState slashBladeState
        ) {
            net.minecraft.util.RandomSource random = livingEntity.getRandom();

            // 使用 Fibonacci 球面均匀分布算法（仅上半球）
            // 这种方法比随机采样更均匀
            double goldenRatio = (1.0 + Math.sqrt(5.0)) / 2.0;
            double angleIncrement = 2.0 * Math.PI * goldenRatio;

            // 计算极角 theta（0 到 π/2，上半球）
            // 使用 cos(theta) 在 [0, 1] 均匀分布来保证球面均匀
            double cosTheta = (double) index / (double) total;  // 0 到 1
            double theta = Math.acos(cosTheta);  // 0 到 π/2

            // 计算方位角 phi
            double phi = angleIncrement * index;
            phi = phi % (2.0 * Math.PI);  // 限制在 [0, 2π)

            // 球坐标转笛卡尔坐标
            double sinTheta = Math.sin(theta);
            double x = sphereRadius * sinTheta * Math.cos(phi);
            double y = sphereRadius * cosTheta;  // 使用 cosTheta（已知值）更精确
            double z = sphereRadius * sinTheta * Math.sin(phi);

            // 剑的生成位置：目标位置 + 球面偏移
            Vec3 swordPos = targetPos.add(x, y, z);

            // 创建幻影剑
            SummondSwordEntity summonedSword = new SummondSwordEntity(
                    RecastingEntities.SUMMOND_SWORD.get(),
                    worldIn,
                    livingEntity
            );

            // 设置位置
            summonedSword.setPos(swordPos.x, swordPos.y, swordPos.z);

            // 计算带有正态分布偏移的目标位置
            Vec3 offsetTargetPos = calculateGaussianOffset(targetPos, random);

            // 设置朝向偏移后的目标位置
            summonedSword.lookAt(offsetTargetPos, false);

            // 设置属性
            summonedSword.setColor(slashBladeState.getColorCode());
            summonedSword.setModifiedRatio(attack);

            // 计算当前剑在第几个tick生成
            int currentSpawnTick = index / ((int) Math.ceil((double) totalSwords / spawnDuration));

            // 发射延迟 = 等待所有剑生成完毕的时间 + 随机延迟(0~maxLaunchDelay)
            // 等待时间 = (spawnDuration - currentSpawnTick)，确保所有剑都在第40tick后才开始发射
            int waitForAllSpawn = spawnDuration - currentSpawnTick;
            int randomLaunchDelay = random.nextInt(maxLaunchDelay + 1);
            int totalDelay = waitForAllSpawn + randomLaunchDelay;

            summonedSword.setStartDelay(totalDelay);

            // 设置随机旋转
            summonedSword.setRoll(random.nextFloat() * 360.0f);

            // 添加到世界
            worldIn.addFreshEntity(summonedSword);
        }

        /**
         * 计算带有正态分布偏移的目标位置
         */
        private Vec3 calculateGaussianOffset(Vec3 basePos, net.minecraft.util.RandomSource random) {
            // 使用 Box-Muller 变换生成正态分布的偏移
            // 标准差设为 offsetRange/3，使得约99.7%的点在偏移范围内
            double sigma = targetOffsetRange / 3.0;

            // 生成三个独立的正态分布随机数
            double offsetX = random.nextGaussian() * sigma;
            double offsetY = random.nextGaussian() * sigma;
            double offsetZ = random.nextGaussian() * sigma;

            return basePos.add(offsetX, offsetY, offsetZ);
        }
    }

    /**
     * 剑刃风暴 Slash Arts
     * 在玩家周围随机位置生成大量高速旋转的幻影剑，持续攻击周围敌人
     * 类似魔兽世界剑圣的剑刃风暴
     */
    @Setter
    @Accessors(chain = true)
    public static class BladeStormSlashArts extends ExtendedSlashArts {

        float attack = 0.01f;
        int totalSwords = 128;         // 总共生成的剑数量
        float rotationSpeed = 32.0f;  // 旋转速度（度/tick）
        float minRadius = 1.50f;       // 最小半径
        float maxRadius = 4.50f;       // 最大半径
        float minHeightOffset = -2.0f; // 最小高度偏移（相对于玩家中心）
        float maxHeightOffset = 2.0f;  // 最大高度偏移（相对于玩家中心）
        int duration = 60;           // 持续时间（tick）
        float speedVariation = 0.3f;  // 速度随机变化幅度（0.3 = ±30%）
        boolean randomDirection = true; // 是否随机旋转方向（false=统一顺时针，true=随机顺/逆时针）

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            Level worldIn = livingEntity.level();
            net.minecraft.util.RandomSource random = livingEntity.getRandom();

            // 生成大量随机位置的旋转剑
            for(int i = 0; i < totalSwords; i++) {
                SummondSpiralSwordEntity ss = new SummondSpiralSwordEntity(
                        RecastingEntities.SUMMOND_SPIRAL_SWORD.get(),
                        worldIn,
                        livingEntity
                );

                // 设置旋转中心为玩家
                ss.setCenterEntity(livingEntity);

                // 随机半径（在 minRadius 和 maxRadius 之间）
                float randomRadius = minRadius + random.nextFloat() * (maxRadius - minRadius);

                // 随机初始角度（0-360度）
                float randomAngle = random.nextFloat() * 360.0f;

                // 随机高度偏移（在 minHeightOffset 和 maxHeightOffset 之间）
                float randomHeightOffset = minHeightOffset + random.nextFloat() * (maxHeightOffset - minHeightOffset);

                // 随机旋转速度（基础速度 ± speedVariation）
                float speedModifier = 1.0f + (random.nextFloat() * 2.0f - 1.0f) * speedVariation;
                float currentSpeed = rotationSpeed * speedModifier;

                // 控制旋转方向
                if (randomDirection) {
                    // 随机方向：50%概率顺时针，50%逆时针
                    if (random.nextBoolean()) {
                        currentSpeed = -currentSpeed;
                    }
                }

                // 统一朝向：所有剑都朝外
                boolean isOutward = true;

                // 设置旋转参数
                ss.setRotationRadius(randomRadius);
                ss.setRotationSpeed(currentSpeed);
                ss.setRotationRadiusModifier(1.05f);
                ss.setRotationAngle(randomAngle);   // 随机初始角度
                ss.setRotationAxis(new Vec3(0, 1, 0)); // 绕 Y 轴旋转
                ss.setRotationDirectionOutward(isOutward);

                // 设置随机高度偏移，创造立体风暴效果
                ss.setCenterHeightOffset(randomHeightOffset);

                // 关键：启用旋转时攻击，这样剑就会持续造成伤害而不是飞出去
                ss.setCanAttackDuringRotation(true);

                // 设置基本属性
                ss.setModifiedRatio(attack);
                ss.setColor(slashBladeState.getColorCode());
                ss.setRoll(0);

                ss.setStartDelay(duration);
                ss.setMaxLifeTime(duration);

                worldIn.addFreshEntity(ss);
            }

            // 播放音效
            livingEntity.playSound(
                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                    0.4F,
                    1.2F
            );
        }
    }

    /**
     * 斩铁式·极 Slash Arts
     * 在目标位置超高频率连续发动大量斩击（乱舞的远程版本）
     * 类似瞬狱杀的效果
     */
    @Setter
    @Accessors(chain = true)
    public static class ZantetsudenMaxSlashArts extends ExtendedSlashArts {

        int attackNumber = 25;        // 攻击次数
        int delay = 1;                // 每次攻击间隔（tick）
        float hit = 0.03f;             // 每次伤害倍率
        float range = 3.0f;           // 攻击范围（随机偏移）
        int life = 8;                 // 每次斩击的持续时间
        float size = 2.0f;            // 斩击大小

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            Level worldIn = livingEntity.level();

            // 获取攻击目标位置（远程锁定点）
            Vec3 targetPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

            // 在目标位置创建大小为8的次元斩（只生成一次）
            JudgementCutEntity jc = new JudgementCutEntity(
                    RecastingEntities.JUDGEMENT_CUT.get(),
                    worldIn,
                    livingEntity
            );
            jc.setPos(targetPos.x, targetPos.y, targetPos.z);
            jc.setColor(slashBladeState.getColorCode());
            jc.setSize(8.0f);
            jc.setMaxLifeTime(attackNumber * delay);
            worldIn.addFreshEntity(jc);

            // 获取实体的定时器
            LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);

            timeRunOptional.ifPresent(timeRun -> {
                // 超高频率连续攻击
                for(int i = 0; i < attackNumber; i++) {
                    int _delay = delay * i;

                    timeRun.addTimerCell(
                            () -> {
                                // 在目标位置附近随机偏移
                                Vec3 randomOffset = new Vec3(
                                        (livingEntity.getRandom().nextFloat() - 0.5f) * range,
                                        (livingEntity.getRandom().nextFloat() - 0.5f) * range,
                                        (livingEntity.getRandom().nextFloat() - 0.5f) * range
                                );
                                Vec3 attackPos = targetPos.add(randomOffset);

                                // 创建斩击特效
                                SlashEffectEntity slashEffect = new SlashEffectEntity(
                                        RecastingEntities.SLASH_EFFECT.get(),
                                        worldIn,
                                        livingEntity
                                );

                                // 设置位置
                                slashEffect.setPos(attackPos.x, attackPos.y, attackPos.z);

                                // 随机朝向（完全随机的 Yaw 和 Pitch）
                                float randomYaw = livingEntity.getRandom().nextFloat() * 360;
                                float randomPitch = (livingEntity.getRandom().nextFloat() - 0.5f) * 180; // -90 到 +90 度
                                slashEffect.setRot(randomYaw, randomPitch, true);

                                // 随机旋转角度（Roll）
                                float randomRoll = livingEntity.getRandom().nextFloat() * 360;
                                slashEffect.setRoll(randomRoll);

                                // 设置属性
                                slashEffect.setColor(slashBladeState.getColorCode());
                                slashEffect.setModifiedRatio(hit);
                                slashEffect.setMaxLifeTime(life);
                                slashEffect.setSize(size);
                                slashEffect.setThump(true); // 暴击效果

                                // 添加到世界
                                worldIn.addFreshEntity(slashEffect);

                                // 播放音效（音量较小，因为频率很高）
                                worldIn.playSound(null, attackPos.x, attackPos.y, attackPos.z,
                                        SoundEvents.PLAYER_ATTACK_SWEEP,
                                        net.minecraft.sounds.SoundSource.PLAYERS, 0.15F,
                                        1.2F + livingEntity.getRandom().nextFloat() * 0.4F);
                            },
                            _delay
                    );
                }
            });

            // 播放主音效
            livingEntity.playSound(
                    SoundEvents.PLAYER_ATTACK_SWEEP,
                    1.0F,
                    0.8F
            );
        }
    }

    /**
     * 斩铁式·行 Slash Arts
     * 在目标位置向各个方向发射大量驱动剑气（DriveEntity）
     * 剑气会从目标位置向四面八方飞散
     */
    @Setter
    @Accessors(chain = true)
    public static class ZantetsudenRowSlashArts extends ExtendedSlashArts {

        int driveNumber = 20;        // 剑气数量
        int delay = 1;                // 每次生成间隔（tick）
        float attack = 0.015f;         // 每次伤害倍率
        float speed = 2.5f;           // 剑气速度
        int life = 20;                 // 剑气持续时间
        float size = 2.0f;             // 剑气大小
        float range = 2.0f;            // 生成位置随机偏移范围
        boolean ignoreBlock = true;   // 是否穿透墙体

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            Level worldIn = livingEntity.level();

            // 获取攻击目标位置（远程锁定点）
            Vec3 targetPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

            // 在目标位置创建大小为8的次元斩（只生成一次）
            JudgementCutEntity jc = new JudgementCutEntity(
                    RecastingEntities.JUDGEMENT_CUT.get(),
                    worldIn,
                    livingEntity
            );
            jc.setPos(targetPos.x, targetPos.y, targetPos.z);
            jc.setColor(slashBladeState.getColorCode());
            jc.setSize(8.0f);
            jc.setMaxLifeTime(driveNumber * delay);
            worldIn.addFreshEntity(jc);

            // 获取实体的定时器
            LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);

            timeRunOptional.ifPresent(timeRun -> {
                // 延迟生成驱动剑气
                for(int i = 0; i < driveNumber; i++) {
                    int _delay = delay * i;

                    timeRun.addTimerCell(
                            () -> {
                                net.minecraft.util.RandomSource random = livingEntity.getRandom();

                                DriveEntity driveEntity = new DriveEntity(
                                        RecastingEntities.DRIVE.get(),
                                        worldIn,
                                        livingEntity
                                );

                                // 在目标位置附近随机偏移
                                Vec3 randomOffset = new Vec3(
                                        (random.nextFloat() - 0.5f) * range,
                                        (random.nextFloat() - 0.5f) * range,
                                        (random.nextFloat() - 0.5f) * range
                                );
                                Vec3 spawnPos = targetPos.add(randomOffset);

                                // 设置位置
                                driveEntity.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

                                // 随机朝向（向各个方向发射）
                                float randomYaw = random.nextFloat() * 360;
                                float randomPitch = (random.nextFloat() - 0.5f) * 180; // -90 到 +90 度
                                driveEntity.setRot(randomYaw, randomPitch, true);

                                // 计算目标方向（从生成位置向随机方向）
                                Vec3 direction = new Vec3(
                                        Math.cos(Math.toRadians(randomYaw)) * Math.cos(Math.toRadians(randomPitch)),
                                        Math.sin(Math.toRadians(randomPitch)),
                                        Math.sin(Math.toRadians(randomYaw)) * Math.cos(Math.toRadians(randomPitch))
                                );
                                Vec3 targetDirection = spawnPos.add(direction.scale(10)); // 向前10格作为目标点

                                // 设置属性
                                driveEntity.setColor(slashBladeState.getColorCode());
                                driveEntity.setModifiedRatio(attack);
                                driveEntity.setMaxLifeTime(life);
                                driveEntity.setSize(size);
                                driveEntity.setSeep(speed);
                                driveEntity.setParameter(ignoreBlock); // 是否穿透墙体
                                driveEntity.setRoll(random.nextFloat() * 360); // 随机旋转角度

                                // 设置朝向和速度
                                driveEntity.lookAt(targetDirection, false);

                                // 添加到世界
                                worldIn.addFreshEntity(driveEntity);

                                // 播放音效（音量较小，因为频率很高）
                                worldIn.playSound(null, spawnPos.x, spawnPos.y, spawnPos.z,
                                        SoundEvents.CHORUS_FRUIT_TELEPORT,
                                        net.minecraft.sounds.SoundSource.PLAYERS, 0.15F,
                                        1.2F + random.nextFloat() * 0.4F);
                            },
                            _delay
                    );
                }
            });

            // 播放主音效
            livingEntity.playSound(
                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                    0.4F,
                    1.2F
            );
        }
    }

    /**
     * 业火 Slash Arts
     * 召唤 size 为 6 的红色次元斩，无重复攻击，为攻击目标附加灵魂燃烧
     */
    @Setter
    @Accessors(chain = true)
    public static class InfernoSlashArts extends ExtendedSlashArts {

        int soulBurnLevel = 4;  // 默认附加4层灵魂燃烧

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            Level worldIn = livingEntity.level();

            // 获取攻击目标位置
            Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

            // 创建次元斩
            JudgementCutEntity jc = new JudgementCutEntity(
                    RecastingEntities.JUDGEMENT_CUT.get(),
                    worldIn,
                    livingEntity
            );

            // 设置位置
            jc.setPos(attackPos.x, attackPos.y, attackPos.z);

            // 设置颜色为红色
            jc.setColor(0xFF0000);

            // 设置大小为 6
            jc.setSize(6.0f);

            // 设置无重复攻击
            jc.setRepeatedAttack(false);

            // 添加攻击回调：为攻击目标附加灵魂燃烧
            int finalSoulBurnLevel = soulBurnLevel;
            jc.attackActionCallbackPoint.register(hitEntity -> {
                hitEntity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                        buffStackData -> {
                            Level world = hitEntity.level();
                            BuffType soulBurnBuffType = RecastingBuffTypes.SOUL_BURN.get();

                            // 获取当前层数
                            int currentLevel = buffStackData.getLevel(soulBurnBuffType, world);

                            // 附加指定层数灵魂燃烧
                            int newLevel = currentLevel + finalSoulBurnLevel;
                            buffStackData.setLevel(soulBurnBuffType, newLevel, world);
                        }
                );
            });

            // 添加到世界
            worldIn.addFreshEntity(jc);

            // 播放音效
            worldIn.playSound(null, jc.getX(), jc.getY(), jc.getZ(),
                    SoundEvents.ENDERMAN_TELEPORT,
                    net.minecraft.sounds.SoundSource.PLAYERS, 0.5F,
                    0.8F / (livingEntity.getRandom().nextFloat() * 0.4F + 0.8F));
        }
    }

    /**
     * 激光光束 Slash Arts
     * 按玩家当前视角延迟连射直线伤害
     */
    @Setter
    @Accessors(chain = true)
    public static class LaserBeamSlashArts extends ExtendedSlashArts {

        int beamCount = 1;
        int delay = 3;
        float attack = 1.0f;
        float range = 20f;
        float radius = 0.75f;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {
            if (livingEntity.level().isClientSide()) {
                return;
            }

            float distMul = propertiesDefinitionExtension.attackDistance();
            float finalRange = range * distMul;
            int color = slashBladeState.getColorCode();
            DamageStructure damageStructure = new DamageStructure(attack, 0);
            List<com.til.recasting.registry.instance.AttackType> attackTypes = List.of(RecastingAttackTypes.LASER_ATTACK.get());

            LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN);
            timeRunOptional.ifPresent(timeRun -> {
                for (int i = 0; i < beamCount; i++) {
                    timeRun.addTimerCell(
                            () -> AttackHelper.attackAlongLook(
                                    livingEntity,
                                    finalRange,
                                    radius,
                                    damageStructure,
                                    attackTypes,
                                    color
                            ),
                            delay * i
                    );
                }
            });

            livingEntity.playSound(SoundEvents.BEACON_ACTIVATE, 0.35F, 1.6F);
        }
    }

}



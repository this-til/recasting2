package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.*;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.mixin.SlashArtsAccessor;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

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

    // 苍穹十二连
    public static final RegistryObject<ExtendedSlashArts> HEAVEN_TWELVE_HIT = registerExtendedSA("heaven_twelve_hit", new HeavenTwelveHitSlashArts());
    public static final RegistryObject<ExtendedSlashArts> HEAVEN_TWELVE_HIT_LAMBDA = registerExtendedSA("heaven_twelve_hit_lambda", new HeavenTwelveHitSlashArts().setLightningNumber(18).setLightningAttack(1.3f).setAttack(0.5f));

    // 云轮
    public static final RegistryObject<ExtendedSlashArts> CLOUD_WHEEL = registerExtendedSA("cloud_wheel", new CloudWheelSlashArts().setLightningNumber(0));
    // 云轮风暴
    public static final RegistryObject<ExtendedSlashArts> CLOUD_WHEEL_STORM = registerExtendedSA("cloud_wheel_storm", new CloudWheelSlashArts().setLightningNumber(7).setAttackNumber(10));

    public static final RegistryObject<ExtendedSlashArts> STELLAR_ROTATION = registerExtendedSA("stellar_rotation", new StellarRotationSlashArts());

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

                // 如果需要集中攻击，则朝向目标位置
                if (concentrate) {
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

        float attack = 0.35f;
        int attackNumber = 8;
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

}



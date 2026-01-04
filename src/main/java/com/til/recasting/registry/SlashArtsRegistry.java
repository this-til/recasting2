package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.capability.TimeRunCapability;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.mixin.SlashArtsAccessor;
import com.til.recasting.mixin_api.IEntityModifiedRatio;
import com.til.recasting.mixin_api.IEntitySize;
import com.til.recasting.mixin_api.ISlashBladeStateExtension;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.entity.EntityAbstractSummonedSword;
import mods.flammpfeil.slashblade.entity.EntityJudgementCut;
import mods.flammpfeil.slashblade.event.client.UserPoseOverrider;
import mods.flammpfeil.slashblade.event.handler.FallHandler;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import mods.flammpfeil.slashblade.util.AttackManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
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

        /**
         * 获取攻击目标位置
         * 优先返回锁定目标的位置，如果没有锁定目标则使用视线追踪
         *
         * @param livingEntity    发起攻击的实体
         * @param slashBladeState 刀剑状态
         * @param maxDistance     最大追踪距离
         * @return 攻击目标位置
         */
        protected static Vec3 getAttackTargetPosition(LivingEntity livingEntity, ISlashBladeState slashBladeState, double maxDistance) {
            Level worldIn = livingEntity.level();

            // 尝试获取锁定的目标
            var target = slashBladeState.getTargetEntity(worldIn);
            if (target != null && target.isAlive() && !target.isRemoved()) {
                return new Vec3(
                        target.getX(),
                        target.getY() + target.getEyeHeight() * 0.5,
                        target.getZ()
                );
            } else {
                // 如果没有锁定目标，使用视线追踪
                Vec3 start = livingEntity.getEyePosition(1.0f);
                Vec3 end = start.add(livingEntity.getLookAngle().scale(maxDistance));
                HitResult result = worldIn.clip(
                        new ClipContext(
                                start,
                                end,
                                ClipContext.Block.COLLIDER,
                                ClipContext.Fluid.NONE,
                                livingEntity
                        )
                );
                return result.getLocation();
            }
        }

        /**
         * 获取攻击目标位置（使用默认距离40）
         *
         * @param livingEntity    发起攻击的实体
         * @param slashBladeState 刀剑状态
         * @return 攻击目标位置
         */
        protected static Vec3 getAttackTargetPosition(LivingEntity livingEntity, ISlashBladeState slashBladeState) {
            return getAttackTargetPosition(livingEntity, slashBladeState, 40.0);
        }

        /**
         * 在圆形范围内生成随机向量
         *
         * @param random 随机数生成器
         * @param radius 圆形半径
         * @return 随机向量
         */
        protected static Vec3 getRandomVectorInCircle(net.minecraft.util.RandomSource random, float radius) {
            // 使用均匀分布生成圆内随机点
            double angle = random.nextDouble() * 2 * Math.PI;
            double r = Math.sqrt(random.nextDouble()) * radius;
            double x = r * Math.cos(angle);
            double z = r * Math.sin(angle);
            return new Vec3(x, 0, z);
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
            LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(TimeRunCapability.TIME_RUN);

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

        int number = 12;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            Level worldIn = livingEntity.level();

            // 获取攻击目标位置
            Vec3 attackPos = getAttackTargetPosition(livingEntity, slashBladeState);

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
                EntityAbstractSummonedSword summonedSword =
                        new EntityAbstractSummonedSword(
                                SlashBlade.RegistryEvents.SummonedSword,
                                worldIn
                        );

                // 设置位置
                summonedSword.setPos(pos.x, pos.y, pos.z);

                // 设置朝向（朝向攻击目标）
                Vec3 dir = attackPos.subtract(pos).normalize();
                summonedSword.shoot(dir.x, dir.y, dir.z, 3.0f, 0.0f);

                // 设置属性
                summonedSword.setOwner(livingEntity);
                summonedSword.setColor(slashBladeState.getColorCode());
                summonedSword.setDelay(i);  // 延迟发射
                summonedSword.setRoll(livingEntity.getRandom().nextFloat() * 360.0f);

                // 添加到世界
                worldIn.addFreshEntity(summonedSword);
            }

            // 播放音效
            livingEntity.playSound(
                    net.minecraft.sounds.SoundEvents.CHORUS_FRUIT_TELEPORT,
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
            Vec3 attackPos = getAttackTargetPosition(livingEntity, slashBladeState);

            // 计算基础生成位置（在实体上方）
            Vec3 basePos = livingEntity.position().add(0, range / 2, 0);

            net.minecraft.util.RandomSource random = livingEntity.getRandom();

            // 创建大量召唤剑
            for(int i = 0; i < attackNumber; i++) {
                EntityAbstractSummonedSword summonedSword =
                        new EntityAbstractSummonedSword(
                                SlashBlade.RegistryEvents.SummonedSword,
                                worldIn
                        );

                // 在圆形范围内随机生成位置
                Vec3 randomOffset = getRandomVectorInCircle(random, range);
                Vec3 pos = basePos.add(randomOffset);

                // 设置位置
                summonedSword.setPos(pos.x, pos.y, pos.z);

                // 设置大小
                //noinspection ConstantValue
                if (summonedSword instanceof IEntitySize) {
                    ((com.til.recasting.mixin_api.IEntitySize) summonedSword).setRecasting$size(0.6f);
                }

                // 设置属性
                summonedSword.setOwner(livingEntity);
                summonedSword.setColor(slashBladeState.getColorCode());
                summonedSword.setDamage(attack);
                summonedSword.setDelay(random.nextInt(60));  // 随机延迟发射
                summonedSword.setRoll(random.nextFloat() * 360.0f);

                // 如果需要集中攻击，则朝向目标位置
                if (concentrate) {
                    Vec3 dir = attackPos.subtract(pos).normalize();
                    summonedSword.shoot(dir.x, dir.y, dir.z, 1.0f, 0.0f);
                } else {
                    // 否则直接向下射击
                    summonedSword.setXRot(90.0f);  // 朝下
                }

                // 添加到世界
                worldIn.addFreshEntity(summonedSword);
            }

            // 播放音效
            livingEntity.playSound(
                    net.minecraft.sounds.SoundEvents.CHORUS_FRUIT_TELEPORT,
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
            LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(TimeRunCapability.TIME_RUN);

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
            Vec3 pos = getAttackTargetPosition(livingEntity, slashBladeState);

            // 创建自定义的黑洞次元斩
            EntityJudgementCut jc = new EntityJudgementCut(SlashBlade.RegistryEvents.JudgementCut, worldIn) {

                @Override
                public void tick() {
                    super.tick();

                    // 每 tick 执行吸引效果
                    if (!this.level().isClientSide()) {
                        Vec3 centerPos = this.position();

                        // 创建 AABB 范围
                        AABB aabb = new AABB(
                                centerPos.x - range, centerPos.y - range, centerPos.z - range,
                                centerPos.x + range, centerPos.y + range, centerPos.z + range
                        );

                        // 获取范围内的所有实体
                        List<Entity> entities =
                                this.level().getEntities(
                                        this,
                                        aabb,
                                        entity -> {
                                            // 过滤条件：必须是生物实体，且不是施法者自己
                                            if (!(entity instanceof LivingEntity)) {
                                                return false;
                                            }
                                            if (entity == this.getOwner()) {
                                                return false;
                                            }
                                            // 可以添加更多过滤条件
                                            return entity.isAlive() && !entity.isSpectator();
                                        }
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
            jc.setOwner(livingEntity);

            // 设置颜色
            itemStack.getCapability(mods.flammpfeil.slashblade.item.ItemSlashBlade.BLADESTATE)
                    .ifPresent(state -> jc.setColor(state.getColorCode()));

            // 设置等级
            livingEntity.getCapability(mods.flammpfeil.slashblade.capability.concentrationrank.ConcentrationRankCapabilityProvider.RANK_POINT)
                    .ifPresent(rank -> jc.setRank(rank.getRankLevel(worldIn.getGameTime())));

            // 设置伤害倍率
            //noinspection ConstantValue
            if (jc instanceof IEntityModifiedRatio entityModifiedRatio) {
                entityModifiedRatio.setRecasting$modifiedRatio(attack);
            }
            jc.setDamage(0);

            // 设置生命时间
            jc.setLifetime(life);

            jc.setColor(slashBladeState.getColorCode());

            // 设置大小
            //noinspection ConstantValue
            if (jc instanceof IEntitySize) {
                ((IEntitySize) jc).setRecasting$size(size);
            }

            // 添加到世界
            worldIn.addFreshEntity(jc);

            // 播放音效
            worldIn.playSound(null, jc.getX(), jc.getY(), jc.getZ(),
                    net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT,
                    net.minecraft.sounds.SoundSource.PLAYERS, 0.5F,
                    0.8F / (livingEntity.getRandom().nextFloat() * 0.4F + 0.8F));
        }

        /**
         * 自定义的黑洞次元斩实体
         * 重写 tick 方法以实现吸引效果
         */
        public static class VoidHoleJudgementCut extends EntityJudgementCut {

            private final float attractRange;
            private final float attractPower;

            public VoidHoleJudgementCut(net.minecraft.world.entity.EntityType<? extends EntityJudgementCut> type, Level level, float attractRange, float attractPower) {
                super(type, level);
                this.attractRange = attractRange;
                this.attractPower = attractPower;
            }

            @Override
            public void tick() {
                super.tick();

                // 每 tick 执行吸引效果
                if (!this.level().isClientSide()) {
                    Vec3 centerPos = this.position();

                    // 创建 AABB 范围
                    AABB aabb = new AABB(
                            centerPos.x - attractRange, centerPos.y - attractRange, centerPos.z - attractRange,
                            centerPos.x + attractRange, centerPos.y + attractRange, centerPos.z + attractRange
                    );

                    // 获取范围内的所有实体
                    List<Entity> entities =
                            this.level().getEntities(
                                    this,
                                    aabb,
                                    entity -> {
                                        // 过滤条件：必须是生物实体，且不是施法者自己
                                        if (!(entity instanceof LivingEntity)) {
                                            return false;
                                        }
                                        if (entity == this.getOwner()) {
                                            return false;
                                        }
                                        // 可以添加更多过滤条件
                                        return entity.isAlive() && !entity.isSpectator();
                                    }
                            );

                    // 对每个实体施加吸引力
                    for(Entity entity : entities) {
                        Vec3 direction = centerPos.subtract(entity.position());
                        double length = direction.length();

                        if (length > attractRange || length < 0.1) {
                            continue;
                        }

                        // 计算力度：距离越近力度越大（平方衰减）
                        double lengthRatio = length / attractRange;
                        double strength = (1 - lengthRatio) * (1 - lengthRatio);
                        double _power = attractPower * attractRange;

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
            LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(TimeRunCapability.TIME_RUN);

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
                                    pos = getAttackTargetPosition(livingEntity, slashBladeState);
                                }

                                // 创建次元斩
                                EntityJudgementCut jc =
                                        new EntityJudgementCut(
                                                SlashBlade.RegistryEvents.JudgementCut,
                                                worldIn
                                        );

                                jc.setPos(pos.x, pos.y, pos.z);
                                jc.setOwner(livingEntity);

                                // 设置颜色
                                itemStack.getCapability(mods.flammpfeil.slashblade.item.ItemSlashBlade.BLADESTATE)
                                        .ifPresent(state -> jc.setColor(state.getColorCode()));

                                // 设置等级
                                livingEntity.getCapability(mods.flammpfeil.slashblade.capability.concentrationrank.ConcentrationRankCapabilityProvider.RANK_POINT)
                                        .ifPresent(rank -> jc.setRank(rank.getRankLevel(worldIn.getGameTime())));

                                // 设置伤害倍率
                                //noinspection ConstantValue
                                if (jc instanceof IEntityModifiedRatio entityModifiedRatio) {
                                    entityModifiedRatio.setRecasting$modifiedRatio(hit);
                                }
                                jc.setDamage(0);

                                // 设置生命时间
                                jc.setLifetime(10);

                                // 添加到世界
                                worldIn.addFreshEntity(jc);

                                // 更新位置记录
                                lastPos[0] = pos;

                                // 播放音效
                                worldIn.playSound(null, jc.getX(), jc.getY(), jc.getZ(),
                                        net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT,
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
            Vec3 attackPos = getAttackTargetPosition(livingEntity, slashBladeState);

            // 创建 AABB 范围
            AABB aabb = new AABB(
                    attackPos.x - attackRange, attackPos.y - attackRange, attackPos.z - attackRange,
                    attackPos.x + attackRange, attackPos.y + attackRange, attackPos.z + attackRange
            );

            // 获取范围内的所有敌对实体
            List<Entity> attackEntities =
                    worldIn.getEntities(
                            livingEntity,
                            aabb,
                            entity -> {
                                if (!(entity instanceof LivingEntity)) {
                                    return false;
                                }
                                if (entity == livingEntity) {
                                    return false;
                                }
                                // 可以添加更多过滤条件（例如：敌对判定）
                                return entity.isAlive() && !entity.isSpectator();
                            }
                    );

            // 获取实体的定时器
            LazyOptional<ITimeRun> timeRunOptional = livingEntity.getCapability(TimeRunCapability.TIME_RUN);

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
                                        Entity candidate =
                                                attackEntities.get(livingEntity.getRandom().nextInt(attackEntities.size()));

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
                                EntityJudgementCut jc =
                                        new EntityJudgementCut(
                                                SlashBlade.RegistryEvents.JudgementCut,
                                                worldIn
                                        );

                                jc.setPos(targetPos.x, targetPos.y, targetPos.z);
                                jc.setOwner(livingEntity);

                                // 设置颜色
                                itemStack.getCapability(mods.flammpfeil.slashblade.item.ItemSlashBlade.BLADESTATE)
                                        .ifPresent(state -> jc.setColor(state.getColorCode()));

                                // 设置等级
                                livingEntity.getCapability(mods.flammpfeil.slashblade.capability.concentrationrank.ConcentrationRankCapabilityProvider.RANK_POINT)
                                        .ifPresent(rank -> jc.setRank(rank.getRankLevel(worldIn.getGameTime())));

                                // 设置伤害倍率
                                //noinspection ConstantValue
                                if (jc instanceof IEntityModifiedRatio entityModifiedRatio) {
                                    entityModifiedRatio.setRecasting$modifiedRatio(hit);
                                }
                                jc.setDamage(0);

                                // 设置生命时间
                                jc.setLifetime(10);

                                // 添加到世界
                                worldIn.addFreshEntity(jc);

                                // 播放音效
                                worldIn.playSound(null, jc.getX(), jc.getY(), jc.getZ(),
                                        net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT,
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
     * 星辰 Slash Arts
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
        int zoneTime = 160;
        float attackProbability = 1 / 20f;
        float summondSwordAttack = 0.02f;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            Level worldIn = livingEntity.level();

            // 获取攻击目标位置
            Vec3 attackPos = getAttackTargetPosition(livingEntity, slashBladeState);

            // 创建 AABB 范围，查找敌人
            AABB aabb = new AABB(
                    attackPos.x - range, attackPos.y - range, attackPos.z - range,
                    attackPos.x + range, attackPos.y + range, attackPos.z + range
            );

            List<Entity> entityList =
                    worldIn.getEntities(
                            livingEntity,
                            aabb,
                            entity -> {
                                if (!(entity instanceof LivingEntity)) {
                                    return false;
                                }
                                if (entity == livingEntity) {
                                    return false;
                                }
                                return entity.isAlive() && !entity.isSpectator();
                            }
                    );

            // 第一阶段：发射初始召唤剑
            for(int i = 0; i < attackNumber; i++) {
                // 使用自定义召唤剑，击中后产生次元斩
                StarSummonedSword summonedSword = new StarSummonedSword(
                        SlashBlade.RegistryEvents.SummonedSword,
                        worldIn,
                        livingEntity,
                        judgementCutAttack
                );

                // 设置位置为玩家位置
                Vec3 spawnPos = livingEntity.position().add(0, 1.5, 0);
                summonedSword.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

                // 设置属性
                summonedSword.setOwner(livingEntity);
                summonedSword.setColor(slashBladeState.getColorCode());
                summonedSword.setDamage(attack);
                summonedSword.setDelay(10 + livingEntity.getRandom().nextInt(10));

                // 设置朝向：如果有敌人，朝向随机敌人，否则朝向攻击位置
                Vec3 targetPos;
                if (!entityList.isEmpty()) {
                    Entity target = entityList.get(livingEntity.getRandom().nextInt(entityList.size()));
                    targetPos = new Vec3(target.getX(), target.getY() + target.getEyeHeight() * 0.5, target.getZ());
                } else {
                    targetPos = attackPos;
                }

                Vec3 direction = targetPos.subtract(spawnPos).normalize();
                summonedSword.shoot(direction.x, direction.y, direction.z, 2.0f, 0.0f);

                // 添加到世界
                worldIn.addFreshEntity(summonedSword);
            }

            // 播放音效
            livingEntity.playSound(
                    net.minecraft.sounds.SoundEvents.CHORUS_FRUIT_TELEPORT,
                    0.2F,
                    1.45F
            );

            // 第二阶段：生成持续的次元斩阵地（如果 zoneNumber > 0）
            if (zoneNumber > 0) {
                for(int i = 0; i < zoneNumber; i++) {
                    // 在玩家周围随机位置生成次元斩
                    Vec3 randomOffset = getRandomVectorInCircle(livingEntity.getRandom(), 16);
                    Vec3 zonePos = livingEntity.position().add(randomOffset);

                    // 创建持续存在的次元斩
                    EntityJudgementCut starJC = new EntityJudgementCut(
                            SlashBlade.RegistryEvents.JudgementCut,
                            worldIn
                    ) {
                        @Override
                        public void tick() {
                            super.tick();

                            if (!this.level().isClientSide() && this.random.nextFloat() < attackProbability) {
                                Vec3 pos = this.position();

                                // 实时获取目标位置
                                Vec3 currentTargetPos = getAttackTargetPosition(livingEntity, slashBladeState);

                                // 使用自定义召唤剑，击中后产生次元斩
                                StarSummonedSword summonedSword = new StarSummonedSword(
                                        SlashBlade.RegistryEvents.SummonedSword,
                                        this.level(),
                                        livingEntity,
                                        judgementCutAttack
                                );

                                summonedSword.setPos(pos.x, pos.y, pos.z);
                                summonedSword.setOwner(livingEntity);
                                summonedSword.setColor(slashBladeState.getColorCode());
                                summonedSword.setDamage(summondSwordAttack);
                                summonedSword.setDelay(10);

                                // 朝向实时获取的目标位置
                                Vec3 direction = currentTargetPos.subtract(pos).normalize();
                                summonedSword.shoot(direction.x, direction.y, direction.z, 2.0f, 0.0f);

                                this.level().addFreshEntity(summonedSword);

                                // 播放音效
                                this.level().playSound(null, pos.x, pos.y, pos.z,
                                        net.minecraft.sounds.SoundEvents.CHORUS_FRUIT_TELEPORT,
                                        net.minecraft.sounds.SoundSource.PLAYERS, 0.2F, 1.45F);
                            }
                        }
                    };

                    starJC.setPos(zonePos.x, zonePos.y, zonePos.z);
                    starJC.setOwner(livingEntity);
                    starJC.setColor(slashBladeState.getColorCode());
                    starJC.setDamage(0);  // 这个次元斩本身不造成伤害
                    starJC.setLifetime(zoneTime);

                    worldIn.addFreshEntity(starJC);
                }
            }
        }

        /**
         * 自定义召唤剑 - 击中后产生次元斩
         */
        public static class StarSummonedSword extends EntityAbstractSummonedSword {

            private final LivingEntity caster;
            private final float judgementCutAttack;

            public StarSummonedSword(
                    net.minecraft.world.entity.EntityType<? extends EntityAbstractSummonedSword> type,
                    Level level,
                    LivingEntity caster,
                    float judgementCutAttack
            ) {
                super(type, level);
                this.caster = caster;
                this.judgementCutAttack = judgementCutAttack;
            }

            @Override
            protected void onHitEntity(net.minecraft.world.phys.EntityHitResult entityHitResult) {
                // 先调用父类的击中逻辑（造成伤害等）
                super.onHitEntity(entityHitResult);

                // 在击中位置生成次元斩
                if (!this.level().isClientSide()) {
                    Entity hitEntity = entityHitResult.getEntity();

                    // 确定次元斩生成位置（被击中实体的位置）
                    Vec3 jcPos = hitEntity.position().add(0, hitEntity.getEyeHeight() * 0.5, 0);

                    // 创建次元斩
                    EntityJudgementCut jc =
                            new EntityJudgementCut(
                                    SlashBlade.RegistryEvents.JudgementCut,
                                    this.level()
                            );

                    jc.setPos(jcPos.x, jcPos.y, jcPos.z);
                    jc.setOwner(caster);
                    jc.setColor(this.getColor());

                    // 设置伤害倍率
                    //noinspection ConstantValue
                    if (jc instanceof IEntityModifiedRatio entityModifiedRatio) {
                        entityModifiedRatio.setRecasting$modifiedRatio(judgementCutAttack);
                    }
                    jc.setDamage(0);

                    // 设置生命时间
                    jc.setLifetime(10);

                    // 添加到世界
                    this.level().addFreshEntity(jc);

                    // 播放音效
                    this.level().playSound(null, jcPos.x, jcPos.y, jcPos.z,
                            net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT,
                            net.minecraft.sounds.SoundSource.PLAYERS, 0.5F,
                            0.8F / (this.level().getRandom().nextFloat() * 0.4F + 0.8F));
                }
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

        float attack = 0.45f;
        int attackNumber = 8;
        int life = 80;
        float range = 1;

        @Override
        public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

            Level worldIn = livingEntity.level();

            // 获取玩家视线方向
            Vec3 lookAngle = livingEntity.getLookAngle();

            // 生成多个驱动剑气
            for(int i = 0; i < attackNumber; i++) {
                mods.flammpfeil.slashblade.entity.EntityDrive driveEntity =
                        new mods.flammpfeil.slashblade.entity.EntityDrive(
                                SlashBlade.RegistryEvents.Drive,
                                worldIn
                        );

                // 设置位置为玩家眼睛位置稍微向前
                Vec3 pos = livingEntity.position()
                        .add(0, livingEntity.getEyeHeight() * 0.75, 0)
                        .add(lookAngle.scale(0.3f));
                driveEntity.setPos(pos.x, pos.y, pos.z);

                // 设置属性
                driveEntity.setOwner(livingEntity);
                driveEntity.setColor(slashBladeState.getColorCode());

                // 设置尺寸：随机尺寸 * 攻击距离
                float randomSize = livingEntity.getRandom().nextFloat() * range;
                driveEntity.setBaseSize(randomSize * propertiesDefinitionExtension.attackDistance());

                // 设置伤害
                driveEntity.setDamage(attack);

                // 设置生命时间
                driveEntity.setLifetime(life);

                // 设置随机旋转角度（Roll）
                driveEntity.setRotationRoll(livingEntity.getRandom().nextInt(360));

                // 设置速度
                driveEntity.setSpeed(0.45f);

                // 向前发射（使用玩家的视线方向）
                driveEntity.shoot(lookAngle.x, lookAngle.y, lookAngle.z, driveEntity.getSpeed(), 0.0f);

                // 添加到世界
                worldIn.addFreshEntity(driveEntity);
            }

            // 播放音效
            livingEntity.playSound(
                    net.minecraft.sounds.SoundEvents.CHORUS_FRUIT_TELEPORT,
                    0.2F,
                    1.45F
            );
        }
    }
}



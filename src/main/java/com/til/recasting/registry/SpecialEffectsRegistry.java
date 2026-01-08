package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.TimeRunCapability;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.entity.DriveEntity;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.entity.SlashEffectEntity;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.event.DoSlashExtendEvent;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.PosHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.VectorHelper;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.util.DamageStructure;
import com.til.recasting.util.NumberPack;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Special Effects (SE) 注册表
 */
public class SpecialEffectsRegistry {
    /**
     * 创建 DeferredRegister，用于注册 Special Effects
     */
    public static final DeferredRegister<SpecialEffect> SPECIAL_EFFECT = DeferredRegister.create(
            SpecialEffect.REGISTRY_KEY,
            Recasting.MODID
    );


    public static final RegistryObject<SpecialEffect> COOPERATE_WITH = registerExtendedSE("cooperate_with", CooperateWithSpecialEffect::new);
    // 十字斩 - 挥刀时追加一道剑气
    public static final RegistryObject<SpecialEffect> CROSS_CHOP = registerExtendedSE("cross_chop", CrossChopSpecialEffect::new);
    // 剑气释放 - 挥刀时有概率发出剑气
    public static final RegistryObject<SpecialEffect> DRIVE_RELEASE = registerExtendedSE("drive_release", DriveReleaseSpecialEffect::new);
    // 生长 - 挥刀时恢复生命
    public static final RegistryObject<SpecialEffect> GROWTH = registerExtendedSE("growth", GrowthSpecialEffect::new);
    // 回溯 - 挥刀时恢复耐久
    public static final RegistryObject<SpecialEffect> REGRESSION = registerExtendedSE("regression", RegressionSpecialEffect::new);
    // 断罪 - 触发SA时追加次元斩攻击
    public static final RegistryObject<SpecialEffect> JUDGEMENT = registerExtendedSE("judgement", JudgementSpecialEffect::new);
    // 冲击 - 造成伤害有几率召唤幻影剑造成瞬间伤害
    public static final RegistryObject<SpecialEffect> IMPACT = registerExtendedSE("impact", ImpactSpecialEffect::new);
    // 过载 - 挥刀时小概率触发审判
    public static final RegistryObject<SpecialEffect> OVERLOAD = registerExtendedSE("overload", OverloadSpecialEffect::new);
    // 抵抗 - 挥刀时获得伤害吸收
    public static final RegistryObject<SpecialEffect> RESIST = registerExtendedSE("resist", ResistSpecialEffect::new);
    // 断却 - 触发次元斩之后造成一次大伤害和大范围的劈砍
    public static final RegistryObject<SpecialEffect> SEVER_BREAK = registerExtendedSE("sever_break", SeverBreakSpecialEffect::new);
    // 风暴 - 触发审判时，召唤幻影剑进行攻击
    public static final RegistryObject<SpecialEffect> STORM = registerExtendedSE("storm", StormSpecialEffect::new);
    // 风暴.变体 - 触发审判时，从上方召唤幻影剑进行攻击
    public static final RegistryObject<SpecialEffect> STORM_VARIANT = registerExtendedSE("storm_variant", StormVariantSpecialEffect::new);

    // ==================== 攻击类型增幅 SE ====================
    // 太虚 - 幻影剑增幅
    public static final RegistryObject<SpecialEffect> GREAT_VOID = registerExtendedSE("great_void", () -> new AttackAmplifierSpecialEffect(RecastingAttackTypes.SUMMOND_SWORD_ATTACK, new NumberPack(0.1f, 0.1f)));
    // 斩击精通 - 斩击增幅
    public static final RegistryObject<SpecialEffect> SLASH_MASTERY = registerExtendedSE("slash_mastery", () -> new AttackAmplifierSpecialEffect(RecastingAttackTypes.SLASH_EFFECT_ATTACK, new NumberPack(0.1f, 0.1f)));
    // 震荡 - 次元斩增幅
    public static final RegistryObject<SpecialEffect> SHOCK = registerExtendedSE("shock", () -> new AttackAmplifierSpecialEffect(RecastingAttackTypes.JUDGEMENT_CUT_ATTACK, new NumberPack(0.2f, 0.15f)));
    // 剑气纵横 - 剑气增幅
    public static final RegistryObject<SpecialEffect> SWORD_QI_MASTERY = registerExtendedSE("sword_qi_mastery", () -> new AttackAmplifierSpecialEffect(RecastingAttackTypes.DRIVE_ATTACK, new NumberPack(0.2f, 0.15f)));
    // 雷霆万钧 - 闪电增幅
    public static final RegistryObject<SpecialEffect> THUNDER_STRIKE = registerExtendedSE("thunder_strike", () -> new AttackAmplifierSpecialEffect(RecastingAttackTypes.LIGHTNING_ATTACK, new NumberPack(0.2f, 0.15f)));

    // ==================== 特殊刀 SE ====================
    // 黑色玫瑰 - 叠加伤害，每 tick 造成伤害，伤害减半
    public static final RegistryObject<SpecialEffect> BLACK_ROSE = registerExtendedSE("black_rose", () -> new BlackRoseSpecialEffect().setMaxLevel(1));

    public static RegistryObject<SpecialEffect> registerExtendedSE(String name, Supplier<SpecialEffect> factory) {
        return SPECIAL_EFFECT.register(name, factory);
    }

    @Accessors(chain = true)
    public static class ExtendedSpecialEffect extends SpecialEffect {

        @Getter
        @Setter
        int maxLevel = 5;

        public ExtendedSpecialEffect() {
            super(0, false, false);
            MinecraftForge.EVENT_BUS.register(this);
        }


        public boolean hasSpecialEffect(ISlashBladeState slashBladeState) {
            return slashBladeState.hasSpecialEffect(
                    mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getKey(this)
            );
        }

        public int getLevel(PropertiesDefinitionExtension propertiesDefinitionExtension) {
            if (propertiesDefinitionExtension == null) {
                return 0;
            }

            // 获取当前 SpecialEffect 的 ResourceLocation
            IForgeRegistry<SpecialEffect> registry = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get();
            ResourceLocation resourceLocation = registry.getKey(this);

            if (resourceLocation == null) {
                return 0;
            }

            // 从 extendedSpecialLevels 中获取等级
            return propertiesDefinitionExtension.extendedSpecialLevels()
                    .getOrDefault(resourceLocation, 0);
        }

        protected PropertiesDefinitionExtension getPropertiesDefinitionExtension(ItemStack itemStack) {
            if (itemStack == null || itemStack.isEmpty()) {
                return null;
            }
            //noinspection DataFlowIssue
            return itemStack.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                    .orElse(null);
        }

        public String getDescId() {
            return getDescriptionId() + ".desc";
        }

        @Override
        public String toString() {
            try {
                return super.toString();
            } catch (Exception ignored) {
            }
            return this.getClass().getSimpleName();
        }

    }

    public static class AttackAmplifierSpecialEffect extends ExtendedSpecialEffect {
        RegistryObject<AttackType> attackType;
        NumberPack attack;

        public AttackAmplifierSpecialEffect(RegistryObject<AttackType> attackType, NumberPack attack) {
            super();
            this.attackType = attackType;
            this.attack = attack;
        }

        @SubscribeEvent
        public void onEvent(AttackAmplifierEvent event) {
            // 检查是否拥有此特效
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            // 检查攻击类型是否匹配
            if (!event.getAttackTypeList().contains(attackType.get())) {
                return;
            }
            // 添加伤害倍率加成
            PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getItem());
            event.addModifiedRatioAmplifier(attack.of(getLevel(properties)));
        }
    }


    /***
     * 协同攻击
     * 挥刀的时概率额外挥刀
     */
    public static class CooperateWithSpecialEffect extends ExtendedSpecialEffect {

        NumberPack probability = new NumberPack(0.2f, 0.05f);
        NumberPack attackRatio = new NumberPack(0, 0.1f);
        int delay = 10;

        @SubscribeEvent
        public void onEvent(DoSlashExtendEvent event) {
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getBlade());
            int level = getLevel(properties);

            if (event.getUser().getRandom().nextFloat() >= probability.of(level)) {
                return;
            }

            event.getUser().getCapability(TimeRunCapability.TIME_RUN).ifPresent(
                    timeRun -> timeRun.addTimerCell(
                            () -> AttackHelper.doSlash(
                                    event.getUser(),
                                    event.getRoll(),
                                    event.getSlashBladeState().getColorCode(),
                                    event.getCenterOffset(),
                                    event.isMute(),
                                    event.isCritical(),
                                    new DamageStructure(
                                            event.getModifiedRatio() * attackRatio.of(level),
                                            (float) (event.getDamage() * attackRatio.of(level))
                                    ),
                                    event.getAttackRange(),
                                    null
                            ),
                            delay
                    )
            );


        }

    }

    /***
     * 十字斩
     * 挥刀时追加一道剑气
     */
    public static class CrossChopSpecialEffect extends ExtendedSpecialEffect {

        NumberPack attackRatio = new NumberPack(0f, 0.1f);

        @SubscribeEvent
        public void onEvent(DoSlashExtendEvent event) {
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }
            if (event.getUser().level().isClientSide()) {
                return;
            }

            PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getBlade());
            int level = getLevel(properties);

            // 计算位置（参考 AttackHelper.doSlash）
            Vec3 pos = event.getUser().position().add(0.0D, (double) event.getUser().getEyeHeight() * 0.75D, 0.0D)
                    .add(event.getUser().getLookAngle().scale(0.3f));

            pos = pos.add(VectorHelper.getVectorForRotation(-90.0F, event.getUser().getViewYRot(0)).scale(event.getCenterOffset().y))
                    .add(VectorHelper.getVectorForRotation(0, event.getUser().getViewYRot(0) + 90).scale(event.getCenterOffset().z))
                    .add(event.getUser().getLookAngle().scale(event.getCenterOffset().z));

            // 创建十字斩剑气
            SlashEffectEntity crossSlash = new SlashEffectEntity(
                    RecastingEntities.SLASH_EFFECT.get(),
                    event.getUser().level(),
                    event.getUser()
            );

            crossSlash.setPos(pos.x, pos.y, pos.z);
            crossSlash.setRoll(event.getRoll() + 90); // 旋转90度形成十字
            crossSlash.setYRot(event.getUser().getYRot());
            crossSlash.setXRot(0);
            crossSlash.setColor(event.getSlashBladeState().getColorCode());
            crossSlash.setMute(event.isMute());
            crossSlash.setCritical(event.isCritical());
            crossSlash.setModifiedRatio(event.getModifiedRatio() * attackRatio.of(level));
            //noinspection deprecation
            crossSlash.setDamage((float) (event.getDamage() * attackRatio.of(level)));
            crossSlash.setSize(event.getAttackRange());

            // 设置击退（如果有）
            if (event.getKnockback() != null) {
                crossSlash.attackActionCallbackPoint.register(event.getKnockback().action::accept);
            }

            event.getUser().level().addFreshEntity(crossSlash);
        }

    }

    /***
     * 剑气释放
     * 挥刀时有概率发出剑气
     */
    public static class DriveReleaseSpecialEffect extends ExtendedSpecialEffect {

        NumberPack probability = new NumberPack(0.1f, 0.05f);
        NumberPack attackRatio = new NumberPack(0.1f, 0.1f);
        int lifetime = 40;
        float speed = 0.45f;

        @SubscribeEvent
        public void onEvent(DoSlashExtendEvent event) {
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getBlade());
            int level = getLevel(properties);

            // 概率检查
            if (event.getUser().getRandom().nextFloat() >= probability.of(level)) {
                return;
            }

            // 创建剑气实体
            DriveEntity driveEntity = new DriveEntity(
                    RecastingEntities.DRIVE.get(),
                    event.getUser().level(),
                    event.getUser()
            );

            // 设置属性
            driveEntity.setColor(event.getSlashBladeState().getColorCode());
            driveEntity.setSize(event.getAttackRange());
            driveEntity.setModifiedRatio(event.getModifiedRatio() * attackRatio.of(level));
            driveEntity.setMaxLifeTime(lifetime);
            driveEntity.setRoll(event.getRoll());
            driveEntity.setSeep(speed);

            // 获取攻击目标位置并设置方向
            var attackPos = PosHelper.getAttackTargetPosition(event.getUser(), event.getSlashBladeState());
            driveEntity.lookAt(attackPos, false);

            // 添加到世界
            event.getUser().level().addFreshEntity(driveEntity);
        }

    }

    /***
     * 生长
     * 挥刀时恢复生命
     */
    public static class GrowthSpecialEffect extends ExtendedSpecialEffect {

        NumberPack healAmount = new NumberPack(0f, 0.2f);

        @SubscribeEvent
        public void onEvent(DoSlashExtendEvent event) {
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getBlade());
            int level = getLevel(properties);

            // 恢复生命值
            float heal = healAmount.of(level);
            if (heal > 0) {
                event.getUser().heal(heal);
            }
        }

    }

    /***
     * 回溯
     * 挥刀时恢复耐久
     */
    public static class RegressionSpecialEffect extends ExtendedSpecialEffect {

        NumberPack durabilityAmount = new NumberPack(0f, 1f);

        @SubscribeEvent
        public void onEvent(DoSlashExtendEvent event) {
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            // 只在服务端执行
            if (event.getUser().level().isClientSide()) {
                return;
            }

            PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getBlade());
            int level = getLevel(properties);

            ISlashBladeState state = event.getSlashBladeState();
            if (state.getMaxDamage() <= 0) {
                return;
            }

            // 恢复耐久值
            int restoreAmount = (int) durabilityAmount.of(level);
            if (restoreAmount > 0) {
                int currentDamage = state.getDamage();
                int newDamage = Math.max(0, currentDamage - restoreAmount);
                state.setDamage(newDamage);
            }
        }

    }

    /***
     * 断罪
     * 触发SA时追加次元斩攻击
     */
    public static class JudgementSpecialEffect extends ExtendedSpecialEffect {

        NumberPack attackRatio = new NumberPack(0.2f, 0.1f);
        int delay = 5;

        @SubscribeEvent
        public void onEvent(SlashBladeEvent.ChargeActionEvent event) {
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            // 只在服务端执行
            if (event.getEntityLiving().level().isClientSide()) {
                return;
            }

            // 检查是否成功触发SA（不是失败）
            if (event.getType() == mods.flammpfeil.slashblade.slasharts.SlashArts.ArtsType.Fail) {
                return;
            }

            LivingEntity user = event.getEntityLiving();
            ISlashBladeState state = event.getSlashBladeState();
            ItemStack blade = user.getMainHandItem();

            PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(blade);
            int level = getLevel(properties);

            // 延迟执行，确保SA已经触发
            user.getCapability(TimeRunCapability.TIME_RUN).ifPresent(
                    timeRun -> timeRun.addTimerCell(
                            () -> {
                                Level worldIn = user.level();
                                if (worldIn.isClientSide()) {
                                    return;
                                }

                                // 获取攻击目标位置
                                Vec3 pos = PosHelper.getAttackTargetPosition(user, state);

                                // 创建次元斩
                                JudgementCutEntity jc = new JudgementCutEntity(
                                        RecastingEntities.JUDGEMENT_CUT.get(),
                                        worldIn,
                                        user
                                );

                                jc.setPos(pos.x, pos.y, pos.z);

                                // 设置颜色
                                jc.setColor(state.getColorCode());

                                // 设置伤害倍率
                                jc.setModifiedRatio(attackRatio.of(level));

                                // 添加到世界
                                worldIn.addFreshEntity(jc);
                            },
                            delay
                    )
            );
        }

    }

    /***
     * 冲击
     * 造成伤害有几率召唤幻影剑造成瞬间伤害
     */
    public static class ImpactSpecialEffect extends ExtendedSpecialEffect {

        NumberPack probability = new NumberPack(0f, 0.05f);
        NumberPack attackRatio = new NumberPack(0f, 0.1f);


        @SubscribeEvent
        public void onEvent(AttackAmplifierEvent event) {
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            // 只在服务端执行
            if (event.getAttacker().level().isClientSide()) {
                return;
            }

            // 检查目标是否存活
            if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
                return;
            }

            PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getItem());
            int level = getLevel(properties);

            // 概率检查
            if (event.getAttacker().getRandom().nextFloat() >= probability.of(level)) {
                return;
            }


            // 延迟执行，确保攻击已经完成
            event.getAttacker().getCapability(TimeRunCapability.TIME_RUN).ifPresent(
                    timeRun -> timeRun.addTimerCell(
                            () -> {
                                Level worldIn = event.getAttacker().level();
                                if (worldIn.isClientSide()) {
                                    return;
                                }

                                // 检查目标是否仍然存活
                                if (!target.isAlive()) {
                                    return;
                                }

                                // 获取目标位置
                                Vec3 pos = target.position().add(0, target.getEyeHeight() * 0.5, 0);

                                // 创建幻影剑
                                SummondSwordEntity summondSword = new SummondSwordEntity(
                                        RecastingEntities.SUMMOND_SWORD.get(),
                                        worldIn,
                                        event.getAttacker()
                                );

                                // 设置位置和旋转
                                summondSword.setPos(pos.x, pos.y, pos.z);
                                summondSword.setYRot(event.getAttacker().getRandom().nextFloat() * 360);
                                summondSword.setXRot(event.getAttacker().getRandom().nextFloat() * 360);

                                // 设置颜色
                                summondSword.setColor(event.getSlashBladeState().getColorCode());

                                // 设置伤害倍率
                                summondSword.setModifiedRatio(attackRatio.of(level));

                                // 设置最大生命时间
                                summondSword.setMaxLifeTime(40);

                                // 添加到世界
                                worldIn.addFreshEntity(summondSword);

                                // 立即攻击目标
                                summondSword.onHitEntity(target, SummondSwordEntity.SummondAttackType.HIT);
                            },
                            0
                    )
            );
        }

    }

    /***
     * 过载
     * 挥刀时小概率触发审判
     */
    public static class OverloadSpecialEffect extends ExtendedSpecialEffect {

        NumberPack probability = new NumberPack(0f, 0.03f);

        @SubscribeEvent
        public void onEvent(DoSlashExtendEvent event) {
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            // 只在服务端执行
            if (event.getUser().level().isClientSide()) {
                return;
            }

            PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getBlade());
            int level = getLevel(properties);

            // 概率检查
            if (event.getUser().getRandom().nextFloat() >= probability.of(level)) {
                return;
            }

            // 触发次元斩
            Level worldIn = event.getUser().level();
            ISlashBladeState state = event.getSlashBladeState();

            // 获取攻击目标位置
            Vec3 pos = PosHelper.getAttackTargetPosition(event.getUser(), state);

            // 创建次元斩
            JudgementCutEntity jc = new JudgementCutEntity(
                    RecastingEntities.JUDGEMENT_CUT.get(),
                    worldIn,
                    event.getUser()
            );

            jc.setPos(pos.x, pos.y, pos.z);

            // 设置颜色
            jc.setColor(state.getColorCode());

            // 添加到世界
            worldIn.addFreshEntity(jc);

            // 播放音效
            worldIn.playSound(null, jc.getX(), jc.getY(), jc.getZ(),
                    net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT,
                    net.minecraft.sounds.SoundSource.PLAYERS, 0.5F,
                    0.8F / (event.getUser().getRandom().nextFloat() * 0.4F + 0.8F));
        }

    }

    /***
     * 抵抗
     * 挥刀时获得伤害吸收
     */
    public static class ResistSpecialEffect extends ExtendedSpecialEffect {

        NumberPack level = new NumberPack(1f, 0f);
        NumberPack time = new NumberPack(1f, 1f);

        @SubscribeEvent
        public void onEvent(DoSlashExtendEvent event) {
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            // 只在服务端执行
            if (event.getUser().level().isClientSide()) {
                return;
            }

            PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getBlade());
            int levelValue = getLevel(properties);

            // 添加伤害吸收效果
            int effectLevel = (int) level.of(levelValue);
            int duration = (int) time.of(levelValue);
            event.getUser().addEffect(new MobEffectInstance(MobEffects.ABSORPTION, duration, effectLevel));
        }

    }

    /***
     * 断却
     * 触发次元斩之后造成一次大伤害和大范围的劈砍
     */
    public static class SeverBreakSpecialEffect extends ExtendedSpecialEffect {

        NumberPack attackRatio = new NumberPack(0.1f, 0.15f);
        NumberPack rangeRatio = new NumberPack(1f, 0.1f);

        @SubscribeEvent
        public void onEvent(EntityJoinLevelEvent event) {
            // 只在服务端执行
            if (event.getLevel().isClientSide()) {
                return;
            }

            // 检查是否是 JudgementCutEntity
            if (!(event.getEntity() instanceof JudgementCutEntity jc)) {
                return;
            }

            // 获取创建者
            LivingEntity shooter = jc.getShooter();
            if (shooter == null) {
                return;
            }

            // 检查是否拥有此特效
            ItemStack blade = shooter.getMainHandItem();
            if (blade.isEmpty()) {
                return;
            }

            blade.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
                if (!hasSpecialEffect(state)) {
                    return;
                }

                PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(blade);
                int level = getLevel(properties);

                // 获取次元斩位置
                Vec3 attackPos = jc.position();

                // 获取攻击距离
                float attackDistance = blade.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                        .map(PropertiesDefinitionExtension::attackDistance)
                        .orElse(1.0f);

                // 计算期望长度
                double desiredLength = 4 * attackDistance * rangeRatio.of(level);

                // 生成随机偏移
                double x = (shooter.getRandom().nextDouble() * 2 - 1) * desiredLength;
                double y = (shooter.getRandom().nextDouble() * 2 - 1) * desiredLength;
                double z = (shooter.getRandom().nextDouble() * 2 - 1) * desiredLength;

                // 计算新位置
                Vec3 pos = attackPos.add(x, y, z);

                // 创建斩击特效
                SlashEffectEntity slashEffect = new SlashEffectEntity(
                        RecastingEntities.SLASH_EFFECT.get(),
                        event.getLevel(),
                        shooter
                );

                slashEffect.setPos(pos.x, pos.y, pos.z);
                slashEffect.setRoll(shooter.getRandom().nextInt(360));
                slashEffect.lookAt(attackPos, false);
                slashEffect.setColor(state.getColorCode());
                slashEffect.setMute(false);
                slashEffect.setModifiedRatio(attackRatio.of(level));
                slashEffect.setSize((float) (desiredLength / 4));

                // 添加到世界
                event.getLevel().addFreshEntity(slashEffect);
            });
        }

    }

    /***
     * 风暴
     * 触发审判时，召唤幻影剑进行攻击
     */
    public static class StormSpecialEffect extends ExtendedSpecialEffect {

        NumberPack attackRatio = new NumberPack(0f, 0.05f);
        NumberPack number = new NumberPack(2f, 1f);

        @SubscribeEvent
        public void onEvent(EntityJoinLevelEvent event) {
            // 只在服务端执行
            if (event.getLevel().isClientSide()) {
                return;
            }

            // 检查是否是 JudgementCutEntity
            if (!(event.getEntity() instanceof JudgementCutEntity jc)) {
                return;
            }

            // 获取创建者
            LivingEntity shooter = jc.getShooter();
            if (shooter == null) {
                return;
            }

            // 检查是否拥有此特效
            ItemStack blade = shooter.getMainHandItem();
            if (blade.isEmpty()) {
                return;
            }

            blade.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
                if (!hasSpecialEffect(state)) {
                    return;
                }

                PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(blade);
                int level = getLevel(properties);

                // 获取次元斩位置
                Vec3 pos = jc.position();

                // 生成幻影剑数量
                int n = (int) number.of(level);
                float attack = attackRatio.of(level);

                for(int i = 0; i < n; i++) {
                    SummondSwordEntity summondSword = new SummondSwordEntity(
                            RecastingEntities.SUMMOND_SWORD.get(),
                            event.getLevel(),
                            shooter
                    );

                    summondSword.lookAt(pos, false);
                    summondSword.setColor(state.getColorCode());
                    summondSword.setModifiedRatio(attack);
                    summondSword.setStartDelay(shooter.getRandom().nextInt(10));

                    // 添加到世界
                    event.getLevel().addFreshEntity(summondSword);
                }

                // 播放音效
                event.getEntity().playSound(
                        SoundEvents.CHORUS_FRUIT_TELEPORT,
                        0.2F,
                        1.45F
                );
            });
        }

    }

    /***
     * 风暴.变体
     * 触发审判时，从上方召唤幻影剑进行攻击
     */
    public static class StormVariantSpecialEffect extends ExtendedSpecialEffect {

        NumberPack attackRatio = new NumberPack(0f, 0.05f);
        NumberPack number = new NumberPack(2f, 1f);

        @SubscribeEvent
        public void onEvent(EntityJoinLevelEvent event) {
            // 只在服务端执行
            if (event.getLevel().isClientSide()) {
                return;
            }

            // 检查是否是 JudgementCutEntity
            if (!(event.getEntity() instanceof JudgementCutEntity jc)) {
                return;
            }

            // 获取创建者
            LivingEntity shooter = jc.getShooter();
            if (shooter == null) {
                return;
            }

            // 检查是否拥有此特效
            ItemStack blade = shooter.getMainHandItem();
            if (blade.isEmpty()) {
                return;
            }

            blade.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
                if (!hasSpecialEffect(state)) {
                    return;
                }

                PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(blade);
                int level = getLevel(properties);

                // 获取次元斩位置
                Vec3 attackPos = jc.position();

                // 生成幻影剑数量
                int n = (int) number.of(level);
                float attack = attackRatio.of(level);

                for(int i = 0; i < n; i++) {
                    // 在上方随机位置生成
                    Vec3 randomOffset = PosHelper.getRandomVectorInCircle(shooter.getRandom(), 4.5f);
                    Vec3 pos = attackPos.add(0, 8, 0).add(randomOffset);

                    SummondSwordEntity summondSword = new SummondSwordEntity(
                            RecastingEntities.SUMMOND_SWORD.get(),
                            event.getLevel(),
                            shooter
                    );

                    summondSword.setPos(pos.x, pos.y, pos.z);
                    summondSword.lookAt(attackPos, false);
                    summondSword.setColor(state.getColorCode());
                    summondSword.setModifiedRatio(attack);
                    summondSword.setStartDelay(shooter.getRandom().nextInt(10));
                    summondSword.setRoll(shooter.getRandom().nextInt(360));

                    // 添加到世界
                    event.getLevel().addFreshEntity(summondSword);
                }

                // 播放音效
                event.getEntity().playSound(
                        SoundEvents.CHORUS_FRUIT_TELEPORT,
                        0.2F,
                        1.45F
                );
            });
        }

    }

    /***
     * 黑色玫瑰
     * 叠加伤害，每 tick 造成伤害，伤害减半
     */
    public static class BlackRoseSpecialEffect extends ExtendedSpecialEffect {

        float attack = 0.05f;
        float attenuation = 0.75f;
        int attackInterval = 5;

        // 存储每个攻击者对每个目标的累计伤害
        // Map<攻击者, Map<目标, 累计伤害>>
        Map<LivingEntity, Map<LivingEntity, Float>> accumulatedDamageMap = new HashMap<>();

        @SubscribeEvent
        public void onAttackAmplifier(AttackAmplifierEvent event) {
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }
            if (event.getAttacker().level().isClientSide()) {
                return;
            }
            if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
                return;
            }

            // 检查攻击类型，如果是防止递归攻击类型，不叠加伤害（避免递归）
            if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
                return;
            }

            LivingEntity attacker = event.getAttacker();

            // 获取或创建目标伤害映射
            Map<LivingEntity, Float> targetDamageMap = accumulatedDamageMap.computeIfAbsent(attacker, k -> new HashMap<>());

            // 计算本次伤害（基于最终伤害倍率）
            AttributeInstance attribute = attacker.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attribute == null) {
                return;
            }
            float baseDamage = (float) (attribute.getValue() * event.getUltimatelyModifiedRatio());
            baseDamage += event.getExtraDamage();

            // 叠加伤害
            float currentAccumulated = targetDamageMap.getOrDefault(target, 0f);
            float newDamage = baseDamage * attack;
            float totalDamage = currentAccumulated + newDamage;

            targetDamageMap.put(target, totalDamage);
        }

        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }

            // 如果没有攻击者，直接返回
            if (accumulatedDamageMap.isEmpty()) {
                return;
            }

            // 获取第一个攻击者的世界和时间（所有攻击者应该在同一个世界）
            LivingEntity firstAttacker = accumulatedDamageMap.keySet().iterator().next();
            if (firstAttacker == null || firstAttacker.level().isClientSide()) {
                return;
            }

            long currentTime = firstAttacker.level().getGameTime();
            if (currentTime % attackInterval != 0) {
                return;
            }

            // 清理无效的攻击者并统一造成伤害
            Iterator<Map.Entry<LivingEntity, Map<LivingEntity, Float>>> attackerIterator = accumulatedDamageMap.entrySet().iterator();
            while (attackerIterator.hasNext()) {
                Map.Entry<LivingEntity, Map<LivingEntity, Float>> attackerEntry = attackerIterator.next();
                LivingEntity attacker = attackerEntry.getKey();

                // 如果攻击者无效，清除整个条目
                if (attacker == null || !attacker.isAlive() || attacker.level().isClientSide()) {
                    attackerIterator.remove();
                    continue;
                }

                Map<LivingEntity, Float> targetDamageMap = attackerEntry.getValue();

                if (targetDamageMap == null || targetDamageMap.isEmpty()) {
                    continue;
                }

                Iterator<Map.Entry<LivingEntity, Float>> targetIterator = targetDamageMap.entrySet().iterator();
                while (targetIterator.hasNext()) {
                    Map.Entry<LivingEntity, Float> targetEntry = targetIterator.next();
                    LivingEntity target = targetEntry.getKey();
                    Float accumulatedDamage = targetEntry.getValue();

                    // 如果目标无效，清除条目
                    if (target == null || !target.isAlive() || target.level() != attacker.level()) {
                        targetIterator.remove();
                        continue;
                    }

                    if (accumulatedDamage == null) {
                        targetIterator.remove();
                        continue;
                    }

                    // 如果累计伤害小于 0.1，不造成伤害，清除条目
                    if (accumulatedDamage < 0.1f) {
                        targetIterator.remove();
                        continue;
                    }

                    // 使用 AttackHelper 造成伤害，同时使用黑色玫瑰攻击类型和防止递归攻击类型
                    // 这样会触发 AttackAmplifierEvent，但由于我们检查了攻击类型，不会递归叠加
                    AttackHelper.attack(
                            attacker,
                            target,
                            new DamageStructure(0f, accumulatedDamage),
                            List.of(RecastingAttackTypes.BLACK_ROSE_ATTACK.get(), RecastingAttackTypes.NO_RECURSION_ATTACK.get())
                    );

                    // 伤害减半
                    float newDamage = accumulatedDamage * attenuation;
                    if (newDamage < 0.1f) {
                        targetIterator.remove();
                    } else {
                        targetEntry.setValue(newDamage);
                    }
                }

                // 如果目标映射为空，清除攻击者条目
                if (targetDamageMap.isEmpty()) {
                    attackerIterator.remove();
                }
            }
        }

    }
}


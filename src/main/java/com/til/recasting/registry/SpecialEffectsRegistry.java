package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.entity.DriveEntity;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.entity.LightningEntity;
import com.til.recasting.entity.SlashEffectEntity;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.entity.SummondSpiralSwordEntity;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.event.DoSlashExtendEvent;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.MathHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.mixin.AttackManagerMixin;
import com.til.recasting.registry.instance.BuffType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.AttackManager;
import mods.flammpfeil.slashblade.util.VectorHelper;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.util.DamageStructure;
import com.til.recasting.util.NumberPack;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
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

    // 光子灼痕 - 攻击范围内叠层，满层释放短直线激光；三档冷却
    public static final RegistryObject<SpecialEffect> PHOTON_SCAR = registerExtendedSE("photon_scar",
            () -> new PhotonScarSpecialEffect().setCooldownTicks(30).setMaxLevel(1).setSpecial(true));
    public static final RegistryObject<SpecialEffect> PHOTON_SCAR_2 = registerExtendedSE("photon_scar_2",
            () -> new PhotonScarSpecialEffect().setCooldownTicks(20).setMaxLevel(1).setSpecial(true));
    public static final RegistryObject<SpecialEffect> PHOTON_SCAR_3 = registerExtendedSE("photon_scar_3",
            () -> new PhotonScarSpecialEffect().setCooldownTicks(10).setMaxLevel(1).setSpecial(true));

    public static RegistryObject<SpecialEffect> registerExtendedSE(String name, Supplier<SpecialEffect> factory) {
        return SPECIAL_EFFECT.register(name, factory);
    }

    @Accessors(chain = true)
    public static class ExtendedSpecialEffect extends SpecialEffect {

        @Getter
        @Setter
        int maxLevel = 5;

        @Getter
        @Setter
        boolean isSpecial = false;

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

            return propertiesDefinitionExtension.getExtendedSpecialLevels(resourceLocation);
        }

        protected PropertiesDefinitionExtension getPropertiesDefinitionExtension(ItemStack itemStack) {
            if (itemStack == null || itemStack.isEmpty()) {
                return null;
            }
            //noinspection DataFlowIssue
            return itemStack.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                    .orElse(null);
        }

        protected RenderDefinitionExtension getRenderDefinitionExtension(ItemStack itemStack) {
            if (itemStack == null || itemStack.isEmpty()) {
                return null;
            }
            //noinspection DataFlowIssue
            return itemStack.getCapability(CapabilityRegistryHandler.RENDER_DEFINITION_EXTENSION)
                    .orElse(null);
        }

        protected IBuffStackData getBuffStackData(Entity entity) {
            if (entity == null) {
                return null;
            }
            //noinspection DataFlowIssue
            return entity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA)
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

            event.getUser().getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(
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
        int lifetime = 20;
        float speed = 1.25f;

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
            user.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(
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

            if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
                return;
            }

            PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getItem());
            int level = getLevel(properties);

            // 概率检查
            if (event.getAttacker().getRandom().nextFloat() >= probability.of(level)) {
                return;
            }


            // 延迟执行，确保攻击已经完成
            event.getAttacker().getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(
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
        NumberPack number = new NumberPack(1f, 1f);

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
        NumberPack number = new NumberPack(1f, 1f);

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

            long currentTime = event.getServer().getTickCount();
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

    /***
     * 星闪
     * 攻击目标叠加层数，达到最大层数时触发额外伤害并重置目标速度
     */
    @Setter
    @Accessors(chain = true)
    public static class StarBlinkSpecialEffect extends ExtendedSpecialEffect {

        float attack = 2.25f;
        int addLevel = 1;

        @SubscribeEvent
        public void onEvent(AttackAmplifierEvent event) {
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            // 只在服务端执行
            if (event.getAttacker().level().isClientSide()) {
                return;
            }

            // 检查目标是否是生物实体且存活
            if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
                return;
            }

            if (event.getAttackTypeList().contains(RecastingAttackTypes.STAR_BLINK_ATTACK.get())) {
                return;
            }

            target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                    buffStackData -> {
                        Level world = target.level();
                        BuffType starBlinkBuffType = RecastingBuffTypes.STAR_BLINK.get();

                        // 获取当前层数
                        int currentLevel = buffStackData.getLevel(starBlinkBuffType, world);

                        // 检查是否达到最大层数
                        if (currentLevel >= starBlinkBuffType.getMaxLevel()) {
                            // 重置层数
                            buffStackData.setLevel(starBlinkBuffType, 0, world);

                            if (world instanceof ServerLevel serverLevel) {
                                int color = event.getSlashBladeState().getColorCode();
                                double r = ((color >> 16) & 0xFF) / 255.0;
                                double g = ((color >> 8) & 0xFF) / 255.0;
                                double b = (color & 0xFF) / 255.0;
                                serverLevel.sendParticles(
                                        RecastingParticleTypes.STAR_BLINK.get(),
                                        target.getX(),
                                        target.getY() + target.getBbHeight() * 0.5,
                                        target.getZ(),
                                        0,
                                        r, g, b,
                                        1.0
                                );
                            }

                            // 造成伤害
                            AttackHelper.attack(
                                    event.getAttacker(),
                                    target,
                                    new DamageStructure(attack, 0),
                                    List.of(RecastingAttackTypes.STAR_BLINK_ATTACK.get())
                            );

                            // 将目标速度设为0
                            target.setDeltaMovement(Vec3.ZERO);
                            return;
                        }

                        // 增加层数
                        int newLevel = currentLevel + addLevel;
                        buffStackData.setLevel(starBlinkBuffType, newLevel, world);

                    }
            );

        }

    }

    /***
     * 分裂
     * 挥刀时发射幻影剑进行辅助攻击
     */
    public static class SplitSpecialEffect extends ExtendedSpecialEffect {

        NumberPack attackRatio = new NumberPack(0.2f, 0.15f);

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

            // 获取攻击目标位置
            Vec3 attackPos = PosHelper.getAttackTargetPosition(event.getUser(), event.getSlashBladeState());

            // 生成幻影剑数量
            float attack = attackRatio.of(level);

            SummondSwordEntity summondSword = new SummondSwordEntity(
                    RecastingEntities.SUMMOND_SWORD.get(),
                    event.getUser().level(),
                    event.getUser()
            );

            // 朝向攻击目标
            summondSword.lookAt(attackPos, false);

            // 设置属性
            summondSword.setColor(event.getSlashBladeState().getColorCode());
            summondSword.setModifiedRatio(attack);
            summondSword.setStartDelay(event.getUser().getRandom().nextInt(5));

            // 添加到世界
            event.getUser().level().addFreshEntity(summondSword);
        }

    }

    /***
     * 吸血转化
     * 将攻击伤害的一部分转化为生命恢复
     */
    public static class LifeStealSpecialEffect extends ExtendedSpecialEffect {

        NumberPack healRatio = new NumberPack(0f, 0.01f); // 伤害转化比例

        @SubscribeEvent
        public void onEvent(AttackAmplifierEvent event) {
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }
            if (event.getAttacker().level().isClientSide()) {
                return;
            }
            if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
                return;
            }

            LivingEntity attacker = event.getAttacker();

            // 计算预期伤害（基于最终伤害倍率）
            AttributeInstance attribute = attacker.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attribute == null) {
                return;
            }

            PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getItem());
            int level = getLevel(properties);

            // 提前计算并保存需要的值
            double ultimatelyModifiedRatio = event.getUltimatelyModifiedRatio();
            float extraDamage = event.getExtraDamage();
            float currentHealRatio = healRatio.of(level);

            // 延迟执行，确保攻击已经完成并造成实际伤害
            attacker.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(
                    timeRun -> timeRun.addTimerCell(
                            () -> {
                                Level worldIn = attacker.level();
                                if (worldIn.isClientSide()) {
                                    return;
                                }

                                // 重新获取属性（可能在延迟期间发生变化）
                                AttributeInstance currentAttribute = attacker.getAttribute(Attributes.ATTACK_DAMAGE);
                                if (currentAttribute == null) {
                                    return;
                                }

                                // 计算预期伤害
                                float expectedDamage = (float) (currentAttribute.getValue() * ultimatelyModifiedRatio);
                                expectedDamage += extraDamage;

                                // 恢复生命值（基于预期伤害的比例）
                                float healAmount = expectedDamage * currentHealRatio;
                                if (healAmount > 0) {
                                    attacker.heal(healAmount);
                                }
                            },
                            1 // 延迟1 tick，确保伤害已经应用
                    )
            );
        }

    }

    /***
     * 回旋
     * 幻影剑造成伤害后叠加剑势，达到一定层数后触发风暴幻影剑
     */
    public static class SpiralSpecialEffect extends ExtendedSpecialEffect {

        NumberPack modifiedRatio = new NumberPack(0.1f, 0);
        NumberPack count = new NumberPack(4, 1);
        int addLevel = 1; // 每次叠加的层数
        NumberPack triggerInterval = new NumberPack(40, 0); // 触发间隔（tick）


        // 存储每个攻击者的最后触发时间
        Map<LivingEntity, Long> lastTriggerTimeMap = new HashMap<>();

        @SubscribeEvent
        public void onAttackEvent(AttackAmplifierEvent event) {
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            // 只在服务端执行
            if (event.getAttacker().level().isClientSide()) {
                return;
            }

            // 检查目标是否是生物实体且存活
            if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
                return;
            }

            // 只处理幻影剑攻击
            if (!event.getAttackTypeList().contains(RecastingAttackTypes.SUMMOND_SWORD_ATTACK.get())) {
                return;
            }

            if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_SPIRAL_SPECIAL_RECURSION_ATTACK.get())) {
                return;
            }

            if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
                return;
            }

            LivingEntity attacker = event.getAttacker();
            long currentTime = target.level().getGameTime();


            ItemStack blade = event.getItem();

            //noinspection DataFlowIssue
            PropertiesDefinitionExtension se = blade.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION).orElse(null);
            //noinspection ConstantValue
            if (se == null) {
                return;
            }


            //noinspection DataFlowIssue
            IBuffStackData buffStackData = target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).orElse(null);
            //noinspection ConstantValue
            if (buffStackData == null) {
                return;
            }

            Level world = target.level();
            BuffType swordMomentumBuffType = RecastingBuffTypes.SWORD_MOMENTUM.get();

            // 获取当前层数
            int currentLevel = buffStackData.getLevel(swordMomentumBuffType, world);

            Long lastTriggerTime = lastTriggerTimeMap.get(target);
            int interval = (int) triggerInterval.of(getLevel(se));

            // 如果触发间隔还没到，不触发风暴效果，也不叠加层数
            if (lastTriggerTime != null && (currentTime - lastTriggerTime) < interval) {
                return;
            }

            if (currentLevel < swordMomentumBuffType.getMaxLevel()) {
                int newLevel = currentLevel + addLevel;
                buffStackData.setLevel(swordMomentumBuffType, newLevel, world);
                return;
            }

            // 触发间隔到了，触发风暴效果并重置层数
            buffStackData.setLevel(swordMomentumBuffType, 0, world);
            lastTriggerTimeMap.put(target, currentTime);

            // 触发风暴效果
            performStormSwordsInternal(
                    attacker,
                    target,
                    event.getSlashBladeState(),
                    se
            );

        }


        public void performStormSwordsInternal(LivingEntity entity, LivingEntity target, ISlashBladeState state, PropertiesDefinitionExtension propertiesDefinitionExtension) {
            Level worldIn = entity.level();

            if (target == null || !target.isAlive() || target.isRemoved()) {
                return;
            }

            int count = (int) this.count.of(this.getLevel(propertiesDefinitionExtension));

            float off = entity.getRandom().nextFloat() * 360;

            for(int i = 0; i < count; i++) {
                SummondSpiralSwordEntity ss = new SummondSpiralSwordEntity(RecastingEntities.SUMMOND_SPIRAL_SWORD.get(), worldIn, entity);

                // 设置旋转中心为目标
                ss.setCenterEntity(target);

                // 设置旋转参数（使用辅助方法自动计算修饰参数）
                ss.setRadiusExpansion(2.5f, 6.0f, 30);
                ss.setSpeedDecay(16.0f, 0.3f, 30);
                ss.setRotationAngle(off + (360.0f / count * i));
                ss.setRotationAxis(new Vec3(0, 1, 0));
                ss.setRotationDirectionOutward(false);

                // 设置基本属性
                ss.setModifiedRatio(modifiedRatio.of(getLevel(propertiesDefinitionExtension)));
                ss.setColor(state.getColorCode());
                ss.setRoll(0);
                ss.setStartDelay(30);

                ss.addAttackType(RecastingAttackTypes.NO_SPIRAL_SPECIAL_RECURSION_ATTACK.get());

                worldIn.addFreshEntity(ss);

                entity.playSound(
                        SoundEvents.CHORUS_FRUIT_TELEPORT,
                        0.2F,
                        1.45F
                );
            }
        }

        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }

            long currentTime = event.getServer().getTickCount();
            if (currentTime % 20 != 0) {
                return;
            }
            // 如果没有实体，直接返回
            if (lastTriggerTimeMap.isEmpty()) {
                return;
            }

            // 清理无效的实体，防止内存泄漏
            Iterator<Map.Entry<LivingEntity, Long>> iterator = lastTriggerTimeMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<LivingEntity, Long> entry = iterator.next();
                LivingEntity target = entry.getKey();

                // 如果实体无效（null、死亡、被移除或在客户端），清除条目
                if (target == null || !target.isAlive() || target.isRemoved() || target.level().isClientSide()) {
                    iterator.remove();
                }
            }
        }

    }

    /***
     * 破片
     * 幻影剑造成伤害时叠加层级，达到一定层级时额外造成一次大量的伤害
     */
    public static class FragmentSpecialEffect extends ExtendedSpecialEffect {

        NumberPack attack = new NumberPack(3f, 1f); // 额外伤害
        int addLevel = 1; // 每次叠加的层数

        @SubscribeEvent
        public void onEvent(AttackAmplifierEvent event) {
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            // 只在服务端执行
            if (event.getAttacker().level().isClientSide()) {
                return;
            }

            // 检查目标是否是生物实体且存活
            if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
                return;
            }

            // 只处理幻影剑攻击
            if (!event.getAttackTypeList().contains(RecastingAttackTypes.SUMMOND_SWORD_ATTACK.get())) {
                return;
            }


            PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getItem());
            int level = getLevel(properties);

            target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                    buffStackData -> {
                        Level world = target.level();
                        BuffType fragmentBuffType = RecastingBuffTypes.FRAGMENT.get();

                        // 获取当前层数
                        int currentLevel = buffStackData.getLevel(fragmentBuffType, world);

                        // 增加层数
                        int newLevel = currentLevel + addLevel;
                        buffStackData.setLevel(fragmentBuffType, newLevel, world);

                        // 检查是否达到最大层数（64层）
                        if (newLevel >= fragmentBuffType.getMaxLevel()) {
                            // 重置层数
                            buffStackData.setLevel(fragmentBuffType, 0, world);

                            // 造成大量额外伤害
                            float damage = attack.of(level);
                            AttackHelper.attack(
                                    event.getAttacker(),
                                    target,
                                    new DamageStructure(damage, 0),
                                    List.of(RecastingAttackTypes.FRAGMENT_ATTACK.get())
                            );

                            // TODO 粒子 音效
                        }
                    }
            );
        }

    }

    /***
     * 撕裂
     * 次元斩造成伤害后叠加层数，满层级后造成额外的伤害
     */
    public static class TearSpecialEffect extends ExtendedSpecialEffect {

        NumberPack attack = new NumberPack(0.5f, 0.2f); // 额外伤害
        int addLevel = 1; // 每次叠加的层数

        @SubscribeEvent
        public void onEvent(AttackAmplifierEvent event) {
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            // 只在服务端执行
            if (event.getAttacker().level().isClientSide()) {
                return;
            }

            // 检查目标是否是生物实体且存活
            if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
                return;
            }

            // 只处理次元斩攻击
            if (!event.getAttackTypeList().contains(RecastingAttackTypes.JUDGEMENT_CUT_ATTACK.get())) {
                return;
            }

            PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getItem());
            int level = getLevel(properties);

            target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                    buffStackData -> {
                        Level world = target.level();
                        BuffType tearBuffType = RecastingBuffTypes.TEAR.get();

                        // 获取当前层数
                        int currentLevel = buffStackData.getLevel(tearBuffType, world);

                        // 增加层数
                        int newLevel = currentLevel + addLevel;
                        buffStackData.setLevel(tearBuffType, newLevel, world);

                        // 检查是否达到最大层数
                        if (newLevel >= tearBuffType.getMaxLevel()) {
                            // 重置层数
                            buffStackData.setLevel(tearBuffType, 0, world);

                            // 造成大量额外伤害
                            float damage = attack.of(level);
                            AttackHelper.attack(
                                    event.getAttacker(),
                                    target,
                                    new DamageStructure(damage, 0),
                                    List.of(RecastingAttackTypes.TEAR_ATTACK.get())
                            );

                            // TODO 粒子 音效
                        }
                    }
            );
        }

    }

    /***
     * 旋风
     * 你的次元斩将允许造成重复的伤害
     */
    public static class WhirlwindSpecialEffect extends ExtendedSpecialEffect {

        NumberPack attackInterval = new NumberPack(9, -1);

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

                // 设置次元斩允许重复攻击
                jc.setRepeatedAttack(true);

                float v = attackInterval.of(getLevel(getPropertiesDefinitionExtension(blade)));

                if (jc.getAttackInterval() > v) {
                    jc.setAttackInterval((int) v);
                }
            });
        }

    }

    /***
     * 断灭
     * 召唤一定数量的次元斩之后额外召唤一个巨型次元斩
     */
    public static class AnnihilationSpecialEffect extends ExtendedSpecialEffect {

        NumberPack attackRatio = new NumberPack(0.01f, 0.01f); // 攻击倍率
        int addLevel = 1; // 每次叠加的层数
        int giantLifetime = 40; // 巨型次元斩的生命时间
        float giantSize = 6.0f; // 巨型次元斩的大小倍率

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

                // 使用Buff系统跟踪次元斩计数
                shooter.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                        buffStackData -> {
                            Level world = shooter.level();
                            BuffType annihilationBuffType = RecastingBuffTypes.ANNIHILATION.get();

                            // 获取当前层数
                            int currentLevel = buffStackData.getLevel(annihilationBuffType, world);

                            // 增加层数
                            int newLevel = currentLevel + addLevel;
                            buffStackData.setLevel(annihilationBuffType, newLevel, world);

                            // 检查是否达到最大层数（6层）
                            if (newLevel >= annihilationBuffType.getMaxLevel()) {
                                // 重置层数
                                buffStackData.setLevel(annihilationBuffType, 0, world);

                                // 创建巨型次元斩
                                Level worldIn = shooter.level();
                                Vec3 pos = jc.position();

                                JudgementCutEntity giantJc = new JudgementCutEntity(
                                        RecastingEntities.JUDGEMENT_CUT.get(),
                                        worldIn,
                                        shooter
                                );

                                giantJc.setPos(pos.x, pos.y, pos.z);
                                giantJc.setColor(state.getColorCode());
                                giantJc.setModifiedRatio(attackRatio.of(level));
                                giantJc.setMaxLifeTime(giantLifetime);
                                giantJc.setSize(giantSize);

                                // 添加到世界
                                worldIn.addFreshEntity(giantJc);
                            }
                        }
                );
            });
        }

    }

    /***
     * 染色
     * 挥刀时更改刀刃颜色为随机的
     */
    public static class ColorDyeSpecialEffect extends ExtendedSpecialEffect {

        @SubscribeEvent
        public void onEvent(DoSlashExtendEvent event) {
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            // 只在服务端执行
            if (event.getUser().level().isClientSide()) {
                return;
            }

            // 生成随机颜色（RGB格式，0x000000 到 0xFFFFFF）
            int randomColor = event.getUser().getRandom().nextInt(0x1000000); // 0 到 16777215 (0xFFFFFF)

            // 设置刀刃颜色
            event.getSlashBladeState().setColorCode(randomColor);
        }

    }

    /***
     * 雷暴
     * 触发SA时，在目标位置召唤闪电
     */
    public static class ThunderstormSpecialEffect extends ExtendedSpecialEffect {

        NumberPack attackRatio = new NumberPack(0.1f, 0.1f);
        NumberPack lightningCount = new NumberPack(1f, 0.4f);
        NumberPack spreadRange = new NumberPack(3f, 0f); // 扩散范围
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
            user.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(
                    timeRun -> timeRun.addTimerCell(
                            () -> {
                                Level worldIn = user.level();
                                if (worldIn.isClientSide()) {
                                    return;
                                }

                                // 获取攻击目标位置
                                Vec3 centerPos = PosHelper.getAttackTargetPosition(user, state);

                                // 计算闪电数量和扩散范围
                                int count = (int) lightningCount.of(level);
                                float attack = attackRatio.of(level);
                                float range = spreadRange.of(level);

                                // 生成多道闪电
                                for(int i = 0; i < count; i++) {
                                    // 在圆形范围内生成随机偏移（只在 x-z 平面，不修改 y 轴）
                                    double angle = user.getRandom().nextDouble() * 2.0 * Math.PI;
                                    double radius = user.getRandom().nextDouble() * range;
                                    Vec3 randomOffset = new Vec3(
                                            radius * Math.cos(angle),
                                            0, // y 轴保持为 0
                                            radius * Math.sin(angle)
                                    );
                                    Vec3 lightningPos = centerPos.add(randomOffset);

                                    // 创建闪电实体
                                    LightningEntity lightning = new LightningEntity(
                                            RecastingEntities.LIGHTNING.get(),
                                            worldIn,
                                            user
                                    );

                                    lightning.setPos(lightningPos.x, lightningPos.y, lightningPos.z);
                                    lightning.setModifiedRatio(attack);
                                    lightning.setMaxLifeTime(20);

                                    // 添加到世界
                                    worldIn.addFreshEntity(lightning);
                                }

                                // 播放音效
                                worldIn.playSound(null, centerPos.x, centerPos.y, centerPos.z,
                                        SoundEvents.LIGHTNING_BOLT_THUNDER,
                                        net.minecraft.sounds.SoundSource.WEATHER, 1.0F,
                                        0.8F + user.getRandom().nextFloat() * 0.2F);
                            },
                            delay
                    )
            );
        }

    }

    /***
     * 雷神之怒
     * 击杀敌人时，在死亡位置召唤闪电
     */
    public static class ThunderGodsWrathSpecialEffect extends ExtendedSpecialEffect {

        NumberPack attackRatio = new NumberPack(0.2f, 0.1f);

        @SubscribeEvent
        public void onEvent(LivingDeathEvent event) {
            // 只在服务端执行
            if (event.getEntity().level().isClientSide()) {
                return;
            }

            // 获取击杀者
            net.minecraft.world.entity.Entity killer = event.getSource().getEntity();
            if (!(killer instanceof LivingEntity attacker)) {
                return;
            }

            // 检查击杀者是否持有刀
            ItemStack blade = attacker.getMainHandItem();
            if (blade.isEmpty()) {
                return;
            }

            // 检查是否拥有此特效
            blade.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
                if (!hasSpecialEffect(state)) {
                    return;
                }

                PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(blade);
                int level = getLevel(properties);

                // 获取死亡位置
                LivingEntity victim = event.getEntity();
                Vec3 deathPos = victim.position();

                // 计算伤害倍率和爆炸范围
                float attack = attackRatio.of(level);

                // 在死亡位置创建强力闪电
                LightningEntity lightning = new LightningEntity(
                        RecastingEntities.LIGHTNING.get(),
                        victim.level(),
                        attacker
                );

                lightning.setPos(deathPos.x, deathPos.y, deathPos.z);
                lightning.setModifiedRatio(attack);


                // 添加到世界
                victim.level().addFreshEntity(lightning);

            });
        }

    }

    /***
     * 电离
     * 受到闪电伤害时叠加电离buff，每层提供1%全伤害增伤，最高64层，高等级叠层更快
     */
    public static class IonizationSpecialEffect extends ExtendedSpecialEffect {

        // 每次受到闪电伤害叠加的层数
        NumberPack addLevelPerHit = new NumberPack(0f, 1f);

        @SubscribeEvent
        public void onAttackAmplifier(AttackAmplifierEvent event) {
            // 检查攻击者是否拥有此特效
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            // 只在服务端执行
            if (event.getAttacker().level().isClientSide()) {
                return;
            }

            // 检查目标是否是生物实体且存活
            if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
                return;
            }


            // 从事件获取攻击者的刀
            ItemStack blade = event.getItem();

            PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(blade);
            int level = getLevel(properties);

            // 获取电离buff类型
            Level world = target.level();
            BuffType ionizationBuffType = RecastingBuffTypes.IONIZATION.get();

            // 检查是否是闪电攻击，如果是则叠加buff
            if (event.getAttackTypeList().contains(RecastingAttackTypes.LIGHTNING_ATTACK.get()) && !event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
                // 获取目标的当前层数
                target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                        buffStackData -> {
                            int currentLevel = buffStackData.getLevel(ionizationBuffType, world);
                            int maxLevel = ionizationBuffType.getMaxLevel();

                            // 计算要叠加的层数（高等级叠层更快）
                            int addLevel = (int) addLevelPerHit.of(level);
                            int newLevel = Math.min(currentLevel + addLevel, maxLevel);

                            // 设置新层数
                            buffStackData.setLevel(ionizationBuffType, newLevel, world);
                        }
                );
            }

            // 应用增伤：根据目标的电离buff层数提供全伤害增伤（对所有攻击类型都生效）
            target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                    buffStackData -> {
                        int ionizationLevel = buffStackData.getLevel(ionizationBuffType, world);
                        if (ionizationLevel > 0) {
                            // 每层提供1%全伤害增伤
                            float damageBonus = ionizationLevel * 0.01f;
                            event.addModifiedRatioAmplifier(damageBonus);
                        }
                    }
            );
        }

    }

    /***
     * 蓄能
     * 造成伤害后叠加层数，到12层时造成一道闪电攻击目标
     */
    public static class EnergyStorageSpecialEffect extends ExtendedSpecialEffect {

        NumberPack addLevelPerHit = new NumberPack(1f, 0f); // 每次造成伤害叠加的层数
        NumberPack lightningAttackRatio = new NumberPack(0.2f, 0.1f); // 闪电伤害倍率

        @SubscribeEvent
        public void onAttackAmplifier(AttackAmplifierEvent event) {
            // 检查攻击者是否拥有此特效
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            // 只在服务端执行
            if (event.getAttacker().level().isClientSide()) {
                return;
            }

            // 检查目标是否是生物实体且存活
            if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
                return;
            }

            // 排除递归攻击
            if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
                return;
            }

            // 从事件获取攻击者的刀
            ItemStack blade = event.getItem();
            PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(blade);
            int level = getLevel(properties);

            // 获取蓄能buff类型（对目标叠加）
            Level world = target.level();
            BuffType energyStorageBuffType = RecastingBuffTypes.ENERGY_STORAGE.get();

            // 获取当前层数（对目标叠加）
            target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                    buffStackData -> {
                        int currentLevel = buffStackData.getLevel(energyStorageBuffType, world);
                        int maxLevel = energyStorageBuffType.getMaxLevel();

                        // 计算要叠加的层数
                        int addLevel = (int) addLevelPerHit.of(level);
                        int newLevel = Math.min(currentLevel + addLevel, maxLevel);

                        // 设置新层数
                        buffStackData.setLevel(energyStorageBuffType, newLevel, world);

                        // 检查是否达到最大层数（48层）
                        if (newLevel >= maxLevel) {
                            // 重置层数
                            buffStackData.setLevel(energyStorageBuffType, 0, world);

                            // 触发闪电攻击（攻击目标）
                            LivingEntity attacker = event.getAttacker();
                            triggerLightningAttack(attacker, target, blade, event.getSlashBladeState(), properties);
                        }
                    }
            );
        }

        private void triggerLightningAttack(LivingEntity attacker, LivingEntity target, ItemStack blade, ISlashBladeState state, PropertiesDefinitionExtension properties) {
            Level world = attacker.level();
            if (world.isClientSide()) {
                return;
            }

            // 获取目标站立位置（地面位置）
            Vec3 targetPos = new Vec3(target.getX(), target.getY(), target.getZ());

            // 计算闪电伤害倍率
            int level = getLevel(properties);
            float attack = lightningAttackRatio.of(level);

            // 创建闪电实体
            LightningEntity lightning = new LightningEntity(
                    RecastingEntities.LIGHTNING.get(),
                    world,
                    attacker
            );

            lightning.setPos(targetPos.x, targetPos.y, targetPos.z);
            lightning.setModifiedRatio(attack);
            lightning.setMaxLifeTime(20);

            // 添加到世界
            world.addFreshEntity(lightning);

        }

    }

    /***
     * 雷云
     * 目标受到雷电伤害后获得雷光buff，持有雷光的实体受到伤害后附加相当于原伤害10%等级的附加闪电伤害
     */
    public static class ThunderCloudSpecialEffect extends ExtendedSpecialEffect {

        NumberPack thunderLightLevelPerHit = new NumberPack(0.5f, 0.5f); // 每次受到雷电伤害获得的雷光层数
        float damageRatio = 0.1f;

        @SubscribeEvent
        public void onAttackAmplifier(AttackAmplifierEvent event) {
            // 检查攻击者是否拥有此特效
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            // 只在服务端执行
            if (event.getAttacker().level().isClientSide()) {
                return;
            }

            // 检查目标是否是生物实体且存活
            if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
                return;
            }

            // 排除递归攻击
            if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
                return;
            }

            // 检查是否是闪电攻击
            if (!event.getAttackTypeList().contains(RecastingAttackTypes.LIGHTNING_ATTACK.get())) {
                return;
            }

            // 给目标添加雷光buff
            Level world = target.level();
            BuffType thunderLightBuffType = RecastingBuffTypes.THUNDER_LIGHT.get();

            ItemStack blade = event.getItem();
            PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(blade);

            target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                    buffStackData -> {
                        int currentLevel = buffStackData.getLevel(thunderLightBuffType, world);
                        int maxLevel = thunderLightBuffType.getMaxLevel();
                        int newLevel = Math.min(currentLevel + (int) thunderLightLevelPerHit.of(getLevel(properties)), maxLevel);
                        buffStackData.setLevel(thunderLightBuffType, newLevel, world);
                    }
            );
        }

        @SubscribeEvent
        public void onAttackAmplifierForThunderLight(AttackAmplifierEvent event) {

            // 只在服务端执行
            if (event.getAttacker().level().isClientSide()) {
                return;
            }

            // 检查目标是否是生物实体且存活
            if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
                return;
            }

            // 排除递归攻击
            if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
                return;
            }

            // 检查目标（受害者）是否有雷光buff
            Level world = target.level();
            BuffType thunderLightBuffType = RecastingBuffTypes.THUNDER_LIGHT.get();

            target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                    buffStackData -> {
                        int thunderLightLevel = buffStackData.getLevel(thunderLightBuffType, world);
                        if (thunderLightLevel <= 0) {
                            return;
                        }

                        // 计算附加闪电伤害比例：10% + 5% * SE等级
                        float damageRatioValue = damageRatio;

                        // 创建闪电伤害源，使用 modifiedRatio 基于 finalDamage 计算额外伤害
                        AttackType lightningAttackType = RecastingAttackTypes.LIGHTNING_ATTACK.get();
                        AttackAmplifierEvent.DamageSourceInfo damageSourceInfo = lightningAttackType.createDamageSource(event.getAttacker(), target);

                        if (damageSourceInfo != null) {
                            // 使用 addDamageSourceInfo 添加额外的闪电伤害（基于伤害倍率）
                            event.addDamageSourceInfo(
                                    damageSourceInfo.damageSource(),
                                    new DamageStructure(damageRatioValue, 0f)
                            );
                        }
                    }
            );
        }

    }

    /***
     * 解算
     * SE攻击带有演算buff的目标时消耗一层演算，造成1.75基数的附加伤害，并具有小爆炸的音效和粒子效果
     */
    @Setter
    @Accessors(chain = true)
    public static class ResolveSpecialEffect extends ExtendedSpecialEffect {

        float damageRatio = 1f;

        @SubscribeEvent
        public void onEvent(AttackAmplifierEvent event) {
            // 检查攻击者是否拥有此特效
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            // 只在服务端执行
            if (event.getAttacker().level().isClientSide()) {
                return;
            }

            // 检查目标是否是生物实体且存活
            if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
                return;
            }

            // 排除递归攻击
            if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
                return;
            }

            if (event.getAttackTypeList().contains(RecastingAttackTypes.MATRIX.get())) {
                return;
            }

            // 检查目标是否有演算buff
            Level world = target.level();
            BuffType calculusBuffType = RecastingBuffTypes.CALCULUS.get();

            target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                    buffStackData -> {
                        int currentLevel = buffStackData.getLevel(calculusBuffType, world);
                        if (currentLevel <= 0) {
                            return;
                        }

                        // 消耗一层演算
                        int newLevel = Math.max(0, currentLevel - 1);
                        buffStackData.setLevel(calculusBuffType, newLevel, world);

                        AttackHelper.attack(
                                event.getAttacker(),
                                event.getTarget(),
                                new DamageStructure(damageRatio, 0),
                                List.of(RecastingAttackTypes.RESOLVE.get(), RecastingAttackTypes.NO_RECURSION_ATTACK.get())
                        );


                        // 播放小爆炸音效和粒子效果
                        if (world instanceof ServerLevel serverLevel) {
                            Vec3 pos = target.position().add(0, target.getEyeHeight() * 0.5, 0);
                            serverLevel.playSound(
                                    null,
                                    pos.x, pos.y, pos.z,
                                    SoundEvents.GENERIC_EXPLODE,
                                    net.minecraft.sounds.SoundSource.PLAYERS,
                                    0.3F,
                                    1.2F + target.getRandom().nextFloat() * 0.3F
                            );

                            // 生成小爆炸粒子效果
                            serverLevel.sendParticles(
                                    ParticleTypes.EXPLOSION,
                                    pos.x, pos.y, pos.z,
                                    3,
                                    0.2, 0.2, 0.2,
                                    0.1
                            );
                        }
                    }
            );
        }

    }

    /***
     * 燃沫
     * 攻击处于灵魂燃烧的目标时，使其额外受到当前生命1%的额外伤害，并有概率增加一层灵魂燃烧
     */
    public static class FlameFoamSpecialEffect extends ExtendedSpecialEffect {

        float healthDamageRatio = 0.01f; // 额外伤害比例（当前生命值的1%）
        float addSoulBurnProbability = 0.1f; // 增加灵魂燃烧的概率

        @SubscribeEvent
        public void onEvent(AttackAmplifierEvent event) {
            // 检查攻击者是否拥有此特效
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            // 只在服务端执行
            if (event.getAttacker().level().isClientSide()) {
                return;
            }

            // 检查目标是否是生物实体且存活
            if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
                return;
            }

            // 排除递归攻击
            if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
                return;
            }

            // 检查目标是否有灵魂燃烧buff
            Level world = target.level();
            BuffType soulBurnBuffType = RecastingBuffTypes.SOUL_BURN.get();

            target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                    buffStackData -> {
                        int currentSoulBurnLevel = buffStackData.getLevel(soulBurnBuffType, world);
                        if (currentSoulBurnLevel <= 0) {
                            return;
                        }

                        // 计算目标当前生命值的1%作为额外伤害
                        float currentHealth = target.getHealth();
                        float extraDamage = currentHealth * healthDamageRatio;

                        // 创建魔法伤害源，使用 extraDamage 添加固定数值的额外伤害
                        AttackType magicAttackType = RecastingAttackTypes.SUMMOND_SWORD_ATTACK.get();
                        AttackAmplifierEvent.DamageSourceInfo damageSourceInfo = magicAttackType.createDamageSource(event.getAttacker(), target);

                        if (damageSourceInfo != null) {
                            // 使用 addDamageSourceInfo 添加额外的魔法伤害（固定数值）
                            event.addDamageSourceInfo(
                                    damageSourceInfo.damageSource(),
                                    new DamageStructure(0f, extraDamage)
                            );
                        }

                        if (event.getAttacker().getRandom().nextFloat() < addSoulBurnProbability) {
                            int maxLevel = soulBurnBuffType.getMaxLevel();
                            int newLevel = Math.min(currentSoulBurnLevel + 1, maxLevel);
                            buffStackData.setLevel(soulBurnBuffType, newLevel, world);
                        }
                    }
            );
        }

    }

    /**
     * 光子灼痕
     * 命中叠层；满层释放短直线激光；激光对灼痕目标增伤
     */
    @Setter
    @Accessors(chain = true)
    public static class PhotonScarSpecialEffect extends ExtendedSpecialEffect {

        float laserBonusPerStack = 0.08f;
        float miniLaserAttack = 0.6f;
        float miniLaserRange = 4f;
        float miniLaserRadius = 0.75f;
        int cooldownTicks = 30;
        int addLevel = 1;

        Map<LivingEntity, Long> lastTriggerTimeMap = new WeakHashMap<>();

        @SubscribeEvent
        public void onEvent(AttackAmplifierEvent event) {
            if (!hasSpecialEffect(event.getSlashBladeState())) {
                return;
            }

            if (event.getAttacker().level().isClientSide()) {
                return;
            }

            if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
                return;
            }

            if (event.getAttackTypeList().contains(RecastingAttackTypes.PHOTON_SCAR_ATTACK.get())) {
                return;
            }

            if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
                return;
            }

            LivingEntity attacker = event.getAttacker();
            ItemStack blade = event.getItem();
            PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(blade);
            float attackDistance = properties == null ? 1.0f : properties.attackDistance();

            Level world = target.level();
            BuffType photonScarBuffType = RecastingBuffTypes.PHOTON_SCAR.get();

            // 激光打灼痕：按层增伤
            if (event.getAttackTypeList().contains(RecastingAttackTypes.LASER_ATTACK.get())) {
                target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(buffStackData -> {
                    int stacks = buffStackData.getLevel(photonScarBuffType, world);
                    if (stacks > 0) {
                        event.addModifiedRatioAmplifier(laserBonusPerStack * stacks);
                    }
                });
            }

            long now = world.getGameTime();
            Long last = lastTriggerTimeMap.get(attacker);
            if (last != null && now - last < cooldownTicks) {
                return;
            }

            target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(buffStackData -> {
                int currentLevel = buffStackData.getLevel(photonScarBuffType, world);
                int maxLevel = photonScarBuffType.getMaxLevel();

                if (currentLevel >= maxLevel) {
                    buffStackData.setLevel(photonScarBuffType, 0, world);
                    lastTriggerTimeMap.put(attacker, now);

                    Vec3 start = attacker.getEyePosition();
                    Vec3 direction = target.getBoundingBox().getCenter().subtract(start);
                    if (MathHelper.epsilonEquals(direction.lengthSqr(), 0.0)) {
                        direction = attacker.getLookAngle();
                    } else {
                        direction = direction.normalize();
                    }
                    float range = miniLaserRange * attackDistance;
                    Vec3 end = start.add(direction.scale(range));
                    AttackHelper.attackAlongSegment(
                            attacker,
                            start,
                            end,
                            miniLaserRadius,
                            new DamageStructure(miniLaserAttack, 0),
                            List.of(
                                    RecastingAttackTypes.LASER_ATTACK.get(),
                                    RecastingAttackTypes.PHOTON_SCAR_ATTACK.get()
                            ),
                            event.getSlashBladeState().getColorCode()
                    );
                    return;
                }

                lastTriggerTimeMap.put(attacker, now);
                int newLevel = Math.min(currentLevel + addLevel, maxLevel);
                buffStackData.setLevel(photonScarBuffType, newLevel, world);
            });
        }

    }
}


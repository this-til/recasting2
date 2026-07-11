package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.util.DamageStructure;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * 光子灼烧 / 灼痕 Buff 处理器
 * <ul>
 *   <li>灼烧：服务端周期结算火焰伤害</li>
 *   <li>灼痕：目标处于灼烧时，受击叠层；满层从攻击者头顶发射短光棱，命中后清零（无冷却）</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PhotonScarBuffHandler {

    /** 灼烧每层每次结算的火焰伤害 */
    private static final float DAMAGE_PER_STACK = 0.15f;
    /** 灼烧结算间隔（tick） */
    private static final int TICKS_PER_INTERVAL = 5;

    /** 灼痕满层短光束伤害倍率 */
    private static final float MINI_LASER_ATTACK = 0.45f;
    /** 刀状态缺失时的短光束默认颜色 */
    private static final int DEFAULT_COLOR = 0x50DCFF;

    /**
     * 灼烧持续伤害：按层数造成火焰伤害
     */
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide()) {
            return;
        }

        if (entity.tickCount % TICKS_PER_INTERVAL != 0) {
            return;
        }

        entity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            int currentLevel = data.getLevel(RecastingBuffTypes.PHOTON_BURN.get(), entity.level());
            if (currentLevel <= 0) {
                return;
            }

            float damage = currentLevel * DAMAGE_PER_STACK;
            if (damage <= 0) {
                return;
            }

            DamageSource damageSource = entity.damageSources().onFire();
            entity.hurt(damageSource, damage);
        });
    }

    /**
     * 灼烧状态下叠灼痕；满层触发短光束。
     * 优先级低于 SE，保证同一次激光先叠灼烧再处理灼痕。
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onAttackAmplifier(AttackAmplifierEvent event) {
        if (event.getAttacker().level().isClientSide()) {
            return;
        }

        if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
            return;
        }

        if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
            return;
        }

        LivingEntity attacker = event.getAttacker();
        Level world = target.level();
        BuffType photonBurnBuffType = RecastingBuffTypes.PHOTON_BURN.get();
        BuffType photonScarBuffType = RecastingBuffTypes.PHOTON_SCAR.get();

        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(buffStackData -> {
            int burnLevel = buffStackData.getLevel(photonBurnBuffType, world);
            if (burnLevel <= 0) {
                return;
            }

            int color = event.getSlashBladeState() == null
                    ? DEFAULT_COLOR
                    : event.getSlashBladeState().getColorCode();

            stackScarAndMaybeTrigger(
                    attacker,
                    target,
                    buffStackData,
                    photonScarBuffType,
                    1,
                    MINI_LASER_ATTACK,
                    color
            );
        });
    }

    /**
     * 叠加灼痕；达到最大层数时清零并发射带物理碰撞的短光棱（火焰激光伤害）
     */
    private static void stackScarAndMaybeTrigger(
            LivingEntity attacker,
            LivingEntity target,
            com.til.recasting.capability.IBuffStackData buffStackData,
            BuffType photonScarBuffType,
            int addLevel,
            float miniLaserAttack,
            int color
    ) {
        Level world = target.level();
        int currentScar = buffStackData.getLevel(photonScarBuffType, world);
        int newScar = Math.min(currentScar + addLevel, photonScarBuffType.getMaxLevel());

        if (newScar >= photonScarBuffType.getMaxLevel()) {
            buffStackData.setLevel(photonScarBuffType, 0, world);

            // 自头顶发射短光棱：先同步线段特效，再仅对实际碰撞到的目标结算伤害
            Vec3 start = PosHelper.getAboveHead(attacker, 0.5);
            Vec3 aim = target.getBoundingBox().getCenter();
            if (start.distanceToSqr(aim) > 1.0E-8) {
                PosHelper.BeamHit hit = PosHelper.castLivingBeam(world, attacker, start, aim);
                if (world instanceof ServerLevel serverLevel) {
                    // 与 LaserBeamSlashArts 共用命中粒子（刀色 DefaultParticle 高闪）
                    PrismBeamEffectHelper.sync(
                            serverLevel,
                            start,
                            hit.hitPos(),
                            color,
                            PrismBeamEffectHelper.DEFAULT_LIFE_TICKS
                    );
                }
                if (hit.entity() == target) {
                    AttackHelper.attack(
                            attacker,
                            target,
                            new DamageStructure(miniLaserAttack, 0),
                            List.of(
                                    RecastingAttackTypes.LASER_ATTACK.get(),
                                    RecastingAttackTypes.PHOTON_SCAR_ATTACK.get()
                            )
                    );
                    attacker.playSound(SoundEvents.BEACON_ACTIVATE, 0.25F, 1.8F);
                }
            }
        } else {
            buffStackData.setLevel(photonScarBuffType, newScar, world);
        }
    }
}

package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.SpecialEffectsRegistry;
import com.til.recasting.registry.se.PhotonScarSpecialEffect;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.util.DamageStructure;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 光子灼烧 / 灼痕 Buff 处理器
 * - 灼烧：每 0.25 秒造成固定魔法伤害：0.15 × 层数
 * - 灼痕：目标处于灼烧时叠层；满层释放短光束并清零（冷却中仍可叠层但不触发）
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PhotonScarBuffHandler {

    private static final float DAMAGE_PER_STACK = 0.15f;
    private static final int TICKS_PER_INTERVAL = 5;

    private static final float MINI_LASER_ATTACK = 0.15f;
    private static final int DEFAULT_COOLDOWN_TICKS = 10;
    private static final int DEFAULT_COLOR = 0x50DCFF;

    /** 受击者灼痕满层触发冷却 */
    public static final Map<LivingEntity, Long> LAST_SCAR_TRIGGER_TIME_MAP = new WeakHashMap<>();

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

            DamageSource damageSource = entity.damageSources().magic();
            entity.hurt(damageSource, damage);
        });
    }

    /**
     * 灼烧状态下：叠灼痕 + 满层短光束。
     * 优先级低于 SE，以便同一次激光先叠上灼烧再处理灼痕。
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

            int cooldownTicks = resolveCooldownTicks(event.getSlashBladeState());
            int color = event.getSlashBladeState() == null
                    ? DEFAULT_COLOR
                    : event.getSlashBladeState().getColorCode();

            stackScarAndMaybeTrigger(
                    attacker,
                    target,
                    buffStackData,
                    photonScarBuffType,
                    1,
                    cooldownTicks,
                    MINI_LASER_ATTACK,
                    color
            );
        });
    }

    private static void stackScarAndMaybeTrigger(
            LivingEntity attacker,
            LivingEntity target,
            com.til.recasting.capability.IBuffStackData buffStackData,
            BuffType photonScarBuffType,
            int addLevel,
            int cooldownTicks,
            float miniLaserAttack,
            int color
    ) {
        Level world = target.level();
        int currentScar = buffStackData.getLevel(photonScarBuffType, world);
        int newScar = Math.min(currentScar + addLevel, photonScarBuffType.getMaxLevel());
        long gameTime = world.getGameTime();
        Long lastScarTrigger = LAST_SCAR_TRIGGER_TIME_MAP.get(target);
        boolean onCooldown = lastScarTrigger != null && gameTime - lastScarTrigger < cooldownTicks;

        if (newScar >= photonScarBuffType.getMaxLevel() && !onCooldown) {
            buffStackData.setLevel(photonScarBuffType, 0, world);
            LAST_SCAR_TRIGGER_TIME_MAP.put(target, gameTime);

            Vec3 start = PosHelper.getAboveHead(attacker, 0.5);
            Vec3 aim = target.getBoundingBox().getCenter();
            if (start.distanceToSqr(aim) > 1.0E-8) {
                PosHelper.BeamHit hit = PosHelper.castLivingBeam(world, attacker, start, aim);
                if (world instanceof ServerLevel serverLevel) {
                    AttackHelper.spawnPrismAlongSegment(serverLevel, start, hit.hitPos(), color, 0.25f);
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

    private static int resolveCooldownTicks(ISlashBladeState slashBladeState) {
        if (slashBladeState == null) {
            return DEFAULT_COOLDOWN_TICKS;
        }

        IForgeRegistry<SpecialEffect> registry =
                mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get();

        SpecialEffect effect = resolvePhotonScarEffect(slashBladeState, registry);
        if (effect instanceof PhotonScarSpecialEffect photonScar) {
            return photonScar.getCooldownTicks();
        }
        return DEFAULT_COOLDOWN_TICKS;
    }

    private static SpecialEffect resolvePhotonScarEffect(
            ISlashBladeState slashBladeState,
            IForgeRegistry<SpecialEffect> registry
    ) {
        ResourceLocation scar = registry.getKey(SpecialEffectsRegistry.PHOTON_SCAR.get());
        if (scar != null && slashBladeState.hasSpecialEffect(scar)) {
            return SpecialEffectsRegistry.PHOTON_SCAR.get();
        }
        ResourceLocation scar2 = registry.getKey(SpecialEffectsRegistry.PHOTON_SCAR_2.get());
        if (scar2 != null && slashBladeState.hasSpecialEffect(scar2)) {
            return SpecialEffectsRegistry.PHOTON_SCAR_2.get();
        }
        ResourceLocation scar3 = registry.getKey(SpecialEffectsRegistry.PHOTON_SCAR_3.get());
        if (scar3 != null && slashBladeState.hasSpecialEffect(scar3)) {
            return SpecialEffectsRegistry.PHOTON_SCAR_3.get();
        }
        return null;
    }
}

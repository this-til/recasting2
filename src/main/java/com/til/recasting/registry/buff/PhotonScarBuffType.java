package com.til.recasting.registry.buff;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.handler.PrismBeamEffectHelper;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.util.DamageStructure;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;

/**
 * 光子灼痕：灼烧状态下受击叠层；满层短光棱后清零。
 */
@Getter
@Setter
@Accessors(chain = true)
public class PhotonScarBuffType extends BuffType {

    float miniLaserAttack = 0.25f;
    int defaultColor = 0x50DCFF;
    int addLevelPerHit = 1;

    public PhotonScarBuffType() {
        decayInterval = 100;
        maxLevel = 9;
    }

    /**
     * 优先级低于 SE，保证同一次激光先叠灼烧再处理灼痕。
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onAttackAmplifier(AttackAmplifierEvent event) {
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

        IBuffStackData buffStackData = RecastingAttachments.buffStackData(target);
        int burnLevel = buffStackData.getLevel(RecastingBuffTypes.PHOTON_BURN.get(), world);
        if (burnLevel <= 0) {
            return;
        }

        int color = event.getSlashBladeState() == null
                ? defaultColor
                : event.getSlashBladeState().getColorCode();

        stackScarAndMaybeTrigger(attacker, target, buffStackData, addLevelPerHit, miniLaserAttack, color);
    }

    private void stackScarAndMaybeTrigger(
            LivingEntity attacker,
            LivingEntity target,
            IBuffStackData buffStackData,
            int addScarLevel,
            float laserAttack,
            int color
    ) {
        Level world = target.level();
        int currentScar = buffStackData.getLevel(this, world);
        int newScar = Math.min(currentScar + addScarLevel, maxLevel);

        if (newScar >= maxLevel) {
            buffStackData.setLevel(this, 0, world);

            Vec3 start = PosHelper.getAboveHead(attacker, 0.5);
            Vec3 aim = target.getBoundingBox().getCenter();
            if (start.distanceToSqr(aim) > 1.0E-8) {
                PosHelper.BeamHit hit = PosHelper.castLivingBeam(world, attacker, start, aim);
                if (world instanceof ServerLevel serverLevel) {
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
                            new DamageStructure(laserAttack, 0),
                            List.of(
                                    RecastingAttackTypes.LASER_ATTACK.get(),
                                    RecastingAttackTypes.PHOTON_SCAR_ATTACK.get()
                            )
                    );
                    attacker.playSound(SoundEvents.BEACON_ACTIVATE, 0.25F, 1.8F);
                }
            }
        } else {
            buffStackData.setLevel(this, newScar, world);
        }
    }
}

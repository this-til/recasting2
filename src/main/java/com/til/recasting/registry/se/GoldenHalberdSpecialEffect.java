package com.til.recasting.registry.se;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingParticleTypes;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.util.DamageStructure;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

/***
 * 金戈
 * 斩击命中叠加层数，满层引爆小范围额外伤害
 */
@Setter
@Accessors(chain = true)
public class GoldenHalberdSpecialEffect extends ExtendedSpecialEffect {

    int maxStacks = 12;
    float burstRatio = 1.5f;
    int stacksPerHit = 1;
    float burstRange = 2.5f;

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
        if (!event.getAttackTypeList().contains(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get())) {
            return;
        }
        if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
            return;
        }

        LivingEntity attacker = event.getAttacker();
        int colorCode = event.getSlashBladeState().getColorCode();

        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(buffStackData -> {
            Level world = target.level();
            BuffType goldenHalberdBuffType = RecastingBuffTypes.GOLDEN_HALBERD.get();

            int current = buffStackData.getLevel(goldenHalberdBuffType, world);
            int next = current + stacksPerHit;

            if (next >= maxStacks) {
                buffStackData.setLevel(goldenHalberdBuffType, 0, world);

                Vec3 pos = target.position().add(0, target.getEyeHeight() * 0.5, 0);
                AttackHelper.areaAttack(
                        attacker,
                        pos,
                        new DamageStructure(burstRatio, 0f),
                        burstRange,
                        List.of(
                                RecastingAttackTypes.GOLDEN_HALBERD_ATTACK.get(),
                                RecastingAttackTypes.NO_RECURSION_ATTACK.get()
                        ),
                        null,
                        null
                );

                if (world instanceof ServerLevel serverLevel) {
                    serverLevel.playSound(
                            null, pos.x, pos.y, pos.z,
                            SoundEvents.PLAYER_ATTACK_SWEEP,
                            SoundSource.PLAYERS,
                            0.5F,
                            0.8F + target.getRandom().nextFloat() * 0.4F
                    );
                    double r = ((colorCode >> 16) & 0xFF) / 255.0;
                    double g = ((colorCode >> 8) & 0xFF) / 255.0;
                    double b = (colorCode & 0xFF) / 255.0;
                    ParticleHelper.sendParticlesLongRange(
                            serverLevel,
                            RecastingParticleTypes.GOLDEN_HALBERD.get(),
                            pos.x, pos.y, pos.z,
                            0,
                            r, g, b,
                            1.0
                    );
                }
            } else {
                buffStackData.setLevel(goldenHalberdBuffType, next, world);
            }
        });
    }
}

package com.til.recasting.registry.se;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.handler.SoulBurnBuffHandler;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/***
 * 燃沫
 * 攻击处于灵魂燃烧的目标时，直接削减其当前生命百分比血量，并有概率增加一层灵魂燃烧
 */
@Setter
@Accessors(chain = true)
public class FlameFoamSpecialEffect extends ExtendedSpecialEffect {

    float healthDamageRatio = 0.01f;
    float addSoulBurnProbability = 0.1f;

    @SubscribeEvent
    public void onEvent(AttackAmplifierEvent event) {
        LivingEntity target = resolveServerLivingTarget(event);
        if (target == null) {
            return;
        }

        if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
            return;
        }

        Level world = target.level();
        BuffType soulBurnBuffType = RecastingBuffTypes.SOUL_BURN.get();

        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                buffStackData -> {
                    int currentSoulBurnLevel = buffStackData.getLevel(soulBurnBuffType, world);
                    if (currentSoulBurnLevel <= 0) {
                        return;
                    }

                    float currentHealth = target.getHealth();
                    float extraDamage = currentHealth * healthDamageRatio;
                    if (extraDamage > 0f) {
                        float newHealth = currentHealth - extraDamage;
                        if (newHealth < 0.1f) {
                            newHealth = 0.0f;
                        }
                        target.setHealth(newHealth);
                        spawnMagmaBurstHitEffect(target);
                    }

                    if (event.getAttacker().getRandom().nextFloat() < addSoulBurnProbability) {
                        int maxLevel = soulBurnBuffType.getMaxLevel();
                        int newLevel = Math.min(currentSoulBurnLevel + 1, maxLevel);
                        buffStackData.setLevel(soulBurnBuffType, newLevel, world);
                        SoulBurnBuffHandler.ensureSoulBurnTimer(target);
                    }
                }
        );
    }

    private void spawnMagmaBurstHitEffect(LivingEntity target) {
        if (!(target.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 pos = target.position().add(0, target.getBbHeight() * 0.5, 0);
        serverLevel.playSound(
                null,
                pos.x, pos.y, pos.z,
                SoundEvents.LAVA_POP,
                SoundSource.PLAYERS,
                0.8F,
                0.7F + target.getRandom().nextFloat() * 0.3F
        );
        serverLevel.playSound(
                null,
                pos.x, pos.y, pos.z,
                SoundEvents.BLAZE_SHOOT,
                SoundSource.PLAYERS,
                0.35F,
                0.9F + target.getRandom().nextFloat() * 0.2F
        );
        ParticleHelper.sendParticlesLongRange(
                serverLevel,
                ParticleTypes.LAVA,
                pos.x, pos.y, pos.z,
                18,
                0.35, 0.45, 0.35,
                0.12
        );
        ParticleHelper.sendParticlesLongRange(
                serverLevel,
                ParticleTypes.FLAME,
                pos.x, pos.y, pos.z,
                16,
                0.4, 0.5, 0.4,
                0.08
        );
        ParticleHelper.sendParticlesLongRange(
                serverLevel,
                ParticleTypes.SMOKE,
                pos.x, pos.y, pos.z,
                8,
                0.3, 0.35, 0.3,
                0.02
        );
    }

}

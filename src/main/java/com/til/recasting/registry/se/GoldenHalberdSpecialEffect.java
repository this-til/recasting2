package com.til.recasting.registry.se;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.util.DamageStructure;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/***
 * 金戈
 * 斩击命中叠加层数，满层引爆额外伤害
 */
@Setter
@Accessors(chain = true)
public class GoldenHalberdSpecialEffect extends ExtendedSpecialEffect {

    int maxStacks = 12;
    float burstRatio = 1.5f;
    int stacksPerHit = 1;

    Map<LivingEntity, Map<LivingEntity, Integer>> stackMap = new HashMap<>();

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
        if (event.getAttackTypeList().contains(RecastingAttackTypes.GOLDEN_HALBERD_ATTACK.get())) {
            return;
        }

        LivingEntity attacker = event.getAttacker();
        AttributeInstance attribute = attacker.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attribute == null) {
            return;
        }
        float baseDamage = (float) (attribute.getValue() * event.getUltimatelyModifiedRatio());
        baseDamage += event.getExtraDamage();

        Map<LivingEntity, Integer> targetMap = stackMap.computeIfAbsent(attacker, k -> new HashMap<>());
        int current = targetMap.getOrDefault(target, 0);
        int next = current + stacksPerHit;

        if (next >= maxStacks) {
            targetMap.remove(target);
            if (targetMap.isEmpty()) {
                stackMap.remove(attacker);
            }

            AttackHelper.attack(
                    attacker,
                    target,
                    new DamageStructure(0f, baseDamage * burstRatio),
                    List.of(RecastingAttackTypes.GOLDEN_HALBERD_ATTACK.get())
            );

            if (target.level() instanceof ServerLevel serverLevel) {
                Vec3 pos = target.position().add(0, target.getEyeHeight() * 0.5, 0);
                serverLevel.playSound(
                        null, pos.x, pos.y, pos.z,
                        SoundEvents.PLAYER_ATTACK_SWEEP,
                        SoundSource.PLAYERS,
                        0.5F,
                        0.8F + target.getRandom().nextFloat() * 0.4F
                );
                ParticleHelper.sendParticlesLongRange(
                        serverLevel, ParticleTypes.SWEEP_ATTACK,
                        pos.x, pos.y, pos.z,
                        1, 0, 0, 0, 0
                );
                ParticleHelper.sendParticlesLongRange(
                        serverLevel, ParticleTypes.FLASH,
                        pos.x, pos.y, pos.z,
                        8, 0.3, 0.3, 0.3, 0
                );
                ParticleHelper.sendParticlesLongRange(
                        serverLevel, ParticleTypes.CRIT,
                        pos.x, pos.y, pos.z,
                        16, 0.5, 0.5, 0.5, 0.4
                );
            }
        } else {
            targetMap.put(target, next);
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (stackMap.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<LivingEntity, Map<LivingEntity, Integer>>> outerIter = stackMap.entrySet().iterator();
        while (outerIter.hasNext()) {
            Map.Entry<LivingEntity, Map<LivingEntity, Integer>> outerEntry = outerIter.next();
            LivingEntity attacker = outerEntry.getKey();
            Map<LivingEntity, Integer> targetMap = outerEntry.getValue();

            if (attacker == null || !attacker.isAlive() || attacker.level().isClientSide()) {
                outerIter.remove();
                continue;
            }

            targetMap.keySet().removeIf(target -> target == null || !target.isAlive() || target.level() != attacker.level());
            if (targetMap.isEmpty()) {
                outerIter.remove();
            }
        }
    }
}

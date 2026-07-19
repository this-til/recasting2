package com.til.recasting.registry.se;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.util.DamageStructure;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/***
 * 茶韵
 * 剑气命中储存部分伤害延迟释放；连续命中累加并刷新倒计时
 */
@Setter
@Accessors(chain = true)
public class TeaAromaSpecialEffect extends ExtendedSpecialEffect {

    float storeRatio = 0.2f;
    int delayTicks = 30;

    Map<LivingEntity, Map<LivingEntity, DelayedEntry>> delayedMap = new HashMap<>();

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
        if (!event.getAttackTypeList().contains(RecastingAttackTypes.DRIVE_ATTACK.get())) {
            return;
        }
        if (event.getAttackTypeList().contains(RecastingAttackTypes.TEA_AROMA_ATTACK.get())) {
            return;
        }

        LivingEntity attacker = event.getAttacker();
        AttributeInstance attribute = attacker.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attribute == null) {
            return;
        }
        float baseDamage = (float) (attribute.getValue() * event.getUltimatelyModifiedRatio());
        baseDamage += event.getExtraDamage();
        float storedDamage = baseDamage * storeRatio;

        Map<LivingEntity, DelayedEntry> targetMap = delayedMap.computeIfAbsent(attacker, k -> new HashMap<>());
        DelayedEntry existing = targetMap.get(target);
        if (existing != null) {
            existing.damage += storedDamage;
            existing.ticksLeft = delayTicks;
        } else {
            targetMap.put(target, new DelayedEntry(storedDamage, delayTicks));
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (delayedMap.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<LivingEntity, Map<LivingEntity, DelayedEntry>>> outerIter = delayedMap.entrySet().iterator();
        while (outerIter.hasNext()) {
            Map.Entry<LivingEntity, Map<LivingEntity, DelayedEntry>> outerEntry = outerIter.next();
            LivingEntity attacker = outerEntry.getKey();
            Map<LivingEntity, DelayedEntry> targetMap = outerEntry.getValue();

            if (attacker == null || !attacker.isAlive() || attacker.level().isClientSide()) {
                outerIter.remove();
                continue;
            }

            Iterator<Map.Entry<LivingEntity, DelayedEntry>> innerIter = targetMap.entrySet().iterator();
            while (innerIter.hasNext()) {
                Map.Entry<LivingEntity, DelayedEntry> innerEntry = innerIter.next();
                LivingEntity target = innerEntry.getKey();
                DelayedEntry delayed = innerEntry.getValue();

                if (target == null || !target.isAlive() || target.level() != attacker.level()) {
                    innerIter.remove();
                    continue;
                }

                delayed.ticksLeft--;
                if (delayed.ticksLeft <= 0) {
                    float releaseDamage = delayed.damage;
                    innerIter.remove();

                    AttackHelper.attack(
                            attacker,
                            target,
                            new DamageStructure(0f, releaseDamage),
                            List.of(RecastingAttackTypes.TEA_AROMA_ATTACK.get())
                    );

                    if (target.level() instanceof ServerLevel serverLevel) {
                        Vec3 pos = target.position().add(0, target.getEyeHeight() * 0.5, 0);
                        ParticleHelper.sendParticlesLongRange(
                                serverLevel,
                                new DustParticleOptions(new Vector3f(180f / 255f, 140f / 255f, 80f / 255f), 1.0f),
                                pos.x, pos.y, pos.z,
                                12, 0.4, 0.4, 0.4, 0.03
                        );
                    }
                }
            }

            if (targetMap.isEmpty()) {
                outerIter.remove();
            }
        }
    }

    private static class DelayedEntry {
        float damage;
        int ticksLeft;

        DelayedEntry(float damage, int ticksLeft) {
            this.damage = damage;
            this.ticksLeft = ticksLeft;
        }
    }
}

package com.til.recasting.registry.buff;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.util.DamageStructure;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

/**
 * 叠晖：有日核时幻影剑命中追加晖光、叠晖并耗日核；满层幻影剑伤害翻倍。
 */
@Getter
@Setter
@Accessors(chain = true)
public class SunsetStackBuffType extends BuffType {

    int stackAddPerHit = 1;
    int coreConsumePerHit = 1;
    float huiGuangRatio = 0.15f;
    float fullStackAmplifier = 0.5f;

    public SunsetStackBuffType() {
        decayInterval = 100;
        maxLevel = 50;
    }

    @SubscribeEvent
    public void onAttackAmplifier(AttackAmplifierEvent event) {
        if (event.getAttacker().level().isClientSide()) {
            return;
        }
        if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
            return;
        }

        List<AttackType> types = event.getAttackTypeList();
        if (!types.contains(RecastingAttackTypes.SUMMOND_SWORD_ATTACK.get())) {
            return;
        }
        if (types.contains(RecastingAttackTypes.HUI_GUANG_ATTACK.get())) {
            return;
        }

        boolean fromSunsetSa = types.contains(RecastingAttackTypes.SUNSET_CORE_MARK.get());
        Level world = target.level();

        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(buffStackData -> {
            int coreLevel = buffStackData.getLevel(RecastingBuffTypes.SUNSET_CORE.get(), world);
            if (coreLevel > 0) {
                AttackAmplifierEvent.DamageSourceInfo huiGuangSource =
                        RecastingAttackTypes.HUI_GUANG_ATTACK.get().createDamageSource(event.getAttacker(), target);
                if (huiGuangSource != null) {
                    var attackDamage = event.getAttacker().getAttribute(Attributes.ATTACK_DAMAGE);
                    float extraDamage = attackDamage == null
                            ? 0f
                            : (float) (attackDamage.getValue() * huiGuangRatio);
                    event.addDamageSourceInfo(huiGuangSource.damageSource(), new DamageStructure(0f, extraDamage));
                }

                spawnHuiGuangHitParticles(target);

                int currentStack = buffStackData.getLevel(this, world);
                buffStackData.setLevel(this, currentStack + stackAddPerHit, world);

                if (!fromSunsetSa) {
                    buffStackData.setLevel(RecastingBuffTypes.SUNSET_CORE.get(), coreLevel - coreConsumePerHit, world);
                }
            }

            if (buffStackData.getLevel(this, world) >= maxLevel) {
                event.addModifiedRatioAmplifier(fullStackAmplifier);
            }
        });
    }

    private void spawnHuiGuangHitParticles(LivingEntity target) {
        if (!(target.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        double x = target.getX();
        double y = target.getY() + target.getBbHeight() * 0.5;
        double z = target.getZ();
        ParticleHelper.sendParticlesLongRange(serverLevel, ParticleTypes.FLAME, x, y, z, 10, 0.4, 0.35, 0.4, 0.02);
        ParticleHelper.sendParticlesLongRange(serverLevel, ParticleTypes.END_ROD, x, y, z, 6, 0.3, 0.3, 0.3, 0.05);
    }
}

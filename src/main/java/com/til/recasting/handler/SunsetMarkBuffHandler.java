package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.util.DamageStructure;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * 长空落日：晖光 / 叠晖 / 日核消耗
 * <ul>
 *   <li>日核仅由 LongSkySunsetSlashArts 命中叠加</li>
 *   <li>其它幻影剑命中且目标有日核：追加晖光伤害、叠晖 +1，并消耗 1 层日核</li>
 *   <li>叠晖满层：对该目标的幻影剑伤害 +100%</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SunsetMarkBuffHandler {

    private static final int STACK_ADD_PER_HIT = 1;
    private static final int CORE_CONSUME_PER_HIT = 1;
    private static final float HUI_GUANG_RATIO = 0.15f;
    private static final float FULL_STACK_AMPLIFIER = 0.5f;

    @SubscribeEvent
    public static void onAttackAmplifier(AttackAmplifierEvent event) {
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
            BuffType coreType = RecastingBuffTypes.SUNSET_CORE.get();
            BuffType stackType = RecastingBuffTypes.SUNSET_STACK.get();

            int coreLevel = buffStackData.getLevel(coreType, world);
            if (coreLevel > 0) {
                AttackAmplifierEvent.DamageSourceInfo huiGuangSource =
                        RecastingAttackTypes.HUI_GUANG_ATTACK.get().createDamageSource(event.getAttacker(), target);
                if (huiGuangSource != null) {
                    var attackDamage = event.getAttacker().getAttribute(Attributes.ATTACK_DAMAGE);
                    float extraDamage = attackDamage == null
                            ? 0f
                            : (float) (attackDamage.getValue() * HUI_GUANG_RATIO);
                    event.addDamageSourceInfo(huiGuangSource.damageSource(), new DamageStructure(0f, extraDamage));
                }

                spawnHuiGuangHitParticles(target);

                int currentStack = buffStackData.getLevel(stackType, world);
                buffStackData.setLevel(stackType, currentStack + STACK_ADD_PER_HIT, world);

                if (!fromSunsetSa) {
                    buffStackData.setLevel(coreType, coreLevel - CORE_CONSUME_PER_HIT, world);
                }
            }

            if (buffStackData.getLevel(stackType, world) >= stackType.getMaxLevel()) {
                event.addModifiedRatioAmplifier(FULL_STACK_AMPLIFIER);
            }
        });
    }

    private static void spawnHuiGuangHitParticles(LivingEntity target) {
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

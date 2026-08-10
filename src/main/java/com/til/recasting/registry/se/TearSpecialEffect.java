package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.util.DamageStructure;
import com.til.recasting.util.NumberPack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

/***
 * 撕裂
 * 次元斩造成伤害后叠加层数，满层级后造成额外的伤害
 */
public class TearSpecialEffect extends ExtendedSpecialEffect {

    NumberPack attack = new NumberPack(0.5f, 0.2f); // 额外伤害
    int addLevel = 1; // 每次叠加的层数

    @SubscribeEvent
    public void onEvent(AttackAmplifierEvent event) {
        LivingEntity target = resolveServerLivingTarget(event);
        if (target == null) {
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

                    // 获取当前层数
                    int currentLevel = buffStackData.getLevel(RecastingBuffTypes.TEAR.get(), world);

                    // 增加层数
                    int newLevel = currentLevel + addLevel;
                    buffStackData.setLevel(RecastingBuffTypes.TEAR.get(), newLevel, world);

                    // 检查是否达到最大层数
                    if (newLevel >= RecastingBuffTypes.TEAR.get().getMaxLevel()) {
                        // 重置层数
                        buffStackData.setLevel(RecastingBuffTypes.TEAR.get(), 0, world);

                        // 粒子与音效
                        if (world instanceof ServerLevel serverLevel) {
                            Vec3 pos = target.position().add(0, target.getEyeHeight() * 0.5, 0);
                            serverLevel.playSound(
                                    null,
                                    pos.x, pos.y, pos.z,
                                    SoundEvents.PLAYER_ATTACK_SWEEP,
                                    SoundSource.PLAYERS,
                                    0.5F,
                                    0.8F + target.getRandom().nextFloat() * 0.4F
                            );
                            ParticleHelper.sendParticlesLongRange(
                                    serverLevel,
                                    ParticleTypes.SWEEP_ATTACK,
                                    pos.x, pos.y, pos.z,
                                    1,
                                    0, 0, 0,
                                    0
                            );
                            ParticleHelper.sendParticlesLongRange(
                                    serverLevel,
                                    ParticleTypes.CRIT,
                                    pos.x, pos.y, pos.z,
                                    12,
                                    0.4, 0.6, 0.4,
                                    0.4
                            );
                            ParticleHelper.sendParticlesLongRange(
                                    serverLevel,
                                    ParticleTypes.ENCHANTED_HIT,
                                    pos.x, pos.y, pos.z,
                                    8,
                                    0.3, 0.3, 0.3,
                                    0.3
                            );
                        }

                        // 造成大量额外伤害
                        float damage = attack.of(level);
                        AttackHelper.attack(
                                event.getAttacker(),
                                target,
                                new DamageStructure(damage, 0),
                                List.of(RecastingAttackTypes.TEAR_ATTACK.get())
                        );
                    }
                }
        );
    }

}

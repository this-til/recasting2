package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.util.DamageStructure;
import com.til.recasting.util.NumberPack;
import com.til.recasting.handler.ParticleHelper;
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
 * 破片
 * 幻影剑造成伤害时叠加层级，达到一定层级时额外造成一次大量的伤害
 */
public class FragmentSpecialEffect extends ExtendedSpecialEffect {

    NumberPack attack = new NumberPack(3f, 1f); // 额外伤害
    int addLevel = 1; // 每次叠加的层数

    @SubscribeEvent
    public void onEvent(AttackAmplifierEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        // 只在服务端执行
        if (event.getAttacker().level().isClientSide()) {
            return;
        }

        // 检查目标是否是生物实体且存活
        if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
            return;
        }

        // 只处理幻影剑攻击
        if (!event.getAttackTypeList().contains(RecastingAttackTypes.SUMMOND_SWORD_ATTACK.get())) {
            return;
        }


        PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getItem());
        int level = getLevel(properties);

        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                buffStackData -> {
                    Level world = target.level();
                    BuffType fragmentBuffType = RecastingBuffTypes.FRAGMENT.get();

                    // 获取当前层数
                    int currentLevel = buffStackData.getLevel(fragmentBuffType, world);

                    // 增加层数
                    int newLevel = currentLevel + addLevel;
                    buffStackData.setLevel(fragmentBuffType, newLevel, world);

                    // 检查是否达到最大层数（64层）
                    if (newLevel >= fragmentBuffType.getMaxLevel()) {
                        // 重置层数
                        buffStackData.setLevel(fragmentBuffType, 0, world);

                        // 粒子与音效
                        if (world instanceof ServerLevel serverLevel) {
                            Vec3 pos = target.position().add(0, target.getEyeHeight() * 0.5, 0);
                            serverLevel.playSound(
                                    null,
                                    pos.x, pos.y, pos.z,
                                    SoundEvents.GENERIC_EXPLODE,
                                    SoundSource.PLAYERS,
                                    0.4F,
                                    1.0F + target.getRandom().nextFloat() * 0.3F
                            );
                            ParticleHelper.sendParticlesLongRange(
                                    serverLevel,
                                    ParticleTypes.EXPLOSION,
                                    pos.x, pos.y, pos.z,
                                    3,
                                    0.2, 0.2, 0.2,
                                    0.1
                            );
                            ParticleHelper.sendParticlesLongRange(
                                    serverLevel,
                                    ParticleTypes.CRIT,
                                    pos.x, pos.y, pos.z,
                                    20,
                                    0.5, 0.7, 0.5,
                                    0.5
                            );
                            ParticleHelper.sendParticlesLongRange(
                                    serverLevel,
                                    ParticleTypes.ENCHANTED_HIT,
                                    pos.x, pos.y, pos.z,
                                    10,
                                    0.4, 0.4, 0.4,
                                    0.4
                            );
                        }

                        // 造成大量额外伤害
                        float damage = attack.of(level);
                        AttackHelper.attack(
                                event.getAttacker(),
                                target,
                                new DamageStructure(damage, 0),
                                List.of(RecastingAttackTypes.FRAGMENT_ATTACK.get())
                        );
                    }
                }
        );
    }

}

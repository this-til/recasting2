package com.til.recasting.registry.se;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingParticleTypes;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.util.DamageStructure;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

/***
 * 星闪
 * 攻击目标叠加层数，达到最大层数时触发额外伤害并重置目标速度
 */
@Setter
@Accessors(chain = true)
public class StarBlinkSpecialEffect extends ExtendedSpecialEffect {

    float attack = 2.25f;
    int addLevel = 1;

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

        if (event.getAttackTypeList().contains(RecastingAttackTypes.STAR_BLINK_ATTACK.get())) {
            return;
        }

        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                buffStackData -> {
                    Level world = target.level();
                    BuffType starBlinkBuffType = RecastingBuffTypes.STAR_BLINK.get();

                    // 获取当前层数
                    int currentLevel = buffStackData.getLevel(starBlinkBuffType, world);

                    // 检查是否达到最大层数
                    if (currentLevel >= starBlinkBuffType.getMaxLevel()) {
                        // 重置层数
                        buffStackData.setLevel(starBlinkBuffType, 0, world);

                        if (world instanceof ServerLevel serverLevel) {
                            int color = event.getSlashBladeState().getColorCode();
                            double r = ((color >> 16) & 0xFF) / 255.0;
                            double g = ((color >> 8) & 0xFF) / 255.0;
                            double b = (color & 0xFF) / 255.0;
                            serverLevel.sendParticles(
                                    RecastingParticleTypes.STAR_BLINK.get(),
                                    target.getX(),
                                    target.getY() + target.getBbHeight() * 0.5,
                                    target.getZ(),
                                    0,
                                    r, g, b,
                                    1.0
                            );
                        }

                        // 造成伤害
                        AttackHelper.attack(
                                event.getAttacker(),
                                target,
                                new DamageStructure(attack, 0),
                                List.of(RecastingAttackTypes.STAR_BLINK_ATTACK.get())
                        );

                        // 将目标速度设为0
                        target.setDeltaMovement(Vec3.ZERO);
                        return;
                    }

                    // 增加层数
                    int newLevel = currentLevel + addLevel;
                    buffStackData.setLevel(starBlinkBuffType, newLevel, world);

                }
        );

    }

}

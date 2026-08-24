package com.til.recasting.registry.se;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.util.DamageStructure;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;

/***
 * 解算
 * SE攻击带有演算buff的目标时消耗一层演算，造成1.75基数的附加伤害，并具有小爆炸的音效和粒子效果
 */
@Setter
@Accessors(chain = true)
public class ResolveSpecialEffect extends ExtendedSpecialEffect {

    float damageRatio = 1f;

    @SubscribeEvent
    public void onEvent(AttackAmplifierEvent event) {
        LivingEntity target = resolveServerLivingTarget(event);
        if (target == null) {
            return;
        }

        // 排除递归攻击
        if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
            return;
        }

        if (event.getAttackTypeList().contains(RecastingAttackTypes.MATRIX.get())) {
            return;
        }

        // 检查目标是否有演算buff
        Level world = target.level();

        var buffStackData = RecastingAttachments.buffStackData(target);
        int currentLevel = buffStackData.getLevel(RecastingBuffTypes.CALCULUS.get(), world);
        if (currentLevel <= 0) {
            return;
        }

        // 消耗一层演算
        int newLevel = Math.max(0, currentLevel - 1);
        buffStackData.setLevel(RecastingBuffTypes.CALCULUS.get(), newLevel, world);

        AttackHelper.attack(
                event.getAttacker(),
                event.getTarget(),
                new DamageStructure(damageRatio, 0),
                List.of(RecastingAttackTypes.RESOLVE.get(), RecastingAttackTypes.NO_RECURSION_ATTACK.get())
        );


        // 播放小爆炸音效和粒子效果
        if (world instanceof ServerLevel serverLevel) {
            Vec3 pos = target.position().add(0, target.getEyeHeight() * 0.5, 0);
            serverLevel.playSound(
                    null,
                    pos.x, pos.y, pos.z,
                    SoundEvents.GENERIC_EXPLODE,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    0.3F,
                    1.2F + target.getRandom().nextFloat() * 0.3F
            );

            // 生成小爆炸粒子效果
            serverLevel.sendParticles(
                    ParticleTypes.EXPLOSION,
                    pos.x, pos.y, pos.z,
                    3,
                    0.2, 0.2, 0.2,
                    0.1
            );
        }
    }

}

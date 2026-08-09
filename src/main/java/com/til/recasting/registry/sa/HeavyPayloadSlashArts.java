package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.util.DamageStructure;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

/**
 * [回到未来计划]大包弹：锁定优先、视锥索敌；近距盒伤后在目标处放出白色闪光次元斩。
 */
@Setter
@Accessors(chain = true)
public class HeavyPayloadSlashArts extends ExtendedSlashArts {

    private float meleeRatio = 1.0f;
    private float judgementRatio = 1.25f;
    private float areaRange = 2.5f;
    private int judgementLife = 2;
    private int judgementColor = 0xFFFFFF;

    @Override
    public void trigger(
            LivingEntity livingEntity,
            ItemStack itemStack,
            ISlashBladeState slashBladeState,
            RenderDefinitionExtension renderDefinitionExtension,
            PropertiesDefinitionExtension propertiesDefinitionExtension
    ) {
        Level level = livingEntity.level();
        if (level.isClientSide()) {
            return;
        }

        LivingEntity target = resolveLockThenCone(livingEntity, slashBladeState, level);
        if (target == null) {
            return;
        }

        Vec3 center = PosHelper.getEntityAimPosition(target);

        AttackHelper.areaAttack(
                livingEntity,
                center,
                new DamageStructure(meleeRatio, 0.0f),
                areaRange,
                List.of(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get()),
                null,
                null
        );

        JudgementCutEntity jc = new JudgementCutEntity(
                RecastingEntities.JUDGEMENT_CUT.get(),
                level,
                livingEntity
        );
        jc.setPos(center.x, center.y, center.z);
        jc.setColor(judgementColor);
        jc.setModifiedRatio(judgementRatio);
        jc.setMaxLifeTime(judgementLife);
        level.addFreshEntity(jc);

        level.playSound(
                null,
                center.x,
                center.y,
                center.z,
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS,
                0.5F,
                1.0F
        );

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.EXPLOSION,
                    center.x,
                    center.y,
                    center.z,
                    4,
                    0.35,
                    0.35,
                    0.35,
                    0.05
            );
        }
    }

    @Nullable
    private static LivingEntity resolveLockThenCone(
            LivingEntity caster,
            ISlashBladeState slashBladeState,
            Level level
    ) {
        Entity lock = slashBladeState.getTargetEntity(level);
        if (lock instanceof LivingEntity locked && locked.isAlive() && !locked.isRemoved()) {
            return locked;
        }
        return EntityHelper.selectClosestInViewCone(caster).orElse(null);
    }
}

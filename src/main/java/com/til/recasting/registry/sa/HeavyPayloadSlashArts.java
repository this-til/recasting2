package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingAttackTypes;
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
 * [回到未来计划]大包弹：锁定优先、视锥索敌；目标处大范围盒伤与爆炸特效，无次元斩。
 */
@Setter
@Accessors(chain = true)
public class HeavyPayloadSlashArts extends ExtendedSlashArts {

    private float meleeRatio = 1.0f;
    private float areaRange = 40.0f;

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

        level.playSound(
                null,
                center.x,
                center.y,
                center.z,
                SoundEvents.GENERIC_EXPLODE,
                SoundSource.PLAYERS,
                1.0F,
                0.8F
        );

        if (level instanceof ServerLevel serverLevel) {
            ParticleHelper.sendParticlesLongRange(
                    serverLevel,
                    ParticleTypes.EXPLOSION,
                    center.x,
                    center.y,
                    center.z,
                    64,
                    5.6,
                    5.6,
                    5.6,
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

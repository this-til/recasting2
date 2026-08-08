package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 相位碎裂：将周围敌人拉至身前聚点，并以球面幻影剑收束打击。
 */
@Setter
@Accessors(chain = true)
public class PhaseFractureSlashArts extends ExtendedSlashArts {

    private float pullRange = 50.0f;
    private float focusDistance = 5.0f;
    private float shellRadius = 10.0f;
    private float bladeRatio = 0.25f;
    private int yawSteps = 8;
    private int pitchSteps = 8;
    private int startDelayMax = 5;
    private int bladeLife = 30;

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

        Vec3 focus = livingEntity.position().add(livingEntity.getLookAngle().scale(focusDistance));
        RandomSource random = livingEntity.getRandom();
        int color = slashBladeState.getColorCode();

        List<LivingEntity> nearby = EntityHelper.getTargettableLivingEntityWithinAABB(
                level,
                livingEntity,
                livingEntity.position(),
                pullRange
        );
        for (LivingEntity entity : nearby) {
            double jitterX = (random.nextDouble() - 0.5) * 0.8;
            double jitterZ = (random.nextDouble() - 0.5) * 0.8;
            entity.teleportTo(focus.x + jitterX, focus.y, focus.z + jitterZ);
            entity.setDeltaMovement(Vec3.ZERO);
            entity.hurtMarked = true;
            if (level instanceof ServerLevel serverLevel) {
                ParticleHelper.sendParticlesLongRange(
                        serverLevel,
                        ParticleTypes.PORTAL,
                        entity.getX(),
                        entity.getY() + entity.getBbHeight() * 0.5,
                        entity.getZ(),
                        8,
                        0.2,
                        0.2,
                        0.2,
                        0.0
                );
            }
        }

        float yawStep = 360.0f / yawSteps;
        float pitchStep = 360.0f / pitchSteps;
        for (int i = 0; i < yawSteps; i++) {
            for (int o = 0; o < pitchSteps; o++) {
                float yaw = i * yawStep;
                float pitch = o * pitchStep;
                Vec3 dir = directionFromYawPitch(yaw, pitch);
                Vec3 spawnPos = focus.add(dir.scale(shellRadius));

                SummondSwordEntity blade = new SummondSwordEntity(
                        RecastingEntities.SUMMOND_SWORD.get(),
                        level,
                        livingEntity
                );
                blade.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                blade.setColor(color);
                blade.setModifiedRatio(bladeRatio);
                blade.setStartDelay(random.nextInt(startDelayMax + 1));
                blade.setMaxLifeTime(bladeLife);
                blade.setRoll(random.nextInt(361));
                blade.setIgnoringBlock(true);
                blade.lookAt(focus, false);
                level.addFreshEntity(blade);
            }
        }

        level.playSound(
                null,
                focus.x,
                focus.y,
                focus.z,
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS,
                1.0F,
                0.7F + random.nextFloat() * 0.3F
        );
    }

    private static Vec3 directionFromYawPitch(float yawDeg, float pitchDeg) {
        float yaw = yawDeg * Mth.DEG_TO_RAD;
        float pitch = pitchDeg * Mth.DEG_TO_RAD;
        float cosPitch = Mth.cos(pitch);
        return new Vec3(
                -Mth.sin(yaw) * cosPitch,
                -Mth.sin(pitch),
                Mth.cos(yaw) * cosPitch
        );
    }
}

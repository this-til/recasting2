package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.FinalGlowBlackHoleEntity;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 终焉超新星爆：在瞄准点生成坍缩黑洞。
 */
@Setter
@Accessors(chain = true)
public class FinalSupernovaSlashArts extends ExtendedSlashArts {

    private int collapseTicks = 100;
    private float horizonStart = 16.0f;
    private float horizonEnd = 0.0f;
    private float particleRadiusStart = 24.0f;
    private float particleRadiusEnd = 0.0f;
    private float effectRange = 64.0f;
    private float pullPower = 0.02f;
    private int raysPerTick = 96;
    private float absorbRadius = 2.0f;
    private float damageRatio = 4.35f;
    private float damageFalloffStart = 16.0f;
    private int blockBreakFxPerTick = 6;

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

        Vec3 pos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);
        FinalGlowBlackHoleEntity hole = new FinalGlowBlackHoleEntity(
                RecastingEntities.FINAL_GLOW_BLACK_HOLE.get(),
                level,
                livingEntity
        );
        hole.setCollapseTicks(collapseTicks)
                .setHorizonStart(horizonStart)
                .setHorizonEnd(horizonEnd)
                .setParticleRadiusStart(particleRadiusStart)
                .setParticleRadiusEnd(particleRadiusEnd)
                .setEffectRange(effectRange)
                .setPullPower(pullPower)
                .setRaysPerTick(raysPerTick)
                .setAbsorbRadius(absorbRadius)
                .setDamageRatio(damageRatio)
                .setDamageFalloffStart(damageFalloffStart)
                .setBlockBreakFxPerTick(blockBreakFxPerTick)
                .applyConfig();
        hole.setPos(pos.x, pos.y, pos.z);
        hole.setColor(slashBladeState.getColorCode());
        level.addFreshEntity(hole);

        level.playSound(
                null,
                pos.x,
                pos.y,
                pos.z,
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS,
                1.0F,
                0.6F
        );
    }
}

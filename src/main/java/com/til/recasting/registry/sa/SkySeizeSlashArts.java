package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.LightningEntity;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.handler.LightningChainHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.registry.instance.AttackType;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * [回到未来计划]擒苍决：高空雨落幻影剑，消失时落雷并传导闪电链。
 */
@Setter
@Accessors(chain = true)
public class SkySeizeSlashArts extends ExtendedSlashArts {

    private int bladeCount = 44;
    private float spawnRate = 35.0f;
    private float spawnHeight = 18.0f;
    private float bladeRatio = 1.25f;
    private int startDelay = 5;
    private int breakDelay = 10;
    private int bladeColor = 0x3333FF;
    private float lightningRatio = 0.5f;
    private int lightningLife = 20;
    private int chainHops = 3;
    private int chainHopDelay = 2;
    private float chainRange = 16.0f;
    private float chainRatio = 0.4f;

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

        RandomSource random = livingEntity.getRandom();
        int color = bladeColor;
        float playerWidth = livingEntity.getBbWidth();

        for (int i = 1; i <= bladeCount; i++) {
            double xSpeed = random.nextGaussian() * 0.02;
            double ySpeed = random.nextGaussian() * 0.02;
            double zSpeed = random.nextGaussian() * 0.02;
            double rx = random.nextDouble();
            double rz = random.nextDouble();
            double ry = random.nextDouble();

            double x = livingEntity.getX() + ((rx * 2.0 - 1.0) * playerWidth - xSpeed * 10.0) * spawnRate;
            double y = livingEntity.getY() + ((ry * 2.0 - 1.0) * playerWidth - ySpeed * 10.0) * spawnRate + spawnHeight;
            double z = livingEntity.getZ() + ((rz * 2.0 - 1.0) * playerWidth - zSpeed * 10.0) * spawnRate;

            SummondSwordEntity sword = new SummondSwordEntity(
                    RecastingEntities.SUMMOND_SWORD.get(),
                    level,
                    livingEntity
            );
            sword.setPos(x, y, z);
            sword.setModifiedRatio(bladeRatio);
            sword.setColor(color);
            sword.setStartDelay(startDelay);
            sword.setMaxLifeTime(random.nextInt(i) + 30);
            sword.setBreakDelay(breakDelay);
            sword.setRoll(90.0f);
            sword.lookAt(new Vec3(0.0, -1.0, 0.0), true);

            sword.endCallbackPoint.register(() -> onBladeEnd(sword, livingEntity, color));

            level.addFreshEntity(sword);
        }

        level.playSound(
                null,
                livingEntity.getX(),
                livingEntity.getY(),
                livingEntity.getZ(),
                SoundEvents.LIGHTNING_BOLT_THUNDER,
                SoundSource.PLAYERS,
                0.45f,
                1.2f
        );
    }

    private void onBladeEnd(SummondSwordEntity sword, LivingEntity caster, int color) {
        Level level = sword.level();
        if (level.isClientSide()) {
            return;
        }
        if (caster == null || !caster.isAlive()) {
            return;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 tip = sword.position();

        LightningEntity lightning = new LightningEntity(
                RecastingEntities.LIGHTNING.get(),
                level,
                caster
        );
        lightning.setPos(tip.x, tip.y, tip.z);
        lightning.setModifiedRatio(lightningRatio);
        lightning.setMaxLifeTime(lightningLife);
        lightning.setColor(color);
        level.addFreshEntity(lightning);

        List<AttackType> attackTypes = List.of(RecastingAttackTypes.LIGHTNING_ATTACK.get());
        LightningChainHelper.startHopSequence(
                caster,
                tip,
                null,
                serverLevel,
                color,
                chainHops,
                chainHopDelay,
                chainRange,
                chainRatio,
                attackTypes,
                false
        );
    }
}

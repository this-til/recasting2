package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.DriveEntity;
import com.til.recasting.handler.TimeBeyondChargeHandler;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * 时之彼端：蓄力加速时间后释放裂岚式十字斩（Drive 尾杀），威力随蓄力时长提升。
 */
@Setter
@Accessors(chain = true)
public class TimeBeyondSlashArts extends ExtendedSlashArts {

    private float attackMin = 0.1f;
    private float attackMax = 3.0f;
    private float crossSize = 3.5f;
    private int driveLife = 10;
    private float driveSpeed = 4.5f;

    @Override
    public void trigger(
            LivingEntity livingEntity,
            ItemStack itemStack,
            ISlashBladeState slashBladeState,
            RenderDefinitionExtension renderDefinitionExtension,
            PropertiesDefinitionExtension propertiesDefinitionExtension
    ) {
        if (livingEntity.level().isClientSide()) {
            return;
        }

        float progress = TimeBeyondChargeHandler.consumeProgress(livingEntity);
        float ratio = Mth.lerp(progress, attackMin, attackMax);
        RandomSource random = livingEntity.getRandom();
        float roll = random.nextFloat() * 360.0f;
        spawnCrossSlash(livingEntity, slashBladeState, roll, ratio);
        spawnCrossSlash(livingEntity, slashBladeState, roll + 90.0f, ratio);
    }

    private void spawnCrossSlash(
            LivingEntity livingEntity,
            ISlashBladeState slashBladeState,
            float roll,
            float attackRatio
    ) {
        DriveEntity driveEntity = new DriveEntity(
                RecastingEntities.DRIVE.get(),
                livingEntity.level(),
                livingEntity
        );

        Vec3 pos = livingEntity.position()
                .add(0.0D, livingEntity.getEyeHeight() * 0.75D, 0.0D)
                .add(livingEntity.getLookAngle().scale(0.3f));
        driveEntity.setPos(pos.x, pos.y, pos.z);
        driveEntity.setColor(slashBladeState.getColorCode());
        driveEntity.setModifiedRatio(attackRatio);
        driveEntity.setMaxLifeTime(driveLife);
        driveEntity.setSize(crossSize);
        driveEntity.setRoll(roll);
        driveEntity.setSeep(driveSpeed);
        driveEntity.lookAt(livingEntity.getLookAngle(), true);

        livingEntity.level().addFreshEntity(driveEntity);
    }
}

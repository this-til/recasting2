package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.DriveEntity;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 多重剑气 Slash Arts
 * 向前发射多个驱动剑气，每个剑气有随机的尺寸和旋转角度
 */
@Setter
@Accessors(chain = true)
public class MultipleDriveSlashArts extends ExtendedSlashArts {

    float attack = 0.15f;
    int attackNumber = 4;
    int life = 80;
    float range = 1;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

        Level worldIn = livingEntity.level();
        Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

        // 生成多个驱动剑气
        for(int i = 0; i < attackNumber; i++) {
            DriveEntity driveEntity =
                    new DriveEntity(
                            RecastingEntities.DRIVE.get(),
                            worldIn,
                            livingEntity
                    );

            // 设置属性
            driveEntity.setColor(slashBladeState.getColorCode());

            // 设置尺寸：随机尺寸 * 攻击距离
            float randomSize = livingEntity.getRandom().nextFloat() * range;
            driveEntity.setSize(randomSize * propertiesDefinitionExtension.attackDistance());

            // 设置伤害
            driveEntity.setModifiedRatio(attack);

            // 设置生命时间
            driveEntity.setMaxLifeTime(life);

            // 设置随机旋转角度（Roll）
            driveEntity.setRoll(livingEntity.getRandom().nextInt(360));

            // 设置速度
            driveEntity.setSeep(0.45f);

            // 向前发射（使用玩家的视线方向）
            driveEntity.lookAt(attackPos, false);

            // 添加到世界
            worldIn.addFreshEntity(driveEntity);
        }

        // 播放音效
        livingEntity.playSound(
                SoundEvents.CHORUS_FRUIT_TELEPORT,
                0.2F,
                1.45F
        );
    }
}

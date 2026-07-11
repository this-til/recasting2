package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.entity.DriveEntity;
import com.til.recasting.event.DoSlashExtendEvent;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.util.NumberPack;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/***
 * 剑气释放
 * 挥刀时有概率发出剑气
 */
public class DriveReleaseSpecialEffect extends ExtendedSpecialEffect {

    NumberPack probability = new NumberPack(0.1f, 0.05f);
    NumberPack attackRatio = new NumberPack(0.1f, 0.1f);
    int lifetime = 20;
    float speed = 1.25f;

    @SubscribeEvent
    public void onEvent(DoSlashExtendEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getBlade());
        int level = getLevel(properties);

        // 概率检查
        if (event.getUser().getRandom().nextFloat() >= probability.of(level)) {
            return;
        }

        // 创建剑气实体
        DriveEntity driveEntity = new DriveEntity(
                RecastingEntities.DRIVE.get(),
                event.getUser().level(),
                event.getUser()
        );

        // 设置属性
        driveEntity.setColor(event.getSlashBladeState().getColorCode());
        driveEntity.setSize(event.getAttackRange());
        driveEntity.setModifiedRatio(event.getModifiedRatio() * attackRatio.of(level));
        driveEntity.setMaxLifeTime(lifetime);
        driveEntity.setRoll(event.getRoll());
        driveEntity.setSeep(speed);

        // 获取攻击目标位置并设置方向
        var attackPos = PosHelper.getAttackTargetPosition(event.getUser(), event.getSlashBladeState());
        driveEntity.lookAt(attackPos, false);

        // 添加到世界
        event.getUser().level().addFreshEntity(driveEntity);
    }

}

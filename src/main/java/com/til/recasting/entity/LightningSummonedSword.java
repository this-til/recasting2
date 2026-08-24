package com.til.recasting.entity;

import com.til.recasting.registry.RecastingEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 自定义召唤剑 - 击中后产生闪电
 */
public class LightningSummonedSword extends SummondSwordEntity {

    public LightningSummonedSword(
            EntityType<? extends SummondSwordEntity> entityTypeIn,
            Level worldIn,
            LivingEntity shooting,
            int lightningColor,
            float lightningAttack
    ) {
        super(entityTypeIn, worldIn, shooting);

        attackActionCallbackPoint.register(e -> {

            // 确定闪电生成位置（被击中实体的位置）
            Vec3 lightningPos = e.position();

            // 创建闪电实体
            LightningEntity lightningEntity = new LightningEntity(
                    RecastingEntities.LIGHTNING.get(),
                    this.level(),
                    getShooter()
            );

            lightningEntity.setPos(lightningPos.x, lightningPos.y, lightningPos.z);
            lightningEntity.setModifiedRatio(lightningAttack);
            lightningEntity.setMaxLifeTime(20);

            // 添加到世界
            this.level().addFreshEntity(lightningEntity);
        });
    }
}

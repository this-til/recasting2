package com.til.recasting.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * 星旋斩特效实体。
 */
public class StellarRotationEntity extends JudgementCutEntity {

    public StellarRotationEntity(EntityType<? extends StellarRotationEntity> entityTypeIn, Level worldIn, LivingEntity shooting) {
        super(entityTypeIn, worldIn, shooting);
        setRepeatedAttack(true);
    }
}

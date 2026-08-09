package com.til.recasting.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class MatrixEntity extends ContinuousDamageEntity {
    public MatrixEntity(EntityType<? extends MatrixEntity> entityTypeIn, Level worldIn, LivingEntity shooting) {
        super(entityTypeIn, worldIn, shooting);
        this.setRepeatedAttack(true);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

}

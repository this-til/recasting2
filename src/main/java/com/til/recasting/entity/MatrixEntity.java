package com.til.recasting.entity;

import com.til.recasting.registry.SlashArtsRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

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

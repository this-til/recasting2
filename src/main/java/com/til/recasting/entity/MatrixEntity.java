package com.til.recasting.entity;

import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.RecastingBuffTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * 穷观阵实体。
 */
public class MatrixEntity extends ContinuousDamageEntity {

    public MatrixEntity(EntityType<? extends MatrixEntity> entityTypeIn, Level worldIn, LivingEntity shooting) {
        super(entityTypeIn, worldIn, shooting);
        this.setRepeatedAttack(true);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (!level().isClientSide()) {
            LivingEntity shooter = getShooter();
            if (shooter != null && !shooter.isRemoved()) {
                RecastingAttachments.buffStackData(shooter)
                        .setLevel(RecastingBuffTypes.MATRIX.get(), 0, shooter.level());
            }
        }
        super.remove(reason);
    }
}

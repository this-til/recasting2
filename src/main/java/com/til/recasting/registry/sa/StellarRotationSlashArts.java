package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.StellarRotationEntity;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;

@Setter
@Accessors(chain = true)
public class StellarRotationSlashArts extends ExtendedSlashArts {
    float attack = 0.01f;
    float moveRange = 32;
    float size = 3;
    int attackInterval = 1;
    int life = 60;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {
        Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

        List<LivingEntity> entityList = EntityHelper.getTargettableLivingEntityWithinAABB(livingEntity.level(), livingEntity, attackPos, moveRange);

        for(Entity entity : entityList) {
            entity.setPos(attackPos);
        }

        StellarRotationEntity jc = new StellarRotationEntity(
                RecastingEntities.STELLAR_ROTATION.get(),
                livingEntity.level(),
                livingEntity
        );
        jc.setPos(attackPos.x, attackPos.y, attackPos.z);
        jc.setColor(slashBladeState.getColorCode());
        jc.setModifiedRatio(attack);
        jc.setMaxLifeTime(life);
        jc.setAttackInterval(attackInterval);
        jc.setSize(size);

        jc.attackActionCallbackPoint.register(e -> e.setDeltaMovement(Vec3.ZERO));

        jc.level().addFreshEntity(jc);
        livingEntity.playSound(
                SoundEvents.CHORUS_FRUIT_TELEPORT,
                0.2F,
                1.45F
        );
    }
}

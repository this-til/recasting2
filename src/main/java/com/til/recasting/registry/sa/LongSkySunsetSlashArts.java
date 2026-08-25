package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 长空落日 Slash Arts
 * 星流索敌齐射幻影剑；标记 {@link RecastingAttackTypes#SUNSET_CORE_MARK}，由叠晖 Buff 在伤害结算前叠加日核。
 */
@Setter
@Accessors(chain = true)
public class LongSkySunsetSlashArts extends ExtendedSlashArts {

    int swordCount = 8;
    float attack = 0.12f;
    float range = 24f;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {
        Level worldIn = livingEntity.level();
        if (worldIn.isClientSide()) {
            return;
        }

        Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);
        List<LivingEntity> entityList = EntityHelper.getTargettableLivingEntityWithinAABB(
                worldIn,
                livingEntity,
                attackPos,
                range
        );

        for (int i = 0; i < swordCount; i++) {
            SummondSwordEntity summonedSword = new SummondSwordEntity(
                    RecastingEntities.SUMMOND_SWORD.get(),
                    worldIn,
                    livingEntity
            );
            summonedSword.setColor(slashBladeState.getColorCode());
            summonedSword.setModifiedRatio(attack);
            summonedSword.setStartDelay(10 + livingEntity.getRandom().nextInt(10));
            summonedSword.addAttackType(RecastingAttackTypes.SUNSET_CORE_MARK.get());

            Vec3 targetPos;
            if (!entityList.isEmpty()) {
                Entity target = entityList.get(livingEntity.getRandom().nextInt(entityList.size()));
                targetPos = new Vec3(target.getX(), target.getY() + target.getEyeHeight() * 0.5, target.getZ());
            } else {
                targetPos = attackPos;
            }
            summonedSword.lookAt(targetPos, false);
            worldIn.addFreshEntity(summonedSword);
        }

        livingEntity.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 0.2F, 1.45F);
    }
}

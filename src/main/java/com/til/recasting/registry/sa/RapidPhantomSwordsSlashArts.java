package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.SummondSwordEntity;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 急行幻影剑 Slash Arts
 * 在目标位置周围召唤多把幻影剑
 */
@Setter
@Accessors(chain = true)
public class RapidPhantomSwordsSlashArts extends ExtendedSlashArts {

    float attack = 0.15f;
    int number = 12;
    float range = 12f;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

        Level worldIn = livingEntity.level();

        // 获取攻击目标位置
        Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

        // 获取范围内的所有实体
        List<Entity> entityList = EntityHelper.getTargettableEntitiesWithinAABB(
                worldIn,
                livingEntity,
                attackPos,
                range
        );

        // 创建多把召唤剑
        for(int i = 0; i < number; i++) {
            SummondSwordEntity summonedSword = new SummondSwordEntity(
                    RecastingEntities.SUMMOND_SWORD.get(),
                    worldIn,
                    livingEntity
            );

            // 设置属性
            summonedSword.setColor(slashBladeState.getColorCode());
            summonedSword.setModifiedRatio(attack);
            summonedSword.setStartDelay(livingEntity.getRandom().nextInt(10));


            // 设置朝向：如果有敌人，朝向随机敌人，否则朝向攻击位置
            if (!entityList.isEmpty()) {
                Entity target = entityList.get(livingEntity.getRandom().nextInt(entityList.size()));
                Vec3 targetPos = new Vec3(
                        target.getX(),
                        target.getY() + target.getEyeHeight() * 0.5,
                        target.getZ()
                );
                summonedSword.lookAt(targetPos, false);
            } else {
                summonedSword.lookAt(attackPos, false);
            }

            // 添加到世界
            worldIn.addFreshEntity(summonedSword);
        }

        // 播放音效
        livingEntity.playSound(
                SoundEvents.CHORUS_FRUIT_TELEPORT,
                0.2F,
                1.45F
        );
    }
}

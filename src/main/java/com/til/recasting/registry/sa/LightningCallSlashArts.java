package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.LightningEntity;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 引雷 Slash Arts
 * 在目标位置召唤一道闪电
 */
@Setter
@Accessors(chain = true)
public class LightningCallSlashArts extends ExtendedSlashArts {

    float attack = 0.5f;
    int lifeTicks = 20;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

        Level worldIn = livingEntity.level();

        // 获取攻击目标位置
        Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

        // 创建闪电实体
        LightningEntity lightningEntity = new LightningEntity(
                RecastingEntities.LIGHTNING.get(),
                worldIn,
                livingEntity
        );

        // 设置位置
        lightningEntity.setPos(attackPos.x, attackPos.y, attackPos.z);

        // 设置属性
        lightningEntity.setModifiedRatio(attack);
        lightningEntity.setMaxLifeTime(lifeTicks);

        // 添加到世界
        worldIn.addFreshEntity(lightningEntity);
    }
}

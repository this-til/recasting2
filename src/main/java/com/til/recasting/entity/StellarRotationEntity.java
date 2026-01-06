package com.til.recasting.entity;

import com.til.recasting.registry.RecastingAttackTypes;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * @Author: til
 * @Description: 星旋斩特效实体
 */
public class StellarRotationEntity extends JudgementCutEntity {

    public StellarRotationEntity(EntityType<? extends StellarRotationEntity> entityTypeIn, Level worldIn, LivingEntity shooting) {
        super(entityTypeIn, worldIn, shooting);
    }

}


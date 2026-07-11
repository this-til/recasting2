package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.LightningSummonedSword;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 苍穹十二连 Slash Arts
 * 发射多把召唤剑，击中敌人后在敌人位置生成闪电
 */
@Setter
@Accessors(chain = true)
public class HeavenTwelveHitSlashArts extends ExtendedSlashArts {

    int lightningNumber = 12;
    float attack = 0.1f;
    float lightningAttack = 0.3f;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

        Level worldIn = livingEntity.level();

        // 获取攻击目标位置
        Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

        // 创建多把召唤剑
        for(int i = 0; i < lightningNumber; i++) {
            LightningSummonedSword summonedSword = new LightningSummonedSword(
                    RecastingEntities.SUMMOND_SWORD.get(),
                    worldIn,
                    livingEntity,
                    slashBladeState.getColorCode(),
                    lightningAttack
            );


            // 设置属性
            summonedSword.setColor(slashBladeState.getColorCode());
            summonedSword.setModifiedRatio(attack);

            // 设置大小
            summonedSword.setSize(1.25f);

            // 朝向攻击目标位置
            summonedSword.lookAt(attackPos, false);

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

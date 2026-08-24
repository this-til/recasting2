package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.SummondSwordEntity;
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
 * 剑雨 Slash Arts
 * 在目标区域上方召唤大量剑雨攻击
 */
@Setter
@Accessors(chain = true)
public class SwordRainSlashArts extends ExtendedSlashArts {

    float attack = 0.05f;
    int attackNumber = 150;
    float range = 5;
    boolean concentrate = false;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

        Level worldIn = livingEntity.level();

        // 获取攻击目标位置
        Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

        // 计算基础生成位置（在实体上方）
        Vec3 basePos = livingEntity.position().add(0, range / 2, 0);

        net.minecraft.util.RandomSource random = livingEntity.getRandom();

        // 创建大量召唤剑
        for(int i = 0; i < attackNumber; i++) {
            SummondSwordEntity summonedSword =
                    new SummondSwordEntity(
                            RecastingEntities.SUMMOND_SWORD.get(),
                            worldIn,
                            livingEntity
                    );

            // 在圆形范围内随机生成位置
            Vec3 randomOffset = PosHelper.getRandomVectorInCircle(random, range);
            Vec3 pos = basePos.add(randomOffset);

            // 设置位置
            summonedSword.setPos(pos.x, pos.y, pos.z);

            // 设置大小
            summonedSword.setSize(0.6f);

            // 设置属性
            summonedSword.setColor(slashBladeState.getColorCode());
            summonedSword.setModifiedRatio(attack);
            summonedSword.setStartDelay(random.nextInt(60));  // 随机延迟发射
            summonedSword.setRoll(random.nextFloat() * 360.0f);

            // 初始化飞行方向：构造函数内 lookAt(getDeltaMovement()) 在速度为零时无法得到正确朝向，必须在 setPos 后重设
            if (concentrate) {
                summonedSword.lookAt(attackPos, false);
            } else {
                summonedSword.setRot(livingEntity.getYRot(), livingEntity.getXRot(), true);
                summonedSword.updateMotion();
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

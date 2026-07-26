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
 * 风暴幻影剑 Slash Arts
 * 在实体周围召唤多把幻影剑并发射
 */
@Setter
@Accessors(chain = true)
public class StormPhantomSwordsSlashArts extends ExtendedSlashArts {

    float attack = 0.15f;
    int number = 12;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

        Level worldIn = livingEntity.level();

        // 获取攻击目标位置
        Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

        // 计算生成位置（玩家眼睛位置 + 侧向偏移）
        Vec3 pos = livingEntity.getEyePosition(1.0f)
                .add(mods.flammpfeil.slashblade.util.VectorHelper.getVectorForRotation(
                        0.0f,
                        livingEntity.getYRot() + 90
                ).scale(livingEntity.getRandom().nextBoolean()
                        ? 1
                        : -1));

        float roll = livingEntity.getRandom().nextFloat() * 360.0f;

        // 创建多把召唤剑
        for(int i = 0; i < number; i++) {
            SummondSwordEntity summonedSword =
                    new SummondSwordEntity(
                            RecastingEntities.SUMMOND_SWORD.get(),
                            worldIn,
                            livingEntity
                    );

            // 设置位置
            summonedSword.setPos(pos.x, pos.y, pos.z);

            // 设置朝向（朝向攻击目标）
            summonedSword.lookAt(attackPos, false);

            // 设置属性
            summonedSword.setColor(slashBladeState.getColorCode());
            summonedSword.setStartDelay(i);  // 延迟发射
            summonedSword.setRoll(roll);
            summonedSword.setModifiedRatio(attack);

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

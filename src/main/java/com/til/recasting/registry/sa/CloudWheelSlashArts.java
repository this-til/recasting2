package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.LightningSummonedSword;
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
 * 云轮 Slash Arts
 * 在目标位置上方生成多把召唤剑，击中后生成闪电
 */
@Setter
@Accessors(chain = true)
public class CloudWheelSlashArts extends ExtendedSlashArts {

    float attack = 0.2f;
    int attackNumber = 6;
    float lightningAttack = 0.4f;
    int lightningNumber = 10;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

        Level worldIn = livingEntity.level();
        net.minecraft.util.RandomSource random = livingEntity.getRandom();

        // 获取攻击目标位置
        Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

        // 第一阶段：在目标位置上方5格，圆形范围内生成召唤剑
        for(int i = 0; i < attackNumber; i++) {
            SummondSwordEntity summonedSword = new SummondSwordEntity(
                    RecastingEntities.SUMMOND_SWORD.get(),
                    worldIn,
                    livingEntity
            );

            // 在圆形范围内随机生成位置
            Vec3 randomOffset = PosHelper.getRandomVectorInCircle(random, 2.5f);
            Vec3 pos = attackPos.add(0, 5, 0).add(randomOffset);
            summonedSword.setPos(pos.x, pos.y, pos.z);

            // 设置属性
            summonedSword.setColor(slashBladeState.getColorCode());
            summonedSword.setModifiedRatio(attack);
            summonedSword.setStartDelay(5);
            summonedSword.setRoll(random.nextInt(360));

            summonedSword.lookAt(attackPos, false);

            // 添加到世界
            worldIn.addFreshEntity(summonedSword);
        }

        // 第二阶段：在目标位置上方7格生成一把黄色召唤剑
        {
            LightningSummonedSword summonedSword = new LightningSummonedSword(
                    RecastingEntities.SUMMOND_SWORD.get(),
                    worldIn,
                    livingEntity,
                    0xFFFF00, // 黄色
                    lightningAttack
            );

            Vec3 pos = attackPos.add(0, 7, 0);
            summonedSword.setPos(pos.x, pos.y, pos.z);

            // 设置属性
            summonedSword.setColor(0xFFFF00); // 黄色
            summonedSword.setModifiedRatio(attack);
            summonedSword.setStartDelay(10);

            // 设置大小
            summonedSword.setSize(1.25f);

            // 朝向攻击目标位置
            summonedSword.lookAt(attackPos, false);

            // 添加到世界
            worldIn.addFreshEntity(summonedSword);
        }

        // 第三阶段：生成多把黄色召唤剑（如果 lightningNumber > 0）
        if (lightningNumber > 0) {
            for(int i = 0; i < lightningNumber; i++) {
                LightningSummonedSword summonedSword = new LightningSummonedSword(
                        RecastingEntities.SUMMOND_SWORD.get(),
                        worldIn,
                        livingEntity,
                        0xFFFF00, // 黄色
                        lightningAttack
                );

                // 设置属性
                summonedSword.setColor(0xFFFF00); // 黄色
                summonedSword.setModifiedRatio(attack);
                summonedSword.setStartDelay(i * 2);

                // 设置大小
                summonedSword.setSize(1.25f);

                // 朝向攻击目标位置
                summonedSword.lookAt(attackPos, false);

                // 添加到世界
                worldIn.addFreshEntity(summonedSword);
            }
        }

        livingEntity.playSound(
                SoundEvents.CHORUS_FRUIT_TELEPORT,
                0.2F,
                1.45F
        );
    }
}

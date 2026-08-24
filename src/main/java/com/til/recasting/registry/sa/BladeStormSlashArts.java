package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.SummondSpiralSwordEntity;
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
 * 剑刃风暴 Slash Arts
 * 在玩家周围随机位置生成大量高速旋转的幻影剑，持续攻击周围敌人
 * 类似魔兽世界剑圣的剑刃风暴
 */
@Setter
@Accessors(chain = true)
public class BladeStormSlashArts extends ExtendedSlashArts {

    float attack = 0.01f;
    int totalSwords = 128;         // 总共生成的剑数量
    float rotationSpeed = 32.0f;  // 旋转速度（度/tick）
    float minRadius = 1.50f;       // 最小半径
    float maxRadius = 4.50f;       // 最大半径
    float minHeightOffset = -2.0f; // 最小高度偏移（相对于玩家中心）
    float maxHeightOffset = 2.0f;  // 最大高度偏移（相对于玩家中心）
    int durationTicks = 60;        // 持续时间（tick）
    float speedVariation = 0.3f;  // 速度随机变化幅度（0.3 = ±30%）
    boolean randomDirection = true; // 是否随机旋转方向（false=统一顺时针，true=随机顺/逆时针）

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

        Level worldIn = livingEntity.level();
        net.minecraft.util.RandomSource random = livingEntity.getRandom();

        // 生成大量随机位置的旋转剑
        for(int i = 0; i < totalSwords; i++) {
            SummondSpiralSwordEntity ss = new SummondSpiralSwordEntity(
                    RecastingEntities.SUMMOND_SPIRAL_SWORD.get(),
                    worldIn,
                    livingEntity
            );

            // 设置旋转中心为玩家
            ss.setCenterEntity(livingEntity);

            // 随机半径（在 minRadius 和 maxRadius 之间）
            float randomRadius = minRadius + random.nextFloat() * (maxRadius - minRadius);

            // 随机初始角度（0-360度）
            float randomAngle = random.nextFloat() * 360.0f;

            // 随机高度偏移（在 minHeightOffset 和 maxHeightOffset 之间）
            float randomHeightOffset = minHeightOffset + random.nextFloat() * (maxHeightOffset - minHeightOffset);

            // 随机旋转速度（基础速度 ± speedVariation）
            float speedModifier = 1.0f + (random.nextFloat() * 2.0f - 1.0f) * speedVariation;
            float currentSpeed = rotationSpeed * speedModifier;

            // 控制旋转方向
            if (randomDirection) {
                // 随机方向：50%概率顺时针，50%逆时针
                if (random.nextBoolean()) {
                    currentSpeed = -currentSpeed;
                }
            }

            // 统一朝向：所有剑都朝外
            boolean isOutward = true;

            // 设置旋转参数
            ss.setRotationRadius(randomRadius);
            ss.setRotationSpeed(currentSpeed);
            ss.setRotationRadiusModifier(1.05f);
            ss.setRotationAngle(randomAngle);   // 随机初始角度
            ss.setRotationAxis(new Vec3(0, 1, 0)); // 绕 Y 轴旋转
            ss.setRotationDirectionOutward(isOutward);

            // 设置随机高度偏移，创造立体风暴效果
            ss.setCenterHeightOffset(randomHeightOffset);

            // 关键：启用旋转时攻击，这样剑就会持续造成伤害而不是飞出去
            ss.setCanAttackDuringRotation(true);

            // 设置基本属性
            ss.setModifiedRatio(attack);
            ss.setColor(slashBladeState.getColorCode());
            ss.setRoll(0);

            ss.setStartDelay(durationTicks);
            ss.setMaxLifeTime(durationTicks);

            worldIn.addFreshEntity(ss);
        }

        // 播放音效
        livingEntity.playSound(
                SoundEvents.CHORUS_FRUIT_TELEPORT,
                0.4F,
                1.2F
        );
    }
}

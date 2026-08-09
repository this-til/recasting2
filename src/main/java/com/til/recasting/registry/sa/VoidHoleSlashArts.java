package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.handler.AttractionHelper;
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
 * 拟似黑洞 Slash Arts
 * 创建一个次元斩，吸引范围内的所有实体向中心
 */
@Setter
@Accessors(chain = true)
public class VoidHoleSlashArts extends ExtendedSlashArts {

    float attack = 0.05f;
    int life = 20;
    float size = 4;
    float range = 32;
    float power = 0.02f;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

        Level worldIn = livingEntity.level();

        // 获取目标位置
        Vec3 pos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

        // 创建自定义的黑洞次元斩
        JudgementCutEntity jc = new JudgementCutEntity(RecastingEntities.JUDGEMENT_CUT.get(), worldIn, livingEntity) {

            @Override
            public void tick() {
                super.tick();

                // 每 tick 执行吸引效果
                if (!this.level().isClientSide()) {
                    Vec3 centerPos = this.position();

                    List<Entity> entities = EntityHelper.getTargettableEntitiesWithinAABB(
                            level(),
                            getShooter(),
                            pos,
                            range
                    );

                    for(Entity entity : entities) {
                        AttractionHelper.applyRadialPull(centerPos, entity, range, power);
                    }
                }
            }
        };

        jc.setPos(pos.x, pos.y, pos.z);

        jc.setColor(slashBladeState.getColorCode());

        // 设置伤害倍率
        jc.setModifiedRatio(attack);

        // 设置生命时间
        jc.setMaxLifeTime(life);

        jc.setColor(slashBladeState.getColorCode());

        jc.setSize(size);

        // 添加到世界
        worldIn.addFreshEntity(jc);

        jc.setRepeatedAttack(true);

        // 播放音效
        worldIn.playSound(null, jc.getX(), jc.getY(), jc.getZ(),
                SoundEvents.ENDERMAN_TELEPORT,
                net.minecraft.sounds.SoundSource.PLAYERS, 0.5F,
                0.8F / (livingEntity.getRandom().nextFloat() * 0.4F + 0.8F));
    }

}

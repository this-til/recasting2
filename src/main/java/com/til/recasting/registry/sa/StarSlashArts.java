package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
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
 * 星流 Slash Arts
 * 发射多把追踪召唤剑，击中后产生次元斩
 * 可选：在周围生成持续的次元斩阵地，定期发射召唤剑
 */
@Setter
@Accessors(chain = true)
public class StarSlashArts extends ExtendedSlashArts {

    int attackNumber = 6;
    float attack = 0.25f;
    float judgementCutAttack = 0.12f;
    float range = 12;
    float zoneNumber = 0;
    int zonerRange = 12;
    int zoneTime = 160;
    float attackProbability = 1 / 20f;
    float summondSwordAttack = 0.02f;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

        Level worldIn = livingEntity.level();

        // 获取攻击目标位置
        Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

        List<LivingEntity> entityList = EntityHelper.getTargettableLivingEntityWithinAABB(
                livingEntity.level(),
                livingEntity,
                attackPos,
                range
        );

        // 第一阶段：发射初始召唤剑
        for(int i = 0; i < attackNumber; i++) {
            // 使用自定义召唤剑，击中后产生次元斩
            StarSummonedSword summonedSword = new StarSummonedSword(
                    RecastingEntities.SUMMOND_SWORD.get(),
                    worldIn,
                    livingEntity,
                    judgementCutAttack
            );

            // 设置属性
            summonedSword.setColor(slashBladeState.getColorCode());
            summonedSword.setModifiedRatio(attack);
            summonedSword.setStartDelay(10 + livingEntity.getRandom().nextInt(10));

            // 设置朝向：如果有敌人，朝向随机敌人，否则朝向攻击位置
            Vec3 targetPos;
            if (!entityList.isEmpty()) {
                Entity target = entityList.get(livingEntity.getRandom().nextInt(entityList.size()));
                targetPos = new Vec3(target.getX(), target.getY() + target.getEyeHeight() * 0.5, target.getZ());
            } else {
                targetPos = attackPos;
            }

            summonedSword.lookAt(targetPos, false);

            // 添加到世界
            worldIn.addFreshEntity(summonedSword);
        }

        // 播放音效
        livingEntity.playSound(
                SoundEvents.CHORUS_FRUIT_TELEPORT,
                0.2F,
                1.45F
        );

        // 第二阶段：生成持续的次元斩阵地（如果 zoneNumber > 0）
        if (zoneNumber > 0) {
            for(int i = 0; i < zoneNumber; i++) {
                // 在玩家周围随机位置生成次元斩
                Vec3 randomOffset = PosHelper.getRandomVectorInCircle(livingEntity.getRandom(), zonerRange);
                Vec3 zonePos = livingEntity.position().add(randomOffset);

                // 创建持续存在的次元斩（仅攻击一次；阵地召唤剑由 tick 负责）
                JudgementCutEntity starJC = new JudgementCutEntity(
                        RecastingEntities.JUDGEMENT_CUT.get(),
                        worldIn,
                        livingEntity
                ) {
                    @Override
                    public void tick() {
                        super.tick();

                        if (!this.level().isClientSide() && this.random.nextFloat() < attackProbability) {
                            Vec3 pos = this.position();

                            // 实时获取目标位置
                            Vec3 currentTargetPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

                            // 使用自定义召唤剑，击中后产生次元斩
                            StarSummonedSword summonedSword = new StarSummonedSword(
                                    RecastingEntities.SUMMOND_SWORD.get(),
                                    this.level(),
                                    livingEntity,
                                    judgementCutAttack
                            );

                            summonedSword.setPos(pos.x, pos.y, pos.z);
                            summonedSword.setColor(slashBladeState.getColorCode());
                            summonedSword.setModifiedRatio(summondSwordAttack);
                            summonedSword.setStartDelay(10);

                            // 朝向实时获取的目标位置
                            summonedSword.lookAt(currentTargetPos, false);

                            this.level().addFreshEntity(summonedSword);

                            // 播放音效
                            this.level().playSound(null, pos.x, pos.y, pos.z,
                                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                                    net.minecraft.sounds.SoundSource.PLAYERS, 0.2F, 1.45F);
                        }
                    }

                };

                starJC.setPos(zonePos.x, zonePos.y, zonePos.z);
                starJC.setColor(slashBladeState.getColorCode());
                starJC.setMaxLifeTime(zoneTime);
                starJC.setSingleAttack(true);

                worldIn.addFreshEntity(starJC);
            }
        }
    }

}

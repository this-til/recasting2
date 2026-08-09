package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
        for (int i = 0; i < attackNumber; i++) {
            SummondSwordEntity summonedSword = new SummondSwordEntity(
                    RecastingEntities.SUMMOND_SWORD.get(),
                    worldIn,
                    livingEntity
            );

            summonedSword.setColor(slashBladeState.getColorCode());
            summonedSword.setModifiedRatio(attack);
            summonedSword.setStartDelay(10 + livingEntity.getRandom().nextInt(10));
            summonedSword.attackActionCallbackPoint.register(hit -> spawnJudgementCut(summonedSword, hit));

            Vec3 targetPos;
            if (!entityList.isEmpty()) {
                Entity target = entityList.get(livingEntity.getRandom().nextInt(entityList.size()));
                targetPos = new Vec3(target.getX(), target.getY() + target.getEyeHeight() * 0.5, target.getZ());
            } else {
                targetPos = attackPos;
            }

            summonedSword.lookAt(targetPos, false);
            worldIn.addFreshEntity(summonedSword);
        }

        livingEntity.playSound(
                SoundEvents.CHORUS_FRUIT_TELEPORT,
                0.2F,
                1.45F
        );

        // 第二阶段：生成持续的次元斩阵地（如果 zoneNumber > 0）
        if (zoneNumber > 0) {
            for (int i = 0; i < zoneNumber; i++) {
                Vec3 randomOffset = PosHelper.getRandomVectorInCircle(livingEntity.getRandom(), zonerRange);
                Vec3 zonePos = livingEntity.position().add(randomOffset);

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
                            Vec3 currentTargetPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

                            SummondSwordEntity summonedSword = new SummondSwordEntity(
                                    RecastingEntities.SUMMOND_SWORD.get(),
                                    this.level(),
                                    livingEntity
                            );

                            summonedSword.setPos(pos.x, pos.y, pos.z);
                            summonedSword.setColor(slashBladeState.getColorCode());
                            summonedSword.setModifiedRatio(summondSwordAttack);
                            summonedSword.setStartDelay(10);
                            summonedSword.attackActionCallbackPoint.register(hit -> spawnJudgementCut(summonedSword, hit));
                            summonedSword.lookAt(currentTargetPos, false);

                            this.level().addFreshEntity(summonedSword);

                            this.level().playSound(
                                    null,
                                    pos.x,
                                    pos.y,
                                    pos.z,
                                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                                    SoundSource.PLAYERS,
                                    0.2F,
                                    1.45F
                            );
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

    private void spawnJudgementCut(SummondSwordEntity sword, Entity target) {
        Level level = sword.level();
        if (level.isClientSide()) {
            return;
        }

        Vec3 jcPos = target.position().add(0, target.getEyeHeight() * 0.5, 0);

        JudgementCutEntity jc = new JudgementCutEntity(
                RecastingEntities.JUDGEMENT_CUT.get(),
                level,
                sword.getShooter()
        );

        jc.setPos(jcPos.x, jcPos.y, jcPos.z);
        jc.setColor(sword.getColor());
        jc.setModifiedRatio(judgementCutAttack);
        jc.setMaxLifeTime(10);
        jc.setSingleAttack(true);

        level.addFreshEntity(jc);

        level.playSound(
                null,
                jcPos.x,
                jcPos.y,
                jcPos.z,
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS,
                0.5F,
                0.8F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );
    }

}

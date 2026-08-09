package com.til.recasting.registry.sa;

import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.registry.RecastingEntities;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 自定义召唤剑 - 击中后产生次元斩
 */
public class StarSummonedSword extends SummondSwordEntity {

    protected final float judgementCutAttack;

    public StarSummonedSword(EntityType<? extends SummondSwordEntity> entityTypeIn, Level worldIn, LivingEntity shooting, float judgementCutAttack) {
        super(entityTypeIn, worldIn, shooting);
        this.judgementCutAttack = judgementCutAttack;

        attackActionCallbackPoint.register(target -> {
            Vec3 jcPos = target.position().add(0, target.getEyeHeight() * 0.5, 0);

            JudgementCutEntity jc = new JudgementCutEntity(
                    RecastingEntities.JUDGEMENT_CUT.get(),
                    this.level(),
                    getShooter()
            );

            jc.setPos(jcPos.x, jcPos.y, jcPos.z);
            jc.setColor(this.getColor());
            jc.setModifiedRatio(judgementCutAttack);
            jc.setMaxLifeTime(10);
            jc.setSingleAttack(true);

            this.level().addFreshEntity(jc);

            this.level().playSound(
                    null,
                    jcPos.x,
                    jcPos.y,
                    jcPos.z,
                    SoundEvents.ENDERMAN_TELEPORT,
                    SoundSource.PLAYERS,
                    0.5F,
                    0.8F / (this.level().getRandom().nextFloat() * 0.4F + 0.8F)
            );
        });
    }

}

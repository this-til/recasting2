package com.til.recasting.registry.sa;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.handler.BuffSourceHelper;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingEntities;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 万象归元
 * 视角中心持续降下绝对伤害次元斩，并驱散/压制周围增益。
 */
@Getter
@Setter
@Accessors(chain = true)
public class PhenomenalReturnSlashArts extends ExtendedSlashArts {

    private String rainTimer = "phenomenal_return_rain";
    private int durationTicks = 4 * 20;
    private float centerRange = 32f;
    private float dispelRange = 128f;
    private float attackRatio = 0.08f;
    private int suppressTicks = 9 * 20;
    /**
     * 次元斩生命时间：0.5s ~ 1s
     */
    private int lifeTicksMin = 10;
    private int lifeTicksMax = 20;
    /**
     * 主动锁定存活目标的概率
     */
    private float targetedChance = 0.4f;

    @Override
    public void trigger(
            LivingEntity livingEntity,
            ItemStack itemStack,
            ISlashBladeState slashBladeState,
            RenderDefinitionExtension renderDefinitionExtension,
            PropertiesDefinitionExtension propertiesDefinitionExtension
    ) {
        if (livingEntity.level().isClientSide()) {
            return;
        }

        List<LivingEntity> nearby = EntityHelper.getTargettableLivingEntityWithinAABB(
                livingEntity.level(),
                livingEntity,
                livingEntity.position(),
                dispelRange
        );
        for(LivingEntity entity : nearby) {
            RecastingBuffTypes.BUFF_SUPPRESS.get().dispelBeneficial(entity);
            RecastingBuffTypes.BUFF_SUPPRESS.get().apply(entity, suppressTicks);
        }
        RecastingBuffTypes.BUFF_SUPPRESS.get().dispelBeneficial(livingEntity);
        RecastingBuffTypes.BUFF_SUPPRESS.get().apply(livingEntity, suppressTicks);
        IBuffStackData buffData = RecastingAttachments.buffStackData(livingEntity);
        buffData.setLevel(RecastingBuffTypes.PHENOMENAL_RETURN.get(), durationTicks, livingEntity.level());
        BuffSourceHelper.recordSourceEntity(buffData, RecastingBuffTypes.PHENOMENAL_RETURN.get(), livingEntity, livingEntity);

        ITimeRun timeRun = RecastingAttachments.timeRun(livingEntity);
        timeRun.removeNamedTimerCell(rainTimer);
        timeRun.addNamedTimerCell(
                rainTimer,
                new ITimeRun.TimerCell(
                        () -> tickRain(livingEntity, slashBladeState, timeRun),
                        1,
                        true
                )
        );
    }

    private void tickRain(LivingEntity livingEntity, ISlashBladeState slashBladeState, ITimeRun timeRun) {
        if (!livingEntity.isAlive() || livingEntity.level().isClientSide()) {
            clearRainState(livingEntity, timeRun);
            return;
        }
        int remainingTicks = RecastingAttachments.buffStackData(livingEntity)
                .getLevel(RecastingBuffTypes.PHENOMENAL_RETURN.get(), livingEntity.level());
        if (remainingTicks <= 0) {
            clearRainState(livingEntity, timeRun);
            return;
        }
        spawnJudgementCut(livingEntity, slashBladeState);
    }

    private void clearRainState(LivingEntity livingEntity, ITimeRun timeRun) {
        timeRun.removeNamedTimerCell(rainTimer);
        RecastingAttachments.buffStackData(livingEntity)
                .setLevel(RecastingBuffTypes.PHENOMENAL_RETURN.get(), 0, livingEntity.level());
    }

    private void spawnJudgementCut(LivingEntity user, ISlashBladeState state) {
        Level world = user.level();
        Vec3 center = PosHelper.getAttackTargetPosition(user, state);
        Vec3 pos = resolveSpawnPos(user, center);

        float size = Mth.lerp(user.getRandom().nextFloat(), 2f, 6f);
        int life = Mth.randomBetweenInclusive(user.getRandom(), lifeTicksMin, lifeTicksMax);

        JudgementCutEntity jc = new JudgementCutEntity(
                RecastingEntities.JUDGEMENT_CUT.get(),
                world,
                user
        );
        jc.setPos(pos.x, pos.y, pos.z);
        jc.setColor(state.getColorCode());
        jc.setModifiedRatio(attackRatio);
        jc.setMaxLifeTime(life);
        jc.setSize(size);
        jc.setRepeatedAttack(false);
        jc.setSingleAttack(true);
        jc.addAttackType(RecastingAttackTypes.ABSOLUTE_ATTACK.get());
        world.addFreshEntity(jc);
    }

    private Vec3 resolveSpawnPos(LivingEntity user, Vec3 center) {
        if (user.getRandom().nextFloat() < targetedChance) {
            List<LivingEntity> targets = EntityHelper.getTargettableLivingEntityWithinAABB(
                    user.level(),
                    user,
                    center,
                    centerRange
            );
            if (!targets.isEmpty()) {
                LivingEntity target = targets.get(user.getRandom().nextInt(targets.size()));
                if (target.isAlive()) {
                    return new Vec3(
                            target.getX(),
                            target.getY() + target.getEyeHeight() * 0.5,
                            target.getZ()
                    );
                }
            }
        }

        double angle = user.getRandom().nextDouble() * Math.PI * 2.0;
        double dist = user.getRandom().nextDouble() * centerRange;
        double yOff = (user.getRandom().nextDouble() - 0.5) * 4.0;
        return center.add(Math.cos(angle) * dist, yOff, Math.sin(angle) * dist);
    }
}

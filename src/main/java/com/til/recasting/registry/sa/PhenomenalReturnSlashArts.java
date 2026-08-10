package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.registry.buff.BuffSuppressBuffType;
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
@Setter
@Accessors(chain = true)
public class PhenomenalReturnSlashArts extends ExtendedSlashArts {

    private String rainTimer = "phenomenal_return_rain";
    private int durationTicks = 4 * 20;
    private float centerRange = 32f;
    private float dispelRange = 128f;
    private float attackRatio = 0.08f;
    private int suppressSeconds = 9;
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
        BuffSuppressBuffType suppress = RecastingBuffTypes.BUFF_SUPPRESS.get();
        for(LivingEntity entity : nearby) {
            suppress.dispelBeneficial(entity);
            suppress.apply(entity, suppressSeconds);
        }
        suppress.dispelBeneficial(livingEntity);
        suppress.apply(livingEntity, suppressSeconds);

        livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            timeRun.removeNamedTimerCell(rainTimer);
            int[] remaining = {durationTicks};
            timeRun.addNamedTimerCell(
                    rainTimer,
                    new ITimeRun.TimerCell(
                            () -> {
                                if (!livingEntity.isAlive() || livingEntity.level().isClientSide()) {
                                    timeRun.removeNamedTimerCell(rainTimer);
                                    return;
                                }
                                if (remaining[0] <= 0) {
                                    timeRun.removeNamedTimerCell(rainTimer);
                                    return;
                                }
                                remaining[0]--;
                                spawnJudgementCut(livingEntity, slashBladeState);
                            },
                            1,
                            true
                    )
            );
        });
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

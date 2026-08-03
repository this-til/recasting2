package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.handler.BuffSuppressHandler;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingEntities;
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

    private static final String RAIN_TIMER = "phenomenal_return_rain";
    private static final int DURATION_TICKS = 9 * 20;
    private static final float CENTER_RANGE = 32f;
    private static final float DISPEL_RANGE = 128f;
    private static final float ATTACK_RATIO = 0.08f;
    private static final int SUPPRESS_SECONDS = 9;

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
                DISPEL_RANGE
        );
        for (LivingEntity entity : nearby) {
            BuffSuppressHandler.dispelBeneficial(entity);
            BuffSuppressHandler.apply(entity, SUPPRESS_SECONDS);
        }
        BuffSuppressHandler.dispelBeneficial(livingEntity);
        BuffSuppressHandler.apply(livingEntity, SUPPRESS_SECONDS);

        livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            timeRun.removeNamedTimerCell(RAIN_TIMER);
            int[] remaining = {DURATION_TICKS};
            timeRun.addNamedTimerCell(
                    RAIN_TIMER,
                    new ITimeRun.TimerCell(
                            () -> {
                                if (!livingEntity.isAlive() || livingEntity.level().isClientSide()) {
                                    timeRun.removeNamedTimerCell(RAIN_TIMER);
                                    return;
                                }
                                if (remaining[0] <= 0) {
                                    timeRun.removeNamedTimerCell(RAIN_TIMER);
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
        double angle = user.getRandom().nextDouble() * Math.PI * 2.0;
        double dist = user.getRandom().nextDouble() * CENTER_RANGE;
        double yOff = (user.getRandom().nextDouble() - 0.5) * 4.0;
        Vec3 pos = center.add(Math.cos(angle) * dist, yOff, Math.sin(angle) * dist);

        float size = Mth.lerp(user.getRandom().nextFloat(), 2f, 6f);
        int life = Mth.randomBetweenInclusive(user.getRandom(), 20, 60);

        JudgementCutEntity jc = new JudgementCutEntity(
                RecastingEntities.JUDGEMENT_CUT.get(),
                world,
                user
        );
        jc.setPos(pos.x, pos.y, pos.z);
        jc.setColor(state.getColorCode());
        jc.setModifiedRatio(ATTACK_RATIO);
        jc.setMaxLifeTime(life);
        jc.setSize(size);
        jc.setRepeatedAttack(false);
        jc.setSingleAttack(true);
        jc.addAttackType(RecastingAttackTypes.ABSOLUTE_ATTACK.get());
        world.addFreshEntity(jc);
    }
}

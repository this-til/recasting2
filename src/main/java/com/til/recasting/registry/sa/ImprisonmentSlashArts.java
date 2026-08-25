package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.constant.R;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 禁锢：在锁定目标处生成缓慢抬升的次元斩，钉住目标并环射普通幻影剑。
 * 无锁定时按默认视锥索敌；目标死亡时在附近随机换下一个目标并移动次元斩。
 */
@Setter
@Accessors(chain = true)
public class ImprisonmentSlashArts extends ExtendedSlashArts {

    private static final String TIMER_NAME = "imprisonment";

    private float centerRatio = 0.25f;
    private float bladeRatio = 0.25f;
    private int durationTicks = 300;
    private int spawnIntervalTicks = 2;
    private float shellRadius = 8.0f;
    private int bladeLifeTicks = 35;
    private float judgementCutSize = 2.0f;
    private float liftPerTick = 0.03f;
    private float retargetRange = 24.0f;

    @Override
    public void trigger(
            LivingEntity livingEntity,
            ItemStack itemStack,
            ISlashBladeState slashBladeState,
            RenderDefinitionExtension renderDefinitionExtension,
            PropertiesDefinitionExtension propertiesDefinitionExtension
    ) {
        Level level = livingEntity.level();
        if (level.isClientSide()) {
            return;
        }

        LivingEntity target = resolveTarget(livingEntity, slashBladeState, level);
        if (target == null) {
            return;
        }

        Vec3 center = PosHelper.getEntityAimPosition(target);
        JudgementCutEntity jc = new JudgementCutEntity(
                RecastingEntities.JUDGEMENT_CUT.get(),
                level,
                livingEntity
        );
        jc.setPos(center.x, center.y, center.z);
        jc.setColor(slashBladeState.getColorCode());
        jc.setModifiedRatio(centerRatio);
        jc.setSize(judgementCutSize);
        jc.setMaxLifeTime(durationTicks);
        jc.setSingleAttack(true);
        jc.setAttackInterval(4);
        level.addFreshEntity(jc);

        level.playSound(
                null,
                center.x,
                center.y,
                center.z,
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS,
                0.6F,
                0.8F / (livingEntity.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        ITimeRun timeRun = RecastingAttachments.timeRun(livingEntity);
        timeRun.removeNamedTimerCell(TIMER_NAME);
        AtomicInteger left = new AtomicInteger(durationTicks);
        AtomicReference<LivingEntity> currentTarget = new AtomicReference<>(target);
        timeRun.addNamedTimerCell(
                TIMER_NAME,
                new ITimeRun.TimerCell(
                        () -> tickImprisonment(livingEntity, currentTarget, jc, left, timeRun, slashBladeState.getColorCode()),
                        1,
                        true
                )
        );
    }

    private void tickImprisonment(
            LivingEntity caster,
            AtomicReference<LivingEntity> currentTarget,
            JudgementCutEntity jc,
            AtomicInteger left,
            ITimeRun timeRun,
            int color
    ) {
        if (!caster.isAlive()
                || caster.isRemoved()
                || !jc.isAlive()
                || jc.isRemoved()
                || left.get() <= 0) {
            timeRun.removeNamedTimerCell(TIMER_NAME);
            return;
        }

        LivingEntity target = currentTarget.get();
        if (target == null || !target.isAlive() || target.isRemoved()) {
            LivingEntity next = pickRetarget(caster, jc);
            if (next == null) {
                timeRun.removeNamedTimerCell(TIMER_NAME);
                return;
            }
            currentTarget.set(next);
            target = next;
            Vec3 center = PosHelper.getEntityAimPosition(target);
            moveJudgementCut(jc, center.x, center.y, center.z);
        }

        left.decrementAndGet();

        // 缓慢抬升中心，并将目标钉在中心高度
        moveJudgementCut(jc, jc.getX(), jc.getY() + liftPerTick, jc.getZ());
        double pinY = jc.getY() - target.getBbHeight() * 0.5;
        target.teleportTo(jc.getX(), pinY, jc.getZ());
        target.setDeltaMovement(Vec3.ZERO);
        target.hurtMarked = true;

        if (left.get() % spawnIntervalTicks != 0) {
            return;
        }

        Level level = caster.level();
        RandomSource random = caster.getRandom();
        Vec3 offset = PosHelper.getRandomVectorInCircle(random, shellRadius);
        Vec3 spawnPos = jc.position().add(offset);

        SummondSwordEntity blade = new SummondSwordEntity(
                RecastingEntities.SUMMOND_SWORD.get(),
                level,
                caster
        );
        blade.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        blade.setModel(R.Models.Special.imprisonment$obj);
        blade.setTexture(R.Models.Special.imprisonment$png);
        blade.setColor(color);
        blade.setModifiedRatio(bladeRatio);
        blade.setMaxLifeTime(bladeLifeTicks);
        blade.setStartDelay(0);
        blade.setIgnoringBlock(true);
        blade.lookAt(PosHelper.getEntityAimPosition(target), false);
        level.addFreshEntity(blade);
    }

    /**
     * 移动次元斩并立刻向追踪客户端广播绝对坐标。
     */
    private void moveJudgementCut(JudgementCutEntity jc, double x, double y, double z) {
        jc.teleportTo(x, y, z);
        if (jc.level() instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().broadcastAndSend(jc, new ClientboundTeleportEntityPacket(jc));
        }
    }

    @Nullable
    private LivingEntity pickRetarget(LivingEntity caster, JudgementCutEntity jc) {
        List<LivingEntity> candidates = EntityHelper.getTargettableLivingEntityWithinAABB(
                caster.level(),
                caster,
                jc.position(),
                retargetRange
        );
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(caster.getRandom().nextInt(candidates.size()));
    }

    @Nullable
    private LivingEntity resolveTarget(LivingEntity caster, ISlashBladeState slashBladeState, Level level) {
        Entity lock = slashBladeState.getTargetEntity(level);
        if (lock instanceof LivingEntity locked && locked.isAlive() && !locked.isRemoved()) {
            return locked;
        }
        return EntityHelper.selectClosestInViewCone(caster).orElse(null);
    }
}

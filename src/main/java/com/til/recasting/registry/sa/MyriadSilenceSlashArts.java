package com.til.recasting.registry.sa;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.*;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.util.DamageStructure;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

/**
 * 万灵寂灭
 * 获得咒令：每 tick 自残 1 血，并对视锥内最近目标发射黑色虚空闪电并叠加寂灭。
 */
@Getter
@Setter
@Accessors(chain = true)
public class MyriadSilenceSlashArts extends ExtendedSlashArts {

    private String timer = "myriad_silence";
    private int decreeTicks = 30 * 20;
    private float voidRatio = 0.14f;
    private int talismanColor = 0xA5527B;

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

        IBuffStackData buffData = RecastingAttachments.buffStackData(livingEntity);
        buffData.setLevel(RecastingBuffTypes.CURSE_DECREE.get(), decreeTicks, livingEntity.level());
        BuffSourceHelper.recordSourceEntity(buffData, RecastingBuffTypes.CURSE_DECREE.get(), livingEntity, livingEntity);

        ITimeRun timeRun = RecastingAttachments.timeRun(livingEntity);
        timeRun.removeNamedTimerCell(timer);
        timeRun.addNamedTimerCell(
                timer,
                new ITimeRun.TimerCell(
                        () -> tickCurse(livingEntity, timeRun),
                        1,
                        true
                )
        );
    }

    private void tickCurse(LivingEntity user, ITimeRun timeRun) {
        if (!user.isAlive() || user.level().isClientSide()) {
            timeRun.removeNamedTimerCell(timer);
            return;
        }

        int decree = RecastingAttachments.buffStackData(user)
                .getLevel(RecastingBuffTypes.CURSE_DECREE.get(), user.level());
        if (decree <= 0) {
            timeRun.removeNamedTimerCell(timer);
            return;
        }

        if (user.getHealth() <= 1f) {
            return;
        }

        float before = user.getHealth();
        AbsoluteHealthChangeGuard.run(() -> user.setHealth(before - 1f));
        if (user.getHealth() >= before) {
            return;
        }

        LivingEntity target = EntityHelper.selectClosestInViewCone(user).orElse(null);
        if (target == null) {
            return;
        }

        Level level = user.level();
        if (level instanceof ServerLevel serverLevel) {
            Vec3 from = PosHelper.getAboveHead(user, 0.5);
            Vec3 to = target.getBoundingBox().getCenter();
            LightningChainEffectHelper.sync(serverLevel, from, to, 0x000000);
        }

        AttackHelper.attack(
                user,
                target,
                new DamageStructure(voidRatio, 0f),
                List.of(
                        RecastingAttackTypes.MYRIAD_SILENCE_ATTACK.get(),
                        RecastingAttackTypes.NO_RECURSION_ATTACK.get()
                )
        );

        IBuffStackData targetBuff = RecastingAttachments.buffStackData(target);
        int current = targetBuff.getLevel(RecastingBuffTypes.SPIRIT_SILENCE.get(), level);
        int next = Math.min(RecastingBuffTypes.SPIRIT_SILENCE.get().getMaxLevel(), current + 1);
        targetBuff.setLevel(RecastingBuffTypes.SPIRIT_SILENCE.get(), next, level);
        BuffSourceHelper.recordSourceEntity(targetBuff, RecastingBuffTypes.SPIRIT_SILENCE.get(), target, user);
        RecastingBuffTypes.SPIRIT_SILENCE.get().onStacksChanged(target);

        spawnTalismanParticles(target);
    }

    private void spawnTalismanParticles(LivingEntity target) {
        if (!(target.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        float r = ((talismanColor >> 16) & 0xFF) / 255f;
        float g = ((talismanColor >> 8) & 0xFF) / 255f;
        float b = (talismanColor & 0xFF) / 255f;
        DustParticleOptions dust = new DustParticleOptions(new Vector3f(r, g, b), 1.25f);
        Vec3 pos = target.getBoundingBox().getCenter();
        ParticleHelper.sendParticlesLongRange(serverLevel, dust, pos.x, pos.y, pos.z, 18, 0.35, 0.5, 0.35, 0.02);
    }
}

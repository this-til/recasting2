package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.AbsoluteHealthChangeGuard;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.BuffSourceHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.LightningChainEffectHelper;
import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.handler.SpiritSilenceBuffHandler;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.util.DamageStructure;
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
 * 获得咒令：每 tick 自残 1 血，并对视角目标周围随机实体发射黑色虚空闪电并叠加寂灭。
 */
@Setter
@Accessors(chain = true)
public class MyriadSilenceSlashArts extends ExtendedSlashArts {

    private static final String TIMER = "myriad_silence";
    private static final int DECREE_SECONDS = 30;
    private static final float VOID_RATIO = 0.34f;
    private static final float LOOK_AREA_RANGE = 16f;
    private static final int TALISMAN_COLOR = 0xA5527B;

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

        BuffType decreeType = RecastingBuffTypes.CURSE_DECREE.get();
        livingEntity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            data.setLevel(decreeType, DECREE_SECONDS, livingEntity.level());
            BuffSourceHelper.recordSourceEntity(data, decreeType, livingEntity, livingEntity);
        });

        livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            timeRun.removeNamedTimerCell(TIMER);
            timeRun.addNamedTimerCell(
                    TIMER,
                    new ITimeRun.TimerCell(
                            () -> tickCurse(livingEntity, slashBladeState, timeRun),
                            1,
                            true
                    )
            );
        });
    }

    private void tickCurse(LivingEntity user, ISlashBladeState state, ITimeRun timeRun) {
        if (!user.isAlive() || user.level().isClientSide()) {
            timeRun.removeNamedTimerCell(TIMER);
            return;
        }

        BuffType decreeType = RecastingBuffTypes.CURSE_DECREE.get();
        int decree = user.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA)
                .map(data -> data.getLevel(decreeType, user.level()))
                .orElse(0);
        if (decree <= 0) {
            timeRun.removeNamedTimerCell(TIMER);
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

        LivingEntity target = pickRandomInLookArea(user, state);
        if (target == null) {
            return;
        }

        Level level = user.level();
        if (level instanceof ServerLevel serverLevel) {
            Vec3 from = user.getEyePosition();
            Vec3 to = target.getBoundingBox().getCenter();
            LightningChainEffectHelper.sync(serverLevel, from, to, 0x000000);
        }

        AttackHelper.attack(
                user,
                target,
                new DamageStructure(VOID_RATIO, 0f),
                List.of(
                        RecastingAttackTypes.MYRIAD_SILENCE_ATTACK.get(),
                        RecastingAttackTypes.NO_RECURSION_ATTACK.get()
                )
        );

        BuffType silenceType = RecastingBuffTypes.SPIRIT_SILENCE.get();
        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            int current = data.getLevel(silenceType, level);
            int next = Math.min(silenceType.getMaxLevel(), current + 1);
            data.setLevel(silenceType, next, level);
            BuffSourceHelper.recordSourceEntity(data, silenceType, target, user);
            SpiritSilenceBuffHandler.onStacksChanged(target);
        });

        spawnTalismanParticles(target);
    }

    private LivingEntity pickRandomInLookArea(LivingEntity user, ISlashBladeState state) {
        Vec3 center = PosHelper.getAttackTargetPosition(user, state);
        List<LivingEntity> targets = EntityHelper.getTargettableLivingEntityWithinAABB(
                user.level(),
                user,
                center,
                LOOK_AREA_RANGE
        );
        if (targets.isEmpty()) {
            return null;
        }
        return targets.get(user.getRandom().nextInt(targets.size()));
    }

    private void spawnTalismanParticles(LivingEntity target) {
        if (!(target.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        float r = ((TALISMAN_COLOR >> 16) & 0xFF) / 255f;
        float g = ((TALISMAN_COLOR >> 8) & 0xFF) / 255f;
        float b = (TALISMAN_COLOR & 0xFF) / 255f;
        DustParticleOptions dust = new DustParticleOptions(new Vector3f(r, g, b), 1.25f);
        Vec3 pos = target.getBoundingBox().getCenter();
        ParticleHelper.sendParticlesLongRange(serverLevel, dust, pos.x, pos.y, pos.z, 18, 0.35, 0.5, 0.35, 0.02);
    }
}

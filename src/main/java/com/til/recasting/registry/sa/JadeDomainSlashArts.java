package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.entity.SummondSpiralSwordEntity;
import com.til.recasting.handler.BuffSourceHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.JadeFireBuffHandler;
import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.registry.instance.BuffType;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * 云界 Slash Arts
 * 释放时连续触发刃解，并在持续时间内每秒随机选择范围目标再次触发刃解。
 */
@Setter
@Accessors(chain = true)
public class JadeDomainSlashArts extends ExtendedSlashArts {

    private final String domainTimer = "jade_domain";
    private final String domainVisualTimer = "jade_domain_visual";
    private final int defaultColor = 0x4C8A8D;
    private final int domainVisualColor = 0xEBF0F8;

    private final int domainDuration = 20 * 30;
    private final int domainTickInterval = 20;
    private final int domainVisualTickInterval = 1;
    private final int domainStacks = domainDuration / 20;
    private int initialBladeReleaseCount = 7;
    private final int initialBladeReleaseDelay = 2;
    private final int jadeFireStacksPerRelease = 10;
    private final float domainRange = 32.0f;
    private final int ringSegments = 36;
    private final int waistSegments = 18;
    private final int highlightNodes = 6;
    private final int ringVisualInterval = 2;
    private final float ringDustSize = 1.4f;
    private final float waistDustSize = 1.0f;
    private final float highlightDustSize = 2.4f;
    private final double domainEmitterAngularSpeed = Math.PI / 40.0;

    private float judgementCutAttack = 0.09f;
    private final float judgementCutSize = 1.5f;
    private float phantomSwordAttack = 0.006f;
    private int minPhantomSwordCount = 6;
    private int maxPhantomSwordCount = 9;
    private final int phantomSwordStartDelay = 20;
    private final float phantomSwordMinTiltAngle = 0f;
    private final float phantomSwordMaxTiltAngle = 30f;

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

        BuffType domainBuffType = RecastingBuffTypes.JADE_DOMAIN.get();
        livingEntity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(buffData -> {
            buffData.setLevel(domainBuffType, domainStacks, livingEntity.level());
            BuffSourceHelper.recordSourceEntity(buffData, domainBuffType, livingEntity, livingEntity);
        });
        renderHighlightNodes(livingEntity, 0.0);
        renderBoundaryRing(livingEntity, 0.0);

        Vec3 initialAttackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);
        List<LivingEntity> initialTargets = new ArrayList<>(EntityHelper.getTargettableLivingEntityWithinAABB(
                livingEntity.level(),
                livingEntity,
                initialAttackPos,
                domainRange
        ));
        livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            for (int i = 0; i < initialBladeReleaseCount; i++) {
                timeRun.addTimerCell(
                        () -> triggerInitialBladeRelease(
                                livingEntity,
                                initialTargets,
                                initialAttackPos,
                                slashBladeState.getColorCode()
                        ),
                        initialBladeReleaseDelay * i
                );
            }
        });

        livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            timeRun.addNamedTimerCell(
                    domainTimer,
                    new ITimeRun.TimerCell(new DomainTick(livingEntity), domainTickInterval, true)
            );
            timeRun.addNamedTimerCell(
                    domainVisualTimer,
                    new ITimeRun.TimerCell(
                            new DomainVisualTick(livingEntity),
                            domainVisualTickInterval,
                            true
                    )
            );
        });
    }

    private void stopDomain(LivingEntity caster) {
        caster.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            timeRun.removeNamedTimerCell(domainTimer);
            timeRun.removeNamedTimerCell(domainVisualTimer);
        });
        caster.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(buffData ->
                buffData.setLevel(RecastingBuffTypes.JADE_DOMAIN.get(), 0, caster.level())
        );
    }

    private List<LivingEntity> getLivingWithinRange(LivingEntity caster) {
        AABB aabb = caster.getBoundingBox().inflate(domainRange);
        return caster.level().getEntitiesOfClass(
                LivingEntity.class,
                aabb,
                entity -> entity.isAlive() && entity != caster
        );
    }

    private DustParticleOptions createPaleDust(float size) {
        float red = ((domainVisualColor >> 16) & 0xFF) / 255.0f;
        float green = ((domainVisualColor >> 8) & 0xFF) / 255.0f;
        float blue = (domainVisualColor & 0xFF) / 255.0f;
        return new DustParticleOptions(new Vector3f(red, green, blue), size);
    }

    private void spawnRingParticle(
            ServerLevel serverLevel,
            DustParticleOptions particle,
            LivingEntity caster,
            double angle,
            double y,
            int count,
            double xOffset,
            double yOffset,
            double zOffset,
            double speed
    ) {
        double x = caster.getX() + Math.cos(angle) * domainRange;
        double z = caster.getZ() + Math.sin(angle) * domainRange;
        ParticleHelper.sendParticlesLongRange(
                serverLevel,
                particle,
                x,
                y,
                z,
                count,
                xOffset,
                yOffset,
                zOffset,
                speed
        );
    }

    private void renderBoundaryRing(LivingEntity caster, double baseAngle) {
        if (!(caster.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        DustParticleOptions ringDust = createPaleDust(ringDustSize);
        double footY = caster.getY() + 0.2;
        for (int i = 0; i < ringSegments; i++) {
            double angle = baseAngle + (Math.PI * 2.0) * i / ringSegments;
            spawnRingParticle(serverLevel, ringDust, caster, angle, footY, 1, 0.0, 0.0, 0.0, 0.0);
        }

        DustParticleOptions waistDust = createPaleDust(waistDustSize);
        double waistY = caster.getY() + 1.1;
        double halfStep = Math.PI / waistSegments;
        for (int i = 0; i < waistSegments; i++) {
            double angle = baseAngle + halfStep + (Math.PI * 2.0) * i / waistSegments;
            spawnRingParticle(serverLevel, waistDust, caster, angle, waistY, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    private void renderHighlightNodes(LivingEntity caster, double baseAngle) {
        if (!(caster.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        DustParticleOptions highlightDust = createPaleDust(highlightDustSize);
        for (int i = 0; i < highlightNodes; i++) {
            double angle = baseAngle + (Math.PI * 2.0) * i / highlightNodes;
            double x = caster.getX() + Math.cos(angle) * domainRange;
            double z = caster.getZ() + Math.sin(angle) * domainRange;
            double y = caster.getY() + 0.2;
            ParticleHelper.sendParticlesLongRange(
                    serverLevel,
                    highlightDust,
                    x,
                    y,
                    z,
                    2,
                    0.05,
                    0.35,
                    0.05,
                    0.0
            );
            ParticleHelper.sendParticlesLongRange(
                    serverLevel,
                    ParticleTypes.END_ROD,
                    x,
                    y + 0.4,
                    z,
                    1,
                    0.02,
                    0.5,
                    0.02,
                    0.01
            );
        }
    }

    private LivingEntity pickRandomTarget(LivingEntity caster) {
        List<LivingEntity> candidates = getLivingWithinRange(caster);
        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.get(caster.getRandom().nextInt(candidates.size()));
    }

    private void triggerInitialBladeRelease(
            LivingEntity caster,
            List<LivingEntity> targets,
            Vec3 fallbackPos,
            int color
    ) {
        if (!caster.isAlive() || caster.isRemoved() || caster.level().isClientSide()) {
            return;
        }

        LivingEntity target = pickRandomAliveTarget(caster, targets);
        if (target != null) {
            triggerBladeRelease(caster, target, color);
            return;
        }

        spawnJudgementCut(caster.level(), caster, fallbackPos, color);
    }

    private LivingEntity pickRandomAliveTarget(LivingEntity caster, List<LivingEntity> targets) {
        for (int attempt = 0; attempt < 10 && !targets.isEmpty(); attempt++) {
            LivingEntity candidate = targets.get(caster.getRandom().nextInt(targets.size()));
            if (candidate.isAlive()) {
                return candidate;
            }

            targets.remove(candidate);
        }

        return null;
    }

    private void triggerBladeReleaseAtRandomTarget(LivingEntity caster, int color) {
        if (!caster.isAlive() || caster.isRemoved() || caster.level().isClientSide()) {
            return;
        }

        LivingEntity target = pickRandomTarget(caster);
        if (target == null) {
            return;
        }

        triggerBladeRelease(caster, target, color);
    }

    private void triggerBladeRelease(LivingEntity caster, LivingEntity target, int color) {
        Level world = caster.level();
        Vec3 targetPos = new Vec3(
                target.getX(),
                target.getY() + target.getEyeHeight() * 0.5,
                target.getZ()
        );

        spawnJudgementCut(world, caster, targetPos, color);
        spawnSmallPhantomSwords(world, caster, target, color);
    }

    private void spawnJudgementCut(Level world, LivingEntity caster, Vec3 targetPos, int color) {
        JudgementCutEntity judgementCut = new JudgementCutEntity(
                RecastingEntities.JUDGEMENT_CUT.get(),
                world,
                caster
        );
        judgementCut.setPos(targetPos.x, targetPos.y, targetPos.z);
        judgementCut.setColor(color);
        judgementCut.setModifiedRatio(judgementCutAttack);
        judgementCut.setSize(judgementCutSize);
        judgementCut.setRepeatedAttack(false);
        judgementCut.setRoll(Mth.nextFloat(caster.getRandom(), 0.0f, 360.0f));
        judgementCut.attackActionCallbackPoint.register(hitEntity -> applyJadeFire(hitEntity, caster));
        world.addFreshEntity(judgementCut);
        world.playSound(null, targetPos.x, targetPos.y, targetPos.z,
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS, 0.5F,
                0.8F / (caster.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    private void spawnSmallPhantomSwords(Level world, LivingEntity caster, LivingEntity target, int color) {
        int swordCount = Mth.nextInt(caster.getRandom(), minPhantomSwordCount, maxPhantomSwordCount);
        float angleOffset = caster.getRandom().nextFloat() * 360.0f;
        float tiltAngle = caster.getRandom().nextFloat() * (phantomSwordMaxTiltAngle - phantomSwordMinTiltAngle) + phantomSwordMinTiltAngle;
        float horizontalAngle = caster.getRandom().nextFloat() * 360f;

        for (int i = 0; i < swordCount; i++) {
            spawnSmallPhantomSword(world, caster, target, color, angleOffset, tiltAngle, horizontalAngle, swordCount, i);
        }
    }

    private void spawnSmallPhantomSword(
            Level world,
            LivingEntity caster,
            LivingEntity target,
            int color,
            float angleOffset,
            float tiltAngle,
            float horizontalAngle,
            int swordCount,
            int swordIndex
    ) {
        SummondSpiralSwordEntity summonedSword = new SummondSpiralSwordEntity(
                RecastingEntities.SUMMOND_SPIRAL_SWORD.get(),
                world,
                caster
        );
        summonedSword.setCenterEntity(target);
        summonedSword.setRadiusExpansion(2.5f, 6.0f, 30);
        summonedSword.setSpeedDecay(16.0f, 0.3f, 30);
        summonedSword.setRotationAngle(angleOffset + (360.0f / swordCount * swordIndex));

        float tiltRad = (float) Math.toRadians(tiltAngle);
        float horizontalRad = (float) Math.toRadians(horizontalAngle);

        double y = Math.cos(tiltRad);
        double x = Math.sin(tiltRad) * Math.cos(horizontalRad);
        double z = Math.sin(tiltRad) * Math.sin(horizontalRad);

        summonedSword.setRotationAxis(new Vec3(x, y, z));
        summonedSword.setRotationDirectionOutward(false);
        summonedSword.setIgnoringBlock(true);
        summonedSword.setColor(color);
        summonedSword.setModifiedRatio(phantomSwordAttack);
        summonedSword.setRoll(0);
        summonedSword.setStartDelay(phantomSwordStartDelay);
        summonedSword.addAttackType(RecastingAttackTypes.SPIRAL_SWORD_ATTACK.get());
        world.addFreshEntity(summonedSword);
    }

    private void applyJadeFire(LivingEntity target, LivingEntity caster) {
        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(buffData -> {
            BuffType jadeFireBuffType = RecastingBuffTypes.JADE_FIRE.get();
            int currentLevel = buffData.getLevel(jadeFireBuffType, target.level());
            buffData.setLevel(jadeFireBuffType, currentLevel + jadeFireStacksPerRelease, target.level());
            BuffSourceHelper.recordSourceEntity(buffData, jadeFireBuffType, target, caster);
            JadeFireBuffHandler.ensureJadeFireTimer(target);
        });
    }

    private class DomainTick implements Runnable {

        private final LivingEntity caster;

        private DomainTick(LivingEntity caster) {
            this.caster = caster;
        }

        @Override
        public void run() {
            if (!caster.isAlive() || caster.isRemoved() || caster.level().isClientSide()) {
                stopDomain(caster);
                return;
            }

            boolean hasDomainBuff = caster.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA)
                    .map(data -> data.getLevel(RecastingBuffTypes.JADE_DOMAIN.get(), caster.level()) > 0)
                    .orElse(false);
            if (!hasDomainBuff) {
                stopDomain(caster);
                return;
            }

            ItemStack mainHand = caster.getMainHandItem();
            if (!mainHand.getCapability(ItemSlashBlade.BLADESTATE).isPresent()) {
                return;
            }

            triggerBladeReleaseAtRandomTarget(
                    caster,
                    mainHand.getCapability(ItemSlashBlade.BLADESTATE)
                            .map(ISlashBladeState::getColorCode)
                            .orElse(defaultColor)
            );
        }
    }

    private class DomainVisualTick implements Runnable {

        private final LivingEntity caster;
        private double angle;
        private int tickCounter;

        private DomainVisualTick(LivingEntity caster) {
            this.caster = caster;
        }

        @Override
        public void run() {
            if (!caster.isAlive() || caster.isRemoved() || caster.level().isClientSide()) {
                stopDomain(caster);
                return;
            }

            boolean hasDomainBuff = caster.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA)
                    .map(data -> data.getLevel(RecastingBuffTypes.JADE_DOMAIN.get(), caster.level()) > 0)
                    .orElse(false);
            if (!hasDomainBuff) {
                stopDomain(caster);
                return;
            }

            renderHighlightNodes(caster, angle);
            if ((tickCounter % ringVisualInterval) == 0) {
                renderBoundaryRing(caster, angle);
            }
            tickCounter++;
            angle += domainEmitterAngularSpeed;
        }
    }
}

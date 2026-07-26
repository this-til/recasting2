package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.entity.SummondSpiralSwordEntity;
import com.til.recasting.handler.BuffSourceHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.JadeFireBuffHandler;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.registry.instance.BuffType;
import java.util.List;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * 青界 Slash Arts
 * 释放时连续触发刃解，并在持续时间内每秒随机选择范围目标再次触发刃解。
 */
@Setter
@Accessors(chain = true)
public class JadeDomainSlashArts extends ExtendedSlashArts {

    private static final String DOMAIN_TIMER = "jade_domain";
    private static final int DEFAULT_COLOR = 0x4C8A8D;

    private static final int DOMAIN_DURATION = 20 * 30;
    private static final int DOMAIN_TICK_INTERVAL = 20;
    private static final int DOMAIN_STACKS = DOMAIN_DURATION / 20;
    private int initialBladeReleaseCount = 7;
    private static final int INITIAL_BLADE_RELEASE_DELAY = 2;
    private static final int JADE_FIRE_STACKS_PER_RELEASE = 10;
    private static final float DOMAIN_RANGE = 32.0f;

    private float judgementCutAttack = 0.3f;
    private static final float JUDGEMENT_CUT_SIZE = 1.5f;
    private float phantomSwordAttack = 0.02f;
    private int minPhantomSwordCount = 6;
    private int maxPhantomSwordCount = 9;
    private static final int PHANTOM_SWORD_START_DELAY = 20;
    private static final float PHANTOM_SWORD_MIN_TILT_ANGLE = 0f;
    private static final float PHANTOM_SWORD_MAX_TILT_ANGLE = 30f;

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
            buffData.setLevel(domainBuffType, DOMAIN_STACKS, livingEntity.level());
            BuffSourceHelper.recordSourceEntity(buffData, domainBuffType, livingEntity, livingEntity);
        });

        livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            for (int i = 0; i < initialBladeReleaseCount; i++) {
                timeRun.addTimerCell(
                        () -> triggerBladeReleaseAtRandomTarget(livingEntity, slashBladeState.getColorCode()),
                        INITIAL_BLADE_RELEASE_DELAY * (i + 1)
                );
            }
        });

        livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun ->
                timeRun.addNamedTimerCell(
                        DOMAIN_TIMER,
                        new ITimeRun.TimerCell(new DomainTick(livingEntity), DOMAIN_TICK_INTERVAL, true)
                )
        );
    }

    private void stopDomain(LivingEntity caster) {
        caster.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> timeRun.removeNamedTimerCell(DOMAIN_TIMER));
        caster.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(buffData ->
                buffData.setLevel(RecastingBuffTypes.JADE_DOMAIN.get(), 0, caster.level())
        );
    }

    private List<LivingEntity> getLivingWithinRange(LivingEntity caster) {
        AABB aabb = caster.getBoundingBox().inflate(DOMAIN_RANGE);
        return caster.level().getEntitiesOfClass(
                LivingEntity.class,
                aabb,
                entity -> entity.isAlive() && entity != caster
        );
    }

    private LivingEntity pickRandomTarget(LivingEntity caster) {
        List<LivingEntity> candidates = getLivingWithinRange(caster);
        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.get(caster.getRandom().nextInt(candidates.size()));
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
        judgementCut.setSize(JUDGEMENT_CUT_SIZE);
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
        float tiltAngle = caster.getRandom().nextFloat() * (PHANTOM_SWORD_MAX_TILT_ANGLE - PHANTOM_SWORD_MIN_TILT_ANGLE) + PHANTOM_SWORD_MIN_TILT_ANGLE;
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
        summonedSword.setStartDelay(PHANTOM_SWORD_START_DELAY);
        summonedSword.addAttackType(RecastingAttackTypes.SPIRAL_SWORD_ATTACK.get());
        world.addFreshEntity(summonedSword);
    }

    private void applyJadeFire(LivingEntity target, LivingEntity caster) {
        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(buffData -> {
            BuffType jadeFireBuffType = RecastingBuffTypes.JADE_FIRE.get();
            int currentLevel = buffData.getLevel(jadeFireBuffType, target.level());
            buffData.setLevel(jadeFireBuffType, currentLevel + JADE_FIRE_STACKS_PER_RELEASE, target.level());
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

            triggerBladeReleaseAtRandomTarget(caster, resolveColor(caster));
        }

        private int resolveColor(LivingEntity caster) {
            return caster.getMainHandItem()
                    .getCapability(ItemSlashBlade.BLADESTATE)
                    .map(ISlashBladeState::getColorCode)
                    .orElse(DEFAULT_COLOR);
        }
    }
}

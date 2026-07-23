package com.til.recasting.registry.sa;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.entity.SummondSpiralSwordEntity;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.BuffSourceHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.util.DamageStructure;
import java.util.List;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;

/**
 * 青界 Slash Arts
 * 为施术者展开持续 30 秒的领域，并周期性触发翠火与刃解。
 */
@Setter
@Accessors(chain = true)
public class QingJieSlashArts extends ExtendedSlashArts {

    private static final String DOMAIN_TIMER = "qing_jie_domain";
    private static final String KEY_COLOR = "Color";
    private static final int DEFAULT_COLOR = 0x4C8A8D;

    private static final int DOMAIN_DURATION = 20 * 30;
    private static final int DOMAIN_TICK_INTERVAL = 10;
    private static final int DOMAIN_STACKS = DOMAIN_DURATION / 20;
    private static final int BLADE_RELEASE_INTERVAL = 30;
    private static final float DOMAIN_RANGE = 32.0f;

    private static final float JADE_FIRE_DOT_RATIO = 0.05f;
    private static final float JUDGEMENT_CUT_ATTACK = 0.3f;
    private static final float JUDGEMENT_CUT_SIZE = 1.5f;
    private static final float PHANTOM_EXPLOSION_ATTACK = 0.02f;
    private static final int PHANTOM_SWORD_START_DELAY = 20;
    private static final float PHANTOM_EXPLOSION_MIN_TILT_ANGLE = 0f;
    private static final float PHANTOM_EXPLOSION_MAX_TILT_ANGLE = 30f;

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

        BuffType domainBuffType = RecastingBuffTypes.QING_JIE_DOMAIN.get();
        livingEntity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(buffData -> {
            buffData.setLevel(domainBuffType, DOMAIN_STACKS, livingEntity.level());
            BuffSourceHelper.recordSourceEntity(buffData, domainBuffType, livingEntity, livingEntity);
            buffData.getOrCreateCustomData(domainBuffType, livingEntity.level()).putInt(KEY_COLOR, slashBladeState.getColorCode());
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
                buffData.setLevel(RecastingBuffTypes.QING_JIE_DOMAIN.get(), 0, caster.level())
        );
    }

    private void applyJadeFireToNearbyLiving(LivingEntity caster) {
        List<LivingEntity> nearby = getLivingWithinRange(caster);
        BuffType jadeFireBuffType = RecastingBuffTypes.JADE_FIRE.get();

        for (LivingEntity target : nearby) {
            target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(buffData -> {
                buffData.setLevel(jadeFireBuffType, 1, target.level());
                BuffSourceHelper.recordSourceEntity(buffData, jadeFireBuffType, target, caster);
            });
            target.setDeltaMovement(Vec3.ZERO);
            AttackHelper.attack(
                    caster,
                    target,
                    new DamageStructure(JADE_FIRE_DOT_RATIO, 0f),
                    List.of(RecastingAttackTypes.JADE_FIRE_ATTACK.get())
            );
        }
    }

    private List<LivingEntity> getLivingWithinRange(LivingEntity caster) {
        AABB aabb = caster.getBoundingBox().inflate(DOMAIN_RANGE);
        return caster.level().getEntitiesOfClass(
                LivingEntity.class,
                aabb,
                entity -> entity.isAlive() && entity != caster
        );
    }

    private LivingEntity pickRandomJadeFireTarget(LivingEntity caster) {
        List<LivingEntity> candidates = getLivingWithinRange(caster).stream()
                .filter(target -> target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA)
                        .map(data -> data.getLevel(RecastingBuffTypes.JADE_FIRE.get(), target.level()) > 0)
                        .orElse(false))
                .toList();

        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.get(caster.getRandom().nextInt(candidates.size()));
    }

    private void triggerBladeRelease(LivingEntity caster, LivingEntity target) {
        Level world = caster.level();
        Vec3 targetPos = new Vec3(
                target.getX(),
                target.getY() + target.getEyeHeight() * 0.5,
                target.getZ()
        );
        int color = resolveDomainColor(caster);

        spawnJudgementCut(world, caster, targetPos, color);

        int swordCount = Mth.nextInt(caster.getRandom(), 4, 7);
        float angleOffset = caster.getRandom().nextFloat() * 360.0f;
        float tiltAngle = caster.getRandom().nextFloat() * (PHANTOM_EXPLOSION_MAX_TILT_ANGLE - PHANTOM_EXPLOSION_MIN_TILT_ANGLE) + PHANTOM_EXPLOSION_MIN_TILT_ANGLE;
        float horizontalAngle = caster.getRandom().nextFloat() * 360f;
        for (int i = 0; i < swordCount; i++) {
            spawnSmallPhantomSword(world, caster, target, color, angleOffset, tiltAngle, horizontalAngle, swordCount, i);
        }
    }

    private int resolveDomainColor(LivingEntity caster) {
        return caster.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA)
                .map(data -> {
                    IBuffStackData.BuffEntry entry = data.getEntry(RecastingBuffTypes.QING_JIE_DOMAIN.get());
                    if (entry != null && entry.getCustomData() != null && entry.getCustomData().contains(KEY_COLOR)) {
                        return entry.getCustomData().getInt(KEY_COLOR);
                    }

                    ItemStack mainHandItem = caster.getMainHandItem();
                    LazyOptional<ISlashBladeState> slashBladeState = mainHandItem.getCapability(ItemSlashBlade.BLADESTATE);
                    return slashBladeState.map(ISlashBladeState::getColorCode).orElse(DEFAULT_COLOR);
                })
                .orElse(DEFAULT_COLOR);
    }

    private void spawnJudgementCut(Level world, LivingEntity caster, Vec3 targetPos, int color) {
        JudgementCutEntity judgementCut = new JudgementCutEntity(
                RecastingEntities.JUDGEMENT_CUT.get(),
                world,
                caster
        );
        judgementCut.setPos(targetPos.x, targetPos.y, targetPos.z);
        judgementCut.setColor(color);
        judgementCut.setModifiedRatio(JUDGEMENT_CUT_ATTACK);
        judgementCut.setSize(JUDGEMENT_CUT_SIZE);
        judgementCut.setRepeatedAttack(false);
        world.addFreshEntity(judgementCut);
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
        summonedSword.setModifiedRatio(PHANTOM_EXPLOSION_ATTACK);
        summonedSword.setRoll(0);
        summonedSword.setStartDelay(PHANTOM_SWORD_START_DELAY);
        summonedSword.addAttackType(RecastingAttackTypes.SPIRAL_SWORD_ATTACK.get());
        world.addFreshEntity(summonedSword);
    }

    private class DomainTick implements Runnable {

        private final LivingEntity caster;
        private int livedTicks;

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
                    .map(data -> data.getLevel(RecastingBuffTypes.QING_JIE_DOMAIN.get(), caster.level()) > 0)
                    .orElse(false);
            if (!hasDomainBuff) {
                stopDomain(caster);
                return;
            }

            applyJadeFireToNearbyLiving(caster);

            if (livedTicks % BLADE_RELEASE_INTERVAL == 0) {
                LivingEntity target = pickRandomJadeFireTarget(caster);
                if (target != null) {
                    triggerBladeRelease(caster, target);
                }
            }

            livedTicks += DOMAIN_TICK_INTERVAL;
        }
    }
}

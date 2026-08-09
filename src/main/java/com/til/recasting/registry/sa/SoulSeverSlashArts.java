package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.entity.SlashEffectEntity;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.util.DamageStructure;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.client.UserPoseOverrider;
import mods.flammpfeil.slashblade.event.handler.FallHandler;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 断魄 Slash Arts
 * 复用原版虚无刀界的刀光与结算时点，并在伤害结算后追加次元斩。
 */
@Setter
@Accessors(chain = true)
public class SoulSeverSlashArts extends ExtendedSlashArts {

    private int slashLife = 36;
    private float slashAttack = 3.25f;
    private float slashSize = 3f;
    private float giantJudgementCutAttack = 1.5f;
    private float giantJudgementCutSize = 6f;
    private int giantJudgementCutCount = 5;

    @Override
    public void trigger(
            LivingEntity livingEntity,
            ItemStack itemStack,
            ISlashBladeState slashBladeState,
            RenderDefinitionExtension renderDefinitionExtension,
            PropertiesDefinitionExtension propertiesDefinitionExtension
    ) {
        spawnVoidSlashEffect(livingEntity.level(), livingEntity, slashBladeState);
    }

    @Override
    public ComboState createComboState() {
        return ComboState.Builder.newInstance()
                .startAndEnd(2200, 2277)
                .priority(50)
                .speed(1.0F)
                .next(entity -> SlashBlade.prefix("void_slash"))
                .nextOfTimeout(entity -> SlashBlade.prefix("void_slash_sheath"))
                .addTickAction(entity -> entity.setDeltaMovement(Vec3.ZERO))
                .addTickAction(ComboState.TimeLineTickAction.getBuilder().put(16, this::activate).build())
                .addTickAction(ComboState.TimeLineTickAction.getBuilder()
                        .put(16, entityIn -> UserPoseOverrider.setRot(entityIn, -36, true))
                        .put(17, entityIn -> UserPoseOverrider.setRot(entityIn, -36, true))
                        .put(18, entityIn -> UserPoseOverrider.setRot(entityIn, -36, true))
                        .put(19, entityIn -> UserPoseOverrider.setRot(entityIn, -36, true))
                        .put(20, entityIn -> UserPoseOverrider.setRot(entityIn, -36, true))
                        .put(21, entityIn -> UserPoseOverrider.setRot(entityIn, 0, true))
                        .put(57, entityIn -> UserPoseOverrider.setRot(entityIn, 18, true))
                        .put(58, entityIn -> UserPoseOverrider.setRot(entityIn, 18, true))
                        .put(59, entityIn -> UserPoseOverrider.setRot(entityIn, 18, true))
                        .put(60, entityIn -> UserPoseOverrider.setRot(entityIn, 18, true))
                        .put(61, entityIn -> UserPoseOverrider.setRot(entityIn, 18, true))
                        .put(62, entityIn -> UserPoseOverrider.setRot(entityIn, 18, true))
                        .put(63, entityIn -> UserPoseOverrider.setRot(entityIn, 18, true))
                        .put(64, entityIn -> UserPoseOverrider.setRot(entityIn, 18, true))
                        .put(65, entityIn -> UserPoseOverrider.setRot(entityIn, 18, true))
                        .put(66, entityIn -> UserPoseOverrider.setRot(entityIn, 18, true))
                        .put(67, entityIn -> UserPoseOverrider.setRot(entityIn, 0, true))
                        .build())
                .addTickAction(FallHandler::fallDecrease)
                .addHitEffect((target, attack) -> StunManager.setStun(target, 40))
                .build();
    }

    private void activate(LivingEntity livingEntity) {
        if (livingEntity.level().isClientSide()) {
            return;
        }

        ItemStack mainHandItem = livingEntity.getMainHandItem();
        if (mainHandItem.isEmpty()) {
            return;
        }

        mainHandItem.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(slashBladeState -> mainHandItem.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION).ifPresent(propertiesDefinitionExtension -> {
            mainHandItem.getCapability(CapabilityRegistryHandler.RENDER_DEFINITION_EXTENSION).ifPresent(renderDefinitionExtension -> {
                trigger(livingEntity, mainHandItem, slashBladeState, renderDefinitionExtension, propertiesDefinitionExtension);
            });
        }));
    }

    private void spawnVoidSlashEffect(
            Level worldIn,
            LivingEntity livingEntity,
            ISlashBladeState slashBladeState
    ) {
        Vec3 pos = livingEntity.position()
                .add(0.0D, livingEntity.getEyeHeight() * 0.75D, 0.0D)
                .add(livingEntity.getLookAngle().scale(0.3f));

        SlashEffectEntity slashEffect = new TimedVoidSlashEffect(
                RecastingEntities.SLASH_EFFECT.get(),
                worldIn,
                livingEntity,
                slashBladeState
        );
        slashEffect.setPos(pos.x, pos.y, pos.z);
        slashEffect.setRoll(180f);
        slashEffect.setYRot(livingEntity.getYRot() - 22.5f);
        slashEffect.setXRot(0f);
        slashEffect.setColor(slashBladeState.getColorCode());
        slashEffect.setMaxLifeTime(slashLife);
        slashEffect.setSize(slashSize);
        slashEffect.setThump(false);
        slashEffect.setRepeatedAttack(false);
        worldIn.addFreshEntity(slashEffect);
    }

    private void spawnJudgementCut(
            Level worldIn,
            LivingEntity livingEntity,
            ISlashBladeState slashBladeState,
            Vec3 pos,
            float attack,
            float size,
            int delay
    ) {
        livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(
                timeRun -> timeRun.addTimerCell(
                        () -> spawnJudgementCutNow(worldIn, livingEntity, slashBladeState, pos, attack, size),
                        delay
                )
        );
    }

    private void spawnOrderedGiantJudgementCuts(
            LivingEntity shooter,
            ISlashBladeState slashBladeState,
            List<Vec3> targetPositions
    ) {
        if (targetPositions.isEmpty()) {
            return;
        }

        int count = Math.min(giantJudgementCutCount, targetPositions.size());
        for(int i = 0; i < count; i++) {
            int delay = i + 1;
            spawnJudgementCut(
                    shooter.level(),
                    shooter,
                    slashBladeState,
                    targetPositions.get(i),
                    giantJudgementCutAttack,
                    giantJudgementCutSize,
                    delay
            );
        }
    }

    private void spawnJudgementCutNow(
            Level worldIn,
            LivingEntity livingEntity,
            ISlashBladeState slashBladeState,
            Vec3 pos,
            float attack,
            float size
    ) {
        JudgementCutEntity judgementCut = new LockedJudgementCutEntity(
                RecastingEntities.JUDGEMENT_CUT.get(),
                worldIn,
                livingEntity
        );
        judgementCut.setPos(pos.x, pos.y, pos.z);
        judgementCut.setColor(slashBladeState.getColorCode());
        judgementCut.setModifiedRatio(attack);
        judgementCut.setSize(size);
        judgementCut.setRepeatedAttack(false);
        worldIn.addFreshEntity(judgementCut);

        worldIn.playSound(
                null,
                judgementCut.getX(),
                judgementCut.getY(),
                judgementCut.getZ(),
                SoundEvents.ENDERMAN_TELEPORT,
                net.minecraft.sounds.SoundSource.PLAYERS,
                0.5F,
                0.8F / (livingEntity.getRandom().nextFloat() * 0.4F + 0.8F)
        );
    }

    private Vec3 getTargetPos(LivingEntity target) {
        return new Vec3(
                target.getX(),
                target.getY() + target.getEyeHeight() * 0.5,
                target.getZ()
        );
    }

    private List<Vec3> planRepeatableTargetPositions(LivingEntity shooter, List<LivingEntity> hitTargets) {
        if (hitTargets.isEmpty()) {
            return List.of();
        }

        return java.util.stream.IntStream.range(0, giantJudgementCutCount)
                .mapToObj(i -> {
                    LivingEntity target = hitTargets.get(hitTargets.size() == 1
                            ? 0
                            : shooter.getRandom().nextInt(hitTargets.size()));
                    return getTargetPos(target);
                })
                .toList();
    }

    private static class LockedJudgementCutEntity extends JudgementCutEntity {

        private final boolean repeatedAttackLocked;

        private LockedJudgementCutEntity(
                EntityType<? extends JudgementCutEntity> entityTypeIn,
                Level worldIn,
                LivingEntity shooting
        ) {
            super(entityTypeIn, worldIn, shooting);
            this.repeatedAttackLocked = true;
            super.setRepeatedAttack(false);
        }

        @Override
        public void setRepeatedAttack(boolean repeatedAttack) {
            if (repeatedAttackLocked && repeatedAttack) {
                return;
            }
            super.setRepeatedAttack(repeatedAttack);
        }
    }

    private class TimedVoidSlashEffect extends SlashEffectEntity {

        private final ISlashBladeState slashBladeState;
        private boolean resolved;

        private TimedVoidSlashEffect(
                EntityType<? extends SlashEffectEntity> entityTypeIn,
                Level worldIn,
                LivingEntity shooting,
                ISlashBladeState slashBladeState
        ) {
            super(entityTypeIn, worldIn, shooting);
            this.slashBladeState = slashBladeState;
            this.setModifiedRatio(0f);
            this.setMute(false);
            this.setRepeatedAttack(false);
        }

        @Override
        public void remove(@NotNull RemovalReason reason) {
            resolveOnce();
            super.remove(reason);
        }

        private void resolveOnce() {
            if (resolved || level().isClientSide()) {
                return;
            }

            resolved = true;
            LivingEntity shooter = getShooter();
            if (shooter == null) {
                return;
            }

            List<LivingEntity> hitTargets = (this.alreadyHits == null
                    ? List.<Entity>of()
                    : this.alreadyHits).stream()
                    .filter(LivingEntity.class::isInstance)
                    .map(LivingEntity.class::cast)
                    .filter(target -> target != shooter)
                    .toList();

            List<Vec3> plannedTargetPositions = planRepeatableTargetPositions(shooter, hitTargets);

            for(LivingEntity target : hitTargets) {
                if (!target.isAlive()) {
                    continue;
                }
                AttackHelper.doMeleeAttack(
                        shooter,
                        target,
                        new DamageStructure(slashAttack, 0f),
                        List.of(RecastingAttackTypes.SOUL_SEVER_DELAYED_ATTACK.get())
                );
            }

            spawnOrderedGiantJudgementCuts(shooter, slashBladeState, plannedTargetPositions);
        }
    }
}

package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.entity.SlashEffectEntity;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.util.DamageStructure;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

/**
 * 断魄 Slash Arts
 * 复用原版虚无刀界的刀光与结算时点，并在伤害结算后追加次元斩。
 */
@Setter
@Accessors(chain = true)
public class DuanPoSlashArts extends ExtendedSlashArts {

    private int slashLife = 36;
    private float slashAttack = 1.6f;
    private float slashSize = 3f;
    private float judgementCutAttack = 1.0f;
    private float giantJudgementCutAttack = 1.5f;
    private float judgementCutSize = 1f;
    private float giantJudgementCutSize = 6f;
    private int extraJudgementCutCount = 4;

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
            float size
    ) {
        int delay = livingEntity.getRandom().nextInt(6);
        livingEntity.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(
                timeRun -> timeRun.addTimerCell(
                        () -> spawnJudgementCutNow(worldIn, livingEntity, slashBladeState, pos, attack, size),
                        delay
                )
        );
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

            List<LivingEntity> hitTargets = (this.alreadyHits == null ? List.<Entity>of() : this.alreadyHits).stream()
                    .filter(LivingEntity.class::isInstance)
                    .map(LivingEntity.class::cast)
                    .filter(LivingEntity::isAlive)
                    .toList();

            for (LivingEntity target : hitTargets) {
                AttackHelper.doMeleeAttack(
                        shooter,
                        target,
                        new DamageStructure(slashAttack, 0f),
                        List.of(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get())
                );
                spawnJudgementCut(level(), shooter, slashBladeState, getTargetPos(target), judgementCutAttack, judgementCutSize);
            }

            List<LivingEntity> randomTargets = new ArrayList<>(hitTargets);
            Collections.shuffle(randomTargets, new java.util.Random(shooter.getRandom().nextLong()));
            int extraCount = Math.min(extraJudgementCutCount, randomTargets.size());
            for (int i = 0; i < extraCount; i++) {
                spawnJudgementCut(
                        level(),
                        shooter,
                        slashBladeState,
                        getTargetPos(randomTargets.get(i)),
                        giantJudgementCutAttack,
                        giantJudgementCutSize
                );
            }
        }
    }
}

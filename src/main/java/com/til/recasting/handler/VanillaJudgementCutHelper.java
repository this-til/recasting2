package com.til.recasting.handler;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.registry.RecastingEntities;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.RayTraceHelper;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class VanillaJudgementCutHelper {

    private static final double AIR_REACH = 5;
    private static final double ENTITY_REACH = 7;

    private VanillaJudgementCutHelper() {
    }

    public static void spawn(LivingEntity user, boolean critical) {
        if (user.level().isClientSide()) {
            return;
        }
        Vec3 pos = resolveTargetPos(user);
        JudgementCutEntity jc = create(user, pos, critical);
        user.level().addFreshEntity(jc);
        user.level().playSound(
                null, jc.getX(), jc.getY(), jc.getZ(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.5F,
                0.8F / (user.getRandom().nextFloat() * 0.4F + 0.8F)
        );
    }

    public static void spawnSuper(LivingEntity owner, @Nullable List<Entity> exclude) {
        if (owner.level().isClientSide()) {
            return;
        }
        Level level = owner.level();
        List<Entity> founds = TargetSelector.getTargettableEntitiesWithinAABB(
                level,
                owner,
                owner.getBoundingBox().inflate(48.0D),
                TargetSelector.getResolvedReach(owner) + 32D
        );
        if (exclude != null) {
            founds.removeAll(exclude);
        }
        for (Entity entity : founds) {
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 10));
            JudgementCutEntity jc = create(owner, entity.position(), false);
            level.addFreshEntity(jc);
        }
        level.playSound(owner, owner.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private static JudgementCutEntity create(LivingEntity user, Vec3 pos, boolean critical) {
        JudgementCutEntity jc = new JudgementCutEntity(RecastingEntities.JUDGEMENT_CUT.get(), user.level(), user);
        jc.setPos(pos.x, pos.y, pos.z);
        jc.setCritical(critical);
        ItemStack stack = user.getMainHandItem();
        stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> jc.setColor(state.getColorCode()));
        float range = stack.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                .map(PropertiesDefinitionExtension::attackDistance)
                .orElse(1.0f);
        jc.setSize(range);
        jc.setModifiedRatio(1.0f);
        return jc;
    }

    private static Vec3 resolveTargetPos(LivingEntity user) {
        Level worldIn = user.level();
        Vec3 eyePos = user.getEyePosition(1.0f);
        ItemStack stack = user.getMainHandItem();
        Optional<Vec3> resultPos = stack.getCapability(ItemSlashBlade.BLADESTATE)
                .filter(s -> s.getTargetEntity(worldIn) != null)
                .map(s -> Objects.requireNonNull(s.getTargetEntity(worldIn)).getEyePosition(1.0f));
        if (resultPos.isEmpty()) {
            Optional<HitResult> raytraceresult = RayTraceHelper.rayTrace(
                    worldIn, user, eyePos, user.getLookAngle(),
                    AIR_REACH, ENTITY_REACH,
                    entity -> !entity.isSpectator() && entity.isAlive() && entity.isPickable() && (entity != user)
            );
            resultPos = raytraceresult.map(rtr -> {
                HitResult.Type type = rtr.getType();
                if (type == HitResult.Type.ENTITY) {
                    Entity target = ((EntityHitResult) rtr).getEntity();
                    return target.position().add(0, target.getEyeHeight() / 2.0f, 0);
                }
                if (type == HitResult.Type.BLOCK) {
                    return rtr.getLocation();
                }
                return null;
            });
        }
        return resultPos.orElseGet(() -> eyePos.add(user.getLookAngle().scale(AIR_REACH)));
    }
}

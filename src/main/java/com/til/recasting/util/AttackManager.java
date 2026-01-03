package com.til.recasting.util;

import com.google.common.collect.Lists;
import com.til.recasting.event.DoSlashExtendEvent;
import com.til.recasting.mixin_api.IEntityModifiedRatio;
import com.til.recasting.registry.instance.AttackType;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.ArrowReflector;
import mods.flammpfeil.slashblade.ability.TNTExtinguisher;
import mods.flammpfeil.slashblade.capability.concentrationrank.ConcentrationRankCapabilityProvider;
import mods.flammpfeil.slashblade.entity.EntityAbstractSummonedSword;
import mods.flammpfeil.slashblade.entity.EntitySlashEffect;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.KnockBacks;
import mods.flammpfeil.slashblade.util.TargetSelector;
import mods.flammpfeil.slashblade.util.VectorHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;
import java.util.function.Consumer;

public class AttackManager {

    public static EntitySlashEffect doSlash(LivingEntity playerIn, float roll, int colorCode, Vec3 centerOffset, boolean mute, boolean critical, float modifiedRatio, float extraDamage, float attackRange, KnockBacks knockback) {

        if (playerIn.level().isClientSide()) {
            return null;
        }

        AttributeInstance attackAttribute = playerIn.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttribute == null) {
            return null;
        }

        double attack = attackAttribute.getValue();

        ItemStack blade = playerIn.getMainHandItem();
        if (!blade.getCapability(ItemSlashBlade.BLADESTATE).isPresent()) {
            return null;
        }

        DoSlashExtendEvent event = new DoSlashExtendEvent(blade,
                blade.getCapability(ItemSlashBlade.BLADESTATE).orElseThrow(NullPointerException::new),
                playerIn, roll, critical, modifiedRatio, extraDamage, knockback, attackRange);

        if (MinecraftForge.EVENT_BUS.post(event)) {
            return null;
        }
        Vec3 pos = playerIn.position().add(0.0D, (double) playerIn.getEyeHeight() * 0.75D, 0.0D)
                .add(playerIn.getLookAngle().scale(0.3f));

        pos = pos.add(VectorHelper.getVectorForRotation(-90.0F, playerIn.getViewYRot(0)).scale(centerOffset.y))
                .add(VectorHelper.getVectorForRotation(0, playerIn.getViewYRot(0) + 90).scale(centerOffset.z))
                .add(playerIn.getLookAngle().scale(centerOffset.z));

        EntitySlashEffect jc = new EntitySlashEffect(SlashBlade.RegistryEvents.SlashEffect, playerIn.level());
        jc.setPos(pos.x, pos.y, pos.z);
        jc.setOwner(event.getUser());
        jc.setRotationRoll(event.getRoll());
        jc.setYRot(playerIn.getYRot());
        jc.setXRot(0);

        jc.setColor(colorCode);

        jc.setMute(mute);
        jc.setIsCritical(event.isCritical());


        //noinspection ConstantValue
        if (jc instanceof IEntityModifiedRatio entityModifiedRatio) {
            entityModifiedRatio.setRecasting$modifiedRatio(event.getModifiedRatio());
        }
        jc.setDamage(event.getDamage());


        jc.setKnockBack(event.getKnockback());

        jc.setBaseSize(event.getAttackRange());

        playerIn.getCapability(ConcentrationRankCapabilityProvider.RANK_POINT)
                .ifPresent(rank -> jc.setRank(rank.getRankLevel(playerIn.level().getGameTime())));

        playerIn.level().addFreshEntity(jc);

        return jc;

    }

    public static List<Entity> areaAttack(LivingEntity playerIn, Consumer<LivingEntity> beforeHit, float modifiedRatio, float extraDamage, boolean forceHit, boolean resetHit, boolean mute, float attackRange, List<Entity> exclude, List<AttackType> attackTypeList) {
        List<Entity> founds = Lists.newArrayList();

        if (!playerIn.level().isClientSide()) {
            founds = TargetSelector.getTargettableEntitiesWithinAABB(
                    playerIn.level(),
                    playerIn,
                    TargetSelector.getResolvedAxisAligned(playerIn.getBoundingBox(), playerIn.getLookAngle(), 4 * attackRange)
            );

            if (exclude != null) {
                founds.removeAll(exclude);
            }

            for(Entity entity : founds) {
                if (entity instanceof LivingEntity living) {
                    beforeHit.accept(living);
                }
                AttackManager.doMeleeAttack(playerIn, entity, forceHit, resetHit, modifiedRatio, extraDamage, attackTypeList);
            }
        }

        if (!mute) {
            playerIn.level().playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(),
                    SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.5F,
                    0.4F / (playerIn.getRandom().nextFloat() * 0.4F + 0.8F));
        }

        return founds;
    }

    public static void doMeleeAttack(LivingEntity attacker, Entity target, boolean forceHit, boolean resetHit, float modifiedRatio, float extraDamage, List<AttackType> attackTypeList) {
        mods.flammpfeil.slashblade.util.AttackManager.doManagedAttack((t) ->
                attacker.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state) -> {
                    try {
                        state.setOnClick(true);
                        AttackHelper.attack(attacker, t, modifiedRatio, extraDamage, attackTypeList);
                    } finally {
                        state.setOnClick(false);
                    }
                }), target, forceHit, resetHit);

        ArrowReflector.doReflect(target, attacker);
        TNTExtinguisher.doExtinguishing(target, attacker);
    }

    public static void doManagedAttack(Consumer<Entity> attack, Entity target, boolean forceHit, boolean resetHit, List<AttackType> attackTypeList) {
        if (forceHit) {
            target.invulnerableTime = 0;
        }

        attack.accept(target);

        if (resetHit) {
            target.invulnerableTime = 0;
        }
    }

    public static void doAttackWith(DamageSource src, float amount, Entity target, boolean forceHit, boolean resetHit, List<AttackType> attackTypeList) {
        if (target instanceof EntityAbstractSummonedSword) {
            return;
        }

        doManagedAttack((t) -> t.hurt(src, amount), target, forceHit, resetHit, attackTypeList);
    }
}

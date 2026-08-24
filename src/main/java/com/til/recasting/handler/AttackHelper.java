package com.til.recasting.handler;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.entity.SlashEffectEntity;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.event.DoSlashExtendEvent;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingDataComponents;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.util.DamageStructure;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.util.KnockBacks;
import mods.flammpfeil.slashblade.util.VectorHelper;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 攻击助手：创建事件 → 发布事件 → 计算伤害 → 应用效果。
 */
public final class AttackHelper {

    private AttackHelper() {
    }

    public static void attack(
            LivingEntity attacker,
            Entity target,
            DamageStructure damageStructure,
            List<AttackType> attackTypeList
    ) {
        attack(attacker, target, damageStructure, attackTypeList, attacker.getMainHandItem());
    }

    /**
     * @param blade 用于读取拔刀剑状态的堆叠；可为背包中的刀（无需切到主手）。
     */
    public static void attack(
            LivingEntity attacker,
            Entity target,
            DamageStructure damageStructure,
            List<AttackType> attackTypeList,
            ItemStack blade
    ) {
        if (!target.isAttackable()) {
            return;
        }
        if (blade == null || blade.isEmpty()) {
            return;
        }

        Optional<ISlashBladeState> stateOpt = BladeStateAccess.of(blade);
        if (stateOpt.isEmpty()) {
            return;
        }
        ISlashBladeState state = stateOpt.get();

        AttackAmplifierEvent attackAmplifierEvent = new AttackAmplifierEvent(
                blade,
                state,
                attacker,
                target,
                damageStructure.modifiedRatio(),
                damageStructure.extraDamage(),
                attackTypeList,
                attackTypeList.stream()
                        .map(a -> a.createDamageSource(attacker, target))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );

        boolean isCritical = mods.flammpfeil.slashblade.util.AttackHelper.isCriticalHit(attacker, target);

        NeoForge.EVENT_BUS.post(attackAmplifierEvent);

        float knockback = mods.flammpfeil.slashblade.util.AttackHelper.calculateKnockback(attacker);
        mods.flammpfeil.slashblade.util.AttackHelper.FireAspectResult fireAspectResult =
                mods.flammpfeil.slashblade.util.AttackHelper.handleFireAspect(attacker, target);
        Vec3 originalMotion = target.getDeltaMovement();

        double ultimatelyModifiedRatio = attackAmplifierEvent.getUltimatelyModifiedRatio();
        double attackDamage = resolveAttackDamage(attacker, blade);

        if (attackDamage <= 0) {
            return;
        }

        double damage = attackDamage * ultimatelyModifiedRatio;
        damage += attackAmplifierEvent.getExtraDamage();

        if (damage <= 0) {
            return;
        }

        List<AttackAmplifierEvent.DamageSourceInfo> list = attackAmplifierEvent.getDamageSourceInfoList();
        boolean isAbsolute = attackTypeList.contains(RecastingAttackTypes.ABSOLUTE_ATTACK.get());
        boolean noKnockback = attackTypeList.contains(RecastingAttackTypes.NO_KNOCKBACK_ATTACK.get());
        if (list.isEmpty() && !isAbsolute) {
            return;
        }

        float healthBefore = target instanceof LivingEntity livingBefore
                ? livingBefore.getHealth()
                : 0f;

        double finalDamage = damage;
        Optional<Boolean> any = list.stream()
                .map(info -> {
                    target.invulnerableTime = 0;
                    DamageStructure structure = info.damageStructure();
                    boolean hurt = target.hurt(
                            info.damageSource(),
                            (float) (finalDamage * structure.modifiedRatio()) + structure.extraDamage()
                    );
                    if (hurt) {
                        spawnDamageParticlesIfNeeded(target, info);
                    }
                    return hurt;
                })
                .toList()
                .stream()
                .filter(b -> b)
                .findAny();

        boolean absoluteApplied = false;
        if (isAbsolute
                && target instanceof LivingEntity livingTarget
                && livingTarget.getHealth() >= healthBefore) {
            float applied = (float) damage;
            AbsoluteHealthChangeGuard.run(() -> {
                float next = Math.max(0f, livingTarget.getHealth() - applied);
                livingTarget.setHealth(next);
            });
            absoluteApplied = true;
        }

        if (any.isPresent() || absoluteApplied) {
            if (noKnockback) {
                target.setDeltaMovement(originalMotion);
            } else {
                mods.flammpfeil.slashblade.util.AttackHelper.applyKnockback(attacker, target, knockback);
                mods.flammpfeil.slashblade.util.AttackHelper.restoreTargetMotionIfNeeded(target, originalMotion);
            }
            mods.flammpfeil.slashblade.util.AttackHelper.playAttackEffects(attacker, target, isCritical);
            mods.flammpfeil.slashblade.util.AttackHelper.handleEnchantmentsAndDurability(attacker, target);
            mods.flammpfeil.slashblade.util.AttackHelper.handlePostAttackEffects(attacker, target, fireAspectResult);
        } else {
            mods.flammpfeil.slashblade.util.AttackHelper.handleFailedAttack(attacker, target, fireAspectResult);
        }
    }

    public static float sharpnessDamageBonus(int level) {
        if (level <= 0) {
            return 0.0F;
        }
        return Math.min(level * 0.2F, 1.0F);
    }

    public static float vanillaSharpnessDamageBonus(int level) {
        if (level <= 0) {
            return 0.0F;
        }
        return 1.0F + (float) Math.max(0, level - 1) * 0.5F;
    }

    private static double resolveAttackDamage(LivingEntity attacker, ItemStack blade) {
        ItemStack mainHand = attacker.getMainHandItem();
        if (mainHand == blade) {
            AttributeInstance attribute = attacker.getAttribute(Attributes.ATTACK_DAMAGE);
            return attribute == null ? 0.0 : attribute.getValue();
        }

        double base = attacker.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
        ItemAttributeModifiers modifiers = blade.getAttributeModifiers();
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (!entry.slot().test(EquipmentSlot.MAINHAND)) {
                continue;
            }
            if (!entry.attribute().is(Attributes.ATTACK_DAMAGE)) {
                continue;
            }
            AttributeModifier modifier = entry.modifier();
            switch (modifier.operation()) {
                case ADD_VALUE -> base += modifier.amount();
                case ADD_MULTIPLIED_BASE, ADD_MULTIPLIED_TOTAL -> base *= (1.0 + modifier.amount());
            }
        }
        return base;
    }

    private static void spawnDamageParticlesIfNeeded(Entity target, AttackAmplifierEvent.DamageSourceInfo info) {
        if (!info.damageSource().is(DamageTypes.FELL_OUT_OF_WORLD)) {
            return;
        }
        if (!(target.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 center = target.getBoundingBox().getCenter();
        serverLevel.playSound(
                null,
                center.x,
                center.y,
                center.z,
                SoundEvents.ENDER_DRAGON_HURT,
                SoundSource.PLAYERS,
                1.6F,
                1.15F
        );
        ParticleHelper.sendParticlesLongRange(serverLevel, ParticleTypes.PORTAL, center.x, center.y, center.z, 64, 0.3, 0.45, 0.3, 0.32);
        ParticleHelper.sendParticlesLongRange(serverLevel, ParticleTypes.DRAGON_BREATH, center.x, center.y, center.z, 48, 0.4, 0.55, 0.4, 0.22);
        ParticleHelper.sendParticlesLongRange(serverLevel, ParticleTypes.PORTAL, center.x, center.y, center.z, 72, 0.95, 1.15, 0.95, 0.08);
        ParticleHelper.sendParticlesLongRange(serverLevel, ParticleTypes.END_ROD, center.x, center.y, center.z, 18, 0.35, 0.5, 0.35, 0.16);
        ParticleHelper.sendParticlesLongRange(serverLevel, ParticleTypes.ENCHANTED_HIT, center.x, center.y, center.z, 28, 0.45, 0.6, 0.45, 0.28);
        ParticleHelper.sendParticlesLongRange(serverLevel, ParticleTypes.WITCH, center.x, center.y, center.z, 24, 0.5, 0.7, 0.5, 0.12);
    }

    /**
     * 生成本模组 {@link SlashEffectEntity}；SlashBlade {@code AttackManager.doSlash} 签名仍返回
     * {@link EntitySlashEffect}，由 Mixin 填哑元。
     */
    @Nullable
    public static SlashEffectEntity doSlash(
            LivingEntity playerIn,
            float roll,
            int colorCode,
            Vec3 centerOffset,
            boolean mute,
            boolean critical,
            DamageStructure damageStructure,
            float attackRange,
            @Nullable KnockBacks knockback
    ) {
        if (playerIn.level().isClientSide()) {
            return null;
        }

        ItemStack blade = playerIn.getMainHandItem();
        Optional<ISlashBladeState> stateOpt = BladeStateAccess.of(blade);
        if (stateOpt.isEmpty()) {
            return null;
        }

        DoSlashExtendEvent event = new DoSlashExtendEvent(
                blade,
                stateOpt.get(),
                playerIn,
                roll,
                critical,
                damageStructure.modifiedRatio(),
                damageStructure.extraDamage(),
                knockback,
                attackRange,
                centerOffset,
                mute
        );

        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return null;
        }

        Vec3 pos = playerIn.position()
                .add(0.0D, (double) playerIn.getEyeHeight() * 0.75D, 0.0D)
                .add(playerIn.getLookAngle().scale(0.3f));

        pos = pos.add(VectorHelper.getVectorForRotation(-90.0F, playerIn.getViewYRot(0)).scale(centerOffset.y))
                .add(VectorHelper.getVectorForRotation(0, playerIn.getViewYRot(0) + 90).scale(centerOffset.z))
                .add(playerIn.getLookAngle().scale(centerOffset.z));

        SlashEffectEntity jc = new SlashEffectEntity(RecastingEntities.SLASH_EFFECT.get(), playerIn.level(), playerIn);
        jc.setPos(pos.x, pos.y, pos.z);
        jc.setRoll(event.getRoll());
        jc.setYRot(playerIn.getYRot());
        jc.setXRot(0);
        jc.setColor(colorCode);
        jc.setMute(event.isMute());
        jc.setCritical(event.isCritical());
        jc.setModifiedRatio(event.getModifiedRatio());
        //noinspection deprecation
        jc.setDamage((float) event.getDamage());
        jc.setSize(event.getAttackRange());

        KnockBacks resolvedKnockback = event.getKnockback();
        if (resolvedKnockback != null) {
            jc.attackActionCallbackPoint.register(e -> resolvedKnockback.action.accept(e));
        }

        playerIn.level().addFreshEntity(jc);
        return jc;
    }

    public static List<LivingEntity> areaAttack(
            LivingEntity playerIn,
            Vec3 pos,
            DamageStructure damageStructure,
            float attackRange,
            List<AttackType> attackTypeList,
            @Nullable List<Entity> exclude,
            @Nullable Consumer<LivingEntity> beforeHit
    ) {
        if (playerIn.level().isClientSide()) {
            return List.of();
        }

        return EntityHelper.getTargettableLivingEntityWithinAABB(
                        playerIn.level(),
                        playerIn,
                        pos,
                        attackRange
                )
                .stream()
                .filter(e -> exclude == null || !exclude.contains(e))
                .peek(e -> {
                    if (beforeHit != null) {
                        beforeHit.accept(e);
                    }
                    doMeleeAttack(playerIn, e, damageStructure, attackTypeList);
                })
                .toList();
    }

    public static void doMeleeAttack(
            LivingEntity attacker,
            Entity target,
            DamageStructure damageStructure,
            List<AttackType> attackTypeList
    ) {
        target.invulnerableTime = 0;

        BladeStateAccess.of(attacker.getMainHandItem()).ifPresent(state -> {
            try {
                state.setOnClick(true);
                attack(attacker, target, damageStructure, attackTypeList);
            } finally {
                state.setOnClick(false);
            }
        });

        target.invulnerableTime = 0;

        // TODO(P3): 对齐 SlashBlade AttackManager.doMeleeAttack 后置逻辑（ArrowReflector / TNTExtinguisher）
    }

    public static List<LivingEntity> attackAlongSegment(
            LivingEntity attacker,
            Vec3 start,
            Vec3 end,
            float radius,
            DamageStructure damageStructure,
            List<AttackType> attackTypeList,
            int color
    ) {
        if (attacker.level().isClientSide()) {
            return List.of();
        }

        AABB box = new AABB(start, end).inflate(radius);
        List<LivingEntity> candidates = attacker.level().getEntitiesOfClass(
                LivingEntity.class,
                box,
                entity -> EntityPredicateHelper.canTarget(attacker, entity)
        );

        List<LivingEntity> hits = new ArrayList<>();
        for (LivingEntity target : candidates) {
            Vec3 center = target.getBoundingBox().getCenter();
            if (PosHelper.distancePointToSegment(center, start, end) > radius) {
                continue;
            }
            attack(attacker, target, damageStructure, attackTypeList);
            hits.add(target);
        }

        if (attacker.level() instanceof ServerLevel serverLevel) {
            spawnDustAlongSegment(serverLevel, start, end, color, Math.max(0.25f, radius * 0.5f));
        }
        return hits;
    }

    public static List<LivingEntity> attackAlongLook(
            LivingEntity attacker,
            float range,
            float radius,
            DamageStructure damageStructure,
            List<AttackType> attackTypeList,
            int color
    ) {
        Vec3 start = attacker.getEyePosition();
        Vec3 end = start.add(attacker.getLookAngle().scale(range));
        return attackAlongSegment(attacker, start, end, radius, damageStructure, attackTypeList, color);
    }

    public static void spawnDustAlongSegment(
            ServerLevel serverLevel,
            Vec3 start,
            Vec3 end,
            int color,
            float spacing
    ) {
        Vec3 delta = end.subtract(start);
        double length = delta.length();
        if (length <= 0.0) {
            return;
        }
        int steps = Math.max(1, (int) Math.ceil(length / spacing));
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        DustParticleOptions dust = new DustParticleOptions(new Vector3f(r, g, b), 1.0f);
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            Vec3 pos = start.lerp(end, t);
            ParticleHelper.sendParticlesLongRange(serverLevel, dust, pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    public static PropertiesDefinitionExtension propertiesOf(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return PropertiesDefinitionExtension.EMPTY;
        }
        return stack.getOrDefault(
                RecastingDataComponents.PROPERTIES_DEFINITION_EXTENSION.get(),
                PropertiesDefinitionExtension.EMPTY
        );
    }
}

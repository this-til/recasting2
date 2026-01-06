package com.til.recasting.mixin;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.util.DamageStructure;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.EntitySlashEffect;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;
import java.util.function.Consumer;


/**
 * Mixin 用于重写 SlashBlade 原版的 AttackManager 方法
 * 添加自定义攻击距离支持
 */
@Mixin(value = mods.flammpfeil.slashblade.util.AttackManager.class, remap = false)
public abstract class AttackManagerMixin {

    /**
     * 重写 doSlash 方法，添加攻击距离支持
     *
     * @author til
     * @reason 添加自定义攻击距离支持，调用项目自定义的 AttackManager
     */
    @Overwrite(remap = false)
    public static EntitySlashEffect doSlash(LivingEntity playerIn, float roll, int colorCode, Vec3 centerOffset,
                                            boolean mute, boolean critical, double comboRatio, KnockBacks knockback) {
        float attackDistance = playerIn.getMainHandItem()
                .getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                .map(PropertiesDefinitionExtension::attackDistance)
                .orElse(1.0f);

        AttackHelper.doSlash(
                playerIn, roll, colorCode, centerOffset,
                mute, critical, new DamageStructure((float) comboRatio, 0), attackDistance, knockback
        );

        // so,, 放弃使用 EntitySlashEffect但不能直接返回null，希望他是安全的
        return new EntitySlashEffect(SlashBlade.RegistryEvents.SlashEffect, playerIn.level());
    }

    /**
     * 重写 areaAttack 方法，添加攻击距离支持
     *
     * @author til
     * @reason 添加自定义攻击距离支持，调用项目自定义的 AttackManager
     */
    @Overwrite(remap = false)
    public static List<Entity> areaAttack(LivingEntity playerIn, Consumer<LivingEntity> beforeHit, float comboRatio,
                                          boolean forceHit, boolean resetHit, boolean mute, List<Entity> exclude) {
        float attackDistance = playerIn.getMainHandItem()
                .getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                .map(PropertiesDefinitionExtension::attackDistance)
                .orElse(1.0f);

        return AttackHelper.areaAttack(
                        playerIn, playerIn.getPosition(0), new DamageStructure(comboRatio, 0), attackDistance, List.of(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get()), exclude, beforeHit
                ).stream()
                .map(e -> (Entity) e)
                .toList();
    }

    /***
     *
     * @author til
     * @reason 添加 AttackType
     */
    @Overwrite(remap = false)
    public static void doMeleeAttack(LivingEntity attacker, Entity target, boolean forceHit, boolean resetHit, float comboRatio) {
        AttackHelper.doMeleeAttack(attacker, target, new DamageStructure(comboRatio, 0), List.of(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get()));
    }
}


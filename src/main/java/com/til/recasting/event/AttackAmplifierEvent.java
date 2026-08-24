package com.til.recasting.event;

import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.util.DamageStructure;
import lombok.Getter;
import lombok.Setter;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

import java.util.List;

@Getter
public class AttackAmplifierEvent extends Event {

    private final ItemStack item;
    private final ISlashBladeState slashBladeState;
    private final LivingEntity attacker;
    private final Entity target;

    @Setter
    private float extraDamage;

    /**
     * 当前的攻击倍率
     */
    @Setter
    private float modifiedRatio;

    /**
     * 伤害放大器
     */
    private float modifiedRatioAmplifier = 1;

    /**
     * 机制放大器（第二乘区）
     */
    private float mechanismModifiedRatioAmplifier = 1;

    private final List<AttackType> attackTypeList;

    private final List<DamageSourceInfo> damageSourceInfoList;

    public AttackAmplifierEvent(
            ItemStack item,
            ISlashBladeState slashBladeState,
            LivingEntity attacker,
            Entity target,
            float modifiedRatio,
            float extraDamage,
            List<AttackType> attackTypeList,
            List<DamageSourceInfo> damageSourceInfoList
    ) {
        this.item = item;
        this.slashBladeState = slashBladeState;
        this.attacker = attacker;
        this.target = target;
        this.modifiedRatio = modifiedRatio;
        this.extraDamage = extraDamage;
        this.attackTypeList = attackTypeList;
        this.damageSourceInfoList = damageSourceInfoList;
    }

    public float getUltimatelyModifiedRatio() {
        return modifiedRatio * modifiedRatioAmplifier * mechanismModifiedRatioAmplifier;
    }

    public void addExtraDamage(float extraDamage) {
        this.extraDamage += extraDamage;
    }

    public void addModifiedRatioAmplifier(float amplifier) {
        modifiedRatioAmplifier += amplifier;
    }

    public void addMechanismModifiedRatioAmplifier(float amplifier) {
        mechanismModifiedRatioAmplifier += amplifier;
    }

    public void addDamageSourceInfo(DamageSource damageSource, float modifiedRatio) {
        damageSourceInfoList.add(new DamageSourceInfo(damageSource, new DamageStructure(modifiedRatio, 0)));
    }

    public void addDamageSourceInfo(DamageSource damageSource, DamageStructure damageStructure) {
        damageSourceInfoList.add(new DamageSourceInfo(damageSource, damageStructure));
    }

    public record DamageSourceInfo(DamageSource damageSource, DamageStructure damageStructure) {
    }
}

package com.til.recasting.event;

import lombok.Getter;
import lombok.Setter;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class DoSlashExtendEvent extends SlashBladeEvent.DoSlashEvent {

    @Getter
    @Setter
    float attackRange;

    @Getter
    @Setter
    float modifiedRatio;

    @Getter
    Vec3 centerOffset;

    @Getter
    @Setter
    boolean mute;

    public DoSlashExtendEvent(ItemStack blade, ISlashBladeState state, LivingEntity user, float roll, boolean critical, float modifiedRatio, float damage, KnockBacks knockback, float attackRange, Vec3 centerOffset, boolean mute) {
        super(blade, state, user, roll, critical, damage, knockback);
        this.attackRange = attackRange;
        this.modifiedRatio = modifiedRatio;
        this.centerOffset = centerOffset;
        this.mute = mute;
    }

    public void addAttackRange(float attackRange) {
        this.attackRange += attackRange;
    }

    public void addModifiedRatio(float modifiedRatio) {
        this.modifiedRatio += modifiedRatio;
    }
}

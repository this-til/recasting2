package com.til.recasting.event;

import lombok.Getter;
import lombok.Setter;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Recasting 挥刀事件。独立于原版 {@code SlashBladeEvent}，
 * 避免接管原版挥刀时对父类型监听者二次投递。
 */
@Getter
public class DoSlashExtendEvent extends Event implements ICancellableEvent {

    private final ItemStack blade;
    private final ISlashBladeState slashBladeState;
    private final LivingEntity user;
    private final Vec3 centerOffset;

    @Setter
    private float roll;

    @Setter
    private boolean critical;

    @Setter
    private double damage;

    @Setter
    private KnockBacks knockback;

    @Setter
    private float attackRange;

    @Setter
    private float modifiedRatio;

    @Setter
    private boolean mute;

    public DoSlashExtendEvent(
            ItemStack blade,
            ISlashBladeState slashBladeState,
            LivingEntity user,
            float roll,
            boolean critical,
            float modifiedRatio,
            float damage,
            KnockBacks knockback,
            float attackRange,
            Vec3 centerOffset,
            boolean mute
    ) {
        this.blade = blade;
        this.slashBladeState = slashBladeState;
        this.user = user;
        this.roll = roll;
        this.critical = critical;
        this.modifiedRatio = modifiedRatio;
        this.damage = damage;
        this.knockback = knockback != null ? knockback : KnockBacks.cancel;
        this.attackRange = attackRange;
        this.centerOffset = centerOffset == null ? Vec3.ZERO : centerOffset;
        this.mute = mute;
    }

    public void addAttackRange(float attackRange) {
        this.attackRange += attackRange;
    }

    public void addModifiedRatio(float modifiedRatio) {
        this.modifiedRatio += modifiedRatio;
    }
}

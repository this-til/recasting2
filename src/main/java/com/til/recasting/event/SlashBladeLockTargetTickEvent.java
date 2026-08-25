package com.til.recasting.event;

import lombok.Getter;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

/**
 * 主手刀当前锁定到活体目标时，每 tick 派发一次的专用事件
 * （由 {@code ItemSlashBladeInventoryTickMixin} 在锁敌 tick 路径中 post）。
 */
@Getter
public class SlashBladeLockTargetTickEvent extends Event {

    private final LivingEntity user;
    private final ItemStack blade;
    private final ISlashBladeState slashBladeState;
    private final LivingEntity target;

    public SlashBladeLockTargetTickEvent(
            LivingEntity user,
            ItemStack blade,
            ISlashBladeState slashBladeState,
            LivingEntity target
    ) {
        this.user = user;
        this.blade = blade;
        this.slashBladeState = slashBladeState;
        this.target = target;
    }
}

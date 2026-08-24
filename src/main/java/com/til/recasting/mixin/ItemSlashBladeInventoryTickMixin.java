package com.til.recasting.mixin;

import com.til.recasting.event.SlashBladeLockTargetTickEvent;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemSlashBlade.class, remap = false)
public class ItemSlashBladeInventoryTickMixin {

    @Inject(method = "inventoryTick", at = @At("TAIL"))
    private void recasting$publishLockTargetTick(
            @NotNull ItemStack stack,
            @NotNull Level worldIn,
            @NotNull Entity entityIn,
            int itemSlot,
            boolean isSelected,
            CallbackInfo ci
    ) {
        if (worldIn.isClientSide()) {
            return;
        }
        if (!(entityIn instanceof LivingEntity living)) {
            return;
        }
        if (!ItemSlashBlade.isInMainhand(stack, isSelected, living)) {
            return;
        }

        BladeStateAccess.of(stack).ifPresent(state -> {
            Entity locked = state.getTargetEntity(worldIn);
            if (!(locked instanceof LivingEntity target) || !target.isAlive() || target.isRemoved()) {
                return;
            }

            NeoForge.EVENT_BUS.post(new SlashBladeLockTargetTickEvent(living, stack, state, target));
        });
    }
}

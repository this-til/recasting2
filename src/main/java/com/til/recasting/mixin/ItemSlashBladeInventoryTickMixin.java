package com.til.recasting.mixin;

import com.til.recasting.event.SlashBladeLockTargetTickEvent;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemSlashBlade.class)
public class ItemSlashBladeInventoryTickMixin {

    // inventoryTick 继承自原版 Item，运行时为 SRG 名，必须 remap
    @Inject(method = "inventoryTick", at = @At("TAIL"))
    private void recasting2$publishLockTargetTick(
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

        stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
            Entity locked = state.getTargetEntity(worldIn);
            if (!(locked instanceof LivingEntity target) || !target.isAlive() || target.isRemoved()) {
                return;
            }

            MinecraftForge.EVENT_BUS.post(new SlashBladeLockTargetTickEvent(living, stack, state, target));
        });
    }
}

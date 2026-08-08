package com.til.recasting.mixin;

import com.til.recasting.handler.EntityHelper;
import mods.flammpfeil.slashblade.ability.LockOnManager;
import mods.flammpfeil.slashblade.event.handler.InputCommandEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.InputCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 接管潜行锁敌：默认视锥索敌，否则自身中心 ±32 最近目标。
 */
@Mixin(value = LockOnManager.class)
public abstract class LockOnManagerMixin {

    @Inject(method = "onInputChange", at = @At("HEAD"), cancellable = true, remap = false)
    private void recasting$replaceLockOnSearch(InputCommandEvent event, CallbackInfo ci) {
        ci.cancel();

        ServerPlayer player = event.getEntity();
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSlashBlade)) {
            return;
        }

        if (event.getOld().contains(InputCommand.SNEAK) == event.getCurrent().contains(InputCommand.SNEAK)) {
            return;
        }

        Entity targetEntity;
        if (event.getOld().contains(InputCommand.SNEAK) && !event.getCurrent().contains(InputCommand.SNEAK)) {
            targetEntity = null;
        } else {
            targetEntity = EntityHelper.selectClosestInViewCone(player)
                    .map(Entity.class::cast)
                    .or(() -> EntityHelper.selectClosestWithinRange(player, 32.0))
                    .orElse(null);
        }

        stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> state.setTargetEntityId(targetEntity));
    }
}

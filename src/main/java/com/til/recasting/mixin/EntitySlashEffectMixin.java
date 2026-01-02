package com.til.recasting.mixin;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.handler.CapabilityRegistryHandler;
import mods.flammpfeil.slashblade.entity.EntitySlashEffect;
import mods.flammpfeil.slashblade.entity.Projectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin 用于在 EntitySlashEffect 设置 owner 后，从能力系统获取 attackDistance 并设置 setBaseSize
 */
@Mixin(Projectile.class)
public abstract class EntitySlashEffectMixin {

    /**
     * 在 setOwner 方法执行后，如果是 EntitySlashEffect 实例，从 owner 的 ItemStack 获取 PropertiesDefinitionExtension 的 attackDistance
     * 并调用 setBaseSize 设置该值
     */
    @Inject(method = "setOwner(Lnet/minecraft/world/entity/Entity;)V",
            at = @At("RETURN"), remap = false)
    private void recasting$setBaseSizeFromAttackDistance(Entity owner, CallbackInfo ci) {
        //noinspection ConstantValue
        if (!((Object) this instanceof EntitySlashEffect slashEffect)) {
            return;
        }

        if (!(owner instanceof LivingEntity living)) {
            return;
        }

        if (owner.level().isClientSide()) {
            return;
        }

        living.getMainHandItem()
                .getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                .ifPresent(extension -> {
                    float attackDistance = extension.attackDistance();
                    if (attackDistance > 0) {
                        slashEffect.setBaseSize(attackDistance);
                    }
                });
    }
}


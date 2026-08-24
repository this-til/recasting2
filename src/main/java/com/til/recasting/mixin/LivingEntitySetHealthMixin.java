package com.til.recasting.mixin;

import com.til.recasting.handler.AbsoluteHealthChangeGuard;
import com.til.recasting.handler.EmperorLineSeHelper;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * 人皇领域：耀魂充足时拦截直接 setHealth(≤0) 的秒杀路径，改为保留 1 血。
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntitySetHealthMixin {

    @ModifyVariable(method = "setHealth", at = @At("HEAD"), argsOnly = true)
    private float recasting$clampLethalSetHealth(float health) {
        if (health > 0f) {
            return health;
        }
        if (AbsoluteHealthChangeGuard.isGuarded()) {
            return health;
        }
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide()) {
            return health;
        }
        EmperorLineSeHelper.ActiveLine active = EmperorLineSeHelper.resolveHighestEmperor(self);
        if (active == null || active.state().getProudSoulCount() <= 0) {
            return health;
        }
        return 1f;
    }
}

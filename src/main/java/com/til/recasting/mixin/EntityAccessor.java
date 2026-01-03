package com.til.recasting.mixin;

import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.entity.Entity.class)
public interface EntityAccessor {
    @Accessor
    SynchedEntityData getEntityData();
}

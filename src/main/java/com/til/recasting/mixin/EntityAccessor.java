package com.til.recasting.mixin;

import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.world.entity.Entity.class)
public interface EntityAccessor {

    @Accessor
    SynchedEntityData getEntityData();
}

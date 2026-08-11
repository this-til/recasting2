package com.til.recasting.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;

@Mixin(value = CapabilityProvider.class, remap = false)
public interface CapabilityProviderAccessor {

    @Invoker("serializeCaps")
    @Nullable
    CompoundTag recasting$serializeCaps();

    @Invoker("deserializeCaps")
    void recasting$deserializeCaps(CompoundTag tag);
}

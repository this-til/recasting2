package com.til.recasting.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.damagesource.DamageSources.class)
public interface DamageSourcesAccessor {
    @Invoker
    DamageSource callSource(ResourceKey<DamageType> p_270076_, @Nullable Entity p_270656_, @Nullable Entity p_270242_);
}

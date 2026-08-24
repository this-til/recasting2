package com.til.recasting.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(net.minecraft.world.damagesource.DamageSources.class)
public interface DamageSourcesAccessor {

    @Invoker
    DamageSource callSource(
            ResourceKey<DamageType> type,
            @Nullable Entity direct,
            @Nullable Entity causing
    );
}

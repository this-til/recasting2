package com.til.recasting.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Function;

@org.spongepowered.asm.mixin.Mixin(value = mods.flammpfeil.slashblade.slasharts.SlashArts.class, remap = false)
public interface SlashArtsAccessor {

    @Accessor
    void setComboStateJust(Function<LivingEntity, ResourceLocation> comboStateJust);

    @Mutable
    @Accessor
    void setComboState(Function<LivingEntity, ResourceLocation> comboState);
}

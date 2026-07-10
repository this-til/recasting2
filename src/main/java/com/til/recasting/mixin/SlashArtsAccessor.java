package com.til.recasting.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Function;

@Mixin(value = mods.flammpfeil.slashblade.slasharts.SlashArts.class, remap = false)
public interface SlashArtsAccessor {

    @Accessor(remap = false)
    void setComboStateJust(Function<LivingEntity, ResourceLocation> comboStateJust);

    @Mutable
    @Accessor(remap = false)
    void setComboState(Function<LivingEntity, ResourceLocation> comboState);
}

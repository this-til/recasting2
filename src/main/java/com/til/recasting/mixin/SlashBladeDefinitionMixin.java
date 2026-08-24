package com.til.recasting.mixin;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.mixin_api.ISlashBladeStateExtension;
import lombok.Getter;
import lombok.Setter;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * 为 SlashBladeDefinition 添加扩展字段。
 */
@Mixin(SlashBladeDefinition.class)
public abstract class SlashBladeDefinitionMixin implements ISlashBladeStateExtension {

    @Unique
    @Getter
    @Setter
    private RenderDefinitionExtension recasting$renderDefinitionExtension = new RenderDefinitionExtension();

    @Unique
    @Getter
    @Setter
    private PropertiesDefinitionExtension recasting$propertiesDefinitionExtension = new PropertiesDefinitionExtension();
}

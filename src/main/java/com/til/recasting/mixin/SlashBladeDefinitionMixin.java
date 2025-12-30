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
 * Mixin 用于为 SlashBladeDefinition 添加扩展字段
 * 通过包含 PropertiesDefinitionExtension 和 RenderDefinitionExtension 的实例来实现
 */
@Mixin(SlashBladeDefinition.class)
public abstract class SlashBladeDefinitionMixin implements ISlashBladeStateExtension {

    @Unique
    @Getter
    @Setter
    private RenderDefinitionExtension renderDefinitionExtension = new RenderDefinitionExtension();

    @Unique
    @Getter
    @Setter
    private PropertiesDefinitionExtension propertiesDefinitionExtension = new PropertiesDefinitionExtension();

}


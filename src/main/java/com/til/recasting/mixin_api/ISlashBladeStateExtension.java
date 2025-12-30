package com.til.recasting.mixin_api;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraft.nbt.CompoundTag;

/**
 * 扩展接口，用于访问 SlashBladeState 的扩展字段
 */
public interface ISlashBladeStateExtension {

    RenderDefinitionExtension getRenderDefinitionExtension();

    PropertiesDefinitionExtension getPropertiesDefinitionExtension();

    void setRenderDefinitionExtension(RenderDefinitionExtension renderDefinitionExtension);

    void setPropertiesDefinitionExtension(PropertiesDefinitionExtension propertiesDefinitionExtension);
}


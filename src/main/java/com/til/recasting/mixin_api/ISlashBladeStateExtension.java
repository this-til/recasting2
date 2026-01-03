package com.til.recasting.mixin_api;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;

/**
 * 扩展接口，用于访问 SlashBladeState 的扩展字段
 */
public interface ISlashBladeStateExtension {

    RenderDefinitionExtension getRecasting$renderDefinitionExtension();

    PropertiesDefinitionExtension getRecasting$propertiesDefinitionExtension();

    void setRecasting$renderDefinitionExtension(RenderDefinitionExtension renderDefinitionExtension);

    void setRecasting$propertiesDefinitionExtension(PropertiesDefinitionExtension propertiesDefinitionExtension);
}


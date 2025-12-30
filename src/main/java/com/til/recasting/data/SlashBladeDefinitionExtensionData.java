package com.til.recasting.data;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;

/**
 * 扩展数据记录类，用于序列化和反序列化 SlashBladeDefinition 的扩展字段
 */
public record SlashBladeDefinitionExtensionData(
        RenderDefinitionExtension renderDefinitionExtension,
        PropertiesDefinitionExtension propertiesDefinitionExtension
) {
}


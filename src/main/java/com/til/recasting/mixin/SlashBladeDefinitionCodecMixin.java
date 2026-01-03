package com.til.recasting.mixin;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.data.SlashBladeDefinitionExtensionData;
import com.til.recasting.mixin_api.ISlashBladeStateExtension;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin 用于修改 SlashBladeDefinition 的 CODEC，添加扩展字段的序列化支持
 * 扩展字段包括：
 * - attackDistance (来自 PropertiesDefinitionExtension)
 * - summondSwordModel, summondSwordTexture (来自 RenderDefinitionExtension)
 * - slashEffectModel, slashEffectTexture (来自 RenderDefinitionExtension)
 * - judgementCutModel, judgementCutTexture (来自 RenderDefinitionExtension)
 */
@Mixin(SlashBladeDefinition.class)
public abstract class SlashBladeDefinitionCodecMixin {

    @Shadow(remap = false)
    @Final
    @Mutable
    public static Codec<SlashBladeDefinition> CODEC;

    /**
     * 在静态初始化后修改 CODEC，添加扩展字段
     * 使用 flatXmap 包装原始 CODEC，在解码时读取扩展字段，在编码时写入扩展字段
     */
    @Inject(method = "<clinit>", at = @At("TAIL"), remap = false)
    private static void recasting$modifyCodec(CallbackInfo ci) {
        Codec<SlashBladeDefinition> oldCodec = CODEC;

        // 创建扩展数据的 Codec
        Codec<SlashBladeDefinitionExtensionData> extensionCodec = RecordCodecBuilder.create(instance ->
                instance.group(
                        RenderDefinitionExtension.CODEC.optionalFieldOf("render_extension", new RenderDefinitionExtension())
                                .forGetter(SlashBladeDefinitionExtensionData::renderDefinitionExtension),
                        PropertiesDefinitionExtension.CODEC.optionalFieldOf("properties_extension", new PropertiesDefinitionExtension())
                                .forGetter(SlashBladeDefinitionExtensionData::propertiesDefinitionExtension)
                ).apply(instance, SlashBladeDefinitionExtensionData::new)
        );

        CODEC = Codec.pair(oldCodec, extensionCodec).flatXmap(
                definition -> {
                    SlashBladeDefinition first = definition.getFirst();
                    SlashBladeDefinitionExtensionData second = definition.getSecond();

                    if (first instanceof ISlashBladeStateExtension extension) {
                        extension.setRecasting$propertiesDefinitionExtension(second.propertiesDefinitionExtension());
                        extension.setRecasting$renderDefinitionExtension(second.renderDefinitionExtension());
                    } else {
                        throw new IllegalStateException("SlashBladeDefinition must be a SlashBladeStateExtension");
                    }
                    return DataResult.success(first);
                },
                definition -> {
                    SlashBladeDefinitionExtensionData extensionData = null;
                    if (definition instanceof ISlashBladeStateExtension extension) {
                        extensionData = new SlashBladeDefinitionExtensionData(extension.getRecasting$renderDefinitionExtension(), extension.getRecasting$propertiesDefinitionExtension());
                    } else {
                        throw new IllegalStateException("SlashBladeDefinition must be a SlashBladeStateExtension");
                    }
                    return DataResult.success(new Pair<>(definition, extensionData));
                }
        );

    }
}


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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 扩展 SlashBladeDefinition CODEC，序列化 render/properties 扩展字段。
 */
@Mixin(SlashBladeDefinition.class)
public abstract class SlashBladeDefinitionCodecMixin {

    @Shadow(remap = false)
    @Final
    @Mutable
    public static Codec<SlashBladeDefinition> CODEC;

    @Inject(method = "<clinit>", at = @At("TAIL"), remap = false)
    private static void recasting$modifyCodec(CallbackInfo ci) {
        Codec<SlashBladeDefinition> oldCodec = CODEC;

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
                    if (definition instanceof ISlashBladeStateExtension extension) {
                        SlashBladeDefinitionExtensionData extensionData = new SlashBladeDefinitionExtensionData(
                                extension.getRecasting$renderDefinitionExtension(),
                                extension.getRecasting$propertiesDefinitionExtension()
                        );
                        return DataResult.success(new Pair<>(definition, extensionData));
                    }
                    throw new IllegalStateException("SlashBladeDefinition must be a SlashBladeStateExtension");
                }
        );
    }
}

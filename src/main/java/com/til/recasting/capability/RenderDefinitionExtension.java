package com.til.recasting.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

@Data
@Accessors(fluent = true)
public class RenderDefinitionExtension implements INBTSerializable<CompoundTag> {
    public static final Codec<RenderDefinitionExtension> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.optionalFieldOf("summond_sword_model", null)
                            .forGetter(RenderDefinitionExtension::summondSwordModel),
                    ResourceLocation.CODEC.optionalFieldOf("summond_sword_texture", null)
                            .forGetter(RenderDefinitionExtension::summondSwordTexture),
                    ResourceLocation.CODEC.optionalFieldOf("slash_effect_model", null)
                            .forGetter(RenderDefinitionExtension::slashEffectModel),
                    ResourceLocation.CODEC.optionalFieldOf("slash_effect_texture", null)
                            .forGetter(RenderDefinitionExtension::slashEffectTexture),
                    ResourceLocation.CODEC.optionalFieldOf("judgement_cut_model", null)
                            .forGetter(RenderDefinitionExtension::judgementCutModel),
                    ResourceLocation.CODEC.optionalFieldOf("judgement_cut_texture", null)
                            .forGetter(RenderDefinitionExtension::judgementCutTexture))
            .apply(instance, RenderDefinitionExtension::new));

    @Nullable
    ResourceLocation summondSwordModel;
    @Nullable
    ResourceLocation summondSwordTexture;
    @Nullable
    ResourceLocation slashEffectModel;
    @Nullable
    ResourceLocation slashEffectTexture;
    @Nullable
    ResourceLocation judgementCutModel;
    @Nullable
    ResourceLocation judgementCutTexture;

    public RenderDefinitionExtension() {
    }

    public RenderDefinitionExtension(@Nullable ResourceLocation summondSwordModel,
                                     @Nullable ResourceLocation summondSwordTexture,
                                     @Nullable ResourceLocation slashEffectModel,
                                     @Nullable ResourceLocation slashEffectTexture,
                                     @Nullable ResourceLocation judgementCutModel,
                                     @Nullable ResourceLocation judgementCutTexture) {
        this.summondSwordModel = summondSwordModel;
        this.summondSwordTexture = summondSwordTexture;
        this.slashEffectModel = slashEffectModel;
        this.slashEffectTexture = slashEffectTexture;
        this.judgementCutModel = judgementCutModel;
        this.judgementCutTexture = judgementCutTexture;
    }

    @Override
    public CompoundTag serializeNBT() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this)
                .result()
                .filter(CompoundTag.class::isInstance)
                .map(CompoundTag.class::cast)
                .orElse(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag == null) {
            return;
        }
        CODEC.decode(NbtOps.INSTANCE, tag)
                .result()
                .ifPresent(pair -> {
                    RenderDefinitionExtension decoded = pair.getFirst();
                    this.summondSwordModel = decoded.summondSwordModel;
                    this.summondSwordTexture = decoded.summondSwordTexture;
                    this.slashEffectModel = decoded.slashEffectModel;
                    this.slashEffectTexture = decoded.slashEffectTexture;
                    this.judgementCutModel = decoded.judgementCutModel;
                    this.judgementCutTexture = decoded.judgementCutTexture;
                });
    }
}

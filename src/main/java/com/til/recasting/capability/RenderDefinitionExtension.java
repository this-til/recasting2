package com.til.recasting.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 刀渲染定义扩展（召唤剑/斩击/次元斩模型贴图覆盖）。
 */
@lombok.Data
@lombok.experimental.Accessors(fluent = true)
public class RenderDefinitionExtension {
    public static final Codec<RenderDefinitionExtension> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.optionalFieldOf("summond_sword_model")
                            .forGetter(ext -> Optional.ofNullable(ext.summondSwordModel)),
                    ResourceLocation.CODEC.optionalFieldOf("summond_sword_texture")
                            .forGetter(ext -> Optional.ofNullable(ext.summondSwordTexture)),
                    ResourceLocation.CODEC.optionalFieldOf("slash_effect_model")
                            .forGetter(ext -> Optional.ofNullable(ext.slashEffectModel)),
                    ResourceLocation.CODEC.optionalFieldOf("slash_effect_texture")
                            .forGetter(ext -> Optional.ofNullable(ext.slashEffectTexture)),
                    ResourceLocation.CODEC.optionalFieldOf("judgement_cut_model")
                            .forGetter(ext -> Optional.ofNullable(ext.judgementCutModel)),
                    ResourceLocation.CODEC.optionalFieldOf("judgement_cut_texture")
                            .forGetter(ext -> Optional.ofNullable(ext.judgementCutTexture)))
            .apply(instance, (m1, t1, m2, t2, m3, t3) -> new RenderDefinitionExtension(
                    m1.orElse(null), t1.orElse(null),
                    m2.orElse(null), t2.orElse(null),
                    m3.orElse(null), t3.orElse(null))));

    public static final StreamCodec<RegistryFriendlyByteBuf, RenderDefinitionExtension> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public static final RenderDefinitionExtension EMPTY = new RenderDefinitionExtension();

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

    public RenderDefinitionExtension(
            @Nullable ResourceLocation summondSwordModel,
            @Nullable ResourceLocation summondSwordTexture,
            @Nullable ResourceLocation slashEffectModel,
            @Nullable ResourceLocation slashEffectTexture,
            @Nullable ResourceLocation judgementCutModel,
            @Nullable ResourceLocation judgementCutTexture
    ) {
        this.summondSwordModel = summondSwordModel;
        this.summondSwordTexture = summondSwordTexture;
        this.slashEffectModel = slashEffectModel;
        this.slashEffectTexture = slashEffectTexture;
        this.judgementCutModel = judgementCutModel;
        this.judgementCutTexture = judgementCutTexture;
    }

    public CompoundTag save() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this)
                .result()
                .filter(CompoundTag.class::isInstance)
                .map(CompoundTag.class::cast)
                .orElseGet(CompoundTag::new);
    }

    public void load(@Nullable CompoundTag tag) {
        if (tag == null) {
            return;
        }
        CODEC.parse(NbtOps.INSTANCE, tag)
                .result()
                .ifPresent(decoded -> {
                    this.summondSwordModel = decoded.summondSwordModel;
                    this.summondSwordTexture = decoded.summondSwordTexture;
                    this.slashEffectModel = decoded.slashEffectModel;
                    this.slashEffectTexture = decoded.slashEffectTexture;
                    this.judgementCutModel = decoded.judgementCutModel;
                    this.judgementCutTexture = decoded.judgementCutTexture;
                });
    }
}

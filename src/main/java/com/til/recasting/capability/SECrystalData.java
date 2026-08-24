package com.til.recasting.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * SE Crystal 数据实现类（Item DataComponent）。
 */
public class SECrystalData implements ISpecialEffectCrystalData {

    public static final Codec<SECrystalData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.optionalFieldOf("SpecialEffectType")
                            .forGetter(data -> Optional.ofNullable(data.specialEffectType)),
                    Codec.INT.optionalFieldOf("SpecialEffectTypeLevel", 0)
                            .forGetter(SECrystalData::getSpecialEffectLevel))
            .apply(instance, (type, level) -> {
                SECrystalData data = new SECrystalData();
                data.specialEffectType = type.orElse(null);
                data.specialEffectLevel = Math.max(0, level);
                return data;
            }));

    public static final StreamCodec<RegistryFriendlyByteBuf, SECrystalData> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public static final SECrystalData EMPTY = new SECrystalData();

    @Nullable
    private ResourceLocation specialEffectType;
    private int specialEffectLevel = 0;

    @Override
    @Nullable
    public ResourceLocation getSpecialEffectType() {
        return specialEffectType;
    }

    @Override
    public void setSpecialEffectType(@Nullable ResourceLocation specialEffectType) {
        this.specialEffectType = specialEffectType;
    }

    @Override
    public int getSpecialEffectLevel() {
        return specialEffectLevel;
    }

    @Override
    public void setSpecialEffectLevel(int level) {
        this.specialEffectLevel = Math.max(0, level);
    }
}

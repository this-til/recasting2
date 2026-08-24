package com.til.recasting.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * 拔刀剑 FE 储能 DataComponent。
 */
public record FeBladeEnergyData(long energy) {

    public static final FeBladeEnergyData EMPTY = new FeBladeEnergyData(0L);

    public static final Codec<FeBladeEnergyData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.LONG.optionalFieldOf("Energy", 0L).forGetter(FeBladeEnergyData::energy))
            .apply(instance, FeBladeEnergyData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FeBladeEnergyData> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public FeBladeEnergyData {
        energy = Math.max(0L, energy);
    }

    public FeBladeEnergyData withEnergy(long energy) {
        return new FeBladeEnergyData(energy);
    }
}

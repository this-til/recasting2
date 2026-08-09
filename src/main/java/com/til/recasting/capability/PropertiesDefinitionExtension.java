package com.til.recasting.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(fluent = true)
public class PropertiesDefinitionExtension implements INBTSerializable<CompoundTag> {
    public static final Codec<PropertiesDefinitionExtension> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.FLOAT.optionalFieldOf("attack_distance", 1.0f)
                            .forGetter(PropertiesDefinitionExtension::attackDistance),
                    Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
                            .optionalFieldOf("extended_special_levels", new HashMap<>())
                            .forGetter(PropertiesDefinitionExtension::extendedSpecialLevels),
                    Codec.BOOL.optionalFieldOf("tracking_phantom_blade", false)
                            .forGetter(PropertiesDefinitionExtension::trackingPhantomBlade))
            .apply(instance, PropertiesDefinitionExtension::new));

    float attackDistance = 1.0f;
    Map<ResourceLocation, Integer> extendedSpecialLevels = new HashMap<>();
    /**
     * 为 true 时基础射击发射追踪幻影飞刃，否则为普通幻影剑
     */
    boolean trackingPhantomBlade = false;

    public PropertiesDefinitionExtension() {
    }

    public PropertiesDefinitionExtension(float attackDistance) {
        this.attackDistance = attackDistance;
    }

    public PropertiesDefinitionExtension(float attackDistance, Map<ResourceLocation, Integer> extendedSpecialLevels) {
        this(attackDistance, extendedSpecialLevels, false);
    }

    public PropertiesDefinitionExtension(
            float attackDistance,
            Map<ResourceLocation, Integer> extendedSpecialLevels,
            boolean trackingPhantomBlade
    ) {
        this.attackDistance = attackDistance;
        this.extendedSpecialLevels = extendedSpecialLevels != null
                ? new HashMap<>(extendedSpecialLevels)
                : new HashMap<>();
        this.trackingPhantomBlade = trackingPhantomBlade;
    }

    public int getExtendedSpecialLevels(ResourceLocation resourceLocation) {
        return this.extendedSpecialLevels.getOrDefault(resourceLocation, 0);
    }

    public void setExtendedSpecialLevels(ResourceLocation resourceLocation, int value) {
        this.extendedSpecialLevels.put(resourceLocation, value);
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
                    PropertiesDefinitionExtension decoded = pair.getFirst();
                    this.attackDistance = decoded.attackDistance;
                    this.extendedSpecialLevels = decoded.extendedSpecialLevels != null
                            ? new HashMap<>(decoded.extendedSpecialLevels)
                            : new HashMap<>();
                    this.trackingPhantomBlade = decoded.trackingPhantomBlade;
                });
    }
}

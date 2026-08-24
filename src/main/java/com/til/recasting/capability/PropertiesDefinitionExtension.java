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

import java.util.HashMap;
import java.util.Map;

/**
 * 刀属性定义扩展（攻击距离、SE 等级、FE 容量等）。
 */
@lombok.Data
@lombok.experimental.Accessors(fluent = true)
public class PropertiesDefinitionExtension {
    public static final Codec<PropertiesDefinitionExtension> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.FLOAT.optionalFieldOf("attack_distance", 1.0f)
                            .forGetter(PropertiesDefinitionExtension::attackDistance),
                    Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
                            .optionalFieldOf("extended_special_levels", Map.of())
                            .forGetter(ext -> Map.copyOf(ext.extendedSpecialLevels)),
                    Codec.BOOL.optionalFieldOf("tracking_phantom_blade", false)
                            .forGetter(PropertiesDefinitionExtension::trackingPhantomBlade),
                    Codec.LONG.optionalFieldOf("fe_capacity", 0L)
                            .forGetter(PropertiesDefinitionExtension::feCapacity))
            .apply(instance, PropertiesDefinitionExtension::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PropertiesDefinitionExtension> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public static final PropertiesDefinitionExtension EMPTY = new PropertiesDefinitionExtension();

    float attackDistance = 1.0f;
    Map<ResourceLocation, Integer> extendedSpecialLevels = new HashMap<>();
    /**
     * 为 true 时基础射击发射追踪幻影飞刃，否则为普通幻影剑
     */
    boolean trackingPhantomBlade = false;
    /**
     * FE 能量缓存上限；0 表示不具备 FE 能力
     */
    long feCapacity = 0L;

    public PropertiesDefinitionExtension() {
    }

    public PropertiesDefinitionExtension(float attackDistance) {
        this.attackDistance = attackDistance;
    }

    public PropertiesDefinitionExtension(float attackDistance, Map<ResourceLocation, Integer> extendedSpecialLevels) {
        this(attackDistance, extendedSpecialLevels, false, 0L);
    }

    public PropertiesDefinitionExtension(
            float attackDistance,
            Map<ResourceLocation, Integer> extendedSpecialLevels,
            boolean trackingPhantomBlade
    ) {
        this(attackDistance, extendedSpecialLevels, trackingPhantomBlade, 0L);
    }

    public PropertiesDefinitionExtension(
            float attackDistance,
            Map<ResourceLocation, Integer> extendedSpecialLevels,
            boolean trackingPhantomBlade,
            long feCapacity
    ) {
        this.attackDistance = attackDistance;
        this.extendedSpecialLevels = extendedSpecialLevels != null
                ? new HashMap<>(extendedSpecialLevels)
                : new HashMap<>();
        this.trackingPhantomBlade = trackingPhantomBlade;
        this.feCapacity = Math.max(0L, feCapacity);
    }

    public int getExtendedSpecialLevels(ResourceLocation resourceLocation) {
        return this.extendedSpecialLevels.getOrDefault(resourceLocation, 0);
    }

    public void setExtendedSpecialLevels(ResourceLocation resourceLocation, int value) {
        this.extendedSpecialLevels.put(resourceLocation, value);
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
                    this.attackDistance = decoded.attackDistance;
                    this.extendedSpecialLevels = decoded.extendedSpecialLevels != null
                            ? new HashMap<>(decoded.extendedSpecialLevels)
                            : new HashMap<>();
                    this.trackingPhantomBlade = decoded.trackingPhantomBlade;
                    this.feCapacity = decoded.feCapacity;
                });
    }
}

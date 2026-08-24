package com.til.recasting.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.til.recasting.capability.SECrystalData;
import com.til.recasting.registry.RecastingDataComponents;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * SE结晶的需求定义。
 */
@Slf4j
public record SpecialEffectCrystalRequest(
        @Nullable ResourceLocation specialEffectType,
        int level
) {

    public static final Codec<SpecialEffectCrystalRequest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.optionalFieldOf("special_effect_type")
                            .forGetter(req -> java.util.Optional.ofNullable(req.specialEffectType())),
                    Codec.INT.optionalFieldOf("level", -1).forGetter(SpecialEffectCrystalRequest::level))
            .apply(instance, (type, level) ->
                    new SpecialEffectCrystalRequest(type.orElse(null), level)));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpecialEffectCrystalRequest> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public static SpecialEffectCrystalRequest fromJSON(com.google.gson.JsonObject json) {
        return CODEC.parse(JsonOps.INSTANCE, json)
                .resultOrPartial(msg -> log.error("Failed to parse SECrystalRequest: {}", msg))
                .orElseGet(Builder.newInstance()::build);
    }

    public com.google.gson.JsonElement toJson() {
        return CODEC.encodeStart(JsonOps.INSTANCE, this)
                .resultOrPartial(msg -> log.error("Failed to encode SECrystalRequest: {}", msg))
                .orElseThrow();
    }

    public void initItemStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        SECrystalData data = stack.getOrDefault(RecastingDataComponents.SE_CRYSTAL_DATA.get(), new SECrystalData());
        if (this.specialEffectType != null) {
            data.setSpecialEffectType(this.specialEffectType);
        }
        if (this.level >= 0) {
            data.setSpecialEffectLevel(this.level);
        }
        stack.set(RecastingDataComponents.SE_CRYSTAL_DATA.get(), data);
    }

    public boolean test(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        if (!stack.has(RecastingDataComponents.SE_CRYSTAL_DATA.get())) {
            return false;
        }

        SECrystalData data = stack.get(RecastingDataComponents.SE_CRYSTAL_DATA.get());

        if (this.specialEffectType != null) {
            ResourceLocation itemType = data.getSpecialEffectType();
            if (itemType == null || !itemType.equals(this.specialEffectType)) {
                return false;
            }
        }

        if (this.level >= 0) {
            int itemLevel = data.getSpecialEffectLevel();
            return itemLevel == this.level;
        }

        return true;
    }

    public static class Builder {
        private ResourceLocation specialEffectType;
        private int level;

        private Builder() {
            this.specialEffectType = null;
            this.level = -1;
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder specialEffectType(ResourceLocation specialEffectType) {
            this.specialEffectType = specialEffectType;
            return this;
        }

        public Builder level(int level) {
            this.level = level;
            return this;
        }

        public SpecialEffectCrystalRequest build() {
            return new SpecialEffectCrystalRequest(specialEffectType, level);
        }
    }
}

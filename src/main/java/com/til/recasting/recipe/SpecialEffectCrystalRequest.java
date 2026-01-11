package com.til.recasting.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.til.recasting.handler.CapabilityRegistryHandler;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * SE结晶的需求定义
 * 用于定义和匹配特定的特殊效果类型和等级
 * 
 * @param specialEffectType 特殊效果类型，null 表示不限制
 * @param level 特殊效果等级，-1 表示不限制
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

    public static SpecialEffectCrystalRequest fromJSON(JsonObject json) {
        return CODEC.parse(JsonOps.INSTANCE, json)
                .resultOrPartial(msg -> log.error("Failed to parse SECrystalRequest: {}", msg))
                .orElseGet(Builder.newInstance()::build);
    }

    public JsonElement toJson() {
        return CODEC.encodeStart(JsonOps.INSTANCE, this)
                .resultOrPartial(msg -> log.error("Failed to encode SECrystalRequest: {}", msg))
                .orElseThrow();
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.specialEffectType() != null);
        if (this.specialEffectType() != null) {
            buffer.writeResourceLocation(this.specialEffectType());
        }
        buffer.writeInt(this.level());
    }

    public static SpecialEffectCrystalRequest fromNetwork(FriendlyByteBuf buffer) {
        ResourceLocation type = buffer.readBoolean() ? buffer.readResourceLocation() : null;
        int level = buffer.readInt();
        return new SpecialEffectCrystalRequest(type, level);
    }

    /**
     * 初始化物品堆栈，设置 SE结晶 的特殊效果类型和等级
     */
    public void initItemStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        stack.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA).ifPresent(data -> {
            if (this.specialEffectType != null) {
                data.setSpecialEffectType(this.specialEffectType);
            }
            if (this.level >= 0) {
                data.setSpecialEffectLevel(this.level);
            }
        });
    }

    /**
     * 测试物品堆栈是否满足需求
     */
    public boolean test(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        var capabilityResult = stack.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA);
        if (!capabilityResult.isPresent()) {
            return false;
        }

        var data = capabilityResult.orElseThrow(NullPointerException::new);

        // 检查特殊效果类型
        if (this.specialEffectType != null) {
            ResourceLocation itemType = data.getSpecialEffectType();
            if (itemType == null || !itemType.equals(this.specialEffectType)) {
                return false;
            }
        }

        // 检查精确等级匹配
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

        /**
         * 设置特殊效果类型
         */
        public Builder specialEffectType(ResourceLocation specialEffectType) {
            this.specialEffectType = specialEffectType;
            return this;
        }

        /**
         * 设置等级（精确匹配）
         */
        public Builder level(int level) {
            this.level = level;
            return this;
        }

        public SpecialEffectCrystalRequest build() {
            return new SpecialEffectCrystalRequest(specialEffectType, level);
        }
    }
}


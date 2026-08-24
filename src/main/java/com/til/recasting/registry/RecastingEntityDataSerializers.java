package com.til.recasting.registry;

import com.til.recasting.Recasting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 实体同步数据自定义序列化器。
 */
public final class RecastingEntityDataSerializers {

    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, Recasting.MODID);

    private static final StreamCodec<RegistryFriendlyByteBuf, ResourceLocation> RESOURCE_LOCATION_CODEC =
            ResourceLocation.STREAM_CODEC.cast();

    private static final StreamCodec<RegistryFriendlyByteBuf, ResourceLocation[]> RESOURCE_LOCATION_ARRAY_CODEC =
            StreamCodec.of(
                    (buf, resourceLocations) -> {
                        ByteBufCodecs.VAR_INT.encode(buf, resourceLocations.length);
                        for (ResourceLocation resourceLocation : resourceLocations) {
                            RESOURCE_LOCATION_CODEC.encode(buf, resourceLocation);
                        }
                    },
                    buf -> {
                        int length = ByteBufCodecs.VAR_INT.decode(buf);
                        ResourceLocation[] resourceLocations = new ResourceLocation[length];
                        for (int i = 0; i < length; i++) {
                            resourceLocations[i] = RESOURCE_LOCATION_CODEC.decode(buf);
                        }
                        return resourceLocations;
                    }
            );

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<ResourceLocation>> RESOURCE_LOCATION =
            ENTITY_DATA_SERIALIZERS.register(
                    "resource_location",
                    () -> EntityDataSerializer.forValueType(RESOURCE_LOCATION_CODEC)
            );

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<ResourceLocation[]>> RESOURCE_LOCATION_ARRAY =
            ENTITY_DATA_SERIALIZERS.register(
                    "resource_location_array",
                    () -> EntityDataSerializer.forValueType(RESOURCE_LOCATION_ARRAY_CODEC)
            );

    private RecastingEntityDataSerializers() {
    }
}

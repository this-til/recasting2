package com.til.recasting.registry;

import com.til.recasting.Recasting;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class RecastingEntityDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, Recasting.MODID);

    public static final Supplier<EntityDataSerializer<ResourceLocation>> RESOURCE_LOCATION = ENTITY_DATA_SERIALIZERS.register(
            "resource_location",
            () -> EntityDataSerializer.simple(
                    (b, r) -> b.writeUtf(r.toString()),
                    b -> ResourceLocation.parse(b.readUtf())
            )
    );

    public static final Supplier<EntityDataSerializer<ResourceLocation[]>> RESOURCE_LOCATION_ARRAY = ENTITY_DATA_SERIALIZERS.register(
            "resource_location_array",
            () -> EntityDataSerializer.simple(
                    (buffer, resourceLocations) -> {
                        buffer.writeVarInt(resourceLocations.length);
                        for(ResourceLocation resourceLocation : resourceLocations) {
                            buffer.writeUtf(resourceLocation.toString());
                        }
                    },
                    buffer -> {
                        int length = buffer.readVarInt();
                        ResourceLocation[] resourceLocations = new ResourceLocation[length];
                        for(int i = 0; i < length; i++) {
                            resourceLocations[i] = ResourceLocation.parse(buffer.readUtf());
                        }
                        return resourceLocations;
                    }
            )
    );

}

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

}

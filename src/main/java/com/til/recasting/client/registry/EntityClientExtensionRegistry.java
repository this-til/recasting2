package com.til.recasting.client.registry;

import com.til.recasting.Recasting;
import com.til.recasting.client.registry.instance.EntityClientExtension;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.registry.RecastingParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * 实体客户端扩展注册表。
 */
public class EntityClientExtensionRegistry {

    public static final ResourceKey<Registry<EntityClientExtension>> ENTITY_CLIENT_EXTENSION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Recasting.prefix("entity_client_extension"));

    public static final DeferredRegister<EntityClientExtension> ENTITY_CLIENT_EXTENSIONS =
            DeferredRegister.create(ENTITY_CLIENT_EXTENSION_REGISTRY_KEY, Recasting.MODID);

    public static final Supplier<IForgeRegistry<EntityClientExtension>> REGISTRY =
            ENTITY_CLIENT_EXTENSIONS.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<EntityClientExtension> MORTAL_DUST_TRAIL = ENTITY_CLIENT_EXTENSIONS.register(
            "mortal_dust_trail",
            () -> new EntityClientExtension(entity -> entity.clientTickCallbackPoint.register(() -> {
                if (!(entity instanceof SummondSwordEntity blade)) {
                    return;
                }
                SummondSwordEntity.ActionType action = blade.getActionType();
                if (action != SummondSwordEntity.ActionType.PREPARE
                        && action != SummondSwordEntity.ActionType.FLYING) {
                    return;
                }
                Vec3 pos = blade.position();
                for(int i = 0; i < 2; i++) {
                    blade.level().addParticle(
                            RecastingParticleTypes.MORTAL_DUST_TRAIL.get(),
                            true,
                            pos.x,
                            pos.y,
                            pos.z,
                            0.0,
                            0.0,
                            0.0
                    );
                }
            }))
    );
}

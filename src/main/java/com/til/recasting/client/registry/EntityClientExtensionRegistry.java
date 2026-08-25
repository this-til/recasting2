package com.til.recasting.client.registry;

import com.til.recasting.Recasting;
import com.til.recasting.client.registry.instance.EntityClientExtension;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.registry.RecastingParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

/**
 * 实体客户端扩展注册表。
 */
public final class EntityClientExtensionRegistry {

    public static final ResourceKey<Registry<EntityClientExtension>> ENTITY_CLIENT_EXTENSION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Recasting.prefix("entity_client_extension"));

    public static final DeferredRegister<EntityClientExtension> ENTITY_CLIENT_EXTENSIONS =
            DeferredRegister.create(ENTITY_CLIENT_EXTENSION_REGISTRY_KEY, Recasting.MODID);

    public static final Registry<EntityClientExtension> REGISTRY =
            new RegistryBuilder<>(ENTITY_CLIENT_EXTENSION_REGISTRY_KEY).sync(false).create();

    public static final DeferredHolder<EntityClientExtension, EntityClientExtension> MORTAL_DUST_TRAIL = ENTITY_CLIENT_EXTENSIONS.register(
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
                for (int i = 0; i < 2; i++) {
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

    private EntityClientExtensionRegistry() {
    }
}

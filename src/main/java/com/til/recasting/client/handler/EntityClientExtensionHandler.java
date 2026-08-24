package com.til.recasting.client.handler;

import com.til.recasting.Recasting;
import com.til.recasting.client.registry.EntityClientExtensionRegistry;
import com.til.recasting.client.registry.instance.EntityClientExtension;
import com.til.recasting.entity.StandardizationAttackEntity;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = Recasting.MODID, value = Dist.CLIENT)
public final class EntityClientExtensionHandler {

    private EntityClientExtensionHandler() {
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide()) {
            return;
        }
        if (event.getEntity() instanceof StandardizationAttackEntity entity) {
            refresh(entity);
        }
    }

    public static void refresh(StandardizationAttackEntity entity) {
        entity.clientTickCallbackPoint.clear();
        for (ResourceLocation id : entity.getClientExtensions()) {
            EntityClientExtension extension = EntityClientExtensionRegistry.REGISTRY.get(id);
            if (extension == null) {
                continue;
            }
            extension.apply(entity);
        }
    }
}

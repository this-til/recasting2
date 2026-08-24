package com.til.recasting.client.handler;

import com.til.recasting.Recasting;
import com.til.recasting.client.registry.EntityClientExtensionRegistry;
import com.til.recasting.client.registry.instance.EntityClientExtension;
import com.til.recasting.entity.StandardizationAttackEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Recasting.MODID, value = Dist.CLIENT)
public class EntityClientExtensionHandler {

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
        for(ResourceLocation id : entity.getClientExtensions()) {
            EntityClientExtension extension = EntityClientExtensionRegistry.REGISTRY.get().getValue(id);
            if (extension == null) {
                continue;
            }
            extension.apply(entity);
        }
    }
}

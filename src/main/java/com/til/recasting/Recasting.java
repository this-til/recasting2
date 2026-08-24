package com.til.recasting;

import com.mojang.logging.LogUtils;
import com.til.recasting.network.NetworkManager;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingDataComponents;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

/**
 * Recasting 1.21 NeoForge：P1 基建与数据层（Attachment / DataComponent / 网络骨架）。
 */
@Mod(Recasting.MODID)
public class Recasting {

    public static final String MODID = "recasting";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Recasting(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        modEventBus.addListener(NetworkManager::register);

        RecastingAttachments.ATTACHMENT_TYPES.register(modEventBus);
        RecastingDataComponents.DATA_COMPONENTS.register(modEventBus);
        RecastingBuffTypes.BUFF_TYPES.register(modEventBus);

        LOGGER.info("Recasting {} loaded (P1 data layer)", modContainer.getModInfo().getVersion());
    }

    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}

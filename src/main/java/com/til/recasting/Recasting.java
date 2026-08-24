package com.til.recasting;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

/**
 * Recasting 1.21 NeoForge 首版：空壳可加载。
 * 玩法内容尚未从 1.20.1 移植。
 */
@Mod(Recasting.MODID)
public class Recasting {

    public static final String MODID = "recasting";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Recasting(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Recasting {} loaded (empty 1.21 port shell)", modContainer.getModInfo().getVersion());
    }

    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}

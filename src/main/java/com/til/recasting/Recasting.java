package com.til.recasting;

import lombok.extern.log4j.Log4j2;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Log4j2
@Mod(Recasting.MODID)
public class Recasting {

    public static final String MODID = "recasting";

    public Recasting() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(Recasting.MODID, path);
    }
}

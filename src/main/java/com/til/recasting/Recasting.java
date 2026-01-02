package com.til.recasting;

import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.SlashArtsRegistry;
import com.til.recasting.registry.SpecialEffectsRegistry;
import lombok.extern.log4j.Log4j2;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

@Log4j2
@Mod(Recasting.MODID)
public class Recasting {

    public static final String MODID = "recasting";

    public Recasting() {
        @SuppressWarnings("removal")
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册 Slash Arts (SA) 注册表
        SlashArtsRegistry.SLASH_ARTS.register(modEventBus);

        // 注册 Special Effects (SE) 注册表
        SpecialEffectsRegistry.SPECIAL_EFFECT.register(modEventBus);

        // 注册物品
        RecastingItems.ITEMS.register(modEventBus);

        modEventBus.addListener(EventPriority.HIGH, RecastingItems::onBuildCreativeModeTabContents);
    }

    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(Recasting.MODID, path);
    }
}

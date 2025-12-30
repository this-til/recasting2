package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.capability.SlashBladeDefinitionExtensionCapabilityProvider;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityAttachHandler {

    public static final ResourceLocation EXTENSION_KEY = Recasting.prefix("slashblade_extension");

    @SubscribeEvent
    public static void onAttachCapabilitiesItemStack(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (stack.getItem() instanceof ItemSlashBlade) {
            event.addCapability(EXTENSION_KEY, new SlashBladeDefinitionExtensionCapabilityProvider());
        }
    }
}


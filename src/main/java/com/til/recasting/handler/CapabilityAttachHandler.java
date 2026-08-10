package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.capability.provider.FeBladeCapabilityProvider;
import com.til.recasting.capability.provider.SECrystalProvider;
import com.til.recasting.capability.provider.SlashBladeDefinitionExtensionProvider;
import com.til.recasting.registry.RecastingItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityAttachHandler {

    public static final ResourceLocation EXTENSION_KEY = Recasting.prefix("slashblade_extension");
    public static final ResourceLocation FE_ENERGY_KEY = Recasting.prefix("fe_blade_energy");
    public static final ResourceLocation SE_CRYSTAL_KEY = Recasting.prefix("se_crystal_data");

    @SubscribeEvent
    public static void onAttachCapabilitiesItemStack(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (stack.getItem() instanceof ItemSlashBlade) {
            event.addCapability(EXTENSION_KEY, new SlashBladeDefinitionExtensionProvider());
            event.addCapability(FE_ENERGY_KEY, new FeBladeCapabilityProvider(stack));
        }
        if (stack.getItem() == RecastingItems.SE_CRYSTAL.get()) {
            event.addCapability(SE_CRYSTAL_KEY, new SECrystalProvider());
        }
    }
}


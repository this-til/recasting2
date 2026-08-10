package com.til.recasting.client.handler;

import com.til.recasting.Recasting;
import com.til.recasting.client.FeEnergyBarItemDecorator;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 为所有拔刀剑物品注册 FE 能量条装饰器。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class FeEnergyBarDecorationHandler {

    private FeEnergyBarDecorationHandler() {
    }

    @SubscribeEvent
    public static void onRegisterItemDecorations(RegisterItemDecorationsEvent event) {
        for (Item item : ForgeRegistries.ITEMS) {
            if (item instanceof ItemSlashBlade) {
                event.register(item, FeEnergyBarItemDecorator.INSTANCE);
            }
        }
    }
}

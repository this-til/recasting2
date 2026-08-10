package com.til.recasting.advancement;

import com.til.recasting.Recasting;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class RecastingItemPredicates {

    private RecastingItemPredicates() {
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ItemPredicate.register(NamedSlashBladeItemPredicate.TYPE, NamedSlashBladeItemPredicate::fromJson);
            ItemPredicate.register(SeCrystalItemPredicate.TYPE, SeCrystalItemPredicate::fromJson);
            ItemPredicate.register(SlashArtsSphereItemPredicate.TYPE, SlashArtsSphereItemPredicate::fromJson);
            ItemPredicate.register(BladeStatItemPredicate.TYPE, BladeStatItemPredicate::fromJson);
            ItemPredicate.register(EnchantedSlashBladeItemPredicate.TYPE, EnchantedSlashBladeItemPredicate::fromJson);
        });
    }
}

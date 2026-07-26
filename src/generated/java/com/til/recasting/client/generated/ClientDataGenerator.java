package com.til.recasting.client.generated;

import com.til.recasting.Recasting;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientDataGenerator {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        net.minecraft.data.DataGenerator dataGenerator = event.getGenerator();
        PackOutput packOutput = dataGenerator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        dataGenerator.addProvider(event.includeClient(), new RecastingItemModelProvider(packOutput, existingFileHelper));
        dataGenerator.addProvider(event.includeClient(), new RecastingLanguageProvider(packOutput, "zh_cn"));
        dataGenerator.addProvider(event.includeClient(), new RecastingLanguageProvider(packOutput, "en_us"));
    }
}

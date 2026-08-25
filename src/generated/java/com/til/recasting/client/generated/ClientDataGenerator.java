package com.til.recasting.client.generated;

import com.til.recasting.Recasting;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Recasting.MODID)
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

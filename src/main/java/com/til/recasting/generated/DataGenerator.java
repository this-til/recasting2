package com.til.recasting.generated;


import com.til.recasting.Recasting;
import lombok.extern.log4j.Log4j2;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerator {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        net.minecraft.data.DataGenerator dataGenerator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        PackOutput packOutput = dataGenerator.getPackOutput();

        // 注册 SlashBlade 定义数据包生成
        final RegistrySetBuilder bladeBuilder = new RegistrySetBuilder().add(SlashBladeDefinition.REGISTRY_KEY, RecastingSlashBladeBuiltInRegistry::registerAll);

        dataGenerator.addProvider(
                event.includeServer(),
                new DatapackBuiltinEntriesProvider(packOutput, lookupProvider, bladeBuilder, Set.of(Recasting.MODID)) {
                    @Override
                    public @NotNull String getName() {
                        return "Recasting SlashBlade Definition Registry";
                    }
                }
        );

        // 注册配方生成器（物品配方）
        dataGenerator.addProvider(
                event.includeServer(),
                new RecastingRecipeProvider(packOutput)
        );

        // 注册刀配方生成器
        dataGenerator.addProvider(
                event.includeServer(),
                new SlashBladeRecipeProvider(packOutput)
        );

    }

}

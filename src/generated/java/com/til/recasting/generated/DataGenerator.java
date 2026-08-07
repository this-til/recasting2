package com.til.recasting.generated;


import com.til.recasting.Recasting;
import lombok.extern.log4j.Log4j2;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
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
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // 注册 SlashBlade 定义数据包生成
        final RegistrySetBuilder bladeBuilder = new RegistrySetBuilder().add(SlashBladeDefinition.REGISTRY_KEY, SlashBladeDefinitions::registerAll);

        dataGenerator.addProvider(
                event.includeServer(),
                new DatapackBuiltinEntriesProvider(packOutput, lookupProvider, bladeBuilder, Set.of(Recasting.MODID)) {
                    @Override
                    public @NotNull String getName() {
                        return "Recasting SlashBlade Definition Registry";
                    }
                }
        );

        dataGenerator.addProvider(
                event.includeServer(),
                namedProvider("Recasting Recipes", new RecastingRecipes(packOutput))
        );

        dataGenerator.addProvider(
                event.includeServer(),
                namedProvider("Recasting SlashBlade Recipes", new SlashBladeRecipes(packOutput))
        );

        dataGenerator.addProvider(
                event.includeServer(),
                namedProvider("Recasting Special Effect Recipes", new SpecialEffectRecipes(packOutput))
        );

        dataGenerator.addProvider(
                event.includeServer(),
                new RecastingAdvancementProvider(packOutput, lookupProvider)
        );

        BlockTagsProvider blockTags = new BlockTagsProvider(packOutput, lookupProvider, Recasting.MODID, existingFileHelper) {
            @Override
            protected void addTags(@NotNull HolderLookup.Provider provider) {
            }
        };
        dataGenerator.addProvider(event.includeServer(), blockTags);
        dataGenerator.addProvider(
                event.includeServer(),
                new RecastingItemTagsProvider(packOutput, lookupProvider, blockTags.contentsGetter(), existingFileHelper)
        );
    }

    private static DataProvider namedProvider(String name, DataProvider delegate) {
        return new DataProvider() {
            @Override
            public CompletableFuture<?> run(CachedOutput output) {
                return delegate.run(output);
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

}

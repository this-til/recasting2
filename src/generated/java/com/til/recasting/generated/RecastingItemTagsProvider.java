package com.til.recasting.generated;

import com.til.recasting.Recasting;
import com.til.recasting.item.ProudSoulItem;
import com.til.recasting.registry.RecastingTags;
import com.til.recasting.registry.requir.SlashBladeItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * 生成 {@link RecastingTags#PROUD_SOULS} 等物品标签。
 */
public class RecastingItemTagsProvider extends ItemTagsProvider {

    public RecastingItemTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagLookup<Block>> blockTags,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, blockTags, Recasting.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var proudSouls = tag(RecastingTags.PROUD_SOULS)
                .add(SlashBladeItems.PROUDSOUL_TINY.get())
                .add(SlashBladeItems.PROUDSOUL.get())
                .add(SlashBladeItems.PROUDSOUL_INGOT.get())
                .add(SlashBladeItems.PROUDSOUL_SPHERE.get())
                .add(SlashBladeItems.PROUDSOUL_CRYSTAL.get())
                .add(SlashBladeItems.PROUDSOUL_TRAPEZOHEDRON.get());

        for (var entry : ForgeRegistries.ITEMS.getEntries()) {
            if (entry.getValue() instanceof ProudSoulItem) {
                proudSouls.add(entry.getValue());
            }
        }
    }
}

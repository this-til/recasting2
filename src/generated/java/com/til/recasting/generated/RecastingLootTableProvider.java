package com.til.recasting.generated;

import com.til.recasting.Recasting;
import com.til.recasting.registry.RecastingItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * 生成成就奖励等战利品表。
 */
public class RecastingLootTableProvider extends LootTableProvider {

    public RecastingLootTableProvider(PackOutput output) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(AdvancementRewards::new, LootContextParamSets.ADVANCEMENT_REWARD)
        ));
    }

    private static final class AdvancementRewards implements LootTableSubProvider {

        @Override
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
            output.accept(
                    Recasting.prefix("advancements/growth_root"),
                    LootTable.lootTable()
                            .withPool(LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1.0F))
                                    .add(LootItem.lootTableItem(RecastingItems.PROUD_SOUL_BAG.get())))
            );
        }
    }
}

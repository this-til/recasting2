package com.til.recasting.registry;

import com.til.recasting.Recasting;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Recasting 物品 / 方块标签常量。
 */
public final class RecastingTags {

    public static final TagKey<Item> PROUD_SOULS = TagKey.create(Registries.ITEM, Recasting.prefix("proud_souls"));

    private RecastingTags() {
    }
}

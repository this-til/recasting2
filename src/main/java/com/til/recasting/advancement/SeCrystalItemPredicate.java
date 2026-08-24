package com.til.recasting.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.til.recasting.capability.SECrystalData;
import com.til.recasting.registry.RecastingDataComponents;
import com.til.recasting.registry.RecastingItems;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * 匹配指定 SE 结晶（同物品 + SE_CRYSTAL_DATA）。
 */
public record SeCrystalItemPredicate(ResourceLocation effectId, int level) implements ItemSubPredicate {

    public static final Codec<SeCrystalItemPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("effect").forGetter(SeCrystalItemPredicate::effectId),
            Codec.INT.optionalFieldOf("level", 1).forGetter(SeCrystalItemPredicate::level)
    ).apply(instance, SeCrystalItemPredicate::new));

    @Override
    public boolean matches(ItemStack stack) {
        if (stack.isEmpty() || !stack.is(RecastingItems.SE_CRYSTAL.get())) {
            return false;
        }
        SECrystalData data = stack.getOrDefault(
                RecastingDataComponents.SE_CRYSTAL_DATA.get(),
                SECrystalData.EMPTY
        );
        return effectId.equals(data.getSpecialEffectType()) && data.getSpecialEffectLevel() == level;
    }
}

package com.til.recasting.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * 匹配指定命名刀（同物品 + BLADESTATE translationKey）。
 */
public record NamedSlashBladeItemPredicate(ResourceLocation bladeId) implements ItemSubPredicate {

    public static final Codec<NamedSlashBladeItemPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("blade").forGetter(NamedSlashBladeItemPredicate::bladeId)
    ).apply(instance, NamedSlashBladeItemPredicate::new));

    public static final ItemSubPredicate.Type<NamedSlashBladeItemPredicate> TYPE =
            new ItemSubPredicate.Type<>(CODEC);

    public static NamedSlashBladeItemPredicate of(ResourceLocation bladeId) {
        return new NamedSlashBladeItemPredicate(bladeId);
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSlashBlade)) {
            return false;
        }
        return BladeStateAccess.of(stack)
                .map(state -> {
                    String translationKey = state.getTranslationKey();
                    if (BladeTranslationHelper.itemDescriptionId(bladeId).equals(translationKey)) {
                        return true;
                    }
                    return bladeId.equals(BladeTranslationHelper.tryParseBladeId(translationKey));
                })
                .orElse(false);
    }
}

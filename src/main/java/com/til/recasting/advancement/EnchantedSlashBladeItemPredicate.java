package com.til.recasting.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * 匹配带有指定附魔（至少 1 级）的任意拔刀剑。
 */
public record EnchantedSlashBladeItemPredicate(ResourceLocation enchantmentId) implements ItemSubPredicate {

    public static final Codec<EnchantedSlashBladeItemPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("enchantment").forGetter(EnchantedSlashBladeItemPredicate::enchantmentId)
    ).apply(instance, EnchantedSlashBladeItemPredicate::new));

    @Override
    public boolean matches(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSlashBlade)) {
            return false;
        }
        ResourceKey<Enchantment> key = ResourceKey.create(Registries.ENCHANTMENT, enchantmentId);
        return com.til.recasting.handler.MathHelper.getEnchantmentLevel(stack, key) > 0;
    }
}

package com.til.recasting.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.til.recasting.Recasting;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

/**
 * 匹配带有指定附魔（至少 1 级）的任意拔刀剑。
 */
public class EnchantedSlashBladeItemPredicate extends ItemPredicate {

    public static final ResourceLocation TYPE = Recasting.prefix("enchanted_slashblade");

    private final ResourceLocation enchantmentId;

    public EnchantedSlashBladeItemPredicate(ResourceLocation enchantmentId) {
        this.enchantmentId = Objects.requireNonNull(enchantmentId, "enchantmentId");
    }

    public static EnchantedSlashBladeItemPredicate of(Enchantment enchantment) {
        ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
        if (id == null) {
            throw new IllegalArgumentException("Unregistered enchantment: " + enchantment);
        }
        return new EnchantedSlashBladeItemPredicate(id);
    }

    public static EnchantedSlashBladeItemPredicate fromJson(JsonObject json) {
        return new EnchantedSlashBladeItemPredicate(
                ResourceLocation.parse(GsonHelper.getAsString(json, "enchantment")));
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSlashBlade)) {
            return false;
        }
        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(enchantmentId);
        if (enchantment == null) {
            return false;
        }
        return stack.getEnchantmentLevel(enchantment) > 0;
    }

    @Override
    public JsonElement serializeToJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", TYPE.toString());
        json.addProperty("enchantment", enchantmentId.toString());
        return json;
    }
}

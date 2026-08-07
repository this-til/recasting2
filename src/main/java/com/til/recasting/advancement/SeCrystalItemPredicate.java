package com.til.recasting.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.til.recasting.Recasting;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingItems;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

/**
 * 匹配指定 SE 结晶（同物品 + SE_CRYSTAL_DATA）。
 */
public class SeCrystalItemPredicate extends ItemPredicate {

    public static final ResourceLocation TYPE = Recasting.prefix("se_crystal");

    private final ResourceLocation effectId;
    private final int level;

    public SeCrystalItemPredicate(ResourceLocation effectId, int level) {
        this.effectId = Objects.requireNonNull(effectId, "effectId");
        this.level = level;
    }

    public static SeCrystalItemPredicate of(ResourceLocation effectId, int level) {
        return new SeCrystalItemPredicate(effectId, level);
    }

    public static SeCrystalItemPredicate fromJson(JsonObject json) {
        ResourceLocation effectId = ResourceLocation.parse(GsonHelper.getAsString(json, "effect"));
        int level = GsonHelper.getAsInt(json, "level", 1);
        return new SeCrystalItemPredicate(effectId, level);
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() != RecastingItems.SE_CRYSTAL.get()) {
            return false;
        }
        return stack.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA)
                .map(data -> effectId.equals(data.getSpecialEffectType()) && data.getSpecialEffectLevel() == level)
                .orElse(false);
    }

    @Override
    public JsonElement serializeToJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", TYPE.toString());
        json.addProperty("effect", effectId.toString());
        json.addProperty("level", level);
        return json;
    }
}

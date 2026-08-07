package com.til.recasting.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.til.recasting.Recasting;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

/**
 * 匹配指定命名刀（同物品 + BLADESTATE translationKey）。
 */
public class NamedSlashBladeItemPredicate extends ItemPredicate {

    public static final ResourceLocation TYPE = Recasting.prefix("named_slashblade");

    private final ResourceLocation bladeId;

    public NamedSlashBladeItemPredicate(ResourceLocation bladeId) {
        this.bladeId = Objects.requireNonNull(bladeId, "bladeId");
    }

    public static NamedSlashBladeItemPredicate of(ResourceLocation bladeId) {
        return new NamedSlashBladeItemPredicate(bladeId);
    }

    public static NamedSlashBladeItemPredicate fromJson(JsonObject json) {
        return new NamedSlashBladeItemPredicate(
                ResourceLocation.parse(GsonHelper.getAsString(json, "blade")));
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSlashBlade)) {
            return false;
        }
        return stack.getCapability(ItemSlashBlade.BLADESTATE)
                .map(state -> {
                    String translationKey = state.getTranslationKey();
                    if (BladeTranslationHelper.itemDescriptionId(bladeId).equals(translationKey)) {
                        return true;
                    }
                    return bladeId.equals(BladeTranslationHelper.tryParseBladeId(translationKey));
                })
                .orElse(false);
    }

    @Override
    public JsonElement serializeToJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", TYPE.toString());
        json.addProperty("blade", bladeId.toString());
        return json;
    }
}

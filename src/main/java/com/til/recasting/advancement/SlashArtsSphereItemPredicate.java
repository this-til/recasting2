package com.til.recasting.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.til.recasting.Recasting;
import com.til.recasting.registry.requir.SlashBladeItems;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

/**
 * 匹配带指定 {@code SpecialAttackType} 的耀魂宝珠。
 */
public class SlashArtsSphereItemPredicate extends ItemPredicate {

    public static final ResourceLocation TYPE = Recasting.prefix("slash_arts_sphere");
    private static final String SPECIAL_ATTACK_TYPE = "SpecialAttackType";

    private final ResourceLocation slashArtsId;

    public SlashArtsSphereItemPredicate(ResourceLocation slashArtsId) {
        this.slashArtsId = Objects.requireNonNull(slashArtsId, "slashArtsId");
    }

    public static SlashArtsSphereItemPredicate of(ResourceLocation slashArtsId) {
        return new SlashArtsSphereItemPredicate(slashArtsId);
    }

    public static SlashArtsSphereItemPredicate fromJson(JsonObject json) {
        return new SlashArtsSphereItemPredicate(
                ResourceLocation.parse(GsonHelper.getAsString(json, "slash_arts")));
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack.isEmpty() || !stack.is(SlashBladeItems.PROUDSOUL_SPHERE.get())) {
            return false;
        }
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(SPECIAL_ATTACK_TYPE)) {
            return false;
        }
        ResourceLocation stored = ResourceLocation.tryParse(tag.getString(SPECIAL_ATTACK_TYPE));
        return slashArtsId.equals(stored);
    }

    @Override
    public JsonElement serializeToJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", TYPE.toString());
        json.addProperty("slash_arts", slashArtsId.toString());
        return json;
    }
}

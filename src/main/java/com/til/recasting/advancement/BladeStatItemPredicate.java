package com.til.recasting.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.til.recasting.Recasting;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

/**
 * 匹配击杀数或精炼等级超过阈值的拔刀剑（与伤害加成 {@code > threshold} 一致）。
 */
public class BladeStatItemPredicate extends ItemPredicate {

    public static final net.minecraft.resources.ResourceLocation TYPE = Recasting.prefix("blade_stat");

    private final int minExclusiveKillCount;
    private final int minExclusiveRefine;

    public BladeStatItemPredicate(int minExclusiveKillCount, int minExclusiveRefine) {
        this.minExclusiveKillCount = minExclusiveKillCount;
        this.minExclusiveRefine = minExclusiveRefine;
    }

    public static BladeStatItemPredicate minKill(int thresholdExclusive) {
        return new BladeStatItemPredicate(thresholdExclusive, -1);
    }

    public static BladeStatItemPredicate minRefine(int thresholdExclusive) {
        return new BladeStatItemPredicate(-1, thresholdExclusive);
    }

    public static BladeStatItemPredicate fromJson(JsonObject json) {
        int kill = GsonHelper.getAsInt(json, "min_exclusive_kill", -1);
        int refine = GsonHelper.getAsInt(json, "min_exclusive_refine", -1);
        return new BladeStatItemPredicate(kill, refine);
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSlashBlade)) {
            return false;
        }
        return stack.getCapability(ItemSlashBlade.BLADESTATE)
                .map(state -> {
                    if (minExclusiveKillCount >= 0 && state.getKillCount() <= minExclusiveKillCount) {
                        return false;
                    }
                    if (minExclusiveRefine >= 0 && state.getRefine() <= minExclusiveRefine) {
                        return false;
                    }
                    return minExclusiveKillCount >= 0 || minExclusiveRefine >= 0;
                })
                .orElse(false);
    }

    @Override
    public JsonElement serializeToJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", TYPE.toString());
        if (minExclusiveKillCount >= 0) {
            json.addProperty("min_exclusive_kill", minExclusiveKillCount);
        }
        if (minExclusiveRefine >= 0) {
            json.addProperty("min_exclusive_refine", minExclusiveRefine);
        }
        return json;
    }
}

package com.til.recasting.advancement;

import com.google.gson.JsonObject;
import com.til.recasting.Recasting;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

/**
 * 铁砧完成 SE 铭刻 / 提取 / 删除 / 替换后触发。
 */
public class ForgeSeActionTrigger extends SimpleCriterionTrigger<ForgeSeActionTrigger.TriggerInstance> {

    public static final ResourceLocation ID = Recasting.prefix("forge_se_action");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject json, ContextAwarePredicate player, DeserializationContext context) {
        ForgeSeAction action = ForgeSeAction.byName(GsonHelper.getAsString(json, "action"));
        return new TriggerInstance(player, action);
    }

    public void trigger(ServerPlayer player, ForgeSeAction action) {
        this.trigger(player, instance -> instance.matches(action));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        private final ForgeSeAction action;

        public TriggerInstance(ContextAwarePredicate player, ForgeSeAction action) {
            super(ID, player);
            this.action = action;
        }

        public static TriggerInstance action(ForgeSeAction action) {
            return new TriggerInstance(ContextAwarePredicate.ANY, action);
        }

        public boolean matches(ForgeSeAction performed) {
            return this.action == performed;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            json.addProperty("action", action.getSerializedName());
            return json;
        }
    }
}

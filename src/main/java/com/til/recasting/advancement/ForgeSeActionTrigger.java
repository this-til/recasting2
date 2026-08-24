package com.til.recasting.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * 铁砧完成 SE 铭刻 / 提取 / 删除 / 替换后触发。
 */
public class ForgeSeActionTrigger extends SimpleCriterionTrigger<ForgeSeActionTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ForgeSeAction action) {
        this.trigger(player, instance -> instance.matches(action));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, ForgeSeAction action)
            implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                ForgeSeAction.CODEC.fieldOf("action").forGetter(TriggerInstance::action)
        ).apply(instance, TriggerInstance::new));

        public boolean matches(ForgeSeAction performed) {
            return this.action == performed;
        }
    }
}

package com.til.recasting.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.world.item.ItemStack;

/**
 * 匹配击杀数或精炼等级超过阈值的拔刀剑。
 */
public record BladeStatItemPredicate(int minExclusiveKillCount, int minExclusiveRefine) implements ItemSubPredicate {

    public static final Codec<BladeStatItemPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("min_exclusive_kill", -1).forGetter(BladeStatItemPredicate::minExclusiveKillCount),
            Codec.INT.optionalFieldOf("min_exclusive_refine", -1).forGetter(BladeStatItemPredicate::minExclusiveRefine)
    ).apply(instance, BladeStatItemPredicate::new));

    public static final ItemSubPredicate.Type<BladeStatItemPredicate> TYPE =
            new ItemSubPredicate.Type<>(CODEC);

    public static BladeStatItemPredicate minKill(int minExclusiveKillCount) {
        return new BladeStatItemPredicate(minExclusiveKillCount, -1);
    }

    public static BladeStatItemPredicate minRefine(int minExclusiveRefine) {
        return new BladeStatItemPredicate(-1, minExclusiveRefine);
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSlashBlade)) {
            return false;
        }
        return BladeStateAccess.of(stack)
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
}

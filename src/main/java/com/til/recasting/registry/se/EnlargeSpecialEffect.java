
package com.til.recasting.registry.se;

import com.til.recasting.constant.RecastingSlashBladeKeys;
import com.til.recasting.event.DoSlashExtendEvent;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.Util;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;

/**
 * 变大！
 * 挥刀时大幅增加攻击范围；非法棍 Lambda 使用时直接断刀
 */
public class EnlargeSpecialEffect extends ExtendedSpecialEffect {

    private final float attackRangeBonus = 48f;

    @SubscribeEvent
    public void onEvent(DoSlashExtendEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        ISlashBladeState state = event.getSlashBladeState();
        // 与刀 state / 语言条目一致：makeDescriptionId 会把 path 中的 / 换成 .
        String allowedKey = Util.makeDescriptionId("item", RecastingSlashBladeKeys.DHARMA_STICK_LAMBDA.location());
        if (!allowedKey.equals(state.getTranslationKey())) {
            if (event.getUser().level().isClientSide()) {
                return;
            }
            breakBlade(event.getBlade(), state);
            return;
        }

        event.addAttackRange(attackRangeBonus);
    }

    private static void breakBlade(ItemStack blade, ISlashBladeState state) {
        if (state.isBroken()) {
            return;
        }
        state.setBroken(true);
        if (state.getMaxDamage() > 0) {
            state.setDamage(state.getMaxDamage());
        }
        if (!blade.isEmpty() && blade.getMaxDamage() > 0) {
            blade.setDamageValue(blade.getMaxDamage() - 1);
        }
    }
}

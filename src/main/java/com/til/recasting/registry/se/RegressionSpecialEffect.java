package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.event.DoSlashExtendEvent;
import com.til.recasting.util.NumberPack;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/***
 * 回溯
 * 挥刀时恢复耐久
 */
public class RegressionSpecialEffect extends ExtendedSpecialEffect {

    NumberPack durabilityAmount = new NumberPack(0f, 1f);

    @SubscribeEvent
    public void onEvent(DoSlashExtendEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        // 只在服务端执行
        if (event.getUser().level().isClientSide()) {
            return;
        }

        PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getBlade());
        int level = getLevel(properties);

        ISlashBladeState state = event.getSlashBladeState();
        if (state.getMaxDamage() <= 0) {
            return;
        }

        // 恢复耐久值
        int restoreAmount = (int) durabilityAmount.of(level);
        if (restoreAmount > 0) {
            int currentDamage = state.getDamage();
            int newDamage = Math.max(0, currentDamage - restoreAmount);
            state.setDamage(newDamage);
        }
    }

}

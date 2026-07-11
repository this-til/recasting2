package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.event.DoSlashExtendEvent;
import com.til.recasting.util.NumberPack;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/***
 * 生长
 * 挥刀时恢复生命
 */
public class GrowthSpecialEffect extends ExtendedSpecialEffect {

    NumberPack healAmount = new NumberPack(0f, 0.2f);

    @SubscribeEvent
    public void onEvent(DoSlashExtendEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getBlade());
        int level = getLevel(properties);

        // 恢复生命值
        float heal = healAmount.of(level);
        if (heal > 0) {
            event.getUser().heal(heal);
        }
    }

}

package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.event.DoSlashExtendEvent;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.util.DamageStructure;
import com.til.recasting.util.NumberPack;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/***
 * 协同攻击
 * 挥刀的时概率额外挥刀
 */
public class CooperateWithSpecialEffect extends ExtendedSpecialEffect {

    NumberPack probability = new NumberPack(0.2f, 0.05f);
    NumberPack attackRatio = new NumberPack(0, 0.1f);
    int delay = 10;

    @SubscribeEvent
    public void onEvent(DoSlashExtendEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getBlade());
        int level = getLevel(properties);

        if (event.getUser().getRandom().nextFloat() >= probability.of(level)) {
            return;
        }

        event.getUser().getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(
                timeRun -> timeRun.addTimerCell(
                        () -> AttackHelper.doSlash(
                                event.getUser(),
                                event.getRoll(),
                                event.getSlashBladeState().getColorCode(),
                                event.getCenterOffset(),
                                event.isMute(),
                                event.isCritical(),
                                new DamageStructure(
                                        event.getModifiedRatio() * attackRatio.of(level),
                                        (float) (event.getDamage() * attackRatio.of(level))
                                ),
                                event.getAttackRange(),
                                null
                        ),
                        delay
                )
        );


    }

}

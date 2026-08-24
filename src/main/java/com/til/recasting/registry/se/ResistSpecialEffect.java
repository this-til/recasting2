package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.event.DoSlashExtendEvent;
import com.til.recasting.util.NumberPack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;

/***
 * 抵抗
 * 挥刀时获得伤害吸收
 */
public class ResistSpecialEffect extends ExtendedSpecialEffect {

    NumberPack level = new NumberPack(1f, 0f);
    NumberPack time = new NumberPack(1f, 1f);

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
        int levelValue = getLevel(properties);

        // 添加伤害吸收效果
        int effectLevel = (int) level.of(levelValue);
        int duration = (int) time.of(levelValue);
        event.getUser().addEffect(new MobEffectInstance(MobEffects.ABSORPTION, duration, effectLevel));
    }

}

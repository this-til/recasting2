package com.til.recasting.registry.se;

import com.til.recasting.event.DoSlashExtendEvent;
import net.neoforged.bus.api.SubscribeEvent;

/***
 * 染色
 * 挥刀时更改刀刃颜色为随机的
 */
public class ColorDyeSpecialEffect extends ExtendedSpecialEffect {

    @SubscribeEvent
    public void onEvent(DoSlashExtendEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        // 只在服务端执行
        if (event.getUser().level().isClientSide()) {
            return;
        }

        // 生成随机颜色（RGB格式，0x000000 到 0xFFFFFF）
        int randomColor = event.getUser().getRandom().nextInt(0x1000000); // 0 到 16777215 (0xFFFFFF)

        // 设置刀刃颜色
        event.getSlashBladeState().setColorCode(randomColor);
    }

}

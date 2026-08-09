package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.entity.FinalGlowBlackHoleEntity;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 末辉黑洞相关 Forge 事件；不挂在实体类上，避免模组构造期提前加载实体继承链。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FinalGlowBlackHoleHandler {

    private FinalGlowBlackHoleHandler() {
    }

    /**
     * 原版爆炸源为本实体时清空受影响实体列表，避免原版出伤/击退。
     */
    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (event.getExplosion().getExploder() instanceof FinalGlowBlackHoleEntity) {
            event.getAffectedEntities().clear();
        }
    }
}

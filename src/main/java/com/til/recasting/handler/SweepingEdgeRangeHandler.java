package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.event.DoSlashExtendEvent;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * 横扫之刃附魔攻击范围加成处理器
 * 每级横扫之刃附魔增加 0.2 攻击范围
 */
@EventBusSubscriber(modid = Recasting.MODID)
public final class SweepingEdgeRangeHandler {

    private SweepingEdgeRangeHandler() {
    }

    @SubscribeEvent
    public static void onDoSlashExtend(DoSlashExtendEvent event) {
        int sweepingLevel = MathHelper.getEnchantmentLevel(event.getBlade(), Enchantments.SWEEPING_EDGE);

        if (sweepingLevel > 0) {
            event.addAttackRange(sweepingLevel * 0.2f);
        }
    }
}

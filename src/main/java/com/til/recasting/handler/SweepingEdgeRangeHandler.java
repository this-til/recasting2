package com.til.recasting.handler;

import com.til.recasting.event.DoSlashExtendEvent;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.til.recasting.Recasting.MODID;

/**
 * 横扫之刃附魔攻击范围加成处理器
 * 每级横扫之刃附魔增加 0.2 攻击范围
 */
@Mod.EventBusSubscriber(modid = MODID)
public class SweepingEdgeRangeHandler {

    @SubscribeEvent
    public static void onDoSlashExtend(DoSlashExtendEvent event) {
        // 获取横扫之刃附魔等级
        int sweepingLevel = event.getBlade().getEnchantmentLevel(Enchantments.SWEEPING_EDGE);

        if (sweepingLevel > 0) {
            // 每级增加 0.2 攻击范围
            event.addAttackRange(sweepingLevel * 0.2f);
        }
    }
}


package com.til.recasting.handler;

import com.til.recasting.Recasting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 屠巫血咒 / 人皇领域共用的致命伤害耀魂抵挡。
 * 参数取自当前最高阶 SE 实例字段，仅最高阶生效。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ProudSoulLethalAbsorbHelper {

    /** 近窗统计时长：0.2s */
    public static final int PROTECT_WINDOW_TICKS = 4;

    private record ProudSpend(long gameTime, int amount) {
    }

    private static final Map<UUID, Deque<ProudSpend>> RECENT_SPENDS = new ConcurrentHashMap<>();

    private ProudSoulLethalAbsorbHelper() {
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity self = event.getEntity();
        if (self.level().isClientSide()) {
            return;
        }

        float hp = self.getHealth();
        float amount = event.getAmount();
        if (amount < hp) {
            return;
        }

        EmperorLineSeHelper.ActiveLine active = EmperorLineSeHelper.resolveHighest(self);
        if (active == null) {
            return;
        }

        int protectThreshold = active.stats().getProtectThreshold();
        int proudPerDamage = Math.max(1, active.stats().getProudPerDamage());
        int maxProudPerHit = Math.max(proudPerDamage, active.stats().getMaxProudPerHit());

        long now = self.level().getGameTime();
        Deque<ProudSpend> spends = RECENT_SPENDS.computeIfAbsent(self.getUUID(), id -> new ArrayDeque<>());
        purgeExpired(spends, now);
        int spentInWindow = sum(spends);
        boolean protectedMode = spentInWindow > protectThreshold;

        if (protectedMode) {
            event.setAmount(Math.max(0f, hp - 1f));
            return;
        }

        int proud = active.state().getProudSoulCount();
        int maxBlockByCap = maxProudPerHit / proudPerDamage;
        int absorbCap = Math.min(proud / proudPerDamage, maxBlockByCap);
        if (absorbCap <= 0) {
            return;
        }

        float blocked = Math.min(amount, absorbCap);
        int proudCost = (int) blocked * proudPerDamage;
        event.setAmount(amount - blocked);
        active.state().setProudSoulCount(Math.max(0, proud - proudCost));
        spends.addLast(new ProudSpend(now, proudCost));
    }

    private static void purgeExpired(Deque<ProudSpend> spends, long now) {
        while (!spends.isEmpty() && spends.peekFirst().gameTime() < now - PROTECT_WINDOW_TICKS) {
            spends.removeFirst();
        }
    }

    private static int sum(Deque<ProudSpend> spends) {
        int total = 0;
        for (ProudSpend spend : spends) {
            total += spend.amount();
        }
        return total;
    }
}

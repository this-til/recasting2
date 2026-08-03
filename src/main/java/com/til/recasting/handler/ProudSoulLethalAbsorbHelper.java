package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.registry.SpecialEffectsRegistry;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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
 * <ul>
 *   <li>每 150 耀魂抵 1 点伤害</li>
 *   <li>单次最多扣除 3000 耀魂</li>
 *   <li>近 20 tick 累计扣除 &gt; 3000 进入保护态：仍钳制致死，不再扣耀魂</li>
 *   <li>非保护态且耀魂不足：不拦截，放行剩余伤害</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ProudSoulLethalAbsorbHelper {

    public static final int PROUD_PER_DAMAGE = 150;
    public static final int MAX_PROUD_PER_HIT = 3000;
    public static final int PROTECT_WINDOW_TICKS = 20;
    public static final int PROTECT_THRESHOLD = 3000;

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

        AbsorbContext context = resolveAbsorbContext(self);
        if (context == null) {
            return;
        }

        long now = self.level().getGameTime();
        Deque<ProudSpend> spends = RECENT_SPENDS.computeIfAbsent(self.getUUID(), id -> new ArrayDeque<>());
        purgeExpired(spends, now);
        int spentInWindow = sum(spends);
        boolean protectedMode = spentInWindow > PROTECT_THRESHOLD;

        // 窗口保护态：免费保命，不扣耀魂
        if (protectedMode) {
            event.setAmount(Math.max(0f, hp - 1f));
            return;
        }

        int proud = context.state().getProudSoulCount();
        int maxBlockByCap = MAX_PROUD_PER_HIT / PROUD_PER_DAMAGE;
        int absorbCap = Math.min(proud / PROUD_PER_DAMAGE, maxBlockByCap);
        // 耀魂不足：放行，不锁血
        if (absorbCap <= 0) {
            return;
        }

        float blocked = Math.min(amount, absorbCap);
        int proudCost = (int) blocked * PROUD_PER_DAMAGE;
        event.setAmount(amount - blocked);
        context.state().setProudSoulCount(Math.max(0, proud - proudCost));
        spends.addLast(new ProudSpend(now, proudCost));
        // 抵挡后若仍致死：放行剩余伤害（非保护态不强制锁 1 血）
    }

    private static AbsorbContext resolveAbsorbContext(LivingEntity self) {
        InventorySlashBladeSeHelper.BladeSeHit emperor = InventorySlashBladeSeHelper.findFirstInInventory(
                self,
                SpecialEffectsRegistry.HUMAN_EMPEROR_DOMAIN
        );
        if (emperor != null) {
            return new AbsorbContext(emperor.blade(), emperor.state());
        }

        ItemStack main = self.getMainHandItem();
        if (InventorySlashBladeSeHelper.hasSpecialEffect(main, SpecialEffectsRegistry.TU_WU_BLOOD_CURSE)) {
            ISlashBladeState state = main.getCapability(mods.flammpfeil.slashblade.item.ItemSlashBlade.BLADESTATE).orElse(null);
            if (state != null) {
                return new AbsorbContext(main, state);
            }
        }
        return null;
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

    private record AbsorbContext(ItemStack blade, ISlashBladeState state) {
    }
}

package com.til.recasting.registry.se;

import com.til.recasting.handler.AbsoluteHealthChangeGuard;
import com.til.recasting.handler.EmperorLineSeHelper;
import com.til.recasting.handler.InventorySlashBladeSeHelper;
import com.til.recasting.registry.RecastingBuffTypes;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 人皇领域
 * 背包触发：驱散负面、每 tick 回血、免疫击退、屏蔽着火屏幕叠层、秒杀兜底、耀魂修刀。
 * 增伤、致命抵挡与自动补充饥饿由 {@link EmperorLineSeHelper} / ProudSoulLethalAbsorbHelper 按最高阶结算。
 */
@Getter
@Setter
@Accessors(chain = true)
public class HumanEmperorDomainSpecialEffect extends ExtendedSpecialEffect implements EmperorLineStats {

    int lineGrade = 2;
    float damageAmplifier = 0.4752f;
    int proudPerDamage = 90;
    int maxProudPerHit = 3667;
    int protectThreshold = 3667;
    float healPerTick = 0.3f;
    int repairProudCost = 135;
    int repairAmount = 1;
    int foodProudCost = 500;
    int foodRestore = 1;

    @SubscribeEvent
    public void onLivingTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }
        if (entity.level().isClientSide()) {
            return;
        }
        if (!EmperorLineSeHelper.isActiveEmperorEffect(entity, this)) {
            return;
        }
        EmperorLineSeHelper.ActiveLine active = EmperorLineSeHelper.resolveHighestEmperor(entity);
        if (active == null) {
            return;
        }

        RecastingBuffTypes.BUFF_SUPPRESS.get().dispelHarmful(entity);
        entity.heal(healPerTick);
        tryRepairInventoryBlade(entity, active.state(), repairProudCost);
    }

    @SubscribeEvent
    public void onLivingKnockBack(LivingKnockBackEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) {
            return;
        }
        if (!EmperorLineSeHelper.isActiveEmperorEffect(entity, this)) {
            return;
        }
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) {
            return;
        }
        if (AbsoluteHealthChangeGuard.isGuarded()) {
            return;
        }
        if (!EmperorLineSeHelper.isActiveEmperorEffect(entity, this)) {
            return;
        }
        EmperorLineSeHelper.ActiveLine active = EmperorLineSeHelper.resolveHighestEmperor(entity);
        if (active == null || active.state().getProudSoulCount() <= 0) {
            return;
        }
        event.setCanceled(true);
        AbsoluteHealthChangeGuard.run(() -> entity.setHealth(1f));
    }

    private void tryRepairInventoryBlade(LivingEntity entity, ISlashBladeState seBladeState, int cost) {
        if (cost <= 0 || repairAmount <= 0) {
            return;
        }
        if (seBladeState.getProudSoulCount() < cost) {
            return;
        }

        AtomicBoolean repaired = new AtomicBoolean(false);
        InventorySlashBladeSeHelper.forEachInventorySlashBlade(entity, (stack, state) -> {
            if (repaired.get()) {
                return;
            }
            if (seBladeState.getProudSoulCount() < cost) {
                return;
            }
            if (state.getMaxDamage() <= 0) {
                return;
            }
            int damage = state.getDamage();
            if (damage <= 0) {
                return;
            }

            int restored = Math.min(repairAmount, damage);
            state.setDamage(damage - restored);
            seBladeState.setProudSoulCount(seBladeState.getProudSoulCount() - cost);
            repaired.set(true);
        });
    }
}

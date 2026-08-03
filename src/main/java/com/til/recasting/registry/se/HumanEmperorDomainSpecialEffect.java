package com.til.recasting.registry.se;

import com.til.recasting.handler.AbsoluteHealthChangeGuard;
import com.til.recasting.handler.BuffSuppressHandler;
import com.til.recasting.handler.EmperorLineSeHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 人皇领域
 * 背包触发：驱散负面、每 tick 回血、秒杀兜底、耀魂修刀。
 * 增伤与致命抵挡由 {@link EmperorLineSeHelper} / ProudSoulLethalAbsorbHelper 按最高阶结算。
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

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Player player = event.player;
        if (player.level().isClientSide()) {
            return;
        }
        if (!EmperorLineSeHelper.isActiveEmperorEffect(player, this)) {
            return;
        }
        EmperorLineSeHelper.ActiveLine active = EmperorLineSeHelper.resolveHighestEmperor(player);
        if (active == null) {
            return;
        }

        BuffSuppressHandler.dispelHarmful(player);
        player.heal(healPerTick);
        tryRepairInventoryBlade(player, active.state(), repairProudCost);
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

    private void tryRepairInventoryBlade(Player player, ISlashBladeState seBladeState, int cost) {
        if (cost <= 0 || repairAmount <= 0) {
            return;
        }
        if (seBladeState.getProudSoulCount() < cost) {
            return;
        }

        int size = player.getInventory().getContainerSize();
        for (int i = 0; i < size; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemSlashBlade)) {
                continue;
            }
            ISlashBladeState state = stack.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
            if (state == null || state.getMaxDamage() <= 0) {
                continue;
            }
            int damage = state.getDamage();
            if (damage <= 0) {
                continue;
            }

            int restored = Math.min(repairAmount, damage);
            state.setDamage(damage - restored);
            seBladeState.setProudSoulCount(seBladeState.getProudSoulCount() - cost);
            return;
        }
    }
}

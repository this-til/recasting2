package com.til.recasting.registry.se;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.AbsoluteHealthChangeGuard;
import com.til.recasting.handler.BuffSuppressHandler;
import com.til.recasting.handler.InventorySlashBladeSeHelper;
import com.til.recasting.registry.SpecialEffectsRegistry;
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
 * 背包触发：驱散负面、每 tick 回血、持有血咒增伤效果；耀魂充足时秒杀兜底；
 * 消耗耀魂修复背包中任意受损拔刀剑。
 * 致命耀魂抵挡由 ProudSoulLethalAbsorbHelper 处理。
 */
@Setter
@Accessors(chain = true)
public class HumanEmperorDomainSpecialEffect extends ExtendedSpecialEffect {

    float damageAmplifier = 0.3f;
    float healPerTick = 0.5f;
    /** 修复 1 点耐久所需耀魂 */
    int repairProudCost = 300;
    /** 每次修复的耐久量（降低 damage） */
    int repairAmount = 1;

    @SubscribeEvent
    public void onAttackAmplifier(AttackAmplifierEvent event) {
        if (event.getAttacker().level().isClientSide()) {
            return;
        }
        if (!InventorySlashBladeSeHelper.hasInInventory(event.getAttacker(), SpecialEffectsRegistry.HUMAN_EMPEROR_DOMAIN)) {
            return;
        }
        event.addModifiedRatioAmplifier(damageAmplifier);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Player player = event.player;
        if (player.level().isClientSide()) {
            return;
        }
        InventorySlashBladeSeHelper.BladeSeHit seHit = InventorySlashBladeSeHelper.findFirstInInventory(
                player,
                SpecialEffectsRegistry.HUMAN_EMPEROR_DOMAIN
        );
        if (seHit == null) {
            return;
        }

        BuffSuppressHandler.dispelHarmful(player);
        player.heal(healPerTick);
        tryRepairInventoryBlade(player, seHit.state());
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
        if (!InventorySlashBladeSeHelper.hasInInventoryWithProudSoul(entity, SpecialEffectsRegistry.HUMAN_EMPEROR_DOMAIN)) {
            return;
        }
        event.setCanceled(true);
        AbsoluteHealthChangeGuard.run(() -> entity.setHealth(1f));
    }

    private void tryRepairInventoryBlade(Player player, ISlashBladeState seBladeState) {
        if (repairProudCost <= 0 || repairAmount <= 0) {
            return;
        }
        if (seBladeState.getProudSoulCount() < repairProudCost) {
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
            seBladeState.setProudSoulCount(seBladeState.getProudSoulCount() - repairProudCost);
            return;
        }
    }
}

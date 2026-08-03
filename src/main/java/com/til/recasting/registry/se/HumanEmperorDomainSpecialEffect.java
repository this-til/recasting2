package com.til.recasting.registry.se;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.AbsoluteHealthChangeGuard;
import com.til.recasting.handler.BuffSuppressHandler;
import com.til.recasting.handler.InventorySlashBladeSeHelper;
import com.til.recasting.registry.SpecialEffectsRegistry;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 人皇领域
 * 背包触发：驱散负面、每 tick 回血、持有血咒增伤效果；耀魂充足时秒杀兜底。
 * 致命耀魂抵挡由 ProudSoulLethalAbsorbHelper 处理。
 */
@Setter
@Accessors(chain = true)
public class HumanEmperorDomainSpecialEffect extends ExtendedSpecialEffect {

    float damageAmplifier = 0.3f;
    float healPerTick = 0.5f;

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
        LivingEntity player = event.player;
        if (player.level().isClientSide()) {
            return;
        }
        if (!InventorySlashBladeSeHelper.hasInInventory(player, SpecialEffectsRegistry.HUMAN_EMPEROR_DOMAIN)) {
            return;
        }
        BuffSuppressHandler.dispelHarmful(player);
        player.heal(healPerTick);
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
}

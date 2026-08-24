package com.til.recasting.registry.se;

import com.til.recasting.event.DoSlashExtendEvent;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.neoforged.bus.api.SubscribeEvent;

/**
 * 吃
 * 挥刀时消耗耐久，恢复饱和度
 */
public class EatSpecialEffect extends ExtendedSpecialEffect {

    private final int durabilityCost = 12;
    private final float saturationRestore = 1f;

    @SubscribeEvent
    public void onEvent(DoSlashExtendEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        if (event.getUser().level().isClientSide()) {
            return;
        }

        if (!(event.getUser() instanceof Player player)) {
            return;
        }

        ISlashBladeState state = event.getSlashBladeState();
        int maxDamage = state.getMaxDamage();
        if (maxDamage <= 0) {
            return;
        }

        int currentDamage = state.getDamage();
        int remaining = maxDamage - currentDamage;
        if (remaining < durabilityCost) {
            return;
        }

        FoodData foodData = player.getFoodData();
        float newSaturation = Math.min(foodData.getFoodLevel(), foodData.getSaturationLevel() + saturationRestore);
        if (newSaturation <= foodData.getSaturationLevel()) {
            return;
        }

        state.setDamage(currentDamage + durabilityCost);
        foodData.setSaturation(newSaturation);
    }
}

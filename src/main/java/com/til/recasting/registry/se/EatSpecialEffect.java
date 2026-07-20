package com.til.recasting.registry.se;

import com.til.recasting.event.DoSlashExtendEvent;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 吃
 * 挥刀时消耗耐久，恢复饱和度
 */
public class EatSpecialEffect extends ExtendedSpecialEffect {

    private static final int DURABILITY_COST = 12;
    private static final float SATURATION_RESTORE = 1f;

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
        if (remaining < DURABILITY_COST) {
            return;
        }

        FoodData foodData = player.getFoodData();
        float newSaturation = Math.min(foodData.getFoodLevel(), foodData.getSaturationLevel() + SATURATION_RESTORE);
        if (newSaturation <= foodData.getSaturationLevel()) {
            return;
        }

        state.setDamage(currentDamage + DURABILITY_COST);
        foodData.setSaturation(newSaturation);
    }
}

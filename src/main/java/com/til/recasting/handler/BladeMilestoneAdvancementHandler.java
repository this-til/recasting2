package com.til.recasting.handler;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.til.recasting.Recasting.MODID;

/**
 * 击杀 / 精炼里程碑达成时主动触发背包变化判定，解锁对应成就。
 */
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BladeMilestoneAdvancementHandler {

    private static final int KILL_MILESTONE_1 = 1000;
    private static final int KILL_MILESTONE_2 = 10000;
    private static final int KILL_MILESTONE_3 = 100000;
    private static final int KILL_MILESTONE_4 = 1000000;
    private static final int REFINE_MILESTONE_1 = 1000;
    private static final int REFINE_MILESTONE_2 = 10000;

    private BladeMilestoneAdvancementHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack blade = player.getMainHandItem();
        if (blade.isEmpty() || !(blade.getItem() instanceof ItemSlashBlade)) {
            return;
        }
        blade.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
            int killCount = state.getKillCount();
            if (killCount > KILL_MILESTONE_1
                    || killCount > KILL_MILESTONE_2
                    || killCount > KILL_MILESTONE_3
                    || killCount > KILL_MILESTONE_4) {
                triggerInventoryChanged(player, blade);
            }
        });
    }

    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack output = event.getOutput();
        if (output.isEmpty() || !(output.getItem() instanceof ItemSlashBlade)) {
            return;
        }
        int before = event.getLeft().getCapability(ItemSlashBlade.BLADESTATE)
                .map(ISlashBladeState::getRefine)
                .orElse(0);
        int after = output.getCapability(ItemSlashBlade.BLADESTATE)
                .map(ISlashBladeState::getRefine)
                .orElse(0);
        if (before >= after) {
            return;
        }
        if (after > REFINE_MILESTONE_1 || after > REFINE_MILESTONE_2) {
            triggerInventoryChanged(player, output);
        }
    }

    private static void triggerInventoryChanged(ServerPlayer player, ItemStack stack) {
        CriteriaTriggers.INVENTORY_CHANGED.trigger(player, player.getInventory(), stack);
    }
}

package com.til.recasting.handler;

import com.til.recasting.Recasting;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;

/**
 * 击杀 / 精炼里程碑达成时主动触发背包变化判定，解锁对应成就。
 */
@EventBusSubscriber(modid = Recasting.MODID)
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
        BladeStateAccess.of(blade).ifPresent(state -> {
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
        int before = BladeStateAccess.of(event.getLeft())
                .map(ISlashBladeState::getRefine)
                .orElse(0);
        int after = BladeStateAccess.of(output)
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

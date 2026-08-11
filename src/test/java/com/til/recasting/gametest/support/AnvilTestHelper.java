package com.til.recasting.gametest.support;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 通过直投 {@link AnvilUpdateEvent} 验证铁砧预览产出。
 */
public final class AnvilTestHelper {

    private AnvilTestHelper() {
    }

    public static ItemStack preview(ItemStack left, ItemStack right, @Nullable Player player) {
        AnvilUpdateEvent event = new AnvilUpdateEvent(left.copy(), right.copy(), "", 0, player);
        MinecraftForge.EVENT_BUS.post(event);
        ItemStack output = event.getOutput();
        return output == null ? ItemStack.EMPTY : output;
    }

    /**
     * 模拟铁砧取出物品，触发 AnvilRepairEvent 及其监听器（含成就逻辑）。
     */
    public static void simulateRepair(ServerPlayer player, ItemStack left, ItemStack right, ItemStack output) {
        AnvilRepairEvent event = new AnvilRepairEvent(player, left, right, output);
        MinecraftForge.EVENT_BUS.post(event);
    }

    /**
     * 检查玩家是否已获得指定成就。
     */
    public static boolean hasAdvancement(ServerPlayer player, ResourceLocation advancementId) {
        Advancement advancement = player.server.getAdvancements().getAdvancement(advancementId);
        if (advancement == null) {
            return false;
        }
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        return progress.isDone();
    }

    public static void assertHasOutput(ItemStack output, String label) {
        if (output == null || output.isEmpty()) {
            throw new AssertionError("Expected anvil output for: " + label);
        }
    }

    public static void assertNoOutput(ItemStack output, String label) {
        if (output != null && !output.isEmpty()) {
            throw new AssertionError("Expected no anvil output for: " + label + " but got " + output);
        }
    }
}

package com.til.recasting.gametest.support;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
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

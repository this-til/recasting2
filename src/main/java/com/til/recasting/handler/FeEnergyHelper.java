package com.til.recasting.handler;

import com.til.recasting.constant.RecastingLanguageKeys;
import com.til.recasting.energy.FeBladeEnergyStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

/**
 * 拔刀剑 FE 读写与 tooltip 辅助。
 */
public final class FeEnergyHelper {

    private FeEnergyHelper() {
    }

    private static FeBladeEnergyStorage storage(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy instanceof FeBladeEnergyStorage fe) {
            return fe;
        }
        return new FeBladeEnergyStorage(stack);
    }

    public static long getCapacity(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0L;
        }
        return storage(stack).resolveCapacity();
    }

    public static long getStored(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0L;
        }
        return storage(stack).getEnergyLong();
    }

    /**
     * @return 是否抽出了足额能量
     */
    public static boolean tryExtract(ItemStack stack, long amount, boolean simulate) {
        if (stack == null || stack.isEmpty() || amount <= 0L) {
            return false;
        }
        return storage(stack).extractEnergyLong(amount, simulate) >= amount;
    }

    /**
     * @return 实际接收量
     */
    public static long receive(ItemStack stack, long amount, boolean simulate) {
        if (stack == null || stack.isEmpty() || amount <= 0L) {
            return 0L;
        }
        return storage(stack).receiveEnergyLong(amount, simulate);
    }

    /**
     * @return 是否成功写入
     */
    public static boolean setStored(ItemStack stack, long amount) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        long capacity = getCapacity(stack);
        if (capacity <= 0L) {
            return false;
        }
        storage(stack).setEnergyLong(Math.max(0L, Math.min(capacity, amount)));
        return true;
    }

    public static boolean fillToCapacity(ItemStack stack) {
        long capacity = getCapacity(stack);
        if (capacity <= 0L) {
            return false;
        }
        return setStored(stack, capacity);
    }

    public static boolean hasFeCapacity(ItemStack stack) {
        return getCapacity(stack) > 0L;
    }

    public static int getBarWidth(ItemStack stack) {
        long capacity = getCapacity(stack);
        if (capacity <= 0L) {
            return 0;
        }
        long stored = getStored(stack);
        return (int) Math.round(13.0d * ((double) stored / (double) capacity));
    }

    public static final int FE_BAR_COLOR = 0xFFAA00;

    public static String formatEnergy(long value) {
        if (value >= 1_000_000_000_000L) {
            return formatScaled(value, 1_000_000_000_000L, "T");
        }
        if (value >= 1_000_000_000L) {
            return formatScaled(value, 1_000_000_000L, "G");
        }
        if (value >= 1_000_000L) {
            return formatScaled(value, 1_000_000L, "M");
        }
        if (value >= 1_000L) {
            return formatScaled(value, 1_000L, "K");
        }
        return Long.toString(value);
    }

    private static String formatScaled(long value, long unit, String suffix) {
        long whole = value / unit;
        long frac = (value % unit) * 10 / unit;
        if (frac <= 0L) {
            return whole + suffix;
        }
        return whole + "." + frac + suffix;
    }

    @OnlyIn(Dist.CLIENT)
    public static void appendTooltip(ItemStack stack, List<Component> tooltip) {
        long capacity = getCapacity(stack);
        if (capacity <= 0L) {
            return;
        }
        long stored = getStored(stack);
        Component energyTip = Component.literal(formatEnergy(stored) + " / " + formatEnergy(capacity))
                .withStyle(ChatFormatting.GOLD);
        tooltip.add(Component.translatable(RecastingLanguageKeys.TOOLTIP_FE_ENERGY_INFO, energyTip)
                .withStyle(ChatFormatting.GRAY));
    }
}

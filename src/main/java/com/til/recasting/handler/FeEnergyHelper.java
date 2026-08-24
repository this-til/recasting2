package com.til.recasting.handler;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.constant.RecastingLanguageKeys;
import com.til.recasting.energy.FeBladeEnergyStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 拔刀剑 FE 读写与 tooltip 辅助。
 */
public final class FeEnergyHelper {

    private FeEnergyHelper() {
    }

    public static long getCapacity(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0L;
        }
        return stack.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                .map(PropertiesDefinitionExtension::feCapacity)
                .orElse(0L);
    }

    public static long getStored(ItemStack stack) {
        AtomicLong stored = new AtomicLong(0L);
        if (stack == null || stack.isEmpty()) {
            return 0L;
        }
        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
            if (energy instanceof FeBladeEnergyStorage fe) {
                stored.set(fe.getEnergyLong());
            }
        });
        return stored.get();
    }

    /**
     * @return 是否抽出了足额能量
     */
    public static boolean tryExtract(ItemStack stack, long amount, boolean simulate) {
        if (stack == null || stack.isEmpty() || amount <= 0L) {
            return false;
        }
        AtomicBoolean success = new AtomicBoolean(false);
        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
            if (!(energy instanceof FeBladeEnergyStorage fe)) {
                return;
            }
            success.set(fe.extractEnergyLong(amount, simulate) >= amount);
        });
        return success.get();
    }

    /**
     * @return 实际接收量
     */
    public static long receive(ItemStack stack, long amount, boolean simulate) {
        if (stack == null || stack.isEmpty() || amount <= 0L) {
            return 0L;
        }
        AtomicLong accepted = new AtomicLong(0L);
        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
            if (!(energy instanceof FeBladeEnergyStorage fe)) {
                return;
            }
            accepted.set(fe.receiveEnergyLong(amount, simulate));
        });
        return accepted.get();
    }

    /**
     * 将 FE 设为指定值（钳制在 0～容量）。
     *
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
        AtomicBoolean success = new AtomicBoolean(false);
        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
            if (!(energy instanceof FeBladeEnergyStorage fe)) {
                return;
            }
            fe.setEnergyLong(Math.max(0L, Math.min(capacity, amount)));
            success.set(true);
        });
        return success.get();
    }

    /**
     * 将 FE 填满至容量。
     */
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

package com.til.recasting.client.handler;

import com.til.recasting.Config;
import com.til.recasting.Recasting;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.client.SlashBladeItemBarDecorator;
import com.til.recasting.event.SlashBladeItemBarCollectEvent;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.FeEnergyHelper;
import com.til.recasting.handler.InventorySlashBladeSeHelper;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.SpecialEffectsRegistry;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.registry.sa.EternalGuardSlashArts;
import com.til.recasting.registry.sa.JadeDomainSlashArts;
import com.til.recasting.registry.sa.MatrixSlashArts;
import com.til.recasting.registry.sa.MyriadSilenceSlashArts;
import com.til.recasting.registry.sa.PhenomenalReturnSlashArts;
import com.til.recasting.registry.sa.StarfallSlashArts;
import com.til.recasting.registry.sa.TimeBeyondSlashArts;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 注册统筹物品栏进度条装饰器，并向收集事件注入耐久 / FE / 电涌 / SA 自身状态条。
 */
public final class SlashBladeItemBarDecorationHandler {

    private static final int ELECTRIC_SURGE_BAR_COLOR = 0x55FFFF;
    private static final int JADE_DOMAIN_BAR_COLOR = 0x66FFAA;
    private static final int CURSE_DECREE_BAR_COLOR = 0xA5527B;
    private static final int BUFF_SUPPRESS_BAR_COLOR = 0xC0C0C0;
    private static final int MATRIX_BAR_COLOR = 0x4488FF;
    private static final int STARFALL_BAR_COLOR = 0xAACCFF;
    private static final int TIME_BEYOND_BAR_COLOR = 0xFFD060;
    private static final int ETERNAL_GUARD_BAR_COLOR = 0x3A6BFF;

    private SlashBladeItemBarDecorationHandler() {
    }

    @Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ModBus {
        private ModBus() {
        }

        @SubscribeEvent
        public static void onRegisterItemDecorations(RegisterItemDecorationsEvent event) {
            for (Item item : ForgeRegistries.ITEMS) {
                if (item instanceof ItemSlashBlade) {
                    event.register(item, SlashBladeItemBarDecorator.INSTANCE);
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static final class ForgeBus {
        private ForgeBus() {
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void injectDurability(SlashBladeItemBarCollectEvent event) {
            if (!Config.SHOW_SLASHBLADE_DURABILITY_BAR.get()) {
                return;
            }

            ItemStack stack = event.getStack();
            int maxDamage = stack.getMaxDamage();
            int damage = stack.getDamageValue();
            if (maxDamage <= 0 || damage <= 0) {
                return;
            }

            float remain = 1.0f - (float) damage / (float) maxDamage;
            int color = Mth.hsvToRgb(Math.max(0.0f, remain) / 3.0f, 1.0f, 1.0f);
            event.addBar(color, remain);
        }

        @SubscribeEvent(priority = EventPriority.NORMAL)
        public static void injectFe(SlashBladeItemBarCollectEvent event) {
            ItemStack stack = event.getStack();
            if (!FeEnergyHelper.hasFeCapacity(stack)) {
                return;
            }

            long capacity = FeEnergyHelper.getCapacity(stack);
            if (capacity <= 0L) {
                return;
            }

            long stored = FeEnergyHelper.getStored(stack);
            event.addBar(FeEnergyHelper.FE_BAR_COLOR, (float) ((double) stored / (double) capacity));
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void injectElectricSurge(SlashBladeItemBarCollectEvent event) {
            ItemStack stack = event.getStack();
            if (!InventorySlashBladeSeHelper.hasSpecialEffect(stack, SpecialEffectsRegistry.FOCUSED_ENERGY_BLADE)) {
                return;
            }

            Player player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }

            IBuffStackData buffStackData = player.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).orElse(null);
            if (buffStackData == null) {
                return;
            }

            BuffType electricSurge = RecastingBuffTypes.ELECTRIC_SURGE.get();
            int level = buffStackData.getLevel(electricSurge, player.level());
            if (level <= 0) {
                return;
            }

            int maxLevel = Math.max(1, electricSurge.getMaxLevel());
            event.addBar(ELECTRIC_SURGE_BAR_COLOR, (float) level / (float) maxLevel);
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void injectSaSelfDurationBars(SlashBladeItemBarCollectEvent event) {
            ItemStack stack = event.getStack();
            Player player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }

            ISlashBladeState bladeState = stack.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
            if (bladeState == null) {
                return;
            }

            SlashArts arts = bladeState.getSlashArts();
            if (arts == null) {
                return;
            }

            IBuffStackData buffStackData = player.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).orElse(null);
            if (buffStackData == null) {
                return;
            }

            if (arts instanceof JadeDomainSlashArts jadeDomain) {
                addBuffBar(
                        event,
                        buffStackData,
                        player,
                        RecastingBuffTypes.JADE_DOMAIN.get(),
                        jadeDomain.getDomainDuration(),
                        JADE_DOMAIN_BAR_COLOR
                );
            }
            if (arts instanceof MyriadSilenceSlashArts myriadSilence) {
                addBuffBar(
                        event,
                        buffStackData,
                        player,
                        RecastingBuffTypes.CURSE_DECREE.get(),
                        myriadSilence.getDecreeSeconds() * 20,
                        CURSE_DECREE_BAR_COLOR
                );
            }
            if (arts instanceof PhenomenalReturnSlashArts phenomenalReturn) {
                addBuffBar(
                        event,
                        buffStackData,
                        player,
                        RecastingBuffTypes.BUFF_SUPPRESS.get(),
                        phenomenalReturn.getSuppressSeconds() * 20,
                        BUFF_SUPPRESS_BAR_COLOR
                );
            }
            if (arts instanceof MatrixSlashArts matrix) {
                addBuffBar(
                        event,
                        buffStackData,
                        player,
                        RecastingBuffTypes.MATRIX.get(),
                        matrix.getLife(),
                        MATRIX_BAR_COLOR
                );
            }
            if (arts instanceof StarfallSlashArts starfall) {
                addBuffBar(
                        event,
                        buffStackData,
                        player,
                        RecastingBuffTypes.STARFALL.get(),
                        starfall.getLife(),
                        STARFALL_BAR_COLOR
                );
            }
            if (arts instanceof TimeBeyondSlashArts) {
                BuffType charge = RecastingBuffTypes.TIME_BEYOND_CHARGE.get();
                int level = buffStackData.getLevel(charge, player.level());
                if (level > 0) {
                    event.addBar(TIME_BEYOND_BAR_COLOR, (float) level / (float) Math.max(1, TimeBeyondSlashArts.MAX_CHARGE_TICKS));
                }
            }
            if (arts instanceof EternalGuardSlashArts eternalGuard) {
                addBuffBar(
                        event,
                        buffStackData,
                        player,
                        RecastingBuffTypes.ETERNAL_GUARD.get(),
                        eternalGuard.getDurationSeconds() * 20,
                        ETERNAL_GUARD_BAR_COLOR
                );
            }
        }

        private static void addBuffBar(
                SlashBladeItemBarCollectEvent event,
                IBuffStackData buffStackData,
                Player player,
                BuffType buffType,
                int durationTicks,
                int colorRgb
        ) {
            int level = buffStackData.getLevel(buffType, player.level());
            if (level <= 0) {
                return;
            }
            event.addBar(colorRgb, (float) level / (float) Math.max(1, durationTicks));
        }
    }
}

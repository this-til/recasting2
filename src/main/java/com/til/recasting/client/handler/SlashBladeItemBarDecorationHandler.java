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
import com.til.recasting.registry.SlashArtsRegistry;
import com.til.recasting.registry.SpecialEffectsRegistry;
import com.til.recasting.registry.instance.BuffType;
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
import net.minecraftforge.registries.RegistryObject;

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

    /** 云界默认时长 tick（30s） */
    private static final int JADE_DOMAIN_DURATION_TICKS = 20 * 30;
    /** 万灵寂灭 λ 咒令 tick（39s，覆盖基版 30s） */
    private static final int CURSE_DECREE_DURATION_TICKS = 39 * 20;
    /** 万象归元 λ 压制 tick（12s，覆盖基版 9s） */
    private static final int BUFF_SUPPRESS_DURATION_TICKS = 12 * 20;
    /** 穷观阵默认寿命 tick */
    private static final int MATRIX_DURATION_TICKS = 200;
    /** 群星坠落默认寿命 tick */
    private static final int STARFALL_DURATION_TICKS = 600;
    /** 永恒守卫默认时长 tick（25s） */
    private static final int ETERNAL_GUARD_DURATION_TICKS = 25 * 20;

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

            if (matchesSlashArts(arts, SlashArtsRegistry.JADE_DOMAIN, SlashArtsRegistry.JADE_DOMAIN_LAMBDA)) {
                addBuffBar(
                        event,
                        buffStackData,
                        player,
                        RecastingBuffTypes.JADE_DOMAIN.get(),
                        JADE_DOMAIN_DURATION_TICKS,
                        JADE_DOMAIN_BAR_COLOR
                );
            }
            if (matchesSlashArts(arts, SlashArtsRegistry.MYRIAD_SILENCE, SlashArtsRegistry.MYRIAD_SILENCE_LAMBDA)) {
                addBuffBar(
                        event,
                        buffStackData,
                        player,
                        RecastingBuffTypes.CURSE_DECREE.get(),
                        CURSE_DECREE_DURATION_TICKS,
                        CURSE_DECREE_BAR_COLOR
                );
            }
            if (matchesSlashArts(arts, SlashArtsRegistry.PHENOMENAL_RETURN, SlashArtsRegistry.PHENOMENAL_RETURN_LAMBDA)) {
                addBuffBar(
                        event,
                        buffStackData,
                        player,
                        RecastingBuffTypes.BUFF_SUPPRESS.get(),
                        BUFF_SUPPRESS_DURATION_TICKS,
                        BUFF_SUPPRESS_BAR_COLOR
                );
            }
            if (matchesSlashArts(arts, SlashArtsRegistry.MATRIX, SlashArtsRegistry.MATRIX_LAMBDA)) {
                addBuffBar(
                        event,
                        buffStackData,
                        player,
                        RecastingBuffTypes.MATRIX.get(),
                        MATRIX_DURATION_TICKS,
                        MATRIX_BAR_COLOR
                );
            }
            if (matchesSlashArts(arts, SlashArtsRegistry.STARFALL)) {
                addBuffBar(
                        event,
                        buffStackData,
                        player,
                        RecastingBuffTypes.STARFALL.get(),
                        STARFALL_DURATION_TICKS,
                        STARFALL_BAR_COLOR
                );
            }
            if (matchesSlashArts(arts, SlashArtsRegistry.TIME_BEYOND)) {
                BuffType charge = RecastingBuffTypes.TIME_BEYOND_CHARGE.get();
                int level = buffStackData.getLevel(charge, player.level());
                if (level > 0) {
                    int denom = Math.max(1, TimeBeyondSlashArts.MAX_CHARGE_TICKS);
                    event.addBar(TIME_BEYOND_BAR_COLOR, (float) level / (float) denom);
                }
            }
            if (matchesSlashArts(arts, SlashArtsRegistry.ETERNAL_GUARD)) {
                addBuffBar(
                        event,
                        buffStackData,
                        player,
                        RecastingBuffTypes.ETERNAL_GUARD.get(),
                        ETERNAL_GUARD_DURATION_TICKS,
                        ETERNAL_GUARD_BAR_COLOR
                );
            }
        }

        @SafeVarargs
        private static boolean matchesSlashArts(SlashArts arts, RegistryObject<? extends SlashArts>... candidates) {
            for (RegistryObject<? extends SlashArts> candidate : candidates) {
                if (arts == candidate.get()) {
                    return true;
                }
            }
            return false;
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

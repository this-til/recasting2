package com.til.recasting.handler;

import com.til.recasting.Config;
import com.til.recasting.Recasting;
import com.til.recasting.inventory.ProudSoulBagMenu;
import com.til.recasting.registry.requir.SlashBladeItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * 手持拔刀剑击败生物时，按配置概率直接给予玩家耀魂相关物品。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ProudSoulDropHandler {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        ItemStack blade = player.getMainHandItem();
        if (blade.isEmpty() || !(blade.getItem() instanceof ItemSlashBlade)) {
            return;
        }

        RandomSource random = event.getEntity().getRandom();

        if (random.nextDouble() < Config.PROUD_SOUL_TINY_DROP_CHANCE.get()) {
            giveProudSoul(player, new ItemStack(SlashBladeItems.PROUDSOUL_TINY.get()));
        }

        if (random.nextDouble() < Config.PROUD_SOUL_DROP_CHANCE.get()) {
            giveProudSoul(player, new ItemStack(SlashBladeItems.PROUDSOUL.get()));
        }

        if (random.nextDouble() < Config.PROUD_SOUL_ENCHANTED_TINY_DROP_CHANCE.get()) {
            ItemStack enchantedTiny = createEnchantedTinyProudSoul(random);
            if (!enchantedTiny.isEmpty()) {
                giveProudSoul(player, enchantedTiny);
            }
        }
    }

    private static void giveProudSoul(Player player, ItemStack stack) {
        boolean inserted = ProudSoulBagHelper.tryInsertIntoAnyBag(player, stack);
        if (inserted && player.containerMenu instanceof ProudSoulBagMenu menu) {
            menu.syncContentsToClient(player);
        }
        if (!stack.isEmpty()) {
            ItemHandlerHelper.giveItemToPlayer(player, stack);
        }
    }

    /**
     * 生成携带随机附魔的破碎的耀魂。
     */
    private static ItemStack createEnchantedTinyProudSoul(RandomSource random) {
        List<Enchantment> enchantments = new ArrayList<>();
        for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS) {
            if (enchantment != null) {
                enchantments.add(enchantment);
            }
        }
        if (enchantments.isEmpty()) {
            return ItemStack.EMPTY;
        }

        Enchantment chosen = enchantments.get(random.nextInt(enchantments.size()));
        ItemStack stack = new ItemStack(SlashBladeItems.PROUDSOUL_TINY.get());
        int configuredLevel = Math.max(1, Config.PROUD_SOUL_ENCHANTED_TINY_LEVEL.get());
        int enchantLevel = configuredLevel;
        if (!Config.PROUD_SOUL_ENCHANTMENT_IGNORE_MAX_LEVEL.get()) {
            enchantLevel = Math.min(configuredLevel, chosen.getMaxLevel());
        }
        stack.enchant(chosen, enchantLevel);
        return stack;
    }
}

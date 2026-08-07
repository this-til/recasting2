package com.til.recasting.handler;

import com.til.recasting.Config;
import com.til.recasting.Recasting;
import com.til.recasting.inventory.ProudSoulBagMenu;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.requir.SlashBladeItems;
import com.til.recasting.registry.se.ExtendedSpecialEffect;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 手持拔刀剑击败生物时，按配置概率直接给予玩家耀魂相关物品。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ProudSoulDropHandler {

    private static final int SE_CRYSTAL_DROP_LEVEL = 1;

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

        if (random.nextDouble() < Config.BASIC_FLAME_DROP_CHANCE.get()) {
            ItemStack flame = createRandomItem(RecastingItems.getAllFlame(), random);
            if (!flame.isEmpty()) {
                giveProudSoul(player, flame);
            }
        }

        if (random.nextDouble() < Config.SOUL_CUBE_DROP_CHANCE.get()) {
            ItemStack cube = createRandomItem(RecastingItems.getAllSoulCube(), random);
            if (!cube.isEmpty()) {
                giveProudSoul(player, cube);
            }
        }

        if (random.nextDouble() < Config.SE_CRYSTAL_LEVEL_1_DROP_CHANCE.get()) {
            ItemStack seCrystal = createRandomLevel1SeCrystal(random);
            if (!seCrystal.isEmpty()) {
                giveProudSoul(player, seCrystal);
            }
        }

        if (random.nextDouble() < Config.SLASH_ARTS_DROP_CHANCE.get()) {
            ItemStack slashArtsSphere = createRandomSlashArtsSphere(random);
            if (!slashArtsSphere.isEmpty()) {
                giveProudSoul(player, slashArtsSphere);
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

    private static ItemStack createRandomItem(List<RegistryObject<Item>> items, RandomSource random) {
        if (items.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(items.get(random.nextInt(items.size())).get());
    }

    private static ItemStack createRandomLevel1SeCrystal(RandomSource random) {
        List<ExtendedSpecialEffect> candidates = new ArrayList<>();
        for (SpecialEffect specialEffect : mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getValues()) {
            if (!(specialEffect instanceof ExtendedSpecialEffect extended)) {
                continue;
            }
            if (extended.isSpecial()) {
                continue;
            }
            if (extended.getMaxLevel() < SE_CRYSTAL_DROP_LEVEL) {
                continue;
            }
            candidates.add(extended);
        }
        if (candidates.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ExtendedSpecialEffect chosen = candidates.get(random.nextInt(candidates.size()));
        ResourceLocation seKey = Objects.requireNonNull(
                mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getKey(chosen)
        );

        ItemStack stack = new ItemStack(RecastingItems.SE_CRYSTAL.get());
        stack.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA).ifPresent(data -> {
            data.setSpecialEffectType(seKey);
            data.setSpecialEffectLevel(SE_CRYSTAL_DROP_LEVEL);
        });
        return stack;
    }

    private static ItemStack createRandomSlashArtsSphere(RandomSource random) {
        List<ResourceLocation> candidates = new ArrayList<>();
        for (SlashArts slashArts : mods.flammpfeil.slashblade.registry.SlashArtsRegistry.REGISTRY.get().getValues()) {
            if (slashArts == null) {
                continue;
            }
            if (slashArts.equals(mods.flammpfeil.slashblade.registry.SlashArtsRegistry.NONE.get())) {
                continue;
            }
            ResourceLocation key = mods.flammpfeil.slashblade.registry.SlashArtsRegistry.REGISTRY.get().getKey(slashArts);
            if (key != null) {
                candidates.add(key);
            }
        }
        if (candidates.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = new ItemStack(SlashBladeItems.PROUDSOUL_SPHERE.get());
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("SpecialAttackType", candidates.get(random.nextInt(candidates.size())).toString());
        return stack;
    }
}

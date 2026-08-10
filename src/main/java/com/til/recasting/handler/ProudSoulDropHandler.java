package com.til.recasting.handler;

import com.til.recasting.Config;
import com.til.recasting.Recasting;
import com.til.recasting.capability.IProudSoulDropCooldown;
import com.til.recasting.capability.IProudSoulDropCooldown.DropKind;
import com.til.recasting.capability.provider.ProudSoulDropCooldownProvider;
import com.til.recasting.constant.RecastingLanguageKeys;
import com.til.recasting.inventory.ProudSoulBagMenu;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.requir.SlashBladeItems;
import com.til.recasting.registry.se.ExtendedSpecialEffect;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player)) {
            return;
        }
        ProudSoulDropCooldownProvider provider = new ProudSoulDropCooldownProvider();
        event.addCapability(Recasting.prefix("proud_soul_drop_cooldown"), provider);
        event.addListener(provider::invalidate);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        LazyOptional<IProudSoulDropCooldown> oldOptional =
                event.getOriginal().getCapability(CapabilityRegistryHandler.PROUD_SOUL_DROP_COOLDOWN);
        LazyOptional<IProudSoulDropCooldown> newOptional =
                event.getEntity().getCapability(CapabilityRegistryHandler.PROUD_SOUL_DROP_COOLDOWN);
        oldOptional.ifPresent(oldData -> newOptional.ifPresent(newData -> newData.copyFrom(oldData)));
        event.getOriginal().invalidateCaps();
    }

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
        long gameTime = event.getEntity().level().getGameTime();

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

        player.getCapability(CapabilityRegistryHandler.PROUD_SOUL_DROP_COOLDOWN).ifPresent(cooldown -> {
            if (cooldown.isReady(DropKind.BASIC_FLAME, gameTime, Config.BASIC_FLAME_DROP_COOLDOWN_TICKS.get())
                    && random.nextDouble() < Config.BASIC_FLAME_DROP_CHANCE.get()) {
                ItemStack flame = createRandomItem(RecastingItems.getAllFlame(), random);
                if (!flame.isEmpty()) {
                    notifyCooldownDrop(player, flame, DropKind.BASIC_FLAME, Config.BASIC_FLAME_DROP_COOLDOWN_TICKS.get());
                    giveProudSoul(player, flame);
                    cooldown.mark(DropKind.BASIC_FLAME, gameTime);
                }
            }

            if (cooldown.isReady(DropKind.SOUL_CUBE, gameTime, Config.SOUL_CUBE_DROP_COOLDOWN_TICKS.get())
                    && random.nextDouble() < Config.SOUL_CUBE_DROP_CHANCE.get()) {
                ItemStack cube = createRandomItem(RecastingItems.getAllSoulCube(), random);
                if (!cube.isEmpty()) {
                    notifyCooldownDrop(player, cube, DropKind.SOUL_CUBE, Config.SOUL_CUBE_DROP_COOLDOWN_TICKS.get());
                    giveProudSoul(player, cube);
                    cooldown.mark(DropKind.SOUL_CUBE, gameTime);
                }
            }

            if (cooldown.isReady(DropKind.SE_CRYSTAL, gameTime, Config.SE_CRYSTAL_DROP_COOLDOWN_TICKS.get())
                    && random.nextDouble() < Config.SE_CRYSTAL_LEVEL_1_DROP_CHANCE.get()) {
                ItemStack seCrystal = createRandomWhitelistedSeCrystal(random);
                if (!seCrystal.isEmpty()) {
                    notifyCooldownDrop(player, seCrystal, DropKind.SE_CRYSTAL, Config.SE_CRYSTAL_DROP_COOLDOWN_TICKS.get());
                    giveProudSoul(player, seCrystal);
                    cooldown.mark(DropKind.SE_CRYSTAL, gameTime);
                }
            }

            if (cooldown.isReady(DropKind.SLASH_ARTS, gameTime, Config.SLASH_ARTS_DROP_COOLDOWN_TICKS.get())
                    && random.nextDouble() < Config.SLASH_ARTS_DROP_CHANCE.get()) {
                ItemStack slashArtsSphere = createRandomWhitelistedSlashArtsSphere(random);
                if (!slashArtsSphere.isEmpty()) {
                    notifyCooldownDrop(player, slashArtsSphere, DropKind.SLASH_ARTS, Config.SLASH_ARTS_DROP_COOLDOWN_TICKS.get());
                    giveProudSoul(player, slashArtsSphere);
                    cooldown.mark(DropKind.SLASH_ARTS, gameTime);
                }
            }
        });
    }

    private static void notifyCooldownDrop(Player player, ItemStack stack, DropKind kind, int cooldownTicks) {
        player.sendSystemMessage(Component.translatable(
                RecastingLanguageKeys.MESSAGE_PROUD_SOUL_DROP,
                describeDrop(stack),
                describeKind(kind),
                formatCooldown(cooldownTicks)
        ));
    }

    private static Component describeKind(DropKind kind) {
        return switch (kind) {
            case BASIC_FLAME -> Component.translatable(RecastingLanguageKeys.MESSAGE_PROUD_SOUL_DROP_KIND_BASIC_FLAME);
            case SOUL_CUBE -> Component.translatable(RecastingLanguageKeys.MESSAGE_PROUD_SOUL_DROP_KIND_SOUL_CUBE);
            case SE_CRYSTAL -> Component.translatable(RecastingLanguageKeys.MESSAGE_PROUD_SOUL_DROP_KIND_SE_CRYSTAL);
            case SLASH_ARTS -> Component.translatable(RecastingLanguageKeys.MESSAGE_PROUD_SOUL_DROP_KIND_SLASH_ARTS);
        };
    }

    private static Component describeDrop(ItemStack stack) {
        if (stack.is(RecastingItems.SE_CRYSTAL.get())) {
            return stack.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA)
                    .map(data -> {
                        if (!data.hasSpecialEffect()) {
                            return stack.getHoverName();
                        }
                        ResourceLocation seKey = data.getSpecialEffectType();
                        if (seKey == null) {
                            return stack.getHoverName();
                        }
                        SpecialEffect se = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getValue(seKey);
                        if (se == null) {
                            return stack.getHoverName();
                        }
                        return Component.translatable(
                                RecastingLanguageKeys.MESSAGE_PROUD_SOUL_DROP_SE,
                                Component.translatable(se.getDescriptionId()),
                                data.getSpecialEffectLevel(),
                                stack.getHoverName()
                        );
                    })
                    .orElseGet(stack::getHoverName);
        }

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("SpecialAttackType")) {
            ResourceLocation saKey = ResourceLocation.tryParse(tag.getString("SpecialAttackType"));
            if (saKey != null) {
                SlashArts sa = mods.flammpfeil.slashblade.registry.SlashArtsRegistry.REGISTRY.get().getValue(saKey);
                if (sa != null) {
                    return Component.translatable(
                            RecastingLanguageKeys.MESSAGE_PROUD_SOUL_DROP_SA,
                            Component.translatable(sa.getDescriptionId()),
                            stack.getHoverName()
                    );
                }
            }
        }
        return stack.getHoverName();
    }

    private static Component formatCooldown(int cooldownTicks) {
        if (cooldownTicks <= 0) {
            return Component.translatable(RecastingLanguageKeys.MESSAGE_PROUD_SOUL_DROP_COOLDOWN_NOW);
        }
        int totalSeconds = (cooldownTicks + 19) / 20;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        if (hours > 0) {
            if (minutes == 0) {
                return Component.translatable(RecastingLanguageKeys.MESSAGE_PROUD_SOUL_DROP_COOLDOWN_H, hours);
            }
            return Component.translatable(RecastingLanguageKeys.MESSAGE_PROUD_SOUL_DROP_COOLDOWN_HM, hours, minutes);
        }
        if (minutes > 0) {
            if (seconds == 0) {
                return Component.translatable(RecastingLanguageKeys.MESSAGE_PROUD_SOUL_DROP_COOLDOWN_M, minutes);
            }
            return Component.translatable(RecastingLanguageKeys.MESSAGE_PROUD_SOUL_DROP_COOLDOWN_MS, minutes, seconds);
        }
        return Component.translatable(RecastingLanguageKeys.MESSAGE_PROUD_SOUL_DROP_COOLDOWN_S, seconds);
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
        for(Enchantment enchantment : ForgeRegistries.ENCHANTMENTS) {
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

    private static ItemStack createRandomWhitelistedSeCrystal(RandomSource random) {
        List<ResourceLocation> whitelist = parseWhitelist(Config.SE_CRYSTAL_DROP_WHITELIST.get());
        if (whitelist.isEmpty()) {
            return ItemStack.EMPTY;
        }

        List<ExtendedSpecialEffect> candidates = new ArrayList<>();
        for(ResourceLocation key : whitelist) {
            SpecialEffect specialEffect = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getValue(key);
            if (!(specialEffect instanceof ExtendedSpecialEffect extended)) {
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

    private static ItemStack createRandomWhitelistedSlashArtsSphere(RandomSource random) {
        List<ResourceLocation> whitelist = parseWhitelist(Config.SLASH_ARTS_DROP_WHITELIST.get());
        if (whitelist.isEmpty()) {
            return ItemStack.EMPTY;
        }

        List<ResourceLocation> candidates = new ArrayList<>();
        for(ResourceLocation key : whitelist) {
            SlashArts slashArts = mods.flammpfeil.slashblade.registry.SlashArtsRegistry.REGISTRY.get().getValue(key);
            if (slashArts == null) {
                continue;
            }
            if (slashArts.equals(mods.flammpfeil.slashblade.registry.SlashArtsRegistry.NONE.get())) {
                continue;
            }
            candidates.add(key);
        }
        if (candidates.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = new ItemStack(SlashBladeItems.PROUDSOUL_SPHERE.get());
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("SpecialAttackType", candidates.get(random.nextInt(candidates.size())).toString());
        return stack;
    }

    private static List<ResourceLocation> parseWhitelist(List<? extends String> entries) {
        List<ResourceLocation> result = new ArrayList<>();
        for(String entry : entries) {
            if (entry == null || entry.isBlank()) {
                continue;
            }
            ResourceLocation key = ResourceLocation.tryParse(entry.trim());
            if (key != null) {
                result.add(key);
            }
        }
        return result;
    }
}

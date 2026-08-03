package com.til.recasting.handler;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * 扫描玩家背包中的拔刀剑 SE。
 * <p>
 * 按玩家 + SE id 缓存上次命中的槽位，命中时先校验缓存槽，失效再全量扫描。
 */
public final class InventorySlashBladeSeHelper {

    public record BladeSeHit(ItemStack blade, ISlashBladeState state, int slot) {
    }

    /** playerUUID -> (effectId -> inventory slot) */
    private static final Map<UUID, Map<ResourceLocation, Integer>> SLOT_CACHE = new ConcurrentHashMap<>();

    private InventorySlashBladeSeHelper() {
    }

    public static boolean hasSpecialEffect(ItemStack blade, RegistryObject<? extends SpecialEffect> effect) {
        return hasSpecialEffect(blade, effect.getId());
    }

    public static boolean hasSpecialEffect(ItemStack blade, ResourceLocation effectId) {
        if (blade == null || blade.isEmpty() || !(blade.getItem() instanceof ItemSlashBlade) || effectId == null) {
            return false;
        }
        ISlashBladeState state = blade.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
        return state != null && state.hasSpecialEffect(effectId);
    }

    public static boolean isHoldingSpecialEffect(LivingEntity entity, RegistryObject<? extends SpecialEffect> effect) {
        return hasSpecialEffect(entity.getMainHandItem(), effect);
    }

    @Nullable
    public static BladeSeHit findFirstInInventory(LivingEntity entity, RegistryObject<? extends SpecialEffect> effect) {
        return findFirstInInventory(entity, effect.getId());
    }

    @Nullable
    public static BladeSeHit findFirstInInventory(LivingEntity entity, ResourceLocation effectId) {
        if (effectId == null) {
            return null;
        }
        if (!(entity instanceof Player player)) {
            ItemStack main = entity.getMainHandItem();
            if (hasSpecialEffect(main, effectId)) {
                ISlashBladeState state = main.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
                if (state != null) {
                    return new BladeSeHit(main, state, -1);
                }
            }
            return null;
        }

        Integer cachedSlot = getCachedSlot(player.getUUID(), effectId);
        if (cachedSlot != null) {
            BladeSeHit cachedHit = resolveSlot(player, cachedSlot, effectId);
            if (cachedHit != null) {
                return cachedHit;
            }
            clearCachedSlot(player.getUUID(), effectId);
        }

        int size = player.getInventory().getContainerSize();
        for (int i = 0; i < size; i++) {
            BladeSeHit hit = resolveSlot(player, i, effectId);
            if (hit != null) {
                putCachedSlot(player.getUUID(), effectId, i);
                return hit;
            }
        }

        clearCachedSlot(player.getUUID(), effectId);
        return null;
    }

    @Nullable
    private static BladeSeHit resolveSlot(Player player, int slot, ResourceLocation effectId) {
        if (slot < 0 || slot >= player.getInventory().getContainerSize()) {
            return null;
        }
        ItemStack stack = player.getInventory().getItem(slot);
        if (!hasSpecialEffect(stack, effectId)) {
            return null;
        }
        ISlashBladeState state = stack.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
        if (state == null) {
            return null;
        }
        return new BladeSeHit(stack, state, slot);
    }

    private static Integer getCachedSlot(UUID playerId, ResourceLocation effectId) {
        Map<ResourceLocation, Integer> byEffect = SLOT_CACHE.get(playerId);
        if (byEffect == null) {
            return null;
        }
        return byEffect.get(effectId);
    }

    private static void putCachedSlot(UUID playerId, ResourceLocation effectId, int slot) {
        SLOT_CACHE.computeIfAbsent(playerId, id -> new ConcurrentHashMap<>()).put(effectId, slot);
    }

    private static void clearCachedSlot(UUID playerId, ResourceLocation effectId) {
        Map<ResourceLocation, Integer> byEffect = SLOT_CACHE.get(playerId);
        if (byEffect == null) {
            return;
        }
        byEffect.remove(effectId);
        if (byEffect.isEmpty()) {
            SLOT_CACHE.remove(playerId, byEffect);
        }
    }

    /**
     * 玩家退出时清理缓存，避免泄漏。
     */
    public static void clearPlayerCache(UUID playerId) {
        if (playerId != null) {
            SLOT_CACHE.remove(playerId);
        }
    }

    public static boolean hasInInventory(LivingEntity entity, RegistryObject<? extends SpecialEffect> effect) {
        return findFirstInInventory(entity, effect) != null;
    }

    public static boolean hasInInventory(LivingEntity entity, ResourceLocation effectId) {
        return findFirstInInventory(entity, effectId) != null;
    }

    /**
     * 背包存在指定 SE，且该刀当前耀魂 &gt; 0。
     */
    public static boolean hasInInventoryWithProudSoul(LivingEntity entity, RegistryObject<? extends SpecialEffect> effect) {
        BladeSeHit hit = findFirstInInventory(entity, effect);
        return hit != null && hit.state().getProudSoulCount() > 0;
    }

    public static boolean hasInInventoryWithProudSoul(LivingEntity entity, ResourceLocation effectId) {
        BladeSeHit hit = findFirstInInventory(entity, effectId);
        return hit != null && hit.state().getProudSoulCount() > 0;
    }

    public static void forEachInventoryBlade(
            LivingEntity entity,
            RegistryObject<? extends SpecialEffect> effect,
            BiConsumer<ItemStack, ISlashBladeState> consumer
    ) {
        BladeSeHit hit = findFirstInInventory(entity, effect);
        if (hit != null) {
            consumer.accept(hit.blade(), hit.state());
        }
    }
}

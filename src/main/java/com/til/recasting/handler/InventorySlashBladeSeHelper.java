package com.til.recasting.handler;

import com.til.recasting.capability.InventorySlashBladeSeCache;
import com.til.recasting.registry.RecastingAttachments;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * 扫描活体实体背包 / 装备栏中的拔刀剑 SE。
 */
public final class InventorySlashBladeSeHelper {

    private static final int NEGATIVE_CACHE_TICKS = 40;
    private static final EquipmentSlot[] EQUIPMENT_SLOTS = EquipmentSlot.values();

    public record BladeSeHit(ItemStack blade, ISlashBladeState state, int slot, ResourceLocation effectId) {
    }

    private InventorySlashBladeSeHelper() {
    }

    public static boolean hasSpecialEffect(ItemStack blade, DeferredHolder<SpecialEffect, ? extends SpecialEffect> effect) {
        return hasSpecialEffect(blade, effect.getId());
    }

    public static boolean hasSpecialEffect(ItemStack blade, ResourceLocation effectId) {
        return getUsableState(blade, effectId) != null;
    }

    public static boolean isHoldingSpecialEffect(LivingEntity entity, DeferredHolder<SpecialEffect, ? extends SpecialEffect> effect) {
        return hasSpecialEffect(entity.getMainHandItem(), effect);
    }

    @Nullable
    public static BladeSeHit findFirstInInventory(LivingEntity entity, DeferredHolder<SpecialEffect, ? extends SpecialEffect> effect) {
        return findFirstInInventory(entity, effect.getId());
    }

    @Nullable
    public static BladeSeHit findFirstInInventory(LivingEntity entity, ResourceLocation effectId) {
        if (effectId == null) {
            return null;
        }
        return findFirstInInventory(entity, new ResourceLocation[]{effectId});
    }

    @Nullable
    public static BladeSeHit findFirstInInventory(LivingEntity entity, ResourceLocation... effectIdsInPriority) {
        List<ResourceLocation> effectIds = normalizeEffectIds(effectIdsInPriority);
        if (effectIds.isEmpty()) {
            return null;
        }

        InventorySlashBladeSeCache cache = RecastingAttachments.inventorySlashBladeSeCache(entity);
        long gameTime = entity.level().getGameTime();
        for(ResourceLocation effectId : effectIds) {
            InventorySlashBladeSeCache.CachedBlade cachedBlade = cache.getBlade(effectId);
            if (cachedBlade != null) {
                BladeSeHit hit = resolveCachedBlade(entity, cachedBlade, effectId);
                if (hit != null) {
                    return hit;
                }
                cache.remove(effectId);
                return scanInventory(entity, effectIds, cache, gameTime);
            }

            Long negativeUntil = cache.getNegativeUntilGameTime(effectId);
            if (negativeUntil == null) {
                return scanInventory(entity, effectIds, cache, gameTime);
            }
            if (gameTime < negativeUntil) {
                continue;
            }

            cache.remove(effectId);
            return scanInventory(entity, effectIds, cache, gameTime);
        }
        return null;
    }

    @Nullable
    private static BladeSeHit resolveCachedBlade(
            LivingEntity entity,
            InventorySlashBladeSeCache.CachedBlade cachedBlade,
            ResourceLocation effectId
    ) {
        ItemStack currentBlade = getStackAtSlot(entity, cachedBlade.slot());
        if (currentBlade != cachedBlade.blade()) {
            return null;
        }
        ISlashBladeState state = getUsableState(currentBlade, effectId);
        if (state == null) {
            return null;
        }
        return new BladeSeHit(currentBlade, state, cachedBlade.slot(), effectId);
    }

    @Nullable
    private static BladeSeHit scanInventory(
            LivingEntity entity,
            List<ResourceLocation> effectIds,
            InventorySlashBladeSeCache cache,
            long gameTime
    ) {
        Map<ResourceLocation, BladeSeHit> hits = new HashMap<>();
        int size = getSlotCount(entity);
        for(int slot = 0; slot < size; slot++) {
            ItemStack blade = getStackAtSlot(entity, slot);
            ISlashBladeState state = getUsableState(blade);
            if (state == null) {
                continue;
            }

            for(ResourceLocation effectId : effectIds) {
                if (!hits.containsKey(effectId) && state.hasSpecialEffect(effectId)) {
                    hits.put(effectId, new BladeSeHit(blade, state, slot, effectId));
                }
            }
        }

        BladeSeHit firstHit = null;
        for(ResourceLocation effectId : effectIds) {
            BladeSeHit hit = hits.get(effectId);
            if (hit != null) {
                cache.putBlade(effectId, hit.blade(), hit.slot());
                if (firstHit == null) {
                    firstHit = hit;
                }
            } else {
                cache.putNegative(effectId, gameTime + NEGATIVE_CACHE_TICKS);
            }
        }
        return firstHit;
    }

    private static List<ResourceLocation> normalizeEffectIds(ResourceLocation[] effectIds) {
        if (effectIds == null || effectIds.length == 0) {
            return List.of();
        }
        Set<ResourceLocation> normalized = new LinkedHashSet<>();
        for(ResourceLocation effectId : effectIds) {
            if (effectId != null) {
                normalized.add(effectId);
            }
        }
        return List.copyOf(normalized);
    }

    @Nullable
    private static ISlashBladeState getUsableState(ItemStack blade, ResourceLocation effectId) {
        ISlashBladeState state = getUsableState(blade);
        if (state == null || !state.hasSpecialEffect(effectId)) {
            return null;
        }
        return state;
    }

    @Nullable
    private static ISlashBladeState getUsableState(ItemStack blade) {
        ISlashBladeState state = getState(blade);
        if (state == null || state.isBroken()) {
            return null;
        }
        return state;
    }

    @Nullable
    private static ISlashBladeState getState(ItemStack blade) {
        if (blade == null || blade.isEmpty() || !(blade.getItem() instanceof ItemSlashBlade)) {
            return null;
        }
        return BladeStateAccess.of(blade).orElse(null);
    }

    private static int getSlotCount(LivingEntity entity) {
        if (entity instanceof Player player) {
            return player.getInventory().getContainerSize();
        }
        return EQUIPMENT_SLOTS.length;
    }

    @Nullable
    private static ItemStack getStackAtSlot(LivingEntity entity, int slot) {
        if (entity instanceof Player player) {
            if (slot < 0 || slot >= player.getInventory().getContainerSize()) {
                return null;
            }
            return player.getInventory().getItem(slot);
        }
        if (slot < 0 || slot >= EQUIPMENT_SLOTS.length) {
            return null;
        }
        return entity.getItemBySlot(EQUIPMENT_SLOTS[slot]);
    }

    public static boolean hasInInventory(LivingEntity entity, DeferredHolder<SpecialEffect, ? extends SpecialEffect> effect) {
        return findFirstInInventory(entity, effect) != null;
    }

    public static boolean hasInInventory(LivingEntity entity, ResourceLocation effectId) {
        return findFirstInInventory(entity, effectId) != null;
    }

    public static boolean hasInInventoryWithProudSoul(LivingEntity entity, DeferredHolder<SpecialEffect, ? extends SpecialEffect> effect) {
        BladeSeHit hit = findFirstInInventory(entity, effect);
        return hit != null && hit.state().getProudSoulCount() > 0;
    }

    public static boolean hasInInventoryWithProudSoul(LivingEntity entity, ResourceLocation effectId) {
        BladeSeHit hit = findFirstInInventory(entity, effectId);
        return hit != null && hit.state().getProudSoulCount() > 0;
    }

    public static void forEachInventoryBlade(
            LivingEntity entity,
            DeferredHolder<SpecialEffect, ? extends SpecialEffect> effect,
            BiConsumer<ItemStack, ISlashBladeState> consumer
    ) {
        BladeSeHit hit = findFirstInInventory(entity, effect);
        if (hit != null) {
            consumer.accept(hit.blade(), hit.state());
        }
    }

    public static void forEachInventorySlashBlade(
            LivingEntity entity,
            BiConsumer<ItemStack, ISlashBladeState> consumer
    ) {
        int size = getSlotCount(entity);
        for(int slot = 0; slot < size; slot++) {
            ItemStack blade = getStackAtSlot(entity, slot);
            ISlashBladeState state = getState(blade);
            if (state != null) {
                consumer.accept(blade, state);
            }
        }
    }
}

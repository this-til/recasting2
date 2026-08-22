package com.til.recasting.handler;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
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
 * 扫描活体实体背包 / 装备栏中的拔刀剑 SE。
 * <p>
 * 玩家扫描完整物品栏；其他活体扫描全部 {@link EquipmentSlot}。
 * 命中槽位会缓存并在失效前 O(1) 校验；未命中会写入负缓存，在物品栏变更前或 TTL 到期前跳过全量扫描。
 */
public final class InventorySlashBladeSeHelper {

    /**
     * 未命中后默认重扫间隔：2s
     */
    private static final int NEGATIVE_CACHE_TICKS = 40;

    public record BladeSeHit(ItemStack blade, ISlashBladeState state, int slot, ResourceLocation effectId) {
    }

    private record EffectCacheEntry(int slot, long negativeUntilGameTime) {
        private static final int NEGATIVE_SLOT = -1;

        private boolean isNegative() {
            return slot == NEGATIVE_SLOT;
        }

        private static EffectCacheEntry positive(int slot) {
            return new EffectCacheEntry(slot, 0L);
        }

        private static EffectCacheEntry negative(long negativeUntilGameTime) {
            return new EffectCacheEntry(NEGATIVE_SLOT, negativeUntilGameTime);
        }
    }

    /**
     * entityUUID -> (effectId -> cache entry)
     */
    private static final Map<UUID, Map<ResourceLocation, EffectCacheEntry>> SLOT_CACHE = new ConcurrentHashMap<>();

    /**
     * entityUUID -> 无拔刀剑负缓存到期 tick
     */
    private static final Map<UUID, Long> NO_BLADE_UNTIL = new ConcurrentHashMap<>();

    private static final EquipmentSlot[] EQUIPMENT_SLOTS = EquipmentSlot.values();

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
        return findFirstInInventory(entity, new ResourceLocation[]{effectId});
    }

    /**
     * 按优先级单次扫描多个 SE，返回第一个命中的刀。
     */
    @Nullable
    public static BladeSeHit findFirstInInventory(LivingEntity entity, ResourceLocation... effectIdsInPriority) {
        if (effectIdsInPriority == null || effectIdsInPriority.length == 0) {
            return null;
        }

        long gameTime = entity.level().getGameTime();
        UUID entityId = entity.getUUID();
        if (isNoBladeCached(entityId, gameTime)) {
            return null;
        }

        for(ResourceLocation effectId : effectIdsInPriority) {
            if (effectId == null) {
                continue;
            }
            BladeSeHit cachedHit = resolveCached(entity, entityId, effectId, gameTime);
            if (cachedHit != null) {
                return cachedHit;
            }
        }

        BladeSeHit scannedHit = scanInventory(entity, entityId, effectIdsInPriority, gameTime);
        if (scannedHit != null) {
            return scannedHit;
        }

        cacheNegative(entityId, effectIdsInPriority, gameTime);
        return null;
    }

    @Nullable
    private static BladeSeHit resolveCached(
            LivingEntity entity,
            UUID entityId,
            ResourceLocation effectId,
            long gameTime
    ) {
        EffectCacheEntry cached = getCachedEntry(entityId, effectId);
        if (cached == null) {
            return null;
        }
        if (cached.isNegative()) {
            if (gameTime < cached.negativeUntilGameTime()) {
                return null;
            }
            clearCachedSlot(entityId, effectId);
            return null;
        }

        BladeSeHit hit = resolveSlot(entity, cached.slot(), effectId);
        if (hit != null) {
            return hit;
        }
        clearCachedSlot(entityId, effectId);
        return null;
    }

    @Nullable
    private static BladeSeHit scanInventory(
            LivingEntity entity,
            UUID entityId,
            ResourceLocation[] effectIdsInPriority,
            long gameTime
    ) {
        int size = getSlotCount(entity);
        boolean foundSlashBlade = false;
        for(int slot = 0; slot < size; slot++) {
            ItemStack stack = getStackAtSlot(entity, slot);
            if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemSlashBlade)) {
                continue;
            }
            ISlashBladeState state = stack.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
            if (state == null) {
                continue;
            }
            foundSlashBlade = true;

            for(ResourceLocation effectId : effectIdsInPriority) {
                if (effectId == null || !state.hasSpecialEffect(effectId)) {
                    continue;
                }
                putCachedSlot(entityId, effectId, slot);
                return new BladeSeHit(stack, state, slot, effectId);
            }
        }

        if (!foundSlashBlade) {
            NO_BLADE_UNTIL.put(entityId, gameTime + NEGATIVE_CACHE_TICKS);
        }
        return null;
    }

    private static void cacheNegative(UUID entityId, ResourceLocation[] effectIdsInPriority, long gameTime) {
        long negativeUntil = gameTime + NEGATIVE_CACHE_TICKS;
        for(ResourceLocation effectId : effectIdsInPriority) {
            if (effectId != null) {
                putNegativeCache(entityId, effectId, negativeUntil);
            }
        }
    }

    private static boolean isNoBladeCached(UUID entityId, long gameTime) {
        Long until = NO_BLADE_UNTIL.get(entityId);
        if (until == null) {
            return false;
        }
        if (gameTime < until) {
            return true;
        }
        NO_BLADE_UNTIL.remove(entityId, until);
        return false;
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

    @Nullable
    private static BladeSeHit resolveSlot(LivingEntity entity, int slot, ResourceLocation effectId) {
        ItemStack stack = getStackAtSlot(entity, slot);
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        if (!hasSpecialEffect(stack, effectId)) {
            return null;
        }
        ISlashBladeState state = stack.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
        if (state == null) {
            return null;
        }
        return new BladeSeHit(stack, state, slot, effectId);
    }

    @Nullable
    private static EffectCacheEntry getCachedEntry(UUID entityId, ResourceLocation effectId) {
        Map<ResourceLocation, EffectCacheEntry> byEffect = SLOT_CACHE.get(entityId);
        if (byEffect == null) {
            return null;
        }
        return byEffect.get(effectId);
    }

    private static void putCachedSlot(UUID entityId, ResourceLocation effectId, int slot) {
        SLOT_CACHE.computeIfAbsent(entityId, id -> new ConcurrentHashMap<>())
                .put(effectId, EffectCacheEntry.positive(slot));
    }

    private static void putNegativeCache(UUID entityId, ResourceLocation effectId, long negativeUntilGameTime) {
        SLOT_CACHE.computeIfAbsent(entityId, id -> new ConcurrentHashMap<>())
                .put(effectId, EffectCacheEntry.negative(negativeUntilGameTime));
    }

    private static void clearCachedSlot(UUID entityId, ResourceLocation effectId) {
        Map<ResourceLocation, EffectCacheEntry> byEffect = SLOT_CACHE.get(entityId);
        if (byEffect == null) {
            return;
        }
        byEffect.remove(effectId);
        if (byEffect.isEmpty()) {
            SLOT_CACHE.remove(entityId, byEffect);
        }
    }

    /**
     * 物品栏 / 装备变更时清理缓存，避免负缓存长期漏检。
     */
    public static void clearEntityCache(UUID entityId) {
        if (entityId == null) {
            return;
        }
        SLOT_CACHE.remove(entityId);
        NO_BLADE_UNTIL.remove(entityId);
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

    /**
     * 遍历实体背包 / 装备栏中的全部拔刀剑（不按 SE 过滤）。
     */
    public static void forEachInventorySlashBlade(
            LivingEntity entity,
            BiConsumer<ItemStack, ISlashBladeState> consumer
    ) {
        long gameTime = entity.level().getGameTime();
        UUID entityId = entity.getUUID();
        if (isNoBladeCached(entityId, gameTime)) {
            return;
        }

        int size = getSlotCount(entity);
        boolean foundSlashBlade = false;
        for(int i = 0; i < size; i++) {
            ItemStack stack = getStackAtSlot(entity, i);
            if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemSlashBlade)) {
                continue;
            }
            ISlashBladeState state = stack.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
            if (state != null) {
                foundSlashBlade = true;
                consumer.accept(stack, state);
            }
        }
        if (!foundSlashBlade) {
            NO_BLADE_UNTIL.put(entityId, gameTime + NEGATIVE_CACHE_TICKS);
        }
    }
}

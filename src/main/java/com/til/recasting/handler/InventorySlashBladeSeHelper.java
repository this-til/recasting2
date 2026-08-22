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
 * 按实体 UUID + SE id 缓存上次命中的槽位，命中时先校验缓存槽，失效再全量扫描。
 */
public final class InventorySlashBladeSeHelper {

    public record BladeSeHit(ItemStack blade, ISlashBladeState state, int slot) {
    }

    /**
     * entityUUID -> (effectId -> slot index)
     */
    private static final Map<UUID, Map<ResourceLocation, Integer>> SLOT_CACHE = new ConcurrentHashMap<>();

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

        UUID entityId = entity.getUUID();
        Integer cachedSlot = getCachedSlot(entityId, effectId);
        if (cachedSlot != null) {
            BladeSeHit cachedHit = resolveSlot(entity, cachedSlot, effectId);
            if (cachedHit != null) {
                return cachedHit;
            }
            clearCachedSlot(entityId, effectId);
        }

        int size = getSlotCount(entity);
        for(int i = 0; i < size; i++) {
            BladeSeHit hit = resolveSlot(entity, i, effectId);
            if (hit != null) {
                putCachedSlot(entityId, effectId, i);
                return hit;
            }
        }

        clearCachedSlot(entityId, effectId);
        return null;
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
        return new BladeSeHit(stack, state, slot);
    }

    private static Integer getCachedSlot(UUID entityId, ResourceLocation effectId) {
        Map<ResourceLocation, Integer> byEffect = SLOT_CACHE.get(entityId);
        if (byEffect == null) {
            return null;
        }
        return byEffect.get(effectId);
    }

    private static void putCachedSlot(UUID entityId, ResourceLocation effectId, int slot) {
        SLOT_CACHE.computeIfAbsent(entityId, id -> new ConcurrentHashMap<>()).put(effectId, slot);
    }

    private static void clearCachedSlot(UUID entityId, ResourceLocation effectId) {
        Map<ResourceLocation, Integer> byEffect = SLOT_CACHE.get(entityId);
        if (byEffect == null) {
            return;
        }
        byEffect.remove(effectId);
        if (byEffect.isEmpty()) {
            SLOT_CACHE.remove(entityId, byEffect);
        }
    }

    /**
     * 实体离开世界时清理缓存，避免泄漏。
     */
    public static void clearEntityCache(UUID entityId) {
        if (entityId != null) {
            SLOT_CACHE.remove(entityId);
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

    /**
     * 遍历实体背包 / 装备栏中的全部拔刀剑（不按 SE 过滤）。
     */
    public static void forEachInventorySlashBlade(
            LivingEntity entity,
            BiConsumer<ItemStack, ISlashBladeState> consumer
    ) {
        int size = getSlotCount(entity);
        for(int i = 0; i < size; i++) {
            ItemStack stack = getStackAtSlot(entity, i);
            if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemSlashBlade)) {
                continue;
            }
            ISlashBladeState state = stack.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
            if (state != null) {
                consumer.accept(stack, state);
            }
        }
    }
}

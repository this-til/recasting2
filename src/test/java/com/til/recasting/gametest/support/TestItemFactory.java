package com.til.recasting.gametest.support;

import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Map;

/**
 * 铁砧与配方测试共用的物品构造。
 */
public final class TestItemFactory {

    private TestItemFactory() {
    }

    public static ItemStack seCrystal(ResourceLocation type, int level) {
        ItemStack stack = new ItemStack(RecastingItems.SE_CRYSTAL.get());
        stack.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA).ifPresent(data -> {
            data.setSpecialEffectType(type);
            data.setSpecialEffectLevel(level);
            stack.getOrCreateTag().put("se_crystal_data", data.serializeNBT());
        });
        return stack;
    }

    public static ItemStack bladeFromDefinition(RegistryAccess access, ResourceLocation bladeId) {
        SlashBladeDefinition definition = access.registryOrThrow(SlashBladeDefinition.REGISTRY_KEY)
                .getOrThrow(ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, bladeId));
        return definition.getBlade();
    }

    public static ItemStack bladeWithStats(
            RegistryAccess access,
            ResourceLocation bladeId,
            int killCount,
            int refineCount
    ) {
        return bladeWithStatsAndEnchantments(access, bladeId, killCount, refineCount, Map.of());
    }

    public static ItemStack bladeWithStatsAndEnchantments(
            RegistryAccess access,
            ResourceLocation bladeId,
            int killCount,
            int refineCount,
            Map<Enchantment, Integer> enchantments
    ) {
        ItemStack blade = bladeFromDefinition(access, bladeId);
        blade.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
            state.setKillCount(killCount);
            state.setRefine(refineCount);
            blade.getOrCreateTag().put("bladeState", state.serializeNBT());
        });
        applyEnchantments(blade, enchantments);
        return blade;
    }

    public static ItemStack bladeWithRequirements(
            RegistryAccess access,
            ResourceLocation bladeId,
            int killCount,
            int refineCount,
            ResourceLocation seId,
            int seLevel,
            Enchantment enchantment,
            int enchantmentLevel
    ) {
        if (enchantment == null || enchantmentLevel <= 0) {
            return bladeWithRequirements(access, bladeId, killCount, refineCount, seId, seLevel, Map.of());
        }
        return bladeWithRequirements(
                access,
                bladeId,
                killCount,
                refineCount,
                seId,
                seLevel,
                Map.of(enchantment, enchantmentLevel)
        );
    }

    public static ItemStack bladeWithRequirements(
            RegistryAccess access,
            ResourceLocation bladeId,
            int killCount,
            int refineCount,
            ResourceLocation seId,
            int seLevel,
            Map<Enchantment, Integer> enchantments
    ) {
        ItemStack blade = bladeWithSpecialEffect(access, bladeId, seId, seLevel);
        blade.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
            state.setKillCount(killCount);
            state.setRefine(refineCount);
            blade.getOrCreateTag().put("bladeState", state.serializeNBT());
        });
        applyEnchantments(blade, enchantments);
        return blade;
    }

    private static void applyEnchantments(ItemStack blade, Map<Enchantment, Integer> enchantments) {
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            Integer level = entry.getValue();
            if (enchantment != null && level != null && level > 0) {
                blade.enchant(enchantment, level);
            }
        }
    }

    public static ItemStack bladeWithSpecialEffect(
            RegistryAccess access,
            ResourceLocation bladeId,
            ResourceLocation seId,
            int level
    ) {
        ItemStack blade = bladeFromDefinition(access, bladeId);
        blade.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
            state.addSpecialEffect(seId);
            blade.getOrCreateTag().put("bladeState", state.serializeNBT());
        });
        blade.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION).ifPresent(extension -> {
            extension.setExtendedSpecialLevels(seId, level);
        });
        return blade;
    }

    public static ItemStack bladeWithoutSlashArts(RegistryAccess access, ResourceLocation bladeId) {
        ItemStack blade = bladeFromDefinition(access, bladeId);
        blade.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
            state.setSlashArtsKey(mods.flammpfeil.slashblade.registry.SlashArtsRegistry.NONE.getId());
            blade.getOrCreateTag().put("bladeState", state.serializeNBT());
        });
        return blade;
    }
}

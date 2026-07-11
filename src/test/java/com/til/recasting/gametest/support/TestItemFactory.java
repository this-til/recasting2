package com.til.recasting.gametest.support;

import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

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

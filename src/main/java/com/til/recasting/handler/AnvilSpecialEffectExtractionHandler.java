package com.til.recasting.handler;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.se.ExtendedSpecialEffect;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.til.recasting.Recasting.MODID;

/**
 * 铁砧材料与特殊 SE：
 * <ul>
 *   <li>左侧拔刀剑 + 右侧渊寂火 → 去除特殊 SE（保留刀）</li>
 *   <li>左侧拔刀剑 + 右侧聚散变体 → 提取特殊 SE 结晶（刀损毁）</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilSpecialEffectExtractionHandler {

    private static final int SPECIAL_SE_CRYSTAL_LEVEL = 1;

    private record SpecialSESnapshot(ResourceLocation seLocation) {
    }

    /**
     * 铁砧预览：刀配渊寂火用于去除，刀配聚散变体用于提取。
     */
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeft();
        ItemStack rightItem = event.getRight();
        String inputName = event.getName();

        // 刀 + 火 → 去除特殊 SE
        if (leftItem.getItem() instanceof ItemSlashBlade && rightItem.is(RecastingItems.ABYSS_FLAME.get())) {
            Optional<SpecialSESnapshot> specialSE = findBladeSpecialSE(leftItem);
            if (specialSE.isEmpty()) {
                return;
            }

            ItemStack output = leftItem.copy();
            removeAllSpecialEffects(output);
            if (inputName != null && !inputName.isEmpty()) {
                output.setHoverName(Component.literal(inputName));
            }
            event.setOutput(output);
            event.setMaterialCost(1);
            event.setCost(1);
            return;
        }

        // 刀 + 聚散变体 → 提取特殊 SE 结晶
        if (leftItem.getItem() instanceof ItemSlashBlade && rightItem.is(RecastingItems.GATHERING_PARTING_VARIANT.get())) {
            Optional<SpecialSESnapshot> specialSE = findBladeSpecialSE(leftItem);
            if (specialSE.isEmpty()) {
                return;
            }

            ItemStack output = createSpecialSECrystal(specialSE.get(), inputName);
            event.setOutput(output);
            event.setMaterialCost(1);
            event.setCost(5);
        }
    }

    /**
     * 创造模式铁砧默认不消耗材料；提取特殊 SE 时仍强制销毁聚散变体与刀。
     */
    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        if (!event.getEntity().getAbilities().instabuild) {
            return;
        }
        if (!matchesSpecialSeExtraction(event.getLeft(), event.getRight(), event.getOutput())) {
            return;
        }
        if (!(event.getEntity().containerMenu instanceof AnvilMenu anvilMenu)) {
            return;
        }

        anvilMenu.getSlot(0).set(ItemStack.EMPTY);

        ItemStack rightItem = anvilMenu.getSlot(1).getItem();
        rightItem.shrink(1);
        if (rightItem.isEmpty()) {
            anvilMenu.getSlot(1).set(ItemStack.EMPTY);
        } else {
            anvilMenu.getSlot(1).set(rightItem);
        }
    }

    private static boolean matchesSpecialSeExtraction(ItemStack leftItem, ItemStack rightItem, ItemStack output) {
        if (!(leftItem.getItem() instanceof ItemSlashBlade)) {
            return false;
        }
        if (!rightItem.is(RecastingItems.GATHERING_PARTING_VARIANT.get())) {
            return false;
        }
        if (!output.is(RecastingItems.SE_CRYSTAL.get())) {
            return false;
        }

        Optional<SpecialSESnapshot> specialSE = findBladeSpecialSE(leftItem);
        if (specialSE.isEmpty()) {
            return false;
        }

        return output.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA)
                .map(crystalData -> {
                    if (!specialSE.get().seLocation().equals(crystalData.getSpecialEffectType())) {
                        return false;
                    }
                    return crystalData.getSpecialEffectLevel() == SPECIAL_SE_CRYSTAL_LEVEL;
                })
                .orElse(false);
    }

    private static ItemStack createSpecialSECrystal(SpecialSESnapshot specialSE, @Nullable String inputName) {
        ItemStack output = new ItemStack(RecastingItems.SE_CRYSTAL.get());
        output.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA).ifPresent(crystalData -> {
            crystalData.setSpecialEffectType(specialSE.seLocation());
            crystalData.setSpecialEffectLevel(SPECIAL_SE_CRYSTAL_LEVEL);
        });

        if (inputName != null && !inputName.isEmpty()) {
            output.setHoverName(Component.literal(inputName));
        }
        return output;
    }

    /**
     * 查找刀上第一个特殊 SE，提取结晶固定为 1 级。
     */
    private static Optional<SpecialSESnapshot> findBladeSpecialSE(ItemStack bladeStack) {
        if (!(bladeStack.getItem() instanceof ItemSlashBlade)) {
            return Optional.empty();
        }

        AtomicReference<SpecialSESnapshot> found = new AtomicReference<>();
        bladeStack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(bladeState ->
                bladeStack.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION).ifPresent(extension -> {
                    for (ResourceLocation seLocation : bladeState.getSpecialEffects()) {
                        SpecialEffect se = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getValue(seLocation);
                        if (!(se instanceof ExtendedSpecialEffect extendedSE)) {
                            continue;
                        }
                        if (!extendedSE.isSpecial() || !bladeState.hasSpecialEffect(seLocation)) {
                            continue;
                        }
                        if (extension.getExtendedSpecialLevels(seLocation) <= 0) {
                            continue;
                        }
                        found.set(new SpecialSESnapshot(seLocation));
                        return;
                    }
                })
        );
        return Optional.ofNullable(found.get());
    }

    private static void removeAllSpecialEffects(ItemStack bladeStack) {
        bladeStack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(bladeState ->
                bladeStack.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION).ifPresent(extension ->
                        clearSpecialEffects(bladeState, extension)
                )
        );
    }

    private static void clearSpecialEffects(ISlashBladeState bladeState, PropertiesDefinitionExtension extension) {
        List<ResourceLocation> toRemove = new ArrayList<>();
        for (ResourceLocation existingSE : bladeState.getSpecialEffects()) {
            SpecialEffect existingEffect = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getValue(existingSE);
            if (existingEffect instanceof ExtendedSpecialEffect existingExtendedSE
                    && existingExtendedSE.isSpecial()) {
                toRemove.add(existingSE);
            }
        }
        for (ResourceLocation seToRemove : toRemove) {
            bladeState.removeSpecialEffect(seToRemove);
            extension.setExtendedSpecialLevels(seToRemove, 0);
        }
    }
}

package com.til.recasting.handler;

import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.se.ExtendedSpecialEffect;
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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.til.recasting.Recasting.MODID;

/**
 * 铁砧特殊 SE 提取：左侧拔刀剑 + 右侧渊寂火 → 特殊 SE 结晶。
 * 生存与创造模式下刀都会被销毁；渊寂火消耗 1 个。
 */
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilSpecialEffectExtractionHandler {

    private static final int SPECIAL_SE_CRYSTAL_LEVEL = 1;

    private record SpecialSESnapshot(ResourceLocation seLocation) {
    }

    /**
     * 铁砧预览：左侧刀 + 右侧渊寂火 → 输出特殊 SE 结晶（无刀）。
     */
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeft();
        ItemStack rightItem = event.getRight();
        String inputName = event.getName();

        if (!(leftItem.getItem() instanceof ItemSlashBlade)) {
            return;
        }
        if (!rightItem.is(RecastingItems.ABYSS_FLAME.get())) {
            return;
        }

        Optional<SpecialSESnapshot> specialSE = findBladeSpecialSE(leftItem);
        if (specialSE.isEmpty()) {
            return;
        }

        ItemStack output = createSpecialSECrystal(specialSE.get(), inputName);
        event.setOutput(output);
        event.setMaterialCost(1);
        event.setCost(5);
    }

    /**
     * 创造模式铁砧默认不消耗材料；提取特殊 SE 时仍强制销毁刀并消耗渊寂火。
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
        if (!rightItem.is(RecastingItems.ABYSS_FLAME.get())) {
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
}

package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.capability.SECrystalData;
import com.til.recasting.registry.RecastingDataComponents;
import com.til.recasting.registry.RecastingItems;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 铁砧材料与特殊 SE：
 * <ul>
 *   <li>左侧拔刀剑 + 右侧渊寂火 → 去除首个特殊 SE（保留刀）</li>
 *   <li>左侧拔刀剑 + 右侧聚散变体 → 提取首个特殊 SE 结晶（刀损毁）</li>
 * </ul>
 */
@EventBusSubscriber(modid = Recasting.MODID)
public final class AnvilSpecialEffectExtractionHandler {

    private static final int SPECIAL_SE_CRYSTAL_LEVEL = 1;

    private AnvilSpecialEffectExtractionHandler() {
    }

    /**
     * 铁砧预览：刀配渊寂火用于去除，刀配聚散变体用于提取；多特殊 SE 时均取第一个。
     */
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeft();
        ItemStack rightItem = event.getRight();
        String inputName = event.getName();

        if (leftItem.getItem() instanceof ItemSlashBlade && rightItem.is(RecastingItems.ABYSS_FLAME.get())) {
            Optional<BladeSpecialEffectHelper.EffectEntry> specialSE =
                    BladeSpecialEffectHelper.findFirstSpecialEffect(leftItem);
            if (specialSE.isEmpty()) {
                return;
            }

            BladeSpecialEffectHelper.EffectEntry entry = specialSE.get();
            ItemStack output = leftItem.copy();
            BladeStateAccess.of(output).ifPresent(bladeState -> {
                var properties = BladeSpecialEffectHelper.copyProperties(AttackHelper.propertiesOf(output));
                bladeState.removeSpecialEffect(entry.id());
                properties.setExtendedSpecialLevels(entry.id(), 0);
                BladeSpecialEffectHelper.writeProperties(output, properties);
            });
            if (inputName != null && !inputName.isEmpty()) {
                output.set(DataComponents.CUSTOM_NAME, Component.literal(inputName));
            }
            event.setOutput(output);
            event.setMaterialCost(1);
            event.setCost(1);
            return;
        }

        if (leftItem.getItem() instanceof ItemSlashBlade && rightItem.is(RecastingItems.GATHERING_PARTING_VARIANT.get())) {
            Optional<BladeSpecialEffectHelper.EffectEntry> specialSE =
                    BladeSpecialEffectHelper.findFirstSpecialEffect(leftItem);
            if (specialSE.isEmpty()) {
                return;
            }

            ItemStack output = createSpecialSECrystal(specialSE.get(), inputName);
            event.setOutput(output);
            event.setMaterialCost(1);
            event.setCost(5);
        }
    }

    private static ItemStack createSpecialSECrystal(
            BladeSpecialEffectHelper.EffectEntry specialSE,
            @Nullable String inputName
    ) {
        ItemStack output = new ItemStack(RecastingItems.SE_CRYSTAL.get());
        SECrystalData crystalData = new SECrystalData();
        crystalData.setSpecialEffectType(specialSE.id());
        crystalData.setSpecialEffectLevel(SPECIAL_SE_CRYSTAL_LEVEL);
        output.set(RecastingDataComponents.SE_CRYSTAL_DATA.get(), crystalData);

        if (inputName != null && !inputName.isEmpty()) {
            output.set(DataComponents.CUSTOM_NAME, Component.literal(inputName));
        }
        return output;
    }
}

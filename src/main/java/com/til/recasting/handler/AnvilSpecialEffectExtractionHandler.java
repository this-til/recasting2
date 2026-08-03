package com.til.recasting.handler;

import com.til.recasting.registry.RecastingItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Optional;

import static com.til.recasting.Recasting.MODID;

/**
 * 铁砧材料与特殊 SE：
 * <ul>
 *   <li>左侧拔刀剑 + 右侧渊寂火 → 去除首个特殊 SE（保留刀）</li>
 *   <li>左侧拔刀剑 + 右侧聚散变体 → 提取首个特殊 SE 结晶（刀损毁）</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilSpecialEffectExtractionHandler {

    private static final int SPECIAL_SE_CRYSTAL_LEVEL = 1;

    /**
     * 铁砧预览：刀配渊寂火用于去除，刀配聚散变体用于提取；多特殊 SE 时均取第一个。
     */
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeft();
        ItemStack rightItem = event.getRight();
        String inputName = event.getName();

        // 刀 + 火 → 去除首个特殊 SE
        if (leftItem.getItem() instanceof ItemSlashBlade && rightItem.is(RecastingItems.ABYSS_FLAME.get())) {
            Optional<BladeSpecialEffectHelper.EffectEntry> specialSE =
                    BladeSpecialEffectHelper.findFirstSpecialEffect(leftItem);
            if (specialSE.isEmpty()) {
                return;
            }

            BladeSpecialEffectHelper.EffectEntry entry = specialSE.get();
            ItemStack output = leftItem.copy();
            output.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(bladeState ->
                    output.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                            .ifPresent(properties -> {
                                bladeState.removeSpecialEffect(entry.id());
                                properties.setExtendedSpecialLevels(entry.id(), 0);
                            })
            );
            if (inputName != null && !inputName.isEmpty()) {
                output.setHoverName(Component.literal(inputName));
            }
            event.setOutput(output);
            event.setMaterialCost(1);
            event.setCost(1);
            return;
        }

        // 刀 + 聚散变体 → 提取首个特殊 SE 结晶
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
        output.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA).ifPresent(crystalData -> {
            crystalData.setSpecialEffectType(specialSE.id());
            crystalData.setSpecialEffectLevel(SPECIAL_SE_CRYSTAL_LEVEL);
        });

        if (inputName != null && !inputName.isEmpty()) {
            output.setHoverName(Component.literal(inputName));
        }
        return output;
    }

}

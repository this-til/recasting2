package com.til.recasting.handler;

import com.til.recasting.registry.RecastingItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.til.recasting.Recasting.MODID;

/**
 * 铁砧特殊SE提取事件处理器
 * 处理左侧聚散变体 + 右侧拔刀剑 -> 提取特殊SE到结晶的操作
 */
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilSpecialEffectExtractionHandler {

    /**
     * 铁砧更新事件监听器
     * 处理聚散变体 + 拔刀剑 -> 提取特殊SE到结晶
     */
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeft();      // 铁砧左侧物品
        ItemStack rightItem = event.getRight();    // 铁砧右侧物品
        String inputName = event.getName();        // 玩家输入的名称

        // 检查左侧是否为聚散变体
        if (!leftItem.is(RecastingItems.GATHERING_PARTING_VARIANT.get())) {
            return;
        }

        // 检查右侧是否为拔刀剑
        if (!(rightItem.getItem() instanceof ItemSlashBlade)) {
            return;
        }

        // 从拔刀剑中查找特殊SE（isSpecial() == true 且等级 > 0）
        AtomicInteger foundSpecialSELevel = new AtomicInteger(0);
        AtomicReference<ResourceLocation> foundSpecialSELocation = new AtomicReference<>(null);

        rightItem.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(bladeState -> {
            rightItem.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION).ifPresent(extension -> {
                for (ResourceLocation seLocation : bladeState.getSpecialEffects()) {
                    SpecialEffect se = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getValue(seLocation);
                    if (se instanceof com.til.recasting.registry.SpecialEffectsRegistry.ExtendedSpecialEffect extendedSE) {
                        if (extendedSE.isSpecial()) {
                            int level = extension.getExtendedSpecialLevels(seLocation);
                            if (level > 0) {
                                foundSpecialSELocation.set(seLocation);
                                foundSpecialSELevel.set(level);
                                break; // 只提取第一个找到的特殊SE
                            }
                        }
                    }
                }
            });
        });

        // 如果没有找到特殊SE，则不处理
        if (foundSpecialSELocation.get() == null || foundSpecialSELevel.get() == 0) {
            return;
        }

        // 创建SE结晶作为输出
        ItemStack output = new ItemStack(RecastingItems.SE_CRYSTAL.get());
        output.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA).ifPresent(crystalData -> {
            crystalData.setSpecialEffectType(foundSpecialSELocation.get());
            crystalData.setSpecialEffectLevel(foundSpecialSELevel.get());
        });

        if (inputName != null && !inputName.isEmpty()) {
            output.setHoverName(Component.literal(inputName));
        }

        event.setOutput(output);
        event.setMaterialCost(1);
        event.setCost(1);
    }
}

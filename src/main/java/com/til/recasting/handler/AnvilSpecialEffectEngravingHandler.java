package com.til.recasting.handler;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.SpecialEffectsRegistry;
import com.til.recasting.registry.se.ExtendedSpecialEffect;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.til.recasting.Recasting.MODID;

/**
 * 铁砧 SE 铭刻事件处理器
 * 当铁砧左侧为拔刀剑，右侧为 SE 结晶时，执行 SE 铭刻操作
 */
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilSpecialEffectEngravingHandler {

    /**
     * 铁砧更新事件监听器
     * 处理拔刀剑 + SE 结晶的铭刻操作
     */
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeft();      // 铁砧左侧物品
        ItemStack rightItem = event.getRight();    // 铁砧右侧物品
        String inputName = event.getName();        // 玩家输入的名称
        // 检查左侧是否为拔刀剑
        if (!(leftItem.getItem() instanceof ItemSlashBlade)) {
            return;
        }

        // 检查右侧是否为 SE 结晶
        if (!rightItem.is(RecastingItems.SE_CRYSTAL.get())) {
            return;
        }

        // 获取 SE 结晶的数据
        rightItem.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA).ifPresent(crystalData -> {
            // 检查 SE 结晶是否包含有效的特殊效果
            if (!crystalData.hasSpecialEffect()) {
                return;
            }

            ResourceLocation seLocation = crystalData.getSpecialEffectType();
            if (seLocation == null) {
                return;
            }

            // 从注册表中获取 SpecialEffect
            if (!mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().containsKey(seLocation)) {
                return;
            }

            SpecialEffect specialEffect = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getValue(seLocation);
            if (specialEffect == null) {
                return;
            }

            // 检查是否为 ExtendedSpecialEffect
            if (!(specialEffect instanceof com.til.recasting.registry.se.ExtendedSpecialEffect extendedSE)) {
                return;
            }

            int crystalLevel = crystalData.getSpecialEffectLevel();

            if (extendedSE.isSpecial() && crystalLevel > 1) {
                return;
            }

            // 获取拔刀剑当前的特效等级
            AtomicInteger currentLevel = new AtomicInteger(0);
            leftItem.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION).ifPresent(extension -> currentLevel.set(extension.getExtendedSpecialLevels(seLocation)));

            // 条件判断
            if (crystalLevel == 0) {
                // 抹去特效时，必须拥有该特效
                if (currentLevel.get() == 0) {
                    return; // 拔刀剑没有该特效，无法抹去
                }
            } else {
                // 提升等级时，结晶等级必须大于当前等级
                if (crystalLevel <= currentLevel.get()) {
                    return; // 结晶等级不高于当前等级，无法提升
                }

                // 检查玩家是否在创造模式（排除创造模式的限制）
                boolean isCreativeMode = false;
                Player player = event.getPlayer();
                if (player != null && !player.level().isClientSide()) {
                    isCreativeMode = player.getAbilities().instabuild;
                }

                // 检查SE数量限制（创造模式跳过限制）
                if (!isCreativeMode && !extendedSE.isSpecial()) {
                    AtomicInteger normalSECount = new AtomicInteger(0);

                    leftItem.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION).ifPresent(extension -> {
                        leftItem.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(bladeState -> {
                            for (ResourceLocation existingSE : bladeState.getSpecialEffects()) {
                                SpecialEffect existingSpecialEffect = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getValue(existingSE);
                                if (existingSpecialEffect instanceof com.til.recasting.registry.se.ExtendedSpecialEffect existingExtendedSE) {
                                    int existingLevel = extension.getExtendedSpecialLevels(existingSE);
                                    if (existingLevel > 0 && !existingExtendedSE.isSpecial()) {
                                        normalSECount.incrementAndGet();
                                    }
                                }
                            }
                        });
                    });

                    if (currentLevel.get() == 0 && normalSECount.get() >= 4) {
                        return;
                    }
                }
            }

            // 创建输出物品（拔刀剑的副本）
            ItemStack output = leftItem.copy();

            if (crystalLevel > 0) {
                output.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(bladeState ->
                        output.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION).ifPresent(extension -> {
                            if (extendedSE.isSpecial()) {
                                removeOtherSpecialEffects(bladeState, extension, seLocation);
                            }
                            bladeState.addSpecialEffect(seLocation);
                            extension.setExtendedSpecialLevels(crystalData.getSpecialEffectType(), crystalData.getSpecialEffectLevel());
                        })
                );
            } else {
                output.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(bladeState -> bladeState.removeSpecialEffect(seLocation));
                output.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION).ifPresent(extension -> extension.setExtendedSpecialLevels(crystalData.getSpecialEffectType(), 0));

            }

            if (inputName != null && !inputName.isEmpty()) {
                output.setHoverName(Component.literal(inputName));
            }

            event.setOutput(output);

            event.setMaterialCost(1);
            event.setCost(1);
        });
    }

    /**
     * 铭刻新的特殊 SE 前，移除刀上其余特殊 SE（最多保留一个特殊 SE 槽位，铁砧可替换）。
     */
    private static void removeOtherSpecialEffects(
            ISlashBladeState bladeState,
            PropertiesDefinitionExtension extension,
            ResourceLocation keep
    ) {
        List<ResourceLocation> toRemove = new ArrayList<>();
        for (ResourceLocation existingSE : bladeState.getSpecialEffects()) {
            if (existingSE.equals(keep)) {
                continue;
            }
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


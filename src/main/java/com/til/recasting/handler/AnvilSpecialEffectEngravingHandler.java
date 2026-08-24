package com.til.recasting.handler;

import com.til.recasting.Config;
import com.til.recasting.Recasting;
import com.til.recasting.capability.SECrystalData;
import com.til.recasting.registry.RecastingDataComponents;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.se.ExtendedSpecialEffect;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

/**
 * 铁砧 SE 铭刻事件处理器
 * 当铁砧左侧为拔刀剑，右侧为 SE 结晶时，执行 SE 铭刻操作
 */
@EventBusSubscriber(modid = Recasting.MODID)
public final class AnvilSpecialEffectEngravingHandler {

    private AnvilSpecialEffectEngravingHandler() {
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeft();
        ItemStack rightItem = event.getRight();
        String inputName = event.getName();

        if (!(leftItem.getItem() instanceof ItemSlashBlade)) {
            return;
        }

        if (!rightItem.is(RecastingItems.SE_CRYSTAL.get())) {
            return;
        }

        SECrystalData crystalData = rightItem.getOrDefault(
                RecastingDataComponents.SE_CRYSTAL_DATA.get(),
                SECrystalData.EMPTY
        );
        if (!crystalData.hasSpecialEffect()) {
            return;
        }

        ResourceLocation seLocation = crystalData.getSpecialEffectType();
        if (seLocation == null) {
            return;
        }

        if (!mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.containsKey(seLocation)) {
            return;
        }

        SpecialEffect specialEffect = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY
                .get(seLocation);
        if (specialEffect == null) {
            return;
        }

        if (!(specialEffect instanceof ExtendedSpecialEffect extendedSE)) {
            return;
        }

        int crystalLevel = crystalData.getSpecialEffectLevel();

        if (extendedSE.isSpecial() && crystalLevel != 1) {
            return;
        }

        int currentLevel = AttackHelper.propertiesOf(leftItem).getExtendedSpecialLevels(seLocation);

        boolean isCreativeMode = false;
        Player player = event.getPlayer();
        if (player != null) {
            isCreativeMode = player.getAbilities().instabuild;
        }

        if (crystalLevel == 0) {
            if (currentLevel == 0) {
                return;
            }
        } else {
            if (crystalLevel <= currentLevel) {
                return;
            }

            if (!isCreativeMode && !extendedSE.isSpecial() && !Config.isUnlimitedSeEngraving()) {
                int normalSECount = BladeSpecialEffectHelper.countActiveNormalExtendedEffects(leftItem);
                if (currentLevel == 0 && normalSECount >= Config.getNormalSeEngravingLimit()) {
                    return;
                }
            }
        }

        ItemStack output = leftItem.copy();

        if (crystalLevel > 0) {
            boolean switchSpecialSe = extendedSE.isSpecial()
                    && !isCreativeMode
                    && !Config.isUnlimitedSeEngraving();
            BladeStateAccess.of(output).ifPresent(bladeState -> {
                var extension = BladeSpecialEffectHelper.copyProperties(AttackHelper.propertiesOf(output));
                if (switchSpecialSe) {
                    BladeSpecialEffectHelper.removeSpecialEffectsExcept(
                            bladeState,
                            extension,
                            seLocation
                    );
                }
                if (currentLevel == 0) {
                    bladeState.addSpecialEffect(seLocation);
                }
                extension.setExtendedSpecialLevels(crystalData.getSpecialEffectType(), crystalData.getSpecialEffectLevel());
                BladeSpecialEffectHelper.writeProperties(output, extension);
            });
        } else {
            BladeStateAccess.of(output).ifPresent(bladeState -> bladeState.removeSpecialEffect(seLocation));
            var extension = BladeSpecialEffectHelper.copyProperties(AttackHelper.propertiesOf(output));
            extension.setExtendedSpecialLevels(crystalData.getSpecialEffectType(), 0);
            BladeSpecialEffectHelper.writeProperties(output, extension);
        }

        if (inputName != null && !inputName.isEmpty()) {
            output.set(DataComponents.CUSTOM_NAME, Component.literal(inputName));
        }

        event.setOutput(output);
        event.setMaterialCost(1);
        event.setCost(1);
    }
}

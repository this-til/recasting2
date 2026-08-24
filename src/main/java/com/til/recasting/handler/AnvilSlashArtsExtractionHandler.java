package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.constant.RecastingLanguageKeys;
import com.til.recasting.registry.requir.SlashBladeItems;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.SlashArtsRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

/**
 * 铁砧 SA 提取：左侧拔刀剑 + 右侧耀魂宝珠 → 输出写入 SA 的宝珠（刀损毁）。
 */
@EventBusSubscriber(modid = Recasting.MODID)
public final class AnvilSlashArtsExtractionHandler {

    private AnvilSlashArtsExtractionHandler() {
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeft();
        ItemStack rightItem = event.getRight();
        String inputName = event.getName();

        if (!(leftItem.getItem() instanceof ItemSlashBlade)) {
            return;
        }
        if (!rightItem.is(SlashBladeItems.PROUDSOUL_SPHERE.get())) {
            return;
        }

        ResourceLocation foundSALocation = BladeStateAccess.of(leftItem)
                .map(state -> {
                    ResourceLocation saKey = state.getSlashArtsKey();
                    if (saKey == null) {
                        return null;
                    }
                    if (!SlashArtsRegistry.REGISTRY.containsKey(saKey)) {
                        return null;
                    }
                    mods.flammpfeil.slashblade.slasharts.SlashArts sa =
                            SlashArtsRegistry.REGISTRY.get(saKey);
                    if (sa != null && !sa.equals(SlashArtsRegistry.NONE.get())) {
                        return saKey;
                    }
                    return null;
                })
                .orElse(null);

        if (foundSALocation == null) {
            return;
        }

        ItemStack output = rightItem.copy();
        output.setCount(1);

        CustomData existing = output.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = existing != null ? existing.copyTag() : new CompoundTag();
        tag.putString("SpecialAttackType", foundSALocation.toString());
        output.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        if (inputName != null && !inputName.isEmpty()) {
            output.set(DataComponents.CUSTOM_NAME, Component.literal(inputName));
        }

        event.setOutput(output);
        event.setMaterialCost(1);
        event.setCost(1);
    }
}

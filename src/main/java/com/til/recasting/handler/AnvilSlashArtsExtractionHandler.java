package com.til.recasting.handler;

import com.til.recasting.registry.requir.SlashBladeItems;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.atomic.AtomicReference;

import static com.til.recasting.Recasting.MODID;

/**
 * 铁砧 SA 提取：左侧拔刀剑 + 右侧耀魂宝珠 → 输出写入 SA 的宝珠（刀损毁）。
 */
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilSlashArtsExtractionHandler {

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

        AtomicReference<ResourceLocation> foundSALocation = new AtomicReference<>(null);

        leftItem.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(bladeState -> {
            ResourceLocation saKey = bladeState.getSlashArtsKey();
            if (saKey == null) {
                return;
            }
            if (!mods.flammpfeil.slashblade.registry.SlashArtsRegistry.REGISTRY.get().containsKey(saKey)) {
                return;
            }
            mods.flammpfeil.slashblade.slasharts.SlashArts sa =
                    mods.flammpfeil.slashblade.registry.SlashArtsRegistry.REGISTRY.get().getValue(saKey);
            if (sa != null && !sa.equals(mods.flammpfeil.slashblade.registry.SlashArtsRegistry.NONE.get())) {
                foundSALocation.set(saKey);
            }
        });

        if (foundSALocation.get() == null) {
            return;
        }

        ItemStack output = rightItem.copy();
        output.setCount(1);

        CompoundTag tag = output.getOrCreateTag();
        tag.putString("SpecialAttackType", foundSALocation.get().toString());

        if (inputName != null && !inputName.isEmpty()) {
            output.setHoverName(Component.literal(inputName));
        }

        event.setOutput(output);
        event.setMaterialCost(1);
        event.setCost(1);
    }
}

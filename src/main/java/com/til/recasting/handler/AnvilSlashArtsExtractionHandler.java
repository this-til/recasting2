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
 * 铁砧SA提取事件处理器
 * 处理左侧耀魂宝珠 + 右侧拔刀剑 -> 提取SA到耀魂宝珠的操作
 */
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilSlashArtsExtractionHandler {

    /**
     * 铁砧更新事件监听器
     * 处理耀魂宝珠 + 拔刀剑 -> 提取SA到耀魂宝珠
     */
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeft();      // 铁砧左侧物品
        ItemStack rightItem = event.getRight();    // 铁砧右侧物品
        String inputName = event.getName();        // 玩家输入的名称

        // 检查左侧是否为耀魂宝珠
        if (!leftItem.is(SlashBladeItems.PROUDSOUL_SPHERE.get())) {
            return;
        }

        // 检查右侧是否为拔刀剑
        if (!(rightItem.getItem() instanceof ItemSlashBlade)) {
            return;
        }

        // 从拔刀剑中获取SA
        AtomicReference<ResourceLocation> foundSALocation = new AtomicReference<>(null);

        rightItem.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(bladeState -> {
            ResourceLocation saKey = bladeState.getSlashArtsKey();
            if (saKey != null) {
                // 检查SA是否有效（不是NONE）
                if (mods.flammpfeil.slashblade.registry.SlashArtsRegistry.REGISTRY.get().containsKey(saKey)) {
                    mods.flammpfeil.slashblade.slasharts.SlashArts sa = mods.flammpfeil.slashblade.registry.SlashArtsRegistry.REGISTRY.get().getValue(saKey);
                    // 排除NONE
                    if (sa != null && !sa.equals(mods.flammpfeil.slashblade.registry.SlashArtsRegistry.NONE.get())) {
                        foundSALocation.set(saKey);
                    }
                }
            }
        });

        // 如果没有找到有效的SA，则不处理
        if (foundSALocation.get() == null) {
            return;
        }

        // 创建输出物品（耀魂宝珠的副本）
        ItemStack output = leftItem.copy();

        // 设置SA信息到NBT
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

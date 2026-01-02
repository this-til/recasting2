package com.til.recasting.handler;

import com.til.recasting.Recasting;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.ReachModifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;
import java.util.UUID;

/**
 * 处理物品属性修改事件，修改 ItemSlashBlade 的攻击距离
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemAttributeModifierHandler {

    private static final UUID PLAYER_REACH_AMPLIFIER = UUID.fromString("2D988C13-595B-4E58-B254-39BB6FA077FE");

    /**
     * 监听物品属性修改事件，修改 ItemSlashBlade 的攻击距离
     * 使用 LOWEST 优先级确保在 ItemSlashBlade 添加属性之后执行
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        // 只处理主手槽位
        if (event.getSlotType() != EquipmentSlot.MAINHAND) {
            return;
        }

        // 只处理 SlashBlade 物品
        if (!(event.getItemStack().getItem() instanceof ItemSlashBlade)) {
            return;
        }

        event.getItemStack()
                .getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                .ifPresent(
                        extension -> {
                            float attackDistance = extension.attackDistance();

                            // 获取刀的状态以确定是否断刀
                            event.getItemStack().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(
                                    state -> {
                                        // 获取基础的 reach 值（断刀或正常刀）
                                        double baseReach = state.isBroken()
                                                ? ReachModifier.BrokendReach()
                                                : ReachModifier.BladeReach();

                                        // 查找并移除原有的 ENTITY_REACH 修改器
                                        event.getModifiers()
                                                .get(ForgeMod.ENTITY_REACH.get()).stream()
                                                .filter(Objects::nonNull)
                                                .filter(modifier -> modifier.getId().equals(PLAYER_REACH_AMPLIFIER))
                                                .findFirst()
                                                .ifPresent(originalModifier -> event.removeModifier(ForgeMod.ENTITY_REACH.get(), originalModifier));

                                        // 按比例缩放：新值 = 基础值 * attackDistance + 1
                                        // +1 用于保持与原始计算的兼容性
                                        double newReach = baseReach * attackDistance + 1;

                                        // 添加新的修改器
                                        event.addModifier(
                                                ForgeMod.ENTITY_REACH.get(),
                                                new AttributeModifier(
                                                        PLAYER_REACH_AMPLIFIER,
                                                        "Reach amplifer",
                                                        newReach,
                                                        AttributeModifier.Operation.ADDITION
                                                )
                                        );
                                    }
                            );
                        }
                );
    }
}


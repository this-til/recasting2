package com.til.recasting.handler;

import com.til.recasting.Recasting;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.ReachModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

/**
 * 按刀扩展 {@code attackDistance} 缩放拔刀剑的 {@link Attributes#ENTITY_INTERACTION_RANGE}。
 */
@EventBusSubscriber(modid = Recasting.MODID)
public final class ItemAttributeModifierHandler {

    private static final ResourceLocation PLAYER_REACH_AMPLIFIER = ResourceLocation.fromNamespaceAndPath(
            SlashBlade.MODID,
            "mainhand_reach"
    );

    private ItemAttributeModifierHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof ItemSlashBlade)) {
            return;
        }

        BladeStateAccess.of(stack).ifPresent(state -> {
            float attackDistance = AttackHelper.propertiesOf(stack).attackDistance();
            double baseReach = state.isBroken()
                    ? ReachModifier.BrokendReach()
                    : ReachModifier.BladeReach();
            double newReach = baseReach * attackDistance + 1.0;

            event.replaceModifier(
                    Attributes.ENTITY_INTERACTION_RANGE,
                    new AttributeModifier(
                            PLAYER_REACH_AMPLIFIER,
                            newReach,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    EquipmentSlotGroup.MAINHAND
            );
        });
    }
}

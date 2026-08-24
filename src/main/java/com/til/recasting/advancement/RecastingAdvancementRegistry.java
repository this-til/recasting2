package com.til.recasting.advancement;

import com.til.recasting.Recasting;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * 成就用 ItemSubPredicate 类型注册。
 */
@EventBusSubscriber(modid = Recasting.MODID)
public final class RecastingAdvancementRegistry {

    private RecastingAdvancementRegistry() {
    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(Registries.ITEM_SUB_PREDICATE_TYPE, helper -> {
            helper.register(Recasting.prefix("se_crystal"),
                    new ItemSubPredicate.Type<>(SeCrystalItemPredicate.CODEC));
            helper.register(Recasting.prefix("named_slashblade"),
                    new ItemSubPredicate.Type<>(NamedSlashBladeItemPredicate.CODEC));
            helper.register(Recasting.prefix("blade_stat"),
                    new ItemSubPredicate.Type<>(BladeStatItemPredicate.CODEC));
            helper.register(Recasting.prefix("enchanted_slashblade"),
                    new ItemSubPredicate.Type<>(EnchantedSlashBladeItemPredicate.CODEC));
            helper.register(Recasting.prefix("slash_arts_sphere"),
                    new ItemSubPredicate.Type<>(SlashArtsSphereItemPredicate.CODEC));
        });
    }
}

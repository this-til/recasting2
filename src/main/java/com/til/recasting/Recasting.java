package com.til.recasting;

import com.mojang.logging.LogUtils;
import com.til.recasting.advancement.RecastingCriteriaTriggers;
import com.til.recasting.network.NetworkManager;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingComboStateRegistry;
import com.til.recasting.registry.RecastingCreativeTabs;
import com.til.recasting.registry.RecastingDataComponents;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.RecastingMenus;
import com.til.recasting.registry.RecastingParticleTypes;
import com.til.recasting.registry.RecastingRecipeSerializers;
import com.til.recasting.registry.RecastingEntityDataSerializers;
import com.til.recasting.registry.SlashArtsRegistry;
import com.til.recasting.registry.SpecialEffectsRegistry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

/**
 * Recasting 1.21 NeoForge：P4 系统玩法（物品/铁砧/掉落/进度）。
 */
@Mod(Recasting.MODID)
public class Recasting {

    public static final String MODID = "recasting";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Recasting(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        modEventBus.addListener(NetworkManager::register);

        RecastingAttachments.ATTACHMENT_TYPES.register(modEventBus);
        RecastingDataComponents.DATA_COMPONENTS.register(modEventBus);
        RecastingItems.ITEMS.register(modEventBus);
        RecastingMenus.MENUS.register(modEventBus);
        RecastingRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        RecastingRecipeSerializers.INGREDIENT_TYPES.register(modEventBus);
        RecastingCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        RecastingCriteriaTriggers.TRIGGERS.register(modEventBus);
        RecastingEntityDataSerializers.ENTITY_DATA_SERIALIZERS.register(modEventBus);
        RecastingEntities.ENTITY_TYPES.register(modEventBus);
        RecastingBuffTypes.BUFF_TYPES.register(modEventBus);
        RecastingAttackTypes.ATTACK_TYPES.register(modEventBus);

        RecastingParticleTypes.PARTICLE_TYPES.register(modEventBus);

        // 注册顺序：SlashArts → ComboState → SpecialEffects（与 1.20 一致）
        SlashArtsRegistry.SLASH_ARTS.register(modEventBus);
        RecastingComboStateRegistry.COMBO_STATE.register(modEventBus);
        SpecialEffectsRegistry.SPECIAL_EFFECT.register(modEventBus);

        LOGGER.info("Recasting {} loaded (P4 systems)", modContainer.getModInfo().getVersion());
    }

    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}

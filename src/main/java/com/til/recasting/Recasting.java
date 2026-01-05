package com.til.recasting;

import com.til.recasting.registry.*;
import lombok.extern.log4j.Log4j2;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

@Log4j2
@Mod(Recasting.MODID)
public class Recasting {

    public static final String MODID = "recasting";

    public Recasting() {

        @SuppressWarnings("removal")
        FMLJavaModLoadingContext fmlJavaModLoadingContext = FMLJavaModLoadingContext.get();
        IEventBus modEventBus = fmlJavaModLoadingContext.getModEventBus();

        fmlJavaModLoadingContext.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        RecastingEntityDataSerializers.ENTITY_DATA_SERIALIZERS.register(modEventBus);

        // 注册实体类型
        RecastingEntities.ENTITY_TYPES.register(modEventBus);

        // 注册攻击类型注册表
        RecastingAttackTypes.ATTACK_TYPES.register(modEventBus);

        // 注册 Slash Arts (SA) 注册表
        // 注意：必须在 ComboState 之前注册，因为 ComboState 依赖于 SlashArts
        SlashArtsRegistry.SLASH_ARTS.register(modEventBus);

        // 注册 Combo State 注册表
        // 注意：必须在 SlashArts 之后注册，因为它会调用 SlashArts 的实例方法
        RecastingComboStateRegistry.COMBO_STATE.register(modEventBus);

        // 注册 Special Effects (SE) 注册表
        SpecialEffectsRegistry.SPECIAL_EFFECT.register(modEventBus);

        // 注册物品
        RecastingItems.ITEMS.register(modEventBus);


        modEventBus.addListener(EventPriority.HIGH, RecastingItems::onBuildCreativeModeTabContents);
    }

    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(Recasting.MODID, path);
    }
}

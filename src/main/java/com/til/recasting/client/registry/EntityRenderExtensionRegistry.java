package com.til.recasting.client.registry;

import com.til.recasting.Recasting;
import com.til.recasting.client.renderer.EntityRenderExtension;
import com.til.recasting.constant.R;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.registry.RecastingBuffTypes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.awt.*;
import java.util.function.Supplier;

/**
 * 实体渲染扩展注册表（客户端专用）
 * 用于注册和管理实体渲染扩展
 */
public class EntityRenderExtensionRegistry {

    /**
     * 实体渲染扩展注册表键
     */
    public static final ResourceKey<Registry<EntityRenderExtension>> ENTITY_RENDER_EXTENSION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Recasting.prefix("entity_render_extension"));

    /**
     * 实体渲染扩展注册表
     */
    public static final DeferredRegister<EntityRenderExtension> ENTITY_RENDER_EXTENSIONS =
            DeferredRegister.create(ENTITY_RENDER_EXTENSION_REGISTRY_KEY, Recasting.MODID);

    /**
     * 实体渲染扩展注册表实例
     */
    public static final Supplier<IForgeRegistry<EntityRenderExtension>> REGISTRY =
            ENTITY_RENDER_EXTENSIONS.makeRegistry(() -> new RegistryBuilder<EntityRenderExtension>()
                    .setDefaultKey(Recasting.prefix("default"))
            );

    // ==================== 预定义的渲染扩展 ====================

    /**
     * 星闪渲染扩展
     * 为拥有星闪buff的实体渲染层级效果
     */
    public static final RegistryObject<EntityRenderExtension> STAR_BLINK = ENTITY_RENDER_EXTENSIONS.register(
            "star_blink",
            () -> new EntityRenderExtension.BuffLevelRender(
                    R.Models.Mark.starBlink$obj,
                    SummondSwordEntity.defaultTexture,
                    new Color(210, 118, 246).getRGB(),
                    RecastingBuffTypes.STAR_BLINK
            )
    );

}


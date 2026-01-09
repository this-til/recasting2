package com.til.recasting.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderStateManage extends BladeRenderState {

    public RenderStateManage(String p_i225973_1_, Runnable p_i225973_2_, Runnable p_i225973_3_) {
        super(p_i225973_1_, p_i225973_2_, p_i225973_3_);
    }


    // 与 1.12.5 原版相同的混合模式: GL_SRC_ALPHA, GL_ONE, GL_ONE, GL_ZERO
    // 用于发光效果的加法混合
    protected static final RenderStateShard.TransparencyStateShard ADDITIVE_TRANSPARENCY =
            new RenderStateShard.TransparencyStateShard("additive_transparency", () -> {
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(
                        GlStateManager.SourceFactor.SRC_ALPHA,
                        GlStateManager.DestFactor.ONE,
                        GlStateManager.SourceFactor.ONE,
                        GlStateManager.DestFactor.ZERO
                );
            }, () -> {
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            });

    public static RenderType mackLuminous(ResourceLocation texture) {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                .setOutputState(ITEM_ENTITY_TARGET)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, true, true))
                .setTransparencyState(ADDITIVE_TRANSPARENCY)
                // 移除 lightmap 以禁用全局光照影响，物体将始终保持最大亮度
                .setOverlayState(OVERLAY)
                .setWriteMaskState(COLOR_DEPTH_WRITE)
                .createCompositeState(false);

        return RenderType.create("luminous_" + texture, DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, false, true, state);
    }

}

package com.til.recasting.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.til.recasting.Recasting;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;

import org.jetbrains.annotations.Nullable;
import java.io.IOException;

/**
 * 模组核心着色器注册与访问。
 * <p>
 * BladeRift 式 SDF HDR 裂隙着色器对齐 FantasyDesire {@code fd_blade_rift}。
 */
@Log4j2
@OnlyIn(Dist.CLIENT)
public final class RecastingShaderHandler {

    @Nullable
    private static ShaderInstance bladeRift;

    private RecastingShaderHandler() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(RecastingShaderHandler::onRegisterShaders);
    }

    @Nullable
    public static ShaderInstance getBladeRift() {
        return bladeRift;
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            ShaderInstance shader = new ShaderInstance(
                    event.getResourceProvider(),
                    Recasting.prefix("blade_rift"),
                    DefaultVertexFormat.POSITION_COLOR_TEX
            );
            event.registerShader(shader, loaded -> bladeRift = loaded);
        } catch (IOException e) {
            log.error("Failed to register blade_rift shader", e);
            bladeRift = null;
        }
    }
}

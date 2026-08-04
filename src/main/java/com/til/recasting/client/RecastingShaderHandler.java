package com.til.recasting.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.til.recasting.Recasting;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * 模组核心着色器注册与访问（HDR 伪泛光等）。
 */
@Log4j2
@OnlyIn(Dist.CLIENT)
public final class RecastingShaderHandler {

    @Nullable
    private static ShaderInstance particleBloom;

    @Nullable
    private static ShaderInstance beamBloom;

    private RecastingShaderHandler() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(RecastingShaderHandler::onRegisterShaders);
    }

    @Nullable
    public static ShaderInstance getParticleBloom() {
        return particleBloom;
    }

    @Nullable
    public static ShaderInstance getBeamBloom() {
        return beamBloom;
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            ShaderInstance shader = new ShaderInstance(
                    event.getResourceProvider(),
                    Recasting.prefix("particle_bloom"),
                    DefaultVertexFormat.PARTICLE
            );
            event.registerShader(shader, loaded -> particleBloom = loaded);
        } catch (IOException e) {
            log.error("Failed to register particle_bloom shader", e);
            particleBloom = null;
        }

        try {
            ShaderInstance shader = new ShaderInstance(
                    event.getResourceProvider(),
                    Recasting.prefix("beam_bloom"),
                    DefaultVertexFormat.POSITION_COLOR_TEX
            );
            event.registerShader(shader, loaded -> beamBloom = loaded);
        } catch (IOException e) {
            log.error("Failed to register beam_bloom shader", e);
            beamBloom = null;
        }
    }
}

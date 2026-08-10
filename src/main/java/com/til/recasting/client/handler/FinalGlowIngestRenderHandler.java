package com.til.recasting.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.til.recasting.Recasting;
import com.til.recasting.client.effect.FinalGlowIngestClientEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * 末辉黑洞吞噬方块：客户端本地方块模型吸附渲染。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class FinalGlowIngestRenderHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        FinalGlowIngestClientEffects.tick();
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        List<FinalGlowIngestClientEffects.Debris> debrisList = FinalGlowIngestClientEffects.snapshot();
        if (debrisList.isEmpty()) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        Vec3 camera = event.getCamera().getPosition();
        float partialTick = event.getPartialTick();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        for(FinalGlowIngestClientEffects.Debris debris : debrisList) {
            BlockState state = debris.state();
            if (state.getRenderShape() == RenderShape.INVISIBLE) {
                continue;
            }
            Vec3 pos = debris.renderPos(partialTick);
            BlockPos lightPos = BlockPos.containing(pos);
            int light = LevelRenderer.getLightColor(minecraft.level, lightPos);

            poseStack.pushPose();
            poseStack.translate(pos.x - camera.x - 0.5, pos.y - camera.y - 0.5, pos.z - camera.z - 0.5);
            minecraft.getBlockRenderer().renderSingleBlock(
                    state,
                    poseStack,
                    bufferSource,
                    light,
                    OverlayTexture.NO_OVERLAY,
                    ModelData.EMPTY,
                    null
            );
            poseStack.popPose();
        }
        bufferSource.endBatch();
    }
}

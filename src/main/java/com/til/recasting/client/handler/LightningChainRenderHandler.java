package com.til.recasting.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.til.recasting.Recasting;
import com.til.recasting.client.effect.LightningChainClientEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * 闪电链折线自定义渲染（沿 start→end 分段抖动的发光四边形带）。
 * <p>
 * 使用发光线段公共渲染器的专用批绘 Buffer，避免与假人伤害数字等共享 BufferSource。
 * 亮色走加法混合；暗色（低亮度）走普通 Alpha 混合，避免「越黑越淡」。
 */
@EventBusSubscriber(modid = Recasting.MODID, value = Dist.CLIENT)
public class LightningChainRenderHandler {

    private static final float CORE_HALF_WIDTH = 0.04f;
    private static final float SHEATH_HALF_WIDTH = 0.14f;
    /**
     * 低于此亮度视为暗色闪电，改用半透明混合而非加法
     */
    private static final float DARK_LUMINANCE_THRESHOLD = 0.18f;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        LightningChainClientEffects.tick();
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        List<LightningChainClientEffects.Bolt> bolts = LightningChainClientEffects.snapshot();
        if (bolts.isEmpty()) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        Matrix4f matrix = poseStack.last().pose();
        Vec3 camera = event.getCamera().getPosition();
        long gameTime = minecraft.level.getGameTime();
        float partialTick = event.getPartialTick();

        List<LightningChainClientEffects.Bolt> brightBolts = new ArrayList<>();
        List<LightningChainClientEffects.Bolt> darkBolts = new ArrayList<>();
        for(LightningChainClientEffects.Bolt bolt : bolts) {
            if (bolt.alpha(gameTime, partialTick) <= 0.01f) {
                continue;
            }
            if (isDarkColor(bolt.color())) {
                darkBolts.add(bolt);
            } else {
                brightBolts.add(bolt);
            }
        }

        if (!brightBolts.isEmpty()) {
            CameraFacingBeamRenderer.begin(CameraFacingBeamRenderer.BlendMode.ADDITIVE);
            for(LightningChainClientEffects.Bolt bolt : brightBolts) {
                drawBolt(matrix, camera, bolt, gameTime, partialTick, true);
            }
            CameraFacingBeamRenderer.end();
        }

        if (!darkBolts.isEmpty()) {
            CameraFacingBeamRenderer.begin(CameraFacingBeamRenderer.BlendMode.TRANSLUCENT);
            for(LightningChainClientEffects.Bolt bolt : darkBolts) {
                drawBolt(matrix, camera, bolt, gameTime, partialTick, false);
            }
            CameraFacingBeamRenderer.end();
        }
    }

    private static void drawBolt(
            Matrix4f matrix,
            Vec3 camera,
            LightningChainClientEffects.Bolt bolt,
            long gameTime,
            float partialTick,
            boolean bright
    ) {
        float alpha = bolt.alpha(gameTime, partialTick);
        int color = bolt.color();
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        float coreR;
        float coreG;
        float coreB;
        if (bright) {
            coreR = 0.85f;
            coreG = 0.95f;
            coreB = 1.0f;
        } else {
            // 暗色保留刀色/虚空色，核略提亮以便与鞘区分，但不退回白芯
            coreR = Math.min(1.0f, r + 0.12f);
            coreG = Math.min(1.0f, g + 0.12f);
            coreB = Math.min(1.0f, b + 0.14f);
        }

        Vec3[] points = bolt.points();
        CameraFacingBeamRenderer.drawPolyline(
                matrix, camera, points, SHEATH_HALF_WIDTH, r, g, b, alpha * (bright
                        ? 0.5f
                        : 0.7f)
        );
        CameraFacingBeamRenderer.drawPolyline(
                matrix, camera, points, CORE_HALF_WIDTH, coreR, coreG, coreB, alpha * (bright
                        ? 1.0f
                        : 0.9f)
        );

        for(int i = 2; i < points.length - 2; i += 2) {
            if (!shouldDrawBranch(bolt.seed(), i)) {
                continue;
            }
            Vec3 mid = points[i];
            Vec3 dir = points[i + 1].subtract(points[i - 1]);
            if (dir.lengthSqr() <= 1.0E-8) {
                continue;
            }
            dir = dir.normalize();
            Vec3 side = orthogonal(dir, bolt.seed(), i).scale(branchLengthScale(bolt.seed(), i));
            Vec3 branchEnd = mid.add(side);
            CameraFacingBeamRenderer.drawQuad(
                    matrix, camera, mid, branchEnd,
                    SHEATH_HALF_WIDTH * 0.6f, r, g, b, alpha * (bright
                            ? 0.35f
                            : 0.55f)
            );
            CameraFacingBeamRenderer.drawQuad(
                    matrix, camera, mid, branchEnd,
                    CORE_HALF_WIDTH * 0.6f, coreR, coreG, coreB, alpha * (bright
                            ? 0.7f
                            : 0.8f)
            );
        }
    }

    private static boolean isDarkColor(int color) {
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float luminance = 0.2126f * r + 0.7152f * g + 0.0722f * b;
        return luminance < DARK_LUMINANCE_THRESHOLD;
    }

    private static boolean shouldDrawBranch(long seed, int segmentIndex) {
        return hash(seed, segmentIndex, 0) % 3 == 0;
    }

    private static float branchLengthScale(long seed, int segmentIndex) {
        return 0.6f + hashUnit(seed, segmentIndex, 1) * 0.8f;
    }

    private static Vec3 orthogonal(Vec3 dir, long seed, int segmentIndex) {
        Vec3 side = dir.cross(new Vec3(0.0, 1.0, 0.0));
        if (side.lengthSqr() <= 1.0E-8) {
            side = dir.cross(new Vec3(1.0, 0.0, 0.0));
        }
        side = side.normalize();
        double angle = hashUnit(seed, segmentIndex, 2) * Math.PI * 2.0;
        Vec3 bitangent = dir.cross(side).normalize();
        return side.scale(Math.cos(angle)).add(bitangent.scale(Math.sin(angle))).normalize();
    }

    private static int hash(long seed, int segmentIndex, int salt) {
        long mixed = seed + ((long) segmentIndex << 32) + salt * 0x9E3779B9L + 0x9E3779B97F4A7C15L;
        mixed = (mixed ^ (mixed >>> 30)) * 0xBF58476D1CE4E5B9L;
        mixed = (mixed ^ (mixed >>> 27)) * 0x94D049BB133111EBL;
        mixed ^= mixed >>> 31;
        return (int) mixed;
    }

    private static float hashUnit(long seed, int segmentIndex, int salt) {
        return (hash(seed, segmentIndex, salt) & 0x7FFFFFFF) / (float) Integer.MAX_VALUE;
    }

}

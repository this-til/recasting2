package com.til.recasting.client.handler;

import com.til.recasting.Recasting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

/**
 * 时之彼端：客户端按真实时间每帧推进 dayTime，避免仅按 tick 跳变。
 */
@EventBusSubscriber(modid = Recasting.MODID, value = Dist.CLIENT)
public final class TimeBeyondClientTimeHandler {

    private static final double MAX_FRAME_SECONDS = 0.25;

    private static boolean active;
    private static int multiplier = 32;
    private static long anchorDayTime;
    private static long anchorNanoTime;

    private TimeBeyondClientTimeHandler() {
    }

    public static void start(int timeMultiplier, long dayTime) {
        active = true;
        multiplier = Math.max(1, timeMultiplier);
        anchorDayTime = dayTime;
        anchorNanoTime = System.nanoTime();
        applyDayTime(dayTime);
    }

    public static void stop(long dayTime) {
        active = false;
        applyDayTime(dayTime);
    }

    public static boolean isActive() {
        return active;
    }

    @SubscribeEvent
    public static void onRenderFrame(RenderFrameEvent.Pre event) {
        if (!active) {
            return;
        }

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        long now = System.nanoTime();
        double elapsedSeconds = (now - anchorNanoTime) / 1_000_000_000.0;
        if (elapsedSeconds < 0.0) {
            anchorNanoTime = now;
            return;
        }
        if (elapsedSeconds > MAX_FRAME_SECONDS * 8) {
            anchorDayTime = level.getDayTime();
            anchorNanoTime = now;
            return;
        }

        long visualDayTime = anchorDayTime + (long) Math.floor(elapsedSeconds * 20.0 * multiplier);
        if (level.getDayTime() != visualDayTime) {
            level.setDayTime(visualDayTime);
        }
    }

    private static void applyDayTime(long dayTime) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        level.setDayTime(dayTime);
    }
}

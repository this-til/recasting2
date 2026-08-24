package com.til.recasting.client.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 客户端光棱线段特效缓冲
 */
@OnlyIn(Dist.CLIENT)
public final class PrismBeamClientEffects {

    private static final List<Beam> BEAMS = new ArrayList<>();
    private static final List<Beam> BEAMS_VIEW = Collections.unmodifiableList(BEAMS);

    private PrismBeamClientEffects() {
    }

    public static void add(Vec3 start, Vec3 end, int color, int lifeTicks) {
        long now = Minecraft.getInstance().level == null
                ? 0L
                : Minecraft.getInstance().level.getGameTime();
        BEAMS.add(new Beam(start, end, color, Math.max(1, lifeTicks), now));
    }

    public static void tick() {
        if (Minecraft.getInstance().level == null) {
            BEAMS.clear();
            return;
        }
        long now = Minecraft.getInstance().level.getGameTime();
        Iterator<Beam> iterator = BEAMS.iterator();
        while (iterator.hasNext()) {
            Beam beam = iterator.next();
            if (now - beam.startGameTime() >= beam.lifeTicks()) {
                iterator.remove();
            }
        }
    }

    public static List<Beam> snapshot() {
        return BEAMS_VIEW;
    }

    public record Beam(Vec3 start, Vec3 end, int color, int lifeTicks, long startGameTime) {

        public float alpha(long gameTime, float partialTick) {
            float age = (float) (gameTime - startGameTime) + partialTick;
            float life = lifeTicks;
            if (life <= 0.0f) {
                return 0.0f;
            }
            float t = age / life;
            if (t <= 0.0f) {
                return 1.0f;
            }
            if (t >= 1.0f) {
                return 0.0f;
            }
            // 前半保持，后半淡出
            if (t < 0.35f) {
                return 1.0f;
            }
            return 1.0f - (t - 0.35f) / 0.65f;
        }
    }
}

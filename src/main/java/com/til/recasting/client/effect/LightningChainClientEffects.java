package com.til.recasting.client.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 客户端闪电链折线特效缓冲
 */
@OnlyIn(Dist.CLIENT)
public final class LightningChainClientEffects {

    private static final List<Bolt> BOLTS = new ArrayList<>();

    private LightningChainClientEffects() {
    }

    public static void add(Vec3 start, Vec3 end, int color, long seed, int lifeTicks) {
        long now = Minecraft.getInstance().level == null
                ? 0L
                : Minecraft.getInstance().level.getGameTime();
        BOLTS.add(new Bolt(start, end, color, seed, Math.max(1, lifeTicks), now));
    }

    public static void tick() {
        if (Minecraft.getInstance().level == null) {
            BOLTS.clear();
            return;
        }
        long now = Minecraft.getInstance().level.getGameTime();
        Iterator<Bolt> iterator = BOLTS.iterator();
        while (iterator.hasNext()) {
            Bolt bolt = iterator.next();
            if (now - bolt.startGameTime() >= bolt.lifeTicks()) {
                iterator.remove();
            }
        }
    }

    public static List<Bolt> snapshot() {
        return List.copyOf(BOLTS);
    }

    public record Bolt(Vec3 start, Vec3 end, int color, long seed, int lifeTicks, long startGameTime) {

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
            if (t < 0.25f) {
                return 1.0f;
            }
            return 1.0f - (t - 0.25f) / 0.75f;
        }
    }
}

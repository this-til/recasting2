package com.til.recasting.client.effect;

import com.til.recasting.network.FinalGlowIngestMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 末辉黑洞吞噬方块：仅客户端缓冲，按起始坐标向黑洞中心吸附渲染。
 */
@OnlyIn(Dist.CLIENT)
public final class FinalGlowIngestClientEffects {

    private static final int MAX_DEBRIS = 384;
    private static final double STEP_PER_TICK = 1.5;
    private static final List<Debris> DEBRIS = new ArrayList<>();
    private static final List<Debris> DEBRIS_VIEW = Collections.unmodifiableList(DEBRIS);

    private FinalGlowIngestClientEffects() {
    }

    public static void addBatch(int holeEntityId, float absorbRadius, List<FinalGlowIngestMessage.Entry> entries) {
        if (entries == null || entries.isEmpty()) {
            return;
        }
        for(FinalGlowIngestMessage.Entry entry : entries) {
            BlockState state = entry.state();
            if (state == null || state.isAir()) {
                continue;
            }
            BlockPos pos = entry.pos();
            Vec3 start = Vec3.atCenterOf(pos);
            DEBRIS.add(new Debris(state, start, start, holeEntityId, Math.max(0.25f, absorbRadius), 0));
        }
        trimOverflow();
    }

    public static void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            DEBRIS.clear();
            return;
        }

        Iterator<Debris> iterator = DEBRIS.iterator();
        while (iterator.hasNext()) {
            Debris debris = iterator.next();
            Entity hole = minecraft.level.getEntity(debris.holeEntityId);
            if (hole == null || hole.isRemoved()) {
                iterator.remove();
                continue;
            }

            debris.prevPos = debris.pos;
            Vec3 center = hole.position();
            double dx = center.x - debris.pos.x;
            double dy = center.y - debris.pos.y;
            double dz = center.z - debris.pos.z;
            double distSqr = dx * dx + dy * dy + dz * dz;
            double absorbSqr = debris.absorbRadius * debris.absorbRadius;
            if (distSqr <= absorbSqr) {
                iterator.remove();
                continue;
            }

            double dist = Math.sqrt(distSqr);
            double step = Math.min(STEP_PER_TICK, dist);
            double inv = step / dist;
            debris.pos = new Vec3(
                    debris.pos.x + dx * inv,
                    debris.pos.y + dy * inv,
                    debris.pos.z + dz * inv
            );
            debris.age++;
            if (debris.age > 200) {
                iterator.remove();
            }
        }
    }

    public static List<Debris> snapshot() {
        return DEBRIS_VIEW;
    }

    public static void clear() {
        DEBRIS.clear();
    }

    private static void trimOverflow() {
        int overflow = DEBRIS.size() - MAX_DEBRIS;
        if (overflow <= 0) {
            return;
        }
        DEBRIS.subList(0, overflow).clear();
    }

    public static final class Debris {
        private final BlockState state;
        private Vec3 prevPos;
        private Vec3 pos;
        private final int holeEntityId;
        private final float absorbRadius;
        private int age;

        private Debris(BlockState state, Vec3 prevPos, Vec3 pos, int holeEntityId, float absorbRadius, int age) {
            this.state = state;
            this.prevPos = prevPos;
            this.pos = pos;
            this.holeEntityId = holeEntityId;
            this.absorbRadius = absorbRadius;
            this.age = age;
        }

        public BlockState state() {
            return state;
        }

        public Vec3 renderPos(float partialTick) {
            return new Vec3(
                    Mth.lerp(partialTick, prevPos.x, pos.x),
                    Mth.lerp(partialTick, prevPos.y, pos.y),
                    Mth.lerp(partialTick, prevPos.z, pos.z)
            );
        }
    }
}

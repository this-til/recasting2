package com.til.recasting.entity;

import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.AttractionHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.util.DamageStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 末辉终焉超新星爆：视界坍缩后爆炸。
 * <p>
 * 时间线（与 {@link #getMaxLifeTime()} 同步）：视界 16→0、吸积粒子球半径 24→0；
 * 实体伤害走本模组 {@link AttackHelper}；原版爆炸只负责破方块（不出伤）。
 * 爆炸实体列表清理见 {@link com.til.recasting.handler.FinalGlowBlackHoleHandler}。
 */
public class FinalGlowBlackHoleEntity extends JudgementCutEntity {

    private static final int COLLAPSE_TICKS = 100;
    /** 视界半径（同步到 size）：16 → 0，归零后爆炸。 */
    private static final float HORIZON_START = 16.0f;
    private static final float HORIZON_END = 0.0f;
    /** 球形吸积粒子发射半径：24 → 0。 */
    private static final float PARTICLE_RADIUS_START = 24.0f;
    private static final float PARTICLE_RADIUS_END = 0.0f;
    /** 实体吸附、方块吞噬、终结伤害。 */
    private static final float EFFECT_RANGE = 64.0f;
    /** 终结原版爆炸强度（仅破方块）。 */
    private static final float EXPLOSION_POWER = 256.0f;
    private static final float PULL_POWER = 0.02f;
    private static final int RAYS_PER_TICK = 96;
    private static final float ABSORB_RADIUS = 2.0f;
    private static final float DAMAGE_RATIO = 3.35f;

    private final Set<FallingBlockEntity> spawnedFallingBlocks = Collections.newSetFromMap(new IdentityHashMap<>());
    private boolean detonated;
    private boolean clientDetonationFxSpawned;

    public FinalGlowBlackHoleEntity(
            EntityType<? extends FinalGlowBlackHoleEntity> entityTypeIn,
            Level worldIn,
            LivingEntity shooting
    ) {
        super(entityTypeIn, worldIn, shooting);
        setMaxLifeTime(COLLAPSE_TICKS);
        setSize(HORIZON_START);
        setModifiedRatio(DAMAGE_RATIO);
        setMute(true);
        setRepeatedAttack(false);
        addAttackType(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get());
    }

    /**
     * 坍缩进度 0→1，客户端与服务端同一公式（基于 tickCount / maxLifeTime）。
     */
    public float collapseProgress(float partialTick) {
        int life = Math.max(1, getMaxLifeTime());
        return Mth.clamp((tickCount + partialTick) / (float) life, 0.0f, 1.0f);
    }

    public float horizonRadius(float partialTick) {
        return Mth.lerp(collapseProgress(partialTick), HORIZON_START, HORIZON_END);
    }

    public float particleSpawnRadius(float partialTick) {
        return Mth.lerp(collapseProgress(partialTick), PARTICLE_RADIUS_START, PARTICLE_RADIUS_END);
    }

    @Override
    public void tick() {
        if (!completeSetup) {
            completeSetup = true;
            setUp();
        }

        float progress = collapseProgress(0.0f);
        float horizon = horizonRadius(0.0f);

        if (level().isClientSide()) {
            // 本地也写 size，避免网络延迟导致黑球不同步；服务端仍会覆盖同步
            setSize(horizon);
            updateClientVisual();
            baseTick();
            // discard/setRemoved 不会走 remove()；终结特效必须在客户端 tick 触发
            if (!clientDetonationFxSpawned && progress >= 1.0f) {
                spawnClientDetonationFx();
            }
            return;
        }

        baseTick();

        if (detonated || isRemoved()) {
            return;
        }

        LivingEntity shooter = getShooter();
        if (shooter == null || !shooter.isAlive()) {
            clearSpawnedFallingBlocks();
            discard();
            return;
        }

        setSize(horizon);
        attractEntities();
        absorbByRays(RAYS_PER_TICK);
        tickFallingBlocks();

        if (progress >= 1.0f) {
            detonate();
            discard();
        }
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        // 兜底：若客户端未先走到 progress>=1（异常移除），仍尝试播一次
        if (level().isClientSide()) {
            spawnClientDetonationFx();
        } else {
            clearSpawnedFallingBlocks();
        }
        super.remove(reason);
    }

    @Override
    public void onClientRemoval() {
        spawnClientDetonationFx();
        super.onClientRemoval();
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    private void updateClientVisual() {
        float horizon = Math.max(0.0f, horizonRadius(0.0f));
        float particleRadius = Math.max(0.0f, particleSpawnRadius(0.0f));
        double jx = getX() + (random.nextDouble() - 0.5) * 0.12 * Math.max(horizon, 0.5f);
        double jy = getY() + (random.nextDouble() - 0.5) * 0.12 * Math.max(horizon, 0.5f);
        double jz = getZ() + (random.nextDouble() - 0.5) * 0.12 * Math.max(horizon, 0.5f);

        int color = getColor().getRGB();
        float r = ((color >> 16) & 255) / 255.0f;
        float g = ((color >> 8) & 255) / 255.0f;
        float b = (color & 255) / 255.0f;
        DustParticleOptions dust = new DustParticleOptions(new Vector3f(r, g, b), 1.35f);
        DustParticleOptions darkCore = new DustParticleOptions(new Vector3f(0.05f, 0.02f, 0.02f), 2.2f);

        level().addParticle(darkCore, true, jx, jy, jz, 0.0, 0.0, 0.0);
        level().addParticle(dust, true, jx, jy, jz, 0.0, 0.0, 0.0);

        if (particleRadius < 0.25f) {
            return;
        }

        int count = 10 + (int) (particleRadius * 1.5f);
        for(int i = 0; i < count; i++) {
            Vec3 dir = randomUnitVector();
            double radius = particleRadius * (0.92 + random.nextDouble() * 0.08);
            double px = jx + dir.x * radius;
            double py = jy + dir.y * radius;
            double pz = jz + dir.z * radius;
            double vx = (jx - px) * 0.08;
            double vy = (jy - py) * 0.08;
            double vz = (jz - pz) * 0.08;
            level().addParticle(ParticleTypes.PORTAL, true, px, py, pz, vx, vy, vz);
            level().addParticle(ParticleTypes.END_ROD, true, px, py, pz, vx * 0.35, vy * 0.35, vz * 0.35);
            if (i % 2 == 0) {
                level().addParticle(dust, true, px, py, pz, 0.0, 0.0, 0.0);
            }
            if (i % 3 == 0) {
                level().addParticle(ParticleTypes.SMOKE, true, px, py, pz, vx * 0.4, vy * 0.4, vz * 0.4);
            }
        }
    }

    private void spawnClientDetonationFx() {
        if (clientDetonationFxSpawned) {
            return;
        }
        clientDetonationFxSpawned = true;
        Vec3 center = position();
        int color = getColor().getRGB();
        DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> com.til.recasting.client.effect.FinalGlowBlackHoleClientFx.spawnDetonation(center, color)
        );
    }

    private void attractEntities() {
        Vec3 center = position();
        AABB box = new AABB(center, center).inflate(EFFECT_RANGE);
        List<Entity> entities = level().getEntities(this, box, entity -> {
            if (entity == this || entity instanceof FallingBlockEntity) {
                return false;
            }
            LivingEntity shooter = getShooter();
            return shooter == null || entity != shooter;
        });
        for(Entity entity : entities) {
            AttractionHelper.applyRadialPull(center, entity, EFFECT_RANGE, PULL_POWER);
        }
    }

    private void absorbByRays(int rayCount) {
        Vec3 origin = position();
        boolean grief = level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);

        for(int i = 0; i < rayCount; i++) {
            Vec3 dir = randomUnitVector();
            BlockHitResult hit = level().clip(new ClipContext(
                    origin,
                    origin.add(dir.scale(EFFECT_RANGE)),
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    this
            ));
            if (hit.getType() != HitResult.Type.BLOCK) {
                continue;
            }
            BlockPos pos = hit.getBlockPos();
            BlockState state = level().getBlockState(pos);
            if (!isAbsorbable(state, pos)) {
                continue;
            }

            FallingBlockEntity falling = FallingBlockEntity.fall(level(), pos, state);
            configureFallingBlock(falling);
            if (falling.distanceToSqr(origin) <= ABSORB_RADIUS * ABSORB_RADIUS) {
                falling.discard();
            } else {
                spawnedFallingBlocks.add(falling);
            }

            if (!grief) {
                level().setBlock(pos, state, 3);
            }
        }
    }

    private Vec3 randomUnitVector() {
        double u = random.nextDouble();
        double v = random.nextDouble();
        double theta = 2.0 * Math.PI * u;
        double phi = Math.acos(2.0 * v - 1.0);
        double sinPhi = Math.sin(phi);
        return new Vec3(sinPhi * Math.cos(theta), Math.cos(phi), sinPhi * Math.sin(theta));
    }

    private boolean isAbsorbable(BlockState state, BlockPos pos) {
        if (state.isAir()) {
            return false;
        }
        if (state.is(Blocks.BEDROCK) || state.is(Blocks.BARRIER) || state.is(Blocks.COMMAND_BLOCK)
                || state.is(Blocks.CHAIN_COMMAND_BLOCK) || state.is(Blocks.REPEATING_COMMAND_BLOCK)
                || state.is(Blocks.STRUCTURE_BLOCK) || state.is(Blocks.JIGSAW)) {
            return false;
        }
        if (state.getDestroySpeed(level(), pos) < 0.0f) {
            return false;
        }
        if (state.getFluidState().getType() != Fluids.EMPTY && !state.getFluidState().isSource()) {
            return false;
        }
        return !state.is(BlockTags.PORTALS);
    }

    private void configureFallingBlock(FallingBlockEntity falling) {
        falling.noPhysics = true;
        falling.setNoGravity(true);
        falling.dropItem = false;
        falling.disableDrop();
        falling.setDeltaMovement(Vec3.ZERO);
    }

    /**
     * 每 tick 强制拽向中心；距中心 ≤ {@link #ABSORB_RADIUS}（±2 格）立即 {@code discard}，无概率。
     */
    private void tickFallingBlocks() {
        Vec3 center = position();
        double absorbRangeSqr = ABSORB_RADIUS * ABSORB_RADIUS;
        Iterator<FallingBlockEntity> iterator = spawnedFallingBlocks.iterator();
        while (iterator.hasNext()) {
            FallingBlockEntity falling = iterator.next();
            if (falling.isRemoved()) {
                iterator.remove();
                continue;
            }

            falling.noPhysics = true;
            falling.setNoGravity(true);
            falling.dropItem = false;

            double dx = center.x - falling.getX();
            double dy = center.y - falling.getY();
            double dz = center.z - falling.getZ();
            double distSqr = dx * dx + dy * dy + dz * dz;
            if (distSqr <= absorbRangeSqr) {
                falling.discard();
                iterator.remove();
                continue;
            }

            double dist = Math.sqrt(distSqr);
            double step = Math.min(1.5, dist);
            double inv = step / dist;
            falling.setPos(falling.getX() + dx * inv, falling.getY() + dy * inv, falling.getZ() + dz * inv);
            falling.setDeltaMovement(dx * inv, dy * inv, dz * inv);
            falling.hasImpulse = true;
        }
    }

    private void clearSpawnedFallingBlocks() {
        for(FallingBlockEntity falling : spawnedFallingBlocks) {
            if (falling.isAlive() && !falling.isRemoved()) {
                falling.discard();
            }
        }
        spawnedFallingBlocks.clear();
    }

    private void detonate() {
        if (detonated) {
            return;
        }
        detonated = true;

        clearSpawnedFallingBlocks();
        Vec3 center = position();

        LivingEntity shooter = getShooter();
        if (shooter != null) {
            AttackHelper.areaAttack(
                    shooter,
                    center,
                    new DamageStructure(DAMAGE_RATIO, 0.0f),
                    EFFECT_RANGE,
                    List.of(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get()),
                    null,
                    null
            );
        }

        // 原版爆炸：仅破方块（源为本实体，Detonate 清空实体列表）；不出原版粒子/音效，视觉走 ClientFx
        level().explode(
                this,
                null,
                null,
                center.x,
                center.y,
                center.z,
                EXPLOSION_POWER,
                false,
                Level.ExplosionInteraction.MOB,
                false
        );
    }
}

package com.til.recasting.entity;

import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.AttractionHelper;
import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingParticleTypes;
import com.til.recasting.util.DamageStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 末辉终焉超新星爆：坍缩黑洞，射线吞噬方块，终结时圆环迸发与范围伤害。
 */
public class FinalGlowBlackHoleEntity extends StandardizationAttackEntity {

    private static final int SHRINK_TICKS = 30;
    private static final float SIZE_START = 12.0f;
    private static final float SIZE_END = 1.5f;
    private static final float BLOCK_RADIUS = 32.0f;
    private static final float PULL_RANGE = 64.0f;
    private static final float PULL_POWER = 0.02f;
    private static final int RAYS_PER_TICK = 36;
    private static final float ABSORB_RADIUS = 6.0f;
    private static final float ABSORB_CHANCE = 0.07f;
    private static final float RING_WIDTH = 2.5f;
    private static final float DAMAGE_RANGE = 64.0f;
    private static final float DAMAGE_RATIO = 2.5f;

    private final Set<UUID> spawnedFallingBlocks = new HashSet<>();
    private boolean detonated;

    public FinalGlowBlackHoleEntity(
            EntityType<? extends FinalGlowBlackHoleEntity> entityTypeIn,
            Level worldIn,
            LivingEntity shooting
    ) {
        super(entityTypeIn, worldIn, shooting);
        setMaxLifeTime(100);
        setSize(SIZE_START);
        setModifiedRatio(DAMAGE_RATIO);
        setMute(true);
        addAttackType(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get());
    }

    @Override
    public void tick() {
        if (!completeSetup) {
            completeSetup = true;
            setUp();
        }

        if (level().isClientSide()) {
            updateClientVisual();
            baseTick();
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

        updateServerSize();
        attractEntities();
        absorbByRays(RAYS_PER_TICK);
        tickAbsorbNearCenter();

        if (getMaxLifeTime() < tickCount) {
            detonate();
            discard();
        }
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (!level().isClientSide()) {
            clearSpawnedFallingBlocks();
        }
        super.remove(reason);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    private void updateServerSize() {
        float t = Mth.clamp(tickCount / (float) SHRINK_TICKS, 0.0f, 1.0f);
        setSize(Mth.lerp(t, SIZE_START, SIZE_END));
    }

    private void updateClientVisual() {
        float t = Mth.clamp(tickCount / (float) SHRINK_TICKS, 0.0f, 1.0f);
        float size = Mth.lerp(t, SIZE_START, SIZE_END);
        double jitter = (random.nextDouble() - 0.5) * 0.08 * size;
        double jx = getX() + jitter;
        double jy = getY() + (random.nextDouble() - 0.5) * 0.08 * size;
        double jz = getZ() + jitter;

        int color = getColor().getRGB();
        float r = ((color >> 16) & 255) / 255.0f;
        float g = ((color >> 8) & 255) / 255.0f;
        float b = (color & 255) / 255.0f;
        DustParticleOptions dust = new DustParticleOptions(new Vector3f(r, g, b), 1.2f);

        int count = 8 + (int) (size * 2);
        for(int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double radius = size * (0.4 + random.nextDouble() * 0.8);
            double px = jx + Math.cos(angle) * radius;
            double py = jy + (random.nextDouble() - 0.5) * size * 0.4;
            double pz = jz + Math.sin(angle) * radius;
            double vx = (jx - px) * 0.08;
            double vy = (jy - py) * 0.08;
            double vz = (jz - pz) * 0.08;
            level().addParticle(ParticleTypes.PORTAL, px, py, pz, vx, vy, vz);
            if (i % 2 == 0) {
                level().addParticle(dust, px, py, pz, 0, 0, 0);
            }
            if (i % 3 == 0) {
                level().addParticle(ParticleTypes.SMOKE, px, py, pz, vx * 0.5, vy * 0.5, vz * 0.5);
            }
        }
    }

    private void attractEntities() {
        Vec3 center = position();
        AABB box = new AABB(center, center).inflate(PULL_RANGE);
        List<Entity> entities = level().getEntities(this, box, entity -> {
            if (entity == this) {
                return false;
            }
            LivingEntity shooter = getShooter();
            return shooter == null || entity != shooter;
        });
        for(Entity entity : entities) {
            AttractionHelper.applyRadialPull(center, entity, PULL_RANGE, PULL_POWER);
        }
    }

    private void absorbByRays(int rayCount) {
        Vec3 origin = position();
        boolean grief = level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);

        for(int i = 0; i < rayCount; i++) {
            Vec3 dir = randomUnitVector();
            BlockHitResult hit = level().clip(new ClipContext(
                    origin,
                    origin.add(dir.scale(BLOCK_RADIUS)),
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
            configureFallingBlock(falling, origin);
            spawnedFallingBlocks.add(falling.getUUID());

            // fall() 会清方块；非生物破坏规则时立即还原世界方块，仅保留视觉坠落实体
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

    private void configureFallingBlock(FallingBlockEntity falling, Vec3 center) {
        falling.noPhysics = true;
        falling.dropItem = false;
        falling.disableDrop();
        Vec3 toCenter = center.subtract(falling.position());
        double len = toCenter.length();
        if (len > 0.1) {
            falling.setDeltaMovement(toCenter.scale(0.35 / len));
        }
    }

    private void tickAbsorbNearCenter() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 center = position();
        double absorbRangeSqr = ABSORB_RADIUS * ABSORB_RADIUS;
        Iterator<UUID> iterator = spawnedFallingBlocks.iterator();
        while (iterator.hasNext()) {
            UUID id = iterator.next();
            Entity entity = serverLevel.getEntity(id);
            if (!(entity instanceof FallingBlockEntity falling) || !falling.isAlive()) {
                iterator.remove();
                continue;
            }
            if (falling.distanceToSqr(center) > absorbRangeSqr) {
                continue;
            }
            if (random.nextFloat() < ABSORB_CHANCE) {
                falling.discard();
                iterator.remove();
            }
        }
    }

    private void clearSpawnedFallingBlocks() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            spawnedFallingBlocks.clear();
            return;
        }
        for(UUID id : spawnedFallingBlocks) {
            Entity entity = serverLevel.getEntity(id);
            if (entity != null) {
                entity.discard();
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
        int color = getColor().getRGB() & 0xFFFFFF;

        spawnBurstRing(center, 128.0f, color);
        spawnBurstRing(center.add(12.0, 0.0, 0.0), 48.0f, color);
        spawnBurstRing(center.add(-12.0, 0.0, 0.0), 48.0f, color);

        LivingEntity shooter = getShooter();
        if (shooter != null) {
            AttackHelper.areaAttack(
                    shooter,
                    center,
                    new DamageStructure(DAMAGE_RATIO, 0.0f),
                    DAMAGE_RANGE,
                    List.of(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get()),
                    null,
                    null
            );
        }

        level().playSound(
                null,
                center.x,
                center.y,
                center.z,
                SoundEvents.GENERIC_EXPLODE,
                SoundSource.PLAYERS,
                4.0F,
                0.8F
        );
    }

    private void spawnBurstRing(Vec3 center, float radius, int colorRgb) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        ParticleHelper.sendParticlesLongRange(
                serverLevel,
                RecastingParticleTypes.BURST_RING.get(),
                center.x,
                center.y,
                center.z,
                0,
                radius,
                RING_WIDTH,
                colorRgb,
                1.0
        );
    }
}

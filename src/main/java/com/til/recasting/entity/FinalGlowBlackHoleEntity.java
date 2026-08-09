package com.til.recasting.entity;

import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.AttractionHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.util.DamageStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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
 * 末辉终焉超新星爆：坍缩黑洞，射线吞噬方块，终结时圆环迸发与范围伤害。
 * <p>
 * 吸积 / 终结圆环等视觉均在客户端实体 tick（及 remove）本地生成。
 */
public class FinalGlowBlackHoleEntity extends StandardizationAttackEntity {

    private static final int SHRINK_TICKS = 30;
    private static final float SIZE_START = 12.0f;
    private static final float SIZE_END = 1.5f;
    /** 方块吞噬与吸积盘粒子起点半径。 */
    private static final float ACCRETION_RADIUS = 48.0f;
    private static final float PULL_RANGE = 64.0f;
    private static final float PULL_POWER = 0.02f;
    private static final int RAYS_PER_TICK = 48;
    /** FallingBlockEntity 距中心 ≤ 此值（格）时立即销毁，无概率。 */
    private static final float ABSORB_RADIUS = 2.0f;
    private static final float DAMAGE_RANGE = 64.0f;
    private static final float DAMAGE_RATIO = 2.5f;

    private final Set<FallingBlockEntity> spawnedFallingBlocks = Collections.newSetFromMap(new IdentityHashMap<>());
    private boolean detonated;
    private boolean clientDetonationFxSpawned;

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
            if (!clientDetonationFxSpawned && getMaxLifeTime() < tickCount) {
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

        updateServerSize();
        attractEntities();
        absorbByRays(RAYS_PER_TICK);
        tickFallingBlocks();

        if (getMaxLifeTime() < tickCount) {
            detonate();
            discard();
        }
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (level().isClientSide()) {
            spawnClientDetonationFx();
        } else {
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
        double jx = getX() + (random.nextDouble() - 0.5) * 0.12 * size;
        double jy = getY() + (random.nextDouble() - 0.5) * 0.12 * size;
        double jz = getZ() + (random.nextDouble() - 0.5) * 0.12 * size;

        int color = getColor().getRGB();
        float r = ((color >> 16) & 255) / 255.0f;
        float g = ((color >> 8) & 255) / 255.0f;
        float b = (color & 255) / 255.0f;
        DustParticleOptions dust = new DustParticleOptions(new Vector3f(r, g, b), 1.35f);
        DustParticleOptions darkCore = new DustParticleOptions(new Vector3f(0.05f, 0.02f, 0.02f), 2.2f);

        level().addParticle(darkCore, true, jx, jy, jz, 0.0, 0.0, 0.0);
        level().addParticle(dust, true, jx, jy, jz, 0.0, 0.0, 0.0);

        int count = 14 + (int) (size * 3);
        for(int i = 0; i < count; i++) {
            // 水平圆环吸积：起点约 ACCRETION_RADIUS，向中心吸入
            double angle = random.nextDouble() * Math.PI * 2.0;
            double radius = ACCRETION_RADIUS * (0.92 + random.nextDouble() * 0.08);
            double px = jx + Math.cos(angle) * radius;
            double py = jy + (random.nextDouble() - 0.5) * size * 0.35;
            double pz = jz + Math.sin(angle) * radius;
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
        AABB box = new AABB(center, center).inflate(PULL_RANGE);
        List<Entity> entities = level().getEntities(this, box, entity -> {
            if (entity == this || entity instanceof FallingBlockEntity) {
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
                    origin.add(dir.scale(ACCRETION_RADIUS)),
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
}

package com.til.recasting.entity;

import com.til.recasting.registry.RecastingEntities;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.awt.*;

/**
 * [回到未来计划]群星坠落阵：自身不造成伤害，按模式跟随或禁锢锁定目标并刷追踪落星。
 * 落星仅攻击法阵锁定实体，不自动索敌。
 * 客户端粒子阵：仅边框（参考 CryptoMorin/XSeries 轮廓绘制，不做填充）。
 */
public class StarfallArrayEntity extends StandardizationAttackEntity {

    public static final int MODE_FOLLOW = 0;
    public static final int MODE_PIN = 1;

    /**
     * 与旧 OBJ 渲染一致：模型外延约 522，BASE_SCALE 0.02 → 半径约 10.44 格；
     * 实际绘制半径 = 该值 × {@link #getSize()}。
     */
    private static final double MODEL_EXTENT = 522.0;
    private static final double MODEL_BASE_SCALE = 0.02;
    private static final double OUTER_RADIUS = MODEL_EXTENT * MODEL_BASE_SCALE;
    private static final double STAR_RADIUS_RATIO = 0.825;
    /**
     * 五角星内凹半径相对外尖的比例
     */
    private static final double STAR_INNER_RATIO = 0.382;
    private static final double ARRAY_Y_OFFSET = 0.1;
    /**
     * 落星生成圆柱：法阵上方最低偏移与柱高（格）
     */
    private static final double STAR_SPAWN_MIN_HEIGHT = 32.0;
    private static final double STAR_SPAWN_CYLINDER_HEIGHT = 64.0;

    protected static final EntityDataAccessor<Integer> MODE = SynchedEntityData.defineId(StarfallArrayEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> TARGET_ENTITY_ID = SynchedEntityData.defineId(StarfallArrayEntity.class, EntityDataSerializers.INT);

    private int starLife = 200;
    private int starColor = 0x3333FF;
    private float starSize = 2.67f;
    @Nullable
    private ResourceLocation starModel;
    @Nullable
    private ResourceLocation starTexture;

    public StarfallArrayEntity(
            EntityType<? extends StarfallArrayEntity> entityTypeIn,
            Level worldIn,
            LivingEntity shooting
    ) {
        super(entityTypeIn, worldIn, shooting);
        setMaxLifeTime(300);
        setSize(1.0f);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(MODE, MODE_FOLLOW);
        getEntityData().define(TARGET_ENTITY_ID, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            if (tickCount % 2 == 0) {
                spawnArrayParticles();
            }
            return;
        }

        LivingEntity caster = getShooter();
        if (caster == null || !caster.isAlive()) {
            discard();
            return;
        }

        if (getMode() == MODE_FOLLOW) {
            setPos(caster.getX(), caster.getY() + 0.1, caster.getZ());
        } else {
            LivingEntity locked = getPinTarget();
            if (locked == null || !locked.isAlive()) {
                discard();
                return;
            }

            locked.teleportTo(getX(), getY(), getZ());
            locked.setYRot(locked.getYRot());
            locked.setXRot(locked.getXRot());
        }

        LivingEntity locked = getPinTarget();
        if (locked != null && locked.isAlive()) {
            Vec3 spawnPos = randomStarSpawnPos(level().getRandom());
            spawnStar(caster, locked, spawnPos.x, spawnPos.y, spawnPos.z);
        }
    }

    /**
     * 粒子阵法：仅绘制边框（外圈 + 五角星边 + 内五边形），不填充。
     * 半径与旧模型 {@code BASE_SCALE * extent * size} 对齐。
     */
    private void spawnArrayParticles() {
        double cx = getX();
        double cy = getY() + ARRAY_Y_OFFSET;
        double cz = getZ();
        float size = Math.max(0.5f, getSize());
        double outer = OUTER_RADIUS * size;
        double starOuter = outer * STAR_RADIUS_RATIO;
        double starInner = starOuter * STAR_INNER_RATIO;
        double rotation = tickCount * 0.006;

        Color color = getColor();
        DustParticleOptions rimDust = new DustParticleOptions(
                new Vector3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f),
                1.1f
        );

        // 外圈边框
        double rimStep = Math.max(0.05, 0.5 / outer);
        for(double angle = 0.0; angle < Math.PI * 2.0; angle += rimStep) {
            double x = cx + Math.cos(angle + rotation) * outer;
            double z = cz + Math.sin(angle + rotation) * outer;
            level().addParticle(ParticleTypes.END_ROD, true, x, cy, z, 0.0, 0.0, 0.0);
            level().addParticle(rimDust, true, x, cy + 0.02, z, 0.0, 0.0, 0.0);
        }

        // 五角星边框（{5/2}）
        double edgeStep = Math.max(0.04, 0.32 / starOuter);
        for(int i = 0; i < 5; i++) {
            double a1 = rotation + i * Math.PI * 2.0 / 5.0 - Math.PI / 2.0;
            double a2 = rotation + ((i + 2) % 5) * Math.PI * 2.0 / 5.0 - Math.PI / 2.0;
            spawnEdge(
                    cx, cy, cz,
                    Math.cos(a1) * starOuter, Math.sin(a1) * starOuter,
                    Math.cos(a2) * starOuter, Math.sin(a2) * starOuter,
                    edgeStep,
                    rimDust
            );
        }

        // 内五边形边框
        for(int i = 0; i < 5; i++) {
            double a1 = rotation + i * Math.PI * 2.0 / 5.0 + Math.PI / 5.0 - Math.PI / 2.0;
            double a2 = rotation + ((i + 1) % 5) * Math.PI * 2.0 / 5.0 + Math.PI / 5.0 - Math.PI / 2.0;
            spawnEdge(
                    cx, cy, cz,
                    Math.cos(a1) * starInner, Math.sin(a1) * starInner,
                    Math.cos(a2) * starInner, Math.sin(a2) * starInner,
                    edgeStep,
                    rimDust
            );
        }
    }

    private void spawnEdge(
            double cx,
            double cy,
            double cz,
            double ax,
            double az,
            double bx,
            double bz,
            double step,
            DustParticleOptions dust
    ) {
        for(double t = 0.0; t <= 1.0; t += step) {
            double x = cx + ax * (1.0 - t) + bx * t;
            double z = cz + az * (1.0 - t) + bz * t;
            level().addParticle(ParticleTypes.END_ROD, true, x, cy, z, 0.0, 0.0, 0.0);
            level().addParticle(dust, true, x, cy + 0.02, z, 0.0, 0.0, 0.0);
        }
    }

    /**
     * 法阵上方圆柱内均匀随机点：半径与阵体 {@link #getSize()} 一致，Y 为阵心上方 32～96 格区间。
     */
    private Vec3 randomStarSpawnPos(RandomSource random) {
        double radius = OUTER_RADIUS * Math.max(0.5f, getSize());
        double angle = random.nextDouble() * Math.PI * 2.0;
        double r = radius * Math.sqrt(random.nextDouble());
        double x = getX() + Math.cos(angle) * r;
        double z = getZ() + Math.sin(angle) * r;
        double y = getY() + STAR_SPAWN_MIN_HEIGHT + random.nextDouble() * STAR_SPAWN_CYLINDER_HEIGHT;
        return new Vec3(x, y, z);
    }

    private void spawnStar(LivingEntity caster, LivingEntity target, double spawnX, double spawnY, double spawnZ) {
        TrackingSummondSwordEntity star = new TrackingSummondSwordEntity(
                RecastingEntities.TRACKING_SUMMOND_SWORD.get(),
                level(),
                caster
        );
        star.setPos(spawnX, spawnY, spawnZ);
        star.setColor(starColor);
        star.setModifiedRatio(getModifiedRatio());
        star.setInterval(0);
        star.setMaxLifeTime(starLife);
        star.setSize(starSize);
        star.setAutoRetarget(false);
        star.setTargetEntity(target);
        if (starModel != null) {
            star.setModel(starModel);
        }
        if (starTexture != null) {
            star.setTexture(starTexture);
        }
        star.lookAt(target.position().add(0.0, target.getBbHeight() * 0.5, 0.0), false);
        level().addFreshEntity(star);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    public int getMode() {
        return getEntityData().get(MODE);
    }

    public void setMode(int mode) {
        getEntityData().set(MODE, mode);
    }

    public int getTargetEntityId() {
        return getEntityData().get(TARGET_ENTITY_ID);
    }

    public void setTargetEntityId(int id) {
        getEntityData().set(TARGET_ENTITY_ID, id);
    }

    public void setPinTarget(@Nullable LivingEntity target) {
        setTargetEntityId(target != null
                ? target.getId()
                : -1);
    }

    @Nullable
    public LivingEntity getPinTarget() {
        int id = getTargetEntityId();
        if (id <= 0) {
            return null;
        }
        Entity entity = level().getEntity(id);
        if (entity instanceof LivingEntity living) {
            return living;
        }
        return null;
    }

    public void setStarLife(int starLife) {
        this.starLife = starLife;
    }

    public void setStarColor(int starColor) {
        this.starColor = starColor;
    }

    public void setStarSize(float starSize) {
        this.starSize = starSize;
    }

    public void setStarModel(@Nullable ResourceLocation starModel) {
        this.starModel = starModel;
    }

    public void setStarTexture(@Nullable ResourceLocation starTexture) {
        this.starTexture = starTexture;
    }
}

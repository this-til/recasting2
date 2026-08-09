package com.til.recasting.entity;

import com.til.recasting.handler.EntityHelper;
import com.til.recasting.registry.RecastingEntities;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * [回到未来计划]群星坠落阵：自身不造成伤害，按模式跟随或禁锢并刷追踪落星。
 */
public class StarfallArrayEntity extends StandardizationAttackEntity {

    public static final int MODE_FOLLOW = 0;
    public static final int MODE_PIN = 1;

    protected static final EntityDataAccessor<Integer> MODE =
            SynchedEntityData.defineId(StarfallArrayEntity.class, EntityDataSerializers.INT);

    protected static final EntityDataAccessor<Integer> TARGET_ENTITY_ID =
            SynchedEntityData.defineId(StarfallArrayEntity.class, EntityDataSerializers.INT);

    private float seekRange = 45.0f;
    private float starDropHeight = 12.0f;
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
            return;
        }

        LivingEntity caster = getShooter();
        if (caster == null || !caster.isAlive()) {
            discard();
            return;
        }

        if (getMode() == MODE_FOLLOW) {
            setPos(caster.getX(), caster.getY() + 0.1, caster.getZ());
            LivingEntity foe = pickRandomFoe(caster);
            if (foe != null) {
                spawnStar(caster, foe, foe.getY() + starDropHeight);
            }
            return;
        }

        LivingEntity target = getPinTarget();
        if (target == null || !target.isAlive()) {
            discard();
            return;
        }

        target.teleportTo(getX(), getY(), getZ());
        target.setYRot(target.getYRot());
        target.setXRot(target.getXRot());

        RandomSource random = level().getRandom();
        double offset = target.getBbWidth() * (random.nextDouble() * 2.0 - 1.0);
        double spawnY = target.getY() + Math.abs(offset) * 0.5 + 1.0;
        spawnStar(caster, target, spawnY);
    }

    @Nullable
    private LivingEntity pickRandomFoe(LivingEntity caster) {
        List<LivingEntity> foes = EntityHelper.getTargettableLivingEntityWithinAABB(
                level(),
                caster,
                position(),
                seekRange
        );
        if (foes.isEmpty()) {
            return null;
        }
        return foes.get(level().getRandom().nextInt(foes.size()));
    }

    private void spawnStar(LivingEntity caster, LivingEntity target, double spawnY) {
        TrackingSummondSwordEntity star = new TrackingSummondSwordEntity(
                RecastingEntities.TRACKING_SUMMOND_SWORD.get(),
                level(),
                caster
        );
        star.setPos(target.getX(), spawnY, target.getZ());
        star.setColor(starColor);
        star.setModifiedRatio(getModifiedRatio());
        star.setInterval(0);
        star.setMaxLifeTime(starLife);
        star.setSize(starSize);
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
        setTargetEntityId(target != null ? target.getId() : -1);
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

    public void setSeekRange(float seekRange) {
        this.seekRange = seekRange;
    }

    public void setStarDropHeight(float starDropHeight) {
        this.starDropHeight = starDropHeight;
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

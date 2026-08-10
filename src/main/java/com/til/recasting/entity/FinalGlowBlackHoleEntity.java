package com.til.recasting.entity;

import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.AttractionHelper;
import com.til.recasting.handler.BuffSourceHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.item.MatterBallStorage;
import com.til.recasting.network.FinalGlowIngestMessage;
import com.til.recasting.network.NetworkManager;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.util.DamageStructure;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 末辉终焉超新星爆：视界坍缩后终结。
 * <p>
 * 时间线（与 {@link #getMaxLifeTime()} 同步）：视界与吸积粒子半径按实例字段插值归零后终结；
 * absorbRadius 内核：静滞、收集凋落物、经验归释放者、清除外来 FallingBlockEntity；
 * mobGriefing 时方块按精准采集入账并删除；非破坏规则仅特效；
 * 终结时返还物质球；伤害按距离在 {@link #damageFalloffStart}～{@link #effectRange} 从 {@link #damageRatio} 线性衰减至 0。
 */
@Getter
@Setter
@Accessors(chain = true)
public class FinalGlowBlackHoleEntity extends JudgementCutEntity {

    /** 坍缩时长（同步到 maxLifeTime）。 */
    private int collapseTicks = 100;
    /** 视界半径（同步到 size）：起止插值，归零后爆炸。 */
    private float horizonStart = 16.0f;
    private float horizonEnd = 0.0f;
    /** 球形吸积粒子发射半径。 */
    private float particleRadiusStart = 24.0f;
    private float particleRadiusEnd = 0.0f;
    /** 实体吸附、方块吞噬、终结伤害半径。 */
    private float effectRange = 64.0f;
    private float pullPower = 0.02f;
    private int raysPerTick = 96;
    private float absorbRadius = 2.0f;
    private float damageRatio = 3.35f;
    /** 终结伤害满额内径；该距离至 {@link #effectRange} 线性衰减至 0。 */
    private float damageFalloffStart = 16.0f;
    /** 每 tick 最多播几次方块破坏音效/粒子。 */
    private int blockBreakFxPerTick = 6;

    /** 进入 absorbRadius 时钉死的绝对坐标（静滞）。 */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final Map<UUID, Vec3> stasisPins = new HashMap<>();
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final Map<UUID, LivingEntity> stasisTargets = new HashMap<>();
    /** 生命周期内收集物（临时物质球）。 */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final ItemStack collectBall = new ItemStack(RecastingItems.MATTER_BALL.get());
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean detonated;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean clientDetonationFxSpawned;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean ingestedBlocksThisTick;

    public FinalGlowBlackHoleEntity(
            EntityType<? extends FinalGlowBlackHoleEntity> entityTypeIn,
            Level worldIn,
            LivingEntity shooting
    ) {
        super(entityTypeIn, worldIn, shooting);
        applyConfig();
        setMute(true);
        setRepeatedAttack(false);
        addAttackType(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get());
    }

    /** 将坍缩参数写回 maxLifeTime / size / modifiedRatio。 */
    public FinalGlowBlackHoleEntity applyConfig() {
        setMaxLifeTime(collapseTicks);
        setSize(horizonStart);
        setModifiedRatio(damageRatio);
        return this;
    }

    /**
     * 坍缩进度 0→1，客户端与服务端同一公式（基于 tickCount / maxLifeTime）。
     */
    public float collapseProgress(float partialTick) {
        int life = Math.max(1, getMaxLifeTime());
        return Mth.clamp((tickCount + partialTick) / (float) life, 0.0f, 1.0f);
    }

    public float horizonRadius(float partialTick) {
        return Mth.lerp(collapseProgress(partialTick), horizonStart, horizonEnd);
    }

    public float particleSpawnRadius(float partialTick) {
        return Mth.lerp(collapseProgress(partialTick), particleRadiusStart, particleRadiusEnd);
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
            clearAllStasis();
            discard();
            return;
        }

        ingestedBlocksThisTick = false;
        setSize(horizon);
        attractEntities();
        tickAbsorbCore(shooter);
        absorbByRays(raysPerTick);
        tickAmbientSounds(progress, horizon);

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
            clearAllStasis();
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

    /** 持续吸附环境音：随坍缩进度与视界半径抬升强度；直接发给维度内玩家，无视距离。 */
    private void tickAmbientSounds(float progress, float horizon) {
        float intensity = Mth.clamp(horizon / Math.max(horizonStart, 0.5f), 0.12f, 1.0f);
        float pitch = 0.55f + progress * 0.7f;
        boolean activePull = ingestedBlocksThisTick || !stasisTargets.isEmpty() || hasNearbyPullTargets();

        if (tickCount % 4 == 0) {
            playGlobalSound(
                    SoundEvents.PORTAL_AMBIENT,
                    SoundSource.AMBIENT,
                    0.22f + intensity * 0.38f,
                    pitch
            );
        }
        if (tickCount % 8 == 0) {
            playGlobalSound(
                    SoundEvents.CONDUIT_AMBIENT,
                    SoundSource.AMBIENT,
                    0.1f + intensity * 0.22f,
                    0.65f + progress * 0.45f
            );
        }
        if (activePull && tickCount % 6 == 0) {
            playGlobalSound(
                    SoundEvents.ENDERMAN_TELEPORT,
                    SoundSource.HOSTILE,
                    0.06f + intensity * 0.14f,
                    0.3f + progress * 0.28f
            );
        }
        if (activePull && tickCount % 11 == 0) {
            playGlobalSound(
                    SoundEvents.SOUL_ESCAPE,
                    SoundSource.AMBIENT,
                    0.14f + intensity * 0.18f,
                    0.45f + progress * 0.85f
            );
        }
        if (ingestedBlocksThisTick && tickCount % 5 == 0) {
            playGlobalSound(
                    SoundEvents.SCULK_CLICKING,
                    SoundSource.BLOCKS,
                    0.08f + intensity * 0.16f,
                    0.55f + random.nextFloat() * 0.35f
            );
        }
        if (progress >= 0.8f && tickCount % 3 == 0) {
            playGlobalSound(
                    SoundEvents.BEACON_AMBIENT,
                    SoundSource.AMBIENT,
                    0.08f + (progress - 0.8f) * 0.55f,
                    1.1f + progress * 0.6f
            );
        }
    }

    private void playGlobalSound(SoundEvent sound, SoundSource source, float volume, float pitch) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        for(ServerPlayer player : serverLevel.players()) {
            player.playNotifySound(sound, source, volume, pitch);
        }
    }

    private boolean hasNearbyPullTargets() {
        Vec3 center = position();
        AABB box = new AABB(center, center).inflate(effectRange * 0.35);
        LivingEntity shooter = getShooter();
        for(Entity entity : level().getEntities(this, box, e -> {
            if (e == this) {
                return false;
            }
            return shooter == null || e != shooter;
        })) {
            if (entity instanceof LivingEntity living && stasisPins.containsKey(living.getUUID())) {
                continue;
            }
            return true;
        }
        return false;
    }

    private void spawnBlockBreakFx(BlockPos pos, BlockState state) {
        if (state.isAir() || !(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        // 与玩家破坏方块相同的碎块粒子（global 保证远距也能看到）
        serverLevel.globalLevelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
        playGlobalSound(
                state.getSoundType().getBreakSound(),
                SoundSource.BLOCKS,
                0.5f + random.nextFloat() * 0.3f,
                0.62f + random.nextFloat() * 0.38f
        );
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
        AABB box = new AABB(center, center).inflate(effectRange);
        List<Entity> entities = level().getEntities(this, box, entity -> {
            if (entity == this) {
                return false;
            }
            LivingEntity shooter = getShooter();
            return shooter == null || entity != shooter;
        });
        for(Entity entity : entities) {
            if (entity instanceof LivingEntity living && stasisPins.containsKey(living.getUUID())) {
                continue;
            }
            AttractionHelper.applyRadialPull(center, entity, effectRange, pullPower);
        }
    }

    /**
     * absorbRadius 内核：收集凋落物；经验归释放者；清除外来 FallingBlock；生物钉死至爆炸。
     */
    private void tickAbsorbCore(LivingEntity shooter) {
        Vec3 center = position();
        double rangeSqr = absorbRadius * absorbRadius;
        AABB box = new AABB(center, center).inflate(absorbRadius);
        int displayLevel = Math.max(1, (Math.max(0, getMaxLifeTime() - tickCount) + 19) / 20);

        for(Entity entity : level().getEntities(this, box, e -> e != this)) {
            if (entity.distanceToSqr(center) > rangeSqr) {
                continue;
            }
            if (entity instanceof ItemEntity itemEntity) {
                MatterBallStorage.insert(collectBall, itemEntity.getItem());
                itemEntity.discard();
                continue;
            }
            if (entity instanceof ExperienceOrb orb) {
                if (shooter instanceof Player player) {
                    player.giveExperiencePoints(orb.getValue());
                }
                orb.discard();
                continue;
            }
            if (entity instanceof FallingBlockEntity falling) {
                falling.discard();
                continue;
            }
            if (entity instanceof Projectile projectile) {
                if (!shooter.equals(projectile.getOwner())) {
                    projectile.discard();
                }
                continue;
            }
            if (!(entity instanceof LivingEntity living) || living == shooter || !living.isAlive()) {
                continue;
            }
            UUID id = living.getUUID();
            stasisPins.computeIfAbsent(id, ignored -> living.position());
            stasisTargets.put(id, living);
        }

        Iterator<Map.Entry<UUID, LivingEntity>> iterator = stasisTargets.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, LivingEntity> entry = iterator.next();
            LivingEntity living = entry.getValue();
            if (living == null || !living.isAlive() || living.isRemoved()) {
                clearStasisBuff(living);
                stasisPins.remove(entry.getKey());
                iterator.remove();
                continue;
            }
            Vec3 pin = stasisPins.get(entry.getKey());
            if (pin == null) {
                pin = living.position();
                stasisPins.put(entry.getKey(), pin);
            }
            living.teleportTo(pin.x, pin.y, pin.z);
            living.setDeltaMovement(Vec3.ZERO);
            living.hurtMarked = true;
            mountStasisBuff(living, shooter, displayLevel);
        }
    }

    private void mountStasisBuff(LivingEntity target, LivingEntity caster, int displayLevel) {
        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            data.setLevel(RecastingBuffTypes.ETERNAL_GUARD.get(), displayLevel, target.level());
            BuffSourceHelper.recordSourceEntity(data, RecastingBuffTypes.ETERNAL_GUARD.get(), target, caster);
        });
    }

    private void clearStasisBuff(LivingEntity target) {
        if (target == null || target.isRemoved()) {
            return;
        }
        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data ->
                data.setLevel(RecastingBuffTypes.ETERNAL_GUARD.get(), 0, target.level())
        );
    }

    private void clearAllStasis() {
        for(LivingEntity target : stasisTargets.values()) {
            clearStasisBuff(target);
        }
        stasisPins.clear();
        stasisTargets.clear();
    }

    private void absorbByRays(int rayCount) {
        if (level().isClientSide() || !(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 origin = position();
        boolean grief = level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        int breakFxLeft = blockBreakFxPerTick;
        List<FinalGlowIngestMessage.Entry> ingestEntries = new ArrayList<>();
        LivingEntity shooter = getShooter();
        ItemStack silkTool = null;

        for(int i = 0; i < rayCount; i++) {
            Vec3 dir = randomUnitVector();
            BlockHitResult hit = level().clip(new ClipContext(
                    origin,
                    origin.add(dir.scale(effectRange)),
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

            if (breakFxLeft > 0) {
                spawnBlockBreakFx(pos, state);
                breakFxLeft--;
            }

            ingestEntries.add(new FinalGlowIngestMessage.Entry(state, pos));

            if (!grief) {
                continue;
            }

            if (silkTool == null) {
                silkTool = new ItemStack(Items.DIAMOND_PICKAXE);
                silkTool.enchant(Enchantments.SILK_TOUCH, 1);
            }
            BlockEntity blockEntity = level().getBlockEntity(pos);
            for(ItemStack drop : Block.getDrops(state, serverLevel, pos, blockEntity, shooter, silkTool)) {
                MatterBallStorage.insert(collectBall, drop);
            }
            level().removeBlock(pos, false);
        }

        if (ingestEntries.isEmpty()) {
            return;
        }

        ingestedBlocksThisTick = true;
        NetworkManager.INSTANCE.send(
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this),
                new FinalGlowIngestMessage(getId(), absorbRadius, ingestEntries)
        );
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

    private void detonate() {
        if (detonated) {
            return;
        }
        detonated = true;

        clearAllStasis();
        playGlobalSound(SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 2.4f, 0.55f);
        playGlobalSound(SoundEvents.END_PORTAL_SPAWN, SoundSource.HOSTILE, 1.6f, 0.45f);
        Vec3 center = position();

        LivingEntity shooter = getShooter();
        giveMatterBall(shooter, center);

        if (shooter == null) {
            return;
        }

        var attackTypes = List.of(RecastingAttackTypes.SLASH_EFFECT_ATTACK.get());
        float falloffStart = Math.min(damageFalloffStart, effectRange);
        for(LivingEntity target : EntityHelper.getTargettableLivingEntityWithinAABB(
                level(),
                shooter,
                center,
                effectRange
        )) {
            float dist = (float) target.position().distanceTo(center);
            float ratio = damageRatioAtDistance(dist, falloffStart);
            if (ratio <= 0.0f) {
                continue;
            }
            AttackHelper.doMeleeAttack(shooter, target, new DamageStructure(ratio, 0.0f), attackTypes);
        }
    }

    private void giveMatterBall(LivingEntity shooter, Vec3 center) {
        if (MatterBallStorage.isEmpty(collectBall)) {
            return;
        }
        if (shooter instanceof Player player) {
            ItemStack existing = MatterBallStorage.findBall(player, null);
            if (!existing.isEmpty()) {
                MatterBallStorage.mergeFrom(collectBall, existing);
                return;
            }
            ItemStack ball = new ItemStack(RecastingItems.MATTER_BALL.get());
            MatterBallStorage.copyFrom(collectBall, ball);
            MatterBallStorage.clear(collectBall);
            ItemHandlerHelper.giveItemToPlayer(player, ball);
            return;
        }
        ItemStack ball = new ItemStack(RecastingItems.MATTER_BALL.get());
        MatterBallStorage.copyFrom(collectBall, ball);
        MatterBallStorage.clear(collectBall);
        if (!ball.isEmpty()) {
            level().addFreshEntity(new ItemEntity(level(), center.x, center.y, center.z, ball));
        }
    }

    /**
     * {@code dist ≤ falloffStart} 为满额；{@code falloffStart～effectRange} 线性降至 0。
     */
    private float damageRatioAtDistance(float dist, float falloffStart) {
        if (dist <= falloffStart) {
            return damageRatio;
        }
        if (dist >= effectRange || effectRange <= falloffStart) {
            return 0.0f;
        }
        float t = (dist - falloffStart) / (effectRange - falloffStart);
        return damageRatio * (1.0f - t);
    }
}

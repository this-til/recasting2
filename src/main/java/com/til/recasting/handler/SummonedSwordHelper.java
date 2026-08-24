package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.entity.SummondSpiralSwordEntity;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.entity.TrackingSummondSwordEntity;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.registry.se.CommandMappingSpecialEffect;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.capability.inputstate.CapabilityInputState;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.entity.IShootable;
import mods.flammpfeil.slashblade.event.handler.InputCommandEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import mods.flammpfeil.slashblade.util.InputCommand;
import mods.flammpfeil.slashblade.util.RayTraceHelper;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

@EventBusSubscriber(modid = Recasting.MODID)
public final class SummonedSwordHelper {

    public enum ArtType {
        SUMMON,
        SPIRAL,
        STORM,
        BLISTERING,
        HEAVY_RAIN
    }

    public static final ResourceLocation ADVANCEMENT_SUMMONEDSWORDS =
            ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "arts/shooting/summonedswords");
    public static final ResourceLocation ADVANCEMENT_SPIRAL_SWORDS =
            ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "arts/shooting/spiral_swords");
    public static final ResourceLocation ADVANCEMENT_STORM_SWORDS =
            ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "arts/shooting/storm_swords");
    public static final ResourceLocation ADVANCEMENT_BLISTERING_SWORDS =
            ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "arts/shooting/blistering_swords");
    public static final ResourceLocation ADVANCEMENT_HEAVY_RAIN_SWORDS =
            ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "arts/shooting/heavy_rain_swords");

    private SummonedSwordHelper() {
    }

    @SubscribeEvent
    public static void onInputChange(InputCommandEvent event) {
        EnumSet<InputCommand> old = event.getOld();
        EnumSet<InputCommand> current = event.getCurrent();
        ServerPlayer sender = event.getEntity();

        ItemStack blade = sender.getMainHandItem();
        Optional<ISlashBladeState> stateOpt = BladeStateAccess.of(blade);
        if (stateOpt.isEmpty()) {
            return;
        }
        ISlashBladeState bladeState = stateOpt.get();

        if (bladeState.isBroken() || bladeState.isSealed() || !SwordType.from(blade).contains(SwordType.BEWITCHED)) {
            return;
        }

        int powerLevel = blade.getEnchantmentLevel(Enchantments.POWER);
        if (powerLevel <= 0) {
            return;
        }

        InputCommand targetCommand = InputCommand.M_DOWN;
        boolean onDown = !old.contains(targetCommand) && current.contains(targetCommand);
        final long pressTime = event.getState().getLastPressTime(targetCommand);

        if (onDown) {
            var inputState = sender.getData(CapabilityInputState.INPUT_STATE.get());
            inputState.getScheduler().schedule("SpiralSwords", pressTime + 10,
                    (rawEntity, queue, now) -> performSpiralSwords(pressTime, rawEntity));
            inputState.getScheduler().schedule("StormSwords", pressTime + 10,
                    (rawEntity, queue, now) -> performStormSwords(pressTime, rawEntity));
            inputState.getScheduler().schedule("BlisteringSwords", pressTime + 10,
                    (rawEntity, queue, now) -> performBlisteringSwords(pressTime, rawEntity, now));
            inputState.getScheduler().schedule("HeavyRainSwords", pressTime + 10,
                    (rawEntity, queue, now) -> performHeavyRains(pressTime, rawEntity, now));

            executeSummonedSword(sender, true, true);
        }
    }

    public static void replayArt(ServerPlayer player, ArtType artType) {
        switch (artType) {
            case SUMMON -> executeSummonedSword(player, false, false);
            case SPIRAL -> executeSpiralSwords(player, false, false);
            case STORM -> executeStormSwords(player, false, false);
            case BLISTERING -> executeBlisteringSwords(player, false, false);
            case HEAVY_RAIN -> executeHeavyRains(player, false, false);
        }
    }

    private static boolean executeSummonedSword(ServerPlayer sender, boolean consumeProud, boolean notifyCommandMapping) {
        AtomicBoolean success = new AtomicBoolean(false);
        ItemStack blade = sender.getMainHandItem();
        BladeStateAccess.of(blade).ifPresent(state -> {
            if (consumeProud) {
                if (state.getProudSoulCount() < SlashBladeConfig.SUMMON_SWORD_COST.get()) {
                    return;
                }
                state.setProudSoulCount(state.getProudSoulCount() - SlashBladeConfig.SUMMON_SWORD_COST.get());
            }
            AdvancementHelper.grantCriterion(sender, ADVANCEMENT_SUMMONEDSWORDS);

            Level worldIn = sender.level();
            PropertiesDefinitionExtension properties = AttackHelper.propertiesOf(blade);
            boolean trackingMode = properties != null && properties.trackingPhantomBlade();

            SummondSwordEntity ss;
            if (trackingMode) {
                TrackingSummondSwordEntity tracking =
                        new TrackingSummondSwordEntity(RecastingEntities.TRACKING_SUMMOND_SWORD.get(), worldIn, sender);
                tracking.setInterval(10);
                Entity aimTarget = state.getTargetEntity(worldIn);
                if (aimTarget != null) {
                    tracking.setTargetEntity(aimTarget);
                }
                ss = tracking;
            } else {
                ss = new SummondSwordEntity(RecastingEntities.SUMMOND_SWORD.get(), worldIn, sender);
            }

            ss.setModifiedRatio(0.1f);
            ss.setColor(state.getColorCode());
            ss.setRoll(sender.getRandom().nextFloat() * 360.0f);
            ss.lookAt(PosHelper.getAttackTargetPosition(sender, state), false);

            worldIn.addFreshEntity(ss);
            sender.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F, 1.45F);
            success.set(true);
        });

        if (success.get() && notifyCommandMapping) {
            CommandMappingSpecialEffect.trySchedule(sender, ArtType.SUMMON);
        }
        return success.get();
    }

    public static Optional<Entity> findTarget(ServerPlayer sender, Entity lockedT) {
        return Stream.of(
                        Optional.ofNullable(lockedT),
                        RayTraceHelper.rayTrace(sender.level(), sender, sender.getEyePosition(1.0f), sender.getLookAngle(),
                                        12, 12, e -> true)
                                .filter(r -> r.getType() == HitResult.Type.ENTITY)
                                .filter(r -> {
                                    EntityHitResult er = (EntityHitResult) r;
                                    Entity target = er.getEntity();
                                    boolean isMatch = true;
                                    if (target instanceof LivingEntity livingTarget) {
                                        isMatch = TargetSelector.lockon.test(sender, livingTarget);
                                    }
                                    if (target instanceof IShootable shootable) {
                                        isMatch = shootable.getShooter() != sender;
                                    }
                                    return isMatch;
                                })
                                .map(r -> ((EntityHitResult) r).getEntity())
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    static Vec3 calculateViewVector(float x, float y) {
        float f = x * ((float) Math.PI / 180F);
        float f1 = -y * ((float) Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }

    private static void performSpiralSwords(final Long pressTime, LivingEntity rawEntity) {
        if (!(rawEntity instanceof ServerPlayer entity)) {
            return;
        }

        InputCommand targetCommand = InputCommand.M_DOWN;
        boolean inputSucceed = entity.getData(CapabilityInputState.INPUT_STATE.get()).getCommands().contains(targetCommand)
                && (!InputCommand.anyMatch(entity.getData(CapabilityInputState.INPUT_STATE.get()).getCommands(), InputCommand.move)
                || !entity.getData(CapabilityInputState.INPUT_STATE.get()).getCommands().contains(InputCommand.SNEAK))
                && entity.getData(CapabilityInputState.INPUT_STATE.get()).getLastPressTime(targetCommand) == pressTime;

        if (!inputSucceed) {
            return;
        }

        executeSpiralSwords(entity, true, true);
    }

    private static boolean executeSpiralSwords(ServerPlayer entity, boolean consumeProud, boolean notifyCommandMapping) {
        Level worldIn = entity.level();

        List<SummondSpiralSwordEntity> existingSwords = worldIn.getEntitiesOfClass(
                SummondSpiralSwordEntity.class,
                entity.getBoundingBox().inflate(10),
                sword -> sword.getShooter() == entity
                        && sword.getCenterEntity() == entity
                        && sword.getActionType() == SummondSwordEntity.ActionType.PREPARE
        );

        if (!existingSwords.isEmpty()) {
            existingSwords.forEach(sword -> {
                if (sword.getActionType() == SummondSwordEntity.ActionType.PREPARE) {
                    sword.setActionType(SummondSwordEntity.ActionType.FLYING);
                    sword.updateMotion(sword.getSeep());
                }
            });
            if (notifyCommandMapping) {
                CommandMappingSpecialEffect.trySchedule(entity, ArtType.SPIRAL);
            }
            return true;
        }

        AtomicBoolean success = new AtomicBoolean(false);
        BladeStateAccess.of(entity.getMainHandItem()).ifPresent(state -> {
            if (consumeProud) {
                if (state.getProudSoulCount() < SlashBladeConfig.SUMMON_SWORD_ART_COST.get()) {
                    return;
                }
                state.setProudSoulCount(state.getProudSoulCount() - SlashBladeConfig.SUMMON_SWORD_ART_COST.get());
            }

            AdvancementHelper.grantCriterion(entity, ADVANCEMENT_SPIRAL_SWORDS);

            int rank = entity.getData(CapabilityConcentrationRank.RANK_POINT.get())
                    .getRank(worldIn.getGameTime()).level;

            int count = IConcentrationRank.ConcentrationRanks.S.level <= rank ? 8 : 6;

            for (int i = 0; i < count; i++) {
                SummondSpiralSwordEntity ss = new SummondSpiralSwordEntity(
                        RecastingEntities.SUMMOND_SPIRAL_SWORD.get(), worldIn, entity);

                ss.setCenterEntity(entity);
                ss.setRotationRadius(2.0f);
                ss.setRotationSpeed(16.0f);
                ss.setRotationSpeedModifier(1.0f);
                ss.setRotationRadiusModifier(1.0f);
                ss.setRotationAngle(360.0f / count * i);
                ss.setRotationAxis(new Vec3(0, 1, 0));
                ss.setRotationDirectionOutward(true);
                ss.setCanAttackDuringRotation(true);
                ss.setModifiedRatio(0.1f);
                ss.setColor(state.getColorCode());
                ss.setRoll(0);
                ss.setStartDelay(Integer.MAX_VALUE);
                ss.setSeep(3.0f);

                worldIn.addFreshEntity(ss);
                entity.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F, 1.45F);
            }
            success.set(true);
        });

        if (success.get() && notifyCommandMapping) {
            CommandMappingSpecialEffect.trySchedule(entity, ArtType.SPIRAL);
        }
        return success.get();
    }

    private static void performStormSwords(final Long pressTime, LivingEntity rawEntity) {
        if (!(rawEntity instanceof ServerPlayer entity)) {
            return;
        }

        InputCommand targetCommand = InputCommand.M_DOWN;
        var input = entity.getData(CapabilityInputState.INPUT_STATE.get());
        boolean inputSucceed = input.getCommands().contains(targetCommand)
                && input.getCommands().contains(InputCommand.SNEAK)
                && input.getCommands().contains(InputCommand.BACK)
                && !input.getCommands().contains(InputCommand.FORWARD)
                && input.getLastPressTime(targetCommand) == pressTime;
        if (!inputSucceed) {
            return;
        }

        executeStormSwords(entity, true, true);
    }

    private static boolean executeStormSwords(ServerPlayer entity, boolean consumeProud, boolean notifyCommandMapping) {
        AtomicBoolean success = new AtomicBoolean(false);
        BladeStateAccess.of(entity.getMainHandItem()).ifPresent(state -> {
            Level worldIn = entity.level();
            Entity target = state.getTargetEntity(worldIn);

            if (target == null || !target.isAlive() || target.isRemoved()) {
                return;
            }

            if (consumeProud) {
                if (state.getProudSoulCount() < SlashBladeConfig.SUMMON_SWORD_ART_COST.get()) {
                    return;
                }
                state.setProudSoulCount(state.getProudSoulCount() - SlashBladeConfig.SUMMON_SWORD_ART_COST.get());
            }

            AdvancementHelper.grantCriterion(entity, ADVANCEMENT_STORM_SWORDS);

            int rank = entity.getData(CapabilityConcentrationRank.RANK_POINT.get())
                    .getRank(worldIn.getGameTime()).level;
            int count = IConcentrationRank.ConcentrationRanks.S.level <= rank ? 8 : 6;

            for (int i = 0; i < count; i++) {
                SummondSpiralSwordEntity ss = new SummondSpiralSwordEntity(
                        RecastingEntities.SUMMOND_SPIRAL_SWORD.get(), worldIn, entity);

                ss.setCenterEntity(target);
                ss.setRadiusExpansion(2.5f, 6.0f, 30);
                ss.setSpeedDecay(16.0f, 0.3f, 30);
                ss.setRotationAngle(360.0f / count * i);
                ss.setRotationAxis(new Vec3(0, 1, 0));
                ss.setRotationDirectionOutward(false);
                ss.setModifiedRatio(0.1f);
                ss.setColor(state.getColorCode());
                ss.setRoll(0);
                ss.setStartDelay(30);

                worldIn.addFreshEntity(ss);
                entity.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F, 1.45F);
            }
            success.set(true);
        });

        if (success.get() && notifyCommandMapping) {
            CommandMappingSpecialEffect.trySchedule(entity, ArtType.STORM);
        }
        return success.get();
    }

    private static void performBlisteringSwords(final Long pressTime, LivingEntity rawEntity, long now) {
        if (!(rawEntity instanceof ServerPlayer entity)) {
            return;
        }

        InputCommand targetCommand = InputCommand.M_DOWN;
        var input = entity.getData(CapabilityInputState.INPUT_STATE.get());
        boolean inputSucceed = input.getCommands().contains(targetCommand)
                && input.getCommands().contains(InputCommand.SNEAK)
                && input.getCommands().contains(InputCommand.FORWARD)
                && input.getLastPressTime(InputCommand.BACK) + 20 < now
                && input.getLastPressTime(targetCommand) == pressTime;
        if (!inputSucceed) {
            return;
        }

        executeBlisteringSwords(entity, true, true);
    }

    private static boolean executeBlisteringSwords(ServerPlayer entity, boolean consumeProud, boolean notifyCommandMapping) {
        AtomicBoolean success = new AtomicBoolean(false);
        BladeStateAccess.of(entity.getMainHandItem()).ifPresent(state -> {
            Level worldIn = entity.level();

            if (consumeProud) {
                if (state.getProudSoulCount() < SlashBladeConfig.SUMMON_SWORD_ART_COST.get()) {
                    return;
                }
                state.setProudSoulCount(state.getProudSoulCount() - SlashBladeConfig.SUMMON_SWORD_ART_COST.get());
            }

            AdvancementHelper.grantCriterion(entity, ADVANCEMENT_BLISTERING_SWORDS);

            int rank = entity.getData(CapabilityConcentrationRank.RANK_POINT.get())
                    .getRank(worldIn.getGameTime()).level;
            int count = IConcentrationRank.ConcentrationRanks.S.level <= rank ? 8 : 6;

            for (int i = 0; i < count; i++) {
                SummondSwordEntity ss = new SummondSwordEntity(
                        RecastingEntities.SUMMOND_SWORD.get(), worldIn, entity);

                Vec3 basePos = entity.position().add(0, entity.getEyeHeight() * 0.8, 0);
                boolean isRight = i % 2 == 0;
                int level = i / 2;
                double xOffset = (1 - 0.1 * level) * (isRight ? 1 : -1);
                double yOffset = 0.25 * level;
                double zOffset = -0.1 * level;

                Vec3 offset = new Vec3(xOffset, yOffset, zOffset);
                offset = offset.xRot((float) Math.toRadians(-entity.getXRot()));
                offset = offset.yRot((float) Math.toRadians(-entity.getYRot()));

                Vec3 startPos = basePos.add(offset);
                ss.setPos(startPos);
                ss.setModifiedRatio(0.1f);
                ss.setColor(state.getColorCode());
                ss.setRoll(0);
                ss.setStartDelay(i);
                ss.setSeep(3.5f);
                ss.setIgnoringBlock(false);

                Entity target = state.getTargetEntity(worldIn);
                Vec3 targetPos;
                if (target != null && target.isAlive()) {
                    targetPos = new Vec3(target.getX(), target.getY() + target.getEyeHeight() * 0.5, target.getZ());
                } else {
                    Vec3 forwardDir = entity.getLookAngle();
                    targetPos = startPos.add(forwardDir.scale(40));
                }

                ss.lookAt(targetPos, false);
                worldIn.addFreshEntity(ss);
                entity.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F, 1.45F);
            }
            success.set(true);
        });

        if (success.get() && notifyCommandMapping) {
            CommandMappingSpecialEffect.trySchedule(entity, ArtType.BLISTERING);
        }
        return success.get();
    }

    private static void performHeavyRains(final Long pressTime, LivingEntity rawEntity, long now) {
        if (!(rawEntity instanceof ServerPlayer entity)) {
            return;
        }

        InputCommand targetCommand = InputCommand.M_DOWN;
        var input = entity.getData(CapabilityInputState.INPUT_STATE.get());
        boolean inputSucceed = input.getCommands().contains(targetCommand)
                && input.getCommands().contains(InputCommand.SNEAK)
                && input.getCommands().contains(InputCommand.FORWARD)
                && input.getLastPressTime(InputCommand.BACK) + 30 > now
                && input.getLastPressTime(targetCommand) == pressTime;
        if (!inputSucceed) {
            return;
        }

        executeHeavyRains(entity, true, true);
    }

    private static boolean executeHeavyRains(ServerPlayer entity, boolean consumeProud, boolean notifyCommandMapping) {
        AtomicBoolean success = new AtomicBoolean(false);
        BladeStateAccess.of(entity.getMainHandItem()).ifPresent(state -> {
            Level worldIn = entity.level();
            Entity target = state.getTargetEntity(worldIn);
            if (consumeProud) {
                if (state.getProudSoulCount() < SlashBladeConfig.SUMMON_SWORD_ART_COST.get()) {
                    return;
                }
                state.setProudSoulCount(state.getProudSoulCount() - SlashBladeConfig.SUMMON_SWORD_ART_COST.get());
            }

            AdvancementHelper.grantCriterion(entity, ADVANCEMENT_HEAVY_RAIN_SWORDS);

            int rank = entity.getData(CapabilityConcentrationRank.RANK_POINT.get())
                    .getRank(worldIn.getGameTime()).level;

            Vec3 basePos;
            if (target != null) {
                basePos = target.position();
            } else {
                Vec3 forwardDir = calculateViewVector(0, entity.getYRot());
                basePos = entity.getPosition(0).add(forwardDir.scale(5));
            }

            float yOffset = 7;
            basePos = basePos.add(0, yOffset, 0);

            int count = 9 + Math.min(rank - 1, 0);
            int multiplier = 2;

            {
                SummondSwordEntity ss = new SummondSwordEntity(RecastingEntities.SUMMOND_SWORD.get(), worldIn, entity);
                ss.setModifiedRatio(0.1f);
                ss.setColor(state.getColorCode());
                ss.setRoll(0);
                ss.setStartDelay(0);
                ss.setIgnoringBlock(false);
                ss.setPos(basePos);
                ss.setRot(entity.getYRot(), -90.0f, false);
                ss.updateMotion(ss.getSeep());
                worldIn.addFreshEntity(ss);
            }

            for (int i = 0; i < count; i++) {
                for (int l = 0; l < multiplier; l++) {
                    SummondSwordEntity ss = new SummondSwordEntity(RecastingEntities.SUMMOND_SWORD.get(), worldIn, entity);
                    ss.setModifiedRatio(0.1f);
                    ss.setColor(state.getColorCode());
                    ss.setRoll(0);
                    ss.setStartDelay(i);
                    ss.setIgnoringBlock(false);

                    double spreadX = (entity.getRandom().nextDouble() - 0.5) * 4.0;
                    double spreadZ = (entity.getRandom().nextDouble() - 0.5) * 4.0;
                    Vec3 spreadPos = basePos.add(spreadX, 0, spreadZ);

                    ss.setPos(spreadPos);
                    ss.setRot(entity.getYRot(), 90.0f, false);
                    ss.updateMotion(ss.getSeep());

                    worldIn.addFreshEntity(ss);
                    entity.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F, 1.45F);
                }
            }
            success.set(true);
        });

        if (success.get() && notifyCommandMapping) {
            CommandMappingSpecialEffect.trySchedule(entity, ArtType.HEAVY_RAIN);
        }
        return success.get();
    }
}

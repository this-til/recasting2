package com.til.recasting.handler;


import com.til.recasting.entity.SummondSpiralSwordEntity;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.registry.RecastingEntities;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.capability.inputstate.CapabilityInputState;
import mods.flammpfeil.slashblade.capability.slashblade.SlashBladeState;
import mods.flammpfeil.slashblade.entity.*;
import mods.flammpfeil.slashblade.event.handler.InputCommandEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.util.*;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.til.recasting.Recasting.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SummonedSwordHelper {

    public static final ResourceLocation ADVANCEMENT_SUMMONEDSWORDS = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "arts/shooting/summonedswords");
    public static final ResourceLocation ADVANCEMENT_SPIRAL_SWORDS = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "arts/shooting/spiral_swords");
    public static final ResourceLocation ADVANCEMENT_STORM_SWORDS = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "arts/shooting/storm_swords");
    public static final ResourceLocation ADVANCEMENT_BLISTERING_SWORDS = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "arts/shooting/blistering_swords");
    public static final ResourceLocation ADVANCEMENT_HEAVY_RAIN_SWORDS = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "arts/shooting/heavy_rain_swords");

    @SubscribeEvent
    public static void onInputChange(InputCommandEvent event) {
        EnumSet<InputCommand> old = event.getOld();
        EnumSet<InputCommand> current = event.getCurrent();
        ServerPlayer sender = event.getEntity();

        ItemStack blade = sender.getMainHandItem();
        var bladeState = blade.getCapability(ItemSlashBlade.BLADESTATE).orElse(new SlashBladeState(blade));

        if (bladeState.isBroken() || bladeState.isSealed() || !SwordType.from(blade).contains(SwordType.BEWITCHED)) {
            return;
        }

        int powerLevel = blade.getEnchantmentLevel(Enchantments.POWER_ARROWS);
        if (powerLevel <= 0) {
            return;
        }

        InputCommand targetCommnad = InputCommand.M_DOWN;


        boolean onDown = !old.contains(targetCommnad) && current.contains(targetCommnad);

        final long pressTime = event.getState().getLastPressTime(targetCommnad);

        // basic summoned swords
        if (onDown) {

            sender.getCapability(CapabilityInputState.INPUT_STATE).ifPresent(input -> {

                // SpiralSwords command
                input.getScheduler().schedule("SpiralSwords", pressTime + 10, (rawEntity, queue, now) -> performSpiralSwords(pressTime, rawEntity));

                // StormSwords command
                input.getScheduler().schedule("StormSwords", pressTime + 10, (rawEntity, queue, now) -> performStormSwords(pressTime, rawEntity));

                // BlisteringSwords command
                input.getScheduler().schedule("BlisteringSwords", pressTime + 10, (rawEntity, queue, now) -> performBlisteringSwords(pressTime, rawEntity, now));

                input.getScheduler().schedule("HeavyRainSwords", pressTime + 10, (rawEntity, queue, now) -> performHeavyRains(pressTime, rawEntity, now));

            });

            blade.getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state) -> {

                if (state.getProudSoulCount() < SlashBladeConfig.SUMMON_SWORD_COST.get()) {
                    return;
                }
                state.setProudSoulCount(state.getProudSoulCount() - SlashBladeConfig.SUMMON_SWORD_COST.get());
                //幻影剑
                AdvancementHelper.grantCriterion(sender, ADVANCEMENT_SUMMONEDSWORDS);

                Level worldIn = sender.level();

                SummondSwordEntity ss = new SummondSwordEntity(RecastingEntities.SUMMOND_SWORD.get(), worldIn, sender);

                ss.setModifiedRatio(0.1f);
                ss.setColor(state.getColorCode());
                ss.setRoll(sender.getRandom().nextFloat() * 360.0f);
                ss.lookAt(PosHelper.getAttackTargetPosition(sender, state), false);

                worldIn.addFreshEntity(ss);
                sender.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F, 1.45F);
            });
        }
    }

    public static Optional<Entity> findTarget(ServerPlayer sender, Entity lockedT) {
        return Stream.of(Optional.ofNullable(lockedT),
                        RayTraceHelper
                                .rayTrace(sender.level(), sender, sender.getEyePosition(1.0f), sender.getLookAngle(),
                                        12, 12, (e) -> true)
                                .filter(r -> r.getType() == HitResult.Type.ENTITY).filter(r -> {
                                    EntityHitResult er = (EntityHitResult) r;
                                    Entity target = er.getEntity();

                                    boolean isMatch = true;
                                    if (target instanceof LivingEntity) {
                                        isMatch = TargetSelector.lockon.test(sender, (LivingEntity) target);
                                    }

                                    if (target instanceof IShootable) {
                                        isMatch = ((IShootable) target).getShooter() != sender;
                                    }

                                    return isMatch;
                                }).map(r -> ((EntityHitResult) r).getEntity()))
                .filter(Optional::isPresent).map(Optional::get).findFirst();
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

        InputCommand targetCommnad = InputCommand.M_DOWN;
        boolean inputSucceed = entity.getCapability(CapabilityInputState.INPUT_STATE)
                .filter(input -> input.getCommands().contains(targetCommnad)
                        && (!InputCommand.anyMatch(input.getCommands(), InputCommand.move)
                        || !input.getCommands().contains(InputCommand.SNEAK))
                        && input.getLastPressTime(targetCommnad) == pressTime)
                .isPresent();

        if (!inputSucceed) {
            return;
        }

        // spiralSwords
        Level worldIn = entity.level();

        // 检查是否已经有圆环幻影剑在旋转
        List<SummondSpiralSwordEntity> existingSwords = worldIn.getEntitiesOfClass(
                SummondSpiralSwordEntity.class,
                entity.getBoundingBox().inflate(10),
                sword -> sword.getShooter() == entity
                        && sword.getCenterEntity() == entity
                        && sword.getActionType() == SummondSwordEntity.ActionType.PREPARE
        );

        if (!existingSwords.isEmpty()) {
            // fire - 让所有旋转的剑发射
            existingSwords.forEach(sword -> {
                if (sword.getActionType() == SummondSwordEntity.ActionType.PREPARE) {
                    sword.setActionType(SummondSwordEntity.ActionType.FLYING);
                    sword.updateMotion(sword.getSeep());
                }
            });
        } else {
            // summon
            entity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state) -> {

                if (state.getProudSoulCount() < SlashBladeConfig.SUMMON_SWORD_ART_COST.get()) {
                    return;
                }
                state.setProudSoulCount(
                        state.getProudSoulCount() - SlashBladeConfig.SUMMON_SWORD_ART_COST.get());

                //圆环幻影剑
                AdvancementHelper.grantCriterion(entity, ADVANCEMENT_SPIRAL_SWORDS);

                int rank = entity.getCapability(CapabilityConcentrationRank.RANK_POINT)
                        .map(r -> r.getRank(worldIn.getGameTime()).level).orElse(0);

                int count = 6;

                if (IConcentrationRank.ConcentrationRanks.S.level <= rank) {
                    count = 8;
                }

                for(int i = 0; i < count; i++) {
                    SummondSpiralSwordEntity ss = new SummondSpiralSwordEntity(
                            RecastingEntities.SUMMOND_SPIRAL_SWORD.get(), worldIn, entity);

                    // 设置旋转中心为玩家
                    ss.setCenterEntity(entity);

                    // 设置旋转参数（匀速旋转，固定半径）
                    ss.setRotationRadius(2.0f); // 固定半径 2 格
                    ss.setRotationSpeed(16.0f); // 旋转速度 6 度/tick
                    ss.setRotationSpeedModifier(1.0f); // 速度不变
                    ss.setRotationRadiusModifier(1.0f); // 半径不变
                    ss.setRotationAngle(360.0f / count * i); // 均匀分布在圆周上
                    ss.setRotationAxis(new Vec3(0, 1, 0)); // 绕 Y 轴旋转
                    ss.setRotationDirectionOutward(true); // 朝外

                    // 启用旋转时攻击
                    ss.setCanAttackDuringRotation(true);

                    // 设置基本属性
                    ss.setModifiedRatio(0.1f);
                    ss.setColor(state.getColorCode());
                    ss.setRoll(0);
                    ss.setStartDelay(Integer.MAX_VALUE); // 不自动发射，等待手动触发
                    ss.setSeep(3.0f); // 发射速度

                    worldIn.addFreshEntity(ss);

                    entity.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F,
                            1.45F);
                }
            });
        }
    }

    private static void performStormSwords(final Long pressTime, LivingEntity rawEntity) {
        if (!(rawEntity instanceof ServerPlayer entity)) {
            return;
        }

        InputCommand targetCommnad = InputCommand.M_DOWN;
        boolean inputSucceed = entity.getCapability(CapabilityInputState.INPUT_STATE)
                .filter(input -> input.getCommands().contains(targetCommnad)
                        && input.getCommands().contains(InputCommand.SNEAK)
                        && input.getCommands().contains(InputCommand.BACK)
                        && !input.getCommands().contains(InputCommand.FORWARD)
                        && input.getLastPressTime(targetCommnad) == pressTime)
                .isPresent();
        if (!inputSucceed) {
            return;
        }

        // summon
        entity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state) -> {
            Level worldIn = entity.level();
            Entity target = state.getTargetEntity(worldIn);

            if (target == null || !target.isAlive() || target.isRemoved()) {
                return;
            }

            if (state.getProudSoulCount() < SlashBladeConfig.SUMMON_SWORD_ART_COST.get()) {
                return;
            }

            state.setProudSoulCount(state.getProudSoulCount() - SlashBladeConfig.SUMMON_SWORD_ART_COST.get());

            //烈风环影剑
            AdvancementHelper.grantCriterion(entity, ADVANCEMENT_STORM_SWORDS);

            int rank = entity.getCapability(CapabilityConcentrationRank.RANK_POINT)
                    .map(r -> r.getRank(worldIn.getGameTime()).level).orElse(0);

            int count = 6;

            if (IConcentrationRank.ConcentrationRanks.S.level <= rank) {
                count = 8;
            }

            for(int i = 0; i < count; i++) {
                SummondSpiralSwordEntity ss = new SummondSpiralSwordEntity(RecastingEntities.SUMMOND_SPIRAL_SWORD.get(), worldIn, entity);

                // 设置旋转中心为目标
                ss.setCenterEntity(target);

                // 设置旋转参数（使用辅助方法自动计算修饰参数）
                ss.setRadiusExpansion(2.5f, 6.0f, 30); // 40 tick 后从 2.5 格扩展到 8 格
                ss.setSpeedDecay(16.0f, 0.3f, 30); // 40 tick 后从 16 度/tick 衰减到接近 0
                ss.setRotationAngle(360.0f / count * i); // 均匀分布在圆周上
                ss.setRotationAxis(new Vec3(0, 1, 0)); // 绕 Y 轴旋转
                ss.setRotationDirectionOutward(false); // 朝向中心

                // 设置基本属性
                ss.setModifiedRatio(0.1f);
                ss.setColor(state.getColorCode());
                ss.setRoll(0);
                ss.setStartDelay(30); // 延迟 40 tick (2秒) 后发射


                worldIn.addFreshEntity(ss);

                entity.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F, 1.45F);
            }
        });
    }


    private static void performBlisteringSwords(final Long pressTime, LivingEntity rawEntity, long now) {
        if (!(rawEntity instanceof ServerPlayer entity)) {
            return;
        }

        InputCommand targetCommnad = InputCommand.M_DOWN;
        boolean inputSucceed = entity.getCapability(CapabilityInputState.INPUT_STATE)
                .filter(input -> input.getCommands().contains(targetCommnad)
                        && input.getCommands().contains(InputCommand.SNEAK)
                        && input.getCommands().contains(InputCommand.FORWARD)
                        && input.getLastPressTime(InputCommand.BACK) + 20 < now
                        && input.getLastPressTime(targetCommnad) == pressTime)
                .isPresent();
        if (!inputSucceed) {
            return;
        }

        // summon
        entity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state) -> {

            Level worldIn = entity.level();

            if (state.getProudSoulCount() < SlashBladeConfig.SUMMON_SWORD_ART_COST.get()) {
                return;
            }
            state.setProudSoulCount(state.getProudSoulCount() - SlashBladeConfig.SUMMON_SWORD_ART_COST.get());

            //急袭幻影剑
            AdvancementHelper.grantCriterion(entity, ADVANCEMENT_BLISTERING_SWORDS);

            int rank = entity.getCapability(CapabilityConcentrationRank.RANK_POINT)
                    .map(r -> r.getRank(worldIn.getGameTime()).level).orElse(0);

            int count = 6;

            if (IConcentrationRank.ConcentrationRanks.S.level <= rank) {
                count = 8;
            }

            for(int i = 0; i < count; i++) {
                SummondSwordEntity ss = new SummondSwordEntity(
                        RecastingEntities.SUMMOND_SWORD.get(), worldIn, entity);

                // 计算起始位置（参考 EntityBlisteringSwords.faceEntityStandby）
                Vec3 basePos = entity.position().add(0, entity.getEyeHeight() * 0.8, 0);

                boolean isRight = i % 2 == 0;
                int level = i / 2;
                double xOffset = (1 - 0.1 * level) * (isRight
                        ? 1
                        : -1);
                double yOffset = 0.25 * level;
                double zOffset = -0.1 * level;

                Vec3 offset = new Vec3(xOffset, yOffset, zOffset);
                offset = offset.xRot((float) Math.toRadians(-entity.getXRot()));
                offset = offset.yRot((float) Math.toRadians(-entity.getYRot()));

                Vec3 startPos = basePos.add(offset);

                // 设置位置
                ss.setPos(startPos);

                // 设置基本属性
                ss.setModifiedRatio(0.1f);
                ss.setColor(state.getColorCode());
                ss.setRoll(0);
                ss.setStartDelay(i);
                ss.setSeep(3.5f);
                ss.setIgnoringBlock(false);


                // 计算目标位置（参考 EntityBlisteringSwords 的发射逻辑）
                Entity target = state.getTargetEntity(worldIn);
                Vec3 targetPos;
                if (target != null && target.isAlive()) {
                    targetPos = new Vec3(target.getX(), target.getY() + target.getEyeHeight() * 0.5, target.getZ());
                } else {
                    // 如果没有目标，朝向玩家前方
                    Vec3 forwardDir = entity.getLookAngle();
                    targetPos = startPos.add(forwardDir.scale(40));
                }

                ss.lookAt(targetPos, false, true);

                worldIn.addFreshEntity(ss);

                entity.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F,
                        1.45F);
            }
        });
    }

    private static void performHeavyRains(final Long pressTime, LivingEntity rawEntity, long now) {
        if (!(rawEntity instanceof ServerPlayer entity)) {
            return;
        }

        InputCommand targetCommnad = InputCommand.M_DOWN;
        boolean inputSucceed = entity.getCapability(CapabilityInputState.INPUT_STATE)
                .filter(input -> input.getCommands().contains(targetCommnad)
                        && input.getCommands().contains(InputCommand.SNEAK)
                        && input.getCommands().contains(InputCommand.FORWARD)
                        && input.getLastPressTime(InputCommand.BACK) + 30 > now
                        && input.getLastPressTime(targetCommnad) == pressTime)
                .isPresent();
        if (!inputSucceed) {
            return;
        }

        // summon
        entity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state) -> {

            Level worldIn = entity.level();
            Entity target = state.getTargetEntity(worldIn);
            if (state.getProudSoulCount() < SlashBladeConfig.SUMMON_SWORD_ART_COST.get()) {
                return;
            }
            state.setProudSoulCount(
                    state.getProudSoulCount() - SlashBladeConfig.SUMMON_SWORD_ART_COST.get());

            //五月雨
            AdvancementHelper.grantCriterion(entity, ADVANCEMENT_HEAVY_RAIN_SWORDS);

            int rank = entity.getCapability(CapabilityConcentrationRank.RANK_POINT)
                    .map(r -> r.getRank(worldIn.getGameTime()).level).orElse(0);

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

            // 第一个剑（中心位置，无延迟）
            {
                SummondSwordEntity ss = new SummondSwordEntity(RecastingEntities.SUMMOND_SWORD.get(), worldIn, entity);

                ss.setModifiedRatio(0.1f);
                ss.setColor(state.getColorCode());
                ss.setRoll(0);
                ss.setStartDelay(0);
                ss.setIgnoringBlock(false);

                // 设置在目标上方
                ss.setPos(basePos);
                // 向下发射（-90度俯仰角）
                ss.setRot(entity.getYRot(), -90.0f, false);
                ss.updateMotion(ss.getSeep());

                worldIn.addFreshEntity(ss);
            }

            // 其他剑（有扩散和延迟）
            for(int i = 0; i < count; i++) {
                for(int l = 0; l < multiplier; l++) {
                    SummondSwordEntity ss = new SummondSwordEntity(RecastingEntities.SUMMOND_SWORD.get(), worldIn, entity);

                    ss.setModifiedRatio(0.1f);
                    ss.setColor(state.getColorCode());
                    ss.setRoll(0);
                    ss.setStartDelay(i); // 延迟发射
                    ss.setIgnoringBlock(false);

                    // 计算扩散位置（随机偏移）
                    double spreadX = (entity.getRandom().nextDouble() - 0.5) * 4.0; // -2 到 +2 格
                    double spreadZ = (entity.getRandom().nextDouble() - 0.5) * 4.0; // -2 到 +2 格
                    Vec3 spreadPos = basePos.add(spreadX, 0, spreadZ);

                    ss.setPos(spreadPos);
                    // 向下发射（-90度俯仰角）
                    ss.setRot(entity.getYRot(), 90.0f, false);
                    ss.updateMotion(ss.getSeep());

                    worldIn.addFreshEntity(ss);

                    entity.playNotifySound(SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.2F,
                            1.45F);
                }
            }
        });
    }
}

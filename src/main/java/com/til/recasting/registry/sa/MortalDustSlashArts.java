package com.til.recasting.registry.sa;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.entity.TrackingSummondSwordEntity;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.MortalDustEffectHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.util.DamageStructure;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * [回到未来计划]红尘滚滚：对周围敌人头顶落下追踪幻影剑，叠伤溅射，橙尘拖尾与命中喷泉。
 */
@Setter
@Accessors(chain = true)
public class MortalDustSlashArts extends ExtendedSlashArts {

    private static final String BONUS_KEY = "bonus";

    private float seekRange = 35.0f;
    private float bladeRatio = 0.72f;
    private float splashRange = 12.0f;
    private int hoverTicks = 20;
    private int bladeLife = 300;
    private int breakDelay = 10;
    private int buffWindowSeconds = 10;
    private float spawnHeightOffset = 12.0f;

    @Override
    public void trigger(
            LivingEntity livingEntity,
            ItemStack itemStack,
            ISlashBladeState slashBladeState,
            RenderDefinitionExtension renderDefinitionExtension,
            PropertiesDefinitionExtension propertiesDefinitionExtension
    ) {
        Level level = livingEntity.level();
        if (level.isClientSide()) {
            return;
        }

        List<LivingEntity> targets = EntityHelper.getTargettableLivingEntityWithinAABB(
                level,
                livingEntity,
                livingEntity.position(),
                seekRange
        );
        if (targets.isEmpty()) {
            return;
        }

        int color = slashBladeState.getColorCode();
        List<AttackType> attackTypes = List.of(RecastingAttackTypes.SUMMOND_SWORD_ATTACK.get());

        for (LivingEntity target : targets) {
            TrackingSummondSwordEntity blade = new TrackingSummondSwordEntity(
                    RecastingEntities.TRACKING_SUMMOND_SWORD.get(),
                    level,
                    livingEntity
            );
            // 头顶悬停：PREPARE 期间不位移；期满再进入 FLYING 并立刻追踪
            // 旧版 EntityYaoHRender 为空，实体本体不可见，观感全靠粒子
            blade.setPos(target.getX(), livingEntity.getY() + spawnHeightOffset, target.getZ());
            blade.setDeltaMovement(Vec3.ZERO);
            blade.setColor(color);
            blade.setModifiedRatio(bladeRatio);
            blade.setStartDelay(hoverTicks);
            blade.setInterval(0);
            blade.setMaxLifeTime(bladeLife);
            blade.setBreakDelay(breakDelay);
            blade.setTargetEntity(target);
            blade.setIgnoringBlock(true);
            blade.setSize(0.001f);
            blade.setMute(true);
            blade.lookAt(PosHelper.getEntityAimPosition(target), false);

            blade.tickCallbackPoint.register(() -> {
                if (!(blade.level() instanceof ServerLevel serverLevel)) {
                    return;
                }
                SummondSwordEntity.ActionType action = blade.getActionType();
                if (action != SummondSwordEntity.ActionType.PREPARE
                        && action != SummondSwordEntity.ActionType.FLYING) {
                    return;
                }
                MortalDustEffectHelper.spawnTrail(serverLevel, blade.position());
            });

            blade.attackActionCallbackPoint.register(hit -> {
                if (!(hit instanceof LivingEntity livingHit)) {
                    return;
                }
                LivingEntity shooter = blade.getShooter();
                if (shooter == null || !shooter.isAlive()) {
                    return;
                }

                float bonus = readBonus(livingHit);
                float thisHitAmount = estimateHitAmount(shooter, bladeRatio);

                // 主目标：已由幻影剑打过一刀；再补叠伤层
                if (bonus > 0.0f) {
                    AttackHelper.attack(
                            shooter,
                            livingHit,
                            new DamageStructure(0.0f, bonus),
                            attackTypes
                    );
                }

                // 溅射：同帧伤害 = 本击量级 + 叠层（排除主目标）
                AttackHelper.areaAttack(
                        shooter,
                        livingHit.position().add(0.0, livingHit.getBbHeight() * 0.5, 0.0),
                        new DamageStructure(bladeRatio, bonus),
                        splashRange,
                        attackTypes,
                        List.of(livingHit),
                        null
                );
                writeBonus(livingHit, bonus + thisHitAmount);

                if (blade.level() instanceof ServerLevel serverLevel) {
                    // 旧版在剑实体位置喷 YAO_ATTECK
                    MortalDustEffectHelper.spawnHitBurst(serverLevel, blade.position());
                }

                // 贴附结束 BROKEN 走半伤
                blade.setModifiedRatio(bladeRatio * 0.5f);
            });

            level.addFreshEntity(blade);
        }

        level.playSound(
                null,
                livingEntity.getX(),
                livingEntity.getY(),
                livingEntity.getZ(),
                SoundEvents.BLAZE_SHOOT,
                SoundSource.PLAYERS,
                0.55F,
                0.65F + livingEntity.getRandom().nextFloat() * 0.2F
        );
    }

    private float estimateHitAmount(LivingEntity attacker, float ratio) {
        AttributeInstance attribute = attacker.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attribute == null) {
            return Math.max(1.0f, ratio);
        }
        return Math.max(1.0f, (float) (attribute.getValue() * ratio));
    }

    private float readBonus(LivingEntity target) {
        BuffType buffType = RecastingBuffTypes.MORTAL_DUST.get();
        IBuffStackData data = target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).orElse(null);
        if (data == null) {
            return 0.0f;
        }
        if (data.getLevel(buffType, target.level()) <= 0) {
            return 0.0f;
        }
        return data.getOrCreateCustomData(buffType, target.level()).getFloat(BONUS_KEY);
    }

    private void writeBonus(LivingEntity target, float bonus) {
        BuffType buffType = RecastingBuffTypes.MORTAL_DUST.get();
        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            // 层数只表示叠伤窗口剩余秒数，精确累加量在 customData
            data.setLevel(buffType, buffWindowSeconds, target.level());
            CompoundTag customData = data.getOrCreateCustomData(buffType, target.level());
            customData.putFloat(BONUS_KEY, Math.max(0.0f, bonus));
        });
    }
}

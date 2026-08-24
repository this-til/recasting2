package com.til.recasting.registry.se;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.ITimeRun;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.BuffSourceHelper;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingParticleTypes;
import com.til.recasting.util.DamageStructure;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;
import java.util.UUID;

/***
 * 茶韵
 * 命中储存部分伤害延迟释放；连续命中累加并刷新倒计时
 * 剑气命中额外叠加固定层级
 * 延迟伤害以 Buff 层数记录（伤害 × 10，不足 1 记为 1）
 */
@Setter
@Accessors(chain = true)
public class TeaAromaSpecialEffect extends ExtendedSpecialEffect {

    float storeRatio = 0.2f;
    int delayTicks = 30;
    /**
     * 剑气命中时额外叠加的 Buff 层级
     */
    int driveBonusStacks = 10;

    /**
     * 在所有倍率结算之后读取最终伤害，写入目标茶韵 Buff，并调度延迟释放。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAttackAmplifier(AttackAmplifierEvent event) {
        LivingEntity target = resolveServerLivingTarget(event);
        if (target == null) {
            return;
        }
        if (event.getAttackTypeList().contains(RecastingAttackTypes.TEA_AROMA_ATTACK.get())) {
            return;
        }

        LivingEntity attacker = event.getAttacker();
        AttributeInstance attribute = attacker.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attribute == null) {
            return;
        }

        float baseDamage = (float) (attribute.getValue() * event.getUltimatelyModifiedRatio());
        baseDamage += event.getExtraDamage();
        float storedDamage = baseDamage * storeRatio;
        int addUnits = Math.max(1, (int) (storedDamage * 10f));
        if (event.getAttackTypeList().contains(RecastingAttackTypes.DRIVE_ATTACK.get())) {
            addUnits += driveBonusStacks;
        }
        int finalAddUnits = addUnits;

        Level world = target.level();

        var buffStackData = RecastingAttachments.buffStackData(target);
        int current = buffStackData.getLevel(RecastingBuffTypes.TEA_AROMA.get(), world);
        buffStackData.setLevel(RecastingBuffTypes.TEA_AROMA.get(), current + finalAddUnits, world);
        BuffSourceHelper.recordSourceEntity(buffStackData, RecastingBuffTypes.TEA_AROMA.get(), target, attacker);

        ITimeRun timeRun = RecastingAttachments.timeRun(attacker);
        String timerName = buildReleaseTimerName(target.getUUID());
        ITimeRun.TimerCell existing = timeRun.getNamedTimerCell(timerName);
        if (existing != null) {
            // 重置已过时间，等价于把唤醒点重新推后 delayTicks
            existing.use(false);
            return;
        }

        timeRun.addNamedTimerCell(
                timerName,
                new ITimeRun.TimerCell(
                        () -> tryRelease(attacker, target),
                        delayTicks
                )
        );
    }

    private String buildReleaseTimerName(UUID targetId) {
        return "tea_aroma_release:" + targetId;
    }

    private void tryRelease(LivingEntity attacker, LivingEntity target) {
        if (attacker.level().isClientSide()) {
            return;
        }
        if (!target.isAlive() || target.level() != attacker.level()) {
            return;
        }

        var buffStackData = RecastingAttachments.buffStackData(target);
        IBuffStackData.BuffEntry entry = buffStackData.getEntry(RecastingBuffTypes.TEA_AROMA.get());
        if (entry == null || entry.getLevel() <= 0) {
            return;
        }

        LivingEntity source = BuffSourceHelper.getSourceEntity(entry, target.level());
        int units = entry.getLevel();
        buffStackData.setLevel(RecastingBuffTypes.TEA_AROMA.get(), 0, target.level());

        if (source == null) {
            return;
        }

        float releaseDamage = units / 10f;
        AttackHelper.attack(
                source,
                target,
                new DamageStructure(0f, releaseDamage),
                List.of(
                        RecastingAttackTypes.TEA_AROMA_ATTACK.get(),
                        RecastingAttackTypes.NO_RECURSION_ATTACK.get()
                )
        );

        if (target.level() instanceof ServerLevel serverLevel) {
            Vec3 pos = target.position().add(0, target.getEyeHeight() * 0.5, 0);
            // 裂隙撕开：锐利斩击感 + 空间回缝感，不用爆炸声
            serverLevel.playSound(
                    null, pos.x, pos.y, pos.z,
                    SoundEvents.PLAYER_ATTACK_SWEEP,
                    SoundSource.PLAYERS,
                    0.55F,
                    0.75F + target.getRandom().nextFloat() * 0.2F
            );
            serverLevel.playSound(
                    null, pos.x, pos.y, pos.z,
                    SoundEvents.ENDERMAN_TELEPORT,
                    SoundSource.PLAYERS,
                    0.25F,
                    1.4F + target.getRandom().nextFloat() * 0.3F
            );
            // 茶色 RGB(180, 140, 80) 经速度通道传入粒子着色
            ParticleHelper.sendParticlesLongRange(
                    serverLevel,
                    RecastingParticleTypes.TEA_AROMA.get(),
                    pos.x, pos.y, pos.z,
                    0,
                    180.0 / 255.0, 140.0 / 255.0, 80.0 / 255.0,
                    1.0
            );
        }
    }
}

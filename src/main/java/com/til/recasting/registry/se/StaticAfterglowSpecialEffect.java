package com.til.recasting.registry.se;

import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.LightningChainEffectHelper;
import com.til.recasting.handler.LightningChainHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.util.DamageStructure;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;

/**
 * 静电余韵
 * 拥有此 SE 即生效：
 * - 造成伤害时附带雷电附加伤害（受击目标内置冷却）
 * - 造成雷电伤害时有概率触发闪电链 L1（受击目标内置冷却），传导无限制
 */
@Setter
@Accessors(chain = true)
public class StaticAfterglowSpecialEffect extends ExtendedSpecialEffect {

    /**
     * 雷电附加伤害倍率
     */
    private float lightningDamageRatio = 0.3f;

    /**
     * 附加雷电伤害的内置冷却（tick），记录在受击目标
     */
    private int damageCooldownTicks = 4;

    /**
     * 触发闪电链的概率
     */
    private float chainChance = 0.10f;

    /**
     * 闪电链触发的内置冷却（tick），记录在受击目标
     */
    private int chainCooldownTicks = 14;

    @SubscribeEvent
    public void onAttackAmplifier(AttackAmplifierEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        LivingEntity attacker = event.getAttacker();
        Level level = attacker.level();
        if (level.isClientSide()) {
            return;
        }

        if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
            return;
        }

        if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
            return;
        }

        ISlashBladeState state = event.getSlashBladeState();
        int color = state.getColorCode();

        boolean dealtLightning = event.getAttackTypeList().contains(RecastingAttackTypes.LIGHTNING_ATTACK.get());
        boolean bonusAdded = tryAddBonusLightning(event, attacker, target, level, color);

        if (dealtLightning || bonusAdded) {
            tryTriggerLightningChain(attacker, target, level, color);
        }
    }

    private boolean tryAddBonusLightning(
            AttackAmplifierEvent event,
            LivingEntity attacker,
            LivingEntity target,
            Level level,
            int color
    ) {
        var targetBuff = RecastingAttachments.buffStackData(target);
        if (targetBuff.getLevel(RecastingBuffTypes.STATIC_AFTERGLOW_DAMAGE_CD.get(), level) > 0) {
            return false;
        }

        AttackAmplifierEvent.DamageSourceInfo damageSourceInfo =
                RecastingAttackTypes.LIGHTNING_ATTACK.get().createDamageSource(attacker, target);
        if (damageSourceInfo == null) {
            return false;
        }

        targetBuff.setLevel(RecastingBuffTypes.STATIC_AFTERGLOW_DAMAGE_CD.get(), damageCooldownTicks, level);
        event.addDamageSourceInfo(
                damageSourceInfo.damageSource(),
                new DamageStructure(lightningDamageRatio, 0f)
        );

        if (level instanceof ServerLevel serverLevel) {
            Vec3 targetPos = target.getBoundingBox().getCenter();
            LightningChainEffectHelper.spawnHitParticles(serverLevel, targetPos, color);
        }

        return true;
    }

    private void tryTriggerLightningChain(LivingEntity attacker, LivingEntity target, Level level, int color) {
        if (level.getRandom().nextFloat() >= chainChance) {
            return;
        }

        var targetBuff = RecastingAttachments.buffStackData(target);
        if (targetBuff.getLevel(RecastingBuffTypes.STATIC_AFTERGLOW_CHAIN_CD.get(), level) > 0) {
            return;
        }
        targetBuff.setLevel(RecastingBuffTypes.STATIC_AFTERGLOW_CHAIN_CD.get(), chainCooldownTicks, level);

        if (level instanceof ServerLevel serverLevel) {
            List<AttackType> attackTypes = List.of(
                    RecastingAttackTypes.LIGHTNING_ATTACK.get()
            );
            Vec3 from = PosHelper.getAboveHead(attacker, 0.5);
            Vec3 seedPos = target.getBoundingBox().getCenter();
            LightningChainEffectHelper.sync(serverLevel, from, seedPos, color);
            LightningChainHelper.playThunderSound(serverLevel, seedPos, 0);

            LightningChainHelper.startHopSequence(
                    attacker, seedPos, target, serverLevel, color,
                    6, 2, 8f, 0.4f, attackTypes, false
            );
        }
    }
}

package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.EntityPredicateHelper;
import com.til.recasting.capability.ITimeRun;
import com.til.recasting.handler.InventorySlashBladeSeHelper;
import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.SpecialEffectsRegistry;
import com.til.recasting.util.DamageStructure;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
import java.util.List;

/**
 * 犬咬
 * 对锁定目标 / 视锥索敌造成多段延迟伤害；段数随力量附魔增加，最多计入 5 级。
 */
@Setter
@Accessors(chain = true)
public class DogBiteSlashArts extends ExtendedSlashArts {

    private int baseHits = 4;
    private int maxEnchantBonus = 5;
    private float hitRatio = 0.3f;
    private int delayTicks = 5;

    @Override
    public void trigger(
            LivingEntity livingEntity,
            ItemStack itemStack,
            ISlashBladeState slashBladeState,
            RenderDefinitionExtension renderDefinitionExtension,
            PropertiesDefinitionExtension propertiesDefinitionExtension
    ) {
        if (livingEntity.level().isClientSide()) {
            return;
        }
        LivingEntity target = resolveLockThenCone(livingEntity, slashBladeState, livingEntity.level());
        if (target == null) {
            return;
        }
        perform(livingEntity, itemStack, target, false);
    }

    /**
     * 对指定目标释放犬咬多段攻击（SA / SE 共用）。
     *
     * @param resolveFromInventory true 时每段经 {@link InventorySlashBladeSeHelper} 槽位缓存取刀（轩辕同款）。
     */
    public void perform(LivingEntity attacker, ItemStack blade, LivingEntity target, boolean resolveFromInventory) {
        if (attacker == null || target == null) {
            return;
        }
        Level level = attacker.level();
        if (level.isClientSide()) {
            return;
        }
        if (!EntityPredicateHelper.canTarget(attacker, target)) {
            return;
        }

        ItemStack enchantBlade = blade;
        if (resolveFromInventory || enchantBlade == null || enchantBlade.isEmpty()) {
            InventorySlashBladeSeHelper.BladeSeHit hit = InventorySlashBladeSeHelper.findFirstInInventory(
                    attacker,
                    SpecialEffectsRegistry.DOG_BOND
            );
            if (hit == null) {
                return;
            }
            enchantBlade = hit.blade();
        }

        int enchantLevel = Math.min(enchantBlade.getEnchantmentLevel(Enchantments.POWER_ARROWS), maxEnchantBonus);
        int hits = baseHits + Math.max(0, enchantLevel);

        ITimeRun timeRun = RecastingAttachments.timeRun(attacker);
        for(int i = 0; i < hits; i++) {
            int delay = delayTicks * i;
            timeRun.addTimerCell(() -> biteOnce(attacker, blade, target, resolveFromInventory), delay);
        }
    }

    private void biteOnce(
            LivingEntity attacker,
            ItemStack saBlade,
            LivingEntity target,
            boolean resolveFromInventory
    ) {
        if (!attacker.isAlive() || !target.isAlive() || target.isRemoved()) {
            return;
        }
        if (!EntityPredicateHelper.canTarget(attacker, target)) {
            return;
        }

        ItemStack blade = saBlade;
        if (resolveFromInventory) {
            InventorySlashBladeSeHelper.BladeSeHit hit = InventorySlashBladeSeHelper.findFirstInInventory(
                    attacker,
                    SpecialEffectsRegistry.DOG_BOND
            );
            if (hit == null) {
                return;
            }
            blade = hit.blade();
        }
        if (blade == null || blade.isEmpty()) {
            return;
        }

        AttackHelper.attack(
                attacker,
                target,
                new DamageStructure(hitRatio, 0f),
                List.of(
                        RecastingAttackTypes.DOG_BITE_ATTACK.get(),
                        RecastingAttackTypes.NO_RECURSION_ATTACK.get()
                ),
                blade
        );

        Level level = target.level();
        Vec3 pos = target.getBoundingBox().getCenter();
        level.playSound(
                null,
                pos.x,
                pos.y,
                pos.z,
                SoundEvents.WOLF_GROWL,
                SoundSource.PLAYERS,
                0.55F,
                0.9F + attacker.getRandom().nextFloat() * 0.2F
        );

        if (level instanceof ServerLevel serverLevel) {
            ParticleHelper.sendParticlesLongRange(
                    serverLevel,
                    ParticleTypes.CRIT,
                    pos.x,
                    pos.y,
                    pos.z,
                    12,
                    0.35,
                    0.45,
                    0.35,
                    0.12
            );
            ParticleHelper.sendParticlesLongRange(
                    serverLevel,
                    ParticleTypes.ENCHANTED_HIT,
                    pos.x,
                    pos.y,
                    pos.z,
                    8,
                    0.3,
                    0.4,
                    0.3,
                    0.2
            );
        }
    }

    @Nullable
    private static LivingEntity resolveLockThenCone(
            LivingEntity caster,
            ISlashBladeState slashBladeState,
            Level level
    ) {
        Entity lock = slashBladeState.getTargetEntity(level);
        if (lock instanceof LivingEntity locked
                && locked.isAlive()
                && !locked.isRemoved()
                && EntityPredicateHelper.canTarget(caster, locked)) {
            return locked;
        }
        return EntityHelper.selectClosestInViewCone(caster).orElse(null);
    }
}

package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 长空落日 Slash Arts
 * 星流索敌齐射幻影剑；命中叠加日核并播火焰/岩浆命中粒子；叠晖 Buff 在有日核时追加晖光。
 */
@Setter
@Accessors(chain = true)
public class LongSkySunsetSlashArts extends ExtendedSlashArts {

    private final int coreAddPerHit = 4;

    int swordCount = 8;
    float attack = 0.12f;
    float range = 24f;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {
        Level worldIn = livingEntity.level();
        if (worldIn.isClientSide()) {
            return;
        }

        Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);
        List<LivingEntity> entityList = EntityHelper.getTargettableLivingEntityWithinAABB(
                worldIn,
                livingEntity,
                attackPos,
                range
        );

        for (int i = 0; i < swordCount; i++) {
            SummondSwordEntity summonedSword = new SummondSwordEntity(
                    RecastingEntities.SUMMOND_SWORD.get(),
                    worldIn,
                    livingEntity
            );
            summonedSword.setColor(slashBladeState.getColorCode());
            summonedSword.setModifiedRatio(attack);
            summonedSword.setStartDelay(10 + livingEntity.getRandom().nextInt(10));
            summonedSword.addAttackType(RecastingAttackTypes.SUNSET_CORE_MARK.get());
            summonedSword.attackActionCallbackPoint.register(hitEntity -> applySunsetCore(hitEntity, worldIn));

            Vec3 targetPos;
            if (!entityList.isEmpty()) {
                Entity target = entityList.get(livingEntity.getRandom().nextInt(entityList.size()));
                targetPos = new Vec3(target.getX(), target.getY() + target.getEyeHeight() * 0.5, target.getZ());
            } else {
                targetPos = attackPos;
            }
            summonedSword.lookAt(targetPos, false);
            worldIn.addFreshEntity(summonedSword);
        }

        livingEntity.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 0.2F, 1.45F);
    }

    private void applySunsetCore(Entity hitEntity, Level world) {
        if (!(hitEntity instanceof LivingEntity living) || !living.isAlive()) {
            return;
        }
        var buffStackData = RecastingAttachments.buffStackData(living);
        int currentCore = buffStackData.getLevel(RecastingBuffTypes.SUNSET_CORE.get(), world);
        buffStackData.setLevel(RecastingBuffTypes.SUNSET_CORE.get(), currentCore + coreAddPerHit, world);
        spawnSunsetCoreHitParticles(living);
    }

    private void spawnSunsetCoreHitParticles(LivingEntity target) {
        if (!(target.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        double x = target.getX();
        double y = target.getY() + target.getBbHeight() * 0.5;
        double z = target.getZ();
        ParticleHelper.sendParticlesLongRange(serverLevel, ParticleTypes.FLAME, x, y, z, 12, 0.45, 0.4, 0.45, 0.03);
        ParticleHelper.sendParticlesLongRange(serverLevel, ParticleTypes.LAVA, x, y, z, 3, 0.25, 0.25, 0.25, 0.0);
    }
}

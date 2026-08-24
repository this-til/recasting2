package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.SlashEffectEntity;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 异界斩切：八向挥刀（yRot 平分 360°、roll=45°）；
 * 单次 SA 内每个实体仅第一次命中触发 {@link #applyImpactBatch}。
 */
@Setter
@Accessors(chain = true)
public class OtherworldSlashSlashArts extends ExtendedSlashArts {

    private static final int SLASH_COUNT = 8;
    private static final float Y_ROT_STEP = 360.0f / SLASH_COUNT;
    private static final float SLASH_ROLL = 45.0f;

    private float slashRatio = 0.32f;
    private float slashSize = 3.0f;
    private int impactHits = 4;
    private float impactRatio = 0.08f;

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

        Set<UUID> impacted = new HashSet<>();
        float baseYRot = livingEntity.getYRot();
        Vec3 pos = livingEntity.position()
                .add(0.0D, livingEntity.getEyeHeight() * 0.75D, 0.0D)
                .add(livingEntity.getLookAngle().scale(0.3f));

        for (int i = 0; i < SLASH_COUNT; i++) {
            SlashEffectEntity slash = new SlashEffectEntity(
                    RecastingEntities.SLASH_EFFECT.get(),
                    level,
                    livingEntity
            );
            slash.setPos(pos.x, pos.y, pos.z);
            slash.setYRot(baseYRot + i * Y_ROT_STEP);
            slash.setXRot(0.0f);
            slash.setRoll(SLASH_ROLL);
            slash.setColor(slashBladeState.getColorCode());
            slash.setMute(i != 0);
            slash.setModifiedRatio(slashRatio);
            slash.setSize(slashSize);
            slash.attackActionCallbackPoint.register(hitEntity -> {
                if (!impacted.add(hitEntity.getUUID())) {
                    return;
                }
                applyImpactBatch(livingEntity, hitEntity, slashBladeState);
            });
            level.addFreshEntity(slash);
        }
    }

    private void applyImpactBatch(LivingEntity attacker, LivingEntity target, ISlashBladeState state) {
        Level worldIn = attacker.level();
        if (worldIn.isClientSide()) {
            return;
        }
        if (!target.isAlive()) {
            return;
        }

        Vec3 pos = target.position().add(0, target.getEyeHeight() * 0.5, 0);
        for (int i = 0; i < impactHits; i++) {
            SummondSwordEntity summondSword = new SummondSwordEntity(
                    RecastingEntities.SUMMOND_SWORD.get(),
                    worldIn,
                    attacker
            );
            summondSword.setPos(pos.x, pos.y, pos.z);
            summondSword.setYRot(attacker.getRandom().nextFloat() * 360);
            summondSword.setXRot(attacker.getRandom().nextFloat() * 360);
            summondSword.setColor(state.getColorCode());
            summondSword.setModifiedRatio(impactRatio);
            summondSword.setMaxLifeTime(40);
            summondSword.addAttackType(RecastingAttackTypes.NO_RECURSION_ATTACK.get());
            worldIn.addFreshEntity(summondSword);
            summondSword.onHitEntity(target, SummondSwordEntity.SummondAttackType.HIT);
        }
    }
}

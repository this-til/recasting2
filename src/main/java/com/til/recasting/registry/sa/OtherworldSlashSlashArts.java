package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.SlashEffectEntity;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.util.DamageStructure;
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
 * 异界斩切：出伤仍是单次 {@link AttackHelper#doSlash}（size×3、0.32、冲击一批）；
 * 表现另补巴刃式四向环斩弧（三向纯视觉 + 出伤刀对齐其中一向）。特效 TODO 悬空。
 */
@Setter
@Accessors(chain = true)
public class OtherworldSlashSlashArts extends ExtendedSlashArts {

    private float slashRatio = 0.32f;
    private float slashSize = 3.0f;
    private int impactHits = 4;
    private float impactRatio = 0.08f;

    /** 巴刃四向；第一向与出伤斩击共用朝向。 */
    private static final float[] CIRCLE_Y_ROT_OFFSETS = {180.0f, 90.0f, 0.0f, -90.0f};

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

        // 出伤：保持单次 doSlash，不因四向表现翻倍
        SlashEffectEntity slash = AttackHelper.doSlash(
                livingEntity,
                0.0f,
                slashBladeState.getColorCode(),
                Vec3.ZERO,
                false,
                false,
                new DamageStructure(slashRatio, 0.0f),
                slashSize,
                null
        );
        if (slash == null) {
            return;
        }

        applyCircleFacing(slash, livingEntity, CIRCLE_Y_ROT_OFFSETS[0]);

        Set<UUID> impacted = new HashSet<>();
        slash.attackActionCallbackPoint.register(hitEntity -> {
            if (!impacted.add(hitEntity.getUUID())) {
                return;
            }
            applyImpactBatch(livingEntity, hitEntity, slashBladeState);
        });

        // 表现：其余三向纯视觉环斩（不出伤）
        for (int i = 1; i < CIRCLE_Y_ROT_OFFSETS.length; i++) {
            spawnVisualCircleSlash(livingEntity, slashBladeState, CIRCLE_Y_ROT_OFFSETS[i]);
        }
    }

    private void spawnVisualCircleSlash(
            LivingEntity livingEntity,
            ISlashBladeState slashBladeState,
            float yRotOffset
    ) {
        Level level = livingEntity.level();
        Vec3 pos = livingEntity.position()
                .add(0.0D, livingEntity.getEyeHeight() * 0.75D, 0.0D)
                .add(livingEntity.getLookAngle().scale(0.3f));

        SlashEffectEntity visual = new SlashEffectEntity(
                RecastingEntities.SLASH_EFFECT.get(),
                level,
                livingEntity
        );
        visual.setPos(pos.x, pos.y, pos.z);
        applyCircleFacing(visual, livingEntity, yRotOffset);
        visual.setColor(slashBladeState.getColorCode());
        visual.setMute(true);
        visual.setSize(slashSize);
        visual.setParameterRange(0.0f);
        visual.setModifiedRatio(0.0f);
        level.addFreshEntity(visual);
    }

    private static void applyCircleFacing(SlashEffectEntity slash, LivingEntity livingEntity, float yRotOffset) {
        slash.setRoll(0.0f);
        slash.setYRot(livingEntity.getYRot() - 22.5f + yRotOffset);
        slash.setXRot(0.0f);
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

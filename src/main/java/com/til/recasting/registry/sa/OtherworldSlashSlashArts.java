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
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 异界斩切：对照原版巴刃（{@code circle_slash} / {@code CircleSlash}）四向环斩；
 * 斩击范围三倍；命中目标结算一批冲击（4×0.08），单次 SA 每目标仅一批。
 * 特效 TODO 悬空。
 */
@Setter
@Accessors(chain = true)
public class OtherworldSlashSlashArts extends ExtendedSlashArts {

    /** 与原版 CircleSlash DoSlashEvent damage 0.325 对齐的业务倍率。 */
    private float slashRatio = 0.32f;
    /** 相对原版默认斩击 size=1 的三倍。 */
    private float slashSize = 3.0f;
    private int impactHits = 4;
    private float impactRatio = 0.08f;

    /** 原版 ComboState 在 tick 4–7 依次调用的 yRot 偏移。 */
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

        Set<UUID> impacted = new HashSet<>();
        for (float yRotOffset : CIRCLE_Y_ROT_OFFSETS) {
            SlashEffectEntity slash = AttackHelper.doSlash(
                    livingEntity,
                    0.0f,
                    slashBladeState.getColorCode(),
                    Vec3.ZERO,
                    false,
                    true,
                    new DamageStructure(slashRatio, 0.0f),
                    slashSize,
                    KnockBacks.cancel
            );
            if (slash == null) {
                continue;
            }
            // 原版：jc.setYRot(living.getYRot() - 22.5F + yRot)
            slash.setYRot(livingEntity.getYRot() - 22.5f + yRotOffset);
            slash.setXRot(0.0f);
            slash.attackActionCallbackPoint.register(hitEntity -> {
                if (!impacted.add(hitEntity.getUUID())) {
                    return;
                }
                applyImpactBatch(livingEntity, hitEntity, slashBladeState);
            });
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

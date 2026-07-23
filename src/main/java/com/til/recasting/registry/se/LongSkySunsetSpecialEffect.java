package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.event.SlashBladeLockTargetTickEvent;
import com.til.recasting.handler.EntityPredicateHelper;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 长空落日 SE
 * 对 Shift 锁敌目标每 5 tick 发射一把幻影剑；伤害随 SE 层数提升，满级相对基础 +100%
 */
@Setter
@Accessors(chain = true)
public class LongSkySunsetSpecialEffect extends ExtendedSpecialEffect {

    private static final int COOLDOWN_TICKS = 5;

    float baseAttack = 0.15f;

    @SubscribeEvent
    public void onSlashBladeLockTargetTick(SlashBladeLockTargetTickEvent event) {
        LivingEntity user = event.getUser();
        if (user.tickCount % COOLDOWN_TICKS != 0) {
            return;
        }

        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        LivingEntity living = event.getTarget();
        if (!EntityPredicateHelper.canTarget(user, living)) {
            return;
        }

        PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getBlade());
        int seLevel = Math.max(1, getLevel(properties));
        int maxLevel = Math.max(1, getMaxLevel());
        float levelBonus = Math.min(1.0f, (float) seLevel / (float) maxLevel);
        float ratio = baseAttack * (1f + levelBonus);

        SummondSwordEntity sword = new SummondSwordEntity(
                RecastingEntities.SUMMOND_SWORD.get(),
                user.level(),
                user
        );
        sword.setColor(event.getSlashBladeState().getColorCode());
        sword.setModifiedRatio(ratio);
        sword.lookAt(new Vec3(
                living.getX(),
                living.getY() + living.getEyeHeight() * 0.5,
                living.getZ()
        ), false);
        user.level().addFreshEntity(sword);
    }
}

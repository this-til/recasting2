package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.handler.TimeBeyondChargeHandler;
import com.til.recasting.util.DamageStructure;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * 时之彼端：蓄力加速时间后释放十字斩，威力随蓄力时长提升。
 */
@Setter
@Accessors(chain = true)
public class TimeBeyondSlashArts extends ExtendedSlashArts {

    private float attackMin = 0.1f;
    private float attackMax = 3.0f;

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

        float progress = TimeBeyondChargeHandler.consumeProgress(livingEntity);
        float ratio = Mth.lerp(progress, attackMin, attackMax);
        float roll = livingEntity.getRandom().nextFloat() * 360.0f;
        float attackDistance = propertiesDefinitionExtension.attackDistance();
        int color = slashBladeState.getColorCode();
        DamageStructure damage = new DamageStructure(ratio, 0);

        AttackHelper.doSlash(
                livingEntity,
                roll,
                color,
                Vec3.ZERO,
                false,
                true,
                damage,
                attackDistance,
                KnockBacks.cancel
        );
        AttackHelper.doSlash(
                livingEntity,
                roll + 90.0f,
                color,
                Vec3.ZERO,
                false,
                true,
                damage,
                attackDistance,
                KnockBacks.cancel
        );
    }
}

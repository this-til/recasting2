package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.util.DamageStructure;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * 青芒 Slash Arts
 * 以玩家为中心，按360度均匀分布发动多次斩击
 */
@Setter
@Accessors(chain = true)
public class CyanGlowSlashArts extends ExtendedSlashArts {

    int attackNumber = 8;
    float hit = 0.3f;
    int delayTicks = 3;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {
        float angleStep = 360f / attackNumber;
        ITimeRun timeRun = RecastingAttachments.timeRun(livingEntity);

        for (int i = 0; i < attackNumber; i++) {
            int delay = delayTicks * i;
            int index = i;

            timeRun.addTimerCell(
                    () -> {
                        float angle = angleStep * index;
                        AttackHelper.doSlash(
                                livingEntity,
                                angle,
                                slashBladeState.getColorCode(),
                                Vec3.ZERO,
                                false,
                                false,
                                new DamageStructure(hit, 0),
                                propertiesDefinitionExtension.attackDistance(),
                                KnockBacks.cancel
                        );
                    },
                    delay
            );
        }
    }
}

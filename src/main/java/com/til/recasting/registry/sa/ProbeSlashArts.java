package com.til.recasting.registry.sa;

import com.til.recasting.Recasting;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.util.DamageStructure;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * P2 探针 SA：触发时打日志并走一次 {@link AttackHelper#doSlash}，用于验收事件链。
 * TODO(P3): 正式 SA 批量注册后删除本类与 {@code SlashArtsRegistry.PROBE}。
 */
public class ProbeSlashArts extends ExtendedSlashArts {

    @Override
    public void trigger(
            LivingEntity livingEntity,
            ItemStack itemStack,
            ISlashBladeState slashBladeState,
            RenderDefinitionExtension renderDefinitionExtension,
            PropertiesDefinitionExtension propertiesDefinitionExtension
    ) {
        Recasting.LOGGER.info(
                "[P2] ProbeSlashArts.trigger user={} color={}",
                livingEntity.getName().getString(),
                Integer.toHexString(slashBladeState.getColorCode())
        );
        AttackHelper.doSlash(
                livingEntity,
                135f,
                slashBladeState.getColorCode(),
                Vec3.ZERO,
                false,
                true,
                new DamageStructure(0.3f, 0),
                propertiesDefinitionExtension.attackDistance(),
                KnockBacks.cancel
        );
    }
}

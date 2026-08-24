package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.SlashEffectEntity;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.util.DamageStructure;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * 碎段 Slash Arts
 * 发动一次斩击，设置重复攻击和取消击退
 */
@Setter
@Accessors(chain = true)
public class FragmentSlashArts extends ExtendedSlashArts {

    float attack = 0.3f;
    int lifeTicks = 10;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {


        // 调用 AttackHelper.doSlash
        SlashEffectEntity slashEffectEntity = AttackHelper.doSlash(
                livingEntity,
                135f,  // roll
                slashBladeState.getColorCode(),
                Vec3.ZERO,
                false,  // mute
                true,   // critical
                new DamageStructure(attack, 0),
                propertiesDefinitionExtension.attackDistance(),
                KnockBacks.cancel
        );

        if (slashEffectEntity == null) {
            return;
        }

        slashEffectEntity.setMaxLifeTime(lifeTicks);
        slashEffectEntity.setRepeatedAttack(true);
    }
}

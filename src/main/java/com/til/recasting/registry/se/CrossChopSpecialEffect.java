package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.entity.SlashEffectEntity;
import com.til.recasting.event.DoSlashExtendEvent;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.util.NumberPack;
import mods.flammpfeil.slashblade.util.VectorHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/***
 * 十字斩
 * 挥刀时追加一道剑气
 */
public class CrossChopSpecialEffect extends ExtendedSpecialEffect {

    NumberPack attackRatio = new NumberPack(0f, 0.1f);

    @SubscribeEvent
    public void onEvent(DoSlashExtendEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }
        if (event.getUser().level().isClientSide()) {
            return;
        }

        PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getBlade());
        int level = getLevel(properties);

        // 计算位置（参考 AttackHelper.doSlash）
        Vec3 pos = event.getUser().position().add(0.0D, (double) event.getUser().getEyeHeight() * 0.75D, 0.0D)
                .add(event.getUser().getLookAngle().scale(0.3f));

        pos = pos.add(VectorHelper.getVectorForRotation(-90.0F, event.getUser().getViewYRot(0)).scale(event.getCenterOffset().y))
                .add(VectorHelper.getVectorForRotation(0, event.getUser().getViewYRot(0) + 90).scale(event.getCenterOffset().z))
                .add(event.getUser().getLookAngle().scale(event.getCenterOffset().z));

        // 创建十字斩剑气
        SlashEffectEntity crossSlash = new SlashEffectEntity(
                RecastingEntities.SLASH_EFFECT.get(),
                event.getUser().level(),
                event.getUser()
        );

        crossSlash.setPos(pos.x, pos.y, pos.z);
        crossSlash.setRoll(event.getRoll() + 90); // 旋转90度形成十字
        crossSlash.setYRot(event.getUser().getYRot());
        crossSlash.setXRot(0);
        crossSlash.setColor(event.getSlashBladeState().getColorCode());
        crossSlash.setMute(event.isMute());
        crossSlash.setCritical(event.isCritical());
        crossSlash.setModifiedRatio(event.getModifiedRatio() * attackRatio.of(level));
        //noinspection deprecation
        crossSlash.setDamage((float) (event.getDamage() * attackRatio.of(level)));
        crossSlash.setSize(event.getAttackRange());

        // 设置击退（如果有）
        if (event.getKnockback() != null) {
            crossSlash.attackActionCallbackPoint.register(event.getKnockback().action::accept);
        }

        event.getUser().level().addFreshEntity(crossSlash);
    }

}

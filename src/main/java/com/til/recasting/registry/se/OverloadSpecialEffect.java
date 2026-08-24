package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.event.DoSlashExtendEvent;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.util.NumberPack;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;

/***
 * 过载
 * 挥刀时小概率触发审判
 */
public class OverloadSpecialEffect extends ExtendedSpecialEffect {

    NumberPack probability = new NumberPack(0f, 0.03f);

    @SubscribeEvent
    public void onEvent(DoSlashExtendEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        // 只在服务端执行
        if (event.getUser().level().isClientSide()) {
            return;
        }

        PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getBlade());
        int level = getLevel(properties);

        // 概率检查
        if (event.getUser().getRandom().nextFloat() >= probability.of(level)) {
            return;
        }

        // 触发次元斩
        Level worldIn = event.getUser().level();
        ISlashBladeState state = event.getSlashBladeState();

        // 获取攻击目标位置
        Vec3 pos = PosHelper.getAttackTargetPosition(event.getUser(), state);

        // 创建次元斩
        JudgementCutEntity jc = new JudgementCutEntity(
                RecastingEntities.JUDGEMENT_CUT.get(),
                worldIn,
                event.getUser()
        );

        jc.setPos(pos.x, pos.y, pos.z);

        // 设置颜色
        jc.setColor(state.getColorCode());

        // 添加到世界
        worldIn.addFreshEntity(jc);

        // 播放音效
        worldIn.playSound(null, jc.getX(), jc.getY(), jc.getZ(),
                net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT,
                net.minecraft.sounds.SoundSource.PLAYERS, 0.5F,
                0.8F / (event.getUser().getRandom().nextFloat() * 0.4F + 0.8F));
    }

}

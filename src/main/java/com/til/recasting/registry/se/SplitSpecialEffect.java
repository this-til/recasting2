package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.event.DoSlashExtendEvent;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.util.NumberPack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/***
 * 分裂
 * 挥刀时发射幻影剑进行辅助攻击
 */
public class SplitSpecialEffect extends ExtendedSpecialEffect {

    NumberPack attackRatio = new NumberPack(0.2f, 0.15f);

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

        // 获取攻击目标位置
        Vec3 attackPos = PosHelper.getAttackTargetPosition(event.getUser(), event.getSlashBladeState());

        // 生成幻影剑数量
        float attack = attackRatio.of(level);

        SummondSwordEntity summondSword = new SummondSwordEntity(
                RecastingEntities.SUMMOND_SWORD.get(),
                event.getUser().level(),
                event.getUser()
        );

        // 朝向攻击目标
        summondSword.lookAt(attackPos, false);

        // 设置属性
        summondSword.setColor(event.getSlashBladeState().getColorCode());
        summondSword.setModifiedRatio(attack);
        summondSword.setStartDelay(event.getUser().getRandom().nextInt(5));

        // 添加到世界
        event.getUser().level().addFreshEntity(summondSword);
    }

}

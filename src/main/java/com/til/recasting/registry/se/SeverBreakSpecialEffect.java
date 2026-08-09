package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.entity.SlashEffectEntity;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.util.NumberPack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/***
 * 断却
 * 触发次元斩之后造成一次大伤害和大范围的劈砍
 */
public class SeverBreakSpecialEffect extends ExtendedSpecialEffect {

    NumberPack attackRatio = new NumberPack(0.1f, 0.15f);
    NumberPack rangeRatio = new NumberPack(1f, 0.1f);

    @SubscribeEvent
    public void onEvent(EntityJoinLevelEvent event) {
        JudgementCutContext context = resolveJudgementCutContext(event);
        if (context == null) {
            return;
        }
        JudgementCutEntity jc = context.judgementCut();
        var shooter = context.shooter();
        var blade = context.blade();
        var state = context.state();
        int level = context.effectLevel();

        // 获取次元斩位置
        Vec3 attackPos = jc.position();

        // 获取攻击距离
        float attackDistance = blade.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                .map(PropertiesDefinitionExtension::attackDistance)
                .orElse(1.0f);

        // 计算期望长度
        double desiredLength = 4 * attackDistance * rangeRatio.of(level);

        // 生成随机偏移
        double x = (shooter.getRandom().nextDouble() * 2 - 1) * desiredLength;
        double y = (shooter.getRandom().nextDouble() * 2 - 1) * desiredLength;
        double z = (shooter.getRandom().nextDouble() * 2 - 1) * desiredLength;

        // 计算新位置
        Vec3 pos = attackPos.add(x, y, z);

        // 创建斩击特效
        SlashEffectEntity slashEffect = new SlashEffectEntity(
                RecastingEntities.SLASH_EFFECT.get(),
                event.getLevel(),
                shooter
        );

        slashEffect.setPos(pos.x, pos.y, pos.z);
        slashEffect.setRoll(shooter.getRandom().nextInt(360));
        slashEffect.lookAt(attackPos, false);
        slashEffect.setColor(state.getColorCode());
        slashEffect.setMute(false);
        slashEffect.setModifiedRatio(attackRatio.of(level));
        slashEffect.setSize((float) (desiredLength / 4));

        // 添加到世界
        event.getLevel().addFreshEntity(slashEffect);
    }

}

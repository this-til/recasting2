package com.til.recasting.registry.se;

import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.util.NumberPack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/***
 * 断灭
 * 召唤一定数量的次元斩之后额外召唤一个巨型次元斩
 */
public class AnnihilationSpecialEffect extends ExtendedSpecialEffect {

    NumberPack attackRatio = new NumberPack(0.01f, 0.01f); // 攻击倍率
    int addLevel = 1; // 每次叠加的层数
    int giantLifetime = 40; // 巨型次元斩的生命时间
    float giantSize = 6.0f; // 巨型次元斩的大小倍率

    @SubscribeEvent
    public void onEvent(EntityJoinLevelEvent event) {
        JudgementCutContext context = resolveJudgementCutContext(event);
        if (context == null) {
            return;
        }
        JudgementCutEntity jc = context.judgementCut();
        var shooter = context.shooter();
        var state = context.state();
        int level = context.effectLevel();

        // 使用Buff系统跟踪次元斩计数
        shooter.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                buffStackData -> {
                    Level world = shooter.level();

                    // 获取当前层数
                    int currentLevel = buffStackData.getLevel(RecastingBuffTypes.ANNIHILATION.get(), world);

                    // 增加层数
                    int newLevel = currentLevel + addLevel;
                    buffStackData.setLevel(RecastingBuffTypes.ANNIHILATION.get(), newLevel, world);

                    // 检查是否达到最大层数（6层）
                    if (newLevel >= RecastingBuffTypes.ANNIHILATION.get().getMaxLevel()) {
                        // 重置层数
                        buffStackData.setLevel(RecastingBuffTypes.ANNIHILATION.get(), 0, world);

                        // 创建巨型次元斩
                        Level worldIn = shooter.level();
                        Vec3 pos = jc.position();

                        JudgementCutEntity giantJc = new JudgementCutEntity(
                                RecastingEntities.JUDGEMENT_CUT.get(),
                                worldIn,
                                shooter
                        );

                        giantJc.setPos(pos.x, pos.y, pos.z);
                        giantJc.setColor(state.getColorCode());
                        giantJc.setModifiedRatio(attackRatio.of(level));
                        giantJc.setMaxLifeTime(giantLifetime);
                        giantJc.setSize(giantSize);

                        // 添加到世界
                        worldIn.addFreshEntity(giantJc);
                    }
                }
        );
    }

}

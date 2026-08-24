package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.util.NumberPack;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/***
 * 断罪
 * 触发SA时追加次元斩攻击
 */
public class JudgementSpecialEffect extends ExtendedSpecialEffect {

    NumberPack attackRatio = new NumberPack(0.2f, 0.1f);
    int delayTicks = 5;

    @SubscribeEvent
    public void onEvent(SlashBladeEvent.PerformSlashArtEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        // 只在服务端执行
        if (event.getEntityLiving().level().isClientSide()) {
            return;
        }

        // 仅在 SA 实际进入有效连段时追加效果，避免依赖不同 SlashBlade 版本里的 type 访问器。
        if (event.getComboState() == null || ComboStateRegistry.NONE.getId().equals(event.getComboState())) {
            return;
        }

        LivingEntity user = event.getEntityLiving();
        ISlashBladeState state = event.getSlashBladeState();
        ItemStack blade = user.getMainHandItem();

        PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(blade);
        int level = getLevel(properties);

        // 延迟执行，确保SA已经触发
        user.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(
                timeRun -> timeRun.addTimerCell(
                        () -> {
                            Level worldIn = user.level();
                            if (worldIn.isClientSide()) {
                                return;
                            }

                            // 获取攻击目标位置
                            Vec3 pos = PosHelper.getAttackTargetPosition(user, state);

                            // 创建次元斩
                            JudgementCutEntity jc = new JudgementCutEntity(
                                    RecastingEntities.JUDGEMENT_CUT.get(),
                                    worldIn,
                                    user
                            );

                            jc.setPos(pos.x, pos.y, pos.z);

                            // 设置颜色
                            jc.setColor(state.getColorCode());

                            // 设置伤害倍率
                            jc.setModifiedRatio(attackRatio.of(level));

                            // 添加到世界
                            worldIn.addFreshEntity(jc);
                        },
                        delayTicks
                )
        );
    }

}

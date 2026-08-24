package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.entity.LightningEntity;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.util.NumberPack;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;

/***
 * 雷暴
 * 触发SA时，在目标位置召唤闪电
 */
public class ThunderstormSpecialEffect extends ExtendedSpecialEffect {

    NumberPack attackRatio = new NumberPack(0.1f, 0.1f);
    NumberPack lightningCount = new NumberPack(1f, 0.4f);
    NumberPack spreadRange = new NumberPack(3f, 0f); // 扩散范围
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
        RecastingAttachments.timeRun(user).addTimerCell(
                () -> {
                    Level worldIn = user.level();
                    if (worldIn.isClientSide()) {
                        return;
                    }

                    // 获取攻击目标位置
                    Vec3 centerPos = PosHelper.getAttackTargetPosition(user, state);

                    // 计算闪电数量和扩散范围
                    int count = (int) lightningCount.of(level);
                    float attack = attackRatio.of(level);
                    float range = spreadRange.of(level);

                    // 生成多道闪电
                    for(int i = 0; i < count; i++) {
                        // 在圆形范围内生成随机偏移（只在 x-z 平面，不修改 y 轴）
                        double angle = user.getRandom().nextDouble() * 2.0 * Math.PI;
                        double radius = user.getRandom().nextDouble() * range;
                        Vec3 randomOffset = new Vec3(
                                radius * Math.cos(angle),
                                0, // y 轴保持为 0
                                radius * Math.sin(angle)
                        );
                        Vec3 lightningPos = centerPos.add(randomOffset);

                        // 创建闪电实体
                        LightningEntity lightning = new LightningEntity(
                                RecastingEntities.LIGHTNING.get(),
                                worldIn,
                                user
                        );

                        lightning.setPos(lightningPos.x, lightningPos.y, lightningPos.z);
                        lightning.setModifiedRatio(attack);
                        lightning.setMaxLifeTime(20);

                        // 添加到世界
                        worldIn.addFreshEntity(lightning);
                    }

                    // 播放音效
                    worldIn.playSound(null, centerPos.x, centerPos.y, centerPos.z,
                            SoundEvents.LIGHTNING_BOLT_THUNDER,
                            net.minecraft.sounds.SoundSource.WEATHER, 1.0F,
                            0.8F + user.getRandom().nextFloat() * 0.2F);
                },
                delayTicks
        );
    }

}

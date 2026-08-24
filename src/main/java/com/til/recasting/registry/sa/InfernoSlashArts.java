package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.handler.BuffSourceHelper;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 业火 Slash Arts
 * 召唤 size 为 6 的红色次元斩，无重复攻击，为攻击目标附加灵魂燃烧
 */
@Setter
@Accessors(chain = true)
public class InfernoSlashArts extends ExtendedSlashArts {

    int soulBurnLevel = 4;  // 默认附加4层灵魂燃烧

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {

        Level worldIn = livingEntity.level();

        // 获取攻击目标位置
        Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

        // 创建次元斩
        JudgementCutEntity jc = new JudgementCutEntity(
                RecastingEntities.JUDGEMENT_CUT.get(),
                worldIn,
                livingEntity
        );

        // 设置位置
        jc.setPos(attackPos.x, attackPos.y, attackPos.z);

        // 设置颜色为红色
        jc.setColor(0xFF0000);

        // 设置大小为 6
        jc.setSize(6.0f);

        // 设置无重复攻击
        jc.setRepeatedAttack(false);

        // 添加攻击回调：为攻击目标附加灵魂燃烧
        int finalSoulBurnLevel = soulBurnLevel;
        jc.attackActionCallbackPoint.register(hitEntity -> {
            IBuffStackData buffStackData = RecastingAttachments.buffStackData(hitEntity);
            Level world = hitEntity.level();
            int currentLevel = buffStackData.getLevel(RecastingBuffTypes.SOUL_BURN.get(), world);
            int newLevel = currentLevel + finalSoulBurnLevel;
            buffStackData.setLevel(RecastingBuffTypes.SOUL_BURN.get(), newLevel, world);
            BuffSourceHelper.recordSourceEntity(buffStackData, RecastingBuffTypes.SOUL_BURN.get(), hitEntity, livingEntity);
            RecastingBuffTypes.SOUL_BURN.get().ensureTimer(hitEntity);
        });

        // 添加到世界
        worldIn.addFreshEntity(jc);

        // 播放音效
        worldIn.playSound(null, jc.getX(), jc.getY(), jc.getZ(),
                SoundEvents.ENDERMAN_TELEPORT,
                net.minecraft.sounds.SoundSource.PLAYERS, 0.5F,
                0.8F / (livingEntity.getRandom().nextFloat() * 0.4F + 0.8F));
    }
}

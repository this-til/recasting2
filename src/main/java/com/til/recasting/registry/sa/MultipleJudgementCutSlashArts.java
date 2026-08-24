package com.til.recasting.registry.sa;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.RecastingEntities;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 多重次元斩·决 Slash Arts
 * 在同一位置连续发动多次次元斩，每次都在前一次的位置
 */
@Setter
@Accessors(chain = true)
public class MultipleJudgementCutSlashArts extends ExtendedSlashArts {

    int attackNumber = 4;
    float hit = 0.3f;
    int delayTicks = 4;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {
        AtomicReference<Vec3> lastPos = new AtomicReference<>(null);
        ITimeRun timeRun = RecastingAttachments.timeRun(livingEntity);

        for(int i = 0; i < attackNumber; i++) {
            int delay = delayTicks * i;

            timeRun.addTimerCell(
                    () -> {
                        Level worldIn = livingEntity.level();

                        Vec3 pos;
                        if (lastPos.get() != null) {
                            pos = lastPos.get();
                        } else {
                            pos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);
                        }

                        JudgementCutEntity jc = new JudgementCutEntity(
                                RecastingEntities.JUDGEMENT_CUT.get(),
                                worldIn,
                                livingEntity
                        );

                        jc.setPos(pos.x, pos.y, pos.z);
                        jc.setColor(slashBladeState.getColorCode());
                        jc.setModifiedRatio(hit);

                        worldIn.addFreshEntity(jc);
                        lastPos.set(pos);

                        worldIn.playSound(null, jc.getX(), jc.getY(), jc.getZ(),
                                SoundEvents.ENDERMAN_TELEPORT,
                                net.minecraft.sounds.SoundSource.PLAYERS, 0.5F,
                                0.8F / (livingEntity.getRandom().nextFloat() * 0.4F + 0.8F));
                    },
                    delay
            );
        }
    }
}

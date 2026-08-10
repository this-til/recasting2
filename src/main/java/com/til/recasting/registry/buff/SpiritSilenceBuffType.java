package com.til.recasting.registry.buff;

import com.til.recasting.capability.ITimeRun;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.instance.BuffType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import java.util.UUID;

/**
 * 寂灭：叠层写入 ARMOR 修饰器；每秒减 1 层并刷新；0 层移除。
 */
@Getter
@Setter
@Accessors(chain = true)
public class SpiritSilenceBuffType extends BuffType {

    private static final UUID SILENCE_ARMOR_UUID = UUID.fromString("a3f8c2e1-5b4d-4e9a-8c7f-1d2e3f4a5b6c");
    private static final String TIMER = "spirit_silence_decay";

    int decayTicks = 20;
    double armorPenaltyPerStack = 0.01d;

    public SpiritSilenceBuffType() {
        decayInterval = 0;
        maxLevel = 66;
    }

    public void onStacksChanged(LivingEntity target) {
        if (target.level().isClientSide()) {
            return;
        }
        int stacks = target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA)
                .map(data -> data.getLevel(this, target.level()))
                .orElse(0);
        refreshArmorModifier(target, stacks);
        if (stacks > 0) {
            ensureDecayTimer(target);
        }
    }

    private void refreshArmorModifier(LivingEntity target, int stacks) {
        AttributeInstance armor = target.getAttribute(Attributes.ARMOR);
        if (armor == null) {
            return;
        }
        armor.removeModifier(SILENCE_ARMOR_UUID);
        if (stacks <= 0) {
            return;
        }
        armor.addTransientModifier(new AttributeModifier(
                SILENCE_ARMOR_UUID,
                "spirit_silence_armor",
                -armorPenaltyPerStack * stacks,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        ));
    }

    private void ensureDecayTimer(LivingEntity target) {
        target.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            if (timeRun.getNamedTimerCell(TIMER) != null) {
                return;
            }
            timeRun.addNamedTimerCell(
                    TIMER,
                    new ITimeRun.TimerCell(
                            () -> tickDecay(target, timeRun),
                            decayTicks,
                            true
                    )
            );
        });
    }

    private void tickDecay(LivingEntity target, ITimeRun timeRun) {
        if (!target.isAlive() || target.level().isClientSide()) {
            refreshArmorModifier(target, 0);
            timeRun.removeNamedTimerCell(TIMER);
            return;
        }

        Level world = target.level();
        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            int stacks = data.getLevel(this, world);
            if (stacks <= 0) {
                refreshArmorModifier(target, 0);
                timeRun.removeNamedTimerCell(TIMER);
                return;
            }
            data.setLevel(this, stacks - 1, world);
            int next = data.getLevel(this, world);
            refreshArmorModifier(target, next);
            if (next <= 0) {
                timeRun.removeNamedTimerCell(TIMER);
            }
        });
    }
}

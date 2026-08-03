package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.capability.ITimeRun;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

/**
 * 寂灭：叠层时写入 ARMOR 修饰器；每秒减 1 层并刷新；0 层移除。
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SpiritSilenceBuffHandler {

    public static final UUID SILENCE_ARMOR_UUID = UUID.fromString("a3f8c2e1-5b4d-4e9a-8c7f-1d2e3f4a5b6c");
    private static final String TIMER = "spirit_silence_decay";
    private static final int DECAY_INTERVAL = 20;

    private SpiritSilenceBuffHandler() {
    }

    public static void onStacksChanged(LivingEntity target) {
        if (target.level().isClientSide()) {
            return;
        }
        BuffType buffType = RecastingBuffTypes.SPIRIT_SILENCE.get();
        int stacks = target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA)
                .map(data -> data.getLevel(buffType, target.level()))
                .orElse(0);
        refreshArmorModifier(target, stacks);
        if (stacks > 0) {
            ensureDecayTimer(target);
        }
    }

    public static void refreshArmorModifier(LivingEntity target, int stacks) {
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
                -0.01d * stacks,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        ));
    }

    private static void ensureDecayTimer(LivingEntity target) {
        target.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            if (timeRun.getNamedTimerCell(TIMER) != null) {
                return;
            }
            BuffType buffType = RecastingBuffTypes.SPIRIT_SILENCE.get();
            timeRun.addNamedTimerCell(
                    TIMER,
                    new ITimeRun.TimerCell(
                            () -> tickDecay(target, buffType, timeRun),
                            DECAY_INTERVAL,
                            true
                    )
            );
        });
    }

    private static void tickDecay(LivingEntity target, BuffType buffType, ITimeRun timeRun) {
        if (!target.isAlive() || target.level().isClientSide()) {
            refreshArmorModifier(target, 0);
            timeRun.removeNamedTimerCell(TIMER);
            return;
        }

        Level world = target.level();
        target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            int stacks = data.getLevel(buffType, world);
            if (stacks <= 0) {
                refreshArmorModifier(target, 0);
                timeRun.removeNamedTimerCell(TIMER);
                return;
            }
            data.setLevel(buffType, stacks - 1, world);
            int next = data.getLevel(buffType, world);
            refreshArmorModifier(target, next);
            if (next <= 0) {
                timeRun.removeNamedTimerCell(TIMER);
            }
        });
    }
}

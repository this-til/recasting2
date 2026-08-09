package com.til.recasting.registry.se;

import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.SummonedSwordHelper;
import com.til.recasting.registry.SpecialEffectsRegistry;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * 指令映射
 * 触发幻影剑剑技后，按间隔再次发动同名剑技；额外次数由变体决定。
 */
@Getter
@Setter
@Accessors(chain = true)
public class CommandMappingSpecialEffect extends ExtendedSpecialEffect {

    private int extraTriggers = 2;

    private int delayTicks = 20;

    /**
     * 玩家主动触发幻影剑剑技成功后调用；SE 重放路径不会再次进入此处。
     */
    public static void trySchedule(LivingEntity user, SummonedSwordHelper.ArtType artType) {
        if (!(user instanceof ServerPlayer player)) {
            return;
        }
        Level level = player.level();
        if (level.isClientSide()) {
            return;
        }

        CommandMappingSpecialEffect effect = resolveEffect(player);
        if (effect == null) {
            return;
        }
        effect.schedule(player, artType);
    }

    @Nullable
    private static CommandMappingSpecialEffect resolveEffect(LivingEntity user) {
        ItemStack blade = user.getMainHandItem();
        if (blade.isEmpty() || !(blade.getItem() instanceof ItemSlashBlade)) {
            return null;
        }
        ISlashBladeState state = blade.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
        if (state == null) {
            return null;
        }

        if (SpecialEffectsRegistry.COMMAND_MAPPING_LAMBDA.get() instanceof CommandMappingSpecialEffect lambda
                && lambda.hasSpecialEffect(state)) {
            return lambda;
        }
        if (SpecialEffectsRegistry.COMMAND_MAPPING.get() instanceof CommandMappingSpecialEffect normal
                && normal.hasSpecialEffect(state)) {
            return normal;
        }
        return null;
    }

    private void schedule(ServerPlayer player, SummonedSwordHelper.ArtType artType) {
        if (extraTriggers <= 0 || delayTicks <= 0) {
            return;
        }
        player.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            for(int i = 1; i <= extraTriggers; i++) {
                int delay = delayTicks * i;
                timeRun.addTimerCell(() -> recast(player, artType), delay);
            }
        });
    }

    private void recast(ServerPlayer player, SummonedSwordHelper.ArtType artType) {
        if (!player.isAlive()) {
            return;
        }
        if (player.level().isClientSide()) {
            return;
        }

        ItemStack blade = player.getMainHandItem();
        if (blade.isEmpty() || !(blade.getItem() instanceof ItemSlashBlade)) {
            return;
        }
        ISlashBladeState state = blade.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
        if (state == null || !hasSpecialEffect(state)) {
            return;
        }

        SummonedSwordHelper.replayArt(player, artType);
    }
}

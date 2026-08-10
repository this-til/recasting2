package com.til.recasting.registry.se;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingBuffTypes;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 宝具连发
 * 释放 SA 后延迟再次进入同一个 SA 的 ComboState。
 */
public class TreasureBarrageSpecialEffect extends ExtendedSpecialEffect {

    private final int recastDelayTicks = 20;

    private int cooldownTicks = 60;

    @SubscribeEvent
    public void onEvent(SlashBladeEvent.PerformSlashArtEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        LivingEntity user = event.getEntityLiving();
        Level level = user.level();
        if (level.isClientSide()) {
            return;
        }

        ResourceLocation comboState = event.getComboState();
        if (comboState == null || ComboStateRegistry.NONE.getId().equals(comboState)) {
            return;
        }

        SlashArts.ArtsType artsType = event.getType();
        if (artsType == null || artsType == SlashArts.ArtsType.Fail) {
            return;
        }

        IBuffStackData buffStackData = getBuffStackData(user);
        if (buffStackData == null) {
            return;
        }

        if (buffStackData.getLevel(RecastingBuffTypes.TREASURE_BARRAGE_COOLDOWN.get(), level) > 0) {
            return;
        }

        buffStackData.setLevel(RecastingBuffTypes.TREASURE_BARRAGE_COOLDOWN.get(), cooldownTicks, level);

        user.getCapability(CapabilityRegistryHandler.TIME_RUN).ifPresent(timeRun -> {
            timeRun.addTimerCell(() -> recast(user, artsType), recastDelayTicks);
        });
    }

    private void recast(LivingEntity user, SlashArts.ArtsType artsType) {
        if (!user.isAlive()) {
            return;
        }

        Level level = user.level();
        if (level.isClientSide()) {
            return;
        }

        ItemStack blade = user.getMainHandItem();
        if (blade.isEmpty()) {
            return;
        }

        //noinspection DataFlowIssue
        ISlashBladeState state = blade.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
        if (state == null || !hasSpecialEffect(state)) {
            return;
        }

        ResourceLocation comboState = state.getSlashArts().doArts(artsType, user);
        if (comboState == null || ComboStateRegistry.NONE.getId().equals(comboState)) {
            return;
        }

        state.updateComboSeq(user, comboState);
    }

    public TreasureBarrageSpecialEffect setCooldownTicks(int cooldownTicks) {
        this.cooldownTicks = cooldownTicks;
        return this;
    }
}

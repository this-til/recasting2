package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.FeEnergyHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.SlashArtsRegistry;
import com.til.recasting.util.DamageStructure;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 聚焦能量刃：造成伤害时消耗 FE 追加雷电伤害并叠电涌；满层尝试耗 FE 释放异界斩切。
 */
@Getter
@Setter
@Accessors(chain = true)
public class FocusedEnergyBladeSpecialEffect extends ExtendedSpecialEffect {

    private long lightningCost = 10_000L;
    private float lightningRatio = 0.12f;
    private int surgeTriggerStacks = 64;
    private long slashCost = 1_000_000L;
    private int slashCooldownTicks = 20;

    @SubscribeEvent
    public void onAttackAmplifier(AttackAmplifierEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        LivingEntity attacker = event.getAttacker();
        Level level = attacker.level();
        if (level.isClientSide()) {
            return;
        }

        if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
            return;
        }

        if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
            return;
        }

        ItemStack blade = event.getItem();
        if (!FeEnergyHelper.tryExtract(blade, lightningCost, true)) {
            return;
        }

        AttackAmplifierEvent.DamageSourceInfo lightningInfo =
                RecastingAttackTypes.LIGHTNING_ATTACK.get().createDamageSource(attacker, target);
        if (lightningInfo == null) {
            return;
        }

        if (!FeEnergyHelper.tryExtract(blade, lightningCost, false)) {
            return;
        }

        event.addDamageSourceInfo(
                lightningInfo.damageSource(),
                new DamageStructure(lightningRatio, 0.0f)
        );

        attacker.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(buffStackData -> {
            int current = buffStackData.getLevel(RecastingBuffTypes.ELECTRIC_SURGE.get(), level);
            int next = Math.min(surgeTriggerStacks, current + 1);
            buffStackData.setLevel(RecastingBuffTypes.ELECTRIC_SURGE.get(), next, level);

            if (next < surgeTriggerStacks) {
                return;
            }
            if (buffStackData.getLevel(RecastingBuffTypes.OTHERWORLD_SLASH_CD.get(), level) > 0) {
                return;
            }
            if (!FeEnergyHelper.tryExtract(blade, slashCost, false)) {
                return;
            }

            buffStackData.setLevel(RecastingBuffTypes.ELECTRIC_SURGE.get(), 0, level);
            buffStackData.setLevel(RecastingBuffTypes.OTHERWORLD_SLASH_CD.get(), slashCooldownTicks, level);
            triggerOtherworldSlash(attacker, blade, event.getSlashBladeState());
        });
    }

    private void triggerOtherworldSlash(LivingEntity attacker, ItemStack blade, ISlashBladeState state) {
        PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(blade);
        RenderDefinitionExtension render = getRenderDefinitionExtension(blade);
        SlashArtsRegistry.OTHERWORLD_SLASH.get().trigger(attacker, blade, state, render, properties);
    }
}

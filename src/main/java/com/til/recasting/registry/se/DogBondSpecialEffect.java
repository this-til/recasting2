package com.til.recasting.registry.se;

import com.til.recasting.handler.EntityPredicateHelper;
import com.til.recasting.handler.InventorySlashBladeSeHelper;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.SlashArtsRegistry;
import com.til.recasting.registry.SpecialEffectsRegistry;
import com.til.recasting.registry.sa.DogBiteSlashArts;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/**
 * 犬缘
 * 背包触发：受伤时获得伤害吸收，并对可攻击的实体来源释放犬咬，消耗耐久；内置冷却。
 */
@Setter
@Accessors(chain = true)
public class DogBondSpecialEffect extends ExtendedSpecialEffect {

    private int absorptionDurationTicks = 50;
    private int absorptionAmplifier = 1;
    private int durabilityCost = 40;
    private int cooldownTicks = 40;

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity victim = event.getEntity();
        Level level = victim.level();
        if (level.isClientSide()) {
            return;
        }
        if ((float) event.getNewDamage() <= 0f) {
            return;
        }

        InventorySlashBladeSeHelper.BladeSeHit hit = InventorySlashBladeSeHelper.findFirstInInventory(
                victim,
                SpecialEffectsRegistry.DOG_BOND
        );
        if (hit == null) {
            return;
        }

        var buffData = getBuffStackData(victim);
        if (buffData == null) {
            return;
        }
        if (buffData.getLevel(RecastingBuffTypes.DOG_BOND_COOLDOWN.get(), level) > 0) {
            return;
        }

        buffData.setLevel(RecastingBuffTypes.DOG_BOND_COOLDOWN.get(), cooldownTicks, level);
        victim.addEffect(new MobEffectInstance(
                MobEffects.ABSORPTION,
                absorptionDurationTicks,
                absorptionAmplifier
        ));

        Entity sourceEntity = event.getSource().getEntity();
        if (!(sourceEntity instanceof LivingEntity attacker)) {
            return;
        }
        if (!EntityPredicateHelper.canTarget(victim, attacker)) {
            return;
        }

        ISlashBladeState state = hit.state();
        int maxDamage = state.getMaxDamage();
        if (maxDamage <= 0) {
            return;
        }
        int currentDamage = state.getDamage();
        int remaining = maxDamage - currentDamage;
        if (remaining < durabilityCost) {
            return;
        }

        state.setDamage(currentDamage + durabilityCost);

        ItemStack blade = hit.blade();
        if (SlashArtsRegistry.DOG_BITE.get() instanceof DogBiteSlashArts dogBite) {
            dogBite.perform(victim, blade, attacker, true);
        }
    }
}

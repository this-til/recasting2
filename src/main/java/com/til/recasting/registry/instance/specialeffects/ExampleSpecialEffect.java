package com.til.recasting.registry.instance.specialeffects;

import com.til.recasting.registry.SpecialEffectsRegistry;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.til.recasting.Recasting.MODID;

/**
 * Special Effect (SE) 示例实现
 * <p>
 * 这个示例展示了如何创建一个 SE：
 * 1. 继承 SpecialEffect 类
 * 2. 在构造函数中设置 requestLevel、isCopiable、isRemovable
 * 3. 使用 @EventBusSubscriber 和 @SubscribeEvent 订阅事件来实现效果
 * <p>
 * 可用的事件：
 * - SlashBladeEvent.UpdateEvent: 每 tick 更新时触发
 * - SlashBladeEvent.HitEvent: 击中目标时触发
 * - SlashBladeEvent.DoSlashEvent: 执行斩击时触发
 * - 等等...
 */
@Mod.EventBusSubscriber(modid = MODID)
public class ExampleSpecialEffect extends SpecialEffect {

    public ExampleSpecialEffect(int requestLevel) {
        super(requestLevel);
    }

    /**
     * 示例：在每 tick 更新时检查并应用效果
     * 这个示例会在玩家等级不足时给予负面效果
     */
    @SubscribeEvent
    public static void onSlashBladeUpdate(SlashBladeEvent.UpdateEvent event) {
        ISlashBladeState state = event.getSlashBladeState();

        // 检查刀是否拥有此 SE
        if (!state.hasSpecialEffect(SpecialEffectsRegistry.EXAMPLE_SPECIAL_EFFECT.getId())) {
            return;
        }

        // 只处理玩家
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // 只处理当前选中的刀
        if (!event.isSelected()) {
            return;
        }

        int level = player.experienceLevel;

        // 如果玩家等级不足，给予负面效果
        if (!SpecialEffect.isEffective(SpecialEffectsRegistry.EXAMPLE_SPECIAL_EFFECT.get(), level)) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
        }
    }

    /**
     * 示例：在击中目标时应用效果
     * 这个示例会在击中时给予目标负面效果
     */
    @SubscribeEvent
    public static void onSlashBladeHit(SlashBladeEvent.HitEvent event) {
        ISlashBladeState state = event.getSlashBladeState();

        // 检查刀是否拥有此 SE
        if (!state.hasSpecialEffect(SpecialEffectsRegistry.EXAMPLE_SPECIAL_EFFECT.getId())) {
            return;
        }

        // 只处理玩家
        if (!(event.getUser() instanceof Player player)) {
            return;
        }

        int level = player.experienceLevel;

        // 如果玩家等级足够，给予目标负面效果
        if (SpecialEffect.isEffective(SpecialEffectsRegistry.EXAMPLE_SPECIAL_EFFECT.get(), level)) {
            event.getTarget().addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
        }
    }
}


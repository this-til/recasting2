package com.til.recasting.registry.se;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.entity.SummondSpiralSwordEntity;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.util.NumberPack;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;

/***
 * 回旋
 * 幻影剑造成伤害后叠加剑势，达到一定层数后触发风暴幻影剑
 */
public class SpiralSpecialEffect extends ExtendedSpecialEffect {

    NumberPack modifiedRatio = new NumberPack(0.1f, 0);
    NumberPack count = new NumberPack(4, 1);
    int addLevel = 1; // 每次叠加的层数
    NumberPack triggerInterval = new NumberPack(40, 0); // 触发间隔（tick）

    @SubscribeEvent
    public void onAttackEvent(AttackAmplifierEvent event) {
        LivingEntity target = resolveServerLivingTarget(event);
        if (target == null) {
            return;
        }

        // 只处理幻影剑攻击
        if (!event.getAttackTypeList().contains(RecastingAttackTypes.SUMMOND_SWORD_ATTACK.get())) {
            return;
        }

        if (event.getAttackTypeList().contains(RecastingAttackTypes.SPIRAL_SWORD_ATTACK.get())) {
            return;
        }

        if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
            return;
        }

        LivingEntity attacker = event.getAttacker();
        ItemStack blade = event.getItem();

        PropertiesDefinitionExtension se = AttackHelper.propertiesOf(blade);
        if (se == null) {
            return;
        }

        IBuffStackData buffStackData = RecastingAttachments.buffStackData(target);

        Level world = target.level();

        // 获取当前层数
        int currentLevel = buffStackData.getLevel(RecastingBuffTypes.SWORD_MOMENTUM.get(), world);
        int interval = (int) triggerInterval.of(getLevel(se));
        int cooldown = buffStackData.getLevel(RecastingBuffTypes.SPIRAL_COOLDOWN.get(), world);

        // 如果触发间隔还没到，不触发风暴效果，也不叠加层数
        if (cooldown > 0) {
            return;
        }

        if (currentLevel < RecastingBuffTypes.SWORD_MOMENTUM.get().getMaxLevel()) {
            int newLevel = currentLevel + addLevel;
            buffStackData.setLevel(RecastingBuffTypes.SWORD_MOMENTUM.get(), newLevel, world);
            return;
        }

        // 触发间隔到了，触发风暴效果并重置层数
        buffStackData.setLevel(RecastingBuffTypes.SWORD_MOMENTUM.get(), 0, world);
        if (interval > 0) {
            buffStackData.setLevel(RecastingBuffTypes.SPIRAL_COOLDOWN.get(), interval, world);
        }

        // 触发风暴效果
        performStormSwordsInternal(
                attacker,
                target,
                event.getSlashBladeState(),
                se
        );

    }

    public void performStormSwordsInternal(LivingEntity entity, LivingEntity target, ISlashBladeState state, PropertiesDefinitionExtension propertiesDefinitionExtension) {
        Level worldIn = entity.level();

        if (target == null || !target.isAlive() || target.isRemoved()) {
            return;
        }

        int count = (int) this.count.of(this.getLevel(propertiesDefinitionExtension));

        float off = entity.getRandom().nextFloat() * 360;

        for(int i = 0; i < count; i++) {
            SummondSpiralSwordEntity ss = new SummondSpiralSwordEntity(RecastingEntities.SUMMOND_SPIRAL_SWORD.get(), worldIn, entity);

            // 设置旋转中心为目标
            ss.setCenterEntity(target);

            // 设置旋转参数（使用辅助方法自动计算修饰参数）
            ss.setRadiusExpansion(2.5f, 6.0f, 30);
            ss.setSpeedDecay(16.0f, 0.3f, 30);
            ss.setRotationAngle(off + (360.0f / count * i));
            ss.setRotationAxis(new Vec3(0, 1, 0));
            ss.setRotationDirectionOutward(false);

            // 设置基本属性
            ss.setModifiedRatio(modifiedRatio.of(getLevel(propertiesDefinitionExtension)));
            ss.setColor(state.getColorCode());
            ss.setRoll(0);
            ss.setStartDelay(30);

            ss.addAttackType(RecastingAttackTypes.SPIRAL_SWORD_ATTACK.get());

            worldIn.addFreshEntity(ss);

            entity.playSound(
                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                    0.2F,
                    1.45F
            );
        }
    }

}

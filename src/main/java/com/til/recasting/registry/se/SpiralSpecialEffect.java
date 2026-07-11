package com.til.recasting.registry.se;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.entity.SummondSpiralSwordEntity;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.registry.instance.BuffType;
import com.til.recasting.util.NumberPack;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/***
 * 回旋
 * 幻影剑造成伤害后叠加剑势，达到一定层数后触发风暴幻影剑
 */
public class SpiralSpecialEffect extends ExtendedSpecialEffect {

    NumberPack modifiedRatio = new NumberPack(0.1f, 0);
    NumberPack count = new NumberPack(4, 1);
    int addLevel = 1; // 每次叠加的层数
    NumberPack triggerInterval = new NumberPack(40, 0); // 触发间隔（tick）


    // 存储每个攻击者的最后触发时间
    Map<LivingEntity, Long> lastTriggerTimeMap = new HashMap<>();

    @SubscribeEvent
    public void onAttackEvent(AttackAmplifierEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        // 只在服务端执行
        if (event.getAttacker().level().isClientSide()) {
            return;
        }

        // 检查目标是否是生物实体且存活
        if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
            return;
        }

        // 只处理幻影剑攻击
        if (!event.getAttackTypeList().contains(RecastingAttackTypes.SUMMOND_SWORD_ATTACK.get())) {
            return;
        }

        if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_SPIRAL_SPECIAL_RECURSION_ATTACK.get())) {
            return;
        }

        if (event.getAttackTypeList().contains(RecastingAttackTypes.NO_RECURSION_ATTACK.get())) {
            return;
        }

        LivingEntity attacker = event.getAttacker();
        long currentTime = target.level().getGameTime();


        ItemStack blade = event.getItem();

        //noinspection DataFlowIssue
        PropertiesDefinitionExtension se = blade.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION).orElse(null);
        //noinspection ConstantValue
        if (se == null) {
            return;
        }


        //noinspection DataFlowIssue
        IBuffStackData buffStackData = target.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).orElse(null);
        //noinspection ConstantValue
        if (buffStackData == null) {
            return;
        }

        Level world = target.level();
        BuffType swordMomentumBuffType = RecastingBuffTypes.SWORD_MOMENTUM.get();

        // 获取当前层数
        int currentLevel = buffStackData.getLevel(swordMomentumBuffType, world);

        Long lastTriggerTime = lastTriggerTimeMap.get(target);
        int interval = (int) triggerInterval.of(getLevel(se));

        // 如果触发间隔还没到，不触发风暴效果，也不叠加层数
        if (lastTriggerTime != null && (currentTime - lastTriggerTime) < interval) {
            return;
        }

        if (currentLevel < swordMomentumBuffType.getMaxLevel()) {
            int newLevel = currentLevel + addLevel;
            buffStackData.setLevel(swordMomentumBuffType, newLevel, world);
            return;
        }

        // 触发间隔到了，触发风暴效果并重置层数
        buffStackData.setLevel(swordMomentumBuffType, 0, world);
        lastTriggerTimeMap.put(target, currentTime);

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

            ss.addAttackType(RecastingAttackTypes.NO_SPIRAL_SPECIAL_RECURSION_ATTACK.get());

            worldIn.addFreshEntity(ss);

            entity.playSound(
                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                    0.2F,
                    1.45F
            );
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        long currentTime = event.getServer().getTickCount();
        if (currentTime % 20 != 0) {
            return;
        }
        // 如果没有实体，直接返回
        if (lastTriggerTimeMap.isEmpty()) {
            return;
        }

        // 清理无效的实体，防止内存泄漏
        Iterator<Map.Entry<LivingEntity, Long>> iterator = lastTriggerTimeMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<LivingEntity, Long> entry = iterator.next();
            LivingEntity target = entry.getKey();

            // 如果实体无效（null、死亡、被移除或在客户端），清除条目
            if (target == null || !target.isAlive() || target.isRemoved() || target.level().isClientSide()) {
                iterator.remove();
            }
        }
    }

}

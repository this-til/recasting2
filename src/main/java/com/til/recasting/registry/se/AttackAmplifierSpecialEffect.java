package com.til.recasting.registry.se;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.util.NumberPack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

public class AttackAmplifierSpecialEffect extends ExtendedSpecialEffect {
    RegistryObject<AttackType> attackType;
    NumberPack attack;

    public AttackAmplifierSpecialEffect(RegistryObject<AttackType> attackType, NumberPack attack) {
        super();
        this.attackType = attackType;
        this.attack = attack;
    }

    @SubscribeEvent
    public void onEvent(AttackAmplifierEvent event) {
        // 检查是否拥有此特效
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        // 检查攻击类型是否匹配
        if (!event.getAttackTypeList().contains(attackType.get())) {
            return;
        }
        // 添加伤害倍率加成
        PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(event.getItem());
        event.addModifiedRatioAmplifier(attack.of(getLevel(properties)));
    }
}

package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.registry.instance.specialeffects.ExampleSpecialEffect;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Special Effects (SE) 注册表
 * 
 * 使用示例：
 * 1. 创建继承自 SpecialEffect 的类（参考 ExampleSpecialEffect）
 * 2. 在此类中注册你的 Special Effect
 * 3. 在 Recasting.java 中注册此注册表到 modEventBus
 * 4. 在 SlashBladeDefinition 中使用 .addSpecialEffect() 方法添加 SE
 */
public class SpecialEffectsRegistry {
    /**
     * 创建 DeferredRegister，用于注册 Special Effects
     */
    public static final DeferredRegister<SpecialEffect> SPECIAL_EFFECT = DeferredRegister.create(
            SpecialEffect.REGISTRY_KEY,
            Recasting.MODID
    );


    /**
     * 示例：注册一个示例 Special Effect
     * 
     * 构造函数参数说明：
     * - requestLevel: 需要的等级（玩家经验等级）
     * - isCopiable: 是否可以被复制
     * - isRemovable: 是否可以被移除
     */
    public static final RegistryObject<SpecialEffect> EXAMPLE_SPECIAL_EFFECT = SPECIAL_EFFECT.register(
            "example_special_effect",
            () -> new ExampleSpecialEffect(0)
    );
}


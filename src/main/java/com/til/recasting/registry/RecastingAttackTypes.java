package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.mixin.DamageSourcesAccessor;
import com.til.recasting.registry.instance.AttackType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * @Author: til
 * @Description: 攻击类型注册
 */
public class RecastingAttackTypes {

    /**
     * 攻击类型注册表键
     */
    public static final ResourceKey<Registry<AttackType>> ATTACK_TYPE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Recasting.prefix("attack_type"));

    /**
     * 攻击类型注册表
     */
    public static final DeferredRegister<AttackType> ATTACK_TYPES =
            DeferredRegister.create(ATTACK_TYPE_REGISTRY_KEY, Recasting.MODID);

    /**
     * 攻击类型注册表实例
     */
    public static final Supplier<IForgeRegistry<AttackType>> REGISTRY =
            ATTACK_TYPES.makeRegistry(() -> new RegistryBuilder<AttackType>()
                    .setDefaultKey(Recasting.prefix("default"))
            );

    // ==================== 预定义的攻击类型 ====================

    /**
     * 斩击特效攻击类型（物理伤害）
     */
    public static final RegistryObject<AttackType> SLASH_EFFECT_ATTACK = ATTACK_TYPES.register("slash_effect",
            () -> new AttackType((attacker, target) -> new AttackAmplifierEvent.DamageSourceInfo(
                    attacker instanceof Player
                            ? attacker.damageSources().playerAttack((Player) attacker)
                            : attacker.damageSources().mobAttack(attacker),
                    1.0f
            ))
    );

    /**
     * 幻影剑攻击类型（魔法伤害）
     */
    public static final RegistryObject<AttackType> SUMMOND_SWORD_ATTACK = ATTACK_TYPES.register("summond_sword",
            () -> new AttackType((attacker, target) -> new AttackAmplifierEvent.DamageSourceInfo(
                    attacker.damageSources().indirectMagic(target, attacker),
                    1.0f
            ))
    );

    /**
     * 次元斩攻击类型（虚空伤害 - 使用 fellOutOfWorld 伤害源，带上发动者）
     */
    public static final RegistryObject<AttackType> JUDGEMENT_CUT_ATTACK = ATTACK_TYPES.register("judgement_cut",
            () -> new AttackType((attacker, target) -> {
                DamageSourcesAccessor accessor = (DamageSourcesAccessor) attacker.damageSources();
                DamageSource damageSource = accessor.callSource(DamageTypes.DRAGON_BREATH, target, attacker);
                return new AttackAmplifierEvent.DamageSourceInfo(damageSource, 1.0f);
            })
    );

    /**
     * 剑气攻击类型（魔法伤害）
     */
    public static final RegistryObject<AttackType> DRIVE_ATTACK = ATTACK_TYPES.register("drive",
            () -> new AttackType((attacker, target) -> new AttackAmplifierEvent.DamageSourceInfo(
                    attacker.damageSources().indirectMagic(target, attacker),
                    1.0f
            ))
    );

    /**
     * 闪电攻击类型（魔法伤害）
     */
    public static final RegistryObject<AttackType> LIGHTNING_ATTACK = ATTACK_TYPES.register("lightning",
            () -> new AttackType((attacker, target) -> {
                DamageSourcesAccessor accessor = (DamageSourcesAccessor) attacker.damageSources();
                DamageSource damageSource = accessor.callSource(DamageTypes.LIGHTNING_BOLT, target, attacker);
                return new AttackAmplifierEvent.DamageSourceInfo(damageSource, 1.0f);
            })
    );

    /**
     * 黑色玫瑰攻击类型（虚空伤害 - 使用 fellOutOfWorld 伤害源，带上发动者）
     */
    public static final RegistryObject<AttackType> BLACK_ROSE_ATTACK = ATTACK_TYPES.register("black_rose",
            () -> new AttackType((attacker, target) -> {
                DamageSourcesAccessor accessor = (DamageSourcesAccessor) attacker.damageSources();
                DamageSource damageSource = accessor.callSource(DamageTypes.WITHER_SKULL, target, attacker);
                return new AttackAmplifierEvent.DamageSourceInfo(damageSource, 1.0f);
            })
    );

    /**
     * 防止递归攻击类型（用于标记不应该触发SE叠加的攻击，防止递归）
     * 使用通用魔法伤害，带上发动者
     */
    public static final RegistryObject<AttackType> NO_RECURSION_ATTACK = ATTACK_TYPES.register("no_recursion",
            () -> new AttackType((attacker, target) -> null)
    );

    /**
     * 星闪攻击类型（魔法伤害）
     */
    public static final RegistryObject<AttackType> STAR_BLINK_ATTACK = ATTACK_TYPES.register("star_blink",
            () -> new AttackType((attacker, target) -> new AttackAmplifierEvent.DamageSourceInfo(
                    attacker.damageSources().indirectMagic(target, attacker),
                    1.0f
            ))
    );


}


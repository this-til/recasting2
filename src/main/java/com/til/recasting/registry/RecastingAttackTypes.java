package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.mixin.DamageSourcesAccessor;
import com.til.recasting.registry.instance.AttackType;
import com.til.recasting.util.DamageStructure;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

/**
 * 攻击类型注册表。
 */
public final class RecastingAttackTypes {

    public static final ResourceKey<Registry<AttackType>> ATTACK_TYPE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Recasting.prefix("attack_type"));

    public static final DeferredRegister<AttackType> ATTACK_TYPES =
            DeferredRegister.create(ATTACK_TYPE_REGISTRY_KEY, Recasting.MODID);

    public static final Registry<AttackType> REGISTRY =
            new RegistryBuilder<>(ATTACK_TYPE_REGISTRY_KEY).sync(true).create();

    public static final DeferredHolder<AttackType, AttackType> SLASH_EFFECT_ATTACK = ATTACK_TYPES.register(
            "slash_effect",
            () -> new AttackType((attacker, target) -> new AttackAmplifierEvent.DamageSourceInfo(
                    attacker instanceof Player
                            ? attacker.damageSources().playerAttack((Player) attacker)
                            : attacker.damageSources().mobAttack(attacker),
                    new DamageStructure(1.0f, 0.0f)
            ))
    );

    public static final DeferredHolder<AttackType, AttackType> SUMMOND_SWORD_ATTACK = ATTACK_TYPES.register(
            "summond_sword",
            () -> new AttackType((attacker, target) -> new AttackAmplifierEvent.DamageSourceInfo(
                    attacker.damageSources().indirectMagic(target, attacker),
                    new DamageStructure(1.0f, 0.0f)
            ))
    );

    public static final DeferredHolder<AttackType, AttackType> JUDGEMENT_CUT_ATTACK = ATTACK_TYPES.register(
            "judgement_cut",
            () -> new AttackType((attacker, target) -> {
                DamageSourcesAccessor accessor = (DamageSourcesAccessor) attacker.damageSources();
                DamageSource damageSource = accessor.callSource(DamageTypes.DRAGON_BREATH, target, attacker);
                return new AttackAmplifierEvent.DamageSourceInfo(damageSource, new DamageStructure(1.0f, 0.0f));
            })
    );

    public static final DeferredHolder<AttackType, AttackType> DRIVE_ATTACK = ATTACK_TYPES.register(
            "drive",
            () -> new AttackType((attacker, target) -> new AttackAmplifierEvent.DamageSourceInfo(
                    attacker.damageSources().indirectMagic(target, attacker),
                    new DamageStructure(1.0f, 0.0f)
            ))
    );

    public static final DeferredHolder<AttackType, AttackType> LIGHTNING_ATTACK = ATTACK_TYPES.register(
            "lightning",
            () -> new AttackType((attacker, target) -> {
                DamageSourcesAccessor accessor = (DamageSourcesAccessor) attacker.damageSources();
                DamageSource damageSource = accessor.callSource(DamageTypes.LIGHTNING_BOLT, target, attacker);
                return new AttackAmplifierEvent.DamageSourceInfo(damageSource, new DamageStructure(1.0f, 0.0f));
            })
    );

    public static final DeferredHolder<AttackType, AttackType> BLACK_ROSE_ATTACK = ATTACK_TYPES.register(
            "black_rose",
            () -> new AttackType((attacker, target) -> {
                DamageSourcesAccessor accessor = (DamageSourcesAccessor) attacker.damageSources();
                DamageSource damageSource = accessor.callSource(DamageTypes.WITHER_SKULL, target, attacker);
                return new AttackAmplifierEvent.DamageSourceInfo(damageSource, new DamageStructure(1.0f, 0.0f));
            })
    );

    public static final DeferredHolder<AttackType, AttackType> NO_RECURSION_ATTACK = ATTACK_TYPES.register(
            "no_recursion",
            () -> new AttackType((attacker, target) -> null)
    );

    public static final DeferredHolder<AttackType, AttackType> NO_KNOCKBACK_ATTACK = ATTACK_TYPES.register(
            "no_knockback",
            () -> new AttackType((attacker, target) -> null)
    );

    public static final DeferredHolder<AttackType, AttackType> SPIRAL_SWORD_ATTACK = ATTACK_TYPES.register(
            "no_spiral_special_recursion",
            () -> new AttackType((attacker, target) -> null)
    );

    public static final DeferredHolder<AttackType, AttackType> MATRIX = ATTACK_TYPES.register(
            "matrix",
            () -> new AttackType((attacker, target) -> {
                DamageSourcesAccessor accessor = (DamageSourcesAccessor) attacker.damageSources();
                DamageSource damageSource = accessor.callSource(DamageTypes.DRAGON_BREATH, target, attacker);
                return new AttackAmplifierEvent.DamageSourceInfo(damageSource, new DamageStructure(1.0f, 0.0f));
            })
    );

    public static final DeferredHolder<AttackType, AttackType> STAR_BLINK_ATTACK = ATTACK_TYPES.register(
            "star_blink",
            () -> new AttackType((attacker, target) -> new AttackAmplifierEvent.DamageSourceInfo(
                    attacker.damageSources().indirectMagic(target, attacker),
                    new DamageStructure(1.0f, 0.0f)
            ))
    );

    public static final DeferredHolder<AttackType, AttackType> FRAGMENT_ATTACK = ATTACK_TYPES.register(
            "fragment",
            () -> new AttackType((attacker, target) -> new AttackAmplifierEvent.DamageSourceInfo(
                    attacker.damageSources().indirectMagic(target, attacker),
                    new DamageStructure(1.0f, 0.0f)
            ))
    );

    public static final DeferredHolder<AttackType, AttackType> TEAR_ATTACK = ATTACK_TYPES.register(
            "tear",
            () -> new AttackType((attacker, target) -> {
                DamageSourcesAccessor accessor = (DamageSourcesAccessor) attacker.damageSources();
                DamageSource damageSource = accessor.callSource(DamageTypes.DRAGON_BREATH, target, attacker);
                return new AttackAmplifierEvent.DamageSourceInfo(damageSource, new DamageStructure(1.0f, 0.0f));
            })
    );

    public static final DeferredHolder<AttackType, AttackType> RESOLVE = ATTACK_TYPES.register(
            "resolve",
            () -> new AttackType((attacker, target) -> {
                DamageSourcesAccessor accessor = (DamageSourcesAccessor) attacker.damageSources();
                DamageSource damageSource = accessor.callSource(DamageTypes.EXPLOSION, target, attacker);
                return new AttackAmplifierEvent.DamageSourceInfo(damageSource, new DamageStructure(1.0f, 0.0f));
            })
    );

    public static final DeferredHolder<AttackType, AttackType> LASER_ATTACK = ATTACK_TYPES.register(
            "laser",
            () -> new AttackType((attacker, target) -> {
                DamageSourcesAccessor accessor = (DamageSourcesAccessor) attacker.damageSources();
                DamageSource damageSource = accessor.callSource(DamageTypes.ON_FIRE, target, attacker);
                return new AttackAmplifierEvent.DamageSourceInfo(damageSource, new DamageStructure(1.0f, 0.0f));
            })
    );

    public static final DeferredHolder<AttackType, AttackType> PHOTON_SCAR_ATTACK = ATTACK_TYPES.register(
            "photon_scar",
            () -> new AttackType((attacker, target) -> null)
    );

    public static final DeferredHolder<AttackType, AttackType> SUNSET_CORE_MARK = ATTACK_TYPES.register(
            "sunset_core_mark",
            () -> new AttackType((attacker, target) -> null)
    );

    public static final DeferredHolder<AttackType, AttackType> HUI_GUANG_ATTACK = ATTACK_TYPES.register(
            "hui_guang",
            () -> new AttackType((attacker, target) -> {
                DamageSourcesAccessor accessor = (DamageSourcesAccessor) attacker.damageSources();
                DamageSource damageSource = accessor.callSource(DamageTypes.ON_FIRE, target, attacker);
                return new AttackAmplifierEvent.DamageSourceInfo(damageSource, new DamageStructure(1.0f, 0.0f));
            })
    );

    public static final DeferredHolder<AttackType, AttackType> GOLDEN_HALBERD_ATTACK = ATTACK_TYPES.register(
            "golden_halberd",
            () -> new AttackType((attacker, target) -> new AttackAmplifierEvent.DamageSourceInfo(
                    attacker.damageSources().indirectMagic(target, attacker),
                    new DamageStructure(1.0f, 0.0f)
            ))
    );

    public static final DeferredHolder<AttackType, AttackType> SOUL_BURN_ATTACK = ATTACK_TYPES.register(
            "soul_burn",
            () -> new AttackType((attacker, target) -> {
                DamageSourcesAccessor accessor = (DamageSourcesAccessor) attacker.damageSources();
                DamageSource damageSource = accessor.callSource(DamageTypes.IN_FIRE, target, attacker);
                return new AttackAmplifierEvent.DamageSourceInfo(damageSource, new DamageStructure(1.0f, 0.0f));
            })
    );

    public static final DeferredHolder<AttackType, AttackType> PHOTON_BURN_ATTACK = ATTACK_TYPES.register(
            "photon_burn",
            () -> new AttackType((attacker, target) -> {
                DamageSourcesAccessor accessor = (DamageSourcesAccessor) attacker.damageSources();
                DamageSource damageSource = accessor.callSource(DamageTypes.ON_FIRE, target, attacker);
                return new AttackAmplifierEvent.DamageSourceInfo(damageSource, new DamageStructure(1.0f, 0.0f));
            })
    );

    public static final DeferredHolder<AttackType, AttackType> JADE_FIRE_ATTACK = ATTACK_TYPES.register(
            "jade_fire",
            () -> new AttackType((attacker, target) -> {
                DamageSourcesAccessor accessor = (DamageSourcesAccessor) attacker.damageSources();
                DamageSource damageSource = accessor.callSource(DamageTypes.ON_FIRE, target, attacker);
                return new AttackAmplifierEvent.DamageSourceInfo(damageSource, new DamageStructure(1.0f, 0.0f));
            })
    );

    public static final DeferredHolder<AttackType, AttackType> TEA_AROMA_ATTACK = ATTACK_TYPES.register(
            "tea_aroma",
            () -> new AttackType((attacker, target) -> new AttackAmplifierEvent.DamageSourceInfo(
                    attacker.damageSources().indirectMagic(target, attacker),
                    new DamageStructure(1.0f, 0.0f)
            ))
    );

    public static final DeferredHolder<AttackType, AttackType> SOUL_SEVER_DELAYED_ATTACK = ATTACK_TYPES.register(
            "soul_sever_delayed",
            () -> new AttackType((attacker, target) -> {
                DamageSourcesAccessor accessor = (DamageSourcesAccessor) attacker.damageSources();
                DamageSource damageSource = accessor.callSource(DamageTypes.FELL_OUT_OF_WORLD, target, attacker);
                return new AttackAmplifierEvent.DamageSourceInfo(damageSource, new DamageStructure(1.0f, 0.0f));
            })
    );

    public static final DeferredHolder<AttackType, AttackType> MYRIAD_SILENCE_ATTACK = ATTACK_TYPES.register(
            "myriad_silence",
            () -> new AttackType((attacker, target) -> {
                DamageSourcesAccessor accessor = (DamageSourcesAccessor) attacker.damageSources();
                DamageSource damageSource = accessor.callSource(DamageTypes.FELL_OUT_OF_WORLD, target, attacker);
                return new AttackAmplifierEvent.DamageSourceInfo(damageSource, new DamageStructure(1.0f, 0.0f));
            })
    );

    public static final DeferredHolder<AttackType, AttackType> DOG_BITE_ATTACK = ATTACK_TYPES.register(
            "dog_bite",
            () -> new AttackType((attacker, target) -> new AttackAmplifierEvent.DamageSourceInfo(
                    attacker instanceof Player
                            ? attacker.damageSources().playerAttack((Player) attacker)
                            : attacker.damageSources().mobAttack(attacker),
                    new DamageStructure(1.0f, 0.0f)
            ))
    );

    public static final DeferredHolder<AttackType, AttackType> ABSOLUTE_ATTACK = ATTACK_TYPES.register(
            "absolute",
            () -> new AttackType((attacker, target) -> null)
    );

    private RecastingAttackTypes() {
    }
}

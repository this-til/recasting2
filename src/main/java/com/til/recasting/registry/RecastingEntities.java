package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.entity.DriveEntity;
import com.til.recasting.entity.FinalGlowBlackHoleEntity;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.entity.LightningEntity;
import com.til.recasting.entity.MatrixEntity;
import com.til.recasting.entity.SlashEffectEntity;
import com.til.recasting.entity.StarfallArrayEntity;
import com.til.recasting.entity.StellarRotationEntity;
import com.til.recasting.entity.SummondSpiralSwordEntity;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.entity.TrackingSummondSwordEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 实体类型注册表。
 */
public final class RecastingEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Recasting.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<LightningEntity>> LIGHTNING = ENTITY_TYPES.register(
            "lightning",
            () -> EntityType.Builder.<LightningEntity>of((e, l) -> new LightningEntity(e, l, null), MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(64)
                    .updateInterval(20)
                    .fireImmune()
                    .noSave()
                    .noSummon()
                    .setShouldReceiveVelocityUpdates(true)
                    .build("lightning")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<JudgementCutEntity>> JUDGEMENT_CUT = ENTITY_TYPES.register(
            "judgement_cut",
            () -> EntityType.Builder.<JudgementCutEntity>of((e, l) -> new JudgementCutEntity(e, l, null), MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(64)
                    .updateInterval(20)
                    .fireImmune()
                    .noSave()
                    .noSummon()
                    .setShouldReceiveVelocityUpdates(true)
                    .build("judgement_cut")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<SummondSwordEntity>> SUMMOND_SWORD = ENTITY_TYPES.register(
            "summond_sword",
            () -> EntityType.Builder.<SummondSwordEntity>of((e, l) -> new SummondSwordEntity(e, l, null), MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(64)
                    .updateInterval(20)
                    .fireImmune()
                    .noSave()
                    .noSummon()
                    .setShouldReceiveVelocityUpdates(true)
                    .build("summond_sword")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<SummondSpiralSwordEntity>> SUMMOND_SPIRAL_SWORD = ENTITY_TYPES.register(
            "summond_spiral_sword",
            () -> EntityType.Builder.<SummondSpiralSwordEntity>of((e, l) -> new SummondSpiralSwordEntity(e, l, null), MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(64)
                    .updateInterval(20)
                    .fireImmune()
                    .noSave()
                    .noSummon()
                    .setShouldReceiveVelocityUpdates(true)
                    .build("summond_spiral_sword")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<TrackingSummondSwordEntity>> TRACKING_SUMMOND_SWORD = ENTITY_TYPES.register(
            "tracking_summond_sword",
            () -> EntityType.Builder.<TrackingSummondSwordEntity>of((e, l) -> new TrackingSummondSwordEntity(e, l, null), MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(128)
                    .updateInterval(20)
                    .fireImmune()
                    .noSave()
                    .noSummon()
                    .setShouldReceiveVelocityUpdates(true)
                    .build("tracking_summond_sword")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<SlashEffectEntity>> SLASH_EFFECT = ENTITY_TYPES.register(
            "slash_effect",
            () -> EntityType.Builder.<SlashEffectEntity>of((e, l) -> new SlashEffectEntity(e, l, null), MobCategory.MISC)
                    .sized(4f, 4f)
                    .clientTrackingRange(64)
                    .updateInterval(20)
                    .fireImmune()
                    .noSave()
                    .noSummon()
                    .setShouldReceiveVelocityUpdates(true)
                    .build("slash_effect")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<DriveEntity>> DRIVE = ENTITY_TYPES.register(
            "drive",
            () -> EntityType.Builder.<DriveEntity>of((e, l) -> new DriveEntity(e, l, null), MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(64)
                    .updateInterval(20)
                    .fireImmune()
                    .noSave()
                    .noSummon()
                    .setShouldReceiveVelocityUpdates(true)
                    .build("drive")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<StellarRotationEntity>> STELLAR_ROTATION = ENTITY_TYPES.register(
            "stellar_rotation",
            () -> EntityType.Builder.<StellarRotationEntity>of((e, l) -> new StellarRotationEntity(e, l, null), MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(64)
                    .updateInterval(20)
                    .fireImmune()
                    .noSave()
                    .noSummon()
                    .setShouldReceiveVelocityUpdates(true)
                    .build("stellar_rotation")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<MatrixEntity>> MATRIX = ENTITY_TYPES.register(
            "matrix",
            () -> EntityType.Builder.<MatrixEntity>of((e, l) -> new MatrixEntity(e, l, null), MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(128)
                    .updateInterval(20)
                    .fireImmune()
                    .noSave()
                    .noSummon()
                    .setShouldReceiveVelocityUpdates(true)
                    .build("matrix")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<StarfallArrayEntity>> STARFALL_ARRAY = ENTITY_TYPES.register(
            "starfall_array",
            () -> EntityType.Builder.<StarfallArrayEntity>of((e, l) -> new StarfallArrayEntity(e, l, null), MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(128)
                    .updateInterval(1)
                    .fireImmune()
                    .noSave()
                    .noSummon()
                    .setShouldReceiveVelocityUpdates(true)
                    .build("starfall_array")
    );

    public static final DeferredHolder<EntityType<?>, EntityType<FinalGlowBlackHoleEntity>> FINAL_GLOW_BLACK_HOLE = ENTITY_TYPES.register(
            "final_glow_black_hole",
            () -> EntityType.Builder.<FinalGlowBlackHoleEntity>of((e, l) -> new FinalGlowBlackHoleEntity(e, l, null), MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .clientTrackingRange(128)
                    .updateInterval(1)
                    .fireImmune()
                    .noSave()
                    .noSummon()
                    .setShouldReceiveVelocityUpdates(true)
                    .build("final_glow_black_hole")
    );

    private RecastingEntities() {
    }
}

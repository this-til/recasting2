package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.entity.DriveEntity;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.entity.LightningEntity;
import com.til.recasting.entity.SlashEffectEntity;
import com.til.recasting.entity.SummondSwordEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 实体类型注册表
 */
public class RecastingEntities {

    /**
     * 创建 DeferredRegister，用于注册实体类型
     */
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(
            ForgeRegistries.ENTITY_TYPES,
            Recasting.MODID
    );

    /**
     * 闪电特效实体
     * - 用于创建闪电攻击特效
     * - 造成雷电伤害并带有视觉效果
     * - 支持自定义颜色、大小、攻击间隔
     */
    public static final RegistryObject<EntityType<LightningEntity>> LIGHTNING = ENTITY_TYPES.register(
            "lightning",
            () -> EntityType.Builder.<LightningEntity>of((e, l) -> new LightningEntity(e, l, null), MobCategory.MISC)
                    .sized(0.5f, 0.5f)              // 碰撞箱大小
                    .clientTrackingRange(64)         // 客户端追踪范围（格）
                    .updateInterval(20)              // 更新间隔（tick）
                    .fireImmune()                    // 免疫火焰伤害
                    .noSave()                        // 不保存到世界数据
                    .noSummon()                      // 不能通过命令召唤
                    .setShouldReceiveVelocityUpdates(true)
                    .build("lightning")
    );

    /**
     * 次元斩实体
     * - 用于创建次元斩攻击特效
     * - 造成次元斩伤害并带有视觉效果
     * - 支持自定义颜色、大小、生命周期
     */
    public static final RegistryObject<EntityType<JudgementCutEntity>> JUDGEMENT_CUT = ENTITY_TYPES.register(
            "judgement_cut",
            () -> EntityType.Builder.<JudgementCutEntity>of((e, l) -> new JudgementCutEntity(e, l, null), MobCategory.MISC)
                    .sized(1.5f, 1.5f)               // 碰撞箱大小
                    .clientTrackingRange(64)         // 客户端追踪范围（格）
                    .updateInterval(20)              // 更新间隔（tick）
                    .fireImmune()                    // 免疫火焰伤害
                    .noSave()                        // 不保存到世界数据
                    .noSummon()                      // 不能通过命令召唤
                    .setShouldReceiveVelocityUpdates(true)
                    .build("judgement_cut")
    );

    /**
     * 召唤剑实体
     * - 用于创建召唤剑攻击特效
     * - 可跟踪目标、穿透多个敌人
     * - 支持自定义模型、纹理、颜色、大小
     */
    public static final RegistryObject<EntityType<SummondSwordEntity>> SUMMOND_SWORD = ENTITY_TYPES.register(
            "summond_sword",
            () -> EntityType.Builder.<SummondSwordEntity>of((e, l) -> new SummondSwordEntity(e, l, null), MobCategory.MISC)
                    .sized(0.5f, 0.5f)               // 碰撞箱大小
                    .clientTrackingRange(64)         // 客户端追踪范围（格）
                    .updateInterval(20)              // 更新间隔（tick）
                    .fireImmune()                    // 免疫火焰伤害
                    .noSave()                        // 不保存到世界数据
                    .noSummon()                      // 不能通过命令召唤
                    .setShouldReceiveVelocityUpdates(true)
                    .build("summond_sword")
    );

    /**
     * 斩击特效实体
     * - 用于创建斩击攻击特效
     * - 造成范围伤害并带有视觉效果
     * - 支持自定义颜色、大小、旋转
     */
    public static final RegistryObject<EntityType<SlashEffectEntity>> SLASH_EFFECT = ENTITY_TYPES.register(
            "slash_effect",
            () -> EntityType.Builder.<SlashEffectEntity>of((e, l) -> new SlashEffectEntity(e, l, null), MobCategory.MISC)
                    .sized(3.0f, 3.0f)               // 碰撞箱大小
                    .clientTrackingRange(64)         // 客户端追踪范围（格）
                    .updateInterval(20)              // 更新间隔（tick）
                    .fireImmune()                    // 免疫火焰伤害
                    .noSave()                        // 不保存到世界数据
                    .noSummon()                      // 不能通过命令召唤
                    .setShouldReceiveVelocityUpdates(true)
                    .build("slash_effect")
    );

    /**
     * Drive 实体（剑气飞行攻击）
     * - 用于创建飞行剑气特效
     * - 可以穿透多个目标或墙体
     * - 支持自定义速度、颜色、大小
     */
    public static final RegistryObject<EntityType<DriveEntity>> DRIVE = ENTITY_TYPES.register(
            "drive",
            () -> EntityType.Builder.<DriveEntity>of((e, l) -> new DriveEntity(e, l, null), MobCategory.MISC)
                    .sized(3.0f, 3.0f)               // 碰撞箱大小
                    .clientTrackingRange(64)         // 客户端追踪范围（格）
                    .updateInterval(20)              // 更新间隔（tick）
                    .fireImmune()                    // 免疫火焰伤害
                    .noSave()                        // 不保存到世界数据
                    .noSummon()                      // 不能通过命令召唤
                    .setShouldReceiveVelocityUpdates(true)
                    .build("drive")
    );
}


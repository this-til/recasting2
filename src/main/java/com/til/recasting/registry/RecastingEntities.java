package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.entity.LightningEntity;
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
            () -> EntityType.Builder.<LightningEntity>of(LightningEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)              // 碰撞箱大小
                    .clientTrackingRange(64)         // 客户端追踪范围（格）
                    .updateInterval(1)               // 更新间隔（tick）
                    .fireImmune()                    // 免疫火焰伤害
                    .noSave()                        // 不保存到世界数据
                    .noSummon()                      // 不能通过命令召唤
                    .build("lightning")
    );
}


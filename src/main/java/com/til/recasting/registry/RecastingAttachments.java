package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.IJieYuanDogBond;
import com.til.recasting.capability.IProudSoulDropCooldown;
import com.til.recasting.capability.ITimeRun;
import com.til.recasting.capability.InventorySlashBladeSeCache;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 实体 Attachment 注册表（原 Capability 实体侧）。
 */
public final class RecastingAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Recasting.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ITimeRun>> TIME_RUN =
            ATTACHMENT_TYPES.register("time_run", () -> AttachmentType
                    .<ITimeRun>builder(holder -> {
                        ITimeRun.TimeRun timeRun = new ITimeRun.TimeRun();
                        if (holder instanceof LivingEntity livingEntity) {
                            timeRun.setEntity(livingEntity);
                        }
                        return timeRun;
                    })
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<IBuffStackData.BuffStackData>> BUFF_STACK_DATA =
            ATTACHMENT_TYPES.register("buff_stack_data", () -> AttachmentType
                    .builder(holder -> {
                        IBuffStackData.BuffStackData data = new IBuffStackData.BuffStackData();
                        if (holder instanceof LivingEntity livingEntity) {
                            data.setEntity(livingEntity);
                        }
                        return data;
                    })
                    .serialize(IBuffStackData.BuffStackData.CODEC)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<IJieYuanDogBond.JieYuanDogBond>> JIE_YUAN_DOG_BOND =
            ATTACHMENT_TYPES.register("jie_yuan_dog_bond", () -> AttachmentType
                    .builder(IJieYuanDogBond.JieYuanDogBond::new)
                    .serialize(IJieYuanDogBond.JieYuanDogBond.CODEC)
                    .copyOnDeath()
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<IProudSoulDropCooldown.ProudSoulDropCooldown>> PROUD_SOUL_DROP_COOLDOWN =
            ATTACHMENT_TYPES.register("proud_soul_drop_cooldown", () -> AttachmentType
                    .builder(IProudSoulDropCooldown.ProudSoulDropCooldown::new)
                    .serialize(IProudSoulDropCooldown.ProudSoulDropCooldown.CODEC)
                    .copyOnDeath()
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<InventorySlashBladeSeCache>> INVENTORY_SLASH_BLADE_SE_CACHE =
            ATTACHMENT_TYPES.register("inventory_slash_blade_se_cache", () -> AttachmentType
                    .builder(InventorySlashBladeSeCache::new)
                    .build());

    private RecastingAttachments() {
    }

    /**
     * 取得实体 TIME_RUN，并确保绑定当前 LivingEntity。
     */
    public static ITimeRun timeRun(Entity entity) {
        ITimeRun data = entity.getData(TIME_RUN.get());
        if (data instanceof ITimeRun.TimeRun timeRun && entity instanceof LivingEntity livingEntity) {
            timeRun.setEntity(livingEntity);
        }
        return data;
    }

    /**
     * 取得实体 BUFF_STACK_DATA，并确保绑定当前 LivingEntity。
     */
    public static IBuffStackData buffStackData(Entity entity) {
        IBuffStackData.BuffStackData data = entity.getData(BUFF_STACK_DATA.get());
        if (entity instanceof LivingEntity livingEntity) {
            data.setEntity(livingEntity);
        }
        return data;
    }

    public static IJieYuanDogBond jieYuanDogBond(Entity entity) {
        return entity.getData(JIE_YUAN_DOG_BOND.get());
    }

    public static IProudSoulDropCooldown proudSoulDropCooldown(Entity entity) {
        return entity.getData(PROUD_SOUL_DROP_COOLDOWN.get());
    }

    public static InventorySlashBladeSeCache inventorySlashBladeSeCache(Entity entity) {
        return entity.getData(INVENTORY_SLASH_BLADE_SE_CACHE.get());
    }
}

package com.til.recasting.handler;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.registry.instance.BuffType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Buff 来源实体记录工具。
 */
public final class BuffSourceHelper {

    private static final String KEY_SOURCE_ENTITY_UUID = "SourceEntityUuid";

    private BuffSourceHelper() {
    }

    public static void recordSourceEntity(IBuffStackData buffStackData, BuffType buffType, LivingEntity target, @Nullable LivingEntity source) {
        if (buffStackData == null || buffType == null || target == null || source == null) {
            return;
        }

        CompoundTag customData = buffStackData.getOrCreateCustomData(buffType, target.level());
        customData.putUUID(KEY_SOURCE_ENTITY_UUID, source.getUUID());
    }

    @Nullable
    public static LivingEntity getSourceEntity(@Nullable IBuffStackData.BuffEntry entry, Level level) {
        if (entry == null || entry.getCustomData() == null) {
            return null;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        CompoundTag customData = entry.getCustomData();
        if (!customData.hasUUID(KEY_SOURCE_ENTITY_UUID)) {
            return null;
        }

        UUID uuid = customData.getUUID(KEY_SOURCE_ENTITY_UUID);
        Entity entity = serverLevel.getEntity(uuid);
        if (entity instanceof LivingEntity livingEntity && livingEntity.isAlive()) {
            return livingEntity;
        }
        return null;
    }
}

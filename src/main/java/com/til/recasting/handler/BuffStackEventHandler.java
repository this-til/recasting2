package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.network.BuffStackSyncMessage;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.instance.BuffType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Buff 叠加同步：收集脏条目并在服务端 tick 末发送给追踪客户端。
 */
@EventBusSubscriber(modid = Recasting.MODID)
public final class BuffStackEventHandler {

    private static final Map<LivingEntity, Set<BuffType>> ENTITIES_TO_SYNC = new ConcurrentHashMap<>();

    private BuffStackEventHandler() {
    }

    /**
     * 标记指定 buff 需要同步到追踪客户端。
     */
    public static void markForSync(LivingEntity entity, BuffType buffType) {
        if (entity == null || buffType == null || entity.level().isClientSide()) {
            return;
        }
        ENTITIES_TO_SYNC.computeIfAbsent(entity, ignored -> ConcurrentHashMap.newKeySet()).add(buffType);
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (ENTITIES_TO_SYNC.isEmpty()) {
            return;
        }

        // 复制映射以避免并发修改
        Map<LivingEntity, Set<BuffType>> toSync = new HashMap<>();
        for (Map.Entry<LivingEntity, Set<BuffType>> entry : ENTITIES_TO_SYNC.entrySet()) {
            toSync.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        ENTITIES_TO_SYNC.clear();

        for (Map.Entry<LivingEntity, Set<BuffType>> entry : toSync.entrySet()) {
            LivingEntity entity = entry.getKey();
            if (entity.isRemoved() || !entity.isAlive()) {
                continue;
            }
            IBuffStackData data = RecastingAttachments.buffStackData(entity);
            for (BuffType buffType : entry.getValue()) {
                syncToClient(entity, data, buffType);
            }
        }
    }

    private static void syncToClient(LivingEntity entity, IBuffStackData data, BuffType buffType) {
        if (entity.level().isClientSide()) {
            return;
        }
        ResourceLocation buffTypeKey = buffType.getKey();
        if (buffTypeKey == null) {
            return;
        }

        BuffStackSyncMessage message = new BuffStackSyncMessage(
                entity.getId(),
                buffTypeKey,
                data.getEntry(buffType)
        );

        PacketDistributor.sendToPlayersTrackingEntity(entity, message);
        if (entity instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, message);
        }
    }
}

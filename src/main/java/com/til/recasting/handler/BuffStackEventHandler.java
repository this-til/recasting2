package com.til.recasting.handler;

import com.til.recasting.Recasting;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.provider.BuffStackProvider;
import com.til.recasting.network.BuffStackSyncMessage;
import com.til.recasting.network.NetworkManager;
import com.til.recasting.registry.instance.BuffType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Buff叠加数据事件处理器
 * 负责附加Capability、更新数据和网络同步
 */
@Mod.EventBusSubscriber(modid = Recasting.MODID)
public class BuffStackEventHandler {

    /**
     * 需要同步的实体和buff类型映射
     * Key: 实体, Value: 需要同步的buff类型集合
     */
    private static final Map<LivingEntity, Set<BuffType>> entitiesToSync = new ConcurrentHashMap<>();

    /**
     * 标记实体的特定buff类型需要同步
     */
    public static void markForSync(LivingEntity entity, BuffType buffType) {
        if (entity != null && !entity.level().isClientSide() && buffType != null) {
            entitiesToSync.computeIfAbsent(entity, k -> ConcurrentHashMap.newKeySet()).add(buffType);
        }
    }

    /**
     * 为LivingEntity附加BuffStack Capability
     */
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof LivingEntity livingEntity)) {
            return;
        }

        BuffStackProvider provider = new BuffStackProvider();
        provider.setEntity(livingEntity);
        event.addCapability(Recasting.prefix("buff_stack"), provider);
        // 确保在实体移除时失效
        event.addListener(provider::invalidate);
    }

    /**
     * 每tick同步需要更新的实体（服务端）
     */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (entitiesToSync.isEmpty()) {
            return;
        }

        // 复制映射以避免并发修改
        Map<LivingEntity, Set<BuffType>> toSync = new HashMap<>();
        for(Map.Entry<LivingEntity, Set<BuffType>> entry : entitiesToSync.entrySet()) {
            toSync.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        entitiesToSync.clear();

        for(Map.Entry<LivingEntity, Set<BuffType>> entry : toSync.entrySet()) {
            LivingEntity entity = entry.getKey();
            Set<BuffType> buffTypes = entry.getValue();

            // 检查实体是否仍然有效
            if (entity.isRemoved() || !entity.isAlive()) {
                continue;
            }

            entity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
                // 同步每个变化的buff类型
                for(BuffType buffType : buffTypes) {
                    syncToClient(entity, data, buffType);
                }
            });
        }
    }

    /**
     * 同步单个buff条目到客户端
     */
    private static void syncToClient(LivingEntity entity, IBuffStackData data, BuffType buffType) {
        if (entity.level().isClientSide()) {
            return;
        }

        // 获取当前buff条目
        IBuffStackData.BuffEntry entry = data.getEntry(buffType);

        // 创建消息（entry为null时表示移除）
        BuffStackSyncMessage message = new BuffStackSyncMessage(
                entity.getId(),
                buffType,
                entry
        );

        // 发送给所有追踪该实体的玩家
        NetworkManager.INSTANCE.send(
                PacketDistributor.TRACKING_ENTITY.with(() -> entity),
                message
        );

        // 如果实体是玩家，也发送给该玩家自己
        if (entity instanceof ServerPlayer player) {
            NetworkManager.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    message
            );
        }
    }
}

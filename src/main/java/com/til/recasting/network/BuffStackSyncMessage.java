package com.til.recasting.network;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Buff叠加数据网络同步消息
 * 优化为只同步单个buff条目的更新
 */
public class BuffStackSyncMessage {
    private int entityId;
    private ResourceLocation buffTypeKey;
    private IBuffStackData.BuffEntry entry;

    public BuffStackSyncMessage() {
    }

    public BuffStackSyncMessage(int entityId, BuffType buffType, IBuffStackData.BuffEntry entry) {
        this.entityId = entityId;
        this.buffTypeKey = buffType != null
                ? buffType.getKey()
                : null;
        this.entry = entry;
    }

    public static BuffStackSyncMessage decode(FriendlyByteBuf buf) {
        BuffStackSyncMessage msg = new BuffStackSyncMessage();
        msg.entityId = buf.readInt();
        msg.buffTypeKey = buf.readResourceLocation();

        boolean hasEntry = buf.readBoolean();
        if (hasEntry) {
            int level = buf.readInt();
            long lastUpdateTime = buf.readLong();
            CompoundTag customData = buf.readBoolean()
                    ? buf.readNbt()
                    : null;
            msg.entry = new IBuffStackData.BuffEntry(level, lastUpdateTime, customData);
        } else {
            msg.entry = null; // 表示移除该buff
        }

        return msg;
    }

    public static void encode(BuffStackSyncMessage msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeResourceLocation(msg.buffTypeKey);

        if (msg.entry != null) {
            buf.writeBoolean(true);
            buf.writeInt(msg.entry.getLevel());
            buf.writeLong(msg.entry.getLastUpdateTime());
            CompoundTag customData = msg.entry.getCustomData();
            if (customData != null) {
                buf.writeBoolean(true);
                buf.writeNbt(customData);
            } else {
                buf.writeBoolean(false);
            }
        } else {
            buf.writeBoolean(false); // 表示移除该buff
        }
    }

    public static void handle(BuffStackSyncMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);

        if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) {
            return;
        }

        ctx.get().enqueueWork(() -> setData(msg));
    }

    private static void setData(BuffStackSyncMessage msg) {
        if (Minecraft.getInstance().level == null) {
            return;
        }

        Entity entity = Minecraft.getInstance().level.getEntity(msg.entityId);
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        livingEntity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(data -> {
            var registry = RecastingBuffTypes.REGISTRY.get();
            if (registry == null || msg.buffTypeKey == null) {
                return;
            }
            BuffType buffType = registry.getValue(msg.buffTypeKey);
            if (buffType == null) {
                return;
            }
            // 只更新单个条目
            if (msg.entry != null) {
                data.setEntry(buffType, msg.entry);
            } else {
                // entry为null表示移除该buff
                data.remove(buffType);
            }
        });
    }
}

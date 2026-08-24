package com.til.recasting.network;

import com.til.recasting.Recasting;
import com.til.recasting.capability.IBuffStackData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

/**
 * Buff叠加数据网络同步消息
 * 优化为只同步单个buff条目的更新
 */
public record BuffStackSyncMessage(
        int entityId,
        ResourceLocation buffTypeKey,
        @Nullable IBuffStackData.BuffEntry entry
) implements CustomPacketPayload {

    public static final Type<BuffStackSyncMessage> TYPE = new Type<>(Recasting.prefix("buff_stack_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BuffStackSyncMessage> STREAM_CODEC =
            StreamCodec.of(BuffStackSyncMessage::write, BuffStackSyncMessage::read);

    private static BuffStackSyncMessage read(RegistryFriendlyByteBuf buf) {
        int entityId = buf.readInt();
        ResourceLocation buffTypeKey = buf.readResourceLocation();
        IBuffStackData.BuffEntry entry = null;
        if (buf.readBoolean()) {
            int level = buf.readInt();
            long lastUpdateTime = buf.readLong();
            CompoundTag customData = buf.readBoolean() ? buf.readNbt() : null;
            entry = new IBuffStackData.BuffEntry(level, lastUpdateTime, customData);
        }
        return new BuffStackSyncMessage(entityId, buffTypeKey, entry);
    }

    private static void write(RegistryFriendlyByteBuf buf, BuffStackSyncMessage msg) {
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
            buf.writeBoolean(false);
        }
    }

    @Override
    public Type<BuffStackSyncMessage> type() {
        return TYPE;
    }

    public static void handle(BuffStackSyncMessage msg, IPayloadContext ctx) {
        // TODO(P4/P5): 客户端写入 BUFF_STACK_DATA Attachment
    }
}

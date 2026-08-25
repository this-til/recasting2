package com.til.recasting.network;

import com.til.recasting.Recasting;
import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.instance.BuffType;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

/**
 * Buff 叠加数据网络同步消息（单条目增量）。
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
        ctx.enqueueWork(() -> applyOnClient(msg));
    }

    private static void applyOnClient(BuffStackSyncMessage msg) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || msg.buffTypeKey == null) {
            return;
        }
        Entity entity = minecraft.level.getEntity(msg.entityId);
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        BuffType buffType = RecastingBuffTypes.REGISTRY.get(msg.buffTypeKey);
        if (buffType == null) {
            return;
        }
        IBuffStackData data = RecastingAttachments.buffStackData(livingEntity);
        if (msg.entry != null) {
            data.setEntry(buffType, msg.entry);
        } else {
            data.remove(buffType);
        }
    }
}

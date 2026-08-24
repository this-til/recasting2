package com.til.recasting.network;

import com.til.recasting.Recasting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 服务端 → 客户端：时之彼端日夜加速启停，并同步基准 dayTime。
 */
public record TimeBeyondAccelMessage(boolean active, int multiplier, long dayTime) implements CustomPacketPayload {

    public static final Type<TimeBeyondAccelMessage> TYPE = new Type<>(Recasting.prefix("time_beyond_accel"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TimeBeyondAccelMessage> STREAM_CODEC =
            StreamCodec.of(TimeBeyondAccelMessage::write, TimeBeyondAccelMessage::read);

    private static TimeBeyondAccelMessage read(RegistryFriendlyByteBuf buf) {
        boolean active = buf.readBoolean();
        int multiplier = buf.readVarInt();
        long dayTime = buf.readLong();
        return new TimeBeyondAccelMessage(active, multiplier, dayTime);
    }

    private static void write(RegistryFriendlyByteBuf buf, TimeBeyondAccelMessage msg) {
        buf.writeBoolean(msg.active);
        buf.writeVarInt(msg.multiplier);
        buf.writeLong(msg.dayTime);
    }

    @Override
    public Type<TimeBeyondAccelMessage> type() {
        return TYPE;
    }

    public static void handle(TimeBeyondAccelMessage msg, IPayloadContext ctx) {
        // TODO(P5): TimeBeyondClientTimeHandler.start/stop(...)
    }
}

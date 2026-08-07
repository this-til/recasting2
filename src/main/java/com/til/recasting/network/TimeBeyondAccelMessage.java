package com.til.recasting.network;

import com.til.recasting.client.handler.TimeBeyondClientTimeHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 → 客户端：时之彼端日夜加速启停，并同步基准 dayTime。
 */
public class TimeBeyondAccelMessage {

    private boolean active;
    private int multiplier;
    private long dayTime;

    public TimeBeyondAccelMessage() {
    }

    public TimeBeyondAccelMessage(boolean active, int multiplier, long dayTime) {
        this.active = active;
        this.multiplier = multiplier;
        this.dayTime = dayTime;
    }

    public static TimeBeyondAccelMessage decode(FriendlyByteBuf buf) {
        TimeBeyondAccelMessage msg = new TimeBeyondAccelMessage();
        msg.active = buf.readBoolean();
        msg.multiplier = buf.readVarInt();
        msg.dayTime = buf.readLong();
        return msg;
    }

    public static void encode(TimeBeyondAccelMessage msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.active);
        buf.writeVarInt(msg.multiplier);
        buf.writeLong(msg.dayTime);
    }

    public static void handle(TimeBeyondAccelMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
        if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) {
            return;
        }
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> {
                    if (msg.active) {
                        TimeBeyondClientTimeHandler.start(msg.multiplier, msg.dayTime);
                    } else {
                        TimeBeyondClientTimeHandler.stop(msg.dayTime);
                    }
                }
        ));
    }
}

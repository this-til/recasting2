package com.til.recasting.network;

import com.til.recasting.client.effect.LightningChainClientEffects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 → 客户端：闪电链折线段特效
 */
public class LightningChainMessage {

    private Vec3 start;
    private Vec3 end;
    private int color;
    private long seed;
    private int lifeTicks;

    public LightningChainMessage() {
    }

    public LightningChainMessage(Vec3 start, Vec3 end, int color, long seed, int lifeTicks) {
        this.start = start;
        this.end = end;
        this.color = color;
        this.seed = seed;
        this.lifeTicks = lifeTicks;
    }

    public static LightningChainMessage decode(FriendlyByteBuf buf) {
        LightningChainMessage msg = new LightningChainMessage();
        msg.start = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        msg.end = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        msg.color = buf.readInt();
        msg.seed = buf.readLong();
        msg.lifeTicks = buf.readVarInt();
        return msg;
    }

    public static void encode(LightningChainMessage msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.start.x);
        buf.writeDouble(msg.start.y);
        buf.writeDouble(msg.start.z);
        buf.writeDouble(msg.end.x);
        buf.writeDouble(msg.end.y);
        buf.writeDouble(msg.end.z);
        buf.writeInt(msg.color);
        buf.writeLong(msg.seed);
        buf.writeVarInt(msg.lifeTicks);
    }

    public static void handle(LightningChainMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
        if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) {
            return;
        }
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> LightningChainClientEffects.add(msg.start, msg.end, msg.color, msg.seed, msg.lifeTicks)
        ));
    }
}

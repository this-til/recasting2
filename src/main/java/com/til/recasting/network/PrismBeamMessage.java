package com.til.recasting.network;

import com.til.recasting.client.effect.PrismBeamClientEffects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 → 客户端：光棱线段特效
 */
public class PrismBeamMessage {

    private Vec3 start;
    private Vec3 end;
    private int color;
    private int lifeTicks;

    public PrismBeamMessage() {
    }

    public PrismBeamMessage(Vec3 start, Vec3 end, int color, int lifeTicks) {
        this.start = start;
        this.end = end;
        this.color = color;
        this.lifeTicks = lifeTicks;
    }

    public static PrismBeamMessage decode(FriendlyByteBuf buf) {
        PrismBeamMessage msg = new PrismBeamMessage();
        msg.start = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        msg.end = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        msg.color = buf.readInt();
        msg.lifeTicks = buf.readVarInt();
        return msg;
    }

    public static void encode(PrismBeamMessage msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.start.x);
        buf.writeDouble(msg.start.y);
        buf.writeDouble(msg.start.z);
        buf.writeDouble(msg.end.x);
        buf.writeDouble(msg.end.y);
        buf.writeDouble(msg.end.z);
        buf.writeInt(msg.color);
        buf.writeVarInt(msg.lifeTicks);
    }

    public static void handle(PrismBeamMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
        if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) {
            return;
        }
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> PrismBeamClientEffects.add(msg.start, msg.end, msg.color, msg.lifeTicks)
        ));
    }
}

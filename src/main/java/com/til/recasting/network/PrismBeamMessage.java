package com.til.recasting.network;

import com.til.recasting.client.effect.PrismBeamClientEffects;
import com.til.recasting.Recasting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 服务端 → 客户端：光棱线段特效
 */
public record PrismBeamMessage(Vec3 start, Vec3 end, int color, int lifeTicks) implements CustomPacketPayload {

    public static final Type<PrismBeamMessage> TYPE = new Type<>(Recasting.prefix("prism_beam"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PrismBeamMessage> STREAM_CODEC =
            StreamCodec.of(PrismBeamMessage::write, PrismBeamMessage::read);

    private static PrismBeamMessage read(RegistryFriendlyByteBuf buf) {
        Vec3 start = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        Vec3 end = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        int color = buf.readInt();
        int lifeTicks = buf.readVarInt();
        return new PrismBeamMessage(start, end, color, lifeTicks);
    }

    private static void write(RegistryFriendlyByteBuf buf, PrismBeamMessage msg) {
        buf.writeDouble(msg.start.x);
        buf.writeDouble(msg.start.y);
        buf.writeDouble(msg.start.z);
        buf.writeDouble(msg.end.x);
        buf.writeDouble(msg.end.y);
        buf.writeDouble(msg.end.z);
        buf.writeInt(msg.color);
        buf.writeVarInt(msg.lifeTicks);
    }

    @Override
    public Type<PrismBeamMessage> type() {
        return TYPE;
    }

    public static void handle(PrismBeamMessage msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> PrismBeamClientEffects.add(msg.start(), msg.end(), msg.color(), msg.lifeTicks()));
    }
}

package com.til.recasting.network;

import com.til.recasting.client.effect.LightningChainClientEffects;
import com.til.recasting.Recasting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 服务端 → 客户端：闪电链折线段特效
 */
public record LightningChainMessage(Vec3 start, Vec3 end, int color, long seed, int lifeTicks)
        implements CustomPacketPayload {

    public static final Type<LightningChainMessage> TYPE = new Type<>(Recasting.prefix("lightning_chain"));

    public static final StreamCodec<RegistryFriendlyByteBuf, LightningChainMessage> STREAM_CODEC =
            StreamCodec.of(LightningChainMessage::write, LightningChainMessage::read);

    private static LightningChainMessage read(RegistryFriendlyByteBuf buf) {
        Vec3 start = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        Vec3 end = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        int color = buf.readInt();
        long seed = buf.readLong();
        int lifeTicks = buf.readVarInt();
        return new LightningChainMessage(start, end, color, seed, lifeTicks);
    }

    private static void write(RegistryFriendlyByteBuf buf, LightningChainMessage msg) {
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

    @Override
    public Type<LightningChainMessage> type() {
        return TYPE;
    }

    public static void handle(LightningChainMessage msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> LightningChainClientEffects.add(
                msg.start(),
                msg.end(),
                msg.color(),
                msg.seed(),
                msg.lifeTicks()
        ));
    }
}

package com.til.recasting.network;

import com.til.recasting.Recasting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务端 → 客户端：末辉黑洞吞噬方块的起始信息（客户端本地渲染吸附，无实体）。
 */
public record FinalGlowIngestMessage(int holeEntityId, float absorbRadius, List<Entry> entries)
        implements CustomPacketPayload {

    public static final Type<FinalGlowIngestMessage> TYPE = new Type<>(Recasting.prefix("final_glow_ingest"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FinalGlowIngestMessage> STREAM_CODEC =
            StreamCodec.of(FinalGlowIngestMessage::write, FinalGlowIngestMessage::read);

    public record Entry(BlockState state, BlockPos pos) {
    }

    private static FinalGlowIngestMessage read(RegistryFriendlyByteBuf buf) {
        int holeEntityId = buf.readVarInt();
        float absorbRadius = buf.readFloat();
        int count = buf.readVarInt();
        List<Entry> entries = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int stateId = buf.readVarInt();
            BlockPos pos = buf.readBlockPos();
            entries.add(new Entry(Block.stateById(stateId), pos));
        }
        return new FinalGlowIngestMessage(holeEntityId, absorbRadius, entries);
    }

    private static void write(RegistryFriendlyByteBuf buf, FinalGlowIngestMessage msg) {
        buf.writeVarInt(msg.holeEntityId);
        buf.writeFloat(msg.absorbRadius);
        buf.writeVarInt(msg.entries.size());
        for (Entry entry : msg.entries) {
            buf.writeVarInt(Block.getId(entry.state()));
            buf.writeBlockPos(entry.pos());
        }
    }

    @Override
    public Type<FinalGlowIngestMessage> type() {
        return TYPE;
    }

    public static void handle(FinalGlowIngestMessage msg, IPayloadContext ctx) {
        // TODO(P5): FinalGlowIngestClientEffects.addBatch(...)
    }
}

package com.til.recasting.network;

import com.til.recasting.client.effect.FinalGlowIngestClientEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 服务端 → 客户端：末辉黑洞吞噬方块的起始信息（客户端本地渲染吸附，无实体）。
 */
public class FinalGlowIngestMessage {

    private int holeEntityId;
    private float absorbRadius;
    private List<Entry> entries = List.of();

    public FinalGlowIngestMessage() {
    }

    public FinalGlowIngestMessage(int holeEntityId, float absorbRadius, List<Entry> entries) {
        this.holeEntityId = holeEntityId;
        this.absorbRadius = absorbRadius;
        this.entries = entries;
    }

    public static FinalGlowIngestMessage decode(FriendlyByteBuf buf) {
        FinalGlowIngestMessage msg = new FinalGlowIngestMessage();
        msg.holeEntityId = buf.readVarInt();
        msg.absorbRadius = buf.readFloat();
        int count = buf.readVarInt();
        List<Entry> entries = new ArrayList<>(count);
        for(int i = 0; i < count; i++) {
            int stateId = buf.readVarInt();
            BlockPos pos = buf.readBlockPos();
            entries.add(new Entry(Block.stateById(stateId), pos));
        }
        msg.entries = entries;
        return msg;
    }

    public static void encode(FinalGlowIngestMessage msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.holeEntityId);
        buf.writeFloat(msg.absorbRadius);
        buf.writeVarInt(msg.entries.size());
        for(Entry entry : msg.entries) {
            buf.writeVarInt(Block.getId(entry.state()));
            buf.writeBlockPos(entry.pos());
        }
    }

    public static void handle(FinalGlowIngestMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
        if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) {
            return;
        }
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> FinalGlowIngestClientEffects.addBatch(msg.holeEntityId, msg.absorbRadius, msg.entries)
        ));
    }

    public record Entry(BlockState state, BlockPos pos) {
    }
}

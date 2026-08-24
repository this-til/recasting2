package com.til.recasting.network;

import com.til.recasting.inventory.ProudSoulBagMenu;
import com.til.recasting.item.ProudSoulBagStorage;
import com.til.recasting.Recasting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 耀魂背包存储内容同步（服务端 → 客户端）。
 */
public record ProudSoulBagSyncMessage(List<StoredEntry> entries) implements CustomPacketPayload {

    public static final Type<ProudSoulBagSyncMessage> TYPE = new Type<>(Recasting.prefix("proud_soul_bag_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProudSoulBagSyncMessage> STREAM_CODEC =
            StreamCodec.of(ProudSoulBagSyncMessage::write, ProudSoulBagSyncMessage::read);

    public record StoredEntry(ItemStack template, long count) {
    }

    private static ProudSoulBagSyncMessage read(RegistryFriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<StoredEntry> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ItemStack template = ItemStack.STREAM_CODEC.decode(buf);
            long count = buf.readVarLong();
            list.add(new StoredEntry(template, count));
        }
        return new ProudSoulBagSyncMessage(list);
    }

    private static void write(RegistryFriendlyByteBuf buf, ProudSoulBagSyncMessage msg) {
        buf.writeVarInt(msg.entries.size());
        for (StoredEntry entry : msg.entries) {
            ItemStack.STREAM_CODEC.encode(buf, entry.template());
            buf.writeVarLong(entry.count());
        }
    }

    @Override
    public Type<ProudSoulBagSyncMessage> type() {
        return TYPE;
    }

    public static void handle(ProudSoulBagSyncMessage msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player().containerMenu instanceof ProudSoulBagMenu menu)) {
                return;
            }
            List<ProudSoulBagStorage.StoredEntry> entries = msg.entries().stream()
                    .map(entry -> new ProudSoulBagStorage.StoredEntry(entry.template(), entry.count()))
                    .toList();
            menu.setClientEntries(entries);
        });
    }
}

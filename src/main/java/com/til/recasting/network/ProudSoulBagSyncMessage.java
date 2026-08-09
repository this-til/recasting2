package com.til.recasting.network;

import com.til.recasting.inventory.ProudSoulBagMenu;
import com.til.recasting.item.ProudSoulBagStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 耀魂背包存储内容同步（服务端 → 客户端），保证虚拟格与附魔等 NBT 及时可见。
 */
public class ProudSoulBagSyncMessage {

    private List<ProudSoulBagStorage.StoredEntry> entries = List.of();

    public ProudSoulBagSyncMessage() {
    }

    public ProudSoulBagSyncMessage(List<ProudSoulBagStorage.StoredEntry> entries) {
        this.entries = entries;
    }

    public static void encode(ProudSoulBagSyncMessage msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.entries.size());
        for(ProudSoulBagStorage.StoredEntry entry : msg.entries) {
            ProudSoulBagStorage.writeTemplate(buf, entry.template());
            buf.writeVarLong(entry.count());
        }
    }

    public static ProudSoulBagSyncMessage decode(FriendlyByteBuf buf) {
        ProudSoulBagSyncMessage msg = new ProudSoulBagSyncMessage();
        int size = buf.readVarInt();
        List<ProudSoulBagStorage.StoredEntry> list = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            ItemStack template = ProudSoulBagStorage.readTemplate(buf);
            long count = buf.readVarLong();
            list.add(new ProudSoulBagStorage.StoredEntry(template, count));
        }
        msg.entries = list;
        return msg;
    }

    public static void handle(ProudSoulBagSyncMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
        if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) {
            return;
        }
        ctx.get().enqueueWork(() -> applyClient(msg));
    }

    @OnlyIn(Dist.CLIENT)
    private static void applyClient(ProudSoulBagSyncMessage msg) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        if (!(minecraft.player.containerMenu instanceof ProudSoulBagMenu menu)) {
            return;
        }
        menu.setClientEntries(msg.entries);
    }
}

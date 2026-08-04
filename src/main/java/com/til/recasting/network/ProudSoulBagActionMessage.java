package com.til.recasting.network;

import com.til.recasting.inventory.ProudSoulBagMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 耀魂背包虚拟格操作（客户端 → 服务端）。
 */
public class ProudSoulBagActionMessage {

    private ProudSoulBagMenu.Action action;
    private int virtualIndex;

    public ProudSoulBagActionMessage() {
    }

    public ProudSoulBagActionMessage(ProudSoulBagMenu.Action action, int virtualIndex) {
        this.action = action;
        this.virtualIndex = virtualIndex;
    }

    public static void encode(ProudSoulBagActionMessage msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.action);
        buf.writeVarInt(msg.virtualIndex);
    }

    public static ProudSoulBagActionMessage decode(FriendlyByteBuf buf) {
        ProudSoulBagActionMessage msg = new ProudSoulBagActionMessage();
        msg.action = buf.readEnum(ProudSoulBagMenu.Action.class);
        msg.virtualIndex = buf.readVarInt();
        return msg;
    }

    public static void handle(ProudSoulBagActionMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                return;
            }
            if (!(player.containerMenu instanceof ProudSoulBagMenu menu)) {
                return;
            }
            menu.handleVirtualAction(player, msg.action, msg.virtualIndex);
        });
        ctx.get().setPacketHandled(true);
    }
}

package com.til.recasting.network;

import com.til.recasting.inventory.ProudSoulBagMenu;
import com.til.recasting.Recasting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 耀魂背包虚拟格操作（客户端 → 服务端）。
 */
public record ProudSoulBagActionMessage(Action action, int virtualIndex) implements CustomPacketPayload {

    public static final Type<ProudSoulBagActionMessage> TYPE = new Type<>(Recasting.prefix("proud_soul_bag_action"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProudSoulBagActionMessage> STREAM_CODEC =
            StreamCodec.of(ProudSoulBagActionMessage::write, ProudSoulBagActionMessage::read);

    public enum Action {
        PICKUP_OR_SET_DOWN,
        SPLIT_OR_PLACE_SINGLE,
        SHIFT_CLICK,
        ROLL_EXTRACT_ONE,
        ROLL_INSERT_ONE;

        public ProudSoulBagMenu.Action toMenuAction() {
            return ProudSoulBagMenu.Action.valueOf(name());
        }
    }

    private static ProudSoulBagActionMessage read(RegistryFriendlyByteBuf buf) {
        Action action = buf.readEnum(Action.class);
        int virtualIndex = buf.readVarInt();
        return new ProudSoulBagActionMessage(action, virtualIndex);
    }

    private static void write(RegistryFriendlyByteBuf buf, ProudSoulBagActionMessage msg) {
        buf.writeEnum(msg.action);
        buf.writeVarInt(msg.virtualIndex);
    }

    @Override
    public Type<ProudSoulBagActionMessage> type() {
        return TYPE;
    }

    public static void handle(ProudSoulBagActionMessage msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player().containerMenu instanceof ProudSoulBagMenu menu)) {
                return;
            }
            menu.handleVirtualAction(ctx.player(), msg.action().toMenuAction(), msg.virtualIndex());
        });
    }
}

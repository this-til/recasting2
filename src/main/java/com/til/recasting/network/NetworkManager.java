package com.til.recasting.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * 网络管理器：注册全部 CustomPacketPayload。
 */
public final class NetworkManager {

    private static final String PROTOCOL_VERSION = "1";

    private NetworkManager() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(BuffStackSyncMessage.TYPE, BuffStackSyncMessage.STREAM_CODEC, BuffStackSyncMessage::handle);
        registrar.playToClient(PrismBeamMessage.TYPE, PrismBeamMessage.STREAM_CODEC, PrismBeamMessage::handle);
        registrar.playToClient(LightningChainMessage.TYPE, LightningChainMessage.STREAM_CODEC, LightningChainMessage::handle);
        registrar.playToServer(ProudSoulBagActionMessage.TYPE, ProudSoulBagActionMessage.STREAM_CODEC, ProudSoulBagActionMessage::handle);
        registrar.playToClient(ProudSoulBagSyncMessage.TYPE, ProudSoulBagSyncMessage.STREAM_CODEC, ProudSoulBagSyncMessage::handle);
        registrar.playToClient(TimeBeyondAccelMessage.TYPE, TimeBeyondAccelMessage.STREAM_CODEC, TimeBeyondAccelMessage::handle);
        registrar.playToClient(FinalGlowIngestMessage.TYPE, FinalGlowIngestMessage.STREAM_CODEC, FinalGlowIngestMessage::handle);
    }
}

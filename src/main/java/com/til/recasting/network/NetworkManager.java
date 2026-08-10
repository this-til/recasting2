package com.til.recasting.network;

import com.til.recasting.Recasting;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * 网络管理器
 * 负责注册和管理所有网络消息
 */
public class NetworkManager {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            Recasting.prefix("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(
                id++,
                BuffStackSyncMessage.class,
                BuffStackSyncMessage::encode,
                BuffStackSyncMessage::decode,
                BuffStackSyncMessage::handle
        );
        INSTANCE.registerMessage(
                id++,
                PrismBeamMessage.class,
                PrismBeamMessage::encode,
                PrismBeamMessage::decode,
                PrismBeamMessage::handle
        );
        INSTANCE.registerMessage(
                id++,
                LightningChainMessage.class,
                LightningChainMessage::encode,
                LightningChainMessage::decode,
                LightningChainMessage::handle
        );
        INSTANCE.registerMessage(
                id++,
                ProudSoulBagActionMessage.class,
                ProudSoulBagActionMessage::encode,
                ProudSoulBagActionMessage::decode,
                ProudSoulBagActionMessage::handle
        );
        INSTANCE.registerMessage(
                id++,
                ProudSoulBagSyncMessage.class,
                ProudSoulBagSyncMessage::encode,
                ProudSoulBagSyncMessage::decode,
                ProudSoulBagSyncMessage::handle
        );
        INSTANCE.registerMessage(
                id++,
                TimeBeyondAccelMessage.class,
                TimeBeyondAccelMessage::encode,
                TimeBeyondAccelMessage::decode,
                TimeBeyondAccelMessage::handle
        );
        INSTANCE.registerMessage(
                id++,
                FinalGlowIngestMessage.class,
                FinalGlowIngestMessage::encode,
                FinalGlowIngestMessage::decode,
                FinalGlowIngestMessage::handle
        );
    }
}


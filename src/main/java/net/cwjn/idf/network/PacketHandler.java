package net.cwjn.idf.network;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.network.packets.*;
import net.cwjn.idf.network.packets.bonfire.ActivateBonfireMessage;
import net.cwjn.idf.network.packets.bonfire.OpenBonfireScreenMessage;
import net.cwjn.idf.network.packets.bonfire.SyncBonfireMessage;
import net.cwjn.idf.network.packets.config.SendServerDamageJsonMessage;
import net.cwjn.idf.network.packets.config.SendServerResistanceJsonMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        INSTANCE.registerMessage(0, SendServerResistanceJsonMessage.class, SendServerResistanceJsonMessage::encode, SendServerResistanceJsonMessage::decode, SendServerResistanceJsonMessage::handle);
        INSTANCE.registerMessage(1, SendServerDamageJsonMessage.class, SendServerDamageJsonMessage::encode, SendServerDamageJsonMessage::decode, SendServerDamageJsonMessage::handle);
        INSTANCE.registerMessage(2, DisplayDamageIndicatorsMessage.class, DisplayDamageIndicatorsMessage::encode, DisplayDamageIndicatorsMessage::decode, DisplayDamageIndicatorsMessage::handle);
        INSTANCE.registerMessage(3, OpenBonfireScreenMessage.class, OpenBonfireScreenMessage::encode, OpenBonfireScreenMessage::decode, OpenBonfireScreenMessage::handle);
        INSTANCE.registerMessage(4, SyncBonfireMessage.class, SyncBonfireMessage::encode, SyncBonfireMessage::decode, SyncBonfireMessage::handle);
        INSTANCE.registerMessage(5, ActivateBonfireMessage.class, ActivateBonfireMessage::encode, ActivateBonfireMessage::decode, ActivateBonfireMessage::handle);
    }

    public static void serverToPlayer(IDFPacket packet, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void playerToServer(IDFPacket packet) {
        INSTANCE.sendToServer(packet);
    }

    public static void serverToAll(IDFPacket packet) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
    }

}

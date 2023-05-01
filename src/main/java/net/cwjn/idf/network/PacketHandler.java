package net.cwjn.idf.network;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.network.packets.DisplayDamageIndicatorPacket;
import net.cwjn.idf.network.packets.SyncClientConfigPacket;
import net.cwjn.idf.network.packets.SyncSkyDarkenPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
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
        INSTANCE.registerMessage(0, SyncClientConfigPacket.class, SyncClientConfigPacket::encode, SyncClientConfigPacket::decode, SyncClientConfigPacket::handle);
        INSTANCE.registerMessage(1, DisplayDamageIndicatorPacket.class, DisplayDamageIndicatorPacket::encode, DisplayDamageIndicatorPacket::decode, DisplayDamageIndicatorPacket::handle);
        INSTANCE.registerMessage(2, SyncSkyDarkenPacket.class, SyncSkyDarkenPacket::encode, SyncSkyDarkenPacket::decode, SyncSkyDarkenPacket::handle);
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

    public static void serverToNearPoint(IDFPacket packet, double x, double y, double z, double r, ResourceKey<Level> dim) {
        INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, r, dim)), packet);
    }

}

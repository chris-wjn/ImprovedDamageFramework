package net.cwjn.idf.network.packets;

import net.cwjn.idf.data.CommonData;
import net.cwjn.idf.network.IDFPacket;
import net.cwjn.idf.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class RequestBestiaryEntriesPacket implements IDFPacket {

    private final UUID id;

    public RequestBestiaryEntriesPacket(UUID id) {
        this.id = id;
    }

    public static void encode(RequestBestiaryEntriesPacket message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.id);
    }

    public static RequestBestiaryEntriesPacket decode(FriendlyByteBuf buffer) {
        return new RequestBestiaryEntriesPacket(buffer.readUUID());
    }

    public static void handle(RequestBestiaryEntriesPacket message, Supplier<NetworkEvent.Context> ctxSupplier) {
        ctxSupplier.get().enqueueWork(() -> {
            PacketHandler.serverToPlayer(new SendBestiaryEntriesPacket(CommonData.BESTIARY_MAP.get(message.id)), ctxSupplier.get().getSender());
        });
        ctxSupplier.get().setPacketHandled(true);
    }

}

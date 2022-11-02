package net.cwjn.idf.network.packets.bonfire;

import net.cwjn.idf.event.ClientEventsForgeBus;
import net.cwjn.idf.network.IDFPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenBonfireScreenMessage implements IDFPacket {

    public OpenBonfireScreenMessage() {}

    public static void encode(OpenBonfireScreenMessage message, FriendlyByteBuf buffer) {}

    public static OpenBonfireScreenMessage decode(FriendlyByteBuf buffer) {
        return new OpenBonfireScreenMessage();
    }

    public static void handle(OpenBonfireScreenMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(ClientEventsForgeBus::openBonfireScreen);
        ctx.setPacketHandled(true);
    }

}

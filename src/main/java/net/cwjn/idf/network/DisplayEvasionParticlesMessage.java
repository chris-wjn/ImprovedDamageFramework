package net.cwjn.idf.network;

import net.cwjn.idf.event.hook.EvasionEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DisplayEvasionParticlesMessage {

    private double x, y, z;

    public DisplayEvasionParticlesMessage(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void encode(DisplayEvasionParticlesMessage message, FriendlyByteBuf buffer) {
        buffer.writeDouble(message.x);
        buffer.writeDouble(message.y);
        buffer.writeDouble(message.z);
    }

    public static DisplayEvasionParticlesMessage decode(FriendlyByteBuf buffer) {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        return new DisplayEvasionParticlesMessage(x, y, z);
    }

    public static void handle(DisplayEvasionParticlesMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        EvasionEvent.displayEvasionParticles(message.x, message.y, message.z);
        ctx.setPacketHandled(true);
    }

}

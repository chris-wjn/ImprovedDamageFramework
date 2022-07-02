package net.cwjn.idf.network;

import net.cwjn.idf.event.hook.DamageIndicatorEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class DisplayDamageIndicatorsMessage {

    private double x, y, z;
    private float f;
    private float[] colour;
    private static final Random random = new Random();

    public DisplayDamageIndicatorsMessage(double x, double y, double z, float f, float[] colour) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.f = f;
        this.colour = colour;
    }

    public static void encode(DisplayDamageIndicatorsMessage message, FriendlyByteBuf buffer) {
        buffer.writeDouble(message.x);
        buffer.writeDouble(message.y);
        buffer.writeDouble(message.z);
        buffer.writeFloat(message.f);
        buffer.writeFloat(message.colour[0]);
        buffer.writeFloat(message.colour[1]);
        buffer.writeFloat(message.colour[2]);
    }

    public static DisplayDamageIndicatorsMessage decode(FriendlyByteBuf buffer) {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        float f = buffer.readFloat();
        float[] colour = {buffer.readFloat(), buffer.readFloat(), buffer.readFloat()};
        return new DisplayDamageIndicatorsMessage(x, y, z, f, colour);
    }

    public static void handle(DisplayDamageIndicatorsMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        DamageIndicatorEvents.addDamageIndicatorParticle(message.x, message.y, message.z, message.f, random.nextDouble(2) - 1, 0, random.nextDouble(2) - 1, message.colour);
        ctx.setPacketHandled(true);
    }

}

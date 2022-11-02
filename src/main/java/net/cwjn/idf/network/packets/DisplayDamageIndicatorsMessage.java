package net.cwjn.idf.network.packets;

import net.cwjn.idf.event.DamageIndicatorEvents;
import net.cwjn.idf.network.IDFPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class DisplayDamageIndicatorsMessage implements IDFPacket {

    private double x, y, z;
    private float f;
    private UUID id;
    private int colour;
    private float horizontalOffset;
    private static final Random random = new Random();

    public DisplayDamageIndicatorsMessage(double x, double y, double z, float f, float h, int colour, UUID id) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.f = f;
        this.horizontalOffset = h;
        this.colour = colour;
        this.id = id;
    }

    public static void encode(DisplayDamageIndicatorsMessage message, FriendlyByteBuf buffer) {
        buffer.writeDouble(message.x);
        buffer.writeDouble(message.y);
        buffer.writeDouble(message.z);
        buffer.writeFloat(message.f);
        buffer.writeFloat(message.horizontalOffset);
        buffer.writeInt(message.colour);
        buffer.writeUUID(message.id);
    }

    public static DisplayDamageIndicatorsMessage decode(FriendlyByteBuf buffer) {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        float f = buffer.readFloat();
        float h = buffer.readFloat();
        int colour = buffer.readInt();
        UUID id = buffer.readUUID();
        return new DisplayDamageIndicatorsMessage(x, y, z, f, h, colour, id);
    }

    public static void handle(DisplayDamageIndicatorsMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            DamageIndicatorEvents.addDamageIndicatorParticle(message.x, message.y, message.z, message.f, message.colour, message.horizontalOffset, random.nextDouble(1, 1.2), message.id);
        });
        ctx.setPacketHandled(true);
    }

}

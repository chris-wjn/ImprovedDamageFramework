package net.cwjn.idf.network.packets;

import net.cwjn.idf.network.ClientPacketHandler;
import net.cwjn.idf.network.IDFPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class DisplayMissPacket implements IDFPacket {

    private double x, y, z;
    private UUID id;
    private float horizontalOffset;
    private static final Random random = new Random();

    public DisplayMissPacket(double x, double y, double z, float h, UUID id) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.horizontalOffset = h;
        this.id = id;
    }

    public static void encode(DisplayMissPacket message, FriendlyByteBuf buffer) {
        buffer.writeDouble(message.x);
        buffer.writeDouble(message.y);
        buffer.writeDouble(message.z);
        buffer.writeFloat(message.horizontalOffset);
        buffer.writeUUID(message.id);
    }

    public static DisplayMissPacket decode(FriendlyByteBuf buffer) {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        float h = buffer.readFloat();
        UUID id = buffer.readUUID();
        return new DisplayMissPacket(x, y, z, h, id);
    }

    public static void handle(DisplayMissPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.addMissParticle(message.x, message.y, message.z, message.horizontalOffset, random.nextDouble(1, 1.2), message.id));
        });
        ctx.setPacketHandled(true);
    }

}

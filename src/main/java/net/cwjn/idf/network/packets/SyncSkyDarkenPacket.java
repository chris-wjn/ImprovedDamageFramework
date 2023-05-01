package net.cwjn.idf.network.packets;

import net.cwjn.idf.network.ClientPacketHandler;
import net.cwjn.idf.network.IDFPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncSkyDarkenPacket implements IDFPacket {

    private final int darkness;

    public SyncSkyDarkenPacket(int b) {
        darkness = b;
    }

    public static void encode(SyncSkyDarkenPacket message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.darkness);
    }

    public static SyncSkyDarkenPacket decode(FriendlyByteBuf buffer) {
        return new SyncSkyDarkenPacket(buffer.readInt());
    }

    public static void handle(SyncSkyDarkenPacket message, Supplier<NetworkEvent.Context> ctxSupplier) {
        ctxSupplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.updateSkyDarken(message.darkness));
        });
        ctxSupplier.get().setPacketHandled(true);
    }

}

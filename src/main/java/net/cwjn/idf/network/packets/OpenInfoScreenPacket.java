package net.cwjn.idf.network.packets;

import net.cwjn.idf.network.ClientPacketHandler;
import net.cwjn.idf.network.IDFPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenInfoScreenPacket implements IDFPacket {

    public OpenInfoScreenPacket() {

    }

    public static void encode(OpenInfoScreenPacket message, FriendlyByteBuf buffer) {

    }

    public static OpenInfoScreenPacket decode(FriendlyByteBuf buffer) {
        return new OpenInfoScreenPacket();
    }

    public static void handle(OpenInfoScreenPacket message, Supplier<NetworkEvent.Context> ctxSupplier) {
        ctxSupplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandler::openInfoScreen);
        });
        ctxSupplier.get().setPacketHandled(true);
    }

}

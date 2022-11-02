package net.cwjn.idf.network.packets.bonfire;

import net.cwjn.idf.network.ClientPacketHandler;
import net.cwjn.idf.network.IDFPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncBonfireMessage implements IDFPacket {

    public int x;
    public int y;
    public int z;

    public SyncBonfireMessage(BlockPos pos) {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
    }

    public static void encode(SyncBonfireMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.x);
        buffer.writeInt(message.y);
        buffer.writeInt(message.z);
    }

    public static SyncBonfireMessage decode(FriendlyByteBuf buffer) {
        int x1 = buffer.readInt();
        int y1 = buffer.readInt();
        int z1 = buffer.readInt();
        return new SyncBonfireMessage(new BlockPos(x1, y1, z1));
    }

    public static void handle(SyncBonfireMessage message, Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            ClientPacketHandler.syncBonfire(message.x, message.y, message.z);
        });
        ctx.setPacketHandled(true);
    }

}

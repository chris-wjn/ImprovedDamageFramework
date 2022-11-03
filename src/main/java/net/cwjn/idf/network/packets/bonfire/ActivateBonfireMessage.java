package net.cwjn.idf.network.packets.bonfire;

import net.cwjn.idf.rpg.RpgPlayer;
import net.cwjn.idf.rpg.bonfire.BonfireBlock;
import net.cwjn.idf.rpg.bonfire.entity.BonfireBlockEntity;
import net.cwjn.idf.network.IDFPacket;
import net.cwjn.idf.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ActivateBonfireMessage implements IDFPacket {

    private UUID player;
    private String name;
    private int x, y, z;

    public ActivateBonfireMessage(UUID player, String name, int x, int y, int z) {
        this.player = player;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void encode(ActivateBonfireMessage message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.player);
        Util.writeString(message.name, buffer);
        buffer.writeInt(message.x);
        buffer.writeInt(message.y);
        buffer.writeInt(message.z);
    }

    public static ActivateBonfireMessage decode(FriendlyByteBuf buffer) {
        UUID id = buffer.readUUID();
        String returnName = Util.readString(buffer);
        int x1 = buffer.readInt();
        int y1 = buffer.readInt();
        int z1 = buffer.readInt();
        return new ActivateBonfireMessage(id, returnName, x1, y1, z1);
    }

    public static void handle(ActivateBonfireMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            BlockPos pos = new BlockPos(message.x, message.y, message.z);
            Player player = ctx.getSender();
            if (player != null) {
                BonfireBlockEntity be = (BonfireBlockEntity) player.level.getBlockEntity(pos);
                if (be != null) {
                    be.setActive(true);
                    be.setName(message.name);
                    be.setOwner(message.player);
                    RpgPlayer rpgPlayer = (RpgPlayer) player;
                    if (rpgPlayer.getBonfires().size() >= rpgPlayer.getMaxBonfires()) {
                        rpgPlayer.addBonfire(be.getId());
                    }
                    player.level.setBlock(pos, player.level.getBlockState(pos).setValue(BonfireBlock.ACTIVE, true), 2);
                }
            }
        });
        ctx.setPacketHandled(true);
    }

}

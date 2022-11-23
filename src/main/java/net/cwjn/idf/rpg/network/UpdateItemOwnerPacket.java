package net.cwjn.idf.rpg.network;

import net.cwjn.idf.network.IDFPacket;
import net.cwjn.idf.rpg.capability.RpgItemProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class UpdateItemOwnerPacket implements IDFPacket {

    private UUID id;
    private ItemStack itemStack;

    public UpdateItemOwnerPacket(UUID id, ItemStack itemStack) {
        this.id = id;
        this.itemStack = itemStack;
    }

    public static void encode(UpdateItemOwnerPacket message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.id);
        buffer.writeItemStack(message.itemStack, true);
        buffer.write
    }

    public static UpdateItemOwnerPacket decode(FriendlyByteBuf buffer) {
        return new UpdateItemOwnerPacket(buffer.readUUID(), buffer.readItem());
    }

    public static void handle(UpdateItemOwnerPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            message.itemStack.getCapability(RpgItemProvider.RPG_ITEM).ifPresent(h -> {
                h.setOwner();
            });
        });
        ctx.setPacketHandled(true);
    }

}

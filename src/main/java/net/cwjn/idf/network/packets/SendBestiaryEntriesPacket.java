package net.cwjn.idf.network.packets;

import net.cwjn.idf.network.ClientPacketHandler;
import net.cwjn.idf.network.IDFPacket;
import net.cwjn.idf.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Supplier;

public class SendBestiaryEntriesPacket implements IDFPacket {

    private final Collection<ResourceLocation> entityTypes;

    public SendBestiaryEntriesPacket(Collection<ResourceLocation> entities) {
        this.entityTypes = entities;
    }

    public static void encode(SendBestiaryEntriesPacket message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.entityTypes.size());
        for (ResourceLocation rl : message.entityTypes) {
            buffer.writeResourceLocation(rl);
        }
    }

    public static SendBestiaryEntriesPacket decode(FriendlyByteBuf buffer) {
        HashSet<ResourceLocation> ret = new HashSet<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            ret.add(buffer.readResourceLocation());
        }
        return new SendBestiaryEntriesPacket(ret);
    }

    public static void handle(SendBestiaryEntriesPacket message, Supplier<NetworkEvent.Context> ctxSupplier) {
        ctxSupplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.UpdateBestiaryEntries(message.entityTypes));
        });
        ctxSupplier.get().setPacketHandled(true);
    }

}

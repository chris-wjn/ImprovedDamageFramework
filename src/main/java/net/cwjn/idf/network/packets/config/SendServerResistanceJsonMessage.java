package net.cwjn.idf.network.packets.config;

import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.config.json.data.ResistanceData;
import net.cwjn.idf.network.IDFPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public class SendServerResistanceJsonMessage implements IDFPacket {

    Map<ResourceLocation, ResistanceData> map;

    public SendServerResistanceJsonMessage(Map<ResourceLocation, ResistanceData> map) {
        this.map = map;
    }

    public static void encode(SendServerResistanceJsonMessage message, FriendlyByteBuf buffer) { //lol?
        buffer.writeInt(message.map.size());
        for (Map.Entry<ResourceLocation, ResistanceData> entry : message.map.entrySet()) {
            buffer.writeResourceLocation(entry.getKey());
            ResistanceData data = entry.getValue();
            buffer.writeDouble(data.getFire());
            buffer.writeDouble(data.getWater());
            buffer.writeDouble(data.getLightning());
            buffer.writeDouble(data.getMagic());
            buffer.writeDouble(data.getDark());
            buffer.writeDouble(data.getArmour());
            buffer.writeDouble(data.getArmourToughness());
            buffer.writeDouble(data.getEvasion());
            buffer.writeDouble(data.getMaxHP());
            buffer.writeDouble(data.getMovespeed());
            buffer.writeDouble(data.getKnockbackRes());
            buffer.writeDouble(data.getLuck());
            buffer.writeDouble(data.getStrikeMult());
            buffer.writeDouble(data.getPierceMult());
            buffer.writeDouble(data.getSlashMult());
            buffer.writeDouble(data.getCrushMult());
            buffer.writeDouble(data.getGenericMult());
        }
    }

    public static SendServerResistanceJsonMessage decode(FriendlyByteBuf buffer) {
        Map<ResourceLocation, ResistanceData> returnMap = new HashMap<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            returnMap.put(buffer.readResourceLocation(), new ResistanceData(
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble()));
        }
        return new SendServerResistanceJsonMessage(returnMap);
    }

    public static void handle(SendServerResistanceJsonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            //JSONHandler.updateClientResistanceData(message.map);
        });
        ctx.setPacketHandled(true);
    }
}

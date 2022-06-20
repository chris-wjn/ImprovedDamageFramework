package net.cwjn.idf.network;

import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.config.json.ResistanceData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public class SendServerResistanceJsonMessage {

    Map<ResourceLocation, ResistanceData> map;
    public SendServerResistanceJsonMessage() {}
    public SendServerResistanceJsonMessage(Map<ResourceLocation, ResistanceData> map) {
        this.map = map;
    }
    public static void encode(SendServerResistanceJsonMessage message, FriendlyByteBuf buffer) { //lol?
        buffer.writeInt(message.map.size());
        for (Map.Entry<ResourceLocation, ResistanceData> entry : message.map.entrySet()) {
            buffer.writeResourceLocation(entry.getKey());
            ResistanceData data = entry.getValue();
            buffer.writeDouble(data.getResistanceValues()[0]);
            buffer.writeDouble(data.getResistanceValues()[1]);
            buffer.writeDouble(data.getResistanceValues()[2]);
            buffer.writeDouble(data.getResistanceValues()[3]);
            buffer.writeDouble(data.getResistanceValues()[4]);
            buffer.writeDouble(data.getAuxiliary()[0]);
            buffer.writeDouble(data.getAuxiliary()[1]);
            buffer.writeDouble(data.getAuxiliary()[2]);
            buffer.writeDouble(data.getAuxiliary()[3]);
            buffer.writeDouble(data.getAuxiliary()[4]);
            buffer.writeDouble(data.getMultipliers()[0]);
            buffer.writeDouble(data.getMultipliers()[1]);
            buffer.writeDouble(data.getMultipliers()[2]);
            buffer.writeDouble(data.getMultipliers()[3]);
            buffer.writeDouble(data.getMultipliers()[4]);
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
                    buffer.readDouble()));
        }
        return new SendServerResistanceJsonMessage(returnMap);
    }

    public static void handle(SendServerResistanceJsonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        JSONHandler.updateClientResistanceData(message.map);
        ctx.setPacketHandled(true);
    }

}
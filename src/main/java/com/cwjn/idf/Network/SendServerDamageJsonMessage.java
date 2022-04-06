package com.cwjn.idf.Network;

import com.cwjn.idf.Config.DamageData;
import com.cwjn.idf.Config.JSONHandler;
import com.cwjn.idf.Config.ResistanceData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public class SendServerDamageJsonMessage {

    Map<ResourceLocation, DamageData> map;
    public SendServerDamageJsonMessage() {};
    public SendServerDamageJsonMessage(Map<ResourceLocation, DamageData> map) {
        this.map = map;
    }
    public static void encode(SendServerDamageJsonMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.map.size());
        for (Map.Entry<ResourceLocation, DamageData> entry : message.map.entrySet()) {
            buffer.writeResourceLocation(entry.getKey());
            DamageData data = entry.getValue();
            buffer.writeDouble(data.getDamageValues()[0]);
            buffer.writeDouble(data.getDamageValues()[1]);
            buffer.writeDouble(data.getDamageValues()[2]);
            buffer.writeDouble(data.getDamageValues()[3]);
            buffer.writeDouble(data.getDamageValues()[4]);
            buffer.writeCharSequence(data.getDamageClass(), Charset.defaultCharset());
        }
    }
    public static SendServerDamageJsonMessage decode(FriendlyByteBuf buffer) {
        Map<ResourceLocation, DamageData> returnMap = new HashMap<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            returnMap.put(buffer.readResourceLocation(), new DamageData(
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readCharSequence(6, Charset.defaultCharset()).toString())); //we ensure all damage class strings are six characters.
                                                                                                //kind of a shit way to do this but this charsequence thing is trash so i dont care
        }
        return new SendServerDamageJsonMessage(returnMap);
    }

    public static void handle(SendServerDamageJsonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        JSONHandler.updateClientDamageData(message.map);
        ctx.setPacketHandled(true);
    }

}

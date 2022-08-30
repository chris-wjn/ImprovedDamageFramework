package net.cwjn.idf.network;

import net.cwjn.idf.config.json.data.DamageData;
import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public class SendServerDamageJsonMessage {

    Map<ResourceLocation, DamageData> map;
    public SendServerDamageJsonMessage() {}
    public SendServerDamageJsonMessage(Map<ResourceLocation, DamageData> map) {
        this.map = map;
    }
    public static void encode(SendServerDamageJsonMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.map.size());
        for (Map.Entry<ResourceLocation, DamageData> entry : message.map.entrySet()) {
            buffer.writeResourceLocation(entry.getKey());
            DamageData data = entry.getValue();
            buffer.writeInt(data.getDurability());
            buffer.writeDouble(data.getFire());
            buffer.writeDouble(data.getWater());
            buffer.writeDouble(data.getLightning());
            buffer.writeDouble(data.getMagic());
            buffer.writeDouble(data.getDark());
            buffer.writeDouble(data.getAttackDamage());
            buffer.writeDouble(data.getSpeed());
            Util.writeString(data.getDamageClass(), buffer);
            buffer.writeDouble(data.getLifesteal());
            buffer.writeDouble(data.getArmourPenetration());
            buffer.writeDouble(data.getWeight());
            buffer.writeDouble(data.getCritChance());
        }
    }
    public static SendServerDamageJsonMessage decode(FriendlyByteBuf buffer) {
        Map<ResourceLocation, DamageData> returnMap = new HashMap<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            returnMap.put(buffer.readResourceLocation() , new DamageData(
                    buffer.readInt(),
                    buffer.readDouble(), //fire
                    buffer.readDouble(), //water
                    buffer.readDouble(), //lightning
                    buffer.readDouble(), //magic
                    buffer.readDouble(), //dark
                    buffer.readDouble(), //attack damage
                    buffer.readDouble(),
                    Util.readString(buffer),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble()));
        }
        return new SendServerDamageJsonMessage(returnMap);
    }

    public static void handle(SendServerDamageJsonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            JSONHandler.updateClientDamageData(message.map);
        });
        //JSONHandler.updateClientDamageData(message.map);
        ctx.setPacketHandled(true);
    }

}

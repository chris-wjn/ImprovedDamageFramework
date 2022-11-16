package net.cwjn.idf.network.packets.config;

import net.cwjn.idf.config.json.data.ArmourData;
import net.cwjn.idf.config.json.data.ItemData;
import net.cwjn.idf.config.json.data.WeaponData;
import net.cwjn.idf.network.IDFPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class SyncClientConfigPacket implements IDFPacket {

    Map<ResourceLocation, WeaponData> weaponMap0;
    Map<ResourceLocation, ItemData> weaponMap1;
    Map<ResourceLocation, ItemData> weaponMap2;
    Map<ResourceLocation, ArmourData> armourMap0;
    Map<ResourceLocation, ItemData> armourMap1;
    Map<ResourceLocation, ItemData> armourMap2;

    public SyncClientConfigPacket(Map<ResourceLocation, WeaponData> weaponMap0, Map<ResourceLocation, ItemData> weaponMap1, Map<ResourceLocation, ItemData> weaponMap2,
                                  Map<ResourceLocation, ArmourData> armourMap0, Map<ResourceLocation, ItemData> armourMap1, Map<ResourceLocation, ItemData> armourMap2) {
        this.weaponMap0 = weaponMap0;
        this.weaponMap1 = weaponMap1;
        this.weaponMap2 = weaponMap2;
        this.armourMap0 = armourMap0;
        this.armourMap1 = armourMap1;
        this.armourMap2 = armourMap2;
    }

    public static void encode(SyncClientConfigPacket packet, FriendlyByteBuf buffer) {
        buffer.writeMap(packet.weaponMap0, FriendlyByteBuf::readResourceLocation, );
        buffer.writeMap(packet.weaponMap1);
        buffer.writeMap(packet.weaponMap2);
        buffer.writeMap(packet.armourMap0);
        buffer.writeMap(packet.armourMap1);
        buffer.writeMap(packet.armourMap2);
    }

    public static SyncClientConfigPacket decode(FriendlyByteBuf buffer) {

    }

    public static void handle(SyncClientConfigPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {

    }

}

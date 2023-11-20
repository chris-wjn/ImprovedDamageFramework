package net.cwjn.idf.network.packets;

import net.cwjn.idf.config.json.records.ArmourData;
import net.cwjn.idf.config.json.records.ItemData;
import net.cwjn.idf.config.json.records.PresetData;
import net.cwjn.idf.config.json.records.WeaponData;
import net.cwjn.idf.network.ClientPacketHandler;
import net.cwjn.idf.network.IDFPacket;
import net.cwjn.idf.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SyncClientConfigPacket implements IDFPacket {

    public final Map<ResourceLocation, WeaponData> weaponFlat;
    public final Map<ResourceLocation, ItemData> weaponMult;
    public final Map<ResourceLocation, ArmourData> armourFlat;
    public final Map<ResourceLocation, ItemData> armourMult;
    public final Map<String, PresetData> presets;
    public final List<ResourceLocation> compatItems;
    public final List<String> compatMods;

    public SyncClientConfigPacket(Map<ResourceLocation, WeaponData> weaponMap0, Map<ResourceLocation, ItemData> weaponMap2,
                                  Map<ResourceLocation, ArmourData> armourMap0, Map<ResourceLocation, ItemData> armourMap2,
                                  Map<String, PresetData> presets, List<ResourceLocation> compatItems, List<String> compatMods) {
        this.weaponFlat = weaponMap0;
        this.weaponMult = weaponMap2;
        this.armourFlat = armourMap0;
        this.armourMult = armourMap2;
        this.presets = presets;
        this.compatItems = compatItems;
        this.compatMods = compatMods;
    }

    public static void encode(SyncClientConfigPacket packet, FriendlyByteBuf buffer) {
        buffer.writeMap(packet.weaponFlat, FriendlyByteBuf::writeResourceLocation, (buf, weaponData) -> weaponData.writeData(buf));
        buffer.writeMap(packet.weaponMult, FriendlyByteBuf::writeResourceLocation, (buf, itemData) -> itemData.writeData(buf));
        buffer.writeMap(packet.armourFlat, FriendlyByteBuf::writeResourceLocation, (buf, armourData) -> armourData.writeData(buf));
        buffer.writeMap(packet.armourMult, FriendlyByteBuf::writeResourceLocation, (buf, itemData) -> itemData.writeData(buf));
        buffer.writeMap(packet.presets, (buf, s) -> Util.writeString(s, buf), (buf, presetData) -> presetData.writeData(buf));
        buffer.writeInt(packet.compatItems.size());
        for (ResourceLocation rl : packet.compatItems) {
            buffer.writeResourceLocation(rl);
        }
        buffer.writeInt(packet.compatMods.size());
        for (String s : packet.compatMods) {
            Util.writeString(s, buffer);
        }
    }

    public static SyncClientConfigPacket decode(FriendlyByteBuf buffer) {
        Map<ResourceLocation, WeaponData> weaponMap00 = buffer.readMap(FriendlyByteBuf::readResourceLocation, WeaponData::readWeaponData);
        Map<ResourceLocation, ItemData> weaponMap22 = buffer.readMap(FriendlyByteBuf::readResourceLocation, ItemData::readData);
        Map<ResourceLocation, ArmourData> armourMap00 = buffer.readMap(FriendlyByteBuf::readResourceLocation, ArmourData::readArmourData);
        Map<ResourceLocation, ItemData> armourMap22 = buffer.readMap(FriendlyByteBuf::readResourceLocation, ItemData::readData);
        Map<String, PresetData> presets = buffer.readMap(Util::readString, PresetData::readData);
        int size = buffer.readInt();
        List<ResourceLocation> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(buffer.readResourceLocation());
        }
        int size1 = buffer.readInt();
        List<String> list1 = new ArrayList<>();
        for (int n = 0; n < size1; n++) {
            list1.add(Util.readString(buffer));
        }
        return new SyncClientConfigPacket(weaponMap00, weaponMap22, armourMap00, armourMap22, presets, list, list1);
    }

    public static void handle(SyncClientConfigPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.syncClientConfig(packet));
        });
        ctx.setPacketHandled(true);
    }

}

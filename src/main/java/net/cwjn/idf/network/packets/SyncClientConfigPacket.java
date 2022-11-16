package net.cwjn.idf.network.packets;

import net.cwjn.idf.config.json.data.ArmourData;
import net.cwjn.idf.config.json.data.ItemData;
import net.cwjn.idf.config.json.data.WeaponData;
import net.cwjn.idf.network.ClientPacketHandler;
import net.cwjn.idf.network.IDFPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class SyncClientConfigPacket implements IDFPacket {

    public final Map<ResourceLocation, WeaponData> weaponMap0;
    public final Map<ResourceLocation, ItemData> weaponMap1;
    public final Map<ResourceLocation, ItemData> weaponMap2;
    public final Map<ResourceLocation, ArmourData> armourMap0;
    public final Map<ResourceLocation, ItemData> armourMap1;
    public final Map<ResourceLocation, ItemData> armourMap2;

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
        buffer.writeMap(packet.weaponMap0, FriendlyByteBuf::writeResourceLocation, (buf, WeaponData) -> WeaponData.writeWeaponData(buf));
        buffer.writeMap(packet.weaponMap1, FriendlyByteBuf::writeResourceLocation, (buf, ItemData) -> ItemData.writeItemData(buf));
        buffer.writeMap(packet.weaponMap2, FriendlyByteBuf::writeResourceLocation, (buf, ItemData) -> ItemData.writeItemData(buf));
        buffer.writeMap(packet.armourMap0, FriendlyByteBuf::writeResourceLocation, (buf, ArmourData) -> ArmourData.writeArmourData(buf));
        buffer.writeMap(packet.armourMap1, FriendlyByteBuf::writeResourceLocation, (buf, ItemData) -> ItemData.writeItemData(buf));
        buffer.writeMap(packet.armourMap2, FriendlyByteBuf::writeResourceLocation, (buf, ItemData) -> ItemData.writeItemData(buf));
    }

    public static SyncClientConfigPacket decode(FriendlyByteBuf buffer) {
        Map<ResourceLocation, WeaponData> weaponMap00 = buffer.readMap(FriendlyByteBuf::readResourceLocation, WeaponData::readWeaponData);
        Map<ResourceLocation, ItemData> weaponMap11 = buffer.readMap(FriendlyByteBuf::readResourceLocation, ItemData::readItemData);
        Map<ResourceLocation, ItemData> weaponMap22 = buffer.readMap(FriendlyByteBuf::readResourceLocation, ItemData::readItemData);
        Map<ResourceLocation, ArmourData> armourMap00 = buffer.readMap(FriendlyByteBuf::readResourceLocation, ArmourData::readArmourData);
        Map<ResourceLocation, ItemData> armourMap11 = buffer.readMap(FriendlyByteBuf::readResourceLocation, ItemData::readItemData);
        Map<ResourceLocation, ItemData> armourMap22 = buffer.readMap(FriendlyByteBuf::readResourceLocation, ItemData::readItemData);
        return new SyncClientConfigPacket(weaponMap00, weaponMap11, weaponMap22, armourMap00, armourMap11, armourMap22);
    }

    public static void handle(SyncClientConfigPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.syncClientConfig(packet));
        });
        ctx.setPacketHandled(true);
    }

}

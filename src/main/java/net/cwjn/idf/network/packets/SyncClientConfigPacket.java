package net.cwjn.idf.network.packets;

import net.cwjn.idf.config.json.records.ArmourData;
import net.cwjn.idf.config.json.records.ItemData;
import net.cwjn.idf.config.json.records.WeaponData;
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

    public final Map<ResourceLocation, WeaponData> weaponFlat;
    public final Map<ResourceLocation, ItemData> weaponMult;
    public final Map<ResourceLocation, ArmourData> armourFlat;
    public final Map<ResourceLocation, ItemData> armourMult;

    public SyncClientConfigPacket(Map<ResourceLocation, WeaponData> weaponMap0, Map<ResourceLocation, ItemData> weaponMap2,
                                  Map<ResourceLocation, ArmourData> armourMap0, Map<ResourceLocation, ItemData> armourMap2) {
        this.weaponFlat = weaponMap0;
        this.weaponMult = weaponMap2;
        this.armourFlat = armourMap0;
        this.armourMult = armourMap2;
    }

    public static void encode(SyncClientConfigPacket packet, FriendlyByteBuf buffer) {
        buffer.writeMap(packet.weaponFlat, FriendlyByteBuf::writeResourceLocation, (buf, WeaponData) -> WeaponData.writeWeaponData(buf));
        buffer.writeMap(packet.weaponMult, FriendlyByteBuf::writeResourceLocation, (buf, ItemData) -> ItemData.writeItemData(buf));
        buffer.writeMap(packet.armourFlat, FriendlyByteBuf::writeResourceLocation, (buf, ArmourData) -> ArmourData.writeArmourData(buf));
        buffer.writeMap(packet.armourMult, FriendlyByteBuf::writeResourceLocation, (buf, ItemData) -> ItemData.writeItemData(buf));
    }

    public static SyncClientConfigPacket decode(FriendlyByteBuf buffer) {
        Map<ResourceLocation, WeaponData> weaponMap00 = buffer.readMap(FriendlyByteBuf::readResourceLocation, WeaponData::readWeaponData);
        Map<ResourceLocation, ItemData> weaponMap22 = buffer.readMap(FriendlyByteBuf::readResourceLocation, ItemData::readItemData);
        Map<ResourceLocation, ArmourData> armourMap00 = buffer.readMap(FriendlyByteBuf::readResourceLocation, ArmourData::readArmourData);
        Map<ResourceLocation, ItemData> armourMap22 = buffer.readMap(FriendlyByteBuf::readResourceLocation, ItemData::readItemData);
        return new SyncClientConfigPacket(weaponMap00, weaponMap22, armourMap00, armourMap22);
    }

    public static void handle(SyncClientConfigPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.syncClientConfig(packet));
        });
        ctx.setPacketHandled(true);
    }

}

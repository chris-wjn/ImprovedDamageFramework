package net.cwjn.idf.data;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.config.json.JSONUtil;
import net.cwjn.idf.config.json.records.ArmourData;
import net.cwjn.idf.config.json.records.ItemData;
import net.cwjn.idf.config.json.records.WeaponData;
import net.cwjn.idf.network.PacketHandler;
import net.cwjn.idf.network.packets.SyncClientConfigPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static net.cwjn.idf.data.CommonData.*;

@OnlyIn(Dist.DEDICATED_SERVER)
public class ServerData {

    public static void register(IEventBus bus) {
        bus.addListener(ServerData::onPlayerLoginEventServer);
        bus.addListener(ServerData::onServerShutdown);
    }

    //Here we send our config files to clients who join the server
    public static void onPlayerLoginEventServer(PlayerEvent.PlayerLoggedInEvent event) {
        PacketHandler.serverToPlayer(
                new SyncClientConfigPacket(LOGICAL_WEAPON_MAP_FLAT, LOGICAL_WEAPON_MAP_MULT, LOGICAL_ARMOUR_MAP_FLAT, LOGICAL_ARMOUR_MAP_MULT, COMPAT_ITEMS),
                (ServerPlayer) event.getEntity());
        ImprovedDamageFramework.LOGGER.info("Sent server mappings to player " + event.getEntity().getScoreboardName());
    }

    //write maps to config files on dedicated server shutdown
    public static void onServerShutdown(ServerStoppedEvent event) {
        SortedMap<String, ArmourData> sortedArmourOp0Map = new TreeMap<>();
        for (Map.Entry<ResourceLocation, ArmourData> entry : LOGICAL_ARMOUR_MAP_FLAT.entrySet()) {
            sortedArmourOp0Map.put(entry.getKey().toString(), entry.getValue());
        }
        SortedMap<String, ItemData> sortedArmourOp2Map = new TreeMap<>();
        for (Map.Entry<ResourceLocation, ItemData> entry : LOGICAL_ARMOUR_MAP_MULT.entrySet()) {
            sortedArmourOp2Map.put(entry.getKey().toString(), entry.getValue());
        }
        SortedMap<String, WeaponData> sortedWeaponOp0Map = new TreeMap<>();
        for (Map.Entry<ResourceLocation, WeaponData> entry : LOGICAL_WEAPON_MAP_FLAT.entrySet()) {
            sortedWeaponOp0Map.put(entry.getKey().toString(), entry.getValue());
        }
        SortedMap<String, ItemData> sortedWeaponOp2Map = new TreeMap<>();
        for (Map.Entry<ResourceLocation, ItemData> entry : LOGICAL_WEAPON_MAP_MULT.entrySet()) {
            sortedWeaponOp2Map.put(entry.getKey().toString(), entry.getValue());
        }
        File configDir = Paths.get(FMLPaths.CONFIGDIR.get().toAbsolutePath().toString(), "ImprovedDamageFramework").toFile();
        JSONUtil.writeFile(new File(configDir, "armour_items_flat.json"), sortedArmourOp0Map);
        JSONUtil.writeFile(new File(configDir, "armour_items_multiply.json"), sortedArmourOp2Map);
        JSONUtil.writeFile(new File(configDir, "weapon_items_flat.json"), sortedWeaponOp0Map);
        JSONUtil.writeFile(new File(configDir, "weapon_items_multiply.json"), sortedWeaponOp2Map);
    }

}

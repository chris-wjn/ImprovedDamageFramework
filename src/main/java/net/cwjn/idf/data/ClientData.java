package net.cwjn.idf.data;

import net.cwjn.idf.config.json.JSONUtil;
import net.cwjn.idf.config.json.records.ArmourData;
import net.cwjn.idf.config.json.records.ItemData;
import net.cwjn.idf.config.json.records.WeaponData;
import net.minecraft.resources.ResourceLocation;
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

import static net.cwjn.idf.config.json.JSONHandler.updateItems;
import static net.cwjn.idf.data.CommonData.*;

@OnlyIn(Dist.CLIENT)
public class ClientData {
    public static Map<ResourceLocation, ArmourData> CLIENT_ARMOUR_MAP_FLAT;
    public static Map<ResourceLocation, ItemData> CLIENT_ARMOUR_MAP_MULT;
    public static Map<ResourceLocation, WeaponData> CLIENT_WEAPON_MAP_FLAT;
    public static Map<ResourceLocation, ItemData> CLIENT_WEAPON_MAP_MULT;
    public static int skyDarken = 0;
    public static boolean shadersLoaded = false;

    public static void register(IEventBus bus) {
        bus.addListener(ClientData::onPlayerLoginEventClient);
        bus.addListener(ClientData::onShutDownEvent);
    }

    public static void saveClientMappings() {
        CLIENT_ARMOUR_MAP_FLAT = LOGICAL_ARMOUR_MAP_FLAT;
        CLIENT_ARMOUR_MAP_MULT = LOGICAL_ARMOUR_MAP_MULT;
        CLIENT_WEAPON_MAP_FLAT = LOGICAL_WEAPON_MAP_FLAT;
        CLIENT_WEAPON_MAP_MULT = LOGICAL_WEAPON_MAP_MULT;
    }

    //This works kind of weirdly; PlayerLoggedInEvent only fires on the logical server (afaik),
    //by calling it only on a physical client, it ensures that if this event is called, it is ONLY
    //when the logical server is also on the physical client.
    //In other words, this method will only fire when joining a single-player world.
    //This method will return the maps to the client's configs, if they've been updated by a server.
    public static void onPlayerLoginEventClient(PlayerEvent.PlayerLoggedInEvent event) {
        LOGICAL_ARMOUR_MAP_FLAT = CLIENT_ARMOUR_MAP_FLAT;
        LOGICAL_ARMOUR_MAP_MULT = CLIENT_ARMOUR_MAP_MULT;
        LOGICAL_WEAPON_MAP_FLAT = CLIENT_WEAPON_MAP_FLAT;
        LOGICAL_WEAPON_MAP_MULT = CLIENT_WEAPON_MAP_MULT;
        updateItems();
    }

    //When server shuts down, we write our maps to the config files in the case they've been update by the in-game command.
    public static void onShutDownEvent(ServerStoppedEvent event) {
        if (!event.getServer().isDedicatedServer()) {
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

}
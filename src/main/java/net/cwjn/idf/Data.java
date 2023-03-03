package net.cwjn.idf;

import net.cwjn.idf.config.json.data.*;
import net.cwjn.idf.config.json.data.subtypes.AuxiliaryData;
import net.cwjn.idf.config.json.data.subtypes.DefensiveData;
import net.cwjn.idf.config.json.data.subtypes.OffensiveData;
import net.cwjn.idf.network.PacketHandler;
import net.cwjn.idf.network.packets.SyncClientConfigPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

import static net.cwjn.idf.Data.LogicalData.*;
import static net.cwjn.idf.config.json.JSONHandler.updateItems;

public class Data {

    //This inner class contains data that needs to be usable by the logical server
    public static class LogicalData {
        public static Map<ResourceLocation, ArmourData> LOGICAL_ARMOUR_MAP_OP_0 = new HashMap<>();
        public static Map<ResourceLocation, ItemData> LOGICAL_ARMOUR_MAP_OP_1 = new HashMap<>();
        public static Map<ResourceLocation, ItemData> LOGICAL_ARMOUR_MAP_OP_2 = new HashMap<>();
        public static Map<ResourceLocation, WeaponData> LOGICAL_WEAPON_MAP_OP_0 = new HashMap<>();
        public static Map<ResourceLocation, ItemData> LOGICAL_WEAPON_MAP_OP_1 = new HashMap<>();
        public static Map<ResourceLocation, ItemData> LOGICAL_WEAPON_MAP_OP_2 = new HashMap<>();
        public static Map<ResourceLocation, EntityData> LOGICAL_ENTITY_MAP = new HashMap<>();
        public static Map<String, SourceCatcherData> LOGICAL_SOURCE_MAP = new HashMap<>();

        public static EntityData getEntityData(ResourceLocation key) {
            EntityData data = LOGICAL_ENTITY_MAP.getOrDefault(key, null);
            if (data == null) return null;
            return new EntityData(null, data.damageClass(),
                    OffensiveData.combine(data.oData(), data.template().getOffensiveData()),
                    DefensiveData.combine(data.dData(), data.template().getDefensiveData()),
                    AuxiliaryData.combine(data.aData(), data.template().getAuxiliaryData()));
        }

    }

    //This inner class contains data that should only be on physical clients, i.e. NOT dedicated servers
    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber
    public static class ClientData {
        public static Map<ResourceLocation, ArmourData> CLIENT_ARMOUR_MAP_OP_0;
        public static Map<ResourceLocation, ItemData> CLIENT_ARMOUR_MAP_OP_1;
        public static Map<ResourceLocation, ItemData> CLIENT_ARMOUR_MAP_OP_2;
        public static Map<ResourceLocation, WeaponData> CLIENT_WEAPON_MAP_OP_0;
        public static Map<ResourceLocation, ItemData> CLIENT_WEAPON_MAP_OP_1;
        public static Map<ResourceLocation, ItemData> CLIENT_WEAPON_MAP_OP_2;

        public static void saveClientMappings() {
            CLIENT_ARMOUR_MAP_OP_0 = LOGICAL_ARMOUR_MAP_OP_0;
            CLIENT_ARMOUR_MAP_OP_1 = LOGICAL_ARMOUR_MAP_OP_1;
            CLIENT_ARMOUR_MAP_OP_2 = LOGICAL_ARMOUR_MAP_OP_2;
            CLIENT_WEAPON_MAP_OP_0 = LOGICAL_WEAPON_MAP_OP_0;
            CLIENT_WEAPON_MAP_OP_1 = LOGICAL_WEAPON_MAP_OP_1;
            CLIENT_WEAPON_MAP_OP_2 = LOGICAL_WEAPON_MAP_OP_2;
        }

        //This works kind of weirdly; PlayerLoggedInEvent only fires on the logical server (afaik),
        //by calling it only on a physical client, it ensures that if this event is called, it is ONLY
        //when the logical server is also on the physical client.
        //In other words, this method will only fire when joining a single-player world.
        @SubscribeEvent
        public static void onPlayerLoginEventClient(PlayerEvent.PlayerLoggedInEvent event) {
            LOGICAL_ARMOUR_MAP_OP_0 = CLIENT_ARMOUR_MAP_OP_0;
            LOGICAL_ARMOUR_MAP_OP_1 = CLIENT_ARMOUR_MAP_OP_1;
            LOGICAL_ARMOUR_MAP_OP_2 = CLIENT_ARMOUR_MAP_OP_2;
            LOGICAL_WEAPON_MAP_OP_0 = CLIENT_WEAPON_MAP_OP_0;
            LOGICAL_WEAPON_MAP_OP_1 = CLIENT_WEAPON_MAP_OP_1;
            LOGICAL_WEAPON_MAP_OP_2 = CLIENT_WEAPON_MAP_OP_2;
            updateItems();
        }

    }

    //This inner class contains data that should only be on a physical/dedicated server
    @OnlyIn(Dist.DEDICATED_SERVER)
    @Mod.EventBusSubscriber
    public static class ServerData {

        //This method, which fires (afaik) only on the logical server, will onl
        @SubscribeEvent
        public static void onPlayerLoginEventServer(PlayerEvent.PlayerLoggedInEvent event) {
            PacketHandler.serverToPlayer(
                    new SyncClientConfigPacket(LOGICAL_WEAPON_MAP_OP_0, LOGICAL_WEAPON_MAP_OP_1, LOGICAL_WEAPON_MAP_OP_2, LOGICAL_ARMOUR_MAP_OP_0, LOGICAL_ARMOUR_MAP_OP_1, LOGICAL_ARMOUR_MAP_OP_2),
                    (ServerPlayer) event.getEntity());
            ImprovedDamageFramework.LOGGER.info("Sent server mappings to player " + event.getEntity().getScoreboardName());
        }

    }

}

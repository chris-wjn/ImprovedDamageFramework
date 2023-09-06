package net.cwjn.idf.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.cwjn.idf.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.UUID;

public class BestiaryData extends SavedData {

    private Multimap<UUID, ResourceLocation> PLAYER_ENTITIES_KILLED_MAP = HashMultimap.create();
    public static BestiaryData create() {
        return new BestiaryData();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        PLAYER_ENTITIES_KILLED_MAP.asMap()
                .forEach((uuid, entityTypeCollection) -> entityTypeCollection
                .forEach(entityType -> tag.putUUID(entityType.toString(), uuid)));
        return tag;
    }

    public static BestiaryData load(CompoundTag tag) {
        BestiaryData data = create();
        tag.getAllKeys().forEach(key -> data.PLAYER_ENTITIES_KILLED_MAP.put(tag.getUUID(key), ResourceLocation.tryParse(key))
        );
        return data;
    }

    public static BestiaryData getBestiaryData(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(BestiaryData::load, BestiaryData::create, "idf_bestiary_data");
    }

    public static void loadBestiaryData(MinecraftServer server) {
        CommonData.BESTIARY_MAP = server.overworld().getDataStorage().computeIfAbsent(BestiaryData::load, BestiaryData::create, "idf_bestiary_data").PLAYER_ENTITIES_KILLED_MAP;
    }

    public static void saveBestiaryData(MinecraftServer server) {
        CommonData.BESTIARY_MAP.asMap()
                .forEach((uuid, entityTypeCollection) -> entityTypeCollection
                        .forEach(entityType ->
                                server.overworld().getDataStorage().computeIfAbsent(BestiaryData::load, BestiaryData::create, "idf_bestiary_data").PLAYER_ENTITIES_KILLED_MAP.put(uuid, entityType)));
        getBestiaryData(server).setDirty();
        //Don't need to use putIfAbsent because HashMultimaps don't allow duplicate pairs.
    }

}

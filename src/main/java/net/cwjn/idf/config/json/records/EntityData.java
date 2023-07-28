package net.cwjn.idf.config.json.records;

import com.google.gson.*;
import net.cwjn.idf.config.json.records.subtypes.AuxiliaryData;
import net.cwjn.idf.config.json.records.subtypes.DefenceData;
import net.cwjn.idf.config.json.records.subtypes.OffenseData;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public record EntityData (List<EntityTag> tags, String damageClass, OffenseData oData, DefenceData dData, AuxiliaryData aData) {

    public static class EntityDataSerializer implements JsonSerializer<EntityData>, JsonDeserializer<EntityData> {

        public EntityDataSerializer() {}

        @Override
        public EntityData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException {
            final JsonObject obj = json.getAsJsonObject();
            ArrayList<EntityTag> getClasses = new ArrayList<>();
            obj.getAsJsonArray("Modifier Tags").forEach(s -> getClasses.add(EntityTag.valueOf(s.getAsString())));
            return new EntityData(
                    getClasses,
                    obj.get("Damage Class").getAsString(),
                    ctx.deserialize(obj.get("Offense Stats"), OffenseData.class),
                    ctx.deserialize(obj.get("Defense Stats"), DefenceData.class),
                    ctx.deserialize(obj.get("Auxiliary Stats"), AuxiliaryData.class)
            );
        }

        @Override
        public JsonElement serialize(EntityData src, Type typeOfSrc, JsonSerializationContext ctx) {
            JsonObject obj = new JsonObject();
            obj.addProperty("Damage Class", src.damageClass);
            JsonArray classes = new JsonArray();
            src.tags.stream().map(Enum::name).forEach(classes::add);
            obj.add("Modifier Tags", classes);
            obj.add("Offense Stats", ctx.serialize(src.oData, OffenseData.class));
            obj.add("Defense Stats", ctx.serialize(src.dData, DefenceData.class));
            obj.add("Auxiliary Stats", ctx.serialize(src.aData, AuxiliaryData.class));
            return obj;
        }

    }

}

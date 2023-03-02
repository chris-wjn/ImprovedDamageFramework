package net.cwjn.idf.config.json.data;

import com.google.gson.*;
import net.cwjn.idf.config.json.data.subtypes.AuxiliaryData;
import net.cwjn.idf.config.json.data.subtypes.DefensiveData;
import net.cwjn.idf.config.json.data.subtypes.OffensiveData;

import java.lang.reflect.Type;

public record EntityData (EntityDataTemplate template, String damageClass, OffensiveData oData, DefensiveData dData, AuxiliaryData aData) {

    public static class EntityDataSerializer implements JsonSerializer<EntityData>, JsonDeserializer<EntityData> {

        public EntityDataSerializer() {}

        @Override
        public EntityData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException {
            final JsonObject obj = json.getAsJsonObject();
            return new EntityData(
                    EntityDataTemplate.valueOf(obj.get("Template").getAsString()),
                    obj.get("Damage Class").getAsString(),
                    ctx.deserialize(obj.get("Offense Stats"), OffensiveData.class),
                    ctx.deserialize(obj.get("Defense Stats"), DefensiveData.class),
                    ctx.deserialize(obj.get("Auxiliary Stats"), AuxiliaryData.class)
            );
        }

        @Override
        public JsonElement serialize(EntityData src, Type typeOfSrc, JsonSerializationContext ctx) {
            JsonObject obj = new JsonObject();
            obj.addProperty("Template", src.template.name());
            obj.addProperty("Damage Class", src.damageClass);
            obj.add("Offense Stats", ctx.serialize(src.oData, OffensiveData.class));
            obj.add("Defense Stats", ctx.serialize(src.dData, DefensiveData.class));
            obj.add("Auxiliary Stats", ctx.serialize(src.aData, AuxiliaryData.class));
            return obj;
        }

    }

}

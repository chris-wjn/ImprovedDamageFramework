package net.cwjn.idf.config.json.data;

import com.google.gson.*;

import java.lang.reflect.Type;

public record RpgItemData(StatObject CONSTITUTION, StatObject STRENGTH, StatObject DEXTERITY, StatObject AGILITY, StatObject INTELLIGENCE, StatObject WISDOM, StatObject FAITH, StatObject LUCK) {

    public static RpgItemData empty() {
        return new RpgItemData(
                new StatObject(0, 0),
                new StatObject(0, 0),
                new StatObject(0, 0),
                new StatObject(0, 0),
                new StatObject(0, 0),
                new StatObject(0, 0),
                new StatObject(0, 0),
                new StatObject(0, 0)
        );
    }

    public static class StatObjectSerializer implements JsonSerializer<StatObject>, JsonDeserializer<StatObject> {

        public StatObjectSerializer() {};
        
        @Override
        public StatObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject obj = json.getAsJsonObject();
            return new StatObject(
                    obj.get("Requirement").getAsInt(),
                    obj.get("Scalar").getAsDouble()
            );
        }

        @Override
        public JsonElement serialize(StatObject src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("Requirement", src.req);
            obj.addProperty("Scalar", src.scl);
            return obj;
        }
        
    }

    public static class StatObject {
        int req;
        double scl;
        public StatObject(int i, double d) {
            req = i;
            scl = d;
        }
    }
    
}

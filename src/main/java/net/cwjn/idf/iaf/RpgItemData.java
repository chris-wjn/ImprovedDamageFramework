package net.cwjn.idf.iaf;

import com.google.gson.*;
import net.cwjn.idf.data.CommonData;
import net.minecraft.world.entity.ai.attributes.Attribute;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Objects;

public record RpgItemData(StatObject CONSTITUTION, StatObject STRENGTH, StatObject DEXTERITY, StatObject AGILITY, StatObject INTELLIGENCE, StatObject WISDOM, StatObject FAITH, StatObject LUCK) {

    public static RpgItemData empty() {
        return new RpgItemData(
                new StatObject(0, (StatObject.ScalingPair) null),
                new StatObject(0, (StatObject.ScalingPair) null),
                new StatObject(0, (StatObject.ScalingPair) null),
                new StatObject(0, (StatObject.ScalingPair) null),
                new StatObject(0, (StatObject.ScalingPair) null),
                new StatObject(0, (StatObject.ScalingPair) null),
                new StatObject(0, (StatObject.ScalingPair) null),
                new StatObject(0, (StatObject.ScalingPair) null)
        );
    }

    public static class RpgItemSerializer implements JsonSerializer<RpgItemData>, JsonDeserializer<RpgItemData> {

        public RpgItemSerializer() {};

        @Override
        public RpgItemData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException {
            final JsonObject obj = json.getAsJsonObject();
            return new RpgItemData(
                    ctx.deserialize(obj.get("CONSTITUTION"), StatObject.class),
                    ctx.deserialize(obj.get("STRENGTH"), StatObject.class),
                    ctx.deserialize(obj.get("DEXTERITY"), StatObject.class),
                    ctx.deserialize(obj.get("AGILITY"), StatObject.class),
                    ctx.deserialize(obj.get("INTELLIGENCE"), StatObject.class),
                    ctx.deserialize(obj.get("WISDOM"), StatObject.class),
                    ctx.deserialize(obj.get("FAITH"), StatObject.class),
                    ctx.deserialize(obj.get("LUCK"), StatObject.class)
            );
        }


        @Override
        public JsonElement serialize(RpgItemData src, Type typeOfSrc, JsonSerializationContext ctx) {
            JsonObject obj = new JsonObject();
            obj.add("CONSTITUTION", ctx.serialize(src.CONSTITUTION, StatObject.class));
            obj.add("STRENGTH", ctx.serialize(src.STRENGTH, StatObject.class));
            obj.add("DEXTERITY", ctx.serialize(src.DEXTERITY, StatObject.class));
            obj.add("AGILITY", ctx.serialize(src.AGILITY, StatObject.class));
            obj.add("INTELLIGENCE", ctx.serialize(src.INTELLIGENCE, StatObject.class));
            obj.add("WISDOM", ctx.serialize(src.WISDOM, StatObject.class));
            obj.add("FAITH", ctx.serialize(src.FAITH, StatObject.class));
            obj.add("LUCK", ctx.serialize(src.LUCK, StatObject.class));
            return obj;
        }
    }


    public static class StatObject {
        private final int req;
        private final ScalingPair[] scalars;
        public StatObject(int i, @Nullable ScalingPair... scalars) {
            req = i;
            this.scalars = Objects.requireNonNullElseGet(scalars, () -> new ScalingPair[]{});
        }

        public int getReq() {
            return req;
        }

        public ScalingPair[] getScalars() {
            return scalars;
        }

        public static class ScalingPair {
            private String ATTRIBUTE;
            private double SCALAR;
            public ScalingPair(String a, double d) {
                ATTRIBUTE = a;
                SCALAR = d;
            }
            public Attribute getATTRIBUTE() {
                if (!CommonData.SCALABLE_ATTRIBUTES.containsKey(ATTRIBUTE)) throw new IllegalArgumentException("CONFIG CONTAINS MALFORMED SCALING ATTRIBUTE ENTRY!");
                return CommonData.SCALABLE_ATTRIBUTES.get(ATTRIBUTE);
            }
            public double getSCALAR() {
                return SCALAR;
            }
        }

        public static class StatObjectSerializer implements JsonSerializer<StatObject>, JsonDeserializer<StatObject> {

            public StatObjectSerializer() {};

            @Override
            public StatObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException {
                final JsonObject obj = json.getAsJsonObject();
                return new StatObject(
                        obj.get("Requirement").getAsInt(),
                        ctx.deserialize(obj.get("Scalars"), ScalingPair[].class)
                );
            }

            @Override
            public JsonElement serialize(StatObject src, Type typeOfSrc, JsonSerializationContext ctx) {
                JsonObject obj = new JsonObject();
                obj.addProperty("Requirement", src.req);
                obj.add("Scalars", ctx.serialize(src.scalars));
                return obj;
            }

        }

    }
    
}

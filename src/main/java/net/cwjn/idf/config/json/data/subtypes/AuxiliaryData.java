package net.cwjn.idf.config.json.data.subtypes;

import com.google.gson.*;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Type;

public record AuxiliaryData(double hp, double ms, double luck) {

    public static AuxiliaryData empty() {
        return new AuxiliaryData(0, 0, 0);
    }

    public static AuxiliaryData combine(AuxiliaryData data1, AuxiliaryData data2) {
        return new AuxiliaryData(data1.hp + data2.hp, data1.ms + data2.ms, data1.luck + data2.luck);
    }

    public static AuxiliaryData read(FriendlyByteBuf buffer) {
        return new AuxiliaryData(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeDouble(hp);
        buffer.writeDouble(ms);
        buffer.writeDouble(luck);
    }

    public static class AuxiliaryDataSerializer implements JsonSerializer<AuxiliaryData>, JsonDeserializer<AuxiliaryData> {

        public AuxiliaryDataSerializer() {}

        @Override
        public AuxiliaryData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject obj = json.getAsJsonObject();
            return new AuxiliaryData(
                    obj.get("Max Health").getAsDouble(),
                    obj.get("Movement Speed").getAsDouble(),
                    obj.get("Luck").getAsDouble());
        }

        @Override
        public JsonElement serialize(AuxiliaryData src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("Max Health", src.hp);
            obj.addProperty("Movement Speed", src.ms);
            obj.addProperty("Luck", src.luck);
            return obj;
        }
    }

}

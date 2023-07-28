package net.cwjn.idf.config.json.records.subtypes;

import com.google.gson.*;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.attribute.IDFElement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import oshi.util.tuples.Pair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public record AuxiliaryData(double hp, double ms, double luck) {

    public static AuxiliaryData empty() {
        return new AuxiliaryData(0, 0, 0);
    }

    public static AuxiliaryData fullSpeed() {
        return new AuxiliaryData(0, 1.0, 0);
    }

    public static AuxiliaryData create(double hp, double ms, double luck) {
        return new AuxiliaryData(hp, ms, luck);
    }

    public Iterator<Pair<Attribute, Double>> getAttributesWithModifier() {
        ArrayList<Pair<Attribute, Double>> list = new ArrayList<>();
        if (hp != 0) list.add(new Pair<>(Attributes.MAX_HEALTH, hp));
        if (ms != 0) list.add(new Pair<>(Attributes.MOVEMENT_SPEED, ms));
        if (luck != 0) list.add(new Pair<>(Attributes.LUCK, luck));
        return list.iterator();
    }

    public static AuxiliaryData combine(AuxiliaryData data, AuxiliaryData... otherData) {
        return new AuxiliaryData(
                data.hp + Arrays.stream(otherData).mapToDouble(AuxiliaryData::hp).sum(),
                data.ms + Arrays.stream(otherData).mapToDouble(AuxiliaryData::ms).sum(),
                data.luck + Arrays.stream(otherData).mapToDouble(AuxiliaryData::luck).sum());
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

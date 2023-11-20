package net.cwjn.idf.config.json.records;

import com.google.gson.*;
import net.cwjn.idf.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class PresetData implements Iterable<PresetData.AttributeAndModifier> {

    private final List<AttributeAndModifier> attributes;

    public PresetData(List<AttributeAndModifier> attributes) {
        this.attributes = attributes;
    }

    public PresetData(Attribute a, AttributeModifier.Operation op, double amount) {
        this(Arrays.asList(new AttributeAndModifier(a, op.toString(), amount)));
    }

    public void writeData(FriendlyByteBuf buffer) {
        buffer.writeInt(attributes.size());
        for (AttributeAndModifier combo : attributes) {
            combo.write(buffer);
        }
    }

    public static PresetData readData(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        List<AttributeAndModifier> holder = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            holder.add(AttributeAndModifier.read(buffer));
        }
        return new PresetData(holder);
    }

    @NotNull
    @Override
    public Iterator<AttributeAndModifier> iterator() {
        return attributes.iterator();
    }

    public static class PresetSerializer implements JsonSerializer<PresetData>, JsonDeserializer<PresetData> {

        @Override
        public PresetData deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            List<AttributeAndModifier> attributes = new ArrayList<>();
            for (JsonElement element : obj.getAsJsonArray("Modifiers")) {
                attributes.add(ctx.deserialize(element, AttributeAndModifier.class));
            }
            return new PresetData(attributes);
        }

        @Override
        public JsonElement serialize(PresetData data, Type type, JsonSerializationContext ctx) {
            JsonObject obj = new JsonObject();
            JsonArray array = new JsonArray();
            for (AttributeAndModifier mod : data.attributes) {
                array.add(ctx.serialize(mod));
            }
            obj.add("Modifiers", array);
            return obj;
        }

    }

    public static class AttributeAndModifier {
        private final Attribute attribute;
        private final String operation;
        private final double amount;

        public AttributeAndModifier(Attribute attribute, String op, double d) {
            this.attribute = attribute;
            operation = op;
            amount = d;
        }

        public double getAmount() {
            return amount;
        }

        public String getOperation() {
            return operation;
        }

        public Attribute getAttribute() {
            return attribute;
        }

        public void write(FriendlyByteBuf buffer) {
            if (ForgeRegistries.ATTRIBUTES.getKey(attribute) == null) {
                throw new IllegalStateException("Tried to write invalid attribute " + attribute.getDescriptionId());
            }
            else {
                buffer.writeResourceLocation(ForgeRegistries.ATTRIBUTES.getKey(attribute));
                Util.writeString(operation, buffer);
                buffer.writeDouble(amount);
            }
        }

        public static AttributeAndModifier read(FriendlyByteBuf buffer) {
            return new AttributeAndModifier(
                    ForgeRegistries.ATTRIBUTES.getValue(buffer.readResourceLocation()),
                    Util.readString(buffer),
                    buffer.readDouble()
            );
        }

    }

    public static class AttributeAndModifierSerializer implements JsonSerializer<AttributeAndModifier>, JsonDeserializer<AttributeAndModifier> {

        @Override
        public AttributeAndModifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            return new AttributeAndModifier(ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.tryParse(obj.get("Attribute").getAsString())),
                    obj.get("Operation").getAsString(), obj.get("Amount").getAsDouble());
        }

        @Override
        public JsonElement serialize(AttributeAndModifier data, Type type, JsonSerializationContext ctx) {
            JsonObject obj = new JsonObject();
            obj.addProperty("Attribute", ForgeRegistries.ATTRIBUTES.getKey(data.getAttribute()).toString());
            obj.addProperty("Operation", data.getOperation());
            obj.addProperty("Amount", data.getAmount());
            return obj;
        }

    }


}

package net.cwjn.idf.config.json.data.subtypes;

import com.google.gson.*;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Type;

public record OffensiveData(double pDmg, double fDmg, double wDmg, double lDmg, double mDmg, double dDmg, double hDmg,
                            double ls, double pen, double crit, double force, double accuracy, double kb, double atkSpd) {

    public static OffensiveData entityStandard() {
        return new OffensiveData(0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0.4,  0);
    }

    public static OffensiveData damage(double p, double f, double w, double l, double m, double d, double h) {
        return new OffensiveData(p, f, w, l, m, d, h, 0, 0, 0, 0, 0, 0.4, 0);
    }

    public static OffensiveData empty() {
        return new OffensiveData(0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0.0, 0);
    }

    public static OffensiveData combine (OffensiveData data1, OffensiveData data2) {
        return new OffensiveData(
                data1.pDmg + data2.pDmg,
                data1.fDmg + data2.fDmg,
                data1.wDmg + data2.wDmg,
                data1.lDmg + data2.lDmg,
                data1.mDmg + data2.mDmg,
                data1.dDmg + data2.dDmg,
                data1.hDmg + data2.hDmg,
                data1.ls + data2.ls,
                data1.pen + data2.pen,
                data1.crit + data2.crit,
                data1.force + data2.force,
                data1.accuracy + data2.accuracy,
                data1.kb + data2.kb, 
                data1.atkSpd + data2.atkSpd);
    }

    public static OffensiveData read(FriendlyByteBuf buffer) {
        return new OffensiveData(
                buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),
                buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),
                buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),buffer.readDouble()
        );
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeDouble(pDmg);
        buffer.writeDouble(fDmg);
        buffer.writeDouble(wDmg);
        buffer.writeDouble(lDmg);
        buffer.writeDouble(mDmg);
        buffer.writeDouble(dDmg);
        buffer.writeDouble(hDmg);
        buffer.writeDouble(ls);
        buffer.writeDouble(pen);
        buffer.writeDouble(crit);
        buffer.writeDouble(force);
        buffer.writeDouble(accuracy);
        buffer.writeDouble(kb);
        buffer.writeDouble(atkSpd);
    }

    public static class OffensiveDataSerializer implements JsonSerializer<OffensiveData>, JsonDeserializer<OffensiveData> {

        public OffensiveDataSerializer() {}

        @Override
        public OffensiveData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject obj = json.getAsJsonObject();
            return new OffensiveData(
                    obj.get("Damage - PHYSICAL").getAsDouble(),
                    obj.get("Damage - FIRE").getAsDouble(),
                    obj.get("Damage - WATER").getAsDouble(),
                    obj.get("Damage - LIGHTNING").getAsDouble(),
                    obj.get("Damage - MAGIC").getAsDouble(),
                    obj.get("Damage - DARK").getAsDouble(),
                    obj.get("Damage - HOLY").getAsDouble(),
                    obj.get("Lifesteal").getAsDouble(),
                    obj.get("Armour Penetration").getAsDouble(),
                    obj.get("Critical Chance").getAsDouble(),
                    obj.get("Force").getAsDouble(),
                    obj.get("Accuracy").getAsDouble(),
                    obj.get("Knockback").getAsDouble(),
                    obj.get("Attack Speed").getAsDouble());
        }

        @Override
        public JsonElement serialize(OffensiveData src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("Damage - PHYSICAL", src.pDmg);
            obj.addProperty("Damage - FIRE", src.fDmg);
            obj.addProperty("Damage - WATER", src.wDmg);
            obj.addProperty("Damage - LIGHTNING", src.lDmg);
            obj.addProperty("Damage - MAGIC", src.mDmg);
            obj.addProperty("Damage - DARK", src.dDmg);
            obj.addProperty("Damage - HOLY", src.hDmg);
            obj.addProperty("Lifesteal", src.ls);
            obj.addProperty("Armour Penetration", src.pen);
            obj.addProperty("Critical Chance", src.crit);
            obj.addProperty("Force", src.force);
            obj.addProperty("Accuracy", src.accuracy);
            obj.addProperty("Knockback", src.kb);
            obj.addProperty("Attack Speed", src.atkSpd);
            return obj;
        }

    }

}

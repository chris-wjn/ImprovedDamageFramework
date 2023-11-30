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

public record OffenseData(double pDmg, double fDmg, double wDmg, double lDmg, double mDmg, double dDmg, double hDmg,
                          double ls, double pen, double crit, double critDmg, double force, double accuracy, double kb, double atkSpd) {

    public static OffenseData entityStandard() {
        return new OffenseData(0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0.4,  0);
    }

    public static OffenseData setForce(double force) {
        return new OffenseData(0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, force, 0, 0.4,  0);
    }

    public static OffenseData damage(double p, double f, double w, double l, double m, double d, double h) {
        return new OffenseData(p, f, w, l, m, d, h, 0, 0, 0, 0, 0, 0, 0.0, 0);
    }

    public static OffenseData empty() {
        return new OffenseData(0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0.0, 0);
    }

    public static OffenseData guessForceFromDamageSpeed(double damage, double speed) {
        if (damage == 0) damage = 1;
        return new OffenseData(0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0.35*damage*Math.abs(speed), 0, 0.0, 0);
    }

    public static OffenseData rangedDefault() {
        return new OffenseData(0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 2, 10, 0.0, 0);
    }

    public Iterator<Pair<Attribute, Double>> getAttributesWithModifier() {
        ArrayList<Pair<Attribute, Double>> list = new ArrayList<>();
        if (pDmg != 0) list.add(new Pair<>(Attributes.ATTACK_DAMAGE, pDmg));
        if (fDmg != 0) list.add(new Pair<>(IDFElement.FIRE.damage, fDmg));
        if (wDmg != 0) list.add(new Pair<>(IDFElement.WATER.damage, wDmg));
        if (lDmg != 0) list.add(new Pair<>(IDFElement.LIGHTNING.damage, lDmg));
        if (mDmg != 0) list.add(new Pair<>(IDFElement.MAGIC.damage, mDmg));
        if (dDmg != 0) list.add(new Pair<>(IDFElement.DARK.damage, dDmg));
        if (hDmg != 0) list.add(new Pair<>(IDFElement.HOLY.damage, hDmg));
        if (ls != 0) list.add(new Pair<>(IDFAttributes.LIFESTEAL.get(), ls));
        if (pen != 0) list.add(new Pair<>(IDFAttributes.PENETRATING.get(), pen));
        if (crit != 0) list.add(new Pair<>(IDFAttributes.CRIT_CHANCE.get(), crit));
        if (critDmg != 0) list.add(new Pair<>(IDFAttributes.CRIT_DAMAGE.get(), critDmg));
        if (force != 0) list.add(new Pair<>(IDFAttributes.FORCE.get(), force));
        if (accuracy != 0) list.add(new Pair<>(IDFAttributes.ACCURACY.get(), accuracy));
        if (kb != 0) list.add(new Pair<>(Attributes.ATTACK_KNOCKBACK, kb));
        if (atkSpd != 0) list.add(new Pair<>(Attributes.ATTACK_SPEED, atkSpd));
        return list.iterator();
    }

    public static OffenseData combine (OffenseData data, OffenseData... otherData) {
        return new OffenseData(
                data.pDmg + Arrays.stream(otherData).mapToDouble(OffenseData::pDmg).sum(),
                data.fDmg + Arrays.stream(otherData).mapToDouble(OffenseData::fDmg).sum(),
                data.wDmg + Arrays.stream(otherData).mapToDouble(OffenseData::wDmg).sum(),
                data.lDmg + Arrays.stream(otherData).mapToDouble(OffenseData::lDmg).sum(),
                data.mDmg + Arrays.stream(otherData).mapToDouble(OffenseData::mDmg).sum(),
                data.dDmg + Arrays.stream(otherData).mapToDouble(OffenseData::dDmg).sum(),
                data.hDmg + Arrays.stream(otherData).mapToDouble(OffenseData::hDmg).sum(),
                data.ls + Arrays.stream(otherData).mapToDouble(OffenseData::ls).sum(),
                data.pen + Arrays.stream(otherData).mapToDouble(OffenseData::pen).sum(),
                data.crit + Arrays.stream(otherData).mapToDouble(OffenseData::crit).sum(),
                data.critDmg + Arrays.stream(otherData).mapToDouble(OffenseData::critDmg).sum(),
                data.force + Arrays.stream(otherData).mapToDouble(OffenseData::force).sum(),
                data.accuracy + Arrays.stream(otherData).mapToDouble(OffenseData::accuracy).sum(),
                data.kb + Arrays.stream(otherData).mapToDouble(OffenseData::kb).sum(),
                data.atkSpd + Arrays.stream(otherData).mapToDouble(OffenseData::atkSpd).sum());
    }

    public static OffenseData read(FriendlyByteBuf buffer) {
        return new OffenseData(
                buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),
                buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),
                buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),buffer.readDouble()
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
        buffer.writeDouble(critDmg);
        buffer.writeDouble(force);
        buffer.writeDouble(accuracy);
        buffer.writeDouble(kb);
        buffer.writeDouble(atkSpd);
    }

    public static class OffensiveDataSerializer implements JsonSerializer<OffenseData>, JsonDeserializer<OffenseData> {

        public OffensiveDataSerializer() {}

        @Override
        public OffenseData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject obj = json.getAsJsonObject();
            return new OffenseData(
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
                    obj.has("Critical Damage")? obj.get("Critical Damage").getAsDouble() : 0,
                    obj.get("Force").getAsDouble(),
                    obj.get("Accuracy").getAsDouble(),
                    obj.get("Knockback").getAsDouble(),
                    obj.get("Attack Speed").getAsDouble());
        }

        @Override
        public JsonElement serialize(OffenseData src, Type typeOfSrc, JsonSerializationContext context) {
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
            obj.addProperty("Critical Damage", src.critDmg);
            obj.addProperty("Force", src.force);
            obj.addProperty("Accuracy", src.accuracy);
            obj.addProperty("Knockback", src.kb);
            obj.addProperty("Attack Speed", src.atkSpd);
            return obj;
        }

    }

}

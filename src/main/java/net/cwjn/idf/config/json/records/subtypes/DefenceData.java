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

public record DefenceData(double weight, double pDef, double fDef, double wDef, double lDef, double mDef, double dDef, double hDef,
                          double eva, double kbr, double str, double prc, double sls) {

    public static DefenceData entityStandard() {
        return new DefenceData(0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 1.0, 1.0, 1.0);
    }

    public static DefenceData empty() {
        return new DefenceData(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public static DefenceData setWeight(double wgt) {
        return new DefenceData(wgt, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public static DefenceData weightAndPhysical(double wgt, double p) {
        return new DefenceData(wgt, p, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public static DefenceData resistance(double wgt, double p, double f, double w, double l, double m, double d, double h) {
        return new DefenceData(wgt, p, f, w, l, m, d, h, 0, 0, 0, 0, 0);
    }

    public static DefenceData resistanceAndClass(double wgt, double p, double f, double w, double l, double m, double d, double h, double st, double pr, double sl) {
        return new DefenceData(wgt, p, f, w, l, m, d, h, 0, 0, st, pr, sl);
    }

    public static DefenceData dmgClass(double str, double prc, double sls) {
        return new DefenceData(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, str, prc, sls);
    }

    public Iterator<Pair<Attribute, Double>> getAttributesWithModifier() {
        ArrayList<Pair<Attribute, Double>> list = new ArrayList<>();
        if (weight != 0) list.add(new Pair<>(Attributes.ARMOR_TOUGHNESS, weight));
        if (pDef != 0) list.add(new Pair<>(Attributes.ARMOR, pDef));
        if (fDef != 0) list.add(new Pair<>(IDFElement.FIRE.defence, fDef));
        if (wDef != 0) list.add(new Pair<>(IDFElement.WATER.defence, wDef));
        if (lDef != 0) list.add(new Pair<>(IDFElement.LIGHTNING.defence, lDef));
        if (mDef != 0) list.add(new Pair<>(IDFElement.MAGIC.defence, mDef));
        if (dDef != 0) list.add(new Pair<>(IDFElement.DARK.defence, dDef));
        if (hDef != 0) list.add(new Pair<>(IDFElement.HOLY.defence, hDef));
        if (eva != 0) list.add(new Pair<>(IDFAttributes.EVASION.get(), eva));
        if (kbr != 0) list.add(new Pair<>(Attributes.KNOCKBACK_RESISTANCE, kbr));
        if (str != 0) list.add(new Pair<>(IDFAttributes.STRIKE_MULT.get(), str));
        if (prc != 0) list.add(new Pair<>(IDFAttributes.PIERCE_MULT.get(), prc));
        if (sls != 0) list.add(new Pair<>(IDFAttributes.SLASH_MULT.get(), sls));
        return list.iterator();
    }

    public static DefenceData combine (DefenceData data, DefenceData... otherData) {
        return new DefenceData(
                data.weight + Arrays.stream(otherData).mapToDouble(DefenceData::weight).sum(),
                data.pDef + Arrays.stream(otherData).mapToDouble(DefenceData::pDef).sum(),
                data.fDef + Arrays.stream(otherData).mapToDouble(DefenceData::fDef).sum(),
                data.wDef + Arrays.stream(otherData).mapToDouble(DefenceData::wDef).sum(),
                data.lDef + Arrays.stream(otherData).mapToDouble(DefenceData::lDef).sum(),
                data.mDef + Arrays.stream(otherData).mapToDouble(DefenceData::mDef).sum(),
                data.dDef + Arrays.stream(otherData).mapToDouble(DefenceData::dDef).sum(),
                data.hDef + Arrays.stream(otherData).mapToDouble(DefenceData::hDef).sum(),
                data.eva + Arrays.stream(otherData).mapToDouble(DefenceData::eva).sum(),
                data.kbr + Arrays.stream(otherData).mapToDouble(DefenceData::kbr).sum(),
                data.str + Arrays.stream(otherData).mapToDouble(DefenceData::str).sum(),
                data.prc + Arrays.stream(otherData).mapToDouble(DefenceData::prc).sum(),
                data.sls + Arrays.stream(otherData).mapToDouble(DefenceData::sls).sum());
    }

    public static DefenceData read(FriendlyByteBuf buffer) {
        return new DefenceData(
                buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),
                buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),
                buffer.readDouble(),buffer.readDouble(),buffer.readDouble()
        );
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeDouble(weight);
        buffer.writeDouble(pDef);
        buffer.writeDouble(fDef);
        buffer.writeDouble(wDef);
        buffer.writeDouble(lDef);
        buffer.writeDouble(mDef);
        buffer.writeDouble(dDef);
        buffer.writeDouble(hDef);
        buffer.writeDouble(eva);
        buffer.writeDouble(kbr);
        buffer.writeDouble(str);
        buffer.writeDouble(prc);
        buffer.writeDouble(sls);
    }

    public static class DefensiveDataSerializer implements JsonSerializer<DefenceData>, JsonDeserializer<DefenceData> {

        public DefensiveDataSerializer() {}

        @Override
        public DefenceData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject obj = json.getAsJsonObject();
            return new DefenceData(
                    obj.get("Weight").getAsDouble(),
                    obj.get("Defence - PHYSICAL").getAsDouble(),
                    obj.get("Defence - FIRE").getAsDouble(),
                    obj.get("Defence - WATER").getAsDouble(),
                    obj.get("Defence - LIGHTNING").getAsDouble(),
                    obj.get("Defence - MAGIC").getAsDouble(),
                    obj.get("Defence - DARK").getAsDouble(),
                    obj.get("Defence - HOLY").getAsDouble(),
                    obj.get("Evasion").getAsDouble(),
                    obj.get("Knockback Resistance").getAsDouble(),
                    obj.get("Damage Class - STRIKE").getAsDouble(),
                    obj.get("Damage Class - PIERCE").getAsDouble(),
                    obj.get("Damage Class - SLASH").getAsDouble());
        }

        @Override
        public JsonElement serialize(DefenceData src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("Defence - PHYSICAL", src.pDef);
            obj.addProperty("Defence - FIRE", src.fDef);
            obj.addProperty("Defence - WATER", src.wDef);
            obj.addProperty("Defence - LIGHTNING", src.lDef);
            obj.addProperty("Defence - MAGIC", src.mDef);
            obj.addProperty("Defence - DARK", src.dDef);
            obj.addProperty("Defence - HOLY", src.hDef);
            obj.addProperty("Damage Class - STRIKE", src.str);
            obj.addProperty("Damage Class - PIERCE", src.prc);
            obj.addProperty("Damage Class - SLASH", src.sls);
            obj.addProperty("Weight", src.weight);
            obj.addProperty("Evasion", src.eva);
            obj.addProperty("Knockback Resistance", src.kbr);
            return obj;
        }
    }

}

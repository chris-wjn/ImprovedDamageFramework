package net.cwjn.idf.config.json.data;

import com.google.gson.*;
import net.cwjn.idf.config.json.data.subtypes.AuxiliaryData;
import net.cwjn.idf.config.json.data.subtypes.DefensiveData;
import net.cwjn.idf.config.json.data.subtypes.OffensiveData;
import net.cwjn.idf.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

import static net.cwjn.idf.attribute.IDFAttributes.*;
import static net.minecraft.world.entity.ai.attributes.Attributes.*;

//used to hold data for weapons in the json config files
public record WeaponData(int durability, String damageClass, OffensiveData oData, DefensiveData dData, AuxiliaryData aData) 
        implements Iterable<Pair<Attribute, Double>> {

    //iterate through pairs of attribute instances and their values from this data object. Used
    //for attribute modifiers. Only iterates through doubles.
    @Override
    public @NotNull Iterator<Pair<Attribute, Double>> iterator() {
        ArrayList<Pair<Attribute, Double>> values = new ArrayList<>();
        values.add(new Pair<>(ATTACK_DAMAGE, oData.pDmg()));
        values.add(new Pair<>(FIRE_DAMAGE.get(), oData.fDmg()));
        values.add(new Pair<>(WATER_DAMAGE.get(), oData.wDmg()));
        values.add(new Pair<>(LIGHTNING_DAMAGE.get(), oData.lDmg()));
        values.add(new Pair<>(MAGIC_DAMAGE.get(), oData.mDmg()));
        values.add(new Pair<>(DARK_DAMAGE.get(), oData.dDmg()));
        values.add(new Pair<>(ARMOR_TOUGHNESS, dData.defense()));
        values.add(new Pair<>(ARMOR, dData.pRes()));
        values.add(new Pair<>(FIRE_RESISTANCE.get(), dData.fRes()));
        values.add(new Pair<>(WATER_RESISTANCE.get(), dData.wRes()));
        values.add(new Pair<>(LIGHTNING_RESISTANCE.get(), dData.lRes()));
        values.add(new Pair<>(MAGIC_RESISTANCE.get(), dData.mRes()));
        values.add(new Pair<>(DARK_RESISTANCE.get(), dData.dRes()));
        values.add(new Pair<>(LIFESTEAL.get(), oData.ls()));
        values.add(new Pair<>(PENETRATING.get(), oData.pen()));
        values.add(new Pair<>(CRIT_CHANCE.get(), oData.crit()));
        values.add(new Pair<>(FORCE.get(), oData.force()));
        values.add(new Pair<>(ATTACK_KNOCKBACK, oData.kb()));
        values.add(new Pair<>(ATTACK_SPEED, oData.atkSpd()));
        values.add(new Pair<>(EVASION.get(), dData.eva()));
        values.add(new Pair<>(MAX_HEALTH, aData.hp()));
        values.add(new Pair<>(MOVEMENT_SPEED, aData.ms()));
        values.add(new Pair<>(KNOCKBACK_RESISTANCE, dData.kbr()));
        values.add(new Pair<>(LUCK, aData.luck()));
        values.add(new Pair<>(STRIKE_MULT.get(), dData.str()));
        values.add(new Pair<>(PIERCE_MULT.get(), dData.prc()));
        values.add(new Pair<>(SLASH_MULT.get(), dData.sls()));
        return values.iterator();
    }

    @Override
    public void forEach(Consumer<? super Pair<Attribute, Double>> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<Pair<Attribute, Double>> spliterator() {
        return Iterable.super.spliterator();
    }

    //combines two WeaponData objects. data1's damage class will be kept.
    public static WeaponData combine(WeaponData data1, WeaponData data2) {
        return new WeaponData(data1.durability + data2.durability, data1.damageClass,
                OffensiveData.combine(data1.oData, data2.oData),
                DefensiveData.combine(data1.dData, data2.dData),
                AuxiliaryData.combine(data1.aData, data2.aData));
    }

    //used to read a WeaponData object from a packet
    public static WeaponData readWeaponData(FriendlyByteBuf buffer) {
        int d = buffer.readInt();
        String dc = Util.readString(buffer);
        OffensiveData newOData = OffensiveData.read(buffer);
        DefensiveData newDData = DefensiveData.read(buffer);
        AuxiliaryData newAData = AuxiliaryData.read(buffer);
        return new WeaponData(d, dc, newOData, newDData, newAData);
    }

    //used to write a WeaponData object to a packet
    public void writeWeaponData(FriendlyByteBuf buffer) {
        buffer.writeInt(durability);
        Util.writeString(damageClass, buffer);
        oData.write(buffer);
        dData.write(buffer);
        aData.write(buffer);
    }

    //serialize this class as a json object properly
    public static class WeaponSerializer implements JsonSerializer<WeaponData>, JsonDeserializer<WeaponData> {

        public WeaponSerializer() {}

        @Override
        public WeaponData deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            final JsonObject obj = json.getAsJsonObject();
            return new WeaponData(
                    obj.get("Bonus Durability").getAsInt(),
                    obj.get("Damage Class").getAsString(),
                    ctx.deserialize(obj.get("Offense Stats"), OffensiveData.class),
                    ctx.deserialize(obj.get("Defense Stats"), DefensiveData.class),
                    ctx.deserialize(obj.get("Auxiliary Stats"), AuxiliaryData.class)
            );
        }

        @Override
        public JsonElement serialize(WeaponData data, Type type, JsonSerializationContext ctx) {
            JsonObject obj = new JsonObject();
            obj.addProperty("Bonus Durability", data.durability);
            obj.addProperty("Damage Class", data.damageClass);
            obj.add("Offense Stats", ctx.serialize(data.oData, OffensiveData.class));
            obj.add("Defense Stats", ctx.serialize(data.dData, DefensiveData.class));
            obj.add("Auxiliary Stats", ctx.serialize(data.aData, AuxiliaryData.class));
            return obj;
        }

    }

}

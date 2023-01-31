package net.cwjn.idf.config.json.data;

import com.google.gson.*;
import net.cwjn.idf.config.json.data.subtypes.AuxiliaryData;
import net.cwjn.idf.config.json.data.subtypes.DefensiveData;
import net.cwjn.idf.config.json.data.subtypes.OffensiveData;
import net.cwjn.idf.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.ai.attributes.Attribute;
import oshi.util.tuples.Pair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

import static net.cwjn.idf.attribute.IDFAttributes.*;
import static net.minecraft.world.entity.ai.attributes.Attributes.*;

public record WeaponData(int durability, String damageClass, OffensiveData oData, DefensiveData dData, AuxiliaryData aData) 
        implements Iterable<Pair<Attribute, Double>> {

    @Override
    public Iterator<Pair<Attribute, Double>> iterator() {
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

    public static WeaponData combine(WeaponData data1, WeaponData data2) {
        return new WeaponData(data1.durability + data2.durability, data1.damageClass, data1.oData.pDmg() + data2.oData.pDmg(),
                data1.oData.fDmg() + data2.oData.fDmg(), data1.oData.wDmg() + data2.oData.wDmg(), data1.oData.lDmg() + data2.oData.lDmg(),
                data1.oData.mDmg() + data2.oData.mDmg(), data1.oData.dDmg() + data2.oData.dDmg(), data1.oData.ls() + data2.oData.ls(),
                data1.oData.pen() + data2.oData.pen(), data1.criticalChance + data2.criticalChance, data1.force + data2.force,
                data1.knockback + data2.knockback, data1.attackSpeed + data2.attackSpeed, data1.defense + data2.defense,
                data1.physicalResistance + data2.physicalResistance, data1.fireResistance + data2.fireResistance,
                data1.waterResistance + data2.waterResistance, data1.lightningResistance + data2.lightningResistance, data1.magicResistance + data2.magicResistance,
                data1.darkResistance + data2.darkResistance, data1.evasion + data2.evasion, data1.maxHP + data2.maxHP, data1.movespeed + data1.movespeed,
                data1.knockbackResistance + data2.knockbackResistance, data1.luck + data2.luck, data1.strikeMultiplier + data2.strikeMultiplier,
                data1.pierceMultiplier + data2.pierceMultiplier, data1.slashMultiplier + data2.slashMultiplier);
    }

    public static WeaponData empty() {
        return new WeaponData(0, "strike", 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0 , 0, 0);
    }

    public static WeaponData readWeaponData(FriendlyByteBuf buffer) {
        return new WeaponData(buffer.readInt(), Util.readString(buffer), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                buffer.readDouble(), buffer.readDouble());
    }

    public void writeWeaponData(FriendlyByteBuf buffer) {
        buffer.writeInt(durability);
        Util.writeString(damageClass, buffer);
        buffer.writeDouble(oData.pDmg());
        buffer.writeDouble(oData.fDmg());
        buffer.writeDouble(oData.wDmg());
        buffer.writeDouble(oData.lDmg());
        buffer.writeDouble(oData.mDmg());
        buffer.writeDouble(oData.dDmg());
        buffer.writeDouble(oData.ls());
        buffer.writeDouble(oData.pen());
        buffer.writeDouble(criticalChance);
        buffer.writeDouble(force);
        buffer.writeDouble(knockback);
        buffer.writeDouble(attackSpeed);
        buffer.writeDouble(defense);
        buffer.writeDouble(physicalResistance);
        buffer.writeDouble(fireResistance);
        buffer.writeDouble(waterResistance);
        buffer.writeDouble(lightningResistance);
        buffer.writeDouble(magicResistance);
        buffer.writeDouble(darkResistance);
        buffer.writeDouble(evasion);
        buffer.writeDouble(maxHP);
        buffer.writeDouble(movespeed);
        buffer.writeDouble(knockbackResistance);
        buffer.writeDouble(luck);
        buffer.writeDouble(strikeMultiplier);
        buffer.writeDouble(pierceMultiplier);
        buffer.writeDouble(slashMultiplier);
    }

    public static class WeaponSerializer implements JsonSerializer<WeaponData>, JsonDeserializer<WeaponData> {

        public WeaponSerializer() {

        }

        @Override
        public WeaponData deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            final JsonObject obj = json.getAsJsonObject();
            return new WeaponData(obj.get("Bonus Durability").getAsInt(),
                    obj.get("Damage Class").getAsString(),
                    obj.getAsJsonArray("DAMAGE: Physical / Fire / Water / Lightning / Magic / Dark").get(0).getAsDouble(),
                    obj.getAsJsonArray("DAMAGE: Physical / Fire / Water / Lightning / Magic / Dark").get(1).getAsDouble(),
                    obj.getAsJsonArray("DAMAGE: Physical / Fire / Water / Lightning / Magic / Dark").get(2).getAsDouble(),
                    obj.getAsJsonArray("DAMAGE: Physical / Fire / Water / Lightning / Magic / Dark").get(3).getAsDouble(),
                    obj.getAsJsonArray("DAMAGE: Physical / Fire / Water / Lightning / Magic / Dark").get(4).getAsDouble(),
                    obj.getAsJsonArray("DAMAGE: Physical / Fire / Water / Lightning / Magic / Dark").get(5).getAsDouble(),
                    obj.getAsJsonArray("OFFENSIVE AUXILIARY: Lifesteal / Armour Penetration / Crit Chance / Force / Knockback / AttackSpeed").get(0).getAsDouble(),
                    obj.getAsJsonArray("OFFENSIVE AUXILIARY: Lifesteal / Armour Penetration / Crit Chance / Force / Knockback / AttackSpeed").get(1).getAsDouble(),
                    obj.getAsJsonArray("OFFENSIVE AUXILIARY: Lifesteal / Armour Penetration / Crit Chance / Force / Knockback / AttackSpeed").get(2).getAsDouble(),
                    obj.getAsJsonArray("OFFENSIVE AUXILIARY: Lifesteal / Armour Penetration / Crit Chance / Force / Knockback / AttackSpeed").get(3).getAsDouble(),
                    obj.getAsJsonArray("OFFENSIVE AUXILIARY: Lifesteal / Armour Penetration / Crit Chance / Force / Knockback / AttackSpeed").get(4).getAsDouble(),
                    obj.getAsJsonArray("OFFENSIVE AUXILIARY: Lifesteal / Armour Penetration / Crit Chance / Force / Knockback / AttackSpeed").get(5).getAsDouble(),
                    obj.getAsJsonArray("RESISTANCE: Defense / Physical / Fire / Water / Lightning / Magic / Dark").get(0).getAsDouble(),
                    obj.getAsJsonArray("RESISTANCE: Defense / Physical / Fire / Water / Lightning / Magic / Dark").get(1).getAsDouble(),
                    obj.getAsJsonArray("RESISTANCE: Defense / Physical / Fire / Water / Lightning / Magic / Dark").get(2).getAsDouble(),
                    obj.getAsJsonArray("RESISTANCE: Defense / Physical / Fire / Water / Lightning / Magic / Dark").get(3).getAsDouble(),
                    obj.getAsJsonArray("RESISTANCE: Defense / Physical / Fire / Water / Lightning / Magic / Dark").get(4).getAsDouble(),
                    obj.getAsJsonArray("RESISTANCE: Defense / Physical / Fire / Water / Lightning / Magic / Dark").get(5).getAsDouble(),
                    obj.getAsJsonArray("RESISTANCE: Defense / Physical / Fire / Water / Lightning / Magic / Dark").get(6).getAsDouble(),
                    obj.getAsJsonArray("DEFENSIVE AUXILIARY: Evasion / Max HP / Movespeed / Knockback Resistance / Luck").get(0).getAsDouble(),
                    obj.getAsJsonArray("DEFENSIVE AUXILIARY: Evasion / Max HP / Movespeed / Knockback Resistance / Luck").get(1).getAsDouble(),
                    obj.getAsJsonArray("DEFENSIVE AUXILIARY: Evasion / Max HP / Movespeed / Knockback Resistance / Luck").get(2).getAsDouble(),
                    obj.getAsJsonArray("DEFENSIVE AUXILIARY: Evasion / Max HP / Movespeed / Knockback Resistance / Luck").get(3).getAsDouble(),
                    obj.getAsJsonArray("DEFENSIVE AUXILIARY: Evasion / Max HP / Movespeed / Knockback Resistance / Luck").get(4).getAsDouble(),
                    obj.getAsJsonArray("MULTIPLIERS: Strike / Pierce / Slash").get(0).getAsDouble(),
                    obj.getAsJsonArray("MULTIPLIERS: Strike / Pierce / Slash").get(1).getAsDouble(),
                    obj.getAsJsonArray("MULTIPLIERS: Strike / Pierce / Slash").get(2).getAsDouble());
        }

        @Override
        public JsonElement serialize(WeaponData data, Type type, JsonSerializationContext ctx) {
            JsonObject obj = new JsonObject();
            JsonArray DAMAGE_TYPES = new JsonArray(), OFFENSIVE_AUXILIARY = new JsonArray(), RESISTANCE_TYPES = new JsonArray(), DEFENSIVE_AUXILIARY = new JsonArray(), MULTIPLIERS = new JsonArray();
            Util.addAllToJsonArray(DAMAGE_TYPES, data.oData.pDmg(), data.oData.fDmg(), data.oData.wDmg(), data.oData.lDmg(), data.oData.mDmg(), data.oData.dDmg());
            Util.addAllToJsonArray(OFFENSIVE_AUXILIARY, data.oData.ls(), data.oData.pen(), data.criticalChance, data.force, data.knockback, data.attackSpeed);
            Util.addAllToJsonArray(RESISTANCE_TYPES, data.defense, data.physicalResistance, data.fireResistance, data.waterResistance, data.lightningResistance, data.magicResistance, data.darkResistance);
            Util.addAllToJsonArray(DEFENSIVE_AUXILIARY, data.evasion, data.maxHP, data.movespeed, data.knockbackResistance, data.luck);
            Util.addAllToJsonArray(MULTIPLIERS, data.strikeMultiplier, data.pierceMultiplier, data.slashMultiplier);
            obj.addProperty("Bonus Durability", data.durability);
            obj.addProperty("Damage Class", data.damageClass);
            obj.add("DAMAGE: Physical / Fire / Water / Lightning / Magic / Dark", DAMAGE_TYPES);
            obj.add("OFFENSIVE AUXILIARY: Lifesteal / Armour Penetration / Crit Chance / Force / Knockback / AttackSpeed", OFFENSIVE_AUXILIARY);
            obj.add("RESISTANCE: Defense / Physical / Fire / Water / Lightning / Magic / Dark", RESISTANCE_TYPES);
            obj.add("DEFENSIVE AUXILIARY: Evasion / Max HP / Movespeed / Knockback Resistance / Luck", DEFENSIVE_AUXILIARY);
            obj.add("MULTIPLIERS: Strike / Pierce / Slash", MULTIPLIERS);
            return obj;
        }

    }

}

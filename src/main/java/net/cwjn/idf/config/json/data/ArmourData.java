package net.cwjn.idf.config.json.data;

import com.google.gson.*;
import net.cwjn.idf.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import javax.json.Json;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

import static net.cwjn.idf.attribute.IDFAttributes.*;
import static net.cwjn.idf.attribute.IDFAttributes.GENERIC_MULT;
import static net.minecraft.world.entity.ai.attributes.Attributes.*;

public record ArmourData(int durability, double physicalDamage, double fireDamage,
                         double waterDamage, double lightningDamage, double magicDamage, double darkDamage,
                         double lifesteal, double armourPenetration, double criticalChance, double force, double knockback,
                         double attackSpeed, double defense, double physicalResistance, double fireResistance,
                         double waterResistance, double lightningResistance, double magicResistance,
                         double darkResistance, double evasion, double maxHP, double movespeed,
                         double knockbackResistance, double luck, double strikeMultiplier, double pierceMultiplier,
                         double slashMultiplier, double crushMultiplier, double genericMultiplier) implements Iterable<Pair<Attribute, Double>> {

    public static ArmourData combine(ArmourData data1, ArmourData data2) {
        return new ArmourData(data1.durability + data2.durability, data1.physicalDamage + data2.physicalDamage,
                data1.fireDamage + data2.fireDamage, data1.waterDamage + data2.waterDamage, data1.lightningDamage + data2.lightningDamage,
                data1.magicDamage + data2.magicDamage, data1.darkDamage + data2.darkDamage, data1.lifesteal + data2.lifesteal,
                data1.armourPenetration + data2.armourPenetration, data1.criticalChance + data2.criticalChance, data1.force + data2.force,
                data1.knockback + data2.knockback, data1.attackSpeed + data2.attackSpeed, data1.defense + data2.defense,
                data1.physicalResistance + data2.physicalResistance, data1.fireResistance + data2.fireResistance,
                data1.waterResistance + data2.waterResistance, data1.lightningResistance + data2.lightningResistance, data1.magicResistance + data2.magicResistance,
                data1.darkResistance + data2.darkResistance, data1.evasion + data2.evasion, data1.maxHP + data2.maxHP, data1.movespeed + data1.movespeed,
                data1.knockbackResistance + data2.knockbackResistance, data1.luck + data2.luck, data1.strikeMultiplier + data2.strikeMultiplier,
                data1.pierceMultiplier + data2.pierceMultiplier, data1.slashMultiplier + data2.slashMultiplier, data1.crushMultiplier + data2.crushMultiplier,
                data1.genericMultiplier + data2.genericMultiplier);
    }

    public static ArmourData empty() {
        return new ArmourData(0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public static ArmourData readArmourData(FriendlyByteBuf buffer) {
        return new ArmourData(buffer.readInt(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    public void writeArmourData(FriendlyByteBuf buffer) {
        buffer.writeInt(durability);
        buffer.writeDouble(physicalDamage);
        buffer.writeDouble(fireDamage);
        buffer.writeDouble(waterDamage);
        buffer.writeDouble(lightningDamage);
        buffer.writeDouble(magicDamage);
        buffer.writeDouble(darkDamage);
        buffer.writeDouble(lifesteal);
        buffer.writeDouble(armourPenetration);
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
        buffer.writeDouble(crushMultiplier);
        buffer.writeDouble(genericMultiplier);
    }

    @NotNull
    @Override
    public Iterator<Pair<Attribute, Double>> iterator() {
        ArrayList<Pair<Attribute, Double>> values = new ArrayList<>();
        values.add(new Pair<>(ATTACK_DAMAGE, physicalDamage));
        values.add(new Pair<>(FIRE_DAMAGE.get(), fireDamage));
        values.add(new Pair<>(WATER_DAMAGE.get(), waterDamage));
        values.add(new Pair<>(LIGHTNING_DAMAGE.get(), lightningDamage));
        values.add(new Pair<>(MAGIC_DAMAGE.get(), magicDamage));
        values.add(new Pair<>(DARK_DAMAGE.get(), darkDamage));
        values.add(new Pair<>(ARMOR_TOUGHNESS, defense));
        values.add(new Pair<>(ARMOR, physicalResistance));
        values.add(new Pair<>(FIRE_RESISTANCE.get(), fireResistance));
        values.add(new Pair<>(WATER_RESISTANCE.get(), waterResistance));
        values.add(new Pair<>(LIGHTNING_RESISTANCE.get(), lightningResistance));
        values.add(new Pair<>(MAGIC_RESISTANCE.get(), magicResistance));
        values.add(new Pair<>(DARK_RESISTANCE.get(), darkResistance));
        values.add(new Pair<>(LIFESTEAL.get(), lifesteal));
        values.add(new Pair<>(PENETRATING.get(), armourPenetration));
        values.add(new Pair<>(CRIT_CHANCE.get(), criticalChance));
        values.add(new Pair<>(FORCE.get(), force));
        values.add(new Pair<>(ATTACK_KNOCKBACK, knockback));
        values.add(new Pair<>(ATTACK_SPEED, attackSpeed));
        values.add(new Pair<>(EVASION.get(), evasion));
        values.add(new Pair<>(MAX_HEALTH, maxHP));
        values.add(new Pair<>(MOVEMENT_SPEED, movespeed));
        values.add(new Pair<>(KNOCKBACK_RESISTANCE, knockbackResistance));
        values.add(new Pair<>(LUCK, luck));
        values.add(new Pair<>(STRIKE_MULT.get(), strikeMultiplier));
        values.add(new Pair<>(PIERCE_MULT.get(), pierceMultiplier));
        values.add(new Pair<>(SLASH_MULT.get(), slashMultiplier));
        values.add(new Pair<>(CRUSH_MULT.get(), crushMultiplier));
        values.add(new Pair<>(GENERIC_MULT.get(), genericMultiplier));
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

    public static class ArmourSerializer implements JsonSerializer<ArmourData>, JsonDeserializer<ArmourData> {

        public ArmourSerializer() {

        }

        @Override
        public ArmourData deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            final JsonObject obj = json.getAsJsonObject();
            return new ArmourData(
                    obj.get("Bonus Durability").getAsInt(),
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
                    obj.getAsJsonArray("MULTIPLIERS: Strike / Pierce / Slash / Crush / Generic").get(0).getAsDouble(),
                    obj.getAsJsonArray("MULTIPLIERS: Strike / Pierce / Slash / Crush / Generic").get(1).getAsDouble(),
                    obj.getAsJsonArray("MULTIPLIERS: Strike / Pierce / Slash / Crush / Generic").get(2).getAsDouble(),
                    obj.getAsJsonArray("MULTIPLIERS: Strike / Pierce / Slash / Crush / Generic").get(3).getAsDouble(),
                    obj.getAsJsonArray("MULTIPLIERS: Strike / Pierce / Slash / Crush / Generic").get(4).getAsDouble());
        }

        @Override
        public JsonElement serialize(ArmourData data, Type type, JsonSerializationContext ctx) {
            JsonObject obj = new JsonObject();
            JsonArray DAMAGE_TYPES = new JsonArray(), OFFENSIVE_AUXILIARY = new JsonArray(), RESISTANCE_TYPES = new JsonArray(), DEFENSIVE_AUXILIARY = new JsonArray(), MULTIPLIERS = new JsonArray();
            Util.addAllToJsonArray(DAMAGE_TYPES, data.physicalDamage, data.fireDamage, data.waterDamage, data.lightningDamage, data.magicDamage, data.darkDamage);
            Util.addAllToJsonArray(OFFENSIVE_AUXILIARY, data.lifesteal, data.armourPenetration, data.criticalChance, data.force, data.knockback, data.attackSpeed);
            Util.addAllToJsonArray(RESISTANCE_TYPES, data.defense, data.physicalResistance, data.fireResistance, data.waterResistance, data.lightningResistance, data.magicResistance, data.darkResistance);
            Util.addAllToJsonArray(DEFENSIVE_AUXILIARY, data.evasion, data.maxHP, data.movespeed, data.knockbackResistance, data.luck);
            Util.addAllToJsonArray(MULTIPLIERS, data.strikeMultiplier, data.pierceMultiplier, data.slashMultiplier, data.crushMultiplier, data.genericMultiplier);
            obj.addProperty("Bonus Durability", data.durability);
            obj.add("DAMAGE: Physical / Fire / Water / Lightning / Magic / Dark", DAMAGE_TYPES);
            obj.add("OFFENSIVE AUXILIARY: Lifesteal / Armour Penetration / Crit Chance / Force / Knockback / AttackSpeed", OFFENSIVE_AUXILIARY);
            obj.add("RESISTANCE: Defense / Physical / Fire / Water / Lightning / Magic / Dark", RESISTANCE_TYPES);
            obj.add("DEFENSIVE AUXILIARY: Evasion / Max HP / Movespeed / Knockback Resistance / Luck", DEFENSIVE_AUXILIARY);
            obj.add("MULTIPLIERS: Strike / Pierce / Slash / Crush / Generic", MULTIPLIERS);
            return obj;
        }

    }

}
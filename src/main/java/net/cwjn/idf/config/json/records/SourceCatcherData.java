package net.cwjn.idf.config.json.records;

import com.google.gson.*;
import net.cwjn.idf.config.json.records.subtypes.AuxiliaryData;
import net.cwjn.idf.config.json.records.subtypes.DefenceData;
import net.cwjn.idf.config.json.records.subtypes.OffenseData;
import net.cwjn.idf.damage.IDFDamageSource;
import net.cwjn.idf.damage.IDFEntityDamageSource;
import net.cwjn.idf.damage.IDFIndirectEntityDamageSource;
import net.cwjn.idf.damage.IDFInterface;
import net.cwjn.idf.data.CommonData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;

import java.lang.reflect.Type;
import java.util.ArrayList;

public record SourceCatcherData(float fire, float water, float lightning, float magic, float dark, float holy, float pen, float lifesteal, float force, String damageClass, boolean isTrueDamage) {

    public static IDFInterface convert(DamageSource source) {
        if (source instanceof IDFInterface) return (IDFInterface) source;
        final boolean isFall = source.isFall();
        final boolean isFire = source.isFire();
        final boolean isProjectile = source.isProjectile();
        final boolean isExplosion = source.isExplosion();
        final boolean isBypassInvul = source.isBypassInvul();
        DamageSource newSource;
        if (CommonData.LOGICAL_SOURCE_MAP.containsKey(source.msgId)) {
            SourceCatcherData data = CommonData.LOGICAL_SOURCE_MAP.get(source.msgId);
            if (data.isTrueDamage) {
                if (source instanceof IndirectEntityDamageSource) {
                    newSource = new IDFIndirectEntityDamageSource(source.msgId, source.getDirectEntity(), source.getEntity(),
                            data.fire, data.water, data.lightning, data.magic, data.dark, data.holy, data.pen, data.lifesteal, data.force,
                            data.damageClass).setIsConversion().setTrue();
                } else if (source instanceof EntityDamageSource) {
                    newSource = new IDFEntityDamageSource(source.msgId, source.getEntity(),
                            data.fire, data.water, data.lightning, data.magic, data.dark, data.holy, data.pen, data.lifesteal, data.force,
                            data.damageClass).setIsConversion().setTrue();
                } else {
                    newSource = new IDFDamageSource(source.msgId,
                            data.fire, data.water, data.lightning, data.magic, data.dark, data.holy, data.pen, data.lifesteal, data.force,
                            data.damageClass).setIsConversion().setTrue();
                }
            } else {
                if (source instanceof IndirectEntityDamageSource) {
                    newSource = new IDFIndirectEntityDamageSource(source.msgId, source.getDirectEntity(), source.getEntity(),
                            data.fire, data.water, data.lightning, data.magic, data.dark, data.holy, data.pen, data.lifesteal, data.force,
                            data.damageClass).setIsConversion();
                } else if (source instanceof EntityDamageSource) {
                    newSource = new IDFEntityDamageSource(source.msgId, source.getEntity(),
                            data.fire, data.water, data.lightning, data.magic, data.dark, data.holy, data.pen, data.lifesteal, data.force,
                            data.damageClass).setIsConversion();
                } else {
                    newSource = new IDFDamageSource(source.msgId,
                            data.fire, data.water, data.lightning, data.magic, data.dark, data.holy, data.pen, data.lifesteal, data.force,
                            data.damageClass).setIsConversion();
                }
            }
        } else {
            if (source instanceof IndirectEntityDamageSource) {
                newSource = new IDFIndirectEntityDamageSource(source.msgId, source.getDirectEntity(), source.getEntity(),
                        0, 0, 0, 0, 0, 0, 0, 0,
                        "strike").setIsConversion();
            } else if (source instanceof EntityDamageSource) {
                newSource = new IDFEntityDamageSource(source.msgId, source.getEntity(),
                        0, 0, 0, 0, 0, 0, 0, 0,
                        "strike").setIsConversion();
            } else {
                newSource = new IDFDamageSource(source.msgId,
                        0, 0, 0, 0, 0, 0, 0, 0,
                        "strike").setIsConversion();
            }
        }
        if (isBypassInvul) newSource.bypassInvul();
        if (isFire) newSource.setIsFire();
        if (isFall) newSource.setIsFall();
        if (isProjectile) newSource.setProjectile();
        if (isExplosion) newSource.setExplosion();
        return (IDFInterface) newSource;
    }

    public static class SourceCatcherDataSerializer implements JsonSerializer<SourceCatcherData>, JsonDeserializer<SourceCatcherData> {

        public SourceCatcherDataSerializer() {}

        @Override
        public JsonElement serialize(SourceCatcherData src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("Fire", src.fire);
            obj.addProperty("Water", src.water);
            obj.addProperty("Lightning", src.lightning);
            obj.addProperty("Magic", src.magic);
            obj.addProperty("Dark", src.dark);
            obj.addProperty("Holy", src.holy);
            obj.addProperty("Armour Pen", src.pen);
            obj.addProperty("Lifesteal", src.lifesteal);
            obj.addProperty("Force", src.force);
            obj.addProperty("Damage Class", src.damageClass);
            obj.addProperty("isTrueDamage?", src.isTrueDamage);
            return obj;
        }

        @Override
        public SourceCatcherData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject obj = json.getAsJsonObject();
            return new SourceCatcherData(
                    obj.get("Fire").getAsFloat(),
                    obj.get("Water").getAsFloat(),
                    obj.get("Lightning").getAsFloat(),
                    obj.get("Magic").getAsFloat(),
                    obj.get("Dark").getAsFloat(),
                    obj.get("Holy").getAsFloat(),
                    obj.get("Armour Pen").getAsFloat(),
                    obj.get("Lifesteal").getAsFloat(),
                    obj.get("Force").getAsFloat(),
                    obj.get("Damage Class").getAsString(),
                    obj.get("isTrueDamage?").getAsBoolean()
            );
        }

    }

}

package net.cwjn.idf.config.json;

import com.google.gson.*;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.config.json.data.ArmourData;
import net.cwjn.idf.config.json.data.ItemData;
import net.cwjn.idf.config.json.data.WeaponData;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.Type;

@SuppressWarnings(value = "deprecation")
public class JSONUtil {
    public static final Gson SERIALIZER = new GsonBuilder().
            setPrettyPrinting().
            registerTypeAdapter(ArmourData.class, new ArmourDataDeserializer()).
            registerTypeAdapter(WeaponData.class, new WeaponDataDeserializer()).
            registerTypeAdapter(ItemData.class, new ItemDataDeserializer()).
            create();

    public static <T> T getOrCreateConfigFile(File configDir, String configName, T defaults, Type type) {

        File configFile = new File(configDir, configName);

        if (!configFile.exists()) {
            writeFile(configFile, defaults);
        }

        try {
            return SERIALIZER.fromJson(FileUtils.readFileToString(configFile), type);
        }
        catch (Exception e) {
            ImprovedDamageFramework.LOGGER.error("Error parsing config from json: " + configFile.toString(), e);
        }

        return null;
    }

    public static <T> T getConfigFile(File configDir, String configName, Type type) {
        File configFile = new File(configDir, configName);

        try {
            return SERIALIZER.fromJson(FileUtils.readFileToString(configFile), type);
        }
        catch (Exception e) {
            ImprovedDamageFramework.LOGGER.error("Error parsing config from json: " + configFile.toString(), e);
        }

        return null;
    }

    protected static void writeFile(File outputFile, Object obj) {
        try {
            FileUtils.write(outputFile, SERIALIZER.toJson(obj));
        }
        catch (Exception e) {
            ImprovedDamageFramework.LOGGER.error("Error writing config file " + outputFile.getAbsolutePath() + ": " + e.getMessage());
        }
    }

    protected static class ArmourDataDeserializer implements JsonDeserializer<ArmourData> {

        public ArmourDataDeserializer() {

        }

        @Override
        public ArmourData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jObject = json.getAsJsonObject();
            final int durability = jObject.get("durability").getAsInt();
            final double pDam = jObject.get("physicalDamage").getAsDouble();
            final double fDam = jObject.get("fireDamage").getAsDouble();
            final double wDam = jObject.get("waterDamage").getAsDouble();
            final double lDam = jObject.get("lightningDamage").getAsDouble();
            final double mDam = jObject.get("magicDamage").getAsDouble();
            final double dDam = jObject.get("darkDamage").getAsDouble();
            final double ls = jObject.get("lifesteal").getAsDouble();
            final double pen = jObject.get("armourPenetration").getAsDouble();
            final double crit = jObject.get("criticalChance").getAsDouble();
            final double force = jObject.get("force").getAsDouble();
            final double kb = jObject.get("knockback").getAsDouble();
            final double atkspd = jObject.get("attackSpeed").getAsDouble();
            final double def = jObject.get("defense").getAsDouble();
            final double pRes = jObject.get("physicalResistance").getAsDouble();
            final double fRes = jObject.get("fireResistance").getAsDouble();
            final double wRes = jObject.get("waterResistance").getAsDouble();
            final double lRes = jObject.get("lightningResistance").getAsDouble();
            final double mRes = jObject.get("magicResistance").getAsDouble();
            final double dRes = jObject.get("darkResistance").getAsDouble();
            final double eva = jObject.get("evasion").getAsDouble();
            final double hp = jObject.get("maxHP").getAsDouble();
            final double ms = jObject.get("movespeed").getAsDouble();
            final double kbr = jObject.get("knockbackResistance").getAsDouble();
            final double luck = jObject.get("luck").getAsDouble();
            final double str = jObject.get("strikeMultiplier").getAsDouble();
            final double prc = jObject.get("pierceMultiplier").getAsDouble();
            final double sls = jObject.get("slashMultiplier").getAsDouble();
            final double crs = jObject.get("crushMultiplier").getAsDouble();
            final double gen = jObject.get("genericMultiplier").getAsDouble();
            return new ArmourData(durability, pDam, fDam, wDam, lDam, mDam, dDam, ls, pen, crit, force, kb, atkspd,
                    def, pRes, fRes, wRes, lRes, mRes, dRes, eva, hp, ms, kbr, luck, str, prc, sls, crs, gen);
        }

    }

    protected static class WeaponDataDeserializer implements JsonDeserializer<WeaponData> {

        public WeaponDataDeserializer() {

        }

        @Override
        public WeaponData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jObject = json.getAsJsonObject();
            final int durability = jObject.get("durability").getAsInt();
            final String damageClass = jObject.get("damageClass").getAsString();
            final double pDam = jObject.get("physicalDamage").getAsDouble();
            final double fDam = jObject.get("fireDamage").getAsDouble();
            final double wDam = jObject.get("waterDamage").getAsDouble();
            final double lDam = jObject.get("lightningDamage").getAsDouble();
            final double mDam = jObject.get("magicDamage").getAsDouble();
            final double dDam = jObject.get("darkDamage").getAsDouble();
            final double ls = jObject.get("lifesteal").getAsDouble();
            final double pen = jObject.get("armourPenetration").getAsDouble();
            final double crit = jObject.get("criticalChance").getAsDouble();
            final double force = jObject.get("force").getAsDouble();
            final double kb = jObject.get("knockback").getAsDouble();
            final double atkspd = jObject.get("attackSpeed").getAsDouble();
            final double def = jObject.get("defense").getAsDouble();
            final double pRes = jObject.get("physicalResistance").getAsDouble();
            final double fRes = jObject.get("fireResistance").getAsDouble();
            final double wRes = jObject.get("waterResistance").getAsDouble();
            final double lRes = jObject.get("lightningResistance").getAsDouble();
            final double mRes = jObject.get("magicResistance").getAsDouble();
            final double dRes = jObject.get("darkResistance").getAsDouble();
            final double eva = jObject.get("evasion").getAsDouble();
            final double hp = jObject.get("maxHP").getAsDouble();
            final double ms = jObject.get("movespeed").getAsDouble();
            final double kbr = jObject.get("knockbackResistance").getAsDouble();
            final double luck = jObject.get("luck").getAsDouble();
            final double str = jObject.get("strikeMultiplier").getAsDouble();
            final double prc = jObject.get("pierceMultiplier").getAsDouble();
            final double sls = jObject.get("slashMultiplier").getAsDouble();
            final double crs = jObject.get("crushMultiplier").getAsDouble();
            final double gen = jObject.get("genericMultiplier").getAsDouble();
            return new WeaponData(durability, damageClass, pDam, fDam, wDam, lDam, mDam, dDam, ls, pen, crit, force, kb, atkspd,
                    def, pRes, fRes, wRes, lRes, mRes, dRes, eva, hp, ms, kbr, luck, str, prc, sls, crs, gen);
        }

    }

    protected static class ItemDataDeserializer implements JsonDeserializer<ItemData> {

        public ItemDataDeserializer() {

        }

        @Override
        public ItemData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jObject = json.getAsJsonObject();
            final double pDam = jObject.get("physicalDamage").getAsDouble();
            final double fDam = jObject.get("fireDamage").getAsDouble();
            final double wDam = jObject.get("waterDamage").getAsDouble();
            final double lDam = jObject.get("lightningDamage").getAsDouble();
            final double mDam = jObject.get("magicDamage").getAsDouble();
            final double dDam = jObject.get("darkDamage").getAsDouble();
            final double ls = jObject.get("lifesteal").getAsDouble();
            final double pen = jObject.get("armourPenetration").getAsDouble();
            final double crit = jObject.get("criticalChance").getAsDouble();
            final double force = jObject.get("force").getAsDouble();
            final double kb = jObject.get("knockback").getAsDouble();
            final double atkspd = jObject.get("attackSpeed").getAsDouble();
            final double def = jObject.get("defense").getAsDouble();
            final double pRes = jObject.get("physicalResistance").getAsDouble();
            final double fRes = jObject.get("fireResistance").getAsDouble();
            final double wRes = jObject.get("waterResistance").getAsDouble();
            final double lRes = jObject.get("lightningResistance").getAsDouble();
            final double mRes = jObject.get("magicResistance").getAsDouble();
            final double dRes = jObject.get("darkResistance").getAsDouble();
            final double eva = jObject.get("evasion").getAsDouble();
            final double hp = jObject.get("maxHP").getAsDouble();
            final double ms = jObject.get("movespeed").getAsDouble();
            final double kbr = jObject.get("knockbackResistance").getAsDouble();
            final double luck = jObject.get("luck").getAsDouble();
            final double str = jObject.get("strikeMultiplier").getAsDouble();
            final double prc = jObject.get("pierceMultiplier").getAsDouble();
            final double sls = jObject.get("slashMultiplier").getAsDouble();
            final double crs = jObject.get("crushMultiplier").getAsDouble();
            final double gen = jObject.get("genericMultiplier").getAsDouble();
            return new ItemData(pDam, fDam, wDam, lDam, mDam, dDam, ls, pen, crit, force, kb, atkspd,
                    def, pRes, fRes, wRes, lRes, mRes, dRes, eva, hp, ms, kbr, luck, str, prc, sls, crs, gen);
        }

    }

}

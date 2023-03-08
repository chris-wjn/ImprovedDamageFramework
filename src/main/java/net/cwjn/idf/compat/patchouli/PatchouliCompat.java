package net.cwjn.idf.compat.patchouli;

import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.config.json.JSONUtil;
import net.cwjn.idf.config.json.data.EntityData;
import net.cwjn.idf.config.json.data.subtypes.AuxiliaryData;
import net.cwjn.idf.config.json.data.subtypes.DefensiveData;
import net.cwjn.idf.config.json.data.subtypes.OffensiveData;
import net.cwjn.idf.damage.DamageHandler;
import net.cwjn.idf.data.CommonData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.text.DecimalFormat;

import static net.cwjn.idf.util.Util.mBPS;

public class PatchouliCompat implements IComponentProcessor {

    public static void register() {};
    private static final DecimalFormat multFormat = new DecimalFormat();
    private static final DecimalFormat df = new DecimalFormat("#.##");
    static {
        multFormat.setMaximumFractionDigits(0);
    }

    private EntityData data;
    private EntityType<? extends LivingEntity> type;

    @Override
    public void setup(IVariableProvider variables) {
        String name = variables.get("entity").asString();
        data = CommonData.getEntityData(new ResourceLocation(name));
        type = (EntityType<? extends LivingEntity>) ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(name));
        if (data == null) data = new EntityData(null, "MOB NOT IN JSON FILE!", OffensiveData.entityStandard(),
                DefensiveData.entityStandard(), AuxiliaryData.empty());
    }

    @Override
    public IVariable process(String key) {
        return switch (key) {
            case "physicalDamage" ->
                    IVariable.wrap(df.format((DefaultAttributes.getSupplier(type).getBaseValue(Attributes.ATTACK_DAMAGE) * 2) + data.oData().pDmg()));
            case "fireDamage" ->
                    IVariable.wrap(df.format(DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.FIRE_DAMAGE.get())));
            case "waterDamage" ->
                    IVariable.wrap(df.format(DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.WATER_DAMAGE.get())));
            case "lightningDamage" ->
                    IVariable.wrap(df.format(DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.LIGHTNING_DAMAGE.get())));
            case "magicDamage" ->
                    IVariable.wrap(df.format(DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.MAGIC_DAMAGE.get())));
            case "darkDamage" ->
                    IVariable.wrap(df.format(DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.DARK_DAMAGE.get())));
            case "pen" ->
                    IVariable.wrap(df.format(DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.PENETRATING.get())));
            case "lifesteal" ->
                    IVariable.wrap(df.format(DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.LIFESTEAL.get())));
            case "attackWeight" ->
                    IVariable.wrap(multFormat.format(DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.FORCE.get())));
            case "evasion" ->
                    IVariable.wrap(df.format(DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.EVASION.get())));
            case "knockback" ->
                    IVariable.wrap(df.format(((DefaultAttributes.getSupplier(type).getBaseValue(Attributes.ATTACK_KNOCKBACK) + data.oData().kb())/0.4)*100));
            case "knockback_resistance" ->
                    IVariable.wrap(df.format(100-(((DefaultAttributes.getSupplier(type).getBaseValue(Attributes.KNOCKBACK_RESISTANCE) + data.dData().kbr()))*100)));
            case "damageClass" ->
                    IVariable.wrap(data.damageClass().toUpperCase());
            case "health" ->
                    IVariable.wrap(df.format((DefaultAttributes.getSupplier(type).getBaseValue(Attributes.MAX_HEALTH) * 5) + data.aData().hp()));
            case "movespeed" ->
                    IVariable.wrap(mBPS(DefaultAttributes.getSupplier(type).getBaseValue(Attributes.MOVEMENT_SPEED) + data.aData().ms()));
            case "defense" ->
                    IVariable.wrap(df.format(DefaultAttributes.getSupplier(type).getBaseValue(Attributes.ARMOR_TOUGHNESS) + data.dData().defense()));
            case "physicalResistance" ->
                    IVariable.wrap(df.format(DamageHandler.armourFormula(DefaultAttributes.getSupplier(type).getBaseValue(Attributes.ARMOR) + data.dData().pRes())));
            case "fireResistance" ->
                    IVariable.wrap(df.format(DamageHandler.armourFormula(DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.FIRE_RESISTANCE.get()))));
            case "waterResistance" ->
                    IVariable.wrap(df.format(DamageHandler.armourFormula(DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.WATER_RESISTANCE.get()))));
            case "lightningResistance" ->
                    IVariable.wrap(df.format(DamageHandler.armourFormula(DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.LIGHTNING_RESISTANCE.get()))));
            case "magicResistance" ->
                    IVariable.wrap(df.format(DamageHandler.armourFormula(DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.MAGIC_RESISTANCE.get()))));
            case "darkResistance" ->
                    IVariable.wrap(df.format(DamageHandler.armourFormula(DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.DARK_RESISTANCE.get()))));
            case "strike" ->
                    DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.STRIKE_MULT.get()) == 1.0 ? null :
                            DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.STRIKE_MULT.get()) > 1.0 ?
                                    IVariable.wrap("+" + multFormat.format((DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.STRIKE_MULT.get()) - 1.0) * 100) + "%")
                                    :
                                    IVariable.wrap(multFormat.format((DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.STRIKE_MULT.get()) - 1.0) * 100) + "%");
            case "pierce" ->
                    DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.PIERCE_MULT.get()) == 1.0 ? null :
                            DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.PIERCE_MULT.get()) > 1.0 ?
                                    IVariable.wrap("+" + multFormat.format((DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.PIERCE_MULT.get()) - 1.0) * 100) + "%")
                                    :
                                    IVariable.wrap(multFormat.format((DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.PIERCE_MULT.get()) - 1.0) * 100) + "%");
            case "slash" ->
                    DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.SLASH_MULT.get()) == 1.0 ? null :
                            DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.SLASH_MULT.get()) > 1.0 ?
                                    IVariable.wrap("+" + multFormat.format((DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.SLASH_MULT.get()) - 1.0) * 100) + "%")
                                    :
                                    IVariable.wrap(multFormat.format((DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.SLASH_MULT.get()) - 1.0) * 100) + "%");
            case "strikeColour" ->
                    DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.STRIKE_MULT.get()) == 1.0 ? null :
                            DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.STRIKE_MULT.get()) > 1.0 ?
                                    IVariable.wrap("a") : IVariable.wrap("c");
            case "pierceColour" ->
                    DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.PIERCE_MULT.get()) == 1.0 ? null :
                            DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.PIERCE_MULT.get()) > 1.0 ?
                                    IVariable.wrap("a") : IVariable.wrap("c");
            case "slashColour" ->
                    DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.SLASH_MULT.get()) == 1.0 ? null :
                            DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.SLASH_MULT.get()) > 1.0 ?
                                    IVariable.wrap("a") : IVariable.wrap("c");
            case "strikeMsg" ->
                    DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.STRIKE_MULT.get()) == 1.0 ? null :
                            IVariable.wrap("STK ");
            case "pierceMsg" ->
                    DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.PIERCE_MULT.get()) == 1.0 ? null :
                            IVariable.wrap("PRC ");
            case "slashMsg" ->
                    DefaultAttributes.getSupplier(type).getBaseValue(IDFAttributes.SLASH_MULT.get()) == 1.0 ? null :
                            IVariable.wrap("SLS ");
            default -> null;
        };
    }

    public static void createPages() throws URISyntaxException {
        ResourceGrabber grabber = new ResourceGrabber();
        for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES.getValues()) {
            String name = entityType.toString();
            String category = entityType.getCategory() == MobCategory.MONSTER ? "idf:hostile" : "idf:nonhostile";
            Path path = Path.of(grabber.getClass().getClassLoader().getResource("/data/idf/patchouli_books/bestiary/en_us/entries/" +
                    category.substring(4) + "/" + name + ".json").getPath());
            JSONUtil.writeFile(path.toFile(), new PageObject(name, category, name));
        }
    }

}

package net.cwjn.idf.rpg;

import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class RpgAttributes {

    public static final DeferredRegister<Attribute> RPG_ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, ImprovedDamageFramework.MOD_ID);

    public static final RegistryObject<Attribute> FIRE_DAMAGE = register("idf.fire_damage", () -> new RangedAttribute("fire_damage", 0.0D, 0.0D, Double.MAX_VALUE).setSyncable(true));

    private static <T extends Attribute> RegistryObject<T> register(final String name, final Supplier<T> attribute) {
        return RPG_ATTRIBUTES.register(name, attribute);
    }

}

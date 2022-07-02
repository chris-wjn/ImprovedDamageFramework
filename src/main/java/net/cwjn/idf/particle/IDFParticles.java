package net.cwjn.idf.particle;

import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IDFParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ImprovedDamageFramework.MOD_ID);

    public static final RegistryObject<SimpleParticleType> ZERO = PARTICLE_TYPES.register("zero", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> ONE = PARTICLE_TYPES.register("one", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> TWO = PARTICLE_TYPES.register("two", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> THREE = PARTICLE_TYPES.register("three", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> FOUR = PARTICLE_TYPES.register("four", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> FIVE = PARTICLE_TYPES.register("five", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SIX = PARTICLE_TYPES.register("six", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SEVEN = PARTICLE_TYPES.register("seven", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> EIGHT = PARTICLE_TYPES.register("eight", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> NINE = PARTICLE_TYPES.register("nine", () -> new SimpleParticleType(true));

}

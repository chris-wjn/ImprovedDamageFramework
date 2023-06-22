package net.cwjn.idf.particle;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.particle.custom.MissParticle;
import net.cwjn.idf.particle.custom.NumberParticleType;
import net.minecraft.client.particle.SplashParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IDFParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ImprovedDamageFramework.MOD_ID);

    public static final RegistryObject<NumberParticleType> NUMBER_PARTICLE = PARTICLE_TYPES.register("number", () -> new NumberParticleType(true));
    public static final RegistryObject<SimpleParticleType> MISS_PARTICLE = PARTICLE_TYPES.register("miss", () -> new SimpleParticleType(true));

}

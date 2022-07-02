package net.cwjn.idf.event.hook;

import net.cwjn.idf.particle.IDFParticles;
import net.cwjn.idf.particle.custom.numbers.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusEvents {

    @SubscribeEvent
    public static void registerParticleFactories(final ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(IDFParticles.ZERO.get(), Zero.Provider::new);
        Minecraft.getInstance().particleEngine.register(IDFParticles.ONE.get(), One.Provider::new);
        Minecraft.getInstance().particleEngine.register(IDFParticles.TWO.get(), Two.Provider::new);
        Minecraft.getInstance().particleEngine.register(IDFParticles.THREE.get(), Three.Provider::new);
        Minecraft.getInstance().particleEngine.register(IDFParticles.FOUR.get(), Four.Provider::new);
        Minecraft.getInstance().particleEngine.register(IDFParticles.FIVE.get(), Five.Provider::new);
        Minecraft.getInstance().particleEngine.register(IDFParticles.SIX.get(), Six.Provider::new);
        Minecraft.getInstance().particleEngine.register(IDFParticles.SEVEN.get(), Seven.Provider::new);
        Minecraft.getInstance().particleEngine.register(IDFParticles.EIGHT.get(), Eight.Provider::new);
        Minecraft.getInstance().particleEngine.register(IDFParticles.NINE.get(), Nine.Provider::new);
    }

}

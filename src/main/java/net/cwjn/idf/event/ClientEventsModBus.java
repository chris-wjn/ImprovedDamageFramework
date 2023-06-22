package net.cwjn.idf.event;

import net.cwjn.idf.particle.IDFParticles;
import net.cwjn.idf.particle.custom.MissParticle;
import net.cwjn.idf.particle.custom.NumberParticle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEventsModBus {

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
        event.register(IDFParticles.NUMBER_PARTICLE.get(), NumberParticle.Provider::new);
        event.register(IDFParticles.MISS_PARTICLE.get(), MissParticle.Provider::new);
    }

}

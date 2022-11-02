package net.cwjn.idf.event;

import net.cwjn.idf.block.BonfireSwordRenderer;
import net.cwjn.idf.block.entity.IDFBlockEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEventsModBus {

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(IDFBlockEntities.BONFIRE_BASE.get(), BonfireSwordRenderer::new);
    }

}

package net.cwjn.idf.event;

import net.cwjn.idf.rpg.bonfire.block.BonfireSwordRenderer;
import net.cwjn.idf.rpg.bonfire.entity.BonfireEntityRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEventsModBus {

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BonfireEntityRegistry.BONFIRE_BASE.get(), BonfireSwordRenderer::new);
    }

}

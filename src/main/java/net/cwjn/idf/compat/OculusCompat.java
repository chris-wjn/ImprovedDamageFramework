package net.cwjn.idf.compat;

import net.cwjn.idf.data.ClientData;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class OculusCompat {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(OculusCompat.class);
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ClientData.shadersLoaded = IrisApi.getInstance().isShaderPackInUse();
        }
    }

}

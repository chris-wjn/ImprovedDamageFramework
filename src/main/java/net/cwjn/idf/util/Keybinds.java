package net.cwjn.idf.util;

import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.event.KeyEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Keybinds {

    public static KeyMapping openStats;
    public static KeyMapping inspectItem;

    @SubscribeEvent
    public static void registerKeyBinding(final RegisterKeyMappingsEvent event) {
        openStats = create("open_stats", KeyEvent.VK_O);
        inspectItem = create("inspect_item", KeyEvent.VK_I);
        event.register(openStats);
        event.register(inspectItem);
    }

    private static KeyMapping create(String name, int key) {
        return new KeyMapping("key." + ImprovedDamageFramework.MOD_ID + "." + name, key, "key.category." + ImprovedDamageFramework.MOD_ID);
    }

}

package net.cwjn.idf;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.awt.event.KeyEvent;

@OnlyIn(Dist.CLIENT)
public class Keybinds {

    public static KeyMapping openStats;

    public static void register(final FMLClientSetupEvent event) {
        openStats = create("open_stats", KeyEvent.VK_O);
        ClientRegistry.registerKeyBinding(openStats);
    }

    private static KeyMapping create(String name, int key) {
        return new KeyMapping("key." + ImprovedDamageFramework.MOD_ID + "." + name, key, "key.category." + ImprovedDamageFramework.MOD_ID);
    }

}

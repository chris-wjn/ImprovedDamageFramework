package net.cwjn.idf.compat;

import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TooltipsCompat {

    public static boolean enabled = false;

    public static void register() {
        enabled = true;
    };

}



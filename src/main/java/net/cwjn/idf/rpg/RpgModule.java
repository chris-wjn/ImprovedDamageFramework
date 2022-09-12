package net.cwjn.idf.rpg;

import net.cwjn.idf.config.CommonConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.lang.reflect.Method;
import java.util.List;

public class RpgModule {

    public static void register() {
        registerForgeBusEvents();
        registerModBusEvents();
    }

    private static void registerForgeBusEvents() {

    }

    private static void registerModBusEvents() {

    }

}

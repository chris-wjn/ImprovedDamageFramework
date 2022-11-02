package net.cwjn.idf.rpg;

import net.cwjn.idf.rpg.bonfire.BonfireBlockRegistry;
import net.cwjn.idf.rpg.bonfire.entity.BonfireEntityRegistry;
import net.minecraftforge.eventbus.api.IEventBus;

public class RpgModule {

    public static void register(IEventBus bus) {
        registerForgeBusEvents();
        registerModBusEvents();
        addToRegistry(bus);
    }

    private static void registerForgeBusEvents() {

    }

    private static void registerModBusEvents() {

    }

    private static void addToRegistry(IEventBus bus) {
        BonfireBlockRegistry.BLOCKS.register(bus);
        BonfireEntityRegistry.BLOCK_ENTITIES.register(bus);
    }

}

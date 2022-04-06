package com.cwjn.idf.Network;

import com.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class IDFPackerHandler {

    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        INSTANCE.registerMessage(0, SendServerResistanceJsonMessage.class, SendServerResistanceJsonMessage::encode, SendServerResistanceJsonMessage::decode, SendServerResistanceJsonMessage::handle);
        INSTANCE.registerMessage(1, SendServerDamageJsonMessage.class, SendServerDamageJsonMessage::encode, SendServerDamageJsonMessage::decode, SendServerDamageJsonMessage::handle);
    }

}

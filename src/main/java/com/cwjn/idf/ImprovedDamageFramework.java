package com.cwjn.idf;

import com.cwjn.idf.Attributes.AttributeRegistry;
import com.cwjn.idf.Config.Config;
import com.cwjn.idf.Network.IDFPackerHandler;
import com.cwjn.idf.tetraIntegration.Gui;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.CallbackI;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;


@Mod("idf")
public class ImprovedDamageFramework {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "idf";

    public static Logger getLog() {
        return LOGGER;
    }

    public ImprovedDamageFramework() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        AttributeRegistry.ATTRIBUTES.register(bus);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        IDFPackerHandler.init();
        LOGGER.info("Loading Improved Damage Framework...");
        Config.init();
        Gui.register();
    }

    private void serverSetup(final FMLDedicatedServerSetupEvent event) {
        Config.initServer();
    }

}

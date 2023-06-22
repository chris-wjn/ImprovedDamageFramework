package net.cwjn.idf;

import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.compat.CompatHandler;
import net.cwjn.idf.config.ClientConfig;
import net.cwjn.idf.config.CommonConfig;
import net.cwjn.idf.damage.ATHandler;
import net.cwjn.idf.data.ClientData;
import net.cwjn.idf.data.ServerData;
import net.cwjn.idf.gui.BestiaryScreen;
import net.cwjn.idf.gui.StatScreen;
import net.cwjn.idf.hud.PlayerHealthBar;
import net.cwjn.idf.network.PacketHandler;
import net.cwjn.idf.particle.IDFParticles;
import net.cwjn.idf.sound.IDFSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("idf")
public class ImprovedDamageFramework {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "idf";
    public static boolean IAFLoaded = false;
    public static final ResourceLocation FONT_ICONS = new ResourceLocation("idf", "icons");
    public static final ResourceLocation FONT_INDICATORS = new ResourceLocation("idf", "indicators");
    public static final ResourceLocation FONT_TOOLTIPS = new ResourceLocation("idf", "tooltips");
    public static final ResourceLocation FONT_SPACER = new ResourceLocation("idf", "space");

    public ImprovedDamageFramework() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::serverSetup);
        MinecraftForge.EVENT_BUS.register(this);

        IDFAttributes.ATTRIBUTES.register(bus);
        IDFParticles.PARTICLE_TYPES.register(bus);
        IDFSounds.SOUND_EVENTS.register(bus);
        CompatHandler.preInit();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "ImprovedDamageFramework-common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC, "ImprovedDamageFramework-client.toml");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ATHandler::alterStaticSources);
        LOGGER.info(" Altered base game damage sources.");
        event.enqueueWork(IDFAttributes::changeDefaultAttributes);
        LOGGER.info(" Changed properties of vanilla attributes.");
        PacketHandler.init();
        LOGGER.info(" Initialized server-client network.");
        CompatHandler.init(event);
        LOGGER.info(" Initialized compatibility features.");
        LOGGER.info(" Done!");
    }

    private void serverSetup(final FMLDedicatedServerSetupEvent event) {
        ServerData.register(MinecraftForge.EVENT_BUS);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(StatScreen.class);
        MinecraftForge.EVENT_BUS.register(BestiaryScreen.class);
        ClientData.register(MinecraftForge.EVENT_BUS);
        if (ClientConfig.CHANGE_PLAYER_HEALTH_BAR.get()) MinecraftForge.EVENT_BUS.addListener(PlayerHealthBar::replaceWithBar);
        if (ClientConfig.REMOVE_ARMOUR_DISPLAY.get()) MinecraftForge.EVENT_BUS.addListener(PlayerHealthBar::deleteArmorHud);
        CompatHandler.initClient(event);
    }

}

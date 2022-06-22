package net.cwjn.idf;

import net.cwjn.idf.attribute.AttributeRegistry;
import net.cwjn.idf.config.CommonConfig;
import net.cwjn.idf.config.json.Config;
import net.cwjn.idf.damage.ATHandler;
import net.cwjn.idf.enchantment.EnchantmentRegistry;
import net.cwjn.idf.network.IDFPackerHandler;
import net.cwjn.idf.screen.StatsScreen;
import net.cwjn.idf.tetraIntegration.Tetra;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("idf")
public class ImprovedDamageFramework {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "idf";
    public static final ResourceLocation FONT_IDF = new ResourceLocation("idf", "font");
    public static final ResourceLocation FONT_OPTIMUS = new ResourceLocation("idf", "optimus");
    public static final ResourceLocation FONT_PIXEL = new ResourceLocation("idf", "pixel");

    public static Logger getLog() {
        return LOGGER;
    }

    public ImprovedDamageFramework() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        AttributeRegistry.ATTRIBUTES.register(bus);
        EnchantmentRegistry.ENCHANTMENTS.register(bus);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "ImprovedDamageFramework-common.toml");
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ATHandler::alterStaticSources);
        event.enqueueWork(AttributeRegistry::setSyncables);
        IDFPackerHandler.init();
        LOGGER.info("Loading Improved Damage Framework...");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(StatsScreen.class);
        Tetra.registerClient();
        Keybinds.register(event);
    }

}

package net.cwjn.idf;

import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.compat.CompatHandler;
import net.cwjn.idf.config.CommonConfig;
import net.cwjn.idf.damage.ATHandler;
import net.cwjn.idf.enchantment.IDFEnchantments;
import net.cwjn.idf.network.IDFPacketHandler;
import net.cwjn.idf.gui.StatsScreen;
import net.cwjn.idf.particle.IDFParticles;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("idf")
public class ImprovedDamageFramework {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "idf";
    public static final ResourceLocation FONT_IDF = new ResourceLocation("idf", "font");
    public static final ResourceLocation FONT_PIXEL = new ResourceLocation("idf", "pixel");

    public ImprovedDamageFramework() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);

        IDFAttributes.ATTRIBUTES.register(bus);
        IDFEnchantments.ENCHANTMENTS.register(bus);
        IDFParticles.PARTICLE_TYPES.register(bus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "ImprovedDamageFramework-common.toml");
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Loading Improved Damage Framework...");
        event.enqueueWork(ATHandler::alterStaticSources);
        event.enqueueWork(IDFAttributes::setSyncables);
        IDFPacketHandler.init();
        CompatHandler.init(event);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(StatsScreen.class);
        Keybinds.register(event);
        CompatHandler.initClient(event);
    }

}

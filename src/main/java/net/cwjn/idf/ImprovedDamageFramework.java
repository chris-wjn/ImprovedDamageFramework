package net.cwjn.idf;

import net.cwjn.idf.Attributes.AttributeRegistry;
import net.cwjn.idf.Config.Config;
import net.cwjn.idf.Damage.ATHandler;
import net.cwjn.idf.Enchantments.EnchantmentRegistry;
import net.cwjn.idf.Network.IDFPackerHandler;
import net.cwjn.idf.mixin.MixinDamageSource;
import net.cwjn.idf.tetraIntegration.Tetra;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod("idf")
public class ImprovedDamageFramework {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "idf";
    public static final ResourceLocation FONT_IDF = new ResourceLocation("idf", "font");

    public static Logger getLog() {
        return LOGGER;
    }

    public ImprovedDamageFramework() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        AttributeRegistry.ATTRIBUTES.register(bus);
        EnchantmentRegistry.ENCHANTMENTS.register(bus);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ATHandler.alterStaticSources();
        });
        IDFPackerHandler.init();
        LOGGER.info("Loading Improved Damage Framework...");
        Config.init();
        Tetra.registerClient();
    }

    private void serverSetup(final FMLDedicatedServerSetupEvent event) {
        Config.initServer();
    }

}

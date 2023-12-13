package net.cwjn.idf.event;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.config.ClientConfig;
import net.cwjn.idf.data.ClientData;
import net.cwjn.idf.gui.BestiaryScreen;
import net.cwjn.idf.gui.StatScreen;
import net.cwjn.idf.gui.buttons.TabButton;
import net.cwjn.idf.hud.MobHealthbar;
import net.cwjn.idf.util.Keybinds;
import net.cwjn.idf.util.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.cwjn.idf.config.ClientConfig.DISPLAY_HEALTHBAR_ONLY_ON_DAMAGE;
import static net.cwjn.idf.config.ClientConfig.HEALTHBAR_ON_DAMAGE_DISPLAY_TIME;
import static net.cwjn.idf.data.ClientData.displayHealthbarTicks;
import static net.cwjn.idf.data.CommonData.EQUIPMENT_TAG;
import static net.cwjn.idf.gui.buttons.TabButton.TabType.INVENTORY;
import static net.cwjn.idf.gui.buttons.TabButton.TabType.STATS;

@Mod.EventBusSubscriber(modid = ImprovedDamageFramework.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventsForgeBus {

    public static float MOB_HEALTH_BAR_DISTANCE_FACTOR = 30;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack item = event.getItemStack();
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (item.hasTag() && item.getTag().contains(EQUIPMENT_TAG)) {
            Util.doAttributeTooltip(item, player, event.getToolTip());
        }
    }

    @SubscribeEvent
    public static void onInitGui(ScreenEvent.Init event) {
        Screen screen = event.getScreen();
        if (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen || screen instanceof StatScreen) {
            int x = (screen.width - (screen instanceof CreativeModeInventoryScreen ? 195 : 176)) / 2 - 28;
            int y = (screen.height - (screen instanceof CreativeModeInventoryScreen ? 136 : 166)) / 2;
            event.addListener(new TabButton(x, y + 7, INVENTORY, !(screen instanceof StatScreen)));
            event.addListener(new TabButton(x, y + 36, STATS, (screen instanceof StatScreen)));
        }
    }

    @SubscribeEvent
    public static void openStatsScreen(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        onInput(minecraft, event.getKey(), event.getAction());
    }

    private static void onInput(Minecraft minecraft, int key, int action) {
        if (Keybinds.openStats.isDown() && minecraft.screen == null) {
            minecraft.setScreen(new StatScreen());
        }
        else if (Keybinds.openBestiary.isDown() && minecraft.screen == null) {
            minecraft.setScreen(new BestiaryScreen(minecraft.getWindow().getGuiScale()));
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Mob entity && DISPLAY_HEALTHBAR_ONLY_ON_DAMAGE.get()) {
            displayHealthbarTicks.put(entity, HEALTHBAR_ON_DAMAGE_DISPLAY_TIME.get());
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Mob entity && DISPLAY_HEALTHBAR_ONLY_ON_DAMAGE.get()) {
            if (displayHealthbarTicks.containsKey(entity)) {
                int current = displayHealthbarTicks.get(entity);
                if (current <= 1) {
                    displayHealthbarTicks.remove(entity);
                } else {
                    displayHealthbarTicks.put(entity, --current);
                }
            }
        }
    }

    @SubscribeEvent
    public static void prepareHealthbar(RenderNameTagEvent event) {
        if (event.getEntity() instanceof Mob entity) {
            if (entity.isInvisible()) return;
            if (entity.isVehicle()) return;
            if (ClientConfig.BLACKLISTED_HEALTHBAR_ENTITIES.get().contains(Util.getEntityRegistryName(entity.getType()).toString()))
                return;
            if (DISPLAY_HEALTHBAR_ONLY_ON_DAMAGE.get()) {
                if (displayHealthbarTicks.containsKey(entity)) {
                    int unpackedLight = Math.max(LightTexture.sky(event.getPackedLight()) - ClientData.skyDarken, LightTexture.block(event.getPackedLight()));
                    float distance = entity.distanceTo(Minecraft.getInstance().player) - unpackedLight + 15;
                    float minAlpha = distance < MOB_HEALTH_BAR_DISTANCE_FACTOR * 0.5 ? 1 : (1 - (distance / MOB_HEALTH_BAR_DISTANCE_FACTOR));
                    float alpha = (float) Math.min(minAlpha, ((float) displayHealthbarTicks.get(entity) / (HEALTHBAR_ON_DAMAGE_DISPLAY_TIME.get() * 0.5)));
                    MobHealthbar.prepare(entity, alpha);
                }
            }
            else {
                int unpackedLight = Math.max(LightTexture.sky(event.getPackedLight()) - ClientData.skyDarken, LightTexture.block(event.getPackedLight()));
                float distance = entity.distanceTo(Minecraft.getInstance().player) - unpackedLight + 15;
                float alpha = distance < MOB_HEALTH_BAR_DISTANCE_FACTOR*0.5 ? 1 : (1 - (distance/MOB_HEALTH_BAR_DISTANCE_FACTOR));
                MobHealthbar.prepare(entity, alpha);
            }
        }
    }

    @SubscribeEvent
    public static void renderHealthbar(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            Camera c = Minecraft.getInstance().gameRenderer.getMainCamera();
            MobHealthbar.renderBars(event.getPartialTick(), event.getPoseStack(), c);
        }
    }

}

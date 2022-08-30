package net.cwjn.idf.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.text.DecimalFormat;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class HealthBarReplacer {

    private static final ResourceLocation HEALTH_BAR = new ResourceLocation(
            ImprovedDamageFramework.MOD_ID + ":textures/gui/healthbar.png");
    private static final ResourceLocation HEALTH_FILL = new ResourceLocation(
            ImprovedDamageFramework.MOD_ID + ":textures/gui/healthfill.png"
    );
    private static DecimalFormat healthFormat = new DecimalFormat();
    static {
        healthFormat.setMinimumFractionDigits(1);
        healthFormat.setMaximumFractionDigits(1);
        healthFormat.setMinimumIntegerDigits(1);
    }

    private static final Minecraft client = Minecraft.getInstance();
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void replaceWithBar(RenderGuiOverlayEvent event) {
        if (event.isCanceled()
                || client.options.hideGui
                || event.getOverlay() != VanillaGuiOverlay.PLAYER_HEALTH.type()
                || !((ForgeGui) client.gui).shouldDrawSurvivalElements()
                || !(client.getCameraEntity() instanceof Player player)) {
            return;
        }
        renderHealthBar(event.getPoseStack(), player);
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void deleteArmorHud(RenderGuiOverlayEvent event) {
        if (event.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type()) {
            event.setCanceled(true);
        }
    }

    private static void renderHealthBar(PoseStack matrix, Player player) {
        float health = player.getHealth();
        float absorption = player.getAbsorptionAmount();
        float maxHealth = player.getMaxHealth();
        float absorptionPercent = absorption/maxHealth;
        float healthPercent = health / maxHealth;
        int xBar = (client.getWindow().getGuiScaledWidth()) / 2 - 91;
        int xText = ((client.getWindow().getGuiScaledWidth() / 2 - 9) + xBar) / 2;
        int y = client.getWindow().getGuiScaledHeight() - 40;
        matrix.pushPose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        float healthMap = 72.0f * healthPercent;
        float absorptionMap = 72.0f * absorptionPercent;
        RenderSystem.setShaderTexture(0, HEALTH_BAR);
        client.gui.blit(matrix, xBar + 5, y - 3, 0, 0, 72, 13);
        RenderSystem.setShaderTexture(0, HEALTH_FILL);
        client.gui.blit(matrix, xBar + 5, y - 3, 0, 0, (int) healthMap, 13);
        client.gui.blit(matrix, xBar + 5, y - 3, 0, 13, (int) absorptionMap, 13);
        RenderSystem.disableBlend();
        matrix.popPose();
        matrix.pushPose();
        MutableComponent comp = Util.textComponent(healthFormat.format(health) + "/" + healthFormat.format(maxHealth));
        client.font.draw(matrix, comp, (xText - (float) client.font.width(comp) / 2), y, 0xffffff);
        matrix.popPose();
    }

}

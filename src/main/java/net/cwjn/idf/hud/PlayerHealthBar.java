package net.cwjn.idf.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.fml.common.Mod;

import java.text.DecimalFormat;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class PlayerHealthBar {

    private static final ResourceLocation HEALTH_GUI = new ResourceLocation(
            ImprovedDamageFramework.MOD_ID + ":textures/gui/healthgui.png");
    private static DecimalFormat healthFormat = new DecimalFormat();
    static {
        healthFormat.setMinimumFractionDigits(1);
        healthFormat.setMaximumFractionDigits(1);
        healthFormat.setMinimumIntegerDigits(1);
    }

    private static final Minecraft client = Minecraft.getInstance();
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

    public static void deleteArmorHud(RenderGuiOverlayEvent event) {
        if (event.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type()) {
            event.setCanceled(true);
        }
    }

    private static void renderHealthBar(PoseStack matrix, Player player) {
        float health = player.getHealth();
        float absorption = player.getAbsorptionAmount();
        float maxHealth = player.getMaxHealth();
        float absorptionPercent = Mth.clamp(absorption/maxHealth, 0.0f, 1.0f);
        float healthPercent = Mth.clamp(health/maxHealth, 0.0f, 1.0f);
        int xBar = (client.getWindow().getGuiScaledWidth()) / 2 - 91;
        int y = client.getWindow().getGuiScaledHeight() - 40;
        matrix.pushPose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        float healthMap = 72.0f * healthPercent;
        float absorptionMap = 72.0f * absorptionPercent;
        RenderSystem.setShaderTexture(0, HEALTH_GUI);
        client.gui.blit(matrix, xBar + 4, y - 2, 0, 26, 72, 13);
        client.gui.blit(matrix, xBar + 4, y - 2, 0, 0, (int) healthMap, 13);
        client.gui.blit(matrix, xBar + 4, y - 2, 0, 13, (int) absorptionMap, 13);
        RenderSystem.disableBlend();
        matrix.popPose();
    }

}

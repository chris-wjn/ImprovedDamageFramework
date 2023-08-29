package net.cwjn.idf.gui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ScreenEvent;

public class InfoScreen extends Screen {

    private static final ResourceLocation INFO =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/info.png");
    private static final ResourceLocation BKG =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/dirt_bkg.png");
    private static final Window window = Minecraft.getInstance().getWindow();
    private int left, top;
    private final double savedScaleFactor;

    public InfoScreen() {
        super(Component.translatable("idf.info_screen"));
        savedScaleFactor = window.getGuiScale();
    }

    @Override
    protected void init() {
        super.init();
        window.setGuiScale(1);
        left = (window.getGuiScaledWidth() - 1600) / 2;
        top = (window.getGuiScaledHeight() - 800) / 2;
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float pTicks) {
        renderBackground(matrix);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 1.0F);
        RenderSystem.setShaderTexture(0, BKG);
        blit(matrix, 0, 0, 0, 0, window.getWidth(), window.getHeight());
        RenderSystem.setShaderTexture(0, INFO);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1.0F);
        blit(matrix, left, top, 0, 0, 1600, 900, 1600, 900);
        super.render(matrix, mouseX, mouseY, pTicks);
    }

    @Override
    public void onClose() {
        super.onClose();
        window.setGuiScale(savedScaleFactor);
    }

}


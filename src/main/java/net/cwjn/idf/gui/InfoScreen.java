package net.cwjn.idf.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class InfoScreen extends Screen {

    private static final ResourceLocation INFO =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/info.png");
    private int left, top;

    public InfoScreen() {
        super(Component.translatable("idf.info_screen"));
    }

    @Override
    protected void init() {
        super.init();
        left = (width - 1280) / 2;
        top = (height - 908) / 2;
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float pTicks) {
        renderBackground(matrix);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, INFO);
        blit(matrix, left, top, 0, 0, 1280, 908, 1280, 908);
        super.render(matrix, mouseX, mouseY, pTicks);
    }

}


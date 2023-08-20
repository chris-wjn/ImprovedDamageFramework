package net.cwjn.idf.gui.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ArrowButton extends Button {

    private static final ResourceLocation STAT_GUI =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/inventorytabs.png");

    boolean rightArrow = false;

    public ArrowButton(int x, int y, Component component, OnPress onPress, boolean rightArrow) {
        super(x, y, 14, 14, component, onPress);
        this.rightArrow = rightArrow;
    }

    @Override
    public void renderButton(PoseStack matrix, int mouseX, int mouseY, float pTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, STAT_GUI);
        blit(matrix, x, y, this.isHoveredOrFocused()? 31 : 2, 196, 14, 14);
        blit(matrix, x, y, this.rightArrow? 31 : 2, 210, 14, 14);
    }

}

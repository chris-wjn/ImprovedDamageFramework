package net.cwjn.idf.gui.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.gui.StatScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CategoryButton extends Button {

    private static final ResourceLocation STAT_GUI =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/inventorytabs.png");

    StatScreen.Icon type;

    public CategoryButton(int x, int y, Component component, OnPress onPress, StatScreen.Icon iconType) {
        super(x, y, 18, 18, component, onPress);
        type = iconType;
    }

    @Override
    public void renderButton(PoseStack matrix, int mouseX, int mouseY, float pTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, STAT_GUI);
        blit(matrix, x, y, this.isHoveredOrFocused() ? 31 : 2, 196, 24, 24);
        blit(matrix, x+4, y+4, type.locX, type.locY, 16, 16);
    }

}

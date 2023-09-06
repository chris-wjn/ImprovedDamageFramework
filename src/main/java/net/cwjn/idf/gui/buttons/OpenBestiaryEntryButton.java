package net.cwjn.idf.gui.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class OpenBestiaryEntryButton extends Button {

    private static final ResourceLocation BUTTON_FOCUSED =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/toc_button_focused.png");
    private static final ResourceLocation BUTTON =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/toc_button.png");

    public OpenBestiaryEntryButton(int x, int y, ResourceLocation name, OnPress onPress) {
        super(x, y, 119, 21, Component.literal(name.getPath().replace("_", " ").toUpperCase()), onPress);
    }

    @Override
    public void renderButton(PoseStack matrix, int mouseX, int mouseY, float pTick) {
        if (isHoveredOrFocused()) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, BUTTON_FOCUSED);
        } else {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, BUTTON);
        }
        blit(matrix, x, y, 0, 0, 119, 21, 119, 21);
        FormattedCharSequence formattedcharsequence = this.getMessage().getVisualOrderText();
        Minecraft.getInstance().font.draw(matrix, formattedcharsequence, (float)(this.x + this.width / 2 - Minecraft.getInstance().font.width(formattedcharsequence) / 2), (float)this.y + (this.height - 7) / 2, 0x111111);
    }

}

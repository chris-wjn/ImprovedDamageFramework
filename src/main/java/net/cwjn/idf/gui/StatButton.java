package net.cwjn.idf.gui;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public class StatButton extends Button {

    private final ResourceLocation BUTTON_UNFOCUSED;
    private final ResourceLocation BUTTON_FOCUSED;

    public StatButton(int x, int y, int width, int height, OnPress functionSupplier, ResourceLocation unfocused, ResourceLocation focused) {
        super(x, y, width, height, CommonComponents.EMPTY, functionSupplier);
        BUTTON_UNFOCUSED = unfocused;
        BUTTON_FOCUSED = focused;
    }

    @Override
    public void renderButton(PoseStack matrix, int useless1, int useless2, float useless) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.isHoveredOrFocused()) {
            RenderSystem.setShaderTexture(0, BUTTON_FOCUSED);
        } else {
            RenderSystem.setShaderTexture(0, BUTTON_UNFOCUSED);
        }
        blit(matrix, x, y, 0, 0, width, height, width, height);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
    }

}

package net.cwjn.idf.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.util.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

public class BonfireScreen extends Screen {

    private static final ResourceLocation[] GUI_BOOK_OPEN = Util.openBookAnim();
    private static final ResourceLocation[] GUI_BOOK_CLOSE = Util.closeBookAnim();
    private static final ResourceLocation GUI_BOOK = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_static/0.png");
    private static final DecimalFormat df = new DecimalFormat("##.#");
    private static final Style indicators = Style.EMPTY.withFont(ImprovedDamageFramework.FONT_INDICATORS);
    private int w;
    private int h;
    private Player player;
    private Font font;
    private int animFrame;
    private boolean isClosing;

    public BonfireScreen() {
        super(Component.translatable("idf.bonfire_screen"));
    }

    @Override
    protected void init() {
        super.init();
        animFrame = 0;
        w = minecraft.getWindow().getGuiScaledWidth();
        h = minecraft.getWindow().getGuiScaledHeight();
        player = minecraft.player;
        font = minecraft.font;
        isClosing = false;
    }
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(matrix);
        if (!isClosing) {
            matrix.pushPose();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            if (animFrame < 7) {
                if (player.tickCount % 3 == 0) ++animFrame;
            }
            RenderSystem.setShaderTexture(0, GUI_BOOK_OPEN[animFrame]);
            blit(matrix, w / 2 - 384, h / 2 - 320, 0, 0, 768, 640, 768, 640);
        } else {
            matrix.pushPose();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            if (animFrame < 7) {
                if (player.tickCount % 5 == 0) ++animFrame;
            }
            RenderSystem.setShaderTexture(0, GUI_BOOK_CLOSE[animFrame]);
            blit(matrix, w / 2 - 384, h / 2 - 320, 0, 0, 768, 640, 768, 640);
        }
        matrix.popPose();
        super.render(matrix, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        isClosing = true;
        super.onClose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int mod) {
        if (keyCode == KeyEvent.VK_E) {
            this.minecraft.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, mod);
    }
}

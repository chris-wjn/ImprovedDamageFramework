package net.cwjn.idf.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.gui.buttons.InvisibleButton;
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

    private static final ResourceLocation[] GUI_FLIP_RIGHT = Util.flipRightAnim();
    private static final ResourceLocation[] GUI_FLIP_LEFT = Util.flipLeftAnim();
    private static final ResourceLocation[] GUI_OPEN = Util.openAnim();
    private static final ResourceLocation[] GUI_CLOSE = Util.closeAnim();
    private static final ResourceLocation GUI_BOOK = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/book_close/0.png");
    private static final DecimalFormat df = new DecimalFormat("##.#");
    private static final Style indicators = Style.EMPTY.withFont(ImprovedDamageFramework.FONT_INDICATORS);
    private int w;
    private int h;
    private int topMost;
    private int leftMost;
    private int rightMost;
    private int bottomMost;
    private int timer;
    private InvisibleButton openBook;
    private boolean animateOpening;
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
        timer = 0;
        w = minecraft.getWindow().getGuiScaledWidth();
        h = minecraft.getWindow().getGuiScaledHeight();
        topMost = h/2 - 300;
        leftMost = w/2 - 352;
        rightMost = leftMost + 704;
        bottomMost = topMost + 512;
        openBook = addWidget(new InvisibleButton(leftMost, topMost, 704, 512, Component.empty(), (f) -> beginOpenAnim()));
        player = minecraft.player;
        font = minecraft.font;
        isClosing = false;
    }

    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(matrix);
        matrix.pushPose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (animateOpening) {
            if (timer % 3 == 0) {
                animFrame++;
            }
            timer++;
        }
        RenderSystem.setShaderTexture(0, GUI_OPEN[animFrame]);
        if (animFrame >= 4) animateOpening = false;
        blit(matrix, w / 2 - 352, h / 2 - 300, 0, 0, 702, 512, 702, 512);
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

    private void beginOpenAnim() {
        animateOpening = true;
        openBook.active = false;
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

package net.cwjn.idf.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.gui.buttons.CategoryButton;
import net.cwjn.idf.util.Keybinds;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.event.KeyEvent;

import static net.cwjn.idf.gui.StatScreen.Icon.*;
import static net.cwjn.idf.util.Util.translationComponent;

@OnlyIn(Dist.CLIENT)
public class StatScreen extends Screen {

    private static final ResourceLocation STAT_GUI =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/inventorytabs.png");

    private CategoryButton damageButton, resistanceButton, playerButton;
    private int left, top;
    private Icon currentScreen;

    public StatScreen() {
        super(translationComponent("idf.stats_screen"));
    }

    @Override
    protected void init() {
        super.init();
        left = (width - 176) / 2;
        top = (height - 166) / 2;
        damageButton = addRenderableWidget(new CategoryButton(left + 44, top + 15,
                Component.empty(),
                (f) -> changeScreen(DAMAGE), DAMAGE));
        resistanceButton = addRenderableWidget(new CategoryButton(left + 88, top + 15,
                Component.empty(),
                (f) -> changeScreen(RESISTANCE), RESISTANCE));
        playerButton = addRenderableWidget(new CategoryButton(left + 132, top + 15,
                Component.empty(),
                (f) -> changeScreen(PLAYER), PLAYER));
    }

    private void changeScreen(Icon i) {
        currentScreen = i;
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float pTicks) {
        renderBackground(matrix);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, STAT_GUI);
        blit(matrix, left, top, 0, 0, 176, 166);
        super.render(matrix, mouseX, mouseY, pTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public enum Icon {
        DAMAGE(192, 0),
        RESISTANCE(192, 16),
        PLAYER(208, 16),
        FIRE(208, 0),
        WATER(224, 0),
        LIGHTNING(240, 0),
        MAGIC(176, 32),
        DARK(192, 32),
        HOLY(208, 32),
        PHYSICAL(224, 32);
        public final int locX, locY;
        Icon(int x, int y) {
            locX = x;
            locY = y;
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == Keybinds.openStats.getKey().getValue() || pKeyCode == KeyEvent.VK_E) {
            this.minecraft.setScreen(null);
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

}

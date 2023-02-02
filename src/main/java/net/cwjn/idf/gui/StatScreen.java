package net.cwjn.idf.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.gui.buttons.CategoryButton;
import net.cwjn.idf.util.Color;
import net.cwjn.idf.util.Keybinds;
import net.cwjn.idf.util.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.event.KeyEvent;

import static net.cwjn.idf.ImprovedDamageFramework.FONT_ICONS;
import static net.cwjn.idf.ImprovedDamageFramework.FONT_INDICATORS;
import static net.cwjn.idf.attribute.IDFElement.*;
import static net.cwjn.idf.gui.StatScreen.Icon.*;
import static net.cwjn.idf.util.Util.numericalAttributeComponent;
import static net.cwjn.idf.util.Util.translationComponent;

@OnlyIn(Dist.CLIENT)
public class StatScreen extends Screen {

    private static final ResourceLocation STAT_GUI =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/inventorytabs.png");
    //x66, y170, w134, h1 = separator

    private CategoryButton damageButton, resistanceButton, playerButton;
    private static final Style ICON = Style.EMPTY.withFont(FONT_ICONS);
    private static final Style indicators = Style.EMPTY.withFont(FONT_INDICATORS);
    private int left, top;
    private Icon currentScreen = DAMAGE;

    public StatScreen() {
        super(translationComponent("idf.stats_screen"));
    }

    @Override
    protected void init() {
        super.init();
        left = (width - 176) / 2;
        top = (height - 166) / 2;
        damageButton = addRenderableWidget(new CategoryButton(left + 7, top + 7,
                Component.empty(),
                (f) -> changeScreen(DAMAGE), DAMAGE));
        resistanceButton = addRenderableWidget(new CategoryButton(left + 33, top + 7,
                Component.empty(),
                (f) -> changeScreen(RESISTANCE), RESISTANCE));
        playerButton = addRenderableWidget(new CategoryButton(left + 59, top + 7,
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
        Player player = minecraft.player;
        switch (currentScreen) {
            case DAMAGE:
                font.draw(matrix,
                        Util.translationComponent("idf.icon.fire").withStyle(ICON)
                                .append((Util.withColor(Util.translationComponent("idf.stat_screen_physical_damage").withStyle(Style.EMPTY), Color.GRAY))),
                        left+16, top+40, 0xffffff);
                drawCenteredString(font, matrix, player.getAttributeValue(Attributes.ATTACK_DAMAGE), left+70, top+40, 0x292018);
                font.draw(matrix, "F.DMG", left+16, top+56, 0x3f3f3f);
                drawCenteredString(font, matrix, player.getAttributeValue(FIRE.damage), left+70, top+56, 0x802028);
                font.draw(matrix, "W.DMG", left+16, top+72, 0x3f3f3f);
                drawCenteredString(font, matrix, player.getAttributeValue(WATER.damage), left+70, top+72, 0x1c234f);
                font.draw(matrix, "L.DMG", left+16, top+88, 0x3f3f3f);
                drawCenteredString(font, matrix, player.getAttributeValue(LIGHTNING.damage), left+70, top+88, 0x8f5e1d);
                font.draw(matrix, "M.DMG", left+16, top+104, 0x3f3f3f);
                drawCenteredString(font, matrix, player.getAttributeValue(MAGIC.damage), left+70, top+104, 0x599aff);
                font.draw(matrix, "D.DMG", left+16, top+120, 0x3f3f3f);
                drawCenteredString(font, matrix, player.getAttributeValue(DARK.damage), left+70, top+120, 0x3c1b34);
                font.draw(matrix, "H.DMG", left+16, top+136, 0x3f3f3f);
                drawCenteredString(font, matrix, player.getAttributeValue(HOLY.damage), left+70, top+136, 0xe69b39);
                break;
            case RESISTANCE:
                font.draw(matrix, "Fire Resistance", left+16, top+36, 0x802028);
                font.draw(matrix, "Water Resistance", left+16, top+52, 0x1c234f);
                font.draw(matrix, "Lightning Resistance", left+16, top+68, 0x8f5e1d);
                font.draw(matrix, "Magic Resistance", left+16, top+84, 0xffffff);
                font.draw(matrix, "Dark Resistance", left+16, top+100, 0xffffff);
                font.draw(matrix, "Holy Resistance", left+16, top+116, 0xffffff);
                break;
            case PLAYER:
                break;

        }
        super.render(matrix, mouseX, mouseY, pTicks);
    }

    private void drawCenteredString(Font font, PoseStack matrix, double value, float x, float y, int colour) {
        Util.drawCenteredString(font, matrix, numericalAttributeComponent(value).withStyle(indicators), x, y, colour);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public enum Icon {
        DAMAGE(192, 0),
        RESISTANCE(192, 16),
        PLAYER(208, 16);
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

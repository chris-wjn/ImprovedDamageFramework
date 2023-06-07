package net.cwjn.idf.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.gui.buttons.CategoryButton;
import net.cwjn.idf.util.Keybinds;
import net.cwjn.idf.util.Util;
import net.minecraft.ChatFormatting;
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
import java.text.DecimalFormat;

import static net.cwjn.idf.ImprovedDamageFramework.FONT_ICONS;
import static net.cwjn.idf.ImprovedDamageFramework.FONT_INDICATORS;
import static net.cwjn.idf.attribute.IDFElement.*;
import static net.cwjn.idf.gui.StatScreen.Icon.*;
import static net.cwjn.idf.util.Color.INDIANRED;
import static net.cwjn.idf.util.Util.numericalAttributeComponent;
import static net.cwjn.idf.util.Util.translation;

@OnlyIn(Dist.CLIENT)
public class StatScreen extends Screen {

    private static final ResourceLocation STAT_GUI =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/inventorytabs.png");
    //x66, y170, w134, h1 = separator

    private CategoryButton damageButton, resistanceButton, playerButton;
    private static final DecimalFormat df = new DecimalFormat("##.#");
    private static final Style ICON = Style.EMPTY.withFont(FONT_ICONS);
    private static final Style indicators = Style.EMPTY.withFont(FONT_INDICATORS);
    private int left, top;
    private Icon currentScreen = DAMAGE;

    public StatScreen() {
        super(Util.translation("idf.stats_screen"));
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
                drawDamageGUI(matrix, player);
                break;
            case RESISTANCE:
                drawResistanceGUI(matrix, player);
                break;
            case PLAYER:
                font.draw(matrix, drawIconAndString("health", "max_health"), left+16, top+40, 0xffffff);
                drawbar(matrix, left+16, top+40);
                Util.drawCenteredString(font, matrix, Util.text(df.format(player.getHealth()) + "/" + df.format(player.getMaxHealth())), left + 70, top + 40, INDIANRED.getColor());
                font.draw(matrix, drawIconAndString("movespeed", "movespeed"), left+16, top+56, 0xffffff);
                drawbar(matrix, left+16, top+56);
                drawCenteredString(font, matrix, Util.pBPS(player.getAttributeValue(Attributes.MOVEMENT_SPEED)), left+70, top+56, 0x111111);
                font.draw(matrix, drawIconAndString("luck", "luck"), left+16, top+72, 0xffffff);
                drawbar(matrix, left+16, top+72);
                drawCenteredString(font, matrix, player.getAttributeValue(Attributes.LUCK), left+70, top+72, 0x111111);
                break;
        }
        super.render(matrix, mouseX, mouseY, pTicks);
    }

    private void drawDamageGUI(PoseStack matrix, Player player) {
        font.draw(matrix, drawIconAndString("physical", "physical_damage"), left+16, top+40, 0xffffff);
        drawbar(matrix, left+16, top+40);
        drawCenteredString(font, matrix, player.getAttributeValue(Attributes.ATTACK_DAMAGE), left+70, top+40, 0x111111);
        font.draw(matrix, drawIconAndString("fire", "fire_damage"), left+16, top+56, 0xffffff);
        drawbar(matrix, left+16, top+56);
        drawCenteredString(font, matrix, player.getAttributeValue(FIRE.damage), left+70, top+56, 0x111111);
        font.draw(matrix, drawIconAndString("water", "water_damage"), left+16, top+72, 0xffffff);
        drawbar(matrix, left+16, top+72);
        drawCenteredString(font, matrix, player.getAttributeValue(WATER.damage), left+70, top+72, 0x111111);
        font.draw(matrix, drawIconAndString("lightning", "lightning_damage"), left+16, top+88, 0xffffff);
        drawbar(matrix, left+16, top+88);
        drawCenteredString(font, matrix, player.getAttributeValue(LIGHTNING.damage), left+70, top+88, 0x111111);
        font.draw(matrix, drawIconAndString("magic", "magic_damage"), left+16, top+104, 0xffffff);
        drawbar(matrix, left+16, top+104);
        drawCenteredString(font, matrix, player.getAttributeValue(MAGIC.damage), left+70, top+104, 0x111111);
        font.draw(matrix, drawIconAndString("dark", "dark_damage"), left+16, top+120, 0xffffff);
        drawbar(matrix, left+16, top+120);
        drawCenteredString(font, matrix, player.getAttributeValue(DARK.damage), left+70, top+120, 0x111111);
        font.draw(matrix, drawIconAndString("holy", "holy_damage"), left+16, top+136, 0xffffff);
        drawbar(matrix, left+16, top+136);
        drawCenteredString(font, matrix, player.getAttributeValue(HOLY.damage), left+70, top+136, 0x111111);
        font.draw(matrix, drawIconAndString("lifesteal", "lifesteal"), left+96, top+47, 0xffffff);
        drawbar(matrix, left+96, top+47);
        drawCenteredPercentage(font, matrix, player.getAttributeValue(IDFAttributes.LIFESTEAL.get()), left+149, top+47, 0x111111);
        font.draw(matrix, drawIconAndString("armour_penetration", "armour_penetration"), left+96, top+63, 0xffffff);
        drawbar(matrix, left+96, top+63);
        drawCenteredPercentage(font, matrix, player.getAttributeValue(IDFAttributes.PENETRATING.get()), left+149, top+63, 0x111111);
        font.draw(matrix, drawIconAndString("critical_chance", "critical_chance"), left+96, top+79, 0xffffff);
        drawbar(matrix, left+96, top+79);
        drawCenteredPercentage(font, matrix, player.getAttributeValue(IDFAttributes.CRIT_CHANCE.get()), left+149, top+79, 0x111111);
        font.draw(matrix, drawIconAndString("force", "force"), left+96, top+95, 0xffffff);
        drawbar(matrix, left+96, top+95);
        drawCenteredString(font, matrix, player.getAttributeValue(IDFAttributes.FORCE.get()), left+149, top+95, 0x111111);
        font.draw(matrix, drawIconAndString("knockback", "knockback"), left+96, top+111, 0xffffff);
        drawbar(matrix, left+96, top+111);
        drawCenteredString(font, matrix, player.getAttributeValue(Attributes.ATTACK_KNOCKBACK), left+149, top+111, 0x111111);
        font.draw(matrix, drawIconAndString("attack_speed", "attack_speed"), left+96, top+127, 0xffffff);
        drawbar(matrix, left+96, top+127);
        drawCenteredString(font, matrix, player.getAttributeValue(Attributes.ATTACK_SPEED), left+149, top+127, 0x111111);
    }

    private void drawResistanceGUI(PoseStack matrix, Player player) {
        font.draw(matrix, drawIconAndString("physical", "physical_resistance"), left+16, top+40, 0xffffff);
        drawbar(matrix, left+16, top+40);
        drawCenteredString(font, matrix, player.getAttributeValue(Attributes.ARMOR), left+70, top+40, 0x111111);
        font.draw(matrix, drawIconAndString("fire", "fire_resistance"), left+16, top+56, 0xffffff);
        drawbar(matrix, left+16, top+56);
        drawCenteredString(font, matrix, player.getAttributeValue(FIRE.defence), left+70, top+56, 0x111111);
        font.draw(matrix, drawIconAndString("water", "water_resistance"), left+16, top+72, 0xffffff);
        drawbar(matrix, left+16, top+72);
        drawCenteredString(font, matrix, player.getAttributeValue(WATER.defence), left+70, top+72, 0x111111);
        font.draw(matrix, drawIconAndString("lightning", "lightning_resistance"), left+16, top+88, 0xffffff);
        drawbar(matrix, left+16, top+88);
        drawCenteredString(font, matrix, player.getAttributeValue(LIGHTNING.defence), left+70, top+88, 0x111111);
        font.draw(matrix, drawIconAndString("magic", "magic_resistance"), left+16, top+104, 0xffffff);
        drawbar(matrix, left+16, top+104);
        drawCenteredString(font, matrix, player.getAttributeValue(MAGIC.defence), left+70, top+104, 0x111111);
        font.draw(matrix, drawIconAndString("dark", "dark_resistance"), left+16, top+120, 0xffffff);
        drawbar(matrix, left+16, top+120);
        drawCenteredString(font, matrix, player.getAttributeValue(DARK.defence), left+70, top+120, 0x111111);
        font.draw(matrix, drawIconAndString("holy", "holy_resistance"), left+16, top+136, 0xffffff);
        drawbar(matrix, left+16, top+136);
        drawCenteredString(font, matrix, player.getAttributeValue(HOLY.defence), left+70, top+136, 0x111111);
        font.draw(matrix, drawIconAndString("defense", "defense"), left+96, top+47, 0xffffff);
        drawbar(matrix, left+96, top+47);
        drawCenteredString(font, matrix, player.getAttributeValue(Attributes.ARMOR_TOUGHNESS), left+149, top+47, 0x111111);
        font.draw(matrix, drawIconAndString("knockback_resistance", "knockback_resistance"), left+96, top+63, 0xffffff);
        drawbar(matrix, left+96, top+63);
        drawCenteredPercentage(font, matrix, player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)*100, left+149, top+63, 0x111111);
        font.draw(matrix, drawIconAndString("evasion", "evasion"), left+96, top+79, 0xffffff);
        drawbar(matrix, left+96, top+79);
        drawCenteredPercentage(font, matrix, player.getAttributeValue(IDFAttributes.EVASION.get()), left+149, top+79, 0x111111);
        font.draw(matrix, drawIconAndString("strike", "strike"), left+96, top+95, 0xffffff);
        drawbar(matrix, left+96, top+95);
        drawCenteredPercentage(font, matrix, (player.getAttributeValue(IDFAttributes.STRIKE_MULT.get())*100), left+149, top+95,
                player.getAttributeValue(IDFAttributes.STRIKE_MULT.get()) > 1.0 ? ChatFormatting.DARK_RED.getColor() : ChatFormatting.DARK_GREEN.getColor());
        font.draw(matrix, drawIconAndString("pierce", "pierce"), left+96, top+111, 0xffffff);
        drawbar(matrix, left+96, top+111);
        drawCenteredPercentage(font, matrix, (player.getAttributeValue(IDFAttributes.PIERCE_MULT.get())*100), left+149, top+111,
                player.getAttributeValue(IDFAttributes.PIERCE_MULT.get()) > 1.0 ? ChatFormatting.DARK_RED.getColor() : ChatFormatting.DARK_GREEN.getColor());
        font.draw(matrix, drawIconAndString("slash", "slash"), left+96, top+127, 0xffffff);
        drawbar(matrix, left+96, top+127);
        drawCenteredPercentage(font, matrix, (player.getAttributeValue(IDFAttributes.SLASH_MULT.get())*100), left+149, top+127,
                player.getAttributeValue(IDFAttributes.SLASH_MULT.get()) > 1.0 ? ChatFormatting.DARK_RED.getColor() : ChatFormatting.DARK_GREEN.getColor());
    }

    private void drawCenteredString(Font font, PoseStack matrix, double value, float x, float y, int colour) {
        Util.drawCenteredString(font, matrix, numericalAttributeComponent(value), x, y, colour);
    }

    private void drawCenteredPercentage(Font font, PoseStack matrix, double value, float x, float y, int colour) {
        Util.drawCenteredString(font, matrix, numericalAttributeComponent(value).append("%"), x, y, colour);
    }

    private void drawbar(PoseStack matrix, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, STAT_GUI);
        blit(matrix, x-1, y+9, 74, 176, 64, 1);
    }

    private MutableComponent drawIconAndString(String attributeName, String textName) {
        MutableComponent component = Component.literal("");
        component.append(Component.translatable("idf.icon." + attributeName).withStyle(ICON));
        component.append(Util.withColor(Component.translatable("idf.stat_screen_" + textName), 0x111111));
        return component;
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

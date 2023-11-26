package net.cwjn.idf.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.gui.buttons.ArrowButton;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import static net.cwjn.idf.ImprovedDamageFramework.FONT_ICONS;
import static net.cwjn.idf.attribute.IDFElement.*;
import static net.cwjn.idf.damage.DamageHandler.DEFAULT_KNOCKBACK;
import static net.cwjn.idf.util.Color.INDIANRED;
import static net.cwjn.idf.util.Util.numericalAttributeComponent;
import static net.minecraft.network.chat.Component.translatable;

@OnlyIn(Dist.CLIENT)
public class StatScreen extends Screen {

    private static final ResourceLocation STAT_GUI =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/inventorytabs.png");
    //x66, y170, w134, h1 = separator

    private static final DecimalFormat df = new DecimalFormat("##.#");
    private static final Style ICON = Style.EMPTY.withFont(FONT_ICONS);
    private int left, top, bottom, right;
    private int currentScreen = 0;

    public StatScreen() {
        super(Util.translation("idf.stats_screen"));
    }

    @Override
    protected void init() {
        super.init();
        left = (width - 176) / 2;
        top = (height - 166) / 2;
        bottom = top+166;
        right = left+176;
        addRenderableWidget(new ArrowButton(left + 7, bottom - 21,
                Component.empty(),
                (f) -> changeScreen(-1), false));
        addRenderableWidget(new ArrowButton(right - 21, bottom - 21,
                Component.empty(),
                (f) -> changeScreen(1), true));
    }

    private void changeScreen(int i) {
        currentScreen += i;
        if (currentScreen < 0) currentScreen = 2;
        if (currentScreen > 2) currentScreen = 0;
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
            case 0:
                drawDamageGUI(matrix, player);
                break;
            case 1:
                drawResistanceGUI(matrix, player);
                break;
            case 2:
                drawPlayerGUI(matrix, player);
                break;
        }
        super.render(matrix, mouseX, mouseY, pTicks);
    }

    private void drawPlayerGUI(PoseStack matrix, Player player) {
        int y = top+16;
        font.draw(matrix, drawIconAndString("health", "max_health"), left+16, y, 0xffffff);
        drawbar(matrix, left+16, y);
        Util.drawCenteredString(font, matrix, Util.text(df.format(player.getHealth()) + "/" + df.format(player.getMaxHealth())), left + 70, y, INDIANRED.getColor());
        font.draw(matrix, drawIconAndString("movespeed", "movespeed"), left+16, y+=16, 0xffffff);
        drawbar(matrix, left+16, y);
        drawCenteredString(font, matrix, Util.pBPS(player.getAttributeValue(Attributes.MOVEMENT_SPEED)), left+70, y, 0x111111);
        font.draw(matrix, drawIconAndString("luck", "luck"), left+16, y+=16, 0xffffff);
        drawbar(matrix, left+16, y);
        drawCenteredString(font, matrix, player.getAttributeValue(Attributes.LUCK), left+70, y, 0x111111);
    }

    private void drawDamageGUI(PoseStack matrix, Player player) {
        int y = top+16;
        font.draw(matrix, drawIconAndString("physical", "physical_damage"), left+16, y, 0xffffff);
        drawbar(matrix, left+16, y);
        drawCenteredString(font, matrix, player.getAttributeValue(Attributes.ATTACK_DAMAGE), left+70, y, 0x111111);
        font.draw(matrix, drawIconAndString("fire", "fire_damage"), left+16, y+=16, 0xffffff);
        drawbar(matrix, left+16, y);
        drawCenteredString(font, matrix, player.getAttributeValue(FIRE.damage), left+70, y, 0x111111);
        font.draw(matrix, drawIconAndString("water", "water_damage"), left+16, y+=16, 0xffffff);
        drawbar(matrix, left+16, y);
        drawCenteredString(font, matrix, player.getAttributeValue(WATER.damage), left+70, y, 0x111111);
        font.draw(matrix, drawIconAndString("lightning", "lightning_damage"), left+16, y+=16, 0xffffff);
        drawbar(matrix, left+16, y);
        drawCenteredString(font, matrix, player.getAttributeValue(LIGHTNING.damage), left+70, y, 0x111111);
        font.draw(matrix, drawIconAndString("magic", "magic_damage"), left+16, y+=16, 0xffffff);
        drawbar(matrix, left+16, y);
        drawCenteredString(font, matrix, player.getAttributeValue(MAGIC.damage), left+70, y, 0x111111);
        font.draw(matrix, drawIconAndString("dark", "dark_damage"), left+16, y+=16, 0xffffff);
        drawbar(matrix, left+16, y);
        drawCenteredString(font, matrix, player.getAttributeValue(DARK.damage), left+70, y, 0x111111);
        font.draw(matrix, drawIconAndString("holy", "holy_damage"), left+16, y+=16, 0xffffff);
        drawbar(matrix, left+16, y);
        drawCenteredString(font, matrix, player.getAttributeValue(HOLY.damage), left+70, y, 0x111111);
        font.draw(matrix, drawIconAndString("accuracy", "accuracy"), left+16, y+=16, 0xffffff);
        drawbar(matrix, left+16, y);
        drawCenteredString(font, matrix, player.getAttributeValue(IDFAttributes.ACCURACY.get()), left+70, y, getColourGreaterThan(player.getAttributeValue(IDFAttributes.ACCURACY.get()), 10));
        font.draw(matrix, drawIconAndString("lifesteal", "lifesteal"), left+96, y=top+16, 0xffffff);
        drawbar(matrix, left+96, y);
        drawCenteredPercentage(font, matrix, player.getAttributeValue(IDFAttributes.LIFESTEAL.get()), left+149, y, 0x111111);
        font.draw(matrix, drawIconAndString("armour_penetration", "armour_penetration"), left+96, y+=16, 0xffffff);
        drawbar(matrix, left+96, y);
        drawCenteredPercentage(font, matrix, player.getAttributeValue(IDFAttributes.PENETRATING.get()), left+149, y, 0x111111);
        font.draw(matrix, drawIconAndString("crit_chance", "critical_chance"), left+96, y+=16, 0xffffff);
        drawbar(matrix, left+96, y);
        drawCenteredPercentage(font, matrix, player.getAttributeValue(IDFAttributes.CRIT_CHANCE.get()), left+149, y, 0x111111);
        font.draw(matrix, drawIconAndString("crit_damage", "critical_damage"), left+96, y+=16, 0xffffff);
        drawbar(matrix, left+96, y);
        drawCenteredPercentage(font, matrix, player.getAttributeValue(IDFAttributes.CRIT_DAMAGE.get()), left+149, y, getColourGreaterThan(player.getAttributeValue(IDFAttributes.CRIT_DAMAGE.get()), 150));
        font.draw(matrix, drawIconAndString("force", "force"), left+96, y+=16, 0xffffff);
        drawbar(matrix, left+96, y);
        double force = player.getAttributeValue(IDFAttributes.FORCE.get());
        if (force < 0) {
            Util.drawCenteredString(font, matrix, translatable("idf.not_applicable"), left+149, y, 0x111111);
        }
        else {
            drawCenteredString(font, matrix, player.getAttributeValue(IDFAttributes.FORCE.get()), left+149, y, 0x111111);
        }
        font.draw(matrix, drawIconAndString("knockback", "knockback"), left+96, y+=16, 0xffffff);
        drawbar(matrix, left+96, y);
        BigDecimal numerator = BigDecimal.valueOf(player.getAttributeValue(Attributes.ATTACK_KNOCKBACK)), denominator = new BigDecimal(DEFAULT_KNOCKBACK);
        double knockback = numerator.divide(denominator, RoundingMode.CEILING).doubleValue();
        drawCenteredPercentage(font, matrix, knockback*100, left+149, y, getColourGreaterThan(knockback, 1));
        font.draw(matrix, drawIconAndString("attack_speed", "attack_speed"), left+96, y+=16, 0xffffff);
        drawbar(matrix, left+96, y);
        drawCenteredString(font, matrix, player.getAttributeValue(Attributes.ATTACK_SPEED), left+149, y, 0x111111);
    }

    private void drawResistanceGUI(PoseStack matrix, Player player) {
        int y = top+16;
        font.draw(matrix, drawIconAndString("physical", "physical_resistance"), left+16, y, 0xffffff);
        drawbar(matrix, left+16, y);
        drawCenteredString(font, matrix, player.getAttributeValue(Attributes.ARMOR), left+70, y, 0x111111);
        font.draw(matrix, drawIconAndString("fire", "fire_resistance"), left+16, y+=16, 0xffffff);
        drawbar(matrix, left+16, y);
        drawCenteredString(font, matrix, player.getAttributeValue(FIRE.defence), left+70, y, 0x111111);
        font.draw(matrix, drawIconAndString("water", "water_resistance"), left+16, y+=16, 0xffffff);
        drawbar(matrix, left+16, y);
        drawCenteredString(font, matrix, player.getAttributeValue(WATER.defence), left+70, y, 0x111111);
        font.draw(matrix, drawIconAndString("lightning", "lightning_resistance"), left+16, y+=16, 0xffffff);
        drawbar(matrix, left+16, y);
        drawCenteredString(font, matrix, player.getAttributeValue(LIGHTNING.defence), left+70, y, 0x111111);
        font.draw(matrix, drawIconAndString("magic", "magic_resistance"), left+16, y+=16, 0xffffff);
        drawbar(matrix, left+16, y);
        drawCenteredString(font, matrix, player.getAttributeValue(MAGIC.defence), left+70, y, 0x111111);
        font.draw(matrix, drawIconAndString("dark", "dark_resistance"), left+16, y+=16, 0xffffff);
        drawbar(matrix, left+16, y);
        drawCenteredString(font, matrix, player.getAttributeValue(DARK.defence), left+70, y, 0x111111);
        font.draw(matrix, drawIconAndString("holy", "holy_resistance"), left+16, y+=16, 0xffffff);
        drawbar(matrix, left+16, y);
        drawCenteredString(font, matrix, player.getAttributeValue(HOLY.defence), left+70, y, 0x111111);
        font.draw(matrix, drawIconAndString("weight", "weight"), left+96, y=top+16, 0xffffff);
        drawbar(matrix, left+96, y);
        drawCenteredString(font, matrix, player.getAttributeValue(Attributes.ARMOR_TOUGHNESS), left+149, y, 0x111111);
        font.draw(matrix, drawIconAndString("knockback_resistance", "knockback_resistance"), left+96, y+=16, 0xffffff);
        drawbar(matrix, left+96, y);
        drawCenteredPercentage(font, matrix, player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)*100, left+149, y, 0x111111);
        font.draw(matrix, drawIconAndString("evasion", "evasion"), left+96, y+=16, 0xffffff);
        drawbar(matrix, left+96, y);
        drawCenteredPercentage(font, matrix, player.getAttributeValue(IDFAttributes.EVASION.get()), left+149, y, 0x111111);
        font.draw(matrix, drawIconAndString("strike", "strike"), left+96, y+=16, 0xffffff);
        drawbar(matrix, left+96, y);
        drawCenteredPercentage(font, matrix, (player.getAttributeValue(IDFAttributes.STRIKE_MULT.get())*100), left+149, y, getColourLessThan(player.getAttributeValue(IDFAttributes.STRIKE_MULT.get()), 1));
        font.draw(matrix, drawIconAndString("pierce", "pierce"), left+96, y+=16, 0xffffff);
        drawbar(matrix, left+96, y);
        drawCenteredPercentage(font, matrix, (player.getAttributeValue(IDFAttributes.PIERCE_MULT.get())*100), left+149, y, getColourLessThan(player.getAttributeValue(IDFAttributes.PIERCE_MULT.get()), 1));
        font.draw(matrix, drawIconAndString("slash", "slash"), left+96, y+=16, 0xffffff);
        drawbar(matrix, left+96, y);
        drawCenteredPercentage(font, matrix, (player.getAttributeValue(IDFAttributes.SLASH_MULT.get())*100), left+149, y, getColourLessThan(player.getAttributeValue(IDFAttributes.SLASH_MULT.get()), 1));
    }

    private int getColourLessThan(double val, double defaultVal) {
        if (val == defaultVal) return 0x111111;
        else if (val > defaultVal) return ChatFormatting.DARK_RED.getColor();
        else return ChatFormatting.DARK_GREEN.getColor();
    }

    private int getColourGreaterThan(double val, double defaultVal) {
        if (val == defaultVal) return 0x111111;
        else if (val < defaultVal) return ChatFormatting.DARK_RED.getColor();
        else return ChatFormatting.DARK_GREEN.getColor();
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

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == Keybinds.openStats.getKey().getValue() || pKeyCode == KeyEvent.VK_E) {
            this.minecraft.setScreen(null);
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

}

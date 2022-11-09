package net.cwjn.idf.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.util.Keybinds;
import net.cwjn.idf.util.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import static net.cwjn.idf.attribute.IDFAttributes.*;
import static net.minecraft.world.entity.ai.attributes.Attributes.*;

import java.awt.event.KeyEvent;

import static net.cwjn.idf.ImprovedDamageFramework.FONT_ICONS;

public class EquipmentInspectScreen extends Screen {

    private int w, h, currentItemBoxTop, getCurrentItemBoxLeft, selectedItemTop, selectedItemLeft;
    private final int boxWidth = 188, boxHeight = 215;
    private Player player;
    private Font font;
    private final ItemStack hoveredItem, currentItem;
    private static final Style ICON = Style.EMPTY.withFont(FONT_ICONS);

    private static final ResourceLocation currentItemBox = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/item_inspect/current_item.png");
    private static final ResourceLocation hoveredItemBox = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/item_inspect/selected_item.png");

    public EquipmentInspectScreen(ItemStack hovered, ItemStack current) {
        super(Component.translatable("idf.equipment_inspect_screen"));
        hoveredItem = hovered;
        currentItem = current;
    }

    @Override
    protected void init() {
        super.init();
        w = minecraft.getWindow().getGuiScaledWidth();
        h = minecraft.getWindow().getGuiScaledHeight();
        player = minecraft.player;
        font = minecraft.font;
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, currentItemBox);
        blit(matrix, w/2 - boxWidth - 20, h/2 - boxHeight/2, 0, 0, boxWidth, boxHeight, boxWidth, boxHeight);
        RenderSystem.setShaderTexture(0, hoveredItemBox);
        blit(matrix, w/2 + 20, h/2 - boxHeight/2, 0, 0, boxWidth, boxHeight, boxWidth, boxHeight);
        itemRenderer.blitOffset = 100.0F;
        itemRenderer.renderAndDecorateItem(currentItem, w/2 - boxWidth/2 - 27, h/2 - boxHeight/2 + 41);
        itemRenderer.renderAndDecorateItem(hoveredItem, w/2 + 107, h/2 - boxHeight/2 + 41);
        itemRenderer.blitOffset = 0.0F;
        drawCurrentItemStrings(matrix);
    }

    private void drawCurrentItemStrings(PoseStack matrix) {
        // NAME
        Util.drawCenteredString(font, matrix, currentItem.getDisplayName(), w/2 - boxWidth/2, h/2, 0xffffff);
        // DAMAGE/RESISTANCES
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, ATTACK_DAMAGE).
                append(Util.getComponentFromAttribute(currentItem, ARMOR)), w/2 - boxWidth/2  - 70, h/2 + 50, 0xffffff);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, FIRE_DAMAGE.get()).
                append(Util.getComponentFromAttribute(currentItem, FIRE_RESISTANCE.get())), w/2 - boxWidth/2  - 60, h/2 + 100, 0xffffff);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, WATER_DAMAGE.get()).
                append(Util.getComponentFromAttribute(currentItem, WATER_RESISTANCE.get())), w/2 - boxWidth/2 - 60, h/2 + 100, 0xffffff);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, LIGHTNING_DAMAGE.get()).
                append(Util.getComponentFromAttribute(currentItem, LIGHTNING_RESISTANCE.get())), w/2 - boxWidth/2  - 60, h/2 + 150, 0xffffff);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, MAGIC_DAMAGE.get()).
                append(Util.getComponentFromAttribute(currentItem, MAGIC_RESISTANCE.get())), w/2 - boxWidth/2  - 60, h/2 + 200, 0xffffff);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, DARK_DAMAGE.get()).
                append(Util.getComponentFromAttribute(currentItem, DARK_RESISTANCE.get())), w/2 - boxWidth/2 - 60, h/2 + 250, 0xffffff);
        // DAMAGE CLASS MULTIPLIERS
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, STRIKE_MULT.get()), w/2 - boxWidth/2, h/2, 0xffffff);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, PIERCE_MULT.get()), w/2 - boxWidth/2, h/2, 0xffffff);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, SLASH_MULT.get()), w/2 - boxWidth/2, h/2, 0xffffff);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, CRUSH_MULT.get()), w/2 - boxWidth/2, h/2, 0xffffff);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, GENERIC_MULT.get()), w/2 - boxWidth/2, h/2, 0xffffff);
        // OFFENSIVE AUXILIARY
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, LIFESTEAL.get()), w/2 - boxWidth/2, h/2, 0xffffff);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, PENETRATING.get()), w/2 - boxWidth/2, h/2, 0xffffff);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, CRIT_CHANCE.get()), w/2 - boxWidth/2, h/2, 0xffffff);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, ATTACK_KNOCKBACK), w/2 - boxWidth/2, h/2, 0xffffff);
        // DEFENSIVE AUXILIARY
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, EVASION.get()), w/2 - boxWidth/2, h/2, 0xffffff);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, MAX_HEALTH), w/2 - boxWidth/2, h/2, 0xffffff);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, MOVEMENT_SPEED), w/2 - boxWidth/2, h/2, 0xffffff);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, KNOCKBACK_RESISTANCE), w/2 - boxWidth/2, h/2, 0xffffff);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, LUCK), w/2 - boxWidth/2, h/2, 0xffffff);
        // PRIMARY ATTRIBUTES
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, WEIGHT.get()), w/2 - boxWidth/2, h/2, 0xffffff);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, ATTACK_SPEED), w/2 - boxWidth/2, h/2, 0xffffff);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, ARMOR_TOUGHNESS), w/2 - boxWidth/2, h/2, 0xffffff);
        if (currentItem.getTag().contains("idf.damage_class")) Util.drawCenteredString(font, matrix, Util.textComponent(currentItem.getTag().getString("idf.damage_class")), w/2 - boxWidth/2, h/2, 0xffffff);
        else Util.drawCenteredString(font, matrix, Util.textComponent("N/A"), w/2 - boxWidth/2, h/2, 0xffffff);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == Keybinds.inspectItem.getKey().getValue() || pKeyCode == KeyEvent.VK_E) {
            this.minecraft.popGuiLayer();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

}

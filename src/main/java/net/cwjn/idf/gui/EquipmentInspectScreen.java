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

    private final static int WHITE = 0xffffff;
    private int w, h, top, left;
    private final int pWidth = 384, pHeight = 512;
    private Player player;
    private Font font;
    private final ItemStack hoveredItem, currentItem;
    private static final Style ICON = Style.EMPTY.withFont(FONT_ICONS);

    private static final ResourceLocation currentItemBox = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/item_inspect/current_item.png");
    private static final ResourceLocation hoveredItemBox = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/item_inspect/selected_item.png");
    private static final ResourceLocation inspectItemBox = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/item_inspect/item_inspection.png");

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
        top = h/2 -  pHeight/2;
        left = w/2 - pWidth/2;
        player = minecraft.player;
        font = minecraft.font;
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        //RenderSystem.setShaderTexture(0, currentItemBox);
        //blit(matrix, w/2 - boxWidth - 20, h/2 - boxHeight/2, 0, 0, boxWidth, boxHeight, boxWidth, boxHeight);
        //RenderSystem.setShaderTexture(0, hoveredItemBox);
        //blit(matrix, w/2 + 20, h/2 - boxHeight/2, 0, 0, boxWidth, boxHeight, boxWidth, boxHeight);
        RenderSystem.setShaderTexture(0, inspectItemBox);
        blit(matrix, (w - pWidth)/2, (h - pHeight)/2, 0, 0, pWidth, pHeight, pWidth, pHeight);
        //itemRenderer.blitOffset = 100.0F;
        //itemRenderer.renderAndDecorateItem(hoveredItem, w/2 + 107, h/2 - boxHeight/2 + 41);
        //itemRenderer.blitOffset = 0.0F;
        drawCurrentItemStrings(matrix);
    }

    private void drawCurrentItemStrings(PoseStack matrix) {
        // NAME
        Util.drawCenteredString(font, matrix, currentItem.getHoverName(), left, top, WHITE);
        // DAMAGE/RESISTANCES
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, ATTACK_DAMAGE), left + 200, top + 52, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, ARMOR), left + 313, top + 52, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, FIRE_DAMAGE.get()), left  + 200, top + 77, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, FIRE_RESISTANCE.get()), left + 313, top + 77, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, WATER_DAMAGE.get()), left + 200, top + 102, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, WATER_RESISTANCE.get()), left + 313, top + 102, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, LIGHTNING_DAMAGE.get()), left  + 200, top + 127, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, LIGHTNING_RESISTANCE.get()), left + 313, top + 127, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, MAGIC_DAMAGE.get()), left  + 200, top + 152, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, MAGIC_RESISTANCE.get()), left + 313, top + 152, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, DARK_DAMAGE.get()), left + 200, top + 177, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, DARK_RESISTANCE.get()), left + 313, top + 177, WHITE);
        // DAMAGE CLASS MULTIPLIERS
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, STRIKE_MULT.get()), left+ 259, top + 366, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, PIERCE_MULT.get()), left+ 259, top + 391, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, SLASH_MULT.get()), left+ 259, top + 416, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, CRUSH_MULT.get()), left+ 259, top + 441, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, GENERIC_MULT.get()), left+ 259, top + 466, WHITE);
        // OFFENSIVE AUXILIARY
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, LIFESTEAL.get()), left + 259, top + 234, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, PENETRATING.get()), left + 259, top + 259, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, CRIT_CHANCE.get()), left + 259, top + 284, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, ATTACK_KNOCKBACK), left + 259, top + 309, WHITE);
        // DEFENSIVE AUXILIARY
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, EVASION.get()), left + 110, top + 366, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, MAX_HEALTH), left + 110, top + 391, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, MOVEMENT_SPEED), left + 110, top + 416, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, KNOCKBACK_RESISTANCE), left + 110, top + 441, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, LUCK), left + 110, top + 466, WHITE);
        // PRIMARY ATTRIBUTES
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, FORCE.get()), left + 110, top + 284, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, ATTACK_SPEED), left + 110, top + 259, WHITE);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, ARMOR_TOUGHNESS), left + 110, top + 309, WHITE);
        //if (currentItem.getTag().contains("idf.damage_class")) Util.drawCenteredString(font, matrix, Util.textComponent(currentItem.getTag().getString("idf.damage_class")), w/2 - boxWidth/2, h/2, WHITE);
        //else Util.drawCenteredString(font, matrix, Util.textComponent("N/A"), w/2 - boxWidth/2, h/2, WHITE);
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

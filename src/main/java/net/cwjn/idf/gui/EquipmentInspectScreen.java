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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import static net.cwjn.idf.attribute.IDFAttributes.*;
import static net.cwjn.idf.damage.DamageHandler.armourFormula;
import static net.cwjn.idf.damage.DamageHandler.damageFormula;
import static net.cwjn.idf.gui.StatsScreen.Page.*;
import static net.cwjn.idf.util.Color.*;
import static net.cwjn.idf.util.Util.*;
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
        drawStrings(matrix);
    }

    private void drawStrings(PoseStack matrix) {
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, ATTACK_DAMAGE), w/2 - boxWidth/2  - 60, h/2, 0xffffff);
        Util.drawCenteredString(font, matrix, Util.getComponentFromAttribute(currentItem, FIRE_DAMAGE.get()), w/2 - boxWidth/2  - 60, h/2 + 50, 0xffffff);
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

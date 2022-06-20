package net.cwjn.idf.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.Color;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.Keybinds;
import net.cwjn.idf.Util;
import net.cwjn.idf.attribute.AttributeRegistry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

@OnlyIn(Dist.CLIENT)
public class StatsScreen extends Screen {

    private static final ResourceLocation BACKGROUND_TEXTURE =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/stats_screen_gui.png");
    private static final Style optimus = Style.EMPTY.withFont(ImprovedDamageFramework.FONT_OPTIMUS);
    private static final Style pixel = Style.EMPTY.withFont(ImprovedDamageFramework.FONT_PIXEL);
    private static final Style symbolStyle = Style.EMPTY.withFont(ImprovedDamageFramework.FONT_IDF);
    private static final DecimalFormat healthFormat = new DecimalFormat();
    private static final DecimalFormat attributeFormat = new DecimalFormat("#.##");
    static {
        healthFormat.setMinimumFractionDigits(1);
        healthFormat.setMaximumFractionDigits(1);
        healthFormat.setMinimumIntegerDigits(2);
    }


    public StatsScreen() {
        super(new TextComponent("idf:stats_screen"));
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        pPoseStack.pushPose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        int leftPos = width/2 - 128;
        int topPos = height/2 - 128;
        int rightPos = width/2 + 128;
        int botPos = height/2 + 128;
        this.blit(pPoseStack, leftPos, topPos, 0, 0, 256, 256);
        InventoryScreen.renderEntityInInventory(leftPos + 53, topPos + 100, 30, (float)(leftPos + 53) - pMouseX, (float)(topPos + 100 - 50) - pMouseY, this.minecraft.player);
        pPoseStack.popPose();
        pPoseStack.pushPose();
        drawStrings(pPoseStack, leftPos, topPos);
        drawValues(pPoseStack, leftPos, topPos);
        pPoseStack.popPose();
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    private void drawStrings(PoseStack pPoseStack, int leftPos, int topPos) {
        minecraft.font.draw(pPoseStack, new TextComponent(healthFormat.format(this.minecraft.player.getHealth())).withStyle(pixel).
                append(Util.withColor(new TextComponent("/"), Color.WHITESMOKE)).withStyle(pixel).
                append(String.valueOf(healthFormat.format(this.minecraft.player.getAttributeValue(Attributes.MAX_HEALTH)))), (float)(leftPos + 34.5), (topPos + 126), 0xDC143C);
        minecraft.font.draw(pPoseStack, new TextComponent("Physical").withStyle(pixel), (leftPos + 116), (topPos + 22), 0xf5f5dc);
        minecraft.font.draw(pPoseStack, new TextComponent("Fire").withStyle(pixel), (leftPos + 116), (topPos + 28), 0xff5555);
        minecraft.font.draw(pPoseStack, new TextComponent("Water").withStyle(pixel), (leftPos + 116), (topPos + 34), 0x5555ff);
        minecraft.font.draw(pPoseStack, new TextComponent("Lightning").withStyle(pixel), (leftPos + 116), (topPos + 40), 0xffff55);
        minecraft.font.draw(pPoseStack, new TextComponent("Magic").withStyle(pixel), (leftPos + 116), (topPos + 46), 0x55ffff);
        minecraft.font.draw(pPoseStack, new TextComponent("Dark").withStyle(pixel), (leftPos + 116), (topPos + 52), 0xaa00aa);
        minecraft.font.draw(pPoseStack, new TextComponent("Defense").withStyle(pixel), (leftPos + 116), (topPos + 74), 0xffffff);
        minecraft.font.draw(pPoseStack, new TextComponent("Evasion").withStyle(pixel), (leftPos + 116), (topPos + 80), 0xffffff);
        minecraft.font.draw(pPoseStack, new TextComponent("Movespeed").withStyle(pixel), (leftPos + 116), (topPos + 86), 0xffffff);
        minecraft.font.draw(pPoseStack, new TextComponent("Knockback").withStyle(pixel), (leftPos + 116), (topPos + 92), 0xffffff);
        minecraft.font.draw(pPoseStack, new TextComponent("Attack Speed").withStyle(pixel), (leftPos + 116), (topPos + 98), 0xffffff);
        minecraft.font.draw(pPoseStack, new TextComponent("Luck").withStyle(pixel), (leftPos + 116), (topPos + 104), 0xffffff);
    }

    private void drawValues(PoseStack pPoseStack, int leftPos, int topPos) {
        minecraft.font.draw(pPoseStack, new TextComponent(attributeFormat.format(this.minecraft.player.getAttributeValue(Attributes.ATTACK_DAMAGE) + EnchantmentHelper.getDamageBonus(this.minecraft.player.getMainHandItem(), MobType.UNDEFINED)) + "").withStyle(pixel), (leftPos + 177), (topPos + 22), 0xf5f5dc);
        minecraft.font.draw(pPoseStack, new TextComponent(attributeFormat.format(this.minecraft.player.getAttributeValue(Attributes.ARMOR)*3) + "").withStyle(pixel), (leftPos + 206), (topPos + 22), 0xf5f5dc);
        minecraft.font.draw(pPoseStack, new TextComponent(attributeFormat.format(this.minecraft.player.getAttributeValue(AttributeRegistry.FIRE_DAMAGE.get())) + "").withStyle(pixel), (leftPos + 177), (topPos + 28), 0xff5555);
        minecraft.font.draw(pPoseStack, new TextComponent(attributeFormat.format(this.minecraft.player.getAttributeValue(AttributeRegistry.FIRE_RESISTANCE.get())) + "").withStyle(pixel), (leftPos + 206), (topPos + 28), 0xff5555);
        minecraft.font.draw(pPoseStack, new TextComponent(attributeFormat.format(this.minecraft.player.getAttributeValue(AttributeRegistry.WATER_DAMAGE.get())) + "").withStyle(pixel), (leftPos + 177), (topPos + 34), 0x5555ff);
        minecraft.font.draw(pPoseStack, new TextComponent(attributeFormat.format(this.minecraft.player.getAttributeValue(AttributeRegistry.WATER_RESISTANCE.get())) + "").withStyle(pixel), (leftPos + 206), (topPos + 34), 0x5555ff);
        minecraft.font.draw(pPoseStack, new TextComponent(attributeFormat.format(this.minecraft.player.getAttributeValue(AttributeRegistry.LIGHTNING_DAMAGE.get())) + "").withStyle(pixel), (leftPos + 177), (topPos + 40), 0xffff55);
        minecraft.font.draw(pPoseStack, new TextComponent(attributeFormat.format(this.minecraft.player.getAttributeValue(AttributeRegistry.LIGHTNING_RESISTANCE.get())) + "").withStyle(pixel), (leftPos + 206), (topPos + 40), 0xffff55);
        minecraft.font.draw(pPoseStack, new TextComponent(attributeFormat.format(this.minecraft.player.getAttributeValue(AttributeRegistry.MAGIC_DAMAGE.get())) + "").withStyle(pixel), (leftPos + 177), (topPos + 46), 0xffff55);
        minecraft.font.draw(pPoseStack, new TextComponent(attributeFormat.format(this.minecraft.player.getAttributeValue(AttributeRegistry.MAGIC_RESISTANCE.get())) + "").withStyle(pixel), (leftPos + 206), (topPos + 46), 0x55ffff);
        minecraft.font.draw(pPoseStack, new TextComponent(attributeFormat.format(this.minecraft.player.getAttributeValue(AttributeRegistry.DARK_DAMAGE.get())) + "").withStyle(pixel), (leftPos + 177), (topPos + 52), 0xaa00aa);
        minecraft.font.draw(pPoseStack, new TextComponent(attributeFormat.format(this.minecraft.player.getAttributeValue(AttributeRegistry.DARK_RESISTANCE.get())) + "").withStyle(pixel), (leftPos + 206), (topPos + 52), 0xaa00aa);
        minecraft.font.draw(pPoseStack, new TextComponent(attributeFormat.format(this.minecraft.player.getAttributeValue(Attributes.ARMOR_TOUGHNESS)) + "").withStyle(pixel), (leftPos + 206), (topPos + 74), 0xffffff);
        minecraft.font.draw(pPoseStack, new TextComponent(attributeFormat.format(this.minecraft.player.getAttributeValue(AttributeRegistry.EVASION.get())) + "%").withStyle(pixel), (leftPos + 206), (topPos + 80), 0xffffff);
        minecraft.font.draw(pPoseStack, new TextComponent(attributeFormat.format(this.minecraft.player.getAttributeValue(Attributes.MOVEMENT_SPEED)*1000) + "%").withStyle(pixel), (leftPos + 206), (topPos + 86), 0xffffff);
        minecraft.font.draw(pPoseStack, new TextComponent(attributeFormat.format(this.minecraft.player.getAttributeValue(Attributes.ATTACK_KNOCKBACK)) + "").withStyle(pixel), (leftPos + 177), (topPos + 92), 0xffffff);
        minecraft.font.draw(pPoseStack, new TextComponent(attributeFormat.format(this.minecraft.player.getAttributeValue(Attributes.ATTACK_SPEED)) + "").withStyle(pixel), (leftPos + 177), (topPos + 98), 0xffffff);
        minecraft.font.draw(pPoseStack, new TextComponent(attributeFormat.format(this.minecraft.player.getAttributeValue(Attributes.LUCK)) + "").withStyle(pixel), (leftPos + 206), (topPos + 104), 0xffffff);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
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

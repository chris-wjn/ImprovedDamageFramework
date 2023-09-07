package net.cwjn.idf.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.config.json.records.EntityData;
import net.cwjn.idf.config.json.records.EntityTag;
import net.cwjn.idf.data.CommonData;
import net.cwjn.idf.util.Color;
import net.cwjn.idf.util.Keybinds;
import net.cwjn.idf.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.ForgeRegistries;
import oshi.util.tuples.Pair;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static net.cwjn.idf.data.CommonData.*;
import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventory;

public class BestiaryEntryScreen extends Screen {

    ResourceLocation ENTITY_DISPLAY_GUI = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/bestiary_entry.png");
    ResourceLocation ENTITY_INFO_GUI = new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/bestiary_entry_info.png");
    private int width, height, displayLeft, displayTop, infoLeft, infoTop;
    private int DISPLAY_WIDTH = 188, DISPLAY_HEIGHT = 215;
    private int INFO_WIDTH = 328, INFO_HEIGHT = 156;
    private final EntityType<?> type;
    private LivingEntity entity;
    private final int SCALE_FACTOR;
    private static final List<Pair<Integer, Integer>> pos0 = new ArrayList<>();
    private static final List<Pair<Integer, Integer>> pos1 = new ArrayList<>();
    private static final List<Pair<Integer, Integer>> pos2 = new ArrayList<>();
    static {
        pos0.add(new Pair<>(19, 27));
        pos0.add(new Pair<>(19, 37));
        pos0.add(new Pair<>(19, 47));
        pos0.add(new Pair<>(19, 57));
        pos0.add(new Pair<>(19, 67));
        pos0.add(new Pair<>(19, 77));
        pos0.add(new Pair<>(19, 87));
        pos0.add(new Pair<>(19, 97));
        pos0.add(new Pair<>(19, 107));
        pos0.add(new Pair<>(19, 117));
        pos1.add(new Pair<>(119, 27));
        pos1.add(new Pair<>(119, 37));
        pos1.add(new Pair<>(119, 47));
        pos1.add(new Pair<>(119, 57));
        pos1.add(new Pair<>(119, 67));
        pos1.add(new Pair<>(119, 77));
        pos1.add(new Pair<>(119, 87));
        pos1.add(new Pair<>(119, 97));
        pos1.add(new Pair<>(119, 107));
        pos1.add(new Pair<>(119, 117));
        pos2.add(new Pair<>(219, 27));
        pos2.add(new Pair<>(219, 37));
        pos2.add(new Pair<>(219, 47));
        pos2.add(new Pair<>(219, 57));
        pos2.add(new Pair<>(219, 67));
        pos2.add(new Pair<>(219, 77));
        pos2.add(new Pair<>(219, 87));
        pos2.add(new Pair<>(219, 97));
        pos2.add(new Pair<>(219, 107));
        pos2.add(new Pair<>(219, 117));
    }

    protected BestiaryEntryScreen(EntityType<?> type) {
        super(Component.translatable("idf.bestiary_entry_screen"));
        int SCALE_FACTOR1;
        this.type = type;
        SCALE_FACTOR1 = 30*Math.max(type.getHeight(), type.getWidth()) >= 85 ? (int) (90 / Math.max(type.getHeight(), type.getWidth())) : 30;
        if (Util.getEntityRegistryName(type).toString().equals("minecraft:ender_dragon")) {
            SCALE_FACTOR1 = 25;
        }
        SCALE_FACTOR = SCALE_FACTOR1;
    }

    @Override
    protected void init() {
        super.init();
        this.entity = updateMobValues((LivingEntity) type.create(minecraft.level));
        width = minecraft.getWindow().getGuiScaledWidth();
        height = minecraft.getWindow().getGuiScaledHeight();
        displayLeft = (int) ((width - DISPLAY_WIDTH)*0.5);
        displayTop = (int) ((height - DISPLAY_HEIGHT)*0.5 - 100);
        infoLeft = (int) ((width - INFO_WIDTH)*0.5);
        infoTop = (int) ((height - INFO_HEIGHT)*0.5 + 100);
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float pTick) {
        renderBackground(matrix);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, ENTITY_DISPLAY_GUI);
        blit(matrix, displayLeft, displayTop, 140, 17, DISPLAY_WIDTH, DISPLAY_HEIGHT, 477, 240);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, ENTITY_INFO_GUI);
        blit(matrix, infoLeft, infoTop, 62, 42, INFO_WIDTH, INFO_HEIGHT, 477, 240);
        font.draw(matrix, Component.translatable("idf.bestiary_info.health").append(" " + entity.getAttributeValue(Attributes.MAX_HEALTH)), infoLeft+getX(0, 0), infoTop+getY(0, 0), Color.INDIANRED.getColor());
        font.draw(matrix, Component.translatable("idf.bestiary_info.weight").append(" " + entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS)), infoLeft+getX(0,1), infoTop+getY(0,1), Color.BLACK.getColor());
        font.draw(matrix, Component.translatable("idf.bestiary_info.speed").append(" " + Util.mBPS(entity.getAttributeValue(Attributes.MOVEMENT_SPEED)) + "bps"), infoLeft+getX(0,2), infoTop+getY(0,2), Color.GREEN.getColor());
        int x = 2, y = 2, x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        for (Attribute a : AUXILIARY_ATTRIBUTES) {
            if (entity.getAttribute(a) != null) {
                if (entity.getAttributeValue(a) != 0) font.draw(matrix, Component.translatable("idf.bestiary_info." + a.getDescriptionId()).append(" " + entity.getAttributeValue(a)), infoLeft+getX(0,++x), infoTop+getY(0,++y), Color.DARKSLATEGREY.getColor());
            }
        }
        for (Attribute a : DEFENSIVE_ATTRIBUTES) {
            if (entity.getAttribute(a) != null) {
                if (entity.getAttributeValue(a) != 0) font.draw(matrix, Component.translatable("idf.bestiary_info." + a.getDescriptionId()).append(" " + entity.getAttributeValue(a)), infoLeft+getX(1,x1++), infoTop+getY(1,y1++), Color.DARKSLATEGREY.getColor());
            }
        }
        for (Attribute a : OFFENSIVE_ATTRIBUTES) {
            if (entity.getAttribute(a) != null) {
                if (entity.getAttributeValue(a) != 0) font.draw(matrix, Component.translatable("idf.bestiary_info." + a.getDescriptionId()).append(" " + entity.getAttributeValue(a)), infoLeft+getX(2,x2++), infoTop+getY(2,y2++), Color.DARKSLATEGREY.getColor());
            }
        }
        renderEntityInInventory((int) (displayLeft + DISPLAY_WIDTH*0.5), (int) ((displayTop + DISPLAY_HEIGHT*0.5) + type.getHeight() * SCALE_FACTOR *0.5), SCALE_FACTOR, (float)(width*0.5) - mouseX, (float) ((displayTop + DISPLAY_HEIGHT*0.5) - mouseY - type.getHeight()* SCALE_FACTOR * 0.5), entity);
    }

    private void drawAttributeOrNA(PoseStack matrix, Attribute attribute, int i, int i1, int color) {
        if (entity.getAttribute(attribute) != null) {
            drawString(matrix, font, Component.translatable("idf.bestiary_info." + attribute.getDescriptionId()).append(" " + entity.getAttributeValue(attribute)), i, i1, color);
        }
        else {
            drawString(matrix, font, Component.translatable("idf.bestiary_info." + attribute.getDescriptionId()).append(" N/A"), i, i1, Color.BLACK.getColor());
        }
    }

    private static int getX(int i, int index) {
        if (i == 0) {
            return pos0.get(index).getA();
        } else if (i == 1) {
            return pos1.get(index).getA();
        } else {
            return pos2.get(index).getA();
        }
    }

    private static int getY(int i, int index) {
        if (i == 0) {
            return pos0.get(index).getB();
        } else if (i == 1) {
            return pos1.get(index).getB();
        } else {
            return pos2.get(index).getB();
        }
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        if (pMinecraft.screen == this) {
            pMinecraft.setScreen(null);
            pMinecraft.pauseGame(false);
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == Keybinds.openBestiary.getKey().getValue() || pKeyCode == KeyEvent.VK_E) {
            this.minecraft.popGuiLayer();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    private static LivingEntity updateMobValues(LivingEntity entity) {
        //SECOND SECTION: scale the entity's damage and health up
        if (entity.getPersistentData().getBoolean(ENTITY_BONUS)) return entity;
        AttributeInstance healthInstance = entity.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance damageInstance = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damageInstance != null) damageInstance.setBaseValue(damageInstance.getBaseValue() * 2);
        healthInstance.setBaseValue(healthInstance.getBaseValue() * 5);

        //THIRD SECTION: attach bonus attributes defined in entity_data.json
        EntityData data = CommonData.LOGICAL_ENTITY_MAP.get(Util.getEntityRegistryName(entity.getType()));
        if (data != null) {
            if (damageInstance != null) damageInstance.setBaseValue(damageInstance.getBaseValue() + data.oData().pDmg());
            entity.getAttribute(Attributes.ARMOR).setBaseValue(entity.getAttributeBaseValue(Attributes.ARMOR) + data.dData().pDef());
            entity.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(entity.getAttributeBaseValue(Attributes.ARMOR_TOUGHNESS) + data.dData().weight());
            entity.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(entity.getAttributeBaseValue(Attributes.ATTACK_KNOCKBACK) + data.oData().kb());
            healthInstance.setBaseValue(healthInstance.getBaseValue() + data.aData().hp());
            entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(entity.getAttributeBaseValue(Attributes.KNOCKBACK_RESISTANCE) + data.dData().kbr());
            entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(entity.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * data.aData().ms());
            //FOURTH SECTION: give the entity it's attribute modifiers defined by its tags
            for (EntityTag e : data.tags()) {
                for (Iterator<Pair<Attribute, Double>> it = e.getOffensiveData().getAttributesWithModifier(); it.hasNext(); ) {
                    Pair<Attribute, Double> p = it.next();
                    setNewBaseValue(entity, p.getA(), p.getB());
                }
                for (Iterator<Pair<Attribute, Double>> it = e.getDefensiveData().getAttributesWithModifier(); it.hasNext(); ) {
                    Pair<Attribute, Double> p = it.next();
                    setNewBaseValue(entity, p.getA(), p.getB());
                }
                for (Iterator<Pair<Attribute, Double>> it = e.getAuxiliaryData().getAttributesWithModifier(); it.hasNext(); ) {
                    Pair<Attribute, Double> p = it.next();
                    if (p.getA() == Attributes.MOVEMENT_SPEED) {
                        setNewBaseValueByMultiplication(entity, p.getA(), p.getB());
                    } else {
                        setNewBaseValue(entity, p.getA(), p.getB());
                    }
                }
            }
        }
        return entity;
    }

    private static void setNewBaseValue(LivingEntity e, Attribute a, Double val) {
        AttributeInstance instance = e.getAttribute(a);
        if (instance == null) return;
        instance.setBaseValue(val + instance.getBaseValue());
    }

    private static void setNewBaseValueByMultiplication(LivingEntity e, Attribute a, Double val) {
        AttributeInstance instance = e.getAttribute(a);
        if (instance == null) return;
        instance.setBaseValue(val * instance.getBaseValue());
    }

}

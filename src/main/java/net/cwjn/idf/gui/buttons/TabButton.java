package net.cwjn.idf.gui.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.gui.StatScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TabButton extends AbstractButton {

    private final boolean selected;
    private final TabType type;
    private static final ResourceLocation STAT_GUI =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/inventorytabs.png");

    public TabButton(int x, int y, TabType type, boolean selected) {
        super(x, y, 31, 28, Component.empty());
        this.type = type;
        this.selected = selected;
    }

    @Override
    public void render(@NotNull PoseStack matrix, int mouseX, int mouseY, float pTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.screen instanceof InventoryScreen) || !((InventoryScreen) minecraft.screen).getRecipeBookComponent().isVisible()) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, STAT_GUI);
            blit(matrix, x, y, selected ? 31 : 0, 166, width, height);
            blit(matrix, x + (selected ? 8 : 9), y + 6, 176, type.iconIndex * 16, 16, 16);
        }
    }

    @Override
    public void onPress() {
        Minecraft minecraft = Minecraft.getInstance();
        switch (type) {
            case INVENTORY -> minecraft.setScreen(new InventoryScreen(minecraft.player));
            case STATS -> minecraft.setScreen(new StatScreen());
        }
    }

    @Override
    public void updateNarration(@NotNull NarrationElementOutput p_169152_) {

    }

    public enum TabType {
        INVENTORY(0),
        STATS(1);
        public final int iconIndex;
        TabType(int index) {
            iconIndex = index;
        }
    }

}

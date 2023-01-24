package net.cwjn.idf.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static net.cwjn.idf.util.Util.translationComponent;

@OnlyIn(Dist.CLIENT)
public class StatScreen extends Screen {

    private static final ResourceLocation STAT_GUI =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/inventorytabs.png");

    public StatScreen() {
        super(translationComponent("idf.stats_screen"));
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float pTicks) {
        int left = (width - 176) / 2;
        int top = (height - 166) / 2;
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


}

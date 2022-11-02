package net.cwjn.idf.gui.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.util.Color;
import net.cwjn.idf.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CheckboxButton extends AbstractButton {

    private boolean checked;

    public CheckboxButton(int x, int y, String buttonText, boolean checked) {
        super(x, y, 10, 10, Util.translationComponent(buttonText));
        this.checked = checked;
    }

    @Override
    public void onPress() {
        this.checked = !checked;
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            RenderSystem.setShaderTexture(0, new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/checkbox.png"));
            blit(matrix, x, y, 0, 0, 10, 10);
            if (checked) {
                blit(matrix, x, y, 10, 0, 10, 10);
            }
            drawString(matrix, Minecraft.getInstance().font, getMessage().getString(), x + width + 3, y + 2, new Color(255, 255, 255).hashCode());
        }
    }

    @Override
    public int getWidth() {
        return super.getWidth() + 3 + Minecraft.getInstance().font.width(getMessage());
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

    }

}

package net.cwjn.idf.gui.buttons;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class InvisibleButton extends Button {

    public InvisibleButton(int x, int y, int width, int height, Component component, OnPress onPress) {
        super(x, y, width, height, component, onPress);
    }

    @Override
    public void renderButton(PoseStack p_93746_, int p_93747_, int p_93748_, float p_93749_) {

    }
}

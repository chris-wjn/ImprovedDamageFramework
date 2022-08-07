package net.cwjn.idf.gui.buttons;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.gui.StatButton;
import net.minecraft.resources.ResourceLocation;

public class BackButton extends StatButton {

    private static final ResourceLocation UNFOCUSED =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/back_button_unfocused.png");
    private static final ResourceLocation FOCUSED =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/back_button_focused.png");

    public BackButton(int x, int y, OnPress functionSupplier) {
        super(x, y, 71, 28, functionSupplier, UNFOCUSED, FOCUSED);
    }

}

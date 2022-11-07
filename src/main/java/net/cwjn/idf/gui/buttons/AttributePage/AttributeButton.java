package net.cwjn.idf.gui.buttons.AttributePage;

import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.resources.ResourceLocation;

public class AttributeButton extends StatButton {

    private static final ResourceLocation ATTRIBUTE_BUTTON_UNFOCUSED =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/attribute_button_unfocused.png");
    private static final ResourceLocation ATTRIBUTE_BUTTON_FOCUSED =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/attribute_button_focused.png");

    public AttributeButton(int x, int y, OnPress functionSupplier) {
        super(x, y, 328, 42, functionSupplier, ATTRIBUTE_BUTTON_UNFOCUSED, ATTRIBUTE_BUTTON_FOCUSED);
    }

}

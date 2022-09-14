package net.cwjn.idf.gui.buttons;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.gui.StatButton;
import net.minecraft.resources.ResourceLocation;

public class StatsButton extends StatButton {

    private static final ResourceLocation STATS_BUTTON_UNFOCUSED =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/stat_button_unfocused.png");
    private static final ResourceLocation STATS_BUTTON_FOCUSED =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/stat_button_focused.png");

    public StatsButton(int x, int y, OnPress functionSupplier) {
        super(x, y, 222, 42, functionSupplier, STATS_BUTTON_UNFOCUSED, STATS_BUTTON_FOCUSED);
    }

}

package net.cwjn.idf.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientTextTooltip.class)
public class MixinClientTextTooltip {

    @Redirect(method = "renderText", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/util/FormattedCharSequence;FFIZLcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;ZII)I"
    ))
    public int drawNoShadow(Font font, FormattedCharSequence sequence, float f1, float f2, int i1, boolean b1, Matrix4f m4f, MultiBufferSource buffer, boolean b2, int i2, int i3) {
        return font.drawInBatch(sequence, f1, f2, -1, false, m4f, buffer, false, 0, 15728880);
    }

}

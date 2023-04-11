package net.cwjn.idf.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer<T extends Entity> {

    private static final ResourceLocation HEALTH_BAR_GUI =
            new ResourceLocation(ImprovedDamageFramework.MOD_ID, "textures/gui/healthgui.png");
    private static final Minecraft client = Minecraft.getInstance();

    @Inject(method = "renderNameTag", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack$Pose;pose()Lcom/mojang/math/Matrix4f;", shift = At.Shift.AFTER))
    private void injectHealthBar(T pEntity, Component pDisplayName, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, CallbackInfo callback) {
        RenderSystem.setShaderTexture(0, HEALTH_BAR_GUI);
        client.gui.blit(pMatrixStack, 0, 0, 0, 26, 72, 13);
    }

}

package net.cwjn.idf.hud;

import com.ibm.icu.impl.Pair;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.data.ClientData;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobHealthbar {

    private static final ResourceLocation HEALTH_GUI = new ResourceLocation(
            ImprovedDamageFramework.MOD_ID, "textures/gui/healthbar.png");
    
    private static final Minecraft client = Minecraft.getInstance();

    private static final Map<LivingEntity, Float> renderables = new HashMap<>();

    public static void prepare(LivingEntity entity, float alpha) {
        renderables.put(entity, alpha);
    }

    public static void renderBars(float pTick, PoseStack matrix, Camera cam) {
        
        if (cam == null) {
            cam = client.getEntityRenderDispatcher().camera;
        }
        if (cam == null) {
            renderables.clear();
            return;
        }
        if (renderables.isEmpty()) {
            return;
        }

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE,
                GL11.GL_ZERO);

        for (LivingEntity entity : renderables.keySet()) {
            float scaleToGui = 0.025f;
            boolean sneaking = entity.isCrouching();
            float height = entity.getBbHeight() + 0.73F - (sneaking ? 0.25F : 0.0F);

            double x = Mth.lerp(pTick, entity.xo, entity.getX());
            double y = Mth.lerp(pTick, entity.yo, entity.getY());
            double z = Mth.lerp(pTick, entity.zo, entity.getZ());

            Vec3 camPos = cam.getPosition();
            double camX = camPos.x();
            double camY = camPos.y();
            double camZ = camPos.z();

            matrix.pushPose();
            matrix.translate(x - camX, (y + height) - camY, z - camZ);
            matrix.mulPose(Vector3f.YP.rotationDegrees(-cam.getYRot()));
            matrix.mulPose(Vector3f.XP.rotationDegrees(cam.getXRot()));
            matrix.scale(-scaleToGui, -scaleToGui, scaleToGui);

            renderBar(matrix, entity, renderables.get(entity));

            matrix.popPose();
        }

        RenderSystem.disableBlend();
        renderables.clear();
    }

    private static void renderBar(PoseStack matrix, LivingEntity entity, float alpha) {
        float percent = Math.min(1, Math.min(entity.getHealth(), entity.getMaxHealth()) / entity.getMaxHealth());
        Matrix4f m4f = matrix.last().pose();
        drawBar(m4f, 1, 0, true, alpha);
        drawBar(m4f, percent, 1, false, alpha);
    }

    private static void drawBar(Matrix4f matrix, float percent, int zOffset, boolean background, float alpha) {
        float c = 0.00390625F;
        int u = 0;
        int v = background ? 0 : 8;
        int uw = Mth.ceil(81 * percent);
        int vh = 6;

        double size = percent * (float) 40.0;
        double h = 6;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, HEALTH_GUI);
        RenderSystem.enableBlend();
        if (ClientData.shadersLoaded) {
            RenderSystem.setShaderColor(0.4f, 0.4f, 0.4f, alpha);
        }
        else {
            RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        }

        float half = (float) 40.0 / 2;

        float zOffsetAmount = -0.1F;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, (float) (-half + (double) 0), (float) (double) 0, zOffset * zOffsetAmount)
                .uv(u * c, v * c).endVertex();
        buffer.vertex(matrix, (float) (-half + (double) 0), (float) (h + (double) 0), zOffset * zOffsetAmount)
                .uv(u * c, (v + vh) * c).endVertex();
        buffer.vertex(matrix, (float) (-half + size + (double) 0), (float) (h + (double) 0), zOffset * zOffsetAmount)
                .uv((u + uw) * c, (v + vh) * c).endVertex();
        buffer.vertex(matrix, (float) (-half + size + (double) 0), (float) (double) 0, zOffset * zOffsetAmount)
                .uv(((u + uw) * c), v * c).endVertex();
        tesselator.end();

        RenderSystem.disableBlend();

    }


}

package net.cwjn.idf.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class MobHealthbar {

    private static final ResourceLocation HEALTH_GUI = new ResourceLocation(
            ImprovedDamageFramework.MOD_ID, "textures/gui/healthbar.png");
    
    private static final Minecraft client = Minecraft.getInstance();

    private static final List<LivingEntity> renderables = new ArrayList<>();

    public static void prepare(LivingEntity entity) {
        renderables.add(entity);
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

        for (LivingEntity entity : renderables) {
            float scaleToGui = 0.025f;
            boolean sneaking = entity.isCrouching();
            float height = entity.getBbHeight() + 0.6F - (sneaking ? 0.25F : 0.0F);

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

            renderBar(matrix, entity);

            matrix.popPose();
        }

        RenderSystem.disableBlend();
        renderables.clear();
    }

    private static void renderBar(PoseStack matrix, LivingEntity entity) {
        float percent = Math.min(1, Math.min(entity.getHealth(), entity.getMaxHealth()) / entity.getMaxHealth());
        Matrix4f m4f = matrix.last().pose();
        drawBar(m4f, 0, 0, (float) 40, 1, 0, true);
        drawBar(m4f, 0, 0, (float) 40, percent, 1, false);
    }

    private static void drawBar(Matrix4f matrix, double x, double y, float width, float percent, int zOffset, boolean background) {
        float c = 0.00390625F;
        int u = 0;
        int v = background ? 0 : 8;
        int uw = Mth.ceil(81 * percent);
        int vh = 6;

        double size = percent * width;
        double h = 6;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, HEALTH_GUI);
        RenderSystem.enableBlend();

        float half = width / 2;

        float zOffsetAmount = -0.1F;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, (float) (-half + x), (float) y, zOffset * zOffsetAmount)
                .uv(u * c, v * c).endVertex();
        buffer.vertex(matrix, (float) (-half + x), (float) (h + y), zOffset * zOffsetAmount)
                .uv(u * c, (v + vh) * c).endVertex();
        buffer.vertex(matrix, (float) (-half + size + x), (float) (h + y), zOffset * zOffsetAmount)
                .uv((u + uw) * c, (v + vh) * c).endVertex();
        buffer.vertex(matrix, (float) (-half + size + x), (float) y, zOffset * zOffsetAmount)
                .uv(((u + uw) * c), v * c).endVertex();
        tesselator.end();

        RenderSystem.disableBlend();

    }


}

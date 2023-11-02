package net.cwjn.idf.hud;

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

import java.util.HashMap;
import java.util.Map;

public class MobHealthbar {

    private static final int MAX_BAR_WIDTH = 75;

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
            float alpha = renderables.get(entity);
            if (alpha <= 0.05) continue;
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
        //get values from entity
        float currentHealth = entity.getHealth();
        float maxHealth = entity.getMaxHealth();
        float percent = Math.min(1, Math.min(currentHealth, maxHealth) / maxHealth);
        float percentOfMax = Math.min(10/maxHealth, 1);

        //draw bars
        Matrix4f m4f = matrix.last().pose();
        drawBar(m4f, 1, 0, true, alpha, 0);
        drawBar(m4f, percent, 1, false, alpha, percentOfMax);
    }

    private static void drawBar(Matrix4f matrix, float percent, int zOffset, boolean background, float alpha, float percentOfMax) {
        float CONSTANT = 0.00390625F;
        int xTexOffset = 3;
        int yTexOffset = background ? 34 : 30;
        int width = Mth.ceil(81 * percent);
        int height = 4;
        float half = (float) MAX_BAR_WIDTH / 2;
        float zOffsetAmount = -0.1F;
        double size = percent * (float) MAX_BAR_WIDTH;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, HEALTH_GUI);
        RenderSystem.enableBlend();
        if (ClientData.shadersLoaded) {
            RenderSystem.setShaderColor(0.4f, 0.4f, 0.4f, alpha);
        }
        else {
            RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        }

        //Red bar
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, (float) (-half + (double) 0), (float) (double) -0.1, zOffset * zOffsetAmount)
                .uv(xTexOffset * CONSTANT, yTexOffset * CONSTANT).endVertex();
        buffer.vertex(matrix, (float) (-half + (double) 0), (float) (height + (double) 0.1), zOffset * zOffsetAmount)
                .uv(xTexOffset * CONSTANT, (yTexOffset + height) * CONSTANT).endVertex();
        buffer.vertex(matrix, (float) (-half + size + (double) 0), (float) (height + (double) 0.1), zOffset * zOffsetAmount)
                .uv((xTexOffset + width) * CONSTANT, (yTexOffset + height) * CONSTANT).endVertex();
        buffer.vertex(matrix, (float) (-half + size + (double) 0), (float) (double) -0.1, zOffset * zOffsetAmount)
                .uv(((xTexOffset + width) * CONSTANT), yTexOffset * CONSTANT).endVertex();
        tesselator.end();

        if (!background) {
            //Border
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            buffer.vertex(matrix, -half - 2.7f, -3, (zOffset+1)*zOffsetAmount) //top left
                    .uv(0, 38*CONSTANT).endVertex();
            buffer.vertex(matrix, -half - 2.7f, -3 + 10, (zOffset+1)*zOffsetAmount) //bottom left
                    .uv(0, 48*CONSTANT).endVertex();
            buffer.vertex(matrix, -half + MAX_BAR_WIDTH + 2.6f, -3 + 10, (zOffset+1)*zOffsetAmount) //bottom right
                    .uv(87*CONSTANT, 48*CONSTANT).endVertex();
            buffer.vertex(matrix, -half + MAX_BAR_WIDTH + 2.6f, -3, (zOffset+1)*zOffsetAmount) //top right
                    .uv(87*CONSTANT, 38*CONSTANT).endVertex();
            tesselator.end();

            //Dividers
            float dividerSpacing = MAX_BAR_WIDTH*percentOfMax;
            float f = -half + dividerSpacing;
            float dividerWidth = (float) Mth.clamp(dividerSpacing*0.1, 0.25f, 0.5f);
            while (f < (MAX_BAR_WIDTH - half)) {
                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                buffer.vertex(matrix, f, 0, (zOffset + 1) * zOffsetAmount) //top left
                        .uv(2 * CONSTANT, 41 * CONSTANT).endVertex();
                buffer.vertex(matrix, f, 4, (zOffset + 1) * zOffsetAmount) //bottom left
                        .uv(2 * CONSTANT, 43 * CONSTANT).endVertex();
                buffer.vertex(matrix, f + dividerWidth, 4, (zOffset + 1) * zOffsetAmount) //bottom right
                        .uv(3 * CONSTANT, 43 * CONSTANT).endVertex();
                buffer.vertex(matrix, f + dividerWidth, 0, (zOffset + 1) * zOffsetAmount) //top right
                        .uv(3 * CONSTANT, 41 * CONSTANT).endVertex();
                tesselator.end();
                f += dividerSpacing;
            }
        }

        RenderSystem.disableBlend();

    }


}

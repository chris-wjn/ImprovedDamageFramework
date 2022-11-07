package net.cwjn.idf.rpg.bonfire.block;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.cwjn.idf.rpg.bonfire.entity.BonfireBlockEntity;
import net.cwjn.idf.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BonfireSwordRenderer implements BlockEntityRenderer<BonfireBlockEntity> {

    BlockEntityRendererProvider.Context context;
    public BonfireSwordRenderer(BlockEntityRendererProvider.Context ctx) {
        context = ctx;
    }

    @Override
    public void render(BonfireBlockEntity be, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int light, int overlay) {
        if (be.isActive()) {
            renderName(be, partialTicks, matrix, buffer, light);
            matrix.pushPose();
            matrix.translate(0.5, 0.65, 0.5);
            if (Minecraft.getInstance().level.getBlockState(be.getBlockPos()).getBlock() == BonfireBlockRegistry.BONFIRE_BASE.get()) {
                if (Minecraft.getInstance().level.getBlockState(be.getBlockPos()).getValue(BonfireBlock.FACING) == Direction.NORTH) {
                    matrix.mulPose(Vector3f.YP.rotationDegrees(0));
                } else if (Minecraft.getInstance().level.getBlockState(be.getBlockPos()).getValue(BonfireBlock.FACING) == Direction.EAST) {
                    matrix.mulPose(Vector3f.YP.rotationDegrees(90));
                } else if (Minecraft.getInstance().level.getBlockState(be.getBlockPos()).getValue(BonfireBlock.FACING) == Direction.SOUTH) {
                    matrix.mulPose(Vector3f.YP.rotationDegrees(180));
                } else if (Minecraft.getInstance().level.getBlockState(be.getBlockPos()).getValue(BonfireBlock.FACING) == Direction.WEST) {
                    matrix.mulPose(Vector3f.YP.rotationDegrees(270));
                }
            }
            matrix.mulPose(Vector3f.ZP.rotationDegrees(-130));
            Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(Items.IRON_SWORD), ItemTransforms.TransformType.NONE, light, overlay, matrix, buffer, 0);
            matrix.popPose();
        }
    }

    private void renderName(BonfireBlockEntity be, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int light) {
        if (Util.lookingAt(partialTicks, be)) {
            float f = (float) (be.getBlockState().getCollisionShape(Minecraft.getInstance().level, be.getBlockPos()).max(Direction.Axis.Y) + 0.5F);
            matrix.pushPose();
            matrix.translate(0.5D, f, 0.5D);
            matrix.mulPose(context.getBlockEntityRenderDispatcher().camera.rotation());
            matrix.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = matrix.last().pose();
            Font fontrenderer = context.getFont();
            float f2 = (float)(-fontrenderer.width(be.getName()) / 2);
            RenderSystem.enableBlend();
            fontrenderer.drawInBatch(be.getName(), f2, 0, 0xFFFFFF, true, matrix4f, buffer, false, 0, light);
            RenderSystem.disableBlend();
            matrix.popPose();
        }
    }

}

package net.cwjn.idf.particle.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class NumberParticle extends Particle {

    private static final Minecraft client = Minecraft.getInstance();
    private static final Style compacta = Style.EMPTY.withFont(ImprovedDamageFramework.FONT_COMPACTA);
    private static final Style indicator = Style.EMPTY.withFont(ImprovedDamageFramework.FONT_INDICATORS);
    private final float number;
    private final float weight;
    private final int colour;

    protected NumberParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet, float f, int i) {
        super(level, x, y, z, vx, vy, vz);

        gravity = 0.981f;
        weight = 0.03f; //kg
        hasPhysics = false;
        xd = vx;
        yd = vy;
        zd = vz;
        lifetime = 40;
        rCol = 1;
        gCol = 1;
        bCol = 1;
        alpha = 1;
        number = f;
        colour = i;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.yd -= gravity*weight;
            this.move(this.xd, this.yd, this.zd);
            if (this.speedUpWhenYMotionIsBlocked && this.y == this.yo) {
                this.xd *= 1.1D;
                this.zd *= 1.1D;
            }
            this.xd *= this.friction;
            this.yd *= this.friction;
            this.zd *= this.friction;
            if (this.onGround) {
                this.xd *= 0.7F;
                this.zd *= 0.7F;
            }
            this.alpha = (-(1/(float)lifetime) * age + 1);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        PoseStack stack = new PoseStack();
        MutableComponent component = Util.textComponent(String.valueOf((int) Math.ceil(number))).withStyle(indicator);
        Vec3 cam = pRenderInfo.getPosition();
        double displayX = Mth.lerp(pPartialTicks, xo, x) - cam.x;
        double displayY = Mth.lerp(pPartialTicks, yo, y) - cam.y;
        double displayZ = Mth.lerp(pPartialTicks, zo, z) - cam.z;
        stack.pushPose();
        stack.translate(displayX, displayY, displayZ);
        stack.mulPose(Vector3f.YP.rotationDegrees(-pRenderInfo.getYRot()));
        stack.mulPose(Vector3f.XP.rotationDegrees(pRenderInfo.getXRot()));
        stack.scale(-0.025f, -0.025f, 0.025f);
        client.font.draw(stack, component, 0, 0, colour);
        stack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<NumberParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(@NotNull NumberParticleType particleType, @NotNull ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new NumberParticle(level, x, y, z, dx, dy, dz, sprites, particleType.getDamageNumber(), particleType.getColour());
        }
    }

}

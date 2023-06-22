package net.cwjn.idf.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MissParticle extends TextureSheetParticle {

    protected MissParticle(ClientLevel level, double x, double y, double z,
                           SpriteSet set, double vx, double vy, double vz) {
        super(level, x, y, z, vx, vy, vz);
        gravity = 0.981f;
        friction = 0.83f;
        hasPhysics = false;
        xd = vx;
        yd = vy;
        zd = vz;
        lifetime = 40;
        this.setSpriteFromAge(set);
        rCol = 1;
        gCol = 1;
        bCol = 1;
        alpha = 1;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
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
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Provider(SpriteSet set) {
            this.sprites = set;
        }
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new MissParticle(level, x, y, z, sprites, vx, vy, vz);
        }
    }

}

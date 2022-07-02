package net.cwjn.idf.particle.custom;

import net.cwjn.idf.event.hook.DamageIndicatorEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NumberParticle extends TextureSheetParticle {

    protected NumberParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z, vx, vy, vz);

        gravity = 0.75f;
        hasPhysics = false;
        friction = 0.8f;
        xd = vx;
        yd = vy;
        zd = vz;
        quadSize *= 0.85f;
        lifetime = 20;
        setSpriteFromAge(spriteSet);
        float[] colours = DamageIndicatorEvents.colour;
        System.out.println(colours[0] + ", " + colours[1] + ", " + colours[2]);
        rCol = colours[0];
        gCol = colours[1];
        bCol = colours[2];
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new NumberParticle(level, x, y, z, dx, dy, dz, sprites);
        }
    }

}

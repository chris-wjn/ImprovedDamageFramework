package net.cwjn.idf.particle.custom.numbers;

import net.cwjn.idf.particle.custom.NumberParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;

public class Five extends NumberParticle {

    protected Five(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z, vx, vy, vz, spriteSet);
    }

}

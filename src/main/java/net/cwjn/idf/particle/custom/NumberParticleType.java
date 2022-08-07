package net.cwjn.idf.particle.custom;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class NumberParticleType extends ParticleType<NumberParticleType> implements ParticleOptions {

    private float damageNumber = 0;
    private int colour = 0xffffff;

    private static final ParticleOptions.Deserializer<NumberParticleType> DESERIALIZER = new ParticleOptions.Deserializer<NumberParticleType>() {
        public @NotNull NumberParticleType fromCommand(@NotNull ParticleType<NumberParticleType> p_123846_, @NotNull StringReader p_123847_) {
            return (NumberParticleType)p_123846_;
        }

        public @NotNull NumberParticleType fromNetwork(@NotNull ParticleType<NumberParticleType> p_123849_, @NotNull FriendlyByteBuf p_123850_) {
            return (NumberParticleType)p_123849_;
        }
    };
    private final Codec<NumberParticleType> codec = Codec.unit(this::getType);

    public NumberParticleType(boolean pOverrideLimiter) {
        super(pOverrideLimiter, DESERIALIZER);
    }

    public @NotNull NumberParticleType getType() {
        return this;
    }

    public @NotNull Codec<NumberParticleType> codec() {
        return this.codec;
    }

    public void writeToNetwork(@NotNull FriendlyByteBuf pBuffer) {
    }

    public NumberParticleType setColour(int colour) {
        this.colour = colour;
        return this;
    }

    public int getColour() {
        return colour;
    }

    public NumberParticleType setNumber(float f) {
        damageNumber = f;
        return this;
    }

    public float getDamageNumber() {
        return damageNumber;
    }

    public @NotNull String writeToString() {
        return Registry.PARTICLE_TYPE.getKey(this).toString();
    }
}

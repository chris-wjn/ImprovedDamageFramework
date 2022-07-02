package net.cwjn.idf.event.hook;

import net.cwjn.idf.event.post.PostMitigationDamageEvent;
import net.cwjn.idf.network.DisplayDamageIndicatorsMessage;
import net.cwjn.idf.network.IDFPacketHandler;
import net.cwjn.idf.particle.IDFParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.Random;

@Mod.EventBusSubscriber
public class DamageIndicatorEvents {

    private static final float[] MAGIC = {0f, 1.0f, 1.0f};
    private static final float[] DARK = {0.5f, 0, 0.5f};
    private static final float[] LIGHTNING = {1.0f, 1.0f, 0f};
    private static final float[] WATER = {0f, 0f, 1.0f};
    private static final float[] FIRE = {1f, 0.65f, 0f};
    private static final float[] PHYSICAL = {0.96f, 0.96f, 0.86f};
    public static float[] colour;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPostMitigationDamage(PostMitigationDamageEvent event) {
        LivingEntity target = event.getTarget();
        Level world = target.getLevel();
        if (!world.isClientSide()) {
            double x = target.getX();
            double y = target.getEyeY();
            double z = target.getZ();
            if (event.getFire() > 0) {
                IDFPacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getX(), target.getY(), target.getZ(), 30d, target.getCommandSenderWorld().dimension())),
                        new DisplayDamageIndicatorsMessage(x, y, z, event.getFire(), FIRE));
            }
            if (event.getWater() > 0) {
                IDFPacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getX(), target.getY(), target.getZ(), 30d, target.getCommandSenderWorld().dimension())),
                        new DisplayDamageIndicatorsMessage(x, y, z, event.getWater(), WATER));
            }
            if (event.getLightning() > 0) {
                IDFPacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getX(), target.getY(), target.getZ(), 30d, target.getCommandSenderWorld().dimension())),
                        new DisplayDamageIndicatorsMessage(x, y, z, event.getLightning(), LIGHTNING));
            }
            if (event.getMagic() > 0) {
                IDFPacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getX(), target.getY(), target.getZ(), 30d, target.getCommandSenderWorld().dimension())),
                        new DisplayDamageIndicatorsMessage(x, y, z, event.getMagic(), MAGIC));
            }
            if (event.getDark() > 0) {
                IDFPacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getX(), target.getY(), target.getZ(), 30d, target.getCommandSenderWorld().dimension())),
                        new DisplayDamageIndicatorsMessage(x, y, z, event.getDark(), DARK));
            }
            if (event.getPhysical() > 0) {
                IDFPacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getX(), target.getY(), target.getZ(), 30d, target.getCommandSenderWorld().dimension())),
                        new DisplayDamageIndicatorsMessage(x, y, z, event.getPhysical(), PHYSICAL));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void addDamageIndicatorParticle(double x, double y, double z, float f, double xOffset, double yOffset, double zOffset, float[] c) {
        Level world = Minecraft.getInstance().level;
        if (world != null) {
            //this means the f is a single digit. Base case.
            if (f < 10) {
                colour = c;
                world.addParticle(getParticleForDigit((int) f), x, y + yOffset, z, xOffset/4, 0.35, zOffset/4);
            }
            //otherwise, add the particle for the last digit then recursively iterate through the whole number.
            else {
                colour = c;
                world.addParticle(getParticleForDigit((int) (f % 10)), x, y + yOffset, z, xOffset/4, 0.35, zOffset/4);
                addDamageIndicatorParticle(x, y, z, (int) (f / 10), xOffset, (yOffset + 0.25), zOffset, colour);
            }
        }
    }

    private static ParticleOptions getParticleForDigit(int i) {
        switch (i) {
            case 0:
                return IDFParticles.ZERO.get();
            case 1:
                return IDFParticles.ONE.get();
            case 2:
                return IDFParticles.TWO.get();
            case 3:
                return IDFParticles.THREE.get();
            case 4:
                return IDFParticles.FOUR.get();
            case 5:
                return IDFParticles.FIVE.get();
            case 6:
                return IDFParticles.SIX.get();
            case 7:
                return IDFParticles.SEVEN.get();
            case 8:
                return IDFParticles.EIGHT.get();
            default:
                return IDFParticles.NINE.get();
        }
    }
}

package net.cwjn.idf.event.hook;

import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.network.DisplayEvasionParticlesMessage;
import net.cwjn.idf.network.IDFPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber
public class EvasionEvent {

    @SubscribeEvent
    public static void Evasion(LivingAttackEvent event) {
        LivingEntity target = event.getEntityLiving();
        if (target instanceof Player && !target.level.isClientSide()) {
            if (target.getAttributeValue(IDFAttributes.EVASION.get())/100 >= Math.random()) {
                IDFPacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getX(), target.getY(), target.getZ(), 30d, target.getCommandSenderWorld().dimension())),
                        new DisplayEvasionParticlesMessage(target.getX(), target.getY(), target.getZ()));
                target.invulnerableTime = 20;
                event.setCanceled(true);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void displayEvasionParticles(double x, double y, double z) {
        Level world = Minecraft.getInstance().level;
        if (world != null) {
            System.out.println("started particle function");
            for (int i = 0; i < 360; i++) {
                if (i % 20 == 0) {
                    world.addParticle(ParticleTypes.SMOKE, x, y + 1.5, z, Math.cos(i) * 0.25, 0, Math.sin(i) * 0.25);
                    world.addParticle(ParticleTypes.SMOKE, x, y + 1.25, z, Math.cos(i) * 0.25, 0, Math.sin(i) * 0.25);
                    world.addParticle(ParticleTypes.SMOKE, x, y + 1, z, Math.cos(i) * 0.25, 0, Math.sin(i) * 0.25);
                    world.addParticle(ParticleTypes.SMOKE, x, y + 0.75, z, Math.cos(i) * 0.25, 0, Math.sin(i) * 0.25);
                    world.addParticle(ParticleTypes.SMOKE, x, y + 0.5, z, Math.cos(i) * 0.25, 0, Math.sin(i) * 0.25);
                }
            }
        }
    }

}

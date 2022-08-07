package net.cwjn.idf.event;

import net.cwjn.idf.attribute.IDFAttributes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EvasionEvent {

    @SubscribeEvent
    public static void evasion(LivingAttackEvent event) {
        LivingEntity target = event.getEntity();
        if (target.getLevel() instanceof ServerLevel level) {
            if (target.getAttributeValue(IDFAttributes.EVASION.get())/100 >= Math.random()) {
                for (int i = 0; i < 360; i++) {
                    if (i % 20 == 0) {
                        level.sendParticles(ParticleTypes.SMOKE, target.getX(), target.getY() + 1.5, target.getZ(), 3, Math.cos(i) * 0.25, 0, Math.sin(i) * 0.25, 0.25);
                        level.sendParticles(ParticleTypes.SMOKE, target.getX(), target.getY() + 1.25, target.getZ(), 3, Math.cos(i) * 0.25, 0, Math.sin(i) * 0.25, 0.25);
                        level.sendParticles(ParticleTypes.SMOKE, target.getX(), target.getY() + 1, target.getZ(), 3, Math.cos(i) * 0.25, 0, Math.sin(i) * 0.25, 0.25);
                        level.sendParticles(ParticleTypes.SMOKE, target.getX(), target.getY() + 0.75, target.getZ(), 3, Math.cos(i) * 0.25, 0, Math.sin(i) * 0.25, 0.25);
                        level.sendParticles(ParticleTypes.SMOKE, target.getX(), target.getY() + 0.5, target.getZ(), 3, Math.cos(i) * 0.25, 0, Math.sin(i) * 0.25, 0.25);
                    }
                }
                event.setCanceled(true);
            }
        }
    }

}

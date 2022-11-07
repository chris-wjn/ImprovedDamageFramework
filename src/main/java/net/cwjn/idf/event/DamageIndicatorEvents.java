package net.cwjn.idf.event;

import com.mojang.math.Vector3f;
import net.cwjn.idf.util.Color;
import net.cwjn.idf.api.event.PostMitigationDamageEvent;
import net.cwjn.idf.network.packets.DisplayDamageIndicatorsMessage;
import net.cwjn.idf.network.PacketHandler;
import net.cwjn.idf.particle.IDFParticles;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber
public class DamageIndicatorEvents {

    private static final Random random = new Random();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPostMitigationDamage(PostMitigationDamageEvent event) {
        LivingEntity target = event.getTarget();
        Level world = target.getLevel();
        UUID id = Util.NIL_UUID;
        if (target instanceof Player) id = target.getUUID();
        if (!world.isClientSide()) {
            double x = target.getX();
            double y = target.getEyeY();
            double z = target.getZ();
            List<Float> locs = new ArrayList<>();
            locs.add(random.nextFloat(-1.0f, -0.6666667f));
            locs.add(random.nextFloat(-0.6666667f, -0.333333f));
            locs.add(random.nextFloat(-0.333333f, 0));
            locs.add(random.nextFloat(0, 0.333333f));
            locs.add(random.nextFloat(0.333333f, 0.6666667f));
            locs.add(random.nextFloat(0.6666667f, 1.0f));
            if (event.getFire() > 0) {
                int loc = random.nextInt(locs.size());
                PacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getX(), target.getY(), target.getZ(), 30d, target.getCommandSenderWorld().dimension())),
                        new DisplayDamageIndicatorsMessage(x, y, z, event.getFire(), locs.get(loc), Color.FIRE_COLOUR.getColor(), id));
                locs.remove(loc);
            }
            if (event.getWater() > 0) {
                int loc = random.nextInt(locs.size());
                PacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getX(), target.getY(), target.getZ(), 30d, target.getCommandSenderWorld().dimension())),
                        new DisplayDamageIndicatorsMessage(x, y, z, event.getWater(), locs.get(loc), Color.WATER_COLOUR.getColor(), id));
                locs.remove(loc);
            }
            if (event.getLightning() > 0) {
                int loc = random.nextInt(locs.size());
                PacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getX(), target.getY(), target.getZ(), 30d, target.getCommandSenderWorld().dimension())),
                        new DisplayDamageIndicatorsMessage(x, y, z, event.getLightning(), locs.get(loc),  Color.LIGHTNING_COLOUR.getColor(), id));
                locs.remove(loc);
            }
            if (event.getMagic() > 0) {
                int loc = random.nextInt(locs.size());
                PacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getX(), target.getY(), target.getZ(), 30d, target.getCommandSenderWorld().dimension())),
                        new DisplayDamageIndicatorsMessage(x, y, z, event.getMagic(), locs.get(loc),  Color.MAGIC_COLOUR.getColor(), id));
                locs.remove(loc);
            }
            if (event.getDark() > 0) {
                int loc = random.nextInt(locs.size());
                PacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getX(), target.getY(), target.getZ(), 30d, target.getCommandSenderWorld().dimension())),
                        new DisplayDamageIndicatorsMessage(x, y, z, event.getDark(), locs.get(loc),  Color.DARK_COLOUR.getColor(), id));
                locs.remove(loc);
            }
            if (event.getPhysical() > 0) {
                int loc = random.nextInt(locs.size());
                PacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getX(), target.getY(), target.getZ(), 30d, target.getCommandSenderWorld().dimension())),
                        new DisplayDamageIndicatorsMessage(x, y, z, event.getPhysical(), locs.get(loc), Color.PHYSICAL_COLOUR.getColor(), id));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void addDamageIndicatorParticle(double x, double y, double z, float f, int col, double horizontalOffset, double yOffset, UUID id) {
        if (Minecraft.getInstance().player == null) return;
        if (id.equals(Minecraft.getInstance().player.getUUID())) return;
        Level world = Minecraft.getInstance().level;
        Vector3f lookVector = Minecraft.getInstance().gameRenderer.getMainCamera().getLookVector();
        float newZ = -1*lookVector.x();
        float newX = lookVector.z();
        if (world != null) {
            world.addParticle(IDFParticles.NUMBER_PARTICLE.get().setNumber(f).setColour(col), x, y, z, (newX*horizontalOffset)/10, yOffset/3, (newZ*horizontalOffset)/10);
        }
    }
}

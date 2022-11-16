package net.cwjn.idf.event;

import net.cwjn.idf.util.Color;
import net.cwjn.idf.api.event.PostMitigationDamageEvent;
import net.cwjn.idf.network.packets.DisplayDamageIndicatorPacket;
import net.cwjn.idf.network.PacketHandler;
import net.minecraft.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
            List<Float> locs = new ArrayList<>(List.of(
                    random.nextFloat(-1.0f, -0.6666667f),
                    random.nextFloat(-0.6666667f, -0.333333f),
                    random.nextFloat(-0.333333f, 0),
                    random.nextFloat(0, 0.333333f),
                    random.nextFloat(0.333333f, 0.6666667f),
                    random.nextFloat(0.6666667f, 1.0f)));
            if (event.getFire() > 0) {
                int loc = random.nextInt(locs.size());
                PacketHandler.serverToNearPoint(new DisplayDamageIndicatorPacket(x, y, z, event.getFire(), locs.get(loc), Color.FIRE_COLOUR.getColor(), id), x, y, z, 15, target.getCommandSenderWorld().dimension());
                locs.remove(loc);
            }
            if (event.getWater() > 0) {
                int loc = random.nextInt(locs.size());
                PacketHandler.serverToNearPoint(new DisplayDamageIndicatorPacket(x, y, z, event.getWater(), locs.get(loc), Color.WATER_COLOUR.getColor(), id), x, y, z, 15, target.getCommandSenderWorld().dimension());
                locs.remove(loc);
            }
            if (event.getLightning() > 0) {
                int loc = random.nextInt(locs.size());
                PacketHandler.serverToNearPoint(new DisplayDamageIndicatorPacket(x, y, z, event.getLightning(), locs.get(loc),  Color.LIGHTNING_COLOUR.getColor(), id), x, y, z, 15, target.getCommandSenderWorld().dimension());
                locs.remove(loc);
            }
            if (event.getMagic() > 0) {
                int loc = random.nextInt(locs.size());
                PacketHandler.serverToNearPoint(new DisplayDamageIndicatorPacket(x, y, z, event.getMagic(), locs.get(loc),  Color.MAGIC_COLOUR.getColor(), id), x, y, z, 15, target.getCommandSenderWorld().dimension());
                locs.remove(loc);
            }
            if (event.getDark() > 0) {
                int loc = random.nextInt(locs.size());
                PacketHandler.serverToNearPoint(new DisplayDamageIndicatorPacket(x, y, z, event.getDark(), locs.get(loc),  Color.DARK_COLOUR.getColor(), id), x, y, z, 15, target.getCommandSenderWorld().dimension());
                locs.remove(loc);
            }
            if (event.getPhysical() > 0) {
                int loc = random.nextInt(locs.size());
                PacketHandler.serverToNearPoint(new DisplayDamageIndicatorPacket(x, y, z, event.getPhysical(), locs.get(loc), Color.PHYSICAL_COLOUR.getColor(), id), x, y, z, 15, target.getCommandSenderWorld().dimension());
            }
        }
    }

}

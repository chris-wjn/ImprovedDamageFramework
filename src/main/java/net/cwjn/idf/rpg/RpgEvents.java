package net.cwjn.idf.rpg;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;

public class RpgEvents {

    public static void onLivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            RpgItem rpgItem = (RpgItem) event.getTo();
            RpgPlayer rpgPlayer = (RpgPlayer) player;
            if (!checkRequirements(rpgPlayer, rpgItem)) {
                //send message to player saying they cannot use the item
                player.drop(event.getTo(), true);
                player.getInventory().removeItem(event.getTo());
            }
        }
    }

    private static boolean checkRequirements(RpgPlayer player, RpgItem item) {
        return  player.getCons() >= item.getConsReq() &&
                player.getStr() >= item.getStrReq() &&
                player.getDex() >= item.getDexReq() &&
                player.getAgl() >= item.getAglReq() &&
                player.getInt() >= item.getIntReq() &&
                player.getWis() >= item.getWisReq() &&
                player.getFth() >= item.getFthReq();
    }

}

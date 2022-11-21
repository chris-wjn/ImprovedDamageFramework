package net.cwjn.idf.rpg;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.rpg.capability.RpgItem;
import net.cwjn.idf.rpg.capability.RpgItemProvider;
import net.cwjn.idf.rpg.player.RpgPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class RpgEvents {

    @SubscribeEvent
    public static void onLivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            RpgPlayer rpgPlayer = (RpgPlayer) player;
            if (!checkRequirements(rpgPlayer, rpgItem)) {
                //send message to player saying they cannot use the item
                player.drop(event.getTo(), true);
                player.getInventory().removeItem(event.getTo());
            }
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<ItemStack> event) {
        if (event.getObject().hasTag() && event.getObject().getTag().contains("idf.equipment")) {
            event.addCapability(new ResourceLocation(ImprovedDamageFramework.MOD_ID, "rpgitem"), new RpgItemProvider());
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

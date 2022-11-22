package net.cwjn.idf.rpg;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.rpg.capability.RpgItem;
import net.cwjn.idf.rpg.capability.RpgItemProvider;
import net.cwjn.idf.rpg.player.RpgPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class RpgEvents {

    @SubscribeEvent
    public static void onLivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            RpgPlayer rpgPlayer = (RpgPlayer) player;
            event.getTo().getCapability(RpgItemProvider.RPG_ITEM).ifPresent(h -> {
                if (!(rpgPlayer.getCons() >= h.getCONSTITUTION().getLeft() &&
                rpgPlayer.getStr() >= h.getSTRENGTH().getLeft() &&
                rpgPlayer.getDex() >= h.getDEXTERITY().getLeft() &&
                rpgPlayer.getAgl() >= h.getAGILITY().getLeft() &&
                rpgPlayer.getInt() >= h.getINTELLIGENCE().getLeft() &&
                rpgPlayer.getWis() >= h.getWISDOM().getLeft() &&
                rpgPlayer.getFth() >= h.getFAITH().getLeft())) {
                    player.drop(event.getTo(), true);
                    player.getInventory().removeItem(event.getTo());
                }
            });
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<ItemStack> event) {
        if (event.getObject().hasTag() && event.getObject().getTag().contains("idf.equipment")) {
            event.addCapability(new ResourceLocation(ImprovedDamageFramework.MOD_ID, "rpgitem"), new RpgItemProvider());
        }
    }

    @SubscribeEvent
    public static void onAttributeCalculationEvent(ItemAttributeModifierEvent event) {
        event.get
    }

}

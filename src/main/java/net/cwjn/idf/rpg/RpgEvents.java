package net.cwjn.idf.rpg;

import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.api.event.OnItemStackCreatedEvent;
import net.cwjn.idf.attribute.IDFAttributes;
import net.cwjn.idf.network.PacketHandler;
import net.cwjn.idf.rpg.capability.RpgItemProvider;
import net.cwjn.idf.rpg.network.UpdateItemOwnerPacket;
import net.cwjn.idf.rpg.player.RpgPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
            ItemStack item = event.getTo();
            item.getCapability(RpgItemProvider.RPG_ITEM).ifPresent(h -> {
                if (!(rpgPlayer.getCons() >= h.getCONSTITUTION().getLeft() &&
                rpgPlayer.getStr() >= h.getSTRENGTH().getLeft() &&
                rpgPlayer.getDex() >= h.getDEXTERITY().getLeft() &&
                rpgPlayer.getAgl() >= h.getAGILITY().getLeft() &&
                rpgPlayer.getInt() >= h.getINTELLIGENCE().getLeft() &&
                rpgPlayer.getWis() >= h.getWISDOM().getLeft() &&
                rpgPlayer.getFth() >= h.getFAITH().getLeft())) {
                    player.drop(event.getTo(), true);
                    player.getInventory().removeItem(event.getTo());
                } else {
                    if (h.STRENGTH.getRight() > 0) {
                        item.addAttributeModifier(Attributes.ATTACK_DAMAGE,
                                new AttributeModifier("strength_scaling", ));
                    }
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

    public static void setItemOwner(OnItemStackCreatedEvent event) {
        PacketHandler.playerToServer(new UpdateItemOwnerPacket(Minecraft.getInstance().player.getUUID(), itemStack));
    }

}

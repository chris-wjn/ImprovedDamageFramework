package com.cwjn.idf.ItemModification;

import com.cwjn.idf.ImprovedDamageFramework;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ImprovedDamageFramework.MOD_ID)
public class TagAttachment {

    @SubscribeEvent
    public static void ItemHandler(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack item = event.getObject();

    }


}

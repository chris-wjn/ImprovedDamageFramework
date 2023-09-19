package net.cwjn.idf.api.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

/*
* Only fired on client, used to replace default getAttributeModifiers hook that gets replaced by me
*/
public class ReplaceAttributeModifierEvent extends Event {

    public ItemStack item;

    public ReplaceAttributeModifierEvent(ItemStack item) {
        this.item = item;
    }

}

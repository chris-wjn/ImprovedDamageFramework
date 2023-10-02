package net.cwjn.idf.api.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

/*
* Only fired on client, used to replace default getAttributeModifiers hook that gets replaced by me
*/
public class ReplaceItemAttributeModifierEvent extends Event {

    public ItemStack item;

    public ReplaceItemAttributeModifierEvent(ItemStack item) {
        this.item = item;
    }

}

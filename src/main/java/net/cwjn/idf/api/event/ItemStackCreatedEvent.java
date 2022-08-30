package net.cwjn.idf.api.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class ItemStackCreatedEvent extends Event {

    private ItemStack itemStack;

    public ItemStackCreatedEvent(ItemStack i) {
        itemStack = i;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

}

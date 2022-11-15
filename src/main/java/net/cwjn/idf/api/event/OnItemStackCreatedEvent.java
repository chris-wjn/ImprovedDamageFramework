package net.cwjn.idf.api.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class OnItemStackCreatedEvent extends Event {

    private final ItemStack itemStack;

    public OnItemStackCreatedEvent(ItemStack item) {
        this.itemStack = item;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}

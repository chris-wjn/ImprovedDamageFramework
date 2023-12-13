package net.cwjn.idf.api.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

public class IDFTooltipStartEvent extends Event {

    private final ItemStack item;
    private final Player player;
    private final List<Component> list;
    private final boolean isWeapon;

    public ItemStack getItem() {
        return item;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Component> getList() {
        return list;
    }

    public boolean isWeapon() {
        return isWeapon;
    }

    public IDFTooltipStartEvent(ItemStack item, Player player, List<Component> list, boolean isWeapon) {
        this.item = item;
        this.player = player;
        this.list = list;
        this.isWeapon = isWeapon;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}

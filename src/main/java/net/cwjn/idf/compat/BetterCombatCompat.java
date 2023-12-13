package net.cwjn.idf.compat;

import net.bettercombat.logic.WeaponRegistry;
import net.cwjn.idf.api.event.IDFTooltipStartEvent;
import net.cwjn.idf.util.Color;
import net.cwjn.idf.util.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static net.cwjn.idf.util.Util.*;
import static net.minecraft.network.chat.Component.translatable;

public class BetterCombatCompat {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(BetterCombatCompat.class);
    }

    @SubscribeEvent
    public static void onItemTooltipEvent(IDFTooltipStartEvent event) {
        var attributes = WeaponRegistry.getAttributes(event.getItem());
        if (attributes != null) {
            double range = attributes.attackRange();
            MutableComponent durabilityComponent = Component.empty();
            event.getList().add(durabilityComponent
                    .append(Component.literal(" ").append(translatable("idf.right_arrow.symbol").append(spacer(2))))
                    .append(Util.writeIcon("attack_range", true))
                    .append(withColor(translation("attribute.name.generic.attack_range"), Color.GREY)
                    .append(" ")
                            .append(Util.withColor(writeTooltipString(String.valueOf(range)), Color.HOLY_COLOUR))
                    ));
        }
    }

}
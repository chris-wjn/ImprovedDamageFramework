package net.cwjn.idf.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cwjn.idf.ImprovedDamageFramework;
import net.cwjn.idf.config.json.JSONHandler;
import net.cwjn.idf.network.PacketHandler;
import net.cwjn.idf.network.packets.SyncClientConfigPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static net.cwjn.idf.data.CommonData.*;

public class UpdateItemsFromConfigCommand {

    public UpdateItemsFromConfigCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("idfUpdate").requires(player -> player.hasPermission(2)).executes(stack -> update(stack.getSource())));
    }

    private int update(CommandSourceStack source) throws CommandSyntaxException {
        JSONHandler.updateMaps();
        JSONHandler.updateItems();
        PacketHandler.serverToAll(new SyncClientConfigPacket(LOGICAL_WEAPON_MAP_FLAT, LOGICAL_WEAPON_MAP_MULT, LOGICAL_ARMOUR_MAP_FLAT, LOGICAL_ARMOUR_MAP_MULT,
                LOGICAL_PRESET_MAP, COMPAT_ITEMS, COMPAT_MODS));
        ImprovedDamageFramework.LOGGER.info("Updated config and synced relevant clients.");
        return 1;
    }

}

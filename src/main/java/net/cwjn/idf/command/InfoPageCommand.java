package net.cwjn.idf.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cwjn.idf.network.PacketHandler;
import net.cwjn.idf.network.packets.OpenInfoScreenPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class InfoPageCommand {

    public InfoPageCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("idfInfo").executes(stack -> infoPage(stack.getSource())));
    }

    private int infoPage(CommandSourceStack source) throws CommandSyntaxException {
        PacketHandler.serverToPlayer(new OpenInfoScreenPacket(), source.getPlayer());
        return 1;
    }

}

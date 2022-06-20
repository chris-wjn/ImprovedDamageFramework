package net.cwjn.idf.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cwjn.idf.event.ServerEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

public class ChangeDebugStatusCommand {

    public ChangeDebugStatusCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("idfDebug").requires(player -> player.hasPermission(2)).executes(stack -> changeDebugMode(stack.getSource())));
    }

    private int changeDebugMode(CommandSourceStack source) throws CommandSyntaxException {
        ServerEvents.debugMode = !ServerEvents.debugMode;
        source.sendSuccess(new TextComponent("debug mode changed"), true);
        return 1;
    }

}

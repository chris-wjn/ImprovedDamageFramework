package net.cwjn.idf.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cwjn.idf.event.ServerEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

public class ChangeDebugStatusCommand {

    public ChangeDebugStatusCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("idfDebug").requires(player -> player.hasPermission(2)).executes(stack -> changeDebugMode(stack.getSource())));
    }

    private int changeDebugMode(CommandSourceStack source) throws CommandSyntaxException {
        if (!ServerEvents.debugMode) {
            ServerEvents.debugMode = true;
            source.sendSuccess(MutableComponent.create(new TranslatableContents("idf.debug_message_on")), true);
        } else {
            ServerEvents.debugMode = false;
            source.sendSuccess(MutableComponent.create(new TranslatableContents("idf.debug_message_off")), true);
        }
        return 1;
    }

}

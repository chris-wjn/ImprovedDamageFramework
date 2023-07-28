package net.cwjn.idf.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cwjn.idf.event.LogicalEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

public class UpdateItemsFromConfigCommand {

    public UpdateItemsFromConfigCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("idfUpdate").requires(player -> player.hasPermission(2)).executes(stack -> update(stack.getSource())));
    }

    private int update(CommandSourceStack source) throws CommandSyntaxException {
        if (!LogicalEvents.debugMode) {
            LogicalEvents.debugMode = true;
            source.sendSuccess(MutableComponent.create(new TranslatableContents("idf.debug_message_on")), true);
        } else {
            LogicalEvents.debugMode = false;
            source.sendSuccess(MutableComponent.create(new TranslatableContents("idf.debug_message_off")), true);
        }
        return 1;
    }

}

package net.cwjn.idf.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cwjn.idf.config.json.JSONHandler;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

public class UpdateJsonFilesCommand {

    public UpdateJsonFilesCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("updateDamageFramework").requires(player -> player.hasPermission(2)).executes(stack -> updateJsonFiles(stack.getSource())));
    }

    private int updateJsonFiles(CommandSourceStack stack) throws CommandSyntaxException {
        if (stack.getServer().isDedicatedServer()) {
            JSONHandler.updateServerFiles();
            stack.sendSuccess(new TranslatableComponent("idf.update.command.success"), true);
            return 1;
        } else {
            stack.sendFailure(new TranslatableComponent("idf.update.command.failure"));
            return -1;
        }
    }

}

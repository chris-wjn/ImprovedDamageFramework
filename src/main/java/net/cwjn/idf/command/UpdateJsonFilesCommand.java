package net.cwjn.idf.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cwjn.idf.util.Util;
import net.cwjn.idf.config.json.JSONHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class UpdateJsonFilesCommand {

    public UpdateJsonFilesCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("updateDamageFramework").requires(player -> player.hasPermission(2)).executes(stack -> updateJsonFiles(stack.getSource())));
    }

    private int updateJsonFiles(CommandSourceStack stack) throws CommandSyntaxException {
        if (stack.getServer().isDedicatedServer()) {
            JSONHandler.updateServerFiles();
            stack.sendSuccess(Util.translationComponent("idf.update.command.success"), true);
            return 1;
        } else {
            stack.sendFailure(Util.translationComponent("idf.update.command.failure"));
            return -1;
        }
    }

}

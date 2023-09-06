package net.cwjn.idf.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cwjn.idf.data.CommonData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class UnlockBestiaryCommand {

    public UnlockBestiaryCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("idfUnlockBestiary").requires(player -> player.hasPermission(2)).executes(stack -> unlockBestiary(stack.getSource())));
    }

    private int unlockBestiary(CommandSourceStack source) throws CommandSyntaxException {
        UUID id = source.getPlayer().getUUID();
        for (ResourceLocation rl : CommonData.LOGICAL_ENTITY_MAP.keySet()) {
            CommonData.BESTIARY_MAP.put(id, rl);
        }
        return 1;
    }

}

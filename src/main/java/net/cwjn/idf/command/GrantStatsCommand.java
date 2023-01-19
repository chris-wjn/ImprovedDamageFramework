package net.cwjn.idf.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cwjn.idf.event.LogicalEvents;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class GrantStatsCommand {

    /*public GrantStatsCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx) {
        dispatcher.register(literal("addattribute").requires(player -> player.hasPermission(2))
                .then(argument("item", ItemArgument.item(ctx)))
                .then(argument("attribute", ResourceKeyArgument.key(Registry.ATTRIBUTE_REGISTRY)))
                .then(argument("operation", IntegerArgumentType.integer(0, 2)))
                .then(argument("amount", DoubleArgumentType.doubleArg())).executes((stack) -> {
                    return addAttribute(ItemArgument.getItem(stack, "item").getItem(),
                            ResourceKeyArgument.getAttribute(stack, "attribute"),
                            AttributeModifier.Operation.fromValue(IntegerArgumentType.getInteger(stack, "operation")),
                            DoubleArgumentType.getDouble(stack, "amount"));
                }));
    }*/

    /*private static int addAttribute(Item item, Attribute a, AttributeModifier.Operation op, double amt) {

    }*/

}

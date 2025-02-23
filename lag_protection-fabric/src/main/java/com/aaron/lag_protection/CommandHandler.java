package com.aaron.lag_protection;

import com.aaron.lag_protection.manager.PingManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class CommandHandler {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, PingManager pingManager) {
        dispatcher.register(CommandManager.literal("getping")
            .then(CommandManager.argument("player", StringArgumentType.string())
                .executes(ctx -> {
                    String playerName = StringArgumentType.getString(ctx, "player");
                    ServerPlayerEntity target = ctx.getSource().getServer().getPlayerManager().getPlayer(playerName);

                    if (target != null) {
                        int ping = pingManager.getPing(target);
                        ctx.getSource().sendFeedback(() -> Text.literal(target.getName().getString() + " has a ping of " + ping), false);
                    } else {
                        ctx.getSource().sendFeedback(() -> Text.literal("Player not found!"), false);
                    }
                    return Command.SINGLE_SUCCESS;
                })));
    }
}
package com.aaron.lag_protection;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aaron.lag_protection.manager.PingManager;

public class CommandHandler implements CommandExecutor {

    @SuppressWarnings("unused")
    private final Plugin plugin;
    private final PingManager pingManager;

    public CommandHandler(Plugin plugin) {

        this.plugin = plugin;
        this.pingManager = plugin.GetPingManager();

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("getping")) {
            if (args.length == 1) {

                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    int ping = pingManager.GetPing(target);
                    sender.sendMessage(target.getName() + " has a ping of " + ping);
                } else {
                    sender.sendMessage("Player not found!");
                }
            } else {
                sender.sendMessage("Usage: /getping <Player>");
            }
            return true;
        }
        return false;
    }
}
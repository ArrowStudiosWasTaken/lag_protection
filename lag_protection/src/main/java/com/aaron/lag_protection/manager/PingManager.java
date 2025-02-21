package com.aaron.lag_protection.manager;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PingManager implements Listener {

    private final HashMap<UUID, Long> highPingPlayers = new HashMap<>(); // List of all players that have shit ping
    private final JavaPlugin plugin; // Our main file so it we can be called
    private final int minPing = 500; // Minimum ping

    // Simple call to our main file
    public PingManager (JavaPlugin plugin) {

        this.plugin = plugin;

    }

    // Checks the ping of every player every 20 ticks, if they have higher ping than our minPing then they're added to the list
    public void StartPingTest() {

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    int ping = GetPing(player);
                    if (ping > minPing)
                        highPingPlayers.put(player.getUniqueId(), System.currentTimeMillis()); // Add them to highPingPlayers (they're ping is >500)
                    else
                        highPingPlayers.remove(player.getUniqueId()); // Remove them from highPingPlayers (they're ping is <=500)
                }
            }
        }.runTaskTimer(plugin, 0, 20); // BukkitRunnable allows us to run a script every 20 ticks (According to StackOverflow)
    }

    // If a player is taking damage and their on the list then we'll cancel out the damage
    // This is to make it fair because our player can't defend themselves on dogshit ping, muchless even move :/
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (highPingPlayers.containsKey(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    // Getting the actual ping from the player
    public int GetPing(Player player) {
        try {
            // First we're gonna try to get the ping from the getPing method!
            return (int)player.getClass().getMethod("getPing").invoke(player); 
        } catch (NoSuchMethodException e) {
            try {
                // If it doesn't exist for some ungodly reason, then we'll get the ping of the entity from getHandle
                Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
                return (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
                // If we seriously can't get the ping at all from them, we'll just set it to 0 (They don't get protection)
                return 0; // Default ping
            }
        } catch (IllegalAccessException | SecurityException | InvocationTargetException e) {
            // Same thing here, no ping, no protection
            return 0; // Default ping
        }
    }
}
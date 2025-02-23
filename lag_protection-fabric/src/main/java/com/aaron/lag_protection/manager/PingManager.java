package com.aaron.lag_protection.manager;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

import java.util.HashMap;
import java.util.UUID;

public class PingManager {
    private final HashMap<UUID, Long> highPingPlayers = new HashMap<>();
    private static final int MIN_PING = 500;

    public PingManager() {
        // Cancel damage for high-ping players
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                if (highPingPlayers.containsKey(serverPlayer.getUuid())) {
                    return ActionResult.FAIL; // Cancel damage
                }
            }
            return ActionResult.PASS;
        });
    }

    // Check player pings and update list every tick
    public void checkPing(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            int ping = getPing(player);
            if (ping > MIN_PING) {
                highPingPlayers.put(player.getUuid(), System.currentTimeMillis());
            } else {
                highPingPlayers.remove(player.getUuid());
            }
        }
    }

    public int getPing(ServerPlayerEntity player) {
        try {
            return (int) ServerPlayerEntity.class.getDeclaredField("latency").get(player);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return -1; // Return -1 if an error occurs
        }
    }
}

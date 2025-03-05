package com.aaron;

import java.util.HashMap;
import java.util.UUID;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

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
        return player.networkHandler.getLatency();
    }
}
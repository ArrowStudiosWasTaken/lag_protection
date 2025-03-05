package com.aaron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class Lag_protector implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("lag_protection");

    private PingManager pingManager;

    @Override
    public void onInitialize() {
        LOGGER.info("LagProtection 1.0 enabled!");
        pingManager = new PingManager();

        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            CommandHandler.register(dispatcher, pingManager);
        });

        // Start checking ping every tick
        ServerTickEvents.END_SERVER_TICK.register(server -> pingManager.checkPing(server));
    }

    public PingManager getPingManager() {
        return pingManager;
    }
}
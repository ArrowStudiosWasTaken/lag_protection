package com.aaron.lag_protection;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.aaron.lag_protection.manager.PingManager;


public class Plugin extends JavaPlugin {
  private static final Logger LOGGER=Logger.getLogger("lag_protection");

  private PingManager pingManager;

  @Override
  public void onEnable() {
    LOGGER.info("lag_protection 1.0 enabled");
    LOGGER.info("Protecting your players from low client ping!");

    pingManager = new PingManager(this);
    this.getCommand("getPing").setExecutor(new CommandHandler(this));

    pingManager.StartPingTest();
  }

  @Override
  public void onDisable() {
    LOGGER.info("lag_protection 1.0 disabled");
  }

  public PingManager GetPingManager() {

    return pingManager;

  }
}

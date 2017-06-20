package com.branwidth.EldinLand;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Some text here for starting the plugin
        getLogger().info("Enabled EldinLand");
        //Creating the config file for the plugin
        createConfig();
        MySQL.connect();
        // Specifying commands for the plugin
        getCommand("Land").setExecutor(new Land());
    }

    private void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("Config.yml not found, creating!");
                saveDefaultConfig();
            } else {
                getLogger().info("Config.yml for XpExchange found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    // Creating a getter for the Main class
    static Main getPlugin() {
        return Main.getPlugin(Main.class);
    }

    @Override
    public void onDisable(){
        // Some text here for stopping the plugin
        getLogger().info("Disabled EldinLand");
        MySQL.disconnect();
    }


}

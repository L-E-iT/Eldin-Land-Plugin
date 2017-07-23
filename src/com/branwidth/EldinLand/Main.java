package com.branwidth.EldinLand;

import com.branwidth.EldinLand.Listeners.*;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Some text here for starting the plugin
        getLogger().info("Enabled EldinLand");
        //Creating the config file for the plugin
        createFiles();
        MySQL.connect();
        // Specifying commands for the plugin
        getCommand("Land").setExecutor(new Land());
        //Register Listeners
        getServer().getPluginManager().registerEvents(new PlotCreateListener(), this);
        getServer().getPluginManager().registerEvents(new PlotRemoveListener(), this);
        getServer().getPluginManager().registerEvents(new PlotChangeSizeListener(), this);
        getServer().getPluginManager().registerEvents(new PlotChangeOwnerListener(), this);
        getServer().getPluginManager().registerEvents(new PlotRentListener(), this);
        // §A Green §6 Gold
    }


    private void createFiles() {

        File configf = new File(getDataFolder(), "config.yml");

        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
    }

    // Creating a getter for the Main class
    public static Main getPlugin() {
        return Main.getPlugin(Main.class);
    }

    @Override
    public void onDisable(){
        // Some text here for stopping the plugin
        getLogger().info("Disabled EldinLand");
        MySQL.disconnect();
    }


}

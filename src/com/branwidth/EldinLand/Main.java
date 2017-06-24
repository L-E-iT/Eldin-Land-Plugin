package com.branwidth.EldinLand;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {

    private FileConfiguration plot;
    private File plotf;

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
        getServer().getPluginManager().registerEvents(new LandCreateListener(), this);
        getServer().getPluginManager().registerEvents(new LandRemoveListener(), this);
        getServer().getPluginManager().registerEvents(new LandOwnerChangeListener(), this);
    }


    FileConfiguration getPlotConfig() {
        return this.plot;
    }

    void savePlotData() {
        try {
            getPlotConfig().save(plotf);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createFiles() {

        File configf = new File(getDataFolder(), "config.yml");
        plotf = new File(getDataFolder(), "plot.yml");

        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        if (!plotf.exists()) {
            plotf.getParentFile().mkdirs();
            saveResource("plot.yml", false);
        }

        FileConfiguration config = new YamlConfiguration();
        plot = new YamlConfiguration();
        try {
            config.load(configf);
            plot.load(plotf);
        } catch (IOException | InvalidConfigurationException e) {
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

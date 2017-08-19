package com.branwidth.EldinLand;

import com.branwidth.EldinLand.Listeners.*;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.sql.SQLException;

public class Main extends JavaPlugin {
    public static Economy econ = null;

    @Override
    public void onEnable() {
        // Some text here for starting the plugin
        getLogger().info("Enabled EldinLand");
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        //Creating the config file for the plugin
        createFiles();
        MySQL.connect();
        keepAlive();
        // Specifying commands for the plugin
        getCommand("Land").setExecutor(new Land());
        getCommand("Rank").setExecutor(new Rank());
        //Register Listeners
        getServer().getPluginManager().registerEvents(new PlotCreateListener(), this);
        getServer().getPluginManager().registerEvents(new PlotRemoveListener(), this);
        getServer().getPluginManager().registerEvents(new PlotChangeSizeListener(), this);
        getServer().getPluginManager().registerEvents(new PlotChangeOwnerListener(), this);
        getServer().getPluginManager().registerEvents(new PlotRentListener(), this);
        getServer().getPluginManager().registerEvents(new PlotPurchaseListener(), this);
        // §A Green §6 Gold
    }

    private void keepAlive() {
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                try {
                    MySQL.getConnection().prepareStatement("SELECT 1").executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 0l, 1200L);
    }


    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
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

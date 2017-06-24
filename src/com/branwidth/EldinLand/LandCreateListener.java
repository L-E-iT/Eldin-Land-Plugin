package com.branwidth.EldinLand;

import com.bekvon.bukkit.residence.event.ResidenceCreationEvent;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;



public class LandCreateListener implements Listener{

    /*
    Set initial variables
    sec - set configuration section for the listener
    Distance - Set distance in Config file
    logLand - Boolean to confirm if land should be logged or not.
     */
    private ConfigurationSection sec = Main.getPlugin().getPlotConfig().getConfigurationSection("Residences");
    private int Distance = Main.getPlugin().getConfig().getInt("DistanceConfig.Distance");
    private Boolean logLand = true;

    @EventHandler
    public void onCreationEvent(ResidenceCreationEvent event) {
        /*
        playerLoc - Log player location
        insidePlot - Checks if player is inside the plot being created
        p - get player
        resName - new residence name
         */
        Location playerLoc = event.getPlayer().getLocation();
        Boolean insidePlot = event.getPhysicalArea().containsLoc(playerLoc);
        Player p = event.getPlayer();
        String resName = event.getResidenceName();
        p.sendMessage(ChatColor.GOLD + "Checking the area around you for plots...");
        /*
        Checks each plot inside of plot.yml for distance
        Ignores plots with the same uuid as the current player
         */
        for (String key : sec.getKeys(false)) {
            World worldCoord = Bukkit.getServer().getWorld(Main.getPlugin().getPlotConfig().getString("Residences." + key + ".world"));
            Double xCoord = Main.getPlugin().getPlotConfig().getDouble("Residences." + key + ".x");
            Double yCoord = Main.getPlugin().getPlotConfig().getDouble("Residences." + key + ".y");
            Double zCoord = Main.getPlugin().getPlotConfig().getDouble("Residences." + key + ".z");
            Location otherLoc = new Location(worldCoord,xCoord,yCoord,zCoord);
            String otherUUID = Main.getPlugin().getPlotConfig().getString("Residences." + key + ".uuid");
            String otherPlayer = Main.getPlugin().getPlotConfig().getString("Residences." + key + ".player");
            Double playerDistance = playerLoc.distance(otherLoc);
            //logLand used to track if land should be logged to plot.yml
            logLand = true;
            //Confirms player is in the plot
            if(insidePlot) {
                // Confirms the distance and player checks. Cancels event if appropriate
                if (playerLoc.distance(otherLoc) < Distance && !otherUUID.equals(p.getUniqueId().toString())) {
                    event.setCancelled(true);
                    event.isCancelled();
                    Double otherX, otherY, otherZ;
                    otherX = otherLoc.getX();
                    otherY = otherLoc.getY();
                    otherZ = otherLoc.getZ();
                    // Warn player for each plot near the new plot
                    p.sendMessage(ChatColor.DARK_RED + "===== Warning =====");
                    p.sendMessage("§6"+ otherPlayer + "§c owns a plot at " + otherX.intValue() + ", " + otherY.intValue() + ", " + otherZ.intValue() + " Which is "
                            + playerDistance.intValue() + " Tiles away!");
                    p.sendMessage(ChatColor.RED + "Please move at least " + Distance + " Tiles from their plot!");
                    logLand = false;
                }
            } else {
                //Message for players to stand in the plot
                p.sendMessage(ChatColor.RED + "Please stand inside your plot");
                event.setCancelled(true);
                event.isCancelled();
                logLand = false;
                break;
            }
        }
        if (logLand) {
            // Logs the players new plot into the plot.yml file
            p.sendMessage(ChatColor.BLUE + "Recording plot location...");
            ConfigurationSection newPlot = sec.createSection(resName);
            sec.createSection(resName + ".player");
            sec.createSection(resName + ".uuid");
            sec.createSection(resName + ".world");
            sec.createSection(resName + ".x");
            sec.createSection(resName + ".y");
            sec.createSection(resName + ".z");

            // Initialize some variables for the plot.yml recording
            String pName, pUUID, pWorld;
            Double pX, pY, pZ;
            pName = p.getName();
            pUUID = p.getUniqueId().toString();
            pWorld = p.getWorld().getName();
            pX = playerLoc.getX();
            pY = playerLoc.getY();
            pZ = playerLoc.getZ();

            newPlot.set(".player", pName);
            newPlot.set(".uuid", pUUID);
            newPlot.set(".world", pWorld);
            newPlot.set(".x", pX);
            newPlot.set(".y", pY);
            newPlot.set(".z", pZ);
            Main.getPlugin().savePlotData();
            p.sendMessage(ChatColor.BLUE + "Done!");
        }


    }

}
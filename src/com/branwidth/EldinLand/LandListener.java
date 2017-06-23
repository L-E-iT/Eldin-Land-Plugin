package com.branwidth.EldinLand;

import com.bekvon.bukkit.residence.event.ResidenceCreationEvent;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class LandListener implements Listener{

    private ConfigurationSection sec = Main.getPlugin().getPlotConfig().getConfigurationSection("Residences");
    private int Distance = Main.getPlugin().getConfig().getInt("DistanceConfig.Distance");
    private Boolean logLand = true;

    @EventHandler
    public void onCreationEvent(ResidenceCreationEvent event) {
        Location playerLoc = event.getPlayer().getLocation();
        Boolean insidePlot = event.getPhysicalArea().containsLoc(playerLoc);
        Player p = event.getPlayer();
        String resName = event.getResidenceName();
        p.sendMessage(ChatColor.GOLD + "Checking the area around you for plots...");
        for (String key : sec.getKeys(false)) {
            World worldCoord = Bukkit.getServer().getWorld(Main.getPlugin().getPlotConfig().getString("Residences." + key + ".world"));
            Double xCoord = Main.getPlugin().getPlotConfig().getDouble("Residences." + key + ".x");
            Double yCoord = Main.getPlugin().getPlotConfig().getDouble("Residences." + key + ".y");
            Double zCoord = Main.getPlugin().getPlotConfig().getDouble("Residences." + key + ".z");
            Location otherLoc = new Location(worldCoord,xCoord,yCoord,zCoord);
            String otherUUID = Main.getPlugin().getPlotConfig().getString("Residences." + key + ".uuid");
            String otherPlayer = Main.getPlugin().getPlotConfig().getString("Residences." + key + ".player");
            Double playerDistance = playerLoc.distance(otherLoc);
            if (playerLoc.distance(otherLoc) < Distance && !otherUUID.equals(p.getUniqueId().toString()) && insidePlot) {
                event.setCancelled(true);
                event.isCancelled();
                Double otherX, otherY, otherZ;
                otherX = otherLoc.getX();
                otherY = otherLoc.getY();
                otherZ = otherLoc.getZ();
                p.sendMessage(ChatColor.RED + "This is too close to an already existing plot!");
                p.sendMessage(ChatColor.RED + otherPlayer + " owns a plot at " + otherX.intValue() + ", " + otherY.intValue() + ", " + otherZ.intValue() + " Which is "
                        + playerDistance.intValue() + " Tiles away!");
                p.sendMessage(ChatColor.RED + "Please move at least " + Distance + " Tiles from their plot!");
                logLand = false;
            }
        }
        if (logLand) {
            p.sendMessage(ChatColor.BLUE + "Recording plot location...");
            ConfigurationSection newPlot = sec.createSection(resName);
            sec.createSection(resName + ".player");
            sec.createSection(resName + ".uuid");
            sec.createSection(resName + ".world");
            sec.createSection(resName + ".x");
            sec.createSection(resName + ".y");
            sec.createSection(resName + ".z");

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
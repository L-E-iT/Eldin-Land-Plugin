package com.branwidth.EldinLand.Listeners;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.event.ResidenceCreationEvent;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.branwidth.EldinLand.Database;
import com.branwidth.EldinLand.Main;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


public class PlotCreateListener implements Listener {

    @EventHandler
    public void onCreationEvent(ResidenceCreationEvent event) throws SQLException {

        // Ensure that the Database database connection is established
        Database.connect();
        if (!Database.isConnected()) {
            Database.connect();
        }



        //variable initialization
        Player p = event.getPlayer();
        String pUUID = p.getUniqueId().toString().replace("-","");
        Integer distance = Main.getPlugin().getConfig().getInt("DistanceConfig.Distance");
        String preMessage = Main.getPlugin().getConfig().getString("MessagesConfig.PreMessage");
        CuboidArea newArea = event.getResidence().getMainArea();
        // get new area as area with resize for config values
        CuboidArea area = resizeAreaForTownCheck(newArea, distance);
        Double xHighLoc;
        Double zHighLoc;
        Double xLowLoc;
        Double zLowLoc;
        String playerWorldReplaced = null;
        double playerBalance = Main.econ.getBalance(p);

        String playerWorld = Database.getPlayerWorld(p.getWorld().getName());

        if (playerBalance < event.getResidence().getXZSize()*30) {
            event.setCancelled(true);
            p.sendMessage(ChatColor.RED + "You do not have enough Trade Bars!");
        }

        if (playerWorld == null) {
            event.setCancelled(true);
            p.sendMessage("You are not in a valid world!");
        }

        for (Map.Entry<String, ClaimedResidence> entry : Residence.getInstance().getResidenceManager().getResidences().entrySet()) {
            ClaimedResidence res = entry.getValue();
            if (res.checkCollision(area)) {
                if (!res.isOwner(p)) {
                    CuboidArea a = res.getMainArea();
                    xHighLoc = a.getHighLoc().getX();
                    zHighLoc = a.getHighLoc().getZ();
                    xLowLoc = a.getLowLoc().getX();
                    zLowLoc = a.getLowLoc().getZ();
                    Double xLoc = (xHighLoc + xLowLoc) / 2;
                    Double zLoc = (zHighLoc + zLowLoc) / 2;
                    p.sendMessage(ChatColor.DARK_RED + "===== Distance Warning =====");
                    p.sendMessage("§6" + entry.getValue().getRPlayer().getPlayerName() + " §Cowns the plot §6" + entry.getKey() + "§c at X:§6" + xLoc + "§c Y:§6" + zLoc + ".");
                    event.setCancelled(true);
                }
            }
        }

        if(!event.isCancelled()){
            // Database code
            if (!Database.isConnected()) {
                Database.connect();
            }
            if(Database.isConnected()) {
//                PreparedStatement PSselect = Database.getConnection().prepareStatement("select * from players WHERE uuid='" + pUUID + "'");
                ResultSet RSselect = Database.getPlayerLand(pUUID);
                if (RSselect == null) {
                    // get area of plot
                    Long plotArea = newArea.getSize();
                    // get Wild tile values
                    Long prevWildLand = 0L;
                    // get new value
                    Long newWildLand = prevWildLand + plotArea;
                    // set Database statement
                    p.sendMessage(pUUID);
                    p.sendMessage(String.valueOf(newWildLand));
                    p.sendMessage(playerWorld);
                    Database.changePlayerWildLand(pUUID, newWildLand, playerWorld);
                    ResultSet RSWildLand = Database.getPlayerLand(pUUID);
                    // Send player new wild land values
                    if (playerWorld != null) {
                        playerWorldReplaced = playerWorld.replace("_count", "");
                    }
                    while(RSWildLand.next()) {
                        p.sendMessage(preMessage + "§A Added §6" + plotArea + "§A Tiles to " +  StringUtils.capitalize(playerWorldReplaced) + " land");
                        p.sendMessage(String.valueOf(preMessage + "§A New " + StringUtils.capitalize(playerWorldReplaced) + " Land Count: §6" + RSWildLand.getInt(playerWorld)));
                    }
                } else {
                    while (RSselect.next()) {
                        // get area of plot
                        Long plotArea = newArea.getSize();
                        // get Wild tile values
                        Long prevWildLand = RSselect.getLong(playerWorld);
                        // get new value
                        Long newWildLand = prevWildLand + plotArea;
                        // set Database statement
                        Database.changePlayerWildLand(pUUID, newWildLand, playerWorld);
                        ResultSet RSWildLand = Database.getPlayerLand(pUUID);
                        // Send player new wild land values
                        if (playerWorld != null) {
                            playerWorldReplaced = playerWorld.replace("_count", "");
                        }
                        while (RSWildLand.next()) {
                            p.sendMessage(preMessage + "§A Added §6" + plotArea + "§A Tiles to " + StringUtils.capitalize(playerWorldReplaced) + " land");
                            p.sendMessage(String.valueOf(preMessage + "§A New " + StringUtils.capitalize(playerWorldReplaced) + " Land Count: §6" + RSWildLand.getInt(playerWorld)));
                        }
                    }
                }
            } else {
                p.sendMessage("There was an issue connecting to the database!");
            }
        }
    }

    private CuboidArea resizeAreaForTownCheck(CuboidArea newArea, Integer r) {
        CuboidArea area = new CuboidArea();
        area.setHighLocation(newArea.getHighLoc().clone().add(r, r, r));
        area.setLowLocation(newArea.getLowLoc().clone().add(-r, -r, -r));
        return area;
    }
}

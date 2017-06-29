package com.branwidth.EldinLand;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.event.ResidenceCreationEvent;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


public class PlotCreateListener implements Listener {

    @EventHandler
    public void onCreationEvent(ResidenceCreationEvent event) throws SQLException {

        //variable initialization
        Player p = event.getPlayer();
        String pUUID = p.getUniqueId().toString().replace("-","");
        Integer distance = Main.getPlugin().getConfig().getInt("DistanceConfig.Distance");
        CuboidArea newArea = event.getResidence().getMainArea();
        // get new area as area with resize for config values
        CuboidArea area = resizeAreaForTownCheck(newArea, distance);
        Double xHighLoc;
        Double zHighLoc;
        Double xLowLoc;
        Double zLowLoc;
        String playerWorldName = p.getWorld().getName();
        String playerWorld = null;
        String playerWorldReplaced = null;

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
        switch (playerWorldName) {
            case "world":
                playerWorld = "wild_count";
                break;
            case "world_nether":
                playerWorld = "nether_count";
                break;
            case "world_the_end":
                playerWorld = "end_cound";
                break;
            default:
                event.setCancelled(true);
                p.sendMessage(ChatColor.RED + "You are not in a valid world!");
        }
        if(!event.isCancelled()){
            // MySQL code
            if(MySQL.isConnected()) {
                PreparedStatement PSselect = MySQL.getConnection().prepareStatement("select * from players WHERE uuid='" + pUUID + "'");
                ResultSet RSselect = PSselect.executeQuery();
                while (RSselect.next()) {
                    // set length of sides
                    Double xLength = newArea.getHighLoc().getX() - newArea.getLowLoc().getX();
                    Double zLength = newArea.getHighLoc().getZ() - newArea.getLowLoc().getZ();
                    Double xzTileArea = (xLength + 1) * (zLength + 1);
                    // get Wild tile values
                    Double prevWildLand = RSselect.getDouble(playerWorld);
                    Double newWildLand = prevWildLand + xzTileArea;
                    // set MySQL statement
                    PreparedStatement PSinsert = MySQL.getConnection().prepareStatement("UPDATE players SET " + playerWorld + " = " + newWildLand +
                            " WHERE uuid = '" + pUUID + "'");
                    // Execute statment
                    PSinsert.executeUpdate();
                    ResultSet RSWildLand = MySQL.getPlayerLand(pUUID);
                    // Send player new wild land values
                    if (playerWorld != null) {
                        playerWorldReplaced = playerWorld.replace("_count", "");
                    }
                    while(RSWildLand.next()) {
                        p.sendMessage(String.valueOf(ChatColor.GREEN + "New " + StringUtils.capitalize(playerWorldReplaced) + " Land Count: §6" + RSWildLand.getDouble(playerWorld)));
                    }
                }
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

package com.branwidth.EldinLand.Listeners;

import com.bekvon.bukkit.residence.event.ResidenceOwnerChangeEvent;
import com.bekvon.bukkit.residence.event.ResidenceSizeChangeEvent;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.branwidth.EldinLand.Main;
import com.branwidth.EldinLand.MySQL;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;


public class PlotChangeSizeListener implements Listener {

    @EventHandler
    public void onChangeSizeEvent(ResidenceSizeChangeEvent event) throws SQLException {

        // Get Variables
        Player p = event.getPlayer();
        String pUUID = p.getUniqueId().toString().replace("-","");
        String playerWorld = MySQL.getPlayerWorld(p.getWorld().getName());
        String playerWorldReplaced = playerWorld.replace("_count","");
        String preMessage = Main.getPlugin().getConfig().getString("MessagesConfig.PreMessage");
        Long oldArea = event.getOldArea().getSize();
        Long newArea = event.getNewArea().getSize() - oldArea;

        // Ensure that the MySQL database connection is established
        if (!MySQL.isConnected()) {
            MySQL.connect();
        }

        if (!Objects.equals(p.getName(), event.getResidence().getOwner())) {
            event.setCancelled(true);
            p.sendMessage(preMessage + ChatColor.RED + "You do not own this plot!");
        } else {

            // Get old land, set new land values
            ResultSet oldLandRS = MySQL.getPlayerLand(pUUID);
            while (oldLandRS.next()) {
                Long oldLand = oldLandRS.getLong(playerWorld);
                Long newLand = oldLand + newArea;
                MySQL.changePlayerWildLand(pUUID, newLand, playerWorld);

                // Send message to player about new land values
                p.sendMessage(preMessage + "§A Your old§6 " + playerWorldReplaced + " §Aland count was: §6" + oldLand + " §ATiles.");
                p.sendMessage(preMessage + "§A Your new§6 " + playerWorldReplaced + " §Aland count is: §6" + newLand + " §ATiles.");
            }
            //§A Green §6 Gold
        }
    }
}

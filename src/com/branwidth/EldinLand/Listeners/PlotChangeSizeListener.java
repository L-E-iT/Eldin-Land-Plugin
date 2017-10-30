package com.branwidth.EldinLand.Listeners;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.event.ResidenceSizeChangeEvent;
import com.branwidth.EldinLand.Database;
import com.branwidth.EldinLand.Main;
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

        // Ensure that the Database database connection is established
        Database.connect();
        if (!Database.isConnected()) {
            Database.connect();
        }

        // Get Variables
        Player p = event.getPlayer();
        String pUUID = p.getUniqueId().toString().replace("-","");
        String playerWorld = Database.getPlayerWorld(p.getWorld().getName());
        String playerWorldReplaced = playerWorld.replace("_count","");
        String preMessage = Main.getPlugin().getConfig().getString("MessagesConfig.PreMessage");
        Long oldArea = event.getOldArea().getSize();
        Long newArea = event.getNewArea().getSize() - oldArea;
        double playerBalance = Main.econ.getBalance(p);
        double playerOldBalance = Main.econ.getBalance(p) + (event.getNewArea().getSize() - event.getOldArea().getSize())*30;
        p.sendMessage(String.valueOf(playerBalance));
        p.sendMessage(String.valueOf(playerOldBalance));

        if (playerOldBalance < (event.getNewArea().getSize() - event.getOldArea().getSize())*30) {
            p.sendMessage(ChatColor.RED + "You do not have enough Trade Bars!");
            p.sendMessage(ChatColor.RED + "Crediting the amount of TB taken back to your account!");
            Main.econ.depositPlayer(p,(event.getNewArea().getSize() - event.getOldArea().getSize())*30);
            event.getOldArea().save();
            event.setCancelled(true);
        }

        if (!Objects.equals(p.getName(), event.getResidence().getOwner()) || event.isCancelled()) {

        } else {
            // Get old land, set new land values
            ResultSet oldLandRS = Database.getPlayerLand(pUUID);
            while (oldLandRS.next()) {
                Long oldLand = oldLandRS.getLong(playerWorld);
                Long newLand = oldLand + newArea;
                Database.setPlayerWildLand(pUUID, newLand, playerWorld);

                // Send message to player about new land values
                p.sendMessage(preMessage + "§A Your old§6 " + playerWorldReplaced + " §Aland count was: §6" + oldLand + " §ATiles.");
                p.sendMessage(preMessage + "§A Your new§6 " + playerWorldReplaced + " §Aland count is: §6" + newLand + " §ATiles.");
            }
            if (Database.isPlotACity(event.getResidenceName())) {
                Database.changeCitySize(pUUID,newArea,playerWorld, event.getResidenceName());
                Long totalSize = Residence.getInstance().getResidenceManager().getByName(event.getResidenceName()).getXZSize();
                p.sendMessage(preMessage + "§A New size of §6" + event.getResidenceName() + "§A is:§6 " + totalSize);

            }
            //§A Green §6 Gold
        }
    }
}

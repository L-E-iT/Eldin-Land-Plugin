package com.branwidth.EldinLand.Listeners;

import com.bekvon.bukkit.residence.event.ResidenceRentEvent;
import com.branwidth.EldinLand.Database;
import com.branwidth.EldinLand.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.bekvon.bukkit.residence.event.ResidenceRentEvent.RentEventType.*;

public class PlotRentListener implements Listener {

    @EventHandler
    public void onRentEvent(ResidenceRentEvent event) throws SQLException {

        // Ensure that the Database database connection is established
        Database.connect();
        if (!Database.isConnected()) {
            Database.connect();
        }


        // Get basic variables
        Long playerCityLand = 0L;
        Player p = event.getPlayer();
        String pName = p.getPlayer().getName();
        String plotOwner = event.getResidence().getOwner();
        String pUUID = p.getUniqueId().toString().replace("-","");
        String preMessage = Main.getPlugin().getConfig().getString("MessagesConfig.PreMessage");
        // Get cause of rent event (RENT, UNRENT, RENTABLE, UNRENTABLE, RENT_EXPIRE)
        ResidenceRentEvent.RentEventType rentType = event.getCause();
        Long plotSize = event.getResidence().getXZSize();
        Player townOwner = event.getResidence().getParent().getRPlayer().getPlayer();
        String townName = event.getResidence().getParent().getResidenceName();
        double playerBalance = Main.econ.getBalance(p);

        ResultSet rsPlayerCityLand = Database.getPlayerLand(pUUID);
        if (rsPlayerCityLand == null) {
        } else {
            while (rsPlayerCityLand.next()) {
                playerCityLand = rsPlayerCityLand.getLong("city_count");
            }
        }

//        §A Green §6 Gold
            if (rentType.equals(RENT)) {
                if (playerBalance < event.getResidence().getSellPrice()) {
                    p.sendMessage(ChatColor.RED + "You do not have enough Trade Bars!");
                } else {
                    // Change player city land count
                    Database.setPlayerCityLand(pUUID, plotSize, pName);
                    p.sendMessage(preMessage + "§A Added §6" + plotSize + "§A tiles to §6City§A land");
                    Long totalCityLand = playerCityLand + plotSize;
                    p.sendMessage(preMessage + "§A New City tile count: §6" + totalCityLand);
                    // Change town plot details
                    Database.changeCityPlot(townName, plotSize, pUUID, true);
                }
            } else if (rentType.equals(UNRENT) || rentType.equals(RENT_EXPIRE) || rentType.equals(UNRENTABLE)) {
                // Change player city land count
                if (!plotOwner.equals(p.getPlayer().getName())) {
                    Database.setPlayerCityLand(pUUID, -plotSize, pName);
                    Database.changeCityPlot(townName, plotSize, pUUID, false);
                    if (p.isOnline()) {
                        p.sendMessage(preMessage + "§A Removed §6" + plotSize + "§A tiles from §6 City §A land");
                        Long totalCityLand = playerCityLand - plotSize;
                        p.sendMessage(preMessage + "§A New City tile count: §6" + totalCityLand);
                    }
                } else {
                }
                // Change town plot details
            }

    }
}

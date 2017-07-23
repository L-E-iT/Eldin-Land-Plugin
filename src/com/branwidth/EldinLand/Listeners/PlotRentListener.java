package com.branwidth.EldinLand.Listeners;

import com.bekvon.bukkit.residence.event.ResidenceRentEvent;
import com.branwidth.EldinLand.Main;
import com.branwidth.EldinLand.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.bekvon.bukkit.residence.event.ResidenceRentEvent.RentEventType.RENT;
import static com.bekvon.bukkit.residence.event.ResidenceRentEvent.RentEventType.RENT_EXPIRE;
import static com.bekvon.bukkit.residence.event.ResidenceRentEvent.RentEventType.UNRENT;

public class PlotRentListener implements Listener {

    @EventHandler
    public void onRentEvent(ResidenceRentEvent event) throws SQLException {

        // Get basic variables
        Player p = event.getPlayer();
        String plotOwner = event.getResidence().getOwner();
        String pUUID = p.getUniqueId().toString().replace("-","");
        String preMessage = Main.getPlugin().getConfig().getString("MessagesConfig.PreMessage");
        // Get cause of rent event (RENT, UNRENT, RENTABLE, UNRENTABLE, RENT_EXPIRE)
        ResidenceRentEvent.RentEventType rentType = event.getCause();
        Long plotSize = event.getResidence().getXZSize();
        Player townOwner = event.getResidence().getTown().getMainResidence().getRPlayer().getPlayer();
        String townName = event.getResidence().getTown().getTownName();
        p.sendMessage(townOwner.getName());
        p.sendMessage(townName);

        if (!MySQL.isConnected()) {
            MySQL.connect();
        }

        ResultSet rsPlayerCityLand = MySQL.getPlayerLand(pUUID);
        rsPlayerCityLand.next();
        Long playerCityLand = rsPlayerCityLand.getLong("city_count");

//        §A Green §6 Gold
        if (rentType.equals(RENT)) {
            // Change player city land count
            MySQL.changePlayerCityLand(pUUID, plotSize);
            p.sendMessage(preMessage + "§A Added §6" + plotSize + "§A tiles to §6 City §A land");
            Long totalCityLand = playerCityLand + plotSize;
            p.sendMessage(preMessage + "§A New City tile count: §6" + totalCityLand);
            // Change town plot details
            MySQL.changeCityPlot(townName, plotSize, pUUID, true);
        } else if (rentType.equals(UNRENT) || rentType.equals(RENT_EXPIRE)) {
            // Change player city land count
            MySQL.changePlayerCityLand(pUUID, -plotSize);
            MySQL.changeCityPlot(townName, plotSize, pUUID, false);
            try {
                p.sendMessage(preMessage + "§A Removed §6" + plotSize + "§A tiles from §6 City §A land");
                Long totalCityLand = playerCityLand - plotSize;
                p.sendMessage(preMessage + "§A New City tile count: §6" + totalCityLand);
            } catch(Exception e) {
                Bukkit.getLogger().info(p.getName() + "was not online when rent expired.");
            }
            // Change town plot details
        }


    }
}

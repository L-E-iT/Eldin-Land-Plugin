package com.branwidth.EldinLand.Listeners;


import com.bekvon.bukkit.residence.event.ResidenceDeleteEvent;
import com.branwidth.EldinLand.Database;
import com.branwidth.EldinLand.Main;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlotRemoveListener implements Listener {

    @EventHandler
    public void onRemoveEvent(ResidenceDeleteEvent event) throws SQLException {

        // Ensure that the Database database connection is established
        Database.connect();
        if (!Database.isConnected()) {
            Database.connect();
        }


        Player p = event.getPlayer();
        String pUUID = p.getUniqueId().toString().replace("-","");
        long plotArea = event.getResidence().getMainArea().getSize();
        String playerWorld = Database.getPlayerWorld(p.getWorld().getName());
        String playerWorldReplaced = playerWorld.replace("_count","");
        String preMessage = Main.getPlugin().getConfig().getString("MessagesConfig.PreMessage");

        ResultSet RSland = Database.getPlayerLand(pUUID);
        if (Database.isConnected()) {
            while (RSland.next()) {
                if (p.getName().equals(event.getResidence().getOwner())) {
                    Long prevWildLand = RSland.getLong(playerWorld);
                    Long newWildLand = prevWildLand - plotArea;
                    Database.changePlayerWildLand(pUUID, newWildLand, playerWorld);
                    p.sendMessage(preMessage + "§A Removed §6" + plotArea + "§A Tiles from " + StringUtils.capitalize(playerWorldReplaced) + " land");
                    p.sendMessage(String.valueOf(preMessage + "§A New " + StringUtils.capitalize(playerWorldReplaced) + " Land Count: §6" + newWildLand));
                }
            }


        }
    }

}

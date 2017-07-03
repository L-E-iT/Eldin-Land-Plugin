package com.branwidth.EldinLand.Listeners;


import com.bekvon.bukkit.residence.event.ResidenceDeleteEvent;
import com.branwidth.EldinLand.Main;
import com.branwidth.EldinLand.MySQL;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlotRemoveListener implements Listener {

    @EventHandler
    public void onRemoveEvent(ResidenceDeleteEvent event) throws SQLException {

        Player p = event.getPlayer();
        String pUUID = p.getUniqueId().toString().replace("-","");
        long plotArea = event.getResidence().getMainArea().getSize()/event.getResidence().getMainArea().getYSize();
        String playerWorld = MySQL.getPlayerWorld(p.getWorld().getName());
        String playerWorldReplaced = playerWorld.replace("_count","");
        String preMessage = Main.getPlugin().getConfig().getString("MessagesConfig.PreMessage");
        ResultSet RSland = MySQL.getPlayerLand(pUUID);



        if (!MySQL.isConnected()) {
            MySQL.connect();
        }
        if (MySQL.isConnected()) {
            while (RSland.next()) {
                Long prevWildLand = RSland.getLong(playerWorld);
                Long newWildLand = prevWildLand - plotArea;
                MySQL.addPlayerWildLand(pUUID, newWildLand, playerWorld);
                p.sendMessage(preMessage + "§A Removed §6" + plotArea + "§A Tiles from " +  StringUtils.capitalize(playerWorldReplaced) + " land");
                p.sendMessage(String.valueOf(preMessage + "§A New " + StringUtils.capitalize(playerWorldReplaced) + " Land Count: §6" + newWildLand));
            }


        }


    }

}

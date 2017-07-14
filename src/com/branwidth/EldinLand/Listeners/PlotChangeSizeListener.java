package com.branwidth.EldinLand.Listeners;

import com.bekvon.bukkit.residence.event.ResidenceOwnerChangeEvent;
import com.bekvon.bukkit.residence.event.ResidenceSizeChangeEvent;
import com.branwidth.EldinLand.Main;
import com.branwidth.EldinLand.MySQL;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.SQLException;


public class PlotChangeSizeListener implements Listener {

    @EventHandler
    public void onChangeSizeEvent(ResidenceSizeChangeEvent event) throws SQLException {

        String playerWorld = MySQL.getPlayerWorld(p.getWorld().getName());
        String playerWorldReplaced = playerWorld.replace("_count","");
        String preMessage = Main.getPlugin().getConfig().getString("MessagesConfig.PreMessage");

    }
}

package com.branwidth.EldinLand.Listeners;

import com.bekvon.bukkit.residence.event.ResidenceCreationEvent;
import com.bekvon.bukkit.residence.event.ResidenceOwnerChangeEvent;
import com.branwidth.EldinLand.Main;
import com.branwidth.EldinLand.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.SQLException;


public class PlotChangeOwnerListener implements Listener {

    @EventHandler
    public void onChangeOwnerEvent(ResidenceOwnerChangeEvent event) throws SQLException {



//        String pNewName = event.getNewOwner();
//        Player pNew = Bukkit.getPlayer(pNewName);
//
//        String pOldName = event.getResidence().getOwner();
//        Player pOld = Bukkit.getPlayer(pOldName);
//
//        String newPlayerWorld = MySQL.getPlayerWorld(pOld.getWorld().getName());
//        String newPlayerWorldReplaced = newPlayerWorld.replace("_count","");
//        String preMessage = Main.getPlugin().getConfig().getString("MessagesConfig.PreMessage");


//        // Ensure that the MySQL database connection is established
//        if (!MySQL.isConnected()) {
//            MySQL.connect();
//        }



    }
}

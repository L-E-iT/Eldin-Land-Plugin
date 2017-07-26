package com.branwidth.EldinLand.Listeners;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.event.ResidenceOwnerChangeEvent;
import com.branwidth.EldinLand.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.SQLException;


public class PlotChangeOwnerListener implements Listener {

    @EventHandler
    public void onChangeOwnerEvent(ResidenceOwnerChangeEvent event) throws SQLException {

        String pNewName = event.getNewOwner();
        Player pNew = (Player) Residence.getInstance().getOfflinePlayer(pNewName);

        String pOldName = event.getResidence().getOwner();
        Player pOld = Bukkit.getPlayer(pOldName);

        pNew.sendMessage("Test New Owner");
        pOld.sendMessage("Test Old Owner");

        if (!MySQL.isConnected()) {
            MySQL.connect();
        }

        if (event.getResidence().isSubzone()) {
            // Bought or sold city land
        } else {
            // Transferred wild land
        }




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

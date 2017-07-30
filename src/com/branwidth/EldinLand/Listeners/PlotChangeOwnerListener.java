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
        String pNewUUID = pNew.getUniqueId().toString().replace("-","");

        String pOldName = event.getResidence().getOwner();
        Player pOld = Bukkit.getPlayer(pOldName);
        String pOldUUID = pNew.getUniqueId().toString().replace("-","");

        Long tileCount = event.getResidence().getXZSize();

        pNew.sendMessage("Test New Owner");
        pOld.sendMessage("Test Old Owner");

        if (!MySQL.isConnected()) {
            MySQL.connect();
        }

        if (event.getResidence().isSubzone()) {
            // Bought or sold city land
            String townName = event.getResidence().getParent().getResidenceName();
            String townOwner = event.getResidence().getParent().getRPlayer().getPlayerName();
            if (pNew.equals(townOwner)) {
                // land sold back to town
                // take land from old owner
                MySQL.changePlayerCityLand(pOldUUID,-tileCount,pOldName);
                // do not give any to the new owner
                // add bought land amount to available land to sell
                // MySQL.changeAvailableCityLand()
            } else {
                // land sold to another player
                // remove land from current owner
                MySQL.changePlayerCityLand(pOldUUID,-tileCount,pOldName);
                // give land to new owner
                MySQL.changePlayerCityLand(pNewUUID,tileCount,pNewName);
                // remove land from city listing from old player
                MySQL.changeCityPlot(townName,tileCount,pOldUUID,false);
                // add land to city listing for new player
            }
        } else {
            // Transferred wild land
            // remove wild land from old player
            // add wild land to new player
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

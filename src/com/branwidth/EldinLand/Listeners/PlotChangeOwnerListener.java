package com.branwidth.EldinLand.Listeners;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.event.ResidenceOwnerChangeEvent;
import com.branwidth.EldinLand.Database;
import com.branwidth.EldinLand.Main;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.ResultSet;
import java.sql.SQLException;


public class PlotChangeOwnerListener implements Listener {

    @EventHandler
    public void onChangeOwnerEvent(ResidenceOwnerChangeEvent event) throws SQLException {

        // Ensure that the Database database connection is established
        Database.connect();
        if (!Database.isConnected()) {
            Database.connect();
        }


        String pNewName = event.getNewOwner();
        Player pNew = (Player) Residence.getInstance().getOfflinePlayer(pNewName);
        String pNewUUID = pNew.getUniqueId().toString().replace("-", "");
        String worldName = event.getResidence().getWorld();

        String pOldName = event.getResidence().getOwner();
        Player pOld = Bukkit.getPlayer(pOldName);
        String pOldUUID = pNew.getUniqueId().toString().replace("-", "");

        Long tileCount = event.getResidence().getXZSize();
        double playerBalance = Main.econ.getBalance(pNew);


        String playerWorld = Database.getPlayerWorld(event.getResidence().getWorld());
        String playerWorldReplaced = playerWorld.replace("_count", "");

        String preMessage = Main.getPlugin().getConfig().getString("MessagesConfig.PreMessage");

            if (event.getResidence().isSubzone()) {
                // Bought or sold city land
                String townName = event.getResidence().getParent().getResidenceName();
                String townOwner = event.getResidence().getParent().getRPlayer().getPlayerName();
                if (playerBalance < event.getResidence().getSellPrice()) {
                    pNew.sendMessage(ChatColor.RED + "You do not have enough Trade Bars!");
                } else {
                    if (pNewName.equals(townOwner)) {
                        // land sold back to town
                        // take land from old owner
                        Database.changePlayerCityLand(pOldUUID, -tileCount, pOldName);
                        // do not give any to the new owner
                    } else {
                        // land sold to another player
                        // remove land from current owner
                        Database.changePlayerCityLand(pOldUUID, -tileCount, pOldName);
                        // give land to new owner
                        Database.changePlayerCityLand(pNewUUID, tileCount, pNewName);
                        // remove land from city listing from old player
                        Database.changeCityPlot(townName, tileCount, pOldUUID, false);
                        // add land to city listing for new player
                        Database.changeCityPlot(townName, tileCount, pNewUUID, true);
                    }
                }
            } else {
                // Transferred wild land
                // get new player total wild land count
                String worldNameFrm = Database.getPlayerWorld(worldName);
                ResultSet rsNewPlayerLand = Database.getPlayerLand(pNewUUID);
                rsNewPlayerLand.next();
                Long newPlayerOldLandCount = rsNewPlayerLand.getLong(worldNameFrm);

                // get old player total wild land count
                Database.getPlayerLand(pOldUUID);
                ResultSet rsOldPlayerLand = Database.getPlayerLand(pOldUUID);
                rsOldPlayerLand.next();
                Long oldPlayerOldLandCount = rsNewPlayerLand.getLong(worldNameFrm);

                // set new land counts for each old and new player
                long newPlayerNewLandCount = newPlayerOldLandCount + tileCount;
                long oldPlayerNewLandCount = oldPlayerOldLandCount - tileCount;

                // remove wild land from old player
                Database.changePlayerWildLand(pOldUUID, oldPlayerNewLandCount, worldNameFrm);
                pOld.sendMessage(preMessage + "§A Removed §6" + tileCount + "§A tiles from " + StringUtils.capitalize(playerWorldReplaced) + " land.");
                pOld.sendMessage(preMessage + "§A New " + StringUtils.capitalize(playerWorldReplaced) + " Land Count: §6" + oldPlayerNewLandCount);
                // add wild land to new player
                Database.changePlayerWildLand(pNewUUID, newPlayerNewLandCount, worldNameFrm);
                pNew.sendMessage(preMessage + "§A Added §6" + tileCount + "§A tiles to " + StringUtils.capitalize(playerWorldReplaced) + " land.");
                pNew.sendMessage(preMessage + "§A New " + StringUtils.capitalize(playerWorldReplaced) + " Land Count: §6" + newPlayerNewLandCount);
            }
        }
    }

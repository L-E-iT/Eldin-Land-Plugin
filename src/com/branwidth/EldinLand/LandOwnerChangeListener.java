package com.branwidth.EldinLand;


import com.bekvon.bukkit.residence.event.ResidenceOwnerChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LandOwnerChangeListener implements Listener{

    private ConfigurationSection sec = Main.getPlugin().getPlotConfig().getConfigurationSection("Residences");

    @EventHandler
    public void onChangeOwnerEvent(ResidenceOwnerChangeEvent event){
        String newOwner = event.getNewOwner();
        Player newPlayer = Bukkit.getPlayer(newOwner);
        String newUUID = newPlayer.getUniqueId().toString();

        String plotName = event.getResidence().getName();

        String oldOwner = event.getResidence().getOwner();
        Player oldPlayer = Bukkit.getPlayer(oldOwner);
        String oldUUID = oldPlayer.getUniqueId().toString();

        sec.set(plotName + ".uuid", newUUID);
        sec.set(plotName + ".player", newOwner);
        Main.getPlugin().savePlotData();
    }
}

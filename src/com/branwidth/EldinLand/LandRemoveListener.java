package com.branwidth.EldinLand;


import com.bekvon.bukkit.residence.event.ResidenceDeleteEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class LandRemoveListener implements Listener {

    private ConfigurationSection sec = Main.getPlugin().getPlotConfig().getConfigurationSection("Residences");

    @EventHandler
    public void onRemoveEvent(ResidenceDeleteEvent event) {
        // Get some values
        String plotName = event.getResidence().getName();
        Player p = event.getPlayer();
        String userUUID = event.getPlayer().getUniqueId().toString();
        // If the UUID stored is the same as the users, allow removal
        if (sec.getString(plotName + ".uuid").equals(userUUID)) {
            sec.set(plotName, null);
            Main.getPlugin().savePlotData();
        }
    }
}

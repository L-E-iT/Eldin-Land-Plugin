package com.branwidth.EldinLand;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.event.ResidenceCreationEvent;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;


public class DistanceListener implements Listener {

    @EventHandler
    public void onCreationEvent(ResidenceCreationEvent event) {

        Player p = event.getPlayer();
        Integer distance = Main.getPlugin().getConfig().getInt("DistanceConfig.Distance");
        CuboidArea newArea = event.getResidence().getMainArea();
        // get new area as area with resize for config values
        CuboidArea area = resizeAreaForTownCheck(newArea, distance);

        for (Map.Entry<String, ClaimedResidence> entry : Residence.getInstance().getResidenceManager().getResidences().entrySet()) {
            ClaimedResidence res = entry.getValue();
            if (res.checkCollision(area)) {
                if (!res.isOwner(p)) {
                    CuboidArea a = res.getMainArea();
                    Double xHighLoc = a.getHighLoc().getX();
                    Double yHighLoc = a.getHighLoc().getY();
                    Double xLowLoc = a.getLowLoc().getX();
                    Double yLowLoc = a.getLowLoc().getY();
                    Double xLoc = (xHighLoc + xLowLoc) / 2;
                    Double yLoc = (yHighLoc + yLowLoc) / 2;
                    p.sendMessage(ChatColor.DARK_RED + "===== Distance Warning =====");
                    p.sendMessage("§6" + entry.getValue().getRPlayer().getPlayerName() + " §Cowns the plot §6" + entry.getKey() + "§c at X:§6" + xLoc + "§c Y:§6" + yLoc + ".");
                    event.setCancelled(true);
                }
            }
        }
        if(event.isCancelled()){
            // MySQL code

        }
    }

    private CuboidArea resizeAreaForTownCheck(CuboidArea newArea, Integer r) {
        CuboidArea area = new CuboidArea();
        area.setHighLocation(newArea.getHighLoc().clone().add(r, r, r));
        area.setLowLocation(newArea.getLowLoc().clone().add(-r, -r, -r));
        return area;
    }
}

package com.branwidth.EldinLand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class Rank implements CommandExecutor {
    String preMessage = Main.getPlugin().getConfig().getString("MessagesConfig.PreMessage");


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        String pUUID = p.getUniqueId().toString().replace("-","");
        String ranksAvailable;
        PermissionUser pPex = PermissionsEx.getUser(p);
        List<String> pGroups = pPex.getParentIdentifiers();

        pGroups.remove("Admin");
        pGroups.remove("Mod");
        pGroups.remove("Builder");

        if (!MySQL.isConnected()) {
            MySQL.connect();
        }

        // §A Green §6 Gold
        if (args.length == 0){
            p.sendMessage(preMessage + "§A Please specify the land type from the following list:");
            p.sendMessage(preMessage + "§AWild §F| §6City §F| §CNether §F| §5End");
        } else if (args.length == 1) {
            try {
            String landType = args[0];
            ResultSet rsPlayerLand= MySQL.getPlayerLand(pUUID);
            rsPlayerLand.next();
            int wildLand = rsPlayerLand.getInt("wild_count");
            int cityLand = rsPlayerLand.getInt("city_count");
            int netherLand = rsPlayerLand.getInt("nether_count");
            int endLand = rsPlayerLand.getInt("end_count");

            for (String n : pGroups ) {
                pPex.removeGroup(n);
            }

            switch (landType) {
                case "Wild":
                    getPlayerWildRank(wildLand, pPex, p);
                    break;
                case "City":
                    getPlayerCityRank(cityLand, pPex, p);
                    break;
                case "Nether":
                    getPlayerNetherRank(netherLand, pPex, p);
                    break;
                case "End":
                    getPlayerEndRank(endLand, pPex, p);
                    break;
            }
            } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return true;
    }

    private void getPlayerWildRank(int playerLand, PermissionUser pPex, Player p) {
        if (playerLand >= 100 && playerLand < 400){
            pPex.addGroup("Gentry");
            p.sendMessage(preMessage + "§A Rank updated to: §6Gentry");
        } else if (playerLand >= 400 && playerLand < 1600) {
            pPex.addGroup("Yeoman");
            p.sendMessage(preMessage + "§A Rank updated to: §6Yeoman");
        } else if (playerLand >= 1600 && playerLand < 3600) {
            pPex.addGroup("Nobleman");
            p.sendMessage(preMessage + "§A Rank updated to: §6Nobleman");
        } else if (playerLand >= 3600 && playerLand < 10000) {
            pPex.addGroup("Baron");
            p.sendMessage(preMessage + "§A Rank updated to: §6GBaron");
        } else if (playerLand >= 10000 && playerLand < 30625) {
            pPex.addGroup("Duke");
            p.sendMessage(preMessage + "§A Rank updated to: §6Duke");
        } else if (playerLand >= 30625 && playerLand < 62500) {
            pPex.addGroup("Prince");
            p.sendMessage(preMessage + "§A Rank updated to: §6Prince");
        } else if (playerLand >= 62500 && playerLand < 125000) {
            pPex.addGroup("King");
            p.sendMessage(preMessage + "§A Rank updated to: §6King");
        } else if (playerLand >= 125000) {
            pPex.addGroup("Emperor");
            p.sendMessage(preMessage + "§A Rank updated to: §6Emperor");
        } else {
            p.sendMessage(preMessage + "§A You do not have enough wild land!");
        }
    }

    private void getPlayerCityRank(int playerLand, PermissionUser pPex, Player p) {

    }

    private void getPlayerNetherRank(int playerLand, PermissionUser pPex, Player p) {

    }

    private void getPlayerEndRank(int playerLand, PermissionUser pPex, Player p) {

    }
}
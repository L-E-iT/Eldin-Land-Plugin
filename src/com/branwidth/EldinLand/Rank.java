package com.branwidth.EldinLand;

import com.google.common.collect.Ordering;
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

        // Ensure that the Database database connection is established
        Database.connect();
        if (!Database.isConnected()) {
            Database.connect();
        }


        Player p = (Player) sender;
        String pUUID = p.getUniqueId().toString().replace("-", "");
        PermissionUser pPex = PermissionsEx.getUser(p);
        List<String> unsortedGroups = pPex.getParentIdentifiers();

        List<String> pGroups = Ordering.natural().sortedCopy(unsortedGroups);

        pGroups.remove("Admin");
        pGroups.remove("Mod");
        pGroups.remove("Builder");


        // §A Green §6 Gold
        if (args.length == 0) {
            p.sendMessage(preMessage + "§A Please specify the land type from the following list:");
            p.sendMessage(preMessage + " §AWild §F| §6City §F| §CNether §F| §5End");
        } else if (args.length == 1) {
            try {
                String landType = args[0].toLowerCase();
                ResultSet rsPlayerLand = Database.getPlayerLand(pUUID);
                if (rsPlayerLand == null) {
                    p.sendMessage("You don't own any land!");
                    return true;
                }
                rsPlayerLand.next();
                int wildLand = rsPlayerLand.getInt("wild_count");
                int cityLand = rsPlayerLand.getInt("city_count");
                int netherLand = rsPlayerLand.getInt("nether_count");
                int endLand = rsPlayerLand.getInt("end_count");

                if (wildLand < 100 && cityLand < 100 && netherLand < 100 && endLand < 100) {
                    p.sendMessage("You don't own any land!");
                    return true;
                }
                switch (landType) {
                    case "wild":
                        if (wildLand >= 100) {
                            getPlayerWildRank(wildLand, pPex, p, pGroups);
                        } else {
                            p.sendMessage(preMessage + "§A You don't have enough §6Wild §Aland!");
                            break;
                        }
                        break;
                    case "city":
                        if (cityLand >= 100) {
                            getPlayerCityRank(wildLand, pPex, p, pGroups);
                        } else {
                            p.sendMessage(preMessage + "§A You don't have enough §6City §Aland!");
                            break;
                        }
                        break;
                    case "nether":
                        if (netherLand >= 100) {
                            getPlayerNetherRank(wildLand, pPex, p, pGroups);
                        } else {
                            p.sendMessage(preMessage + "§A You don't have enough §6Nether §Aland!");
                            break;
                        }
                        break;
                    case "end":
                        if (endLand >= 100) {
                            getPlayerEndRank(wildLand, pPex, p, pGroups);
                        } else {
                            p.sendMessage(preMessage + "§A You don't have enough §6End §Aland!");
                            break;
                        }
                        break;
                    case "town":
                        if (wildLand >= 100 && Database.getIsTownOwner(pUUID)) {
                            getPlayerTownRank(wildLand, pPex, p, pGroups);
                        } else {
                            p.sendMessage(preMessage + "§A You don't own a §6Town§A!");
                            break;
                        }
                        break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void getPlayerWildRank(int playerLand, PermissionUser pPex, Player p, List<String> pGroups) {
        if (playerLand >= 100 && playerLand < 400) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Gentry");
            p.sendMessage(preMessage + "§A Rank updated to: §6Gentry");
        } else if (playerLand >= 400 && playerLand < 1600) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Yeoman");
            p.sendMessage(preMessage + "§A Rank updated to: §6Yeoman");
        } else if (playerLand >= 1600 && playerLand < 3600) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Nobleman");
            p.sendMessage(preMessage + "§A Rank updated to: §6Nobleman");
        } else if (playerLand >= 3600 && playerLand < 10000) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Baronet");
            p.sendMessage(preMessage + "§A Rank updated to: §6Baronet");
        } else if (playerLand >= 10000 && playerLand < 30625) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Count");
            p.sendMessage(preMessage + "§A Rank updated to: §6Count");
        } else if (playerLand >= 30625 && playerLand < 62500) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Viscount");
            p.sendMessage(preMessage + "§A Rank updated to: §6Viscount");
        } else if (playerLand >= 62500 && playerLand < 125000) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Viceroy");
            p.sendMessage(preMessage + "§A Rank updated to: §6Viceroy");
        } else if (playerLand >= 125000) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Imperatore");
            p.sendMessage(preMessage + "§A Rank updated to: §6Imperatore");
        } else {
            p.sendMessage(preMessage + "§A You do not have enough wild land!");
        }
    }

    private void getPlayerCityRank(int playerLand, PermissionUser pPex, Player p, List<String> pGroups) {
        if (playerLand >= 100 && playerLand < 400) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Villager");
            p.sendMessage(preMessage + "§A Rank updated to: §6Villager");
        } else if (playerLand >= 400 && playerLand < 800) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Commoner");
            p.sendMessage(preMessage + "§A Rank updated to: §6Commoner");
        } else if (playerLand >= 800 && playerLand < 3600) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Aristocrat");
            p.sendMessage(preMessage + "§A Rank updated to: §6Aristocrat");
        } else if (playerLand >= 7200) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Lord");
            p.sendMessage(preMessage + "§A Rank updated to: §6Lord");
        } else {
            p.sendMessage(preMessage + "§A You do not have enough city land!");
        }
    }

    private void getPlayerNetherRank(int playerLand, PermissionUser pPex, Player p, List<String> pGroups) {
        if (playerLand >= 100 && playerLand < 400) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Grue");
            p.sendMessage(preMessage + "§A Rank updated to: §6Grue");
        } else if (playerLand >= 400 && playerLand < 800) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Deviant");
            p.sendMessage(preMessage + "§A Rank updated to: §6Deviant");
        } else if (playerLand >= 800 && playerLand < 1600) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Hellian");
            p.sendMessage(preMessage + "§A Rank updated to: §6Hellian");
        } else if (playerLand >= 1600 && playerLand < 3600) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Dark-Lord");
            p.sendMessage(preMessage + "§A Rank updated to: §6Dark-Lord");
        } else if (playerLand >= 3600 && playerLand < 7200) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Scarlet-Prince");
            p.sendMessage(preMessage + "§A Rank updated to: §6Scarlet-Prince");
        } else if (playerLand >= 7200) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Shadow-King");
            p.sendMessage(preMessage + "§A Rank updated to: §6Shadow-King");
        } else {
            p.sendMessage(preMessage + "§A You do not have enough city land!");
        }
    }

    private void getPlayerEndRank(int playerLand, PermissionUser pPex, Player p, List<String> pGroups) {
        if (playerLand >= 100 && playerLand < 400) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Acolyte");
            p.sendMessage(preMessage + "§A Rank updated to: §6Acolyte");
        } else if (playerLand >= 400 && playerLand < 800) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Shade");
            p.sendMessage(preMessage + "§A Rank updated to: §6Shade");
        } else if (playerLand >= 800 && playerLand < 1600) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Cultist");
            p.sendMessage(preMessage + "§A Rank updated to: §6Cultist");
        } else if (playerLand >= 1600 && playerLand < 3600) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Zealot");
            p.sendMessage(preMessage + "§A Rank updated to: §6Zealot");
        } else if (playerLand >= 3600 && playerLand < 7200) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Dragon-Priest");
            p.sendMessage(preMessage + "§A Rank updated to: §6Dragon-Priest");
        } else if (playerLand >= 7200) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Dragon-Lord");
            p.sendMessage(preMessage + "§A Rank updated to: §6Dragon-Lord");
        } else {
            p.sendMessage(preMessage + "§A You do not have enough city land!");
        }
    }

    private void getPlayerTownRank(int playerLand, PermissionUser pPex, Player p, List<String> pGroups) {
        if (playerLand >= 3600 && playerLand < 10000) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Baron");
            p.sendMessage(preMessage + "§A Rank updated to: §6Baron");
        } else if (playerLand >= 10000 && playerLand < 30625) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Duke");
            p.sendMessage(preMessage + "§A Rank updated to: §6Duke");
        } else if (playerLand >= 30625 && playerLand < 62500) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("Prince");
            p.sendMessage(preMessage + "§A Rank updated to: §6Prince");
        } else if (playerLand >= 62500 && playerLand < 125000) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("King");
            p.sendMessage(preMessage + "§A Rank updated to: §6King");
        } else if (playerLand >= 125000) {
            for (String n : pGroups) {
                pPex.removeGroup(n);
            }
            pPex.addGroup("DEmperor");
            p.sendMessage(preMessage + "§A Rank updated to: §6Emperor");
        } else {
            p.sendMessage(preMessage + "§A You do not have enough city land!");
        }
    }
}
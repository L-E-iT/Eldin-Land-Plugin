package com.branwidth.EldinLand;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.sql.*;

public class Land implements CommandExecutor {


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure that the Database database connection is established
        Database.connect();
        if (!Database.isConnected()) {
            Database.connect();
        }

        Player p = (Player) sender;
        String pUUID = p.getUniqueId().toString().replace("-","");

        String wildLand,cityLand,netherLand,endLand,totalLand;

        if (Database.isConnected()) {
            try {
                // Prepare SQL statement
                ResultSet rs = Database.getPlayerLand(pUUID);
                while (rs.next()) {
                    // Send land information to user
                    wildLand = Integer.toString(rs.getInt("wild_count"));
                    cityLand = Integer.toString(rs.getInt("city_count"));
                    netherLand = Integer.toString(rs.getInt("nether_count"));
                    endLand = Integer.toString(rs.getInt("end_count"));
                    totalLand = Integer.toString(Integer.parseInt(wildLand) + Integer.parseInt(cityLand) + Integer.parseInt(netherLand) + Integer.parseInt(endLand));
                    p.sendMessage(ChatColor.BLUE + "Total Land Tiles: " + totalLand);
                    p.sendMessage(ChatColor.GREEN + "Total Wild Tiles: " + wildLand);
                    p.sendMessage(ChatColor.GOLD + "Total City Tiles: " + cityLand);
                    p.sendMessage(ChatColor.RED + "Total Nether Tiles: " + netherLand);
                    p.sendMessage(ChatColor.DARK_PURPLE + "Total End Tiles: " + endLand);
                }
            } catch (SQLException ex) {
                System.out.print("SQLExeception: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
        } else {
            p.sendMessage(ChatColor.RED + "There was an issue connecting to the database!");
        }
        return true;
        }
    }


package com.branwidth.EldinLand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

public class Database {

    private static Connection conn;

    //Connecting
    public static void connect() {

        String user = Main.getPlugin().getConfig().getString("MySQL.user");
        String pass = Main.getPlugin().getConfig().getString("MySQL.password");
        String port = Main.getPlugin().getConfig().getString("MySQL.port");
        String location = Main.getPlugin().getConfig().getString("MySQL.location");
        String db = Main.getPlugin().getConfig().getString("MySQL.db");

        String dbUrl = "jdbc:mysql://" + location + ":" + port + "/" + db + "?user=" + user + "&password=" + pass;

        if (!isConnected()){
            try {
                conn = DriverManager.getConnection(dbUrl);
            } catch (SQLException e) {
                e.printStackTrace();
                Main.getPlugin().getLogger().info("The issue is in mysql class");
            }
        }
    }

    //Disconnecting
    public static void disconnect() {
        if (isConnected()) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    public static boolean isConnected() {
        return (conn != null);
    }

    public static Connection getConnection() {
        return conn;
    }

    public static ResultSet getPlayerLand(String uuid) throws SQLException {
        // get set query
        PreparedStatement PSland = Database.getConnection().prepareStatement("SELECT * FROM players WHERE UUID = ?");
        PSland.setString(1, uuid);

        // get results from query
        ResultSet RSland = PSland.executeQuery();

        if (RSland.isBeforeFirst()) {
            return RSland;
        } else {
            return null;
        }
    }

    public static String getPlayerWorld(String playerWorldName) {
        switch (playerWorldName) {
            case "world":
                return "wild_count";
            case "world_nether":
                return "nether_count";
            case "world_the_end":
                return "end_count";
            default:
                return null;
        }
    }

    public static void changePlayerWildLand(String pUUID, Long Count, String playerWorld) throws SQLException {
        StringBuilder newUUID = new StringBuilder(pUUID);
        newUUID.insert(8,"-");
        newUUID.insert(13,"-");
        newUUID.insert(18, "-");
        newUUID.insert(23,"-");

        Main.getPlugin().getLogger().info(String.valueOf(newUUID));
        String stringUUID = newUUID.toString();
        UUID playerUUID = UUID.fromString(stringUUID);
        String PlayerName = Bukkit.getPlayer(playerUUID).getName();

        if (Database.getPlayerLand(pUUID) == null) {

            PreparedStatement psInsert = null;
            // set player world equal to count
            if (playerWorld.equals("wild_count")) {
                psInsert = Database.getConnection().prepareStatement("INSERT INTO players SET username = ?, uuid = ?, wild_count = ? ");
            } else  if (playerWorld.equals("city_count")) {
                psInsert = Database.getConnection().prepareStatement("INSERT INTO players SET username = ?, uuid = ?, city_count = ? ");
            } else  if (playerWorld.equals("nether_count")) {
                psInsert = Database.getConnection().prepareStatement("INSERT INTO players SET username = ?, uuid = ?, nether_count = ? ");
            } else  if (playerWorld.equals("end_count")) {
                psInsert = Database.getConnection().prepareStatement("INSERT INTO players SET username = ?, uuid = ?, end_count = ? ");
            }

            psInsert.setString(1, PlayerName);
            psInsert.setString(2, pUUID);
            psInsert.setLong(3, Count);

            psInsert.execute();
        } else {
            PreparedStatement psUpdate = null;

            if (playerWorld.equals("wild_count")) {
                psUpdate = Database.getConnection().prepareStatement("UPDATE players SET wild_count = ? WHERE uuid = ?");
            } else  if (playerWorld.equals("city_count")) {
                psUpdate = Database.getConnection().prepareStatement("UPDATE players SET city_count = ? WHERE uuid = ?");
            } else  if (playerWorld.equals("nether_count")) {
                psUpdate = Database.getConnection().prepareStatement("UPDATE players SET nether_count = ? WHERE uuid = ?");
            } else  if (playerWorld.equals("end_count")) {
                psUpdate = Database.getConnection().prepareStatement("UPDATE players SET end_count = ? WHERE uuid = ?");
            }
            // set player world equal to count
            psUpdate.setLong(1, Count);
            psUpdate.setString(2, pUUID);

            psUpdate.executeUpdate();
        }
    }

    private static int getPlayerID(String pUUID) throws SQLException {
        try {
            // get players ID value from table
            PreparedStatement PSplayer = Database.getConnection().prepareStatement("select * from players WHERE uuid=?");
            PSplayer.setString(1, pUUID);

            ResultSet RSplayer = PSplayer.executeQuery();

            RSplayer.next();
            return RSplayer.getInt("id");
        } catch (Exception e) {
            return 0;
        }
    }

    public static void changePlayerCityLand(String pUUID, Long tileCount, String playerName) throws SQLException {

        if (getPlayerID(pUUID) == 0) {

            // if player doesn't exist, create player and add land
            PreparedStatement ps = Database.getConnection().prepareStatement("INSERT INTO players SET username = ?, uuid = ?, city_count = ?");
            ps.setString(1, playerName);
            ps.setString(2, pUUID);
            ps.setLong(3, tileCount);

            ps.execute();
        } else {
            // add city land to player
            PreparedStatement ps = Database.getConnection().prepareStatement("UPDATE players SET city_count = city_count + ? WHERE uuid = ?");

            ps.setLong(1, tileCount);
            ps.setString(2, pUUID);

            ps.execute();
        }
    }

    public static void changeCityPlot(String plotName, Long tileCount, String pUUID, Boolean addLand) throws SQLException {
        int playerID = getPlayerID(pUUID);
        Boolean ownsLand = false;
        int cityID = 0;

        // Get city ID from cities composition table
        PreparedStatement psCheckPlots = Database.getConnection().prepareStatement("SELECT id FROM city_composition_lookup WHERE residence_name = ?");
        psCheckPlots.setString(1, plotName);

        ResultSet rsCheckPlots = psCheckPlots.executeQuery();
        if (rsCheckPlots != null) {
            rsCheckPlots.next();
            cityID = rsCheckPlots.getInt("id");
        } else {
            // Get city ID from cities table
            PreparedStatement psID = Database.getConnection().prepareStatement("SELECT id FROM cities WHERE city_name = ?");
            psID.setString(1,plotName);

            ResultSet rsID = psID.executeQuery();
            rsID.next();
            cityID = rsID.getInt("id");
        }

        // Will get if a player owns land in a city
        PreparedStatement psOwnLand = Database.getConnection().prepareStatement("SELECT COUNT(*) as count FROM city_plots WHERE city_id = ? AND player_id =?");
        psOwnLand.setInt(1, cityID);
        psOwnLand.setInt(2, playerID);


        ResultSet rsOwnLand = psOwnLand.executeQuery();
        rsOwnLand.next();
        if (rsOwnLand.getInt("count") > 0) {
            ownsLand = true;
        }


        // Checks if the player already owns land in the town
        if (ownsLand) {
            // Check if we are adding land
            if (addLand) {
                PreparedStatement psOwnAdd = Database.getConnection().prepareStatement(
                        "UPDATE city_plots SET plot_tiles = plot_tiles + ? WHERE (player_id = ?) AND (city_id = ?) ");

                psOwnAdd.setLong(1, tileCount);
                psOwnAdd.setInt(2, playerID);
                psOwnAdd.setInt(3, cityID);

                psOwnAdd.execute();
                // Check if we are removing land
            } else {
                PreparedStatement psOwnRemove = Database.getConnection().prepareStatement(
                        "UPDATE city_plots SET plot_tiles = plot_tiles - ? WHERE (player_id = ?) AND (city_id = ?) ");
                Main.getPlugin().getLogger().info("Removing Land");
                psOwnRemove.execute();
                if (playerCityPlotAmount(playerID,cityID)==0) {
                    PreparedStatement psRemovePlot = Database.getConnection().prepareStatement("DELETE FROM city_plots WHERE (city_id = ?) AND (player_id =?)");
                    psRemovePlot.setInt(1, cityID);
                    psRemovePlot.setInt(2, playerID);

                    psRemovePlot.execute();
                }
            }
            // If player doesn't own land in that city yet
        } else {
            if (addLand) {
//                PreparedStatement psNoOwnAdd = Database.getConnection().prepareStatement(
//                        "INSERT INTO city_plots SET plot_tiles = " + tileCount + ", player_id = '" + playerID + "', city_id = " + cityID);
                PreparedStatement psNoOwnAdd = Database.getConnection().prepareStatement(
                        "INSERT INTO city_plots SET plot_tiles = ?, player_id = ?, city_id = ?");

                psNoOwnAdd.setLong(1, tileCount);
                psNoOwnAdd.setInt(2, playerID);
                psNoOwnAdd.setInt(3, cityID);

                psNoOwnAdd.execute();
            } else {
                // God I hope this never executes...
                Player p = Bukkit.getPlayer(pUUID);
                p.sendMessage(ChatColor.RED + "Something went wrong... Submit a ticket about Database Class Errors and City Land with the Eldin Land Plugin");
            }
        }
    }

    public static void changeCitySize(String pUUID, Long Count, String playerWorld, String plotName) throws SQLException {
        // Set Database statement for adding city land to a town (Town expansion)
        int cityID = getCityID(plotName);
        PreparedStatement psChangeCitySize = Database.getConnection().prepareStatement(
                "UPDATE cities SET total_tiles = total_tiles + ? WHERE id = ?");
        psChangeCitySize.setLong(1, Count);
        psChangeCitySize.setInt(2, cityID);

        psChangeCitySize.execute();
    }

    // Check to see if a plot is registered as a city
    public static Boolean isCity(String plotName) throws SQLException {
        int cityID = getCityID(plotName);
        // Statement for if a plot is a city
        PreparedStatement psCityNames = Database.getConnection().prepareStatement(
                "SELECT id FROM cities");
        ResultSet rsCityNames = psCityNames.executeQuery();
        // for each row in the cities table
        while (rsCityNames.next()) {
            // Get city name and check to current plot name
            if (rsCityNames.getString("id").equals(cityID)) {
                // return true if it exists
                return true;
            }
        }
        return false;
    }

    public static int playerCityPlotAmount(int playerID, int cityID) throws SQLException {
        try {
            PreparedStatement PScityPlotAmount = Database.getConnection().prepareStatement("SELECT * FROM city_plots");
            ResultSet RScityPlotAmount = PScityPlotAmount.executeQuery();
            while (RScityPlotAmount.next()) {
                if (RScityPlotAmount.getInt("city_id") == cityID && RScityPlotAmount.getInt("player_id") == playerID) {
                    return RScityPlotAmount.getInt("plot_tiles");
                }
            }
        } catch (Exception e) {
            Main.getPlugin().getLogger().info("Database Failed to get city ID");
            return 1;
        }
        Main.getPlugin().getLogger().info("playerCityPlotAmount did not work in Database.java");
        return 1;

    }

    public static int getCityID(String plotName) {
        // May not be needed.
        try {
            // Get city ID from cities composition table
            PreparedStatement psCheckPlots = Database.getConnection().prepareStatement("SELECT id FROM city_composition_lookup WHERE residence_name = ?");
            psCheckPlots.setString(1, plotName);

            ResultSet rsCheckPlots = psCheckPlots.executeQuery();
            rsCheckPlots.next();
            return rsCheckPlots.getInt("id");
        } catch (Exception e) {
            return 0;
        }
    }

}

package com.branwidth.EldinLand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

public class MySQL {

    private static Connection conn;

    //Connecting
    public static void connect() {

        String user = Main.getPlugin().getConfig().getString("MySQL.user");
        String pass = Main.getPlugin().getConfig().getString("MySQL.password");
        String port = Main.getPlugin().getConfig().getString("MySQL.port");
        String location = Main.getPlugin().getConfig().getString("MySQL.location");
        String db = Main.getPlugin().getConfig().getString("MySQL.db");


        String dbUrl = "jdbc:mysql://"+ location +":" + port + "/" + db + "?autoReconnect=true&user=" + user + "&password=" + pass;

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
        // get result set of a players land
        PreparedStatement PSland = MySQL.getConnection().prepareStatement("select * from players WHERE uuid='" + uuid + "'");
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
        Bukkit.getPlayer(playerUUID).sendMessage(String.valueOf(playerUUID));
        if (MySQL.getPlayerLand(pUUID) == null) {
            PreparedStatement psInsert = MySQL.getConnection().prepareStatement("INSERT INTO players SET username = '" + PlayerName +
                    "', uuid = '"  + pUUID + "', " +  playerWorld + " = " + Count);
            psInsert.execute();
        } else {
            PreparedStatement psUpdate = MySQL.getConnection().prepareStatement("UPDATE players SET " + playerWorld + " = " + Count +
                    " WHERE uuid = '" + pUUID + "'");
            psUpdate.executeUpdate();
        }
    }

    private static int getPlayerID(String pUUID) throws SQLException {
        try {
            PreparedStatement PSplayer = MySQL.getConnection().prepareStatement("select * from players WHERE uuid='" + pUUID + "'");
            ResultSet RSplayer = PSplayer.executeQuery();
            RSplayer.next();
            return RSplayer.getInt("id");
        } catch (Exception e) {
            return 0;
        }
    }

    public static void changePlayerCityLand(String pUUID, Long tileCount, String playerName) throws SQLException {

        if (getPlayerID(pUUID) == 0) {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO players SET username = '" + playerName +
                    "', uuid = '"  + pUUID + "', city_count = " + tileCount);
            ps.execute();
        } else {
            Main.getPlugin().getLogger().info("changePlayerCityLand else called!");
            PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE players SET city_count = city_count + " + tileCount +
                    " WHERE uuid = '" + pUUID + "'");
            ps.execute();
        }
    }

    public static void changeCityPlot(String townName, Long tileCount, String pUUID, Boolean addLand) throws SQLException {
        int playerID = getPlayerID(pUUID);
        Boolean ownsLand = false;

        // Get city ID from cities table
        PreparedStatement psID = MySQL.getConnection().prepareStatement("SELECT id FROM cities WHERE city_name = '" + townName + "'");
        ResultSet rsID = psID.executeQuery();
        rsID.next();
        int cityID = rsID.getInt("id");

        // Will get if a player owns land in a city
        PreparedStatement psOwnLand = MySQL.getConnection().prepareStatement("SELECT COUNT(*) as count FROM city_plots WHERE city_id = " + cityID + " AND player_id = " + playerID);
        ResultSet rsOwnLand = psOwnLand.executeQuery();
        rsOwnLand.next();
        if (rsOwnLand.getInt("count") > 0) {
            ownsLand = true;
        }

        Main.getPlugin().getLogger().info("Made it!");
        // Checks if the player already owns land in the town
        if (ownsLand) {
            // Check if we are adding land
            if (addLand) {
                PreparedStatement psOwnAdd = MySQL.getConnection().prepareStatement(
                        "UPDATE city_plots SET plot_tiles = plot_tiles +" + tileCount + " WHERE (player_id = " + playerID + ") AND (city_id = " + cityID + ")");
                psOwnAdd.execute();
                // Check if we are removing land
            } else {
                PreparedStatement psOwnRemove = MySQL.getConnection().prepareStatement(
                        "UPDATE city_plots SET plot_tiles = plot_tiles -" + tileCount + " WHERE " + " (player_id = " + playerID + " AND city_id = " + cityID + ")");
                Main.getPlugin().getLogger().info("Removing Land");
                psOwnRemove.execute();
                if (playerCityPlotAmount(playerID,cityID)==0) {
                    PreparedStatement psRemovePlot = MySQL.getConnection().prepareStatement("DELETE FROM city_plots WHERE (city_id = " + cityID + ") AND (player_id =" + playerID +" )");
                    psRemovePlot.execute();
                }
            }
            // If player doesn't own land in that city yet
        } else {
            if (addLand) {
                PreparedStatement psNoOwnAdd = MySQL.getConnection().prepareStatement(
                        "INSERT INTO city_plots SET plot_tiles = " + tileCount + ", player_id = '" + playerID + "', city_id = " + cityID);
                psNoOwnAdd.execute();
            } else {
                // God I hope this never executes...
                Player p = Bukkit.getPlayer(pUUID);
                p.sendMessage(ChatColor.RED + "Something went wrong... Submit a ticket about MySQL Class Errors and City Land with the Eldin Land Plugin");
            }
        }
    }

    public static void changeCitySize(String pUUID, Long Count, String playerWorld, String townName) throws SQLException {
        // Set MySQL statement for adding city land to a town (Town expansion)
        PreparedStatement psChangeCitySize = MySQL.getConnection().prepareStatement(
                "UPDATE cities SET total_tiles = total_tiles + " + Count + " WHERE town_name = '" + townName + "'");
        psChangeCitySize.execute();
    }

    // Check to see if a plot is registered as a city
    public static Boolean isCity(String townName) throws SQLException {
        // Statement for if a plot is a city
        PreparedStatement psCityNames = MySQL.getConnection().prepareStatement(
                "SELECT city_name FROM cities");
        ResultSet rsCityNames = psCityNames.executeQuery();
        // for each row in the cities table
        while (rsCityNames.next()) {
            // Get city name and check to current plot name
            if (rsCityNames.getString("city_name").equals(townName)) {
                // return true if it exists
                return true;
            }
        }
        return false;
    }

    public static int playerCityPlotAmount(int playerID, int cityID) throws SQLException {
        try {
            PreparedStatement PScityPlotAmount = MySQL.getConnection().prepareStatement("SELECT * FROM city_plots");
            ResultSet RScityPlotAmount = PScityPlotAmount.executeQuery();
            while (RScityPlotAmount.next()) {
                if (RScityPlotAmount.getInt("city_id") == cityID && RScityPlotAmount.getInt("player_id") == playerID) {
                    return RScityPlotAmount.getInt("plot_tiles");
                }
            }
        } catch (Exception e) {
            Main.getPlugin().getLogger().info("MySQL Failed to get city ID");
            return 1;
        }
        Main.getPlugin().getLogger().info("playerCityPlotAmount did not work in MySQL.java");
        return 1;

    }

    public static int getCityID(String townName) {
        // May not be needed.
        try {
            PreparedStatement PScity = MySQL.getConnection().prepareStatement("SELECT id FROM cities WHERE city_name = '" + townName + "'");
            ResultSet RScity = PScity.executeQuery();
            RScity.next();
            return RScity.getInt("id");
        } catch (Exception e) {
            return 0;
        }
    }

}

package com.branwidth.EldinLand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.*;

public class MySQL {

    private static Connection conn;

    //Connecting
    public static void connect() {

        String user = Main.getPlugin().getConfig().getString("MySQL.user");
        String pass = Main.getPlugin().getConfig().getString("MySQL.password");
        String port = Main.getPlugin().getConfig().getString("MySQL.port");
        String location = Main.getPlugin().getConfig().getString("MySQL.location");
        String db = Main.getPlugin().getConfig().getString("MySQL.db");


        String dbUrl = "jdbc:mysql://"+ location +":" + port + "/" + db + "?user=" + user + "&password=" + pass;

        if (!isConnected()){
            try {
                conn = DriverManager.getConnection(dbUrl);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //Disconnecting
    static void disconnect() {
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
        return RSland;
    }

    public static String getPlayerWorld(String playerWorldName) {
        switch (playerWorldName) {
            case "world":
                return "wild_count";
            case "world_nether":
                return "nether_count";
            case "world_the_end":
                return "end_cound";
            default:
                return null;
        }
    }

    public static void changePlayerWildLand(String pUUID, Long Count, String playerWorld) throws SQLException {
        PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE players SET " + playerWorld + " = " + Count +
                " WHERE uuid = '" + pUUID + "'");
        ps.executeUpdate();
    }

    private static int getPlayerID(String pUUID) throws SQLException {
        PreparedStatement PSplayer = MySQL.getConnection().prepareStatement("select * from players WHERE uuid='" + pUUID + "'");
        ResultSet RSplayer = PSplayer.executeQuery();
        RSplayer.next();
        return RSplayer.getInt("id");
    }

    public static void changePlayerCityLand(String playerName, String pUUID, Long tileCount) throws SQLException {
        PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE players SET city_count = " + tileCount +
                " WHERE uuid = '" + pUUID + "'");

        // Continue this with adding city land
    }

    public static void changeCityPlot(String playerName, String townName, Long tileCount, String pUUID, Boolean addLand) throws SQLException {
        int playerID = getPlayerID(pUUID);
        Boolean ownsLand = false;

        // Get city ID from cities table
        PreparedStatement psID = MySQL.getConnection().prepareStatement("SELECT id FROM cities WHERE city_name = '" + townName + "'");
        ResultSet rsID = psID.executeQuery();
        rsID.next();
        int cityID = rsID.getInt("id");

        // Will get if a player owns land in a city
        PreparedStatement psOwnLand = MySQL.getConnection().prepareStatement("SELECT COUNT(*) FROM city_plots WHERE city_id = " + cityID + " AND player_id = " + playerID);
        ResultSet rsOwnLand = psOwnLand.executeQuery();
        rsOwnLand.next();
        if (rsOwnLand.getInt("count") > 0) {
            ownsLand = true;
        }

        // Set prepared statement for adding land into city tables
//        PrepasredStatement psAddCityLand = MySQL.getConnection().prepareStatement();

        if (ownsLand) {
            if (addLand) {
                // Add land to existing count
            } else {
                // remove land from existing amount
            }
        } else {
            if (addLand) {
                // add a new record to the existing table
            } else {
                Player p = Bukkit.getPlayer(pUUID);
                p.sendMessage(ChatColor.RED + "Something went wrong... Submit a ticket about MySQL Class Errors and City Land with the Eldin Land Plugin");
            }
        }
    }

    public static void changeCitySize(String pUUID, Long Count, String playerWorld, String townName) {
        // "UPDATE cities SET total_tiles = total_tiles + "Count" WHERE town_name = '"townName + "'"
    }

    public static void changePlotOwner() throws SQLException {

    }

}

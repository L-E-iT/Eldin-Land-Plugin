package com.branwidth.EldinLand;

import com.bekvon.bukkit.residence.Residence;
import org.bukkit.ChatColor;

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

//        Will equal city ID:
//        ("SELECT id FROM cities WHERE city_name = '" + townName + "'");

//        Will get if a player owns land in a city
//        ("SELECT COUNT(*) FROM city_plots WHERE (city_id = " + cityID + ") AND (player_id = " + playerID ));

/*        if (player owns land) {
                if (addLand) {
                    add land to the existing count
                    }
                else {
                    remove land from existing count
                }
            } else {
                if (addLand) {
                    add new record to the existing table
                    }
                else {
                    this should never happen? o.o
          }
    */
    }

    public static void changeCitySize(String pUUID, Long Count, String playerWorld, String townName) {
        // "UPDATE cities SET total_tiles = total_tiles + "Count" WHERE town_name = '"townName + "'"
    }

    public static void changePlotOwner() throws SQLException {

    }

}

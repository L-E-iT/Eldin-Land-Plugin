package com.branwidth.EldinLand;

import java.sql.*;

class MySQL {

    private static Connection conn;

    //Connecting
    static void connect() {

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

    static boolean isConnected() {
        return (conn != null);
    }

    static Connection getConnection() {
        return conn;
    }

    static ResultSet getPlayerLand(String uuid) throws SQLException {
        // get result set of a players land
        PreparedStatement PSland = MySQL.getConnection().prepareStatement("select * from players WHERE uuid='" + uuid + "'");
        ResultSet RSland = PSland.executeQuery();
        return RSland;
    }

}

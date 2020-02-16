package net.llamadevelopment.bansystem.components.managers.database;

import net.llamadevelopment.bansystem.BanSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class MySqlProvider {

    private static Connection connection;

    private void connect(BanSystem instance) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + instance.getConfig().getString("MySql.Host") + ":" + instance.getConfig().getString("MySql.Port") + "/" + instance.getConfig().getString("MySql.Database") + "?autoReconnect=true", instance.getConfig().getString("MySql.User"), instance.getConfig().getString("MySql.Password"));
            instance.getLogger().info("§aConnected successfully to database!");
        } catch (Exception e) {
            instance.getLogger().error("§4Failed to connect to database.");
            instance.getLogger().error("§4Please check your details in the config.yml.");
            e.printStackTrace();
            instance.dataError = true;
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public void createTables() {
        this.connect(BanSystem.getInstance());
        update("CREATE TABLE IF NOT EXISTS bans(name VARCHAR(255), reason VARCHAR(255), id VARCHAR(255), banner VARCHAR(255), date VARCHAR(255), end BIGINT(255), PRIMARY KEY (id));");
        update("CREATE TABLE IF NOT EXISTS mutes(name VARCHAR(255), reason VARCHAR(255), id VARCHAR(255), banner VARCHAR(255), date VARCHAR(255), end BIGINT(255), PRIMARY KEY (id));");
        update("CREATE TABLE IF NOT EXISTS banlogs(player VARCHAR(255), reason VARCHAR(255), id VARCHAR(255), banner VARCHAR(255), date VARCHAR(255), PRIMARY KEY (id));");
        update("CREATE TABLE IF NOT EXISTS mutelogs(player VARCHAR(255), reason VARCHAR(255), id VARCHAR(255), banner VARCHAR(255), date VARCHAR(255), PRIMARY KEY (id));");
    }

    public static void update(String qry) {
        if (connection != null) {
            try {
                PreparedStatement ps = connection.prepareStatement(qry);
                ps.executeUpdate();
                ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

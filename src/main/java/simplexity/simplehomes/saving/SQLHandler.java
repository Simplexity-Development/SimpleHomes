package simplexity.simplehomes.saving;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import simplexity.simplehomes.Home;
import simplexity.simplehomes.SimpleHomes;
import simplexity.simplehomes.configs.ConfigHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class SQLHandler extends SaveHandler {

    Connection connection;
    Logger logger = SimpleHomes.getInstance().getLogger();

    private SQLHandler() {
    }

    private static SQLHandler instance;

    public static SQLHandler getInstance() {
        if (instance == null) instance = new SQLHandler();
        return instance;
    }

    @Override
    public void init() {
        try {
            connection = sqlOrSqlLite();
            try (Statement statement = connection.createStatement()) {
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS homes (
                        player_uuid_and_name VARCHAR (255) PRIMARY KEY,
                        player_uuid VARCHAR(36),
                        home_name VARCHAR(255),
                        world_uuid VARCHAR(255),
                        world_name VARCHAR(255),
                        location_x DOUBLE,
                        location_y DOUBLE,
                        location_z DOUBLE,
                        yaw FLOAT,
                        pitch FLOAT
                        );""");
            }

        } catch (SQLException e) {
            logger.severe("Failed to connect to SQLite database");
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Home> getHomes(OfflinePlayer player) {
        List<Home> homes = new ArrayList<>();
        try {
            String query = "SELECT * FROM homes WHERE player_uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, player.getUniqueId().toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String worldUUIDString = resultSet.getString("world_uuid");
                        UUID worldUUID = UUID.fromString(worldUUIDString);
                        homes.add(new Home(
                                resultSet.getString("home_name"),
                                new Location(
                                        SimpleHomes.getInstance().getServer().getWorld(worldUUID),
                                        resultSet.getDouble("location_x"),
                                        resultSet.getDouble("location_y"),
                                        resultSet.getDouble("location_z"),
                                        resultSet.getFloat("yaw"),
                                        resultSet.getFloat("pitch")
                                )
                        ));
                    }
                    return homes;
                } catch (SQLException e) {
                    logger.severe("Failed to get homes for " + player);
                    logger.severe("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            logger.severe("Failed to get homes for " + player);
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Home getHome(OfflinePlayer player, String homeName) {
        try {
            // Prepare the SQL statement to check if the home exists
            String checkIfExistsQuery = "SELECT * FROM homes WHERE player_uuid = ? AND home_name = ?";
            try (PreparedStatement homeExists = connection.prepareStatement(checkIfExistsQuery)) {
                homeExists.setString(1, player.getUniqueId().toString());
                homeExists.setString(2, homeName);
                try (ResultSet resultSet = homeExists.executeQuery()) {
                    if (resultSet != null) {
                        String worldUUIDString = resultSet.getString("world_uuid");
                        UUID worldUUID = UUID.fromString(worldUUIDString);
                        return new Home(
                                resultSet.getString("home_name"),
                                new Location(
                                        SimpleHomes.getInstance().getServer().getWorld(worldUUID),
                                        resultSet.getDouble("location_x"),
                                        resultSet.getDouble("location_y"),
                                        resultSet.getDouble("location_z"),
                                        resultSet.getFloat("yaw"),
                                        resultSet.getFloat("pitch")
                                )
                        );
                    }
                    return null;
                } catch (SQLException e) {
                    logger.severe("Failed to get home for " + player);
                    logger.severe("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            logger.severe("Failed to get home for " + player);
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deleteHome(OfflinePlayer player, String homeName) {
        try {
            // Prepare the SQL statement to check if the home exists
            String checkIfExistsQuery = "SELECT * FROM homes WHERE player_uuid = ? AND home_name = ?";
            try (PreparedStatement homeExists = connection.prepareStatement(checkIfExistsQuery)) {
                homeExists.setString(1, player.getUniqueId().toString());
                homeExists.setString(2, homeName);
                try (ResultSet resultSet = homeExists.executeQuery()) {
                    if (resultSet != null) { // Home exists
                        String deleteQuery = "DELETE FROM homes WHERE player_uuid = ? AND home_name = ?";
                        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                            deleteStatement.setString(1, player.getUniqueId().toString());
                            deleteStatement.setString(2, homeName);
                            deleteStatement.executeUpdate();
                        }
                    } else {
                        return false; // Don't overwrite, return false
                    }
                }
            }
        } catch (SQLException e) {
            logger.severe("Failed to delete home for " + player);
            e.printStackTrace();
            return false; // Error occurred while setting home
        }
        return false;
    }

    @Override
    public boolean setHome(OfflinePlayer player, String homeName, Player onlinePlayer, boolean overwrite) {
        try {
            // Prepare the SQL statement to check if the home exists
            String checkIfExistsQuery = "SELECT COUNT(*) AS count FROM homes WHERE player_uuid = ? AND home_name = ?";

            try (PreparedStatement homeExists = connection.prepareStatement(checkIfExistsQuery)) {
                homeExists.setString(1, player.getUniqueId().toString());
                homeExists.setString(2, homeName);

                try (ResultSet resultSet = homeExists.executeQuery()) {
                    if (resultSet.getInt("count") > 0) { // Home exists
                        if (!overwrite) {
                            return false; // Don't overwrite, return false
                        }
                    }
                }
            }
            // Insert the new home
            String insertQuery = "REPLACE INTO homes (player_uuid_and_name, player_uuid, home_name, world_uuid, location_x, location_y, " +
                    "location_z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, player.getUniqueId() + homeName);
                insertStatement.setString(2, player.getUniqueId().toString());
                insertStatement.setString(3, homeName);
                insertStatement.setString(4, String.valueOf(onlinePlayer.getWorld().getUID()));
                insertStatement.setDouble(5, onlinePlayer.getX());
                insertStatement.setDouble(6, onlinePlayer.getY());
                insertStatement.setDouble(7, onlinePlayer.getZ());
                insertStatement.setFloat(8, onlinePlayer.getYaw());
                insertStatement.setFloat(9, onlinePlayer.getPitch());
                insertStatement.executeUpdate();
            }
            return true; // Home set successfully
        } catch (SQLException e) {
            logger.severe("Failed to set home for " + player);
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
            return false; // Error occurred while setting home
        }
    }

    private Connection sqlOrSqlLite() throws SQLException {
        if (ConfigHandler.getInstance().isUsingMysql()) {
            return DriverManager.getConnection("jdbc:mysql://" + ConfigHandler.getInstance().getIp() + "/" + ConfigHandler.getInstance().getName(), ConfigHandler.getInstance().getUsername(), ConfigHandler.getInstance().getPassword());
        } else {
            return DriverManager.getConnection("jdbc:sqlite:" + SimpleHomes.getInstance().getDataFolder() + "/homes.db");
        }
    }
}

package simplexity.simplehomes.saving;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import simplexity.simplehomes.Home;
import simplexity.simplehomes.SimpleHomes;
import simplexity.simplehomes.saving.SaveHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SQLiteHandler extends SaveHandler {
    
    Connection connection;
    Logger logger = SimpleHomes.getInstance().getLogger();
    private SQLiteHandler(){}
    private static SQLiteHandler instance;
    public static SQLiteHandler getInstance() {
        if (instance == null) instance = new SQLiteHandler();
        return instance;
    }
    
    @Override
    public void init() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + SimpleHomes.getInstance().getDataFolder() + "/homes.db");
            try (Statement statement = connection.createStatement()) {
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS homes (
                        player_uuid VARCHAR(36) PRIMARY KEY,
                        home_name VARCHAR(255),
                        world_name VARCHAR(255),
                        location_x INT,
                        location_y INT,
                        location_z INT
                        );""");
            }
            
        } catch (SQLException e) {
            logger.severe("Failed to connect to SQLite database");
            logger.severe("Error: " + e.getMessage());
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
                        homes.add(new Home(
                                resultSet.getString("home_name"),
                                new Location(
                                        SimpleHomes.getInstance().getServer().getWorld(resultSet.getString("world_name")),
                                        resultSet.getInt("location_x"),
                                        resultSet.getInt("location_y"),
                                        resultSet.getInt("location_z")
                                )
                        ));
                    }
                    return homes;
                } catch (SQLException e) {
                    logger.severe("Failed to get homes for " + player);
                    logger.severe("Error: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            logger.severe("Failed to get homes for " + player);
            logger.severe("Error: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public Home getHome(OfflinePlayer player, String homeName) {
        try {
            // Prepare the SQL statement to check if the home exists
            String checkIfExistsQuery = "SELECT COUNT(*) AS count FROM homes WHERE player_uuid = ? AND home_name = ?";
            try (PreparedStatement homeExists = connection.prepareStatement(checkIfExistsQuery)) {
                homeExists.setString(1, player.getUniqueId().toString());
                homeExists.setString(2, homeName);
                try (ResultSet resultSet = homeExists.executeQuery()) {
                    if (resultSet.getInt("count") > 0) {
                        return new Home(
                                resultSet.getString("home_name"),
                                new Location(
                                        SimpleHomes.getInstance().getServer().getWorld(resultSet.getString("world_name")),
                                        resultSet.getInt("location_x"),
                                        resultSet.getInt("location_y"),
                                        resultSet.getInt("location_z")
                                )
                        );
                    }
                    return null;
                } catch (SQLException e) {
                    logger.severe("Failed to get home for " + player);
                    logger.severe("Error: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            logger.severe("Failed to get home for " + player);
            logger.severe("Error: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public boolean deleteHome(OfflinePlayer player, String homeName) {
        try {
            // Prepare the SQL statement to check if the home exists
            String checkIfExistsQuery = "SELECT COUNT(*) AS count FROM homes WHERE player_uuid = ? AND home_name = ?";
            try (PreparedStatement homeExists = connection.prepareStatement(checkIfExistsQuery)) {
                homeExists.setString(1, player.getUniqueId().toString());
                homeExists.setString(2, homeName);
                try (ResultSet resultSet = homeExists.executeQuery()) {
                    if (resultSet.getInt("count") > 0) { // Home exists
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
            return false; // Error occurred while setting home
        }
        return false;
    }
    
    @Override
    public boolean setHome(OfflinePlayer player, String homeName, Location location, boolean overwrite) {
        try {
            // Prepare the SQL statement to check if the home exists
            String checkIfExistsQuery = "SELECT COUNT(*) AS count FROM homes WHERE player_uuid = ? AND home_name = ?";
            
            try (PreparedStatement homeExists = connection.prepareStatement(checkIfExistsQuery)) {
                homeExists.setString(1, player.getUniqueId().toString());
                homeExists.setString(2, homeName);
                
                try (ResultSet resultSet = homeExists.executeQuery()) {
                    if (resultSet.getInt("count") > 0) { // Home exists
                        if (overwrite) {
                            // Delete the existing home
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
            }
            // Insert the new home
            String insertQuery = "REPLACE INTO homes (player_uuid, home_name, world_name, location_x, location_y, " +
                    "location_z) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, player.getUniqueId().toString());
                insertStatement.setString(2, homeName);
                insertStatement.setString(3, location.getWorld().getName());
                insertStatement.setInt(4, location.getBlockX());
                insertStatement.setInt(5, location.getBlockY());
                insertStatement.setInt(6, location.getBlockZ());
                insertStatement.executeUpdate();
            }
            return true; // Home set successfully
        } catch (SQLException e) {
            logger.severe("Failed to set home for " + player);
            logger.severe("Error: " + e.getMessage());
            return false; // Error occurred while setting home
        }
    }
    
}

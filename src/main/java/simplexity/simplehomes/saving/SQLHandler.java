package simplexity.simplehomes.saving;

import org.bukkit.Location;
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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class SQLHandler {

    Connection connection;
    Logger logger = SimpleHomes.getInstance().getLogger();

    private SQLHandler() {
    }

    private static final HashMap<UUID, List<Home>> cachedHomes = new HashMap<>();

    private static SQLHandler instance;

    public static SQLHandler getInstance() {
        if (instance == null) instance = new SQLHandler();
        return instance;
    }

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

    public List<Home> getHomes(UUID uuid) {
        if (cachedHomes.containsKey(uuid)) {
            return cachedHomes.get(uuid);
        }
        List<Home> homes = new ArrayList<>();
        String query = "SELECT * FROM homes WHERE player_uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid.toString());
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
                cachedHomes.put(uuid, homes);
                return homes;
            } catch (SQLException e) {
                logger.severe("Failed to get homes");
                logger.severe("Error occurred at Home Result.");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            logger.severe("Failed to get homes");
            logger.severe("Error occurred at Home Checking.");
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteHome(UUID uuid, String homeName) {
        // Prepare the SQL statement to check if the home exists
        String checkIfExistsQuery = "SELECT * FROM homes WHERE player_uuid = ? AND home_name = ?";
        try (PreparedStatement homeExists = connection.prepareStatement(checkIfExistsQuery)) {
            homeExists.setString(1, uuid.toString());
            homeExists.setString(2, homeName);
            try (ResultSet resultSet = homeExists.executeQuery()) {
                if (resultSet.next()) { // Home exists
                    String deleteQuery = "DELETE FROM homes WHERE player_uuid = ? AND home_name = ?";
                    try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                        deleteStatement.setString(1, uuid.toString());
                        deleteStatement.setString(2, homeName);
                        deleteStatement.executeUpdate();
                    }
                } else {
                    return false; // Nothing to delete, return false
                }
            }
        } catch (SQLException e) {
            logger.severe("Failed to delete home");
            e.printStackTrace();
            return false; // Error occurred while deleting home
        }
        updateCache(uuid);
        return false;
    }

    public boolean setHome(UUID uuid, Location location, String homeName) {
        String insertQuery = "REPLACE INTO homes " +
                "(player_uuid_and_name, player_uuid, home_name, world_uuid, location_x, location_y, location_z, yaw, pitch) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            insertStatement.setString(1, uuid + homeName);
            insertStatement.setString(2, uuid.toString());
            insertStatement.setString(3, homeName);
            insertStatement.setString(4, String.valueOf(location.getWorld().getUID()));
            insertStatement.setDouble(5, location.getX());
            insertStatement.setDouble(6, location.getY());
            insertStatement.setDouble(7, location.getZ());
            insertStatement.setFloat(8, location.getYaw());
            insertStatement.setFloat(9, location.getPitch());
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Failed to set home");
            logger.severe("Error occurred at Home Insert.");
            e.printStackTrace();
            return false; // Error occurred while setting home
        }
        updateCache(uuid);
        return true; // Home set successfully
    }

    private void updateCache(UUID uuid) {
        List<Home> homes = new ArrayList<>();
        String query = "SELECT * FROM homes WHERE player_uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid.toString());
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
                cachedHomes.put(uuid, homes);
            }
        } catch (SQLException e) {
            logger.severe("Failed to update cache");
            e.printStackTrace();
        }
    }

    public void removePlayerFromCache(UUID uuid) {
        cachedHomes.remove(uuid);
    }

    private Connection sqlOrSqlLite() throws SQLException {
        if (ConfigHandler.getInstance().isUsingMysql()) {
            return DriverManager.getConnection("jdbc:mysql://" + ConfigHandler.getInstance().getIp() + "/" + ConfigHandler.getInstance().getName(), ConfigHandler.getInstance().getUsername(), ConfigHandler.getInstance().getPassword());
        } else {
            return DriverManager.getConnection("jdbc:sqlite:" + SimpleHomes.getInstance().getDataFolder() + "/homes.db");
        }
    }
}

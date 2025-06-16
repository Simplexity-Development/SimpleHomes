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
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class SQLHandler {

    private Connection connection;
    Logger logger = SimpleHomes.getInstance().getLogger();

    private SQLHandler() {
    }

    private static SQLHandler instance;

    public static SQLHandler getInstance() {
        if (instance == null) instance = new SQLHandler();
        return instance;
    }

    public void init() {

        try (Statement statement = getConnection().createStatement()) {
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

        } catch (SQLException e) {
            logger.severe("Failed to connect to SQLite database");
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Home> getHomes(UUID uuid) {
        List<Home> homes = new ArrayList<>();
        String query = "SELECT * FROM homes WHERE player_uuid = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
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

    public void deleteHome(UUID uuid, String homeName) {
        String checkIfExistsQuery = "SELECT * FROM homes WHERE player_uuid = ? AND home_name = ?";
        try (PreparedStatement homeExists = getConnection().prepareStatement(checkIfExistsQuery)) {
            homeExists.setString(1, uuid.toString());
            homeExists.setString(2, homeName);
            try (ResultSet resultSet = homeExists.executeQuery()) {
                if (resultSet.next()) { // Home exists
                    String deleteQuery = "DELETE FROM homes WHERE player_uuid = ? AND home_name = ?";
                    try (PreparedStatement deleteStatement = getConnection().prepareStatement(deleteQuery)) {
                        deleteStatement.setString(1, uuid.toString());
                        deleteStatement.setString(2, homeName);
                        deleteStatement.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            logger.severe("Failed to delete home");
            e.printStackTrace();
        }
        // home deleted successfully
    }

    public void setHome(UUID uuid, Location location, String homeName) {
        String insertQuery = "REPLACE INTO homes " +
                             "(player_uuid_and_name, player_uuid, home_name, world_uuid, location_x, location_y, location_z, yaw, pitch) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insertStatement = getConnection().prepareStatement(insertQuery)) {
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
        }
    }

    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(2)) {
            connection = sqlOrSqlLite();
        }
        return connection;
    }

    private Connection sqlOrSqlLite() throws SQLException {
        if (ConfigHandler.getInstance().isUsingMysql()) {
            return DriverManager.getConnection("jdbc:mysql://" + ConfigHandler.getInstance().getIp() + "/" + ConfigHandler.getInstance().getName(), ConfigHandler.getInstance().getUsername(), ConfigHandler.getInstance().getPassword());
        } else {
            return DriverManager.getConnection("jdbc:sqlite:" + SimpleHomes.getInstance().getDataFolder() + "/homes.db");
        }
    }
}

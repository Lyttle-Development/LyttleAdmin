package com.lyttldev.lyttleadmin.database;

import com.lyttldev.lyttleadmin.LyttleAdmin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLite {

    private Connection connection;

    public void connect(LyttleAdmin plugin) {
        try {
            // Create or open the database file
            String path = plugin.getDataFolder().getAbsolutePath() + "/database.db";
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            System.out.println("Connected to SQLite database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS inventories (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    uuid VARCHAR(36),
                    username VARCHAR(16),
                    location VARCHAR(255),
                    enabled BIT,
                    dateCreated TIMESTAMP,
                    inventoryContents TEXT
                    );""");

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    uuid VARCHAR(36),
                    username VARCHAR(16),
                    dateCreated TIMESTAMP,
                    enabled BIT,
                    message TEXT
                    );""");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Inventory> getInventories() {
        String query = "SELECT * FROM inventories";
        List<Inventory> inventories = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String uuid = resultSet.getString("uuid");
                String username = resultSet.getString("username");
                String location = resultSet.getString("location");
                Boolean enabled = resultSet.getBoolean("enabled");
                Timestamp dateCreated = resultSet.getTimestamp("dateCreated");
                String inventoryContents = resultSet.getString("inventoryContents");

                Inventory inventory = new Inventory(id, uuid, username, location, enabled, dateCreated, inventoryContents);
                inventories.add(inventory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventories;
    }

    public List<Inventory> getInventories(String uuid) {
        String query = "SELECT * FROM inventories WHERE uuid = ?";
        List<Inventory> inventories = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    String location = resultSet.getString("location");
                    Boolean enabled = resultSet.getBoolean("enabled");
                    Timestamp dateCreated = resultSet.getTimestamp("dateCreated");
                    String inventoryContents = resultSet.getString("inventoryContents");

                    Inventory inventory = new Inventory(id, uuid, username, location, enabled, dateCreated, inventoryContents);
                    inventories.add(inventory);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventories;
    }

    public Inventory getInventory(String uuid) {
        // Select the newest Inventory record for the given UUID
        String query = "SELECT * FROM inventories WHERE uuid = ? AND dateCreated = (SELECT MAX(dateCreated) FROM inventories WHERE uuid = ?)";
        Inventory inventory = null;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid);
            statement.setString(2, uuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    String location = resultSet.getString("location");
                    Boolean enabled = resultSet.getBoolean("enabled");
                    Timestamp dateCreated = resultSet.getTimestamp("dateCreated");
                    String inventoryContents = resultSet.getString("inventoryContents");

                    inventory = new Inventory(id, uuid, username, location, enabled, dateCreated, inventoryContents);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventory;
    }

    public Inventory getInventory(String uuid, Timestamp dateCreated) {
        // SQL query to select the newest Inventory record for the given UUID and dateCreated based on YYYY-MM-DD HH:MM:SS not including milliseconds
        String query = "SELECT * FROM inventories WHERE uuid = ? AND dateCreated = ?";
        Inventory inventory = null;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid);
            statement.setTimestamp(2, dateCreated);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    String location = resultSet.getString("location");
                    Boolean enabled = resultSet.getBoolean("enabled");
                    String inventoryContents = resultSet.getString("inventoryContents");

                    inventory = new Inventory(id, uuid, username, location, enabled, dateCreated, inventoryContents);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventory;
    }

    public Inventory insertInventory(Inventory inventory) {
        String query = "INSERT INTO inventories (uuid, username, location, enabled, dateCreated, inventoryContents) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, inventory.getUuid());
            statement.setString(2, inventory.getUsername());
            statement.setString(3, inventory.getLocation());
            statement.setBoolean(4, inventory.getEnabled());
            statement.setTimestamp(5, inventory.getDateCreated());
            statement.setString(6, inventory.getInventoryContents());

            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    inventory.setId(id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventory;
    }

    public Inventory updateInventory(Inventory inventory) {
        String query = "UPDATE inventories SET username = ?, location = ?, enabled = ?, dateCreated = ?, inventoryContents = ? WHERE uuid = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, inventory.getUsername());
            statement.setString(2, inventory.getLocation());
            statement.setBoolean(3, inventory.getEnabled());
            statement.setTimestamp(4, inventory.getDateCreated());
            statement.setString(5, inventory.getInventoryContents());
            statement.setString(6, inventory.getUuid());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventory;
    }

    public List<Inventory> deleteInventories(String uuid) {
        String query = "DELETE FROM inventories WHERE uuid = ?";
        List<Inventory> inventories = getInventories(uuid);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventories;
    }

    public Inventory deleteInventory(String uuid) {
        String query = "DELETE FROM inventories WHERE uuid = ?";
        Inventory inventory = getInventory(uuid);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventory;
    }

    public List<Log> getLogs() {
        String query = "SELECT * FROM logs";
        List<Log> logs = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String uuid = resultSet.getString("uuid");
                String username = resultSet.getString("username");
                Timestamp dateCreated = resultSet.getTimestamp("dateCreated");
                boolean enabled = resultSet.getBoolean("enabled");
                String message = resultSet.getString("message");

                Log log = new Log(id, uuid, username, dateCreated, enabled, message);
                logs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }

    public List<Log> getLogs(int pageSize, int page) {
        String query = "SELECT * FROM logs ORDER BY dateCreated DESC LIMIT ? OFFSET ?";
        List<Log> logs = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, pageSize);
            int actualPageSize = page > 0 ? page * pageSize : 0;
            statement.setInt(2, actualPageSize);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String uuid = resultSet.getString("uuid");
                    String username = resultSet.getString("username");
                    Timestamp dateCreated = resultSet.getTimestamp("dateCreated");
                    boolean enabled = resultSet.getBoolean("enabled");
                    String message = resultSet.getString("message");

                    Log log = new Log(id, uuid, username, dateCreated, enabled, message);
                    logs.add(log);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }

    public List<Log> getLogs(String uuid) {
        String query = "SELECT * FROM logs WHERE uuid = ?";
        List<Log> logs = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    Timestamp dateCreated = resultSet.getTimestamp("dateCreated");
                    boolean enabled = resultSet.getBoolean("enabled");
                    String message = resultSet.getString("message");

                    Log log = new Log(id, uuid, username, dateCreated, enabled, message);
                    logs.add(log);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }

    public Log getLog(int id) {
        String query = "SELECT * FROM logs WHERE id = ?";
        Log log = null;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String uuid = resultSet.getString("uuid");
                    String username = resultSet.getString("username");
                    Timestamp dateCreated = resultSet.getTimestamp("dateCreated");
                    boolean enabled = resultSet.getBoolean("enabled");
                    String message = resultSet.getString("message");

                    log = new Log(id, uuid, username, dateCreated, enabled, message);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return log;
    }

    public Log insertLog(Log log) {
        String query = "INSERT INTO logs (uuid, username, dateCreated, enabled, message) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, log.getUuid());
            statement.setString(2, log.getUsername());
            statement.setTimestamp(3, log.getDateCreated());
            statement.setBoolean(4, log.getEnabled());
            statement.setString(5, log.getMessage());

            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    log.setId(id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return log;
    }

    public Log updateLog(Log log) {
        String query = "UPDATE logs SET username = ?, dateCreated = ?, enabled = ?, message = ? WHERE uuid = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, log.getUsername());
            statement.setTimestamp(2, log.getDateCreated());
            statement.setBoolean(3, log.getEnabled());
            statement.setString(4, log.getMessage());
            statement.setString(5, log.getUuid());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return log;
    }

    public List<Log> deleteLogs(String uuid) {
        String query = "DELETE FROM logs WHERE uuid = ?";
        List<Log> logs = getLogs(uuid);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }

    public Log deleteLog(int id) {
        String query = "DELETE FROM logs WHERE id = ?";
        Log log = getLog(id);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return log;
    }
}

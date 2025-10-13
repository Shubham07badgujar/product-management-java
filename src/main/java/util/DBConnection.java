package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static final String PROPERTIES_FILE = "database.properties";
    private static String url;
    private static String username;
    private static String password;
    private static String driver;
    private static boolean initialized = false;

    private DBConnection() {
    }

    private static void initialize() {
        if (initialized) {
            return;
        }

        Properties properties = new Properties();
        try (InputStream inputStream = DBConnection.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {

            if (inputStream == null) {
                throw new RuntimeException("Unable to find " + PROPERTIES_FILE);
            }

            properties.load(inputStream);

            driver = properties.getProperty("db.driver");
            url = properties.getProperty("db.url");
            username = properties.getProperty("db.username");
            password = properties.getProperty("db.password");

            Class.forName(driver);

            initialized = true;
            System.out.println("✅ Database configuration loaded successfully");

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load database configuration: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        initialize();

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            System.err.println("❌ Failed to create database connection: " + e.getMessage());
            throw e;
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("⚠️  Error closing database connection: " + e.getMessage());
            }
        }
    }

    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            if (connection != null && !connection.isClosed()) {
                System.out.println("✅ Database connection test successful!");
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    public static void commitTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.commit();
            } catch (SQLException e) {
                System.err.println("⚠️  Error committing transaction: " + e.getMessage());
            }
        }
    }

    public static void rollbackTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                System.err.println("⚠️  Error rolling back transaction: " + e.getMessage());
            }
        }
    }
}
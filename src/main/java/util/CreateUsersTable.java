package util;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Utility to create the users table in the database
 */
public class CreateUsersTable {
    public static void main(String[] args) {
        System.out.println("=== Creating Users Table ===\n");

        Connection connection = null;
        Statement statement = null;

        try {
            System.out.println("Connecting to database...");
            connection = DBConnection.getConnection();
            statement = connection.createStatement();

            // Drop existing table to recreate with password column
            System.out.println("Dropping existing users table if exists...");
            statement.executeUpdate("DROP TABLE IF EXISTS users");

            // Create users table with password column
            String createTableSQL = """
                    CREATE TABLE users (
                        id INT PRIMARY KEY,
                        username VARCHAR(50) NOT NULL UNIQUE,
                        email VARCHAR(100) NOT NULL UNIQUE,
                        first_name VARCHAR(50) NOT NULL,
                        last_name VARCHAR(50) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        phone_number VARCHAR(20),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                    )
                    """;

            System.out.println("Creating users table...");
            statement.executeUpdate(createTableSQL);
            System.out.println("✅ Users table created successfully!");

            // Insert sample data with passwords
            System.out.println("Inserting sample users with passwords...");
            String insertSQL = """
                    INSERT INTO users (id, username, email, first_name, last_name, password, phone_number) VALUES
                    (1, 'john_doe', 'john.doe@email.com', 'John', 'Doe', 'password123', '123-456-7890'),
                    (2, 'jane_smith', 'jane.smith@email.com', 'Jane', 'Smith', 'secure456', '987-654-3210'),
                    (3, 'bob_johnson', 'bob.johnson@email.com', 'Bob', 'Johnson', 'bobpass789', NULL),
                    (4, 'alice_brown', 'alice.brown@email.com', 'Alice', 'Brown', 'alice2024', '555-123-4567')
                    """;

            int insertedRows = statement.executeUpdate(insertSQL);
            System.out.println("✅ Inserted " + insertedRows + " sample users!");

            // Commit changes
            DBConnection.commitTransaction(connection);

            System.out.println("\n=== Users Table Setup Complete ===");
            System.out.println("You can now run UserDatabaseDemo to test the functionality!");

        } catch (Exception e) {
            System.err.println("❌ Error creating users table: " + e.getMessage());
            if (connection != null) {
                DBConnection.rollbackTransaction(connection);
            }
        } finally {
            try {
                if (statement != null)
                    statement.close();
                DBConnection.closeConnection(connection);
            } catch (Exception e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
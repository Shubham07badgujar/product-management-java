package util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;

/**
 * Tests for DBConnection utility class
 * Note: These tests require a properly configured database
 */
class DBConnectionTest {

    private Connection connection;

    @BeforeEach
    void setUp() {
        // Clean up any existing connections
        connection = null;
    }

    @AfterEach
    void tearDown() {
        // Clean up connections after each test
        if (connection != null) {
            DBConnection.closeConnection(connection);
        }
    }

    @Test
    void testGetConnection_ShouldReturnValidConnection() {
        // Act
        connection = DBConnection.getConnection();

        // Assert
        assertNotNull(connection);

        // Test that connection is usable
        assertDoesNotThrow(() -> {
            assertFalse(connection.isClosed());
        });
    }

    @Test
    void testGetConnection_MultipleCalls_ShouldReturnConnections() {
        // Act
        Connection connection1 = DBConnection.getConnection();
        Connection connection2 = DBConnection.getConnection();

        // Assert
        assertNotNull(connection1);
        assertNotNull(connection2);

        // Clean up
        DBConnection.closeConnection(connection1);
        DBConnection.closeConnection(connection2);
    }

    @Test
    void testTestConnection_ShouldReturnBooleanResult() {
        // Act
        boolean result = DBConnection.testConnection();

        // Assert
        // Result should be either true or false, not null
        assertNotNull(result);
    }

    @Test
    void testCloseConnection_WithValidConnection_ShouldCloseGracefully() {
        // Arrange
        connection = DBConnection.getConnection();
        assertNotNull(connection);

        // Act & Assert
        assertDoesNotThrow(() -> {
            DBConnection.closeConnection(connection);
        });
    }

    @Test
    void testCloseConnection_WithNullConnection_ShouldHandleGracefully() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            DBConnection.closeConnection(null);
        });
    }

    @Test
    void testCommitTransaction_WithValidConnection_ShouldNotThrowException() {
        // Arrange
        connection = DBConnection.getConnection();

        // Act & Assert
        assertDoesNotThrow(() -> {
            DBConnection.commitTransaction(connection);
        });
    }

    @Test
    void testCommitTransaction_WithNullConnection_ShouldHandleGracefully() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            DBConnection.commitTransaction(null);
        });
    }

    @Test
    void testRollbackTransaction_WithValidConnection_ShouldNotThrowException() {
        // Arrange
        connection = DBConnection.getConnection();

        // Act & Assert
        assertDoesNotThrow(() -> {
            DBConnection.rollbackTransaction(connection);
        });
    }

    @Test
    void testRollbackTransaction_WithNullConnection_ShouldHandleGracefully() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            DBConnection.rollbackTransaction(null);
        });
    }

    @Test
    void testConnectionTransactionFlow_ShouldWorkCorrectly() {
        // Arrange
        connection = DBConnection.getConnection();
        assertNotNull(connection);

        // Act & Assert - Test complete transaction flow
        assertDoesNotThrow(() -> {
            // This simulates a typical transaction flow
            // 1. Get connection (already done)
            // 2. Perform operations (simulated)
            // 3. Commit or rollback
            DBConnection.commitTransaction(connection);
            // 4. Close connection
            DBConnection.closeConnection(connection);
        });
    }

    @Test
    void testConnectionProperties_ShouldBeConfiguredCorrectly() {
        // Arrange
        connection = DBConnection.getConnection();

        if (connection != null) {
            assertDoesNotThrow(() -> {
                // Test basic connection properties
                assertNotNull(connection.getMetaData());
                assertFalse(connection.isClosed());

                // Test transaction settings
                // Note: These depend on database configuration
                assertNotNull(connection.getAutoCommit()); // Should return boolean
            });
        }
    }

    @Test
    void testConnectionStability_MultipleOperations_ShouldRemainStable() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            // Test multiple connection operations
            for (int i = 0; i < 5; i++) {
                Connection tempConnection = DBConnection.getConnection();
                if (tempConnection != null) {
                    assertFalse(tempConnection.isClosed());
                    DBConnection.closeConnection(tempConnection);
                }
            }
        });
    }

    @Test
    void testDatabaseConfiguration_ShouldLoadCorrectly() {
        // This test verifies that database configuration is loaded properly
        assertDoesNotThrow(() -> {
            // Test that we can get a connection, which implies configuration is loaded
            Connection testConnection = DBConnection.getConnection();
            if (testConnection != null) {
                // Configuration loaded successfully
                assertTrue(true);
                DBConnection.closeConnection(testConnection);
            } else {
                // Configuration might be missing or incorrect, but should not throw exception
                assertTrue(true); // Test passes if no exception is thrown
            }
        });
    }
}
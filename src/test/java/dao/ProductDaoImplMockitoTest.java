package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import model.Product;
import util.DBConnection;

@ExtendWith(MockitoExtension.class)
class ProductDaoImplMockitoTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private ProductDaoImpl productDao;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        productDao = new ProductDaoImpl();
        testProduct = new Product(1, "Test Product", 99.99, 10, "Electronics");
    }

    @Test
    void testCreate_Success() throws SQLException {
        // Arrange
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            dbConnectionMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            // Act
            boolean result = productDao.create(testProduct);

            // Assert
            assertTrue(result);
            verify(mockPreparedStatement).setInt(1, testProduct.getId());
            verify(mockPreparedStatement).setString(2, testProduct.getName());
            verify(mockPreparedStatement).setDouble(3, testProduct.getPrice());
            verify(mockPreparedStatement).setInt(4, testProduct.getQuantity());
            verify(mockPreparedStatement).setString(5, testProduct.getCategory());
            verify(mockPreparedStatement).executeUpdate();
            dbConnectionMock.verify(() -> DBConnection.commitTransaction(mockConnection));
        }
    }

    @Test
    void testCreate_Failure() throws SQLException {
        // Arrange
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            dbConnectionMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(0);

            // Act
            boolean result = productDao.create(testProduct);

            // Assert
            assertFalse(result);
            dbConnectionMock.verify(() -> DBConnection.rollbackTransaction(mockConnection));
        }
    }

    @Test
    void testCreate_NullProduct() {
        // Act
        boolean result = productDao.create(null);

        // Assert
        assertFalse(result);
    }

    @Test
    void testCreate_SQLException() throws SQLException {
        // Arrange
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            dbConnectionMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Database error"));

            // Act
            boolean result = productDao.create(testProduct);

            // Assert
            assertFalse(result);
            dbConnectionMock.verify(() -> DBConnection.rollbackTransaction(mockConnection));
        }
    }

    @Test
    void testFindById_Success() throws SQLException {
        // Arrange
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            dbConnectionMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt("id")).thenReturn(testProduct.getId());
            when(mockResultSet.getString("name")).thenReturn(testProduct.getName());
            when(mockResultSet.getDouble("price")).thenReturn(testProduct.getPrice());
            when(mockResultSet.getInt("quantity")).thenReturn(testProduct.getQuantity());
            when(mockResultSet.getString("category")).thenReturn(testProduct.getCategory());

            // Act
            Product result = productDao.findById(1);

            // Assert
            assertNotNull(result);
            assertEquals(testProduct.getId(), result.getId());
            assertEquals(testProduct.getName(), result.getName());
            assertEquals(testProduct.getPrice(), result.getPrice());
            assertEquals(testProduct.getQuantity(), result.getQuantity());
            assertEquals(testProduct.getCategory(), result.getCategory());
            verify(mockPreparedStatement).setInt(1, 1);
        }
    }

    @Test
    void testFindById_NotFound() throws SQLException {
        // Arrange
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            dbConnectionMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            // Act
            Product result = productDao.findById(999);

            // Assert
            assertNull(result);
        }
    }

    @Test
    void testFindById_SQLException() throws SQLException {
        // Arrange
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            dbConnectionMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));

            // Act
            Product result = productDao.findById(1);

            // Assert
            assertNull(result);
        }
    }

    @Test
    void testFindAll_Success() throws SQLException {
        // Arrange
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            dbConnectionMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true, true, false);
            when(mockResultSet.getInt("id")).thenReturn(1, 2);
            when(mockResultSet.getString("name")).thenReturn("Product 1", "Product 2");
            when(mockResultSet.getDouble("price")).thenReturn(99.99, 199.99);
            when(mockResultSet.getInt("quantity")).thenReturn(10, 20);
            when(mockResultSet.getString("category")).thenReturn("Electronics", "Clothing");

            // Act
            List<Product> result = productDao.findAll();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Product 1", result.get(0).getName());
            assertEquals("Product 2", result.get(1).getName());
        }
    }

    @Test
    void testFindAll_EmptyResult() throws SQLException {
        // Arrange
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            dbConnectionMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            // Act
            List<Product> result = productDao.findAll();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testUpdate_Success() throws SQLException {
        // Arrange
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            dbConnectionMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            // Act
            boolean result = productDao.update(testProduct);

            // Assert
            assertTrue(result);
            verify(mockPreparedStatement).setString(1, testProduct.getName());
            verify(mockPreparedStatement).setDouble(2, testProduct.getPrice());
            verify(mockPreparedStatement).setInt(3, testProduct.getQuantity());
            verify(mockPreparedStatement).setString(4, testProduct.getCategory());
            verify(mockPreparedStatement).setInt(5, testProduct.getId());
            dbConnectionMock.verify(() -> DBConnection.commitTransaction(mockConnection));
        }
    }

    @Test
    void testUpdate_NullProduct() {
        // Act
        boolean result = productDao.update(null);

        // Assert
        assertFalse(result);
    }

    @Test
    void testUpdateQuantity_Success() throws SQLException {
        // Arrange
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            dbConnectionMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            // Act
            boolean result = productDao.updateQuantity(1, 25);

            // Assert
            assertTrue(result);
            verify(mockPreparedStatement).setInt(1, 25);
            verify(mockPreparedStatement).setInt(2, 1);
            dbConnectionMock.verify(() -> DBConnection.commitTransaction(mockConnection));
        }
    }

    @Test
    void testUpdateQuantity_NegativeQuantity() {
        // Act
        boolean result = productDao.updateQuantity(1, -5);

        // Assert
        assertFalse(result);
    }

    @Test
    void testDeleteById_Success() throws SQLException {
        // Arrange
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            dbConnectionMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            // Act
            boolean result = productDao.deleteById(1);

            // Assert
            assertTrue(result);
            verify(mockPreparedStatement).setInt(1, 1);
            dbConnectionMock.verify(() -> DBConnection.commitTransaction(mockConnection));
        }
    }

    @Test
    void testDeleteById_NotFound() throws SQLException {
        // Arrange
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            dbConnectionMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(0);

            // Act
            boolean result = productDao.deleteById(999);

            // Assert
            assertFalse(result);
            dbConnectionMock.verify(() -> DBConnection.rollbackTransaction(mockConnection));
        }
    }

    @Test
    void testExistsById_True() throws SQLException {
        // Arrange
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            dbConnectionMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);

            // Act
            boolean result = productDao.existsById(1);

            // Assert
            assertTrue(result);
            verify(mockPreparedStatement).setInt(1, 1);
        }
    }

    @Test
    void testExistsById_False() throws SQLException {
        // Arrange
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            dbConnectionMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            // Act
            boolean result = productDao.existsById(999);

            // Assert
            assertFalse(result);
        }
    }

    @Test
    void testGetTotalCount_Success() throws SQLException {
        // Arrange
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            dbConnectionMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt(1)).thenReturn(5);

            // Act
            int result = productDao.getTotalCount();

            // Assert
            assertEquals(5, result);
        }
    }

    @Test
    void testGetTotalCount_NoResult() throws SQLException {
        // Arrange
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            dbConnectionMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            // Act
            int result = productDao.getTotalCount();

            // Assert
            assertEquals(0, result);
        }
    }

    @Test
    void testFindByName_Success() throws SQLException {
        // Arrange
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            dbConnectionMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true, false);
            when(mockResultSet.getInt("id")).thenReturn(1);
            when(mockResultSet.getString("name")).thenReturn("Test Product");
            when(mockResultSet.getDouble("price")).thenReturn(99.99);
            when(mockResultSet.getInt("quantity")).thenReturn(10);

            // Act
            List<Product> result = productDao.findByName("Test");

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Test Product", result.get(0).getName());
            verify(mockPreparedStatement).setString(1, "%Test%");
        }
    }

    @Test
    void testFindByCategory_Success() throws SQLException {
        // Arrange
        try (MockedStatic<DBConnection> dbConnectionMock = mockStatic(DBConnection.class)) {
            dbConnectionMock.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true, false);
            when(mockResultSet.getInt("id")).thenReturn(1);
            when(mockResultSet.getString("name")).thenReturn("Test Product");
            when(mockResultSet.getDouble("price")).thenReturn(99.99);
            when(mockResultSet.getInt("quantity")).thenReturn(10);
            when(mockResultSet.getString("category")).thenReturn("Electronics");

            // Act
            List<Product> result = productDao.findByCategory("Electronics");

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Electronics", result.get(0).getCategory());
            verify(mockPreparedStatement).setString(1, "%Electronics%");
        }
    }
}
package dao;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import model.Product;

/**
 * Integration tests for ProductDaoImpl
 * Note: These tests require a real database connection and should be run with a
 * test database
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductDaoImplTest {

    private static ProductDaoImpl productDao;
    private static Product testProduct1;
    private static Product testProduct2;
    private static Product testProduct3;

    @BeforeAll
    static void setUpClass() {
        productDao = new ProductDaoImpl();
        testProduct1 = new Product(1001, "Test Laptop", 999.99, 5, "Electronics");
        testProduct2 = new Product(1002, "Test Phone", 599.99, 10, "Electronics");
        testProduct3 = new Product(1003, "Test Shirt", 29.99, 20, "Clothing");
    }

    @BeforeEach
    void setUp() {
        // Clean up any existing test data
        productDao.deleteById(1001);
        productDao.deleteById(1002);
        productDao.deleteById(1003);
    }

    @AfterEach
    void tearDown() {
        // Clean up test data after each test
        productDao.deleteById(1001);
        productDao.deleteById(1002);
        productDao.deleteById(1003);
    }

    @Test
    @Order(1)
    void testCreate_ValidProduct_ShouldReturnTrue() {
        // Act
        boolean result = productDao.create(testProduct1);

        // Assert
        assertTrue(result);
        assertTrue(productDao.existsById(testProduct1.getId()));
    }

    @Test
    @Order(2)
    void testCreate_NullProduct_ShouldReturnFalse() {
        // Act
        boolean result = productDao.create(null);

        // Assert
        assertFalse(result);
    }

    @Test
    @Order(3)
    void testCreate_DuplicateId_ShouldHandleGracefully() {
        // Arrange
        productDao.create(testProduct1);

        // Act
        boolean result = productDao.create(testProduct1);

        // Assert
        // Depending on database constraints, this might return false or throw exception
        // The test verifies the method handles duplicates appropriately
        assertNotNull(result); // Just ensure no exception is thrown
    }

    @Test
    @Order(4)
    void testFindById_ExistingProduct_ShouldReturnProduct() {
        // Arrange
        productDao.create(testProduct1);

        // Act
        Product result = productDao.findById(testProduct1.getId());

        // Assert
        assertNotNull(result);
        assertEquals(testProduct1.getId(), result.getId());
        assertEquals(testProduct1.getName(), result.getName());
        assertEquals(testProduct1.getPrice(), result.getPrice(), 0.01);
        assertEquals(testProduct1.getQuantity(), result.getQuantity());
        assertEquals(testProduct1.getCategory(), result.getCategory());
    }

    @Test
    @Order(5)
    void testFindById_NonExistingProduct_ShouldReturnNull() {
        // Act
        Product result = productDao.findById(9999);

        // Assert
        assertNull(result);
    }

    @Test
    @Order(6)
    void testFindAll_WithMultipleProducts_ShouldReturnAllProducts() {
        // Arrange
        productDao.create(testProduct1);
        productDao.create(testProduct2);
        productDao.create(testProduct3);

        // Act
        List<Product> result = productDao.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.size() >= 3); // At least our test products

        // Check if our test products are in the result
        boolean foundProduct1 = result.stream().anyMatch(p -> p.getId() == testProduct1.getId());
        boolean foundProduct2 = result.stream().anyMatch(p -> p.getId() == testProduct2.getId());
        boolean foundProduct3 = result.stream().anyMatch(p -> p.getId() == testProduct3.getId());

        assertTrue(foundProduct1);
        assertTrue(foundProduct2);
        assertTrue(foundProduct3);
    }

    @Test
    @Order(7)
    void testUpdate_ExistingProduct_ShouldReturnTrue() {
        // Arrange
        productDao.create(testProduct1);
        Product updatedProduct = new Product(testProduct1.getId(), "Updated Laptop", 1199.99, 3, "Electronics");

        // Act
        boolean result = productDao.update(updatedProduct);

        // Assert
        assertTrue(result);

        Product retrievedProduct = productDao.findById(testProduct1.getId());
        assertNotNull(retrievedProduct);
        assertEquals("Updated Laptop", retrievedProduct.getName());
        assertEquals(1199.99, retrievedProduct.getPrice(), 0.01);
        assertEquals(3, retrievedProduct.getQuantity());
    }

    @Test
    @Order(8)
    void testUpdate_NonExistingProduct_ShouldReturnFalse() {
        // Arrange
        Product nonExistingProduct = new Product(9999, "Non-existing Product", 99.99, 1, "Test");

        // Act
        boolean result = productDao.update(nonExistingProduct);

        // Assert
        assertFalse(result);
    }

    @Test
    @Order(9)
    void testUpdate_NullProduct_ShouldReturnFalse() {
        // Act
        boolean result = productDao.update(null);

        // Assert
        assertFalse(result);
    }

    @Test
    @Order(10)
    void testUpdateQuantity_ExistingProduct_ShouldReturnTrue() {
        // Arrange
        productDao.create(testProduct1);
        int newQuantity = 15;

        // Act
        boolean result = productDao.updateQuantity(testProduct1.getId(), newQuantity);

        // Assert
        assertTrue(result);

        Product retrievedProduct = productDao.findById(testProduct1.getId());
        assertNotNull(retrievedProduct);
        assertEquals(newQuantity, retrievedProduct.getQuantity());
    }

    @Test
    @Order(11)
    void testUpdateQuantity_NegativeQuantity_ShouldReturnFalse() {
        // Arrange
        productDao.create(testProduct1);

        // Act
        boolean result = productDao.updateQuantity(testProduct1.getId(), -5);

        // Assert
        assertFalse(result);
    }

    @Test
    @Order(12)
    void testUpdateQuantity_NonExistingProduct_ShouldReturnFalse() {
        // Act
        boolean result = productDao.updateQuantity(9999, 10);

        // Assert
        assertFalse(result);
    }

    @Test
    @Order(13)
    void testDeleteById_ExistingProduct_ShouldReturnTrue() {
        // Arrange
        productDao.create(testProduct1);
        assertTrue(productDao.existsById(testProduct1.getId()));

        // Act
        boolean result = productDao.deleteById(testProduct1.getId());

        // Assert
        assertTrue(result);
        assertFalse(productDao.existsById(testProduct1.getId()));
    }

    @Test
    @Order(14)
    void testDeleteById_NonExistingProduct_ShouldReturnFalse() {
        // Act
        boolean result = productDao.deleteById(9999);

        // Assert
        assertFalse(result);
    }

    @Test
    @Order(15)
    void testExistsById_ExistingProduct_ShouldReturnTrue() {
        // Arrange
        productDao.create(testProduct1);

        // Act
        boolean result = productDao.existsById(testProduct1.getId());

        // Assert
        assertTrue(result);
    }

    @Test
    @Order(16)
    void testExistsById_NonExistingProduct_ShouldReturnFalse() {
        // Act
        boolean result = productDao.existsById(9999);

        // Assert
        assertFalse(result);
    }

    @Test
    @Order(17)
    void testGetTotalCount_WithProducts_ShouldReturnCorrectCount() {
        // Arrange
        int initialCount = productDao.getTotalCount();
        productDao.create(testProduct1);
        productDao.create(testProduct2);

        // Act
        int newCount = productDao.getTotalCount();

        // Assert
        assertEquals(initialCount + 2, newCount);
    }

    @Test
    @Order(18)
    void testFindByName_ExistingProducts_ShouldReturnMatchingProducts() {
        // Arrange
        productDao.create(testProduct1); // "Test Laptop"
        productDao.create(testProduct2); // "Test Phone"
        productDao.create(testProduct3); // "Test Shirt"

        // Act
        List<Product> result = productDao.findByName("Test");

        // Assert
        assertNotNull(result);
        assertTrue(result.size() >= 3);

        // All should contain "Test" in the name
        for (Product product : result) {
            if (product.getId() == testProduct1.getId() ||
                    product.getId() == testProduct2.getId() ||
                    product.getId() == testProduct3.getId()) {
                assertTrue(product.getName().toLowerCase().contains("test"));
            }
        }
    }

    @Test
    @Order(19)
    void testFindByName_NonExistingName_ShouldReturnEmptyList() {
        // Act
        List<Product> result = productDao.findByName("NonExistingProductName12345");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @Order(20)
    void testFindByCategory_ExistingProducts_ShouldReturnMatchingProducts() {
        // Arrange
        productDao.create(testProduct1); // Electronics
        productDao.create(testProduct2); // Electronics
        productDao.create(testProduct3); // Clothing

        // Act
        List<Product> electronicsProducts = productDao.findByCategory("Electronics");
        List<Product> clothingProducts = productDao.findByCategory("Clothing");

        // Assert
        assertNotNull(electronicsProducts);
        assertNotNull(clothingProducts);

        assertTrue(electronicsProducts.size() >= 2);
        assertTrue(clothingProducts.size() >= 1);

        // Check if our test products are in the correct categories
        boolean foundElectronicsProduct1 = electronicsProducts.stream()
                .anyMatch(p -> p.getId() == testProduct1.getId());
        boolean foundElectronicsProduct2 = electronicsProducts.stream()
                .anyMatch(p -> p.getId() == testProduct2.getId());
        boolean foundClothingProduct = clothingProducts.stream().anyMatch(p -> p.getId() == testProduct3.getId());

        assertTrue(foundElectronicsProduct1);
        assertTrue(foundElectronicsProduct2);
        assertTrue(foundClothingProduct);
    }

    @Test
    @Order(21)
    void testFindByCategory_NonExistingCategory_ShouldReturnEmptyList() {
        // Act
        List<Product> result = productDao.findByCategory("NonExistingCategory12345");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @Order(22)
    void testCRUDOperationsFlow_CompleteWorkflow() {
        // Create
        assertTrue(productDao.create(testProduct1));

        // Read
        Product retrieved = productDao.findById(testProduct1.getId());
        assertNotNull(retrieved);
        assertEquals(testProduct1.getName(), retrieved.getName());

        // Update
        Product updated = new Product(testProduct1.getId(), "Updated Name", 1500.0, 8, "Updated Category");
        assertTrue(productDao.update(updated));

        Product retrievedUpdated = productDao.findById(testProduct1.getId());
        assertEquals("Updated Name", retrievedUpdated.getName());
        assertEquals(1500.0, retrievedUpdated.getPrice(), 0.01);
        assertEquals("Updated Category", retrievedUpdated.getCategory());

        // Delete
        assertTrue(productDao.deleteById(testProduct1.getId()));
        assertNull(productDao.findById(testProduct1.getId()));
    }
}
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
import org.junit.jupiter.api.Test;

import model.Product;

/**
 * Essential Integration Tests for ProductDaoImpl
 * Covers critical CRUD operations and search functionality
 * Note: Requires a real database connection
 */
class ProductDaoImplTest {

    private static ProductDaoImpl productDao;
    private static Product testProduct1;
    private static Product testProduct2;

    @BeforeAll
    static void setUpClass() {
        productDao = new ProductDaoImpl();
        testProduct1 = new Product(1001, "Test Laptop", 999.99, 5, "Electronics");
        testProduct2 = new Product(1002, "Test Phone", 599.99, 10, "Electronics");
    }

    @BeforeEach
    void setUp() {
        // Clean up any existing test data
        productDao.deleteById(1001);
        productDao.deleteById(1002);
    }

    @AfterEach
    void tearDown() {
        // Clean up test data after each test
        productDao.deleteById(1001);
        productDao.deleteById(1002);
    }

    /**
     * Test Case 1: Create - Valid Product
     * Tests successful product creation
     */
    @Test
    void testCreate_ValidProduct_ShouldReturnTrue() {
        // Act
        boolean result = productDao.create(testProduct1);

        // Assert
        assertTrue(result, "Should successfully create a valid product");
        assertTrue(productDao.existsById(testProduct1.getId()), "Product should exist in database");
    }

    /**
     * Test Case 2: Create - Null Product
     * Tests null handling in create operation
     */
    @Test
    void testCreate_NullProduct_ShouldReturnFalse() {
        // Act
        boolean result = productDao.create(null);

        // Assert
        assertFalse(result, "Should return false for null product");
    }

    /**
     * Test Case 3: Read - Find By ID (Existing)
     * Tests retrieval of existing product
     */
    @Test
    void testFindById_ExistingProduct_ShouldReturnProduct() {
        // Arrange
        productDao.create(testProduct1);

        // Act
        Product result = productDao.findById(testProduct1.getId());

        // Assert
        assertNotNull(result, "Should return product object");
        assertEquals(testProduct1.getId(), result.getId(), "ID should match");
        assertEquals(testProduct1.getName(), result.getName(), "Name should match");
        assertEquals(testProduct1.getPrice(), result.getPrice(), 0.01, "Price should match");
    }

    /**
     * Test Case 4: Read - Find By ID (Non-Existing)
     * Tests behavior when product doesn't exist
     */
    @Test
    void testFindById_NonExistingProduct_ShouldReturnNull() {
        // Act
        Product result = productDao.findById(9999);

        // Assert
        assertNull(result, "Should return null for non-existing product");
    }

    /**
     * Test Case 5: Read - Find All Products
     * Tests retrieval of all products
     */
    @Test
    void testFindAll_ShouldReturnAllProducts() {
        // Arrange
        productDao.create(testProduct1);
        productDao.create(testProduct2);

        // Act
        List<Product> result = productDao.findAll();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.size() >= 2, "Should return at least 2 products");
    }

    /**
     * Test Case 6: Update - Existing Product
     * Tests successful product update
     */
    @Test
    void testUpdate_ExistingProduct_ShouldReturnTrue() {
        // Arrange
        productDao.create(testProduct1);
        Product updatedProduct = new Product(testProduct1.getId(), "Updated Laptop", 1199.99, 3, "Electronics");

        // Act
        boolean result = productDao.update(updatedProduct);

        // Assert
        assertTrue(result, "Update should succeed");
        Product retrieved = productDao.findById(testProduct1.getId());
        assertEquals("Updated Laptop", retrieved.getName(), "Name should be updated");
        assertEquals(1199.99, retrieved.getPrice(), 0.01, "Price should be updated");
    }

    /**
     * Test Case 7: Update - Non-Existing Product
     * Tests update behavior for non-existing product
     */
    @Test
    void testUpdate_NonExistingProduct_ShouldReturnFalse() {
        // Arrange
        Product nonExistingProduct = new Product(9999, "Non-existing Product", 99.99, 1, "Test");

        // Act
        boolean result = productDao.update(nonExistingProduct);

        // Assert
        assertFalse(result, "Should return false for non-existing product");
    }

    /**
     * Test Case 8: Delete - Existing Product
     * Tests successful product deletion
     */
    @Test
    void testDeleteById_ExistingProduct_ShouldReturnTrue() {
        // Arrange
        productDao.create(testProduct1);
        assertTrue(productDao.existsById(testProduct1.getId()), "Product should exist before deletion");

        // Act
        boolean result = productDao.deleteById(testProduct1.getId());

        // Assert
        assertTrue(result, "Deletion should succeed");
        assertFalse(productDao.existsById(testProduct1.getId()), "Product should not exist after deletion");
    }

    /**
     * Test Case 9: Search - Find By Name
     * Tests search functionality by product name
     */
    @Test
    void testFindByName_ShouldReturnMatchingProducts() {
        // Arrange
        productDao.create(testProduct1); // "Test Laptop"
        productDao.create(testProduct2); // "Test Phone"

        // Act
        List<Product> result = productDao.findByName("Laptop");

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.size() >= 1, "Should find at least one matching product");
        assertTrue(result.stream().anyMatch(p -> p.getName().contains("Laptop")),
                "Should contain product with 'Laptop' in name");
    }

    /**
     * Test Case 10: Search - Find By Category
     * Tests search functionality by category
     */
    @Test
    void testFindByCategory_ShouldReturnMatchingProducts() {
        // Arrange
        productDao.create(testProduct1); // Electronics
        productDao.create(testProduct2); // Electronics

        // Act
        List<Product> result = productDao.findByCategory("Electronics");

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.size() >= 2, "Should find at least 2 electronics products");
    }

    /**
     * Test Case 11: Complete CRUD Workflow
     * Tests the entire lifecycle of a product (Create → Read → Update → Delete)
     */
    @Test
    void testCRUDWorkflow_CompleteCycle() {
        // CREATE
        assertTrue(productDao.create(testProduct1), "Product creation should succeed");

        // READ
        Product retrieved = productDao.findById(testProduct1.getId());
        assertNotNull(retrieved, "Should retrieve created product");
        assertEquals(testProduct1.getName(), retrieved.getName(), "Retrieved product should match created product");

        // UPDATE
        Product updated = new Product(testProduct1.getId(), "Modified Laptop", 1500.0, 8, "Tech");
        assertTrue(productDao.update(updated), "Product update should succeed");
        Product retrievedUpdated = productDao.findById(testProduct1.getId());
        assertEquals("Modified Laptop", retrievedUpdated.getName(), "Product should reflect updates");

        // DELETE
        assertTrue(productDao.deleteById(testProduct1.getId()), "Product deletion should succeed");
        assertNull(productDao.findById(testProduct1.getId()), "Deleted product should not be found");
    }
}
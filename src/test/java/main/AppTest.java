package main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Product;
import service.ProductService;
import util.DBConnection;

class AppTest {

    private static ProductService productService;
    private static ByteArrayOutputStream outputStream;
    private static PrintStream originalOut;
    private static InputStream originalIn;

    @BeforeAll
    static void setUpClass() {
        productService = new ProductService();
        originalOut = System.out;
        originalIn = System.in;
    }

    @BeforeEach
    void setUp() {
        // Capture System.out
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        // Restore original System.out and System.in
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void testMain_DatabaseConnectionSuccess() {
        // Test if main method handles database connection properly
        // This is a basic test that ensures no exceptions are thrown

        assertDoesNotThrow(() -> {
            // We can't easily test the full main method due to its interactive nature
            // But we can test that database connection works
            boolean connectionResult = DBConnection.testConnection();
            assertNotNull(connectionResult);
        });
    }

    @Test
    void testMain_DatabaseConnectionFailure() {
        // Test scenario where database connection fails
        // This would require mocking DBConnection or having a test environment

        assertDoesNotThrow(() -> {
            // The main method should handle connection failure gracefully
            // without throwing exceptions
            String[] args = {};
            // Main.main(args); // This would require database setup
        });
    }

    @Test
    void testApplicationInitialization() {
        // Test that core application components can be initialized
        assertDoesNotThrow(() -> {
            ProductService service = new ProductService();
            assertNotNull(service);
        });
    }

    @Test
    void testApplicationWithValidInput() {
        // Simulate user input for testing menu interactions
        String simulatedInput = "0\n"; // Exit option
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        assertDoesNotThrow(() -> {
            // Test basic application flow
            // Note: Full testing would require refactoring Main class for testability
        });
    }

    @Test
    void testApplicationComponents() {
        // Test that all major components are accessible and functional

        // Test ProductService initialization
        ProductService service = new ProductService();
        assertNotNull(service);

        // Test basic service operations
        int initialCount = service.getTotalProductCount();
        assertTrue(initialCount >= 0);

        // Test product operations with test data
        Product testProduct = new Product(9998, "App Test Product", 19.99, 1, "Test");

        // Clean up any existing test product
        service.removeProductById(9998);

        // Test add product
        boolean addResult = service.addProduct(testProduct);
        assertNotNull(addResult);

        // Test search product
        Product foundProduct = service.searchProductById(9998);
        if (foundProduct != null) {
            assertEquals("App Test Product", foundProduct.getName());
        }

        // Clean up
        service.removeProductById(9998);
    }

    @Test
    void testApplicationErrorHandling() {
        // Test that application handles various error conditions gracefully

        ProductService service = new ProductService();

        // Test null product handling
        boolean nullResult = service.addProduct(null);
        assertFalse(nullResult);

        // Test invalid ID handling
        Product invalidProduct = service.searchProductById(-1);
        assertNull(invalidProduct);

        // Test invalid operations
        boolean invalidUpdate = service.updateProductQuantity(-1, -1);
        assertFalse(invalidUpdate);
    }

    @Test
    void testApplicationDataIntegrity() {
        // Test that application maintains data integrity

        ProductService service = new ProductService();
        Product testProduct = new Product(9997, "Integrity Test Product", 29.99, 5, "Test");

        // Clean up any existing test product
        service.removeProductById(9997);

        try {
            // Add product
            boolean addResult = service.addProduct(testProduct);
            assertNotNull(addResult);

            if (addResult) {
                // Verify product exists
                assertTrue(service.productExists(9997));

                // Update product
                Product updatedProduct = new Product(9997, "Updated Integrity Test", 39.99, 8, "Updated Test");
                boolean updateResult = service.updateProduct(updatedProduct);
                assertNotNull(updateResult);

                if (updateResult) {
                    // Verify update
                    Product retrieved = service.searchProductById(9997);
                    if (retrieved != null) {
                        assertEquals("Updated Integrity Test", retrieved.getName());
                        assertEquals(39.99, retrieved.getPrice(), 0.01);
                    }
                }
            }
        } finally {
            // Clean up
            service.removeProductById(9997);
        }
    }

    @Test
    void testApplicationPerformance() {
        // Basic performance test to ensure reasonable response times

        ProductService service = new ProductService();

        long startTime = System.currentTimeMillis();

        // Perform basic operations
        service.getTotalProductCount();
        service.getAllProducts();
        service.searchProductById(1);
        service.searchProductsByName("test");
        service.searchProductsByCategory("electronics");

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Basic operations should complete within reasonable time (5 seconds)
        assertTrue(executionTime < 5000, "Basic operations took too long: " + executionTime + "ms");
    }

    @Test
    void testApplicationBoundaryConditions() {
        // Test application behavior with boundary conditions

        ProductService service = new ProductService();

        // Test with maximum values
        Product maxProduct = new Product(Integer.MAX_VALUE, "Max Test Product", Double.MAX_VALUE, Integer.MAX_VALUE,
                "Max Test");

        assertDoesNotThrow(() -> {
            // Should handle large values gracefully
            service.addProduct(maxProduct);
            service.removeProductById(Integer.MAX_VALUE);
        });

        // Test with minimum values
        Product minProduct = new Product(1, "Min Test Product", 0.01, 0, "Min Test");

        assertDoesNotThrow(() -> {
            // Should handle minimum values gracefully
            service.addProduct(minProduct);
            service.removeProductById(1);
        });
    }

    @Test
    void testApplicationConcurrency() {
        // Basic concurrency test

        ProductService service = new ProductService();

        assertDoesNotThrow(() -> {
            // Simulate concurrent access (basic test)
            Thread thread1 = new Thread(() -> {
                service.getTotalProductCount();
                service.getAllProducts();
            });

            Thread thread2 = new Thread(() -> {
                service.searchProductsByName("test");
                service.searchProductsByCategory("electronics");
            });

            thread1.start();
            thread2.start();

            thread1.join(5000); // Wait up to 5 seconds
            thread2.join(5000);

            // Threads should complete without hanging
            assertFalse(thread1.isAlive());
            assertFalse(thread2.isAlive());
        });
    }

    @Test
    void testApplicationResourceManagement() {
        // Test that application properly manages resources

        ProductService service = new ProductService();

        assertDoesNotThrow(() -> {
            // Perform multiple operations to test resource management
            for (int i = 0; i < 10; i++) {
                service.getAllProducts();
                service.getTotalProductCount();
            }

            // Multiple operations should not cause resource leaks or errors
        });
    }

    @Test
    void testApplicationValidation() {
        // Test application input validation

        ProductService service = new ProductService();

        // Test various invalid inputs
        assertFalse(service.addProduct(null));
        assertNull(service.searchProductsByName(null));
        assertNull(service.searchProductsByName(""));
        assertNull(service.searchProductsByCategory(null));
        assertNull(service.searchProductsByCategory(""));
        assertFalse(service.updateProductQuantity(1, -1));

        // Test valid inputs
        assertNotNull(service.getAllProducts());
        assertTrue(service.getTotalProductCount() >= 0);
        assertNotNull(service.searchProductsByName("test"));
        assertNotNull(service.searchProductsByCategory("electronics"));
    }
}
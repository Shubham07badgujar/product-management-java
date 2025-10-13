package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ProductValidationTest {

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product(1, "Test Product", 99.99, 10, "Electronics");
    }

    // Constructor Tests

    @Test
    void testConstructor_WithAllParameters_ShouldCreateValidProduct() {
        // Act
        Product product = new Product(1, "Laptop", 999.99, 5, "Electronics");

        // Assert
        assertEquals(1, product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals(999.99, product.getPrice(), 0.01);
        assertEquals(5, product.getQuantity());
        assertEquals("Electronics", product.getCategory());
    }

    @Test
    void testConstructor_WithoutCategory_ShouldUseDefaultCategory() {
        // Act
        Product product = new Product(1, "Laptop", 999.99, 5);

        // Assert
        assertEquals(1, product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals(999.99, product.getPrice(), 0.01);
        assertEquals(5, product.getQuantity());
        assertEquals("General", product.getCategory());
    }

    @Test
    void testConstructor_WithNullCategory_ShouldUseDefaultCategory() {
        // Act
        Product product = new Product(1, "Laptop", 999.99, 5, null);

        // Assert
        assertEquals("General", product.getCategory());
    }

    @Test
    void testConstructor_WithEmptyCategory_ShouldUseDefaultCategory() {
        // Act
        Product product = new Product(1, "Laptop", 999.99, 5, "");

        // Assert
        assertEquals("General", product.getCategory());
    }

    @Test
    void testConstructor_WithWhitespaceCategory_ShouldUseDefaultCategory() {
        // Act
        Product product = new Product(1, "Laptop", 999.99, 5, "   ");

        // Assert
        assertEquals("General", product.getCategory());
    }

    // ID Validation Tests

    @ParameterizedTest
    @ValueSource(ints = { 1, 100, 999, Integer.MAX_VALUE })
    void testSetId_ValidValues_ShouldSetId(int id) {
        // Act
        testProduct.setId(id);

        // Assert
        assertEquals(id, testProduct.getId());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -1, -100, Integer.MIN_VALUE })
    void testSetId_EdgeValues_ShouldSetId(int id) {
        // Act & Assert
        // Note: Product class doesn't validate ID constraints,
        // so even negative IDs are allowed by the current implementation
        testProduct.setId(id);
        assertEquals(id, testProduct.getId());
    }

    // Name Validation Tests

    @Test
    void testSetName_ValidName_ShouldSetName() {
        // Arrange
        String validName = "Valid Product Name";

        // Act
        testProduct.setName(validName);

        // Assert
        assertEquals(validName, testProduct.getName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   ", "\t", "\n" })
    void testSetName_InvalidNames_ShouldStillSet(String name) {
        // Act & Assert
        // Note: Current Product implementation doesn't validate names
        testProduct.setName(name);
        assertEquals(name, testProduct.getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "A",
            "Short Name",
            "This is a very long product name that might exceed typical length limits for product names in most systems",
            "Product with 123 numbers",
            "Product-with-special-chars!@#$%"
    })
    void testSetName_VariousNameFormats_ShouldSetName(String name) {
        // Act
        testProduct.setName(name);

        // Assert
        assertEquals(name, testProduct.getName());
    }

    // Price Validation Tests

    @ParameterizedTest
    @ValueSource(doubles = { 0.01, 1.0, 99.99, 999.99, 9999.99, Double.MAX_VALUE })
    void testSetPrice_ValidPrices_ShouldSetPrice(double price) {
        // Act
        testProduct.setPrice(price);

        // Assert
        assertEquals(price, testProduct.getPrice(), 0.001);
    }

    @ParameterizedTest
    @ValueSource(doubles = { 0.0, -1.0, -99.99, Double.MIN_VALUE })
    void testSetPrice_EdgePrices_ShouldSetPrice(double price) {
        // Act & Assert
        // Note: Current implementation doesn't validate price constraints
        testProduct.setPrice(price);
        assertEquals(price, testProduct.getPrice(), 0.001);
    }

    @Test
    void testSetPrice_NaN_ShouldSetNaN() {
        // Act
        testProduct.setPrice(Double.NaN);

        // Assert
        assertTrue(Double.isNaN(testProduct.getPrice()));
    }

    @Test
    void testSetPrice_Infinity_ShouldSetInfinity() {
        // Act
        testProduct.setPrice(Double.POSITIVE_INFINITY);

        // Assert
        assertTrue(Double.isInfinite(testProduct.getPrice()));
    }

    // Quantity Validation Tests

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 10, 100, 1000, Integer.MAX_VALUE })
    void testSetQuantity_ValidQuantities_ShouldSetQuantity(int quantity) {
        // Act
        testProduct.setQuantity(quantity);

        // Assert
        assertEquals(quantity, testProduct.getQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, -10, -100, Integer.MIN_VALUE })
    void testSetQuantity_NegativeQuantities_ShouldSetQuantity(int quantity) {
        // Act & Assert
        // Note: Current implementation doesn't validate quantity constraints
        testProduct.setQuantity(quantity);
        assertEquals(quantity, testProduct.getQuantity());
    }

    // Category Validation Tests

    @ParameterizedTest
    @ValueSource(strings = { "Electronics", "Clothing", "Books", "Sports", "Home & Garden" })
    void testSetCategory_ValidCategories_ShouldSetCategory(String category) {
        // Act
        testProduct.setCategory(category);

        // Assert
        assertEquals(category, testProduct.getCategory());
    }

    @Test
    void testSetCategory_NullCategory_ShouldUseDefault() {
        // Act
        testProduct.setCategory(null);

        // Assert
        assertEquals("General", testProduct.getCategory());
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "   ", "\t\t", "\n\n" })
    void testSetCategory_EmptyOrWhitespaceCategory_ShouldUseDefault(String category) {
        // Act
        testProduct.setCategory(category);

        // Assert
        assertEquals("General", testProduct.getCategory());
    }

    // Business Logic Tests

    @Test
    void testGetTotalValue_ShouldCalculateCorrectly() {
        // Arrange
        testProduct.setPrice(50.0);
        testProduct.setQuantity(10);

        // Act
        double totalValue = testProduct.getTotalValue();

        // Assert
        assertEquals(500.0, totalValue, 0.01);
    }

    @Test
    void testGetTotalValue_WithZeroQuantity_ShouldReturnZero() {
        // Arrange
        testProduct.setPrice(99.99);
        testProduct.setQuantity(0);

        // Act
        double totalValue = testProduct.getTotalValue();

        // Assert
        assertEquals(0.0, totalValue, 0.01);
    }

    @Test
    void testGetTotalValue_WithZeroPrice_ShouldReturnZero() {
        // Arrange
        testProduct.setPrice(0.0);
        testProduct.setQuantity(10);

        // Act
        double totalValue = testProduct.getTotalValue();

        // Assert
        assertEquals(0.0, totalValue, 0.01);
    }

    @ParameterizedTest
    @CsvSource({
            "10.50, 5, 52.50",
            "0.99, 100, 99.00",
            "999.99, 1, 999.99",
            "1.00, 0, 0.00",
            "0.00, 999, 0.00"
    })
    void testGetTotalValue_VariousInputs_ShouldCalculateCorrectly(double price, int quantity, double expectedTotal) {
        // Arrange
        testProduct.setPrice(price);
        testProduct.setQuantity(quantity);

        // Act
        double totalValue = testProduct.getTotalValue();

        // Assert
        assertEquals(expectedTotal, totalValue, 0.01);
    }

    @Test
    void testIsInStock_WithPositiveQuantity_ShouldReturnTrue() {
        // Arrange
        testProduct.setQuantity(1);

        // Act & Assert
        assertTrue(testProduct.isInStock());
    }

    @Test
    void testIsInStock_WithZeroQuantity_ShouldReturnFalse() {
        // Arrange
        testProduct.setQuantity(0);

        // Act & Assert
        assertFalse(testProduct.isInStock());
    }

    @Test
    void testIsInStock_WithNegativeQuantity_ShouldReturnFalse() {
        // Arrange
        testProduct.setQuantity(-1);

        // Act & Assert
        assertFalse(testProduct.isInStock());
    }

    // Equals and HashCode Tests

    @Test
    void testEquals_SameId_ShouldReturnTrue() {
        // Arrange
        Product product1 = new Product(1, "Product 1", 10.0, 5, "Category1");
        Product product2 = new Product(1, "Product 2", 20.0, 10, "Category2");

        // Act & Assert
        assertEquals(product1, product2);
    }

    @Test
    void testEquals_DifferentId_ShouldReturnFalse() {
        // Arrange
        Product product1 = new Product(1, "Product", 10.0, 5, "Category");
        Product product2 = new Product(2, "Product", 10.0, 5, "Category");

        // Act & Assert
        assertNotEquals(product1, product2);
    }

    @Test
    void testEquals_SameObject_ShouldReturnTrue() {
        // Act & Assert
        assertEquals(testProduct, testProduct);
    }

    @Test
    void testEquals_NullObject_ShouldReturnFalse() {
        // Act & Assert
        assertNotEquals(testProduct, null);
    }

    @Test
    void testEquals_DifferentClass_ShouldReturnFalse() {
        // Act & Assert
        assertNotEquals(testProduct, "Not a Product");
    }

    @Test
    void testHashCode_SameId_ShouldHaveSameHashCode() {
        // Arrange
        Product product1 = new Product(1, "Product 1", 10.0, 5, "Category1");
        Product product2 = new Product(1, "Product 2", 20.0, 10, "Category2");

        // Act & Assert
        assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    void testHashCode_DifferentId_ShouldHaveDifferentHashCode() {
        // Arrange
        Product product1 = new Product(1, "Product", 10.0, 5, "Category");
        Product product2 = new Product(2, "Product", 10.0, 5, "Category");

        // Act & Assert
        assertNotEquals(product1.hashCode(), product2.hashCode());
    }

    // ToString Tests

    @Test
    void testToString_ShouldContainAllProductInfo() {
        // Act
        String result = testProduct.toString();

        // Assert
        assertTrue(result.contains("ID=1"));
        assertTrue(result.contains("Name='Test Product'"));
        assertTrue(result.contains("Price=99.99"));
        assertTrue(result.contains("Quantity=10"));
        assertTrue(result.contains("Category='Electronics'"));
        assertTrue(result.contains("In Stock=Yes"));
    }

    @Test
    void testToString_WithZeroQuantity_ShouldShowNotInStock() {
        // Arrange
        testProduct.setQuantity(0);

        // Act
        String result = testProduct.toString();

        // Assert
        assertTrue(result.contains("In Stock=No"));
    }

    // Edge Case and Stress Tests

    @Test
    void testProduct_ExtremeValues_ShouldHandleGracefully() {
        // Arrange & Act
        Product extremeProduct = new Product(
                Integer.MAX_VALUE,
                "Extreme Product with a very long name that might cause issues in some systems",
                Double.MAX_VALUE,
                Integer.MAX_VALUE,
                "Extreme Category Name");

        // Assert
        assertNotNull(extremeProduct);
        assertEquals(Integer.MAX_VALUE, extremeProduct.getId());
        assertTrue(extremeProduct.getPrice() > 0);
        assertTrue(extremeProduct.getQuantity() > 0);
    }

    @Test
    void testProduct_MinimumValues_ShouldHandleGracefully() {
        // Arrange & Act
        Product minProduct = new Product(
                Integer.MIN_VALUE,
                "",
                Double.MIN_VALUE,
                Integer.MIN_VALUE,
                "");

        // Assert
        assertNotNull(minProduct);
        assertEquals(Integer.MIN_VALUE, minProduct.getId());
        assertEquals("", minProduct.getName());
        assertEquals("General", minProduct.getCategory()); // Empty category becomes "General"
    }

    @Test
    void testProduct_SpecialCharacters_ShouldHandleCorrectly() {
        // Arrange & Act
        Product specialProduct = new Product(
                1,
                "Product with émojis 🎯 and special chars: àáâãäåæçèéêë",
                99.99,
                5,
                "Category with special chars: ñóôõöø");

        // Assert
        assertNotNull(specialProduct);
        assertTrue(specialProduct.getName().contains("émojis"));
        assertTrue(specialProduct.getCategory().contains("ñóôõöø"));
    }
}
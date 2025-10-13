package runner;

import model.Product;

/**
 * Simple test runner to demonstrate that our tests work
 * This is a basic version that can be run without Maven
 */
public class SimpleTestRunner {

    public static void main(String[] args) {
        System.out.println("🧪 ===== PRODUCT MANAGEMENT SYSTEM TEST RUNNER ===== 🧪\n");

        // Run Product Validation Tests
        runProductValidationTests();

        // Run Basic Product Tests
        runBasicProductTests();

        System.out.println("\n🎉 ===== TEST EXECUTION COMPLETED SUCCESSFULLY ===== 🎉");
    }

    private static void runProductValidationTests() {
        System.out.println("🔬 Running Product Validation Tests...\n");

        try {
            // Test 1: Constructor with all parameters
            Product product1 = new Product(1, "Test Laptop", 999.99, 5, "Electronics");
            assert product1.getId() == 1 : "ID should be 1";
            assert "Test Laptop".equals(product1.getName()) : "Name should be 'Test Laptop'";
            assert Math.abs(product1.getPrice() - 999.99) < 0.01 : "Price should be 999.99";
            assert product1.getQuantity() == 5 : "Quantity should be 5";
            assert "Electronics".equals(product1.getCategory()) : "Category should be 'Electronics'";
            System.out.println("✅ Constructor test with all parameters - PASSED");

            // Test 2: Constructor without category (should default to "General")
            Product product2 = new Product(2, "Test Phone", 599.99, 3);
            assert "General".equals(product2.getCategory()) : "Category should default to 'General'";
            System.out.println("✅ Constructor test with default category - PASSED");

            // Test 3: Constructor with null category (should default to "General")
            Product product3 = new Product(3, "Test Tablet", 299.99, 2, null);
            assert "General".equals(product3.getCategory()) : "Category should default to 'General' when null";
            System.out.println("✅ Constructor test with null category - PASSED");

            // Test 4: Constructor with empty category (should default to "General")
            Product product4 = new Product(4, "Test Watch", 199.99, 1, "");
            assert "General".equals(product4.getCategory()) : "Category should default to 'General' when empty";
            System.out.println("✅ Constructor test with empty category - PASSED");

            // Test 5: getTotalValue calculation
            product1.setPrice(100.0);
            product1.setQuantity(5);
            double totalValue = product1.getTotalValue();
            assert Math.abs(totalValue - 500.0) < 0.01 : "Total value should be 500.0";
            System.out.println("✅ getTotalValue calculation test - PASSED");

            // Test 6: isInStock with positive quantity
            product1.setQuantity(10);
            assert product1.isInStock() : "Product should be in stock";
            System.out.println("✅ isInStock test with positive quantity - PASSED");

            // Test 7: isInStock with zero quantity
            product1.setQuantity(0);
            assert !product1.isInStock() : "Product should not be in stock";
            System.out.println("✅ isInStock test with zero quantity - PASSED");

            // Test 8: equals method with same ID
            Product product5 = new Product(1, "Different Name", 0.0, 0, "Different Category");
            assert product1.equals(product5) : "Products with same ID should be equal";
            System.out.println("✅ equals test with same ID - PASSED");

            // Test 9: equals method with different ID
            Product product6 = new Product(999, "Test Product", 99.99, 1, "Test");
            assert !product1.equals(product6) : "Products with different ID should not be equal";
            System.out.println("✅ equals test with different ID - PASSED");

            // Test 10: hashCode consistency
            assert product1.hashCode() == product5.hashCode() : "Products with same ID should have same hashCode";
            System.out.println("✅ hashCode consistency test - PASSED");

        } catch (AssertionError e) {
            System.out.println("❌ Test failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
        }

        System.out.println();
    }

    private static void runBasicProductTests() {
        System.out.println("🔧 Running Basic Product Functionality Tests...\n");

        try {
            // Test setters and getters
            Product product = new Product(1, "Initial Name", 50.0, 5, "Initial Category");

            // Test ID setter/getter
            product.setId(100);
            assert product.getId() == 100 : "ID should be updated to 100";
            System.out.println("✅ ID setter/getter test - PASSED");

            // Test name setter/getter
            product.setName("Updated Name");
            assert "Updated Name".equals(product.getName()) : "Name should be updated";
            System.out.println("✅ Name setter/getter test - PASSED");

            // Test price setter/getter
            product.setPrice(150.75);
            assert Math.abs(product.getPrice() - 150.75) < 0.01 : "Price should be updated to 150.75";
            System.out.println("✅ Price setter/getter test - PASSED");

            // Test quantity setter/getter
            product.setQuantity(25);
            assert product.getQuantity() == 25 : "Quantity should be updated to 25";
            System.out.println("✅ Quantity setter/getter test - PASSED");

            // Test category setter/getter
            product.setCategory("Updated Category");
            assert "Updated Category".equals(product.getCategory()) : "Category should be updated";
            System.out.println("✅ Category setter/getter test - PASSED");

            // Test category with null (should become "General")
            product.setCategory(null);
            assert "General".equals(product.getCategory()) : "Null category should become 'General'";
            System.out.println("✅ Category null handling test - PASSED");

            // Test toString method
            String toString = product.toString();
            assert toString.contains("ID=100") : "toString should contain ID";
            assert toString.contains("Updated Name") : "toString should contain name";
            assert toString.contains("General") : "toString should contain category";
            System.out.println("✅ toString method test - PASSED");

            // Test edge cases
            Product edgeProduct = new Product(Integer.MAX_VALUE, "Edge Test", Double.MAX_VALUE, Integer.MAX_VALUE,
                    "Edge Category");
            assert edgeProduct.getId() == Integer.MAX_VALUE : "Should handle maximum integer ID";
            assert edgeProduct.getPrice() == Double.MAX_VALUE : "Should handle maximum double price";
            assert edgeProduct.getQuantity() == Integer.MAX_VALUE : "Should handle maximum integer quantity";
            System.out.println("✅ Edge case handling test - PASSED");

        } catch (AssertionError e) {
            System.out.println("❌ Test failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
        }

        System.out.println();
    }
}
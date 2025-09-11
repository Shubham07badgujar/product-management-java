package main;

import java.util.List;
import java.util.Scanner;

import model.Product;
import service.ProductService;

/**
 * Main class for the Product Management System
 * Provides a console-based interface for managing products through menu-driven
 * operations
 */


public class Main {
    private static ProductService productService = new ProductService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("    WELCOME TO PRODUCT MANAGEMENT SYSTEM");
        System.out.println("=================================================");

        // Add some sample data for demonstration
        initializeSampleData();

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addProduct();
                    break;
                case 2:
                    viewAllProducts();
                    break;
                case 3:
                    searchProductById();
                    break;
                case 4:
                    searchProductByName();
                    break;
                case 5:
                    updateProductQuantity();
                    break;
                case 6:
                    removeProduct();
                    break;
                case 7:
                    updateProductPrice();
                    break;
                case 8:
                    viewInventoryStatistics();
                    break;
                case 9:
                    viewLowStockProducts();
                    break;
                case 0:
                    running = false;
                    System.out.println("\nThank you for using Product Management System!");
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("\nInvalid choice! Please enter a number between 0-9.");
            }

            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    /**
     * Displays the main menu options
     */
    private static void displayMenu() {
        System.out.println("\n=================================================");
        System.out.println("              MAIN MENU");
        System.out.println("=================================================");
        System.out.println("1. Add Product");
        System.out.println("2. View All Products");
        System.out.println("3. Search Product by ID");
        System.out.println("4. Search Product by Name");
        System.out.println("5. Update Product Quantity");
        System.out.println("6. Remove Product");
        System.out.println("7. Update Product Price");
        System.out.println("8. View Inventory Statistics");
        System.out.println("9. View Low Stock Products");
        System.out.println("0. Exit");
        System.out.println("=================================================");
    }

    /**
     * Adds a new product to the inventory
     */
    private static void addProduct() {
        System.out.println("\n--- ADD NEW PRODUCT ---");

        int id = getIntInput("Enter Product ID: ");

        // Check if product with this ID already exists
        if (productService.searchProductById(id) != null) {
            System.out.println("❌ Error: Product with ID " + id + " already exists!");
            return;
        }

        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("❌ Error: Product name cannot be empty!");
            return;
        }

        double price = getDoubleInput("Enter Product Price: $");
        if (price < 0) {
            System.out.println("❌ Error: Price cannot be negative!");
            return;
        }

        int quantity = getIntInput("Enter Product Quantity: ");
        if (quantity < 0) {
            System.out.println("❌ Error: Quantity cannot be negative!");
            return;
        }

        Product product = new Product(id, name, price, quantity);

        if (productService.addProduct(product)) {
            System.out.println("✅ Product added successfully!");
            System.out.println("Added: " + product);
        } else {
            System.out.println("❌ Failed to add product!");
        }
    }

    /**
     * Displays all products in the inventory
     */
    private static void viewAllProducts() {
        System.out.println("\n--- ALL PRODUCTS ---");

        List<Product> products = productService.getAllProducts();

        if (products.isEmpty()) {
            System.out.println("📦 No products found in inventory.");
            return;
        }

        System.out.println("Total Products: " + products.size());
        System.out.println("-".repeat(120));
        System.out.printf("%-5s %-25s %-10s %-10s %-15s %-10s%n",
                "ID", "Name", "Price", "Quantity", "Total Value", "In Stock");
        System.out.println("-".repeat(120));

        for (Product product : products) {
            System.out.printf("%-5d %-25s $%-9.2f %-10d $%-14.2f %-10s%n",
                    product.getId(),
                    truncate(product.getName(), 25),
                    product.getPrice(),
                    product.getQuantity(),
                    product.getTotalValue(),
                    product.isInStock() ? "Yes" : "No");
        }
        System.out.println("-".repeat(120));
    }

    /**
     * Searches for a product by its ID
     */
    private static void searchProductById() {
        System.out.println("\n--- SEARCH PRODUCT BY ID ---");

        int id = getIntInput("Enter Product ID to search: ");
        Product product = productService.searchProductById(id);

        if (product != null) {
            System.out.println("✅ Product found:");
            System.out.println(product);
        } else {
            System.out.println("❌ No product found with ID: " + id);
        }
    }

    /**
     * Searches for products by name
     */
    private static void searchProductByName() {
        System.out.println("\n--- SEARCH PRODUCT BY NAME ---");

        System.out.print("Enter Product Name (or part of name): ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("❌ Error: Search name cannot be empty!");
            return;
        }

        List<Product> products = productService.searchProductByName(name);

        if (products.isEmpty()) {
            System.out.println("❌ No products found matching: '" + name + "'");
        } else {
            System.out.println("✅ Found " + products.size() + " product(s) matching: '" + name + "'");
            System.out.println("-".repeat(120));
            for (Product product : products) {
                System.out.println(product);
            }
        }
    }

    /**
     * Updates the quantity of a product
     */
    private static void updateProductQuantity() {
        System.out.println("\n--- UPDATE PRODUCT QUANTITY ---");

        int id = getIntInput("Enter Product ID: ");
        Product product = productService.searchProductById(id);

        if (product == null) {
            System.out.println("❌ No product found with ID: " + id);
            return;
        }

        System.out.println("Current product: " + product);
        int newQuantity = getIntInput("Enter new quantity (current: " + product.getQuantity() + "): ");

        if (newQuantity < 0) {
            System.out.println("❌ Error: Quantity cannot be negative!");
            return;
        }

        if (productService.updateProductQuantity(id, newQuantity)) {
            System.out.println("✅ Quantity updated successfully!");
            System.out.println("Updated product: " + productService.searchProductById(id));
        } else {
            System.out.println("❌ Failed to update quantity!");
        }
    }

    /**
     * Removes a product from the inventory
     */
    private static void removeProduct() {
        System.out.println("\n--- REMOVE PRODUCT ---");

        int id = getIntInput("Enter Product ID to remove: ");
        Product product = productService.searchProductById(id);

        if (product == null) {
            System.out.println("❌ No product found with ID: " + id);
            return;
        }

        System.out.println("Product to remove: " + product);
        System.out.print("Are you sure you want to remove this product? (y/N): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("y") || confirm.equals("yes")) {
            if (productService.removeProductById(id)) {
                System.out.println("✅ Product removed successfully!");
            } else {
                System.out.println("❌ Failed to remove product!");
            }
        } else {
            System.out.println("❌ Product removal cancelled.");
        }
    }

    /**
     * Updates the price of a product
     */
    private static void updateProductPrice() {
        System.out.println("\n--- UPDATE PRODUCT PRICE ---");

        int id = getIntInput("Enter Product ID: ");
        Product product = productService.searchProductById(id);

        if (product == null) {
            System.out.println("❌ No product found with ID: " + id);
            return;
        }

        System.out.println("Current product: " + product);
        double newPrice = getDoubleInput("Enter new price (current: $" + product.getPrice() + "): $");

        if (newPrice < 0) {
            System.out.println("❌ Error: Price cannot be negative!");
            return;
        }

        if (productService.updateProductPrice(id, newPrice)) {
            System.out.println("✅ Price updated successfully!");
            System.out.println("Updated product: " + productService.searchProductById(id));
        } else {
            System.out.println("❌ Failed to update price!");
        }
    }

    /**
     * Displays inventory statistics
     */
    private static void viewInventoryStatistics() {
        System.out.println("\n--- INVENTORY STATISTICS ---");

        List<Product> allProducts = productService.getAllProducts();
        List<Product> outOfStockProducts = productService.getOutOfStockProducts();
        List<Product> lowStockProducts = productService.getLowStockProducts(5);

        System.out.println("📊 Total Products: " + productService.getTotalProductCount());
        System.out.printf("💰 Total Inventory Value: $%.2f%n", productService.getTotalInventoryValue());
        System.out.println("❌ Out of Stock Products: " + outOfStockProducts.size());
        System.out.println("⚠️  Low Stock Products (≤5): " + lowStockProducts.size());

        if (!outOfStockProducts.isEmpty()) {
            System.out.println("\n🔴 Out of Stock Products:");
            for (Product product : outOfStockProducts) {
                System.out.println("   - " + product.getName() + " (ID: " + product.getId() + ")");
            }
        }
    }

    /**
     * Displays products with low stock
     */
    private static void viewLowStockProducts() {
        System.out.println("\n--- LOW STOCK PRODUCTS ---");

        int threshold = getIntInput("Enter stock threshold (default 5): ");
        if (threshold <= 0)
            threshold = 5;

        List<Product> lowStockProducts = productService.getLowStockProducts(threshold);

        if (lowStockProducts.isEmpty()) {
            System.out.println("✅ No products with low stock (threshold: " + threshold + ")");
        } else {
            System.out.println("⚠️  Found " + lowStockProducts.size() + " product(s) with low stock:");
            System.out.println("-".repeat(120));
            for (Product product : lowStockProducts) {
                System.out.println(product);
            }
        }
    }

    /**
     * Initializes some sample data for demonstration
     */
    private static void initializeSampleData() {
        productService.addProduct(new Product(1, "Gaming Laptop", 1299.99, 10));
        productService.addProduct(new Product(2, "Wireless Mouse", 29.99, 50));
        productService.addProduct(new Product(3, "Mechanical Keyboard", 89.99, 25));
        productService.addProduct(new Product(4, "4K Monitor", 399.99, 8));
        productService.addProduct(new Product(5, "USB-C Hub", 45.50, 0));
    }

    /**
     * Helper method to get integer input with error handling
     */
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a valid number.");
            }
        }
    }

    /**
     * Helper method to get double input with error handling
     */
    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = Double.parseDouble(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a valid decimal number.");
            }
        }
    }

    /**
     * Helper method to truncate strings for display formatting
     */
    private static String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}

package main;

import java.util.List;
import java.util.Scanner;

import model.Product;
import service.ProductService;
import util.DBConnection;

public class Main {
    private static final ProductService productService = new ProductService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("===== PRODUCT MANAGEMENT SYSTEM =====");
        System.out.println("Connecting to MySQL database...");

        if (!testDatabaseConnection()) {
            System.err.println("Failed to connect to database. Please check your MySQL configuration.");
            System.err.println("1. Make sure MySQL server is running");
            System.err.println("2. Update database.properties with correct credentials");
            System.err.println("3. Run setup_db.sql to create the database and tables");
            return;
        }

        System.out.println("Database connection successful!");

        while (true) {
            displayMenu();
            try {
                int choice = getValidInt("Enter your choice: ", 0, 5);

                switch (choice) {
                    case 1 -> addProduct();
                    case 2 -> viewAllProducts();
                    case 3 -> searchProductById();
                    case 4 -> updateProductQuantity();
                    case 5 -> removeProduct();
                    case 0 -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                }

                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Add Product");
        System.out.println("2. View All Products");
        System.out.println("3. Search Product by ID");
        System.out.println("4. Update Product Quantity");
        System.out.println("5. Remove Product");
        System.out.println("0. Exit");
        System.out.println("=================");
    }

    private static void addProduct() {
        System.out.println("\n--- ADD PRODUCT ---");

        int id = getValidInt("Enter Product ID: ", 1, Integer.MAX_VALUE);

        if (productService.searchProductById(id) != null) {
            System.out.println("Error: Product with ID " + id + " already exists!");
            return;
        }

        String name = getValidString("Enter Product Name: ");
        double price = getValidDouble("Enter Product Price: $", 0.01, 999999.99);
        int quantity = getValidInt("Enter Product Quantity: ", 0, Integer.MAX_VALUE);

        Product product = new Product(id, name, price, quantity);

        if (productService.addProduct(product)) {
            System.out.println("Product added successfully: " + product);
        } else {
            System.out.println("Failed to add product!");
        }
    }

    private static void viewAllProducts() {
        System.out.println("\n--- ALL PRODUCTS ---");
        List<Product> products = productService.getAllProducts();

        if (products.isEmpty()) {
            System.out.println("No products found.");
        } else {
            System.out.println("Total Products: " + products.size());
            for (Product product : products) {
                System.out.println(product);
            }
        }
    }

    private static void searchProductById() {
        System.out.println("\n--- SEARCH BY ID ---");
        int id = getValidInt("Enter Product ID: ", 1, Integer.MAX_VALUE);
        Product product = productService.searchProductById(id);

        if (product != null) {
            System.out.println("Product found: " + product);
        } else {
            System.out.println("No product found with ID: " + id);
        }
    }

    private static void updateProductQuantity() {
        System.out.println("\n--- UPDATE QUANTITY ---");
        int id = getValidInt("Enter Product ID: ", 1, Integer.MAX_VALUE);
        Product product = productService.searchProductById(id);

        if (product == null) {
            System.out.println("No product found with ID: " + id);
            return;
        }

        System.out.println("Current: " + product);
        int newQuantity = getValidInt("Enter new quantity: ", 0, Integer.MAX_VALUE);

        if (productService.updateProductQuantity(id, newQuantity)) {
            System.out.println("Quantity updated successfully!");
        } else {
            System.out.println("Failed to update quantity!");
        }
    }

    private static void removeProduct() {
        System.out.println("\n--- REMOVE PRODUCT ---");
        int id = getValidInt("Enter Product ID: ", 1, Integer.MAX_VALUE);
        Product product = productService.searchProductById(id);

        if (product == null) {
            System.out.println("No product found with ID: " + id);
            return;
        }

        System.out.println("Product to remove: " + product);
        System.out.print("Confirm removal (y/N): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("y")) {
            if (productService.removeProductById(id)) {
                System.out.println("Product removed successfully!");
            } else {
                System.out.println("Failed to remove product!");
            }
        } else {
            System.out.println("Removal cancelled.");
        }
    }

    private static boolean testDatabaseConnection() {
        return DBConnection.testConnection();
    }

    private static int getValidInt(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
            }
        }
    }

    private static double getValidDouble(String prompt, double min, double max) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid decimal number.");
            }
        }
    }

    private static String getValidString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty!");
        }
    }
}

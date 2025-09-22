package main;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import model.Product;
import service.ProductService;
import util.CSVHelper;
import util.DBConnection;

public class Main {
    private static final ProductService productService = new ProductService();
    private static final CSVHelper csvHelper = new CSVHelper();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("===== INVENTORY MANAGEMENT SYSTEM =====");
        System.out.println("Connecting to MySQL database...");

        if (!testDatabaseConnection()) {
            System.err.println("Failed to connect to database. Please check your MySQL configuration.");
            System.err.println("1. Make sure MySQL server is running");
            System.err.println("2. Update database.properties with correct credentials");
            System.err.println("3. Run setup_db.sql to create the database and tables");
            return;
        }

        System.out.println("Database connection successful!");
        System.out.println("CSV integration initialized!");

        while (true) {
            displayMainMenu();
            try {
                int choice = getValidInt("Enter your choice: ", 0, 8);

                switch (choice) {
                    case 1 -> addProduct();
                    case 2 -> viewAllProducts();
                    case 3 -> searchProductById();
                    case 4 -> updateProductQuantity();
                    case 5 -> removeProduct();
                    case 6 -> generateCSVReport();
                    case 7 -> syncDatabaseToCSV();
                    case 8 -> viewCSVStatistics();
                    case 0 -> {
                        System.out.println("Thank you for using Inventory Management System!");
                        System.out.println("Goodbye!");
                        return;
                    }
                }

                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Add Product");
        System.out.println("2. View All Products");
        System.out.println("3. Search Product by ID");
        System.out.println("4. Update Product Quantity");
        System.out.println("5. Remove Product");
        System.out.println("6. Generate CSV Report");
        System.out.println("7. Sync Database to CSV");
        System.out.println("8. View CSV Statistics");
        System.out.println("0. Exit");
        System.out.println("=================");
    }

    private static void generateCSVReport() {
        System.out.println("\n--- GENERATE CSV REPORT ---");

        System.out.println("Generating comprehensive product report...");

        Path reportPath = csvHelper.generateDownloadableReport();

        if (reportPath != null) {
            System.out.println("Report generated successfully!");
            System.out.println("Report location: " + reportPath.toAbsolutePath());
            System.out.println("You can find the CSV file in the 'reports' directory");
        } else {
            System.err.println("Failed to generate report. Please try again.");
        }
    }

    private static void syncDatabaseToCSV() {
        System.out.println("\n--- SYNC DATABASE TO CSV ---");

        System.out.println("Starting synchronization process...");

        if (csvHelper.syncDatabaseToCSV()) {
            System.out.println("Database successfully synchronized to CSV file!");
            System.out.println("All database records are now available in CSV format");
        } else {
            System.err.println("Synchronization failed. Please check the logs for details.");
        }
    }

    private static void viewCSVStatistics() {
        System.out.println("\n--- CSV FILE STATISTICS ---");
        Map<String, Object> stats = csvHelper.getCSVStatistics();

        if ((Boolean) stats.getOrDefault("file_exists", false)) {
            System.out.println("CSV File Status: EXISTS");
            System.out.println("Record Count: " + stats.get("record_count"));
            System.out.println("File Size: " + stats.get("file_size_bytes") + " bytes");
            System.out.println("Last Modified: " + stats.get("last_modified"));
        } else {
            System.out.println("CSV File Status: NOT FOUND");
            System.out.println("Record Count: 0");
        }

        if (stats.containsKey("error")) {
            System.err.println("Error: " + stats.get("error"));
        }
    }

    private static void addProduct() {
        System.out.println("\n--- ADD NEW PRODUCT ---");

        int id = getValidInt("Enter Product ID: ", 1, Integer.MAX_VALUE);

        if (productService.searchProductById(id) != null) {
            System.err.println("Product with ID " + id + " already exists!");
            return;
        }

        String name = getValidString("Enter Product Name: ");
        double price = getValidDouble("Enter Product Price: $", 0.01, 999999.99);
        int quantity = getValidInt("Enter Product Quantity: ", 0, Integer.MAX_VALUE);

        Product product = new Product(id, name, price, quantity);

        if (csvHelper.addProductToCSVAndDB(product)) {
            System.out.println("Product added successfully to both database and CSV!");
            System.out.println("Product details: " + product);
        } else {
            System.err.println("Failed to add product!");
        }
    }

    private static void viewAllProducts() {
        System.out.println("\n--- ALL PRODUCTS ---");

        List<Product> products = productService.getAllProducts();

        if (products == null || products.isEmpty()) {
            System.out.println("No products found in the database.");
        } else {
            System.out.println("Total Products: " + products.size());
            System.out.printf("%-5s %-20s %-10s %-10s %-15s%n", "ID", "Name", "Price", "Quantity", "Total Value");
            System.out.println("--------------------------------------------------------------------");

            double totalInventoryValue = 0;
            for (Product product : products) {
                double totalValue = product.getTotalValue();
                totalInventoryValue += totalValue;
                System.out.printf("%-5d %-20s $%-9.2f %-10d $%-14.2f%n",
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getQuantity(),
                        totalValue);
            }

            System.out.println("====================================================================");
            System.out.println("Total Inventory Value: $" + String.format("%.2f", totalInventoryValue));
        }
    }

    private static void searchProductById() {
        System.out.println("\n--- SEARCH PRODUCT BY ID ---");

        int id = getValidInt("Enter Product ID to search: ", 1, Integer.MAX_VALUE);
        Product product = productService.searchProductById(id);

        if (product != null) {
            System.out.println("Product found!");
            System.out.printf("ID: %d%n", product.getId());
            System.out.printf("Name: %s%n", product.getName());
            System.out.printf("Price: $%.2f%n", product.getPrice());
            System.out.printf("Quantity: %d%n", product.getQuantity());
            System.out.printf("Total Value: $%.2f%n", product.getTotalValue());
        } else {
            System.out.println("No product found with ID: " + id);
        }
    }

    private static void updateProductQuantity() {
        System.out.println("\n=== UPDATE PRODUCT QUANTITY ===");

        int id = getValidInt("Enter Product ID: ", 1, Integer.MAX_VALUE);
        Product product = productService.searchProductById(id);

        if (product == null) {
            System.out.println("No product found with ID: " + id);
            return;
        }

        System.out.println("Current Product Details:");
        System.out.printf("Name: %s%n", product.getName());
        System.out.printf("Current Quantity: %d%n", product.getQuantity());

        int newQuantity = getValidInt("Enter new quantity: ", 0, Integer.MAX_VALUE);

        if (productService.updateProductQuantity(id, newQuantity)) {
            System.out.println("Quantity updated successfully!");
            csvHelper.syncDatabaseToCSV();
            System.out.println("Changes synchronized to CSV file");
        } else {
            System.out.println("Failed to update quantity!");
        }
    }

    private static void removeProduct() {
        System.out.println("\n=== REMOVE PRODUCT ===");

        int id = getValidInt("Enter Product ID to remove: ", 1, Integer.MAX_VALUE);
        Product product = productService.searchProductById(id);

        if (product == null) {
            System.out.println("No product found with ID: " + id);
            return;
        }

        System.out.println("Product to be removed:");
        System.out.printf("Name: %s%n", product.getName());
        System.out.printf("Price: $%.2f%n", product.getPrice());
        System.out.printf("Quantity: %d%n", product.getQuantity());

        System.out.print("Confirm removal (y/N): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("y")) {
            if (productService.removeProductById(id)) {
                System.out.println("Product removed successfully from database!");
                csvHelper.syncDatabaseToCSV();
                System.out.println("Changes synchronized to CSV file");
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

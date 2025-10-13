package main;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import exception.InvalidInputException;
import model.Product;
import model.User;
import service.AuthService;
import service.ProductService;
import util.CSVHelper;
import util.DBConnection;
import util.PaginationUtil;

/**
 * Main class with Authentication for Product Management System
 * Handles user registration, login, and role-based menu access
 */
public class MainWithAuth {
    private static final ProductService productService = new ProductService();
    private static final AuthService authService = new AuthService();
    private static final CSVHelper csvHelper = new CSVHelper();
    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        displayStartupBanner();

        System.out.println("🔗 Connecting to database...");

        if (DBConnection.testConnection()) {
            System.out.println("✅ Database connection successful!\n");
            showAuthMenu();
        } else {
            System.out.println("❌ Failed to connect to database. Please check your configuration.");
            System.out.println("📝 Make sure MySQL is running and database exists.");
        }

        scanner.close();
    }

    private static void displayStartupBanner() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                              ║");
        System.out.println("║    🏪 PRODUCT MANAGEMENT SYSTEM v2.0 🏪                    ║");
        System.out.println("║                                                              ║");
        System.out.println("║    📦 Manage your inventory with ease                       ║");
        System.out.println("║    🚀 Fast • Reliable • Secure                              ║");
        System.out.println("║                                                              ║");
        System.out.println("║    🔐 Secure Login • Role-Based Access                      ║");
        System.out.println("║    💡 Admin & User Roles                                     ║");
        System.out.println("║                                                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    /**
     * Display authentication menu (Login/Register)
     */
    private static void showAuthMenu() {
        while (true) {
            System.out.println("\n🔐 ============ WELCOME ============ 🔐");
            System.out.println("1️⃣  Register");
            System.out.println("2️⃣  Login");
            System.out.println("0️⃣  Exit");
            System.out.println("=====================================");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    registerUser();
                    break;
                case "2":
                    loginUser();
                    if (currentUser != null) {
                        // Show appropriate menu based on role
                        if (currentUser.isAdmin()) {
                            showAdminMenu();
                        } else {
                            showUserMenu();
                        }
                        currentUser = null; // Logout after menu exit
                    }
                    break;
                case "0":
                    System.out.println("👋 Goodbye! Thank you for using Product Management System!");
                    return;
                default:
                    System.out.println("❌ Invalid choice! Please try again.");
            }
        }
    }

    /**
     * Handle user registration
     */
    private static void registerUser() {
        System.out.println("\n📝 ========== USER REGISTRATION ========== 📝");

        try {
            System.out.print("👤 First Name: ");
            String firstName = scanner.nextLine().trim();

            System.out.print("👤 Last Name: ");
            String lastName = scanner.nextLine().trim();

            System.out.print("📧 Email: ");
            String email = scanner.nextLine().trim();

            System.out.print("📞 Phone Number: ");
            String phoneNumber = scanner.nextLine().trim();

            System.out.print("🔒 Password (min 6 characters): ");
            String password = scanner.nextLine().trim();

            System.out.print("🔒 Confirm Password: ");
            String confirmPassword = scanner.nextLine().trim();

            if (!password.equals(confirmPassword)) {
                System.out.println("❌ Passwords do not match!");
                return;
            }

            System.out.println("\n🎭 Select Role:");
            System.out.println("1. 👤 User (View products only)");
            System.out.println("2. 🛠️ Admin (Full access)");
            System.out.print("Choose role (1-2): ");
            String roleChoice = scanner.nextLine().trim();

            String role;
            if (roleChoice.equals("1")) {
                role = "User";
            } else if (roleChoice.equals("2")) {
                role = "Admin";
            } else {
                System.out.println("❌ Invalid role choice! Defaulting to User.");
                role = "User";
            }

            User newUser = new User(firstName, lastName, email, phoneNumber, password, role);

            if (authService.registerUser(newUser)) {
                System.out.println("\n🎉 Registration completed successfully!");
                System.out.println("📧 You can now login with your email: " + email);
            }

        } catch (Exception e) {
            System.out.println("❌ Registration error: " + e.getMessage());
        }
    }

    /**
     * Handle user login
     */
    private static void loginUser() {
        System.out.println("\n🔐 ========== USER LOGIN ========== 🔐");

        try {
            System.out.print("📧 Email: ");
            String email = scanner.nextLine().trim();

            System.out.print("🔒 Password: ");
            String password = scanner.nextLine().trim();

            currentUser = authService.loginUser(email, password);

            if (currentUser != null) {
                System.out.println("\n✨ Login successful!");
                System.out.println("👤 Welcome, " + currentUser.getFirstName() + " " + currentUser.getLastName() + "!");
                System.out.println("🎭 Role: " + currentUser.getRole());
            }

        } catch (Exception e) {
            System.out.println("❌ Login error: " + e.getMessage());
            currentUser = null;
        }
    }

    /**
     * Show User Menu (Limited Access)
     */
    private static void showUserMenu() {
        while (true) {
            System.out.println("\n👤 ============ USER MENU ============ 👤");
            System.out.println("1. 👀 View All Products");
            System.out.println("2. 🔍 Search Product");
            System.out.println("3. 💰 Filter Products by Price Range");
            System.out.println("0. 🚪 Exit");
            System.out.println("=======================================");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewAllProducts();
                    break;
                case "2":
                    searchProducts();
                    break;
                case "3":
                    filterByPriceRange();
                    break;
                case "0":
                    System.out.println("👋 Logging out...");
                    return;
                default:
                    System.out.println("❌ Invalid choice! Please try again.");
            }
        }
    }

    /**
     * Show Admin Menu (Full Access)
     */
    private static void showAdminMenu() {
        while (true) {
            System.out.println("\n🛠️ ============ ADMIN MENU ============ 🛠️");
            System.out.println("1. ➕ Add Product");
            System.out.println("2. 👀 View All Products");
            System.out.println("3. 🔍 Search Product");
            System.out.println("4. ✏️  Update Product");
            System.out.println("5. 🗑️  Delete Product");
            System.out.println("6. 💰 Filter by Price Range");
            System.out.println("7. 💾 Save to CSV");
            System.out.println("0. 🚪 Exit");
            System.out.println("=========================================");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addProduct();
                    break;
                case "2":
                    viewAllProducts();
                    break;
                case "3":
                    searchProducts();
                    break;
                case "4":
                    updateProduct();
                    break;
                case "5":
                    deleteProduct();
                    break;
                case "6":
                    filterByPriceRange();
                    break;
                case "7":
                    saveToCSV();
                    break;
                case "0":
                    System.out.println("👋 Logging out...");
                    return;
                default:
                    System.out.println("❌ Invalid choice! Please try again.");
            }
        }
    }

    // ==================== PRODUCT MANAGEMENT METHODS ====================

    private static void viewAllProducts() {
        System.out.println("\n📦 ========== ALL PRODUCTS ========== 📦");

        List<Product> products = productService.getAllProducts();

        if (products == null || products.isEmpty()) {
            System.out.println("📭 No products found in the database.");
            return;
        }

        if (products.size() > 10) {
            viewProductsWithPagination(products);
        } else {
            displayProductsInTable(products);
        }
    }

    private static void viewProductsWithPagination(List<Product> products) {
        System.out.println("\n📖 Viewing products with pagination (Page navigation available)");
        PaginationUtil<Product> pagination = new PaginationUtil<>(products, 10);

        displayProductPage(pagination.getCurrentPageData());

        System.out.printf("\n📊 Page %d of %d | Total items: %d | Items per page: %d%n",
                pagination.getCurrentPage(),
                pagination.getTotalPages(),
                pagination.getTotalItems(),
                pagination.getPageSize());

        while (true) {
            System.out.println("\n🧭 Navigation Options:");
            System.out.println("  ⏮️  [F] First page");
            System.out.println("  ⬅️  [P] Previous page");
            System.out.println("  ➡️  [N] Next page");
            System.out.println("  ⏭️  [L] Last page");
            System.out.println("  🔢 [G] Go to page");
            System.out.println("  ❌ [X] Exit pagination");

            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "F":
                    pagination.firstPage();
                    displayProductPage(pagination.getCurrentPageData());
                    break;
                case "P":
                    if (pagination.previousPage()) {
                        displayProductPage(pagination.getCurrentPageData());
                    } else {
                        System.out.println("ℹ️ Already on the first page.");
                    }
                    break;
                case "N":
                    if (pagination.nextPage()) {
                        displayProductPage(pagination.getCurrentPageData());
                    } else {
                        System.out.println("ℹ️ Already on the last page.");
                    }
                    break;
                case "L":
                    pagination.lastPage();
                    displayProductPage(pagination.getCurrentPageData());
                    break;
                case "G":
                    System.out.printf("Enter page number (1-%d): ", pagination.getTotalPages());
                    try {
                        int pageNum = Integer.parseInt(scanner.nextLine().trim());
                        if (pagination.goToPage(pageNum)) {
                            displayProductPage(pagination.getCurrentPageData());
                        } else {
                            System.out.println("❌ Invalid page number!");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Please enter a valid number!");
                    }
                    break;
                case "X":
                    return;
                default:
                    System.out.println("❌ Invalid option! Please try again.");
                    continue;
            }

            System.out.printf("\n📊 Page %d of %d | Total items: %d%n",
                    pagination.getCurrentPage(),
                    pagination.getTotalPages(),
                    pagination.getTotalItems());
        }
    }

    private static void displayProductPage(List<Product> products) {
        System.out.println("\n📋 Products List:");
        System.out.println("==========================================");
        for (Product product : products) {
            System.out.printf("🆔 ID: %-3d | 📦 %-20s | 💰 $%-8.2f | 📊 %-3d | 🏷️  %s%n",
                    product.getId(), product.getName(), product.getPrice(),
                    product.getQuantity(), product.getCategory());
        }
        System.out.println("==========================================");
    }

    private static void displayProductsInTable(List<Product> products) {
        System.out.println("\n📋 Products List:");
        System.out.println("==========================================");
        for (Product product : products) {
            System.out.printf("🆔 ID: %-3d | 📦 %-20s | 💰 $%-8.2f | 📊 %-3d | 🏷️  %s%n",
                    product.getId(), product.getName(), product.getPrice(),
                    product.getQuantity(), product.getCategory());
        }
        System.out.println("==========================================");
    }

    private static void addProduct() {
        System.out.println("\n➕ ========== ADD NEW PRODUCT ========== ➕");

        try {
            System.out.print("📦 Product Name: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                throw new InvalidInputException("Product name cannot be empty");
            }

            System.out.print("💰 Price: $");
            double price = Double.parseDouble(scanner.nextLine().trim());
            if (price < 0) {
                throw new InvalidInputException("Price cannot be negative");
            }

            System.out.print("📊 Quantity: ");
            int quantity = Integer.parseInt(scanner.nextLine().trim());
            if (quantity < 0) {
                throw new InvalidInputException("Quantity cannot be negative");
            }

            System.out.print("🏷️ Category: ");
            String category = scanner.nextLine().trim();
            if (category.isEmpty()) {
                category = "General";
            }

            Product product = new Product(0, name, price, quantity, category);

            if (productService.addProduct(product)) {
                System.out.println("✅ Product added successfully!");
                csvHelper.syncDatabaseToCSV();
                System.out.println("🔄 Changes synchronized to CSV file");
            } else {
                System.out.println("❌ Failed to add product");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number format! Please enter valid numbers for price and quantity.");
        } catch (InvalidInputException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
        }
    }

    private static void searchProducts() {
        System.out.println("\n🔍 ========== SEARCH PRODUCTS ========== 🔍");
        System.out.println("1. 🔍 Search by Name");
        System.out.println("2. 🏷️ Search by Category");
        System.out.println("3. 🆔 Search by ID");
        System.out.println("0. 🔙 Exit");
        System.out.print("Choose search type: ");

        String choice = scanner.nextLine().trim();

        try {
            List<Product> results = null;
            Product singleResult = null;

            switch (choice) {
                case "1":
                    System.out.print("📦 Enter product name to search: ");
                    String name = scanner.nextLine().trim();
                    results = productService.searchProductsByName(name);
                    break;
                case "2":
                    System.out.print("🏷️ Enter category to search: ");
                    String category = scanner.nextLine().trim();
                    results = productService.searchProductsByCategory(category);
                    break;
                case "3":
                    System.out.print("🆔 Enter product ID to search: ");
                    int id = Integer.parseInt(scanner.nextLine().trim());
                    singleResult = productService.searchProductById(id);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("❌ Invalid choice!");
                    return;
            }

            if (singleResult != null) {
                System.out.println("\n🎯 Product Found:");
                List<Product> singleList = new ArrayList<>();
                singleList.add(singleResult);
                displayProductsInTable(singleList);
            } else if (results != null && !results.isEmpty()) {
                System.out.println("\n🎯 Search Results (" + results.size() + " found):");

                if (results.size() > 10) {
                    viewProductsWithPagination(results);
                } else {
                    displayProductsInTable(results);
                }
            } else {
                System.out.println("🔍 No products found matching your search criteria.");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number format!");
        } catch (Exception e) {
            System.out.println("❌ Error during search: " + e.getMessage());
        }
    }

    private static void updateProduct() {
        System.out.println("\n✏️ ========== UPDATE PRODUCT ========== ✏️");

        try {
            System.out.print("🆔 Enter Product ID to update: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            Product product = productService.searchProductById(id);
            if (product == null) {
                System.out.println("❌ Product with ID " + id + " not found!");
                return;
            }

            System.out.println("Current product details:");
            System.out.printf("📦 Name: %s%n", product.getName());
            System.out.printf("💰 Price: $%.2f%n", product.getPrice());
            System.out.printf("📊 Quantity: %d%n", product.getQuantity());
            System.out.printf("🏷️ Category: %s%n", product.getCategory());
            System.out.println("\nEnter new values (press Enter to keep current value):");

            String newName = getOptionalString("📦 New Name [" + product.getName() + "]: ");
            if (!newName.isEmpty()) {
                product.setName(newName);
            }

            String priceInput = getOptionalString("💰 New Price [" + product.getPrice() + "]: ");
            if (!priceInput.isEmpty()) {
                double newPrice = Double.parseDouble(priceInput);
                if (newPrice >= 0) {
                    product.setPrice(newPrice);
                }
            }

            String quantityInput = getOptionalString("📊 New Quantity [" + product.getQuantity() + "]: ");
            if (!quantityInput.isEmpty()) {
                int newQuantity = Integer.parseInt(quantityInput);
                if (newQuantity >= 0) {
                    product.setQuantity(newQuantity);
                }
            }

            String newCategory = getOptionalString("🏷️ New Category [" + product.getCategory() + "]: ");
            if (!newCategory.isEmpty()) {
                product.setCategory(newCategory);
            }

            if (productService.updateProduct(product)) {
                System.out.println("✅ Product updated successfully!");
                csvHelper.syncDatabaseToCSV();
                System.out.println("🔄 Changes synchronized to CSV file");
            } else {
                System.out.println("❌ Failed to update product");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number format!");
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private static String getOptionalString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static void deleteProduct() {
        System.out.println("\n🗑️ ========== DELETE PRODUCT ========== 🗑️");
        System.out.println("1. 🆔 Delete by ID");
        System.out.println("2. 📦 Delete by Name");
        System.out.println("3. 🏷️ Delete by Category");
        System.out.println("0. 🔙 Back to Menu");
        System.out.print("Choose delete option: ");

        String choice = scanner.nextLine().trim();

        try {
            switch (choice) {
                case "1":
                    deleteById();
                    break;
                case "2":
                    deleteByName();
                    break;
                case "3":
                    deleteByCategory();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("❌ Invalid choice!");
            }
        } catch (Exception e) {
            System.err.println("❌ Error in delete menu: " + e.getMessage());
        }
    }

    private static void deleteById() {
        try {
            System.out.print("🆔 Enter Product ID to delete: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            Product product = productService.searchProductById(id);
            if (product == null) {
                System.out.println("❌ Product with ID " + id + " not found!");
                return;
            }

            System.out.printf("🗑️ Are you sure you want to delete '%s' (ID: %d)? (yes/no): ",
                    product.getName(), product.getId());
            String confirm = scanner.nextLine().trim().toLowerCase();

            if (confirm.equals("yes") || confirm.equals("y")) {
                if (productService.removeProductById(id)) {
                    System.out.println("✅ Product deleted successfully!");
                    csvHelper.syncDatabaseToCSV();
                    System.out.println("🔄 Changes synchronized to CSV file");
                } else {
                    System.out.println("❌ Failed to delete product");
                }
            } else {
                System.out.println("🔙 Deletion cancelled.");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID format!");
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private static void deleteByName() {
        try {
            System.out.print("📦 Enter Product Name to delete: ");
            String name = scanner.nextLine().trim();

            List<Product> products = productService.searchProductsByName(name);
            if (products == null || products.isEmpty()) {
                System.out.println("❌ No products found with name containing: " + name);
                return;
            }

            if (products.size() == 1) {
                Product product = products.get(0);
                System.out.printf("🗑️ Found product: '%s' (ID: %d, Price: $%.2f)%n",
                        product.getName(), product.getId(), product.getPrice());
                System.out.print("Are you sure you want to delete this product? (yes/no): ");
                String confirm = scanner.nextLine().trim().toLowerCase();

                if (confirm.equals("yes") || confirm.equals("y")) {
                    if (productService.removeProductById(product.getId())) {
                        System.out.println("✅ Product deleted successfully!");
                        csvHelper.syncDatabaseToCSV();
                        System.out.println("🔄 Changes synchronized to CSV file");
                    } else {
                        System.out.println("❌ Failed to delete product");
                    }
                } else {
                    System.out.println("🔙 Deletion cancelled.");
                }
            } else {
                System.out.printf("🔍 Found %d products matching '%s':%n", products.size(), name);
                displayProductsInTable(products);

                System.out.print("🆔 Enter the exact ID of the product to delete: ");
                int id = Integer.parseInt(scanner.nextLine().trim());

                Product selectedProduct = products.stream()
                        .filter(p -> p.getId() == id)
                        .findFirst()
                        .orElse(null);

                if (selectedProduct != null) {
                    System.out.printf("🗑️ Delete '%s' (ID: %d)? (yes/no): ",
                            selectedProduct.getName(), selectedProduct.getId());
                    String confirm = scanner.nextLine().trim().toLowerCase();

                    if (confirm.equals("yes") || confirm.equals("y")) {
                        if (productService.removeProductById(id)) {
                            System.out.println("✅ Product deleted successfully!");
                            csvHelper.syncDatabaseToCSV();
                            System.out.println("🔄 Changes synchronized to CSV file");
                        } else {
                            System.out.println("❌ Failed to delete product");
                        }
                    } else {
                        System.out.println("🔙 Deletion cancelled.");
                    }
                } else {
                    System.out.println("❌ Invalid ID! Product not found in the search results.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID format!");
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private static void deleteByCategory() {
        try {
            System.out.print("🏷️ Enter Category to delete products from: ");
            String category = scanner.nextLine().trim();

            List<Product> products = productService.searchProductsByCategory(category);
            if (products == null || products.isEmpty()) {
                System.out.println("❌ No products found in category: " + category);
                return;
            }

            System.out.printf("🔍 Found %d products in category '%s':%n", products.size(), category);

            if (products.size() > 10) {
                viewProductsWithPagination(products);
            } else {
                displayProductsInTable(products);
            }

            System.out.println("\n🗑️ Delete Options:");
            System.out.println("1. 🆔 Delete specific product by ID");
            System.out.println("2. ⚠️  Delete ALL products in this category");
            System.out.println("0. 🔙 Cancel");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("🆔 Enter Product ID to delete: ");
                    int id = Integer.parseInt(scanner.nextLine().trim());

                    Product selectedProduct = products.stream()
                            .filter(p -> p.getId() == id)
                            .findFirst()
                            .orElse(null);

                    if (selectedProduct != null) {
                        System.out.printf("🗑️ Delete '%s' from category '%s'? (yes/no): ",
                                selectedProduct.getName(), category);
                        String confirm = scanner.nextLine().trim().toLowerCase();

                        if (confirm.equals("yes") || confirm.equals("y")) {
                            if (productService.removeProductById(id)) {
                                System.out.println("✅ Product deleted successfully!");
                                csvHelper.syncDatabaseToCSV();
                                System.out.println("🔄 Changes synchronized to CSV file");
                            } else {
                                System.out.println("❌ Failed to delete product");
                            }
                        } else {
                            System.out.println("🔙 Deletion cancelled.");
                        }
                    } else {
                        System.out.println("❌ Product ID not found in this category!");
                    }
                    break;

                case "2":
                    deleteAllInCategory(products, category);
                    break;

                case "0":
                    System.out.println("🔙 Deletion cancelled.");
                    break;

                default:
                    System.out.println("❌ Invalid choice!");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number format!");
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private static void deleteAllInCategory(List<Product> products, String category) {
        System.out.printf("⚠️ WARNING: This will delete ALL %d products in category '%s'%n", products.size(),
                category);
        System.out.print("⚠️ Type 'DELETE ALL' to confirm or anything else to cancel: ");
        String confirm = scanner.nextLine().trim();

        if (confirm.equals("DELETE ALL")) {
            int deleted = 0;
            for (Product product : products) {
                if (productService.removeProductById(product.getId())) {
                    deleted++;
                }
            }
            System.out.println("✅ Deleted " + deleted + " products from category '" + category + "'");
            csvHelper.syncDatabaseToCSV();
            System.out.println("🔄 Changes synchronized to CSV file");
        } else {
            System.out.println("🔙 Deletion cancelled.");
        }
    }

    private static void filterByPriceRange() {
        System.out.println("\n💰 ========== FILTER BY PRICE RANGE ========== 💰");

        try {
            System.out.print("💵 Enter minimum price: $");
            double minPrice = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("💵 Enter maximum price: $");
            double maxPrice = Double.parseDouble(scanner.nextLine().trim());

            if (minPrice < 0 || maxPrice < 0) {
                System.out.println("❌ Prices cannot be negative!");
                return;
            }

            if (minPrice > maxPrice) {
                System.out.println("❌ Minimum price cannot be greater than maximum price!");
                return;
            }

            List<Product> results = productService.searchProductsByPriceRange(minPrice, maxPrice);

            if (results != null && !results.isEmpty()) {
                System.out.printf("\n🎯 Found %d products in price range $%.2f - $%.2f:%n",
                        results.size(), minPrice, maxPrice);

                if (results.size() > 10) {
                    viewProductsWithPagination(results);
                } else {
                    displayProductsInTable(results);
                }
            } else {
                System.out.printf("🔍 No products found in price range $%.2f - $%.2f%n", minPrice, maxPrice);
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid price format! Please enter valid numbers.");
        } catch (Exception e) {
            System.out.println("❌ Error during price filter: " + e.getMessage());
        }
    }

    private static void saveToCSV() {
        try {
            System.out.print("📁 Enter CSV file path (or press Enter for default 'products.csv'): ");
            String filePath = scanner.nextLine().trim();

            if (filePath.isEmpty()) {
                filePath = "products.csv";
            }

            Path csvPath = Path.of(filePath);
            csvHelper.syncDatabaseToCSV();
            System.out.println("✅ Products exported to CSV successfully: " + csvPath.toAbsolutePath());

        } catch (Exception e) {
            System.err.println("❌ Error saving to CSV: " + e.getMessage());
        }
    }
}

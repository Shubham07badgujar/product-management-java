package main;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import exception.InvalidInputException;
import model.Product;
import model.User;
import service.AuthService;
import service.EmailService;
import service.ProductService;
import service.StockAlertService;
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
    private static final StockAlertService stockAlertService = new StockAlertService();
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
            System.out.println("1️⃣  Login");
            System.out.println("2️⃣  Register");
            System.out.println("3️⃣  Verify Mail");
            System.out.println("4️⃣  Exit");
            System.out.println("=====================================");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
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
                case "2":
                    registerUser();
                    break;
                case "3":
                    verifyEmail();
                    break;
                case "4":
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
            System.out.print("👤 Username: ");
            String username = scanner.nextLine().trim();

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

            User newUser = new User(username, firstName, lastName, email, phoneNumber, password, role);

            if (authService.registerUser(newUser)) {
                System.out.println("\n🎉 Registration completed successfully!");
                System.out.println("� Username: " + username);
                System.out.println("📧 Email: " + email);
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
            System.out.print("� Username: ");
            String username = scanner.nextLine().trim();

            System.out.print("�📧 Email: ");
            String email = scanner.nextLine().trim();

            System.out.print("🔒 Password: ");
            String password = scanner.nextLine().trim();

            currentUser = authService.loginUser(username, email, password);

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
     * Handle email verification with OTP
     */
    private static void verifyEmail() {
        System.out.println("\n📧 ========== VERIFY EMAIL ========== 📧");

        try {
            System.out.print("📧 Enter your registered email address: ");
            String email = scanner.nextLine().trim();

            if (email.isEmpty()) {
                System.out.println("❌ Email cannot be empty!");
                return;
            }

            // Send OTP to email
            if (authService.sendVerificationEmail(email)) {
                // OTP verification loop with resend option
                boolean verified = false;
                while (!verified) {
                    System.out.println("\n🔐 ========== OTP VERIFICATION ========== 🔐");
                    System.out.println("1️⃣  Enter OTP");
                    System.out.println("2️⃣  Resend OTP");
                    System.out.println("0️⃣  Cancel");
                    System.out.println("==========================================");
                    System.out.print("Choose an option: ");

                    String choice = scanner.nextLine().trim();

                    switch (choice) {
                        case "1":
                            // Enter OTP
                            System.out.print("\n🔢 Enter the 6-digit OTP sent to your email: ");
                            String otp = scanner.nextLine().trim();

                            if (otp.isEmpty()) {
                                System.out.println("❌ OTP cannot be empty!");
                                break;
                            }

                            // Verify OTP
                            if (authService.verifyEmail(email, otp)) {
                                System.out.println("✅ Email verification completed successfully!");
                                verified = true;
                            } else {
                                System.out.println("❌ Invalid or expired OTP. Please try again.");
                            }
                            break;

                        case "2":
                            // Resend OTP
                            System.out.println("\n📤 Resending OTP...");
                            if (authService.sendVerificationEmail(email)) {
                                System.out.println("✅ A new OTP has been sent to your email.");
                                System.out.println("📧 Please check your email for the new 6-digit code");
                            } else {
                                System.out.println("❌ Failed to resend OTP. Please try again later.");
                            }
                            break;

                        case "0":
                            // Cancel verification
                            System.out.println("🔙 Email verification cancelled.");
                            return;

                        default:
                            System.out.println("❌ Invalid choice! Please try again.");
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Verification error: " + e.getMessage());
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
            System.out.println("0. � Logout");
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
                    System.out.println("\n✅ You have been logged out successfully.");
                    System.out.println("👋 Goodbye, " + currentUser.getFirstName() + "!");
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
            System.out.println("7. 📊 Generate CSV Report and Send via Email");
            System.out.println("8. ⚠️  Check Low Stock & Send Alert");
            System.out.println("9. 📈 View Stock Status Report");
            System.out.println("0. 🔓 Logout");
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
                    generateAndEmailCSVReport();
                    break;
                case "8":
                    checkLowStockAndAlert();
                    break;
                case "9":
                    viewStockStatusReport();
                    break;
                case "0":
                    System.out.println("\n✅ You have been logged out successfully.");
                    System.out.println("👋 Goodbye, " + currentUser.getFirstName() + "!");
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

    /**
     * Generate CSV Report and Send via Email
     * Admin can send the report to their own email and optionally to another email
     */
    private static void generateAndEmailCSVReport() {
        System.out.println("\n📧 ========== GENERATE CSV REPORT AND SEND VIA EMAIL ========== 📧");

        try {
            // Check if email is configured
            String mailUser = System.getenv("MAIL_USER");
            String mailPass = System.getenv("MAIL_PASS");

            if (mailUser == null || mailPass == null || mailUser.isEmpty() || mailPass.isEmpty()) {
                System.out.println("❌ Email credentials not configured!");
                System.out.println("💡 Please set MAIL_USER and MAIL_PASS environment variables.");
                System.out.println("📄 See EMAIL_SETUP_GUIDE.md for instructions.");
                return;
            }

            // Step 1: Fetch all products from database
            System.out.println("\n📦 Step 1/4: Fetching products from database...");
            List<Product> products = productService.getAllProducts();

            if (products == null || products.isEmpty()) {
                System.out.println("❌ No products found in the database. Cannot generate report.");
                return;
            }

            System.out.println("✅ Found " + products.size() + " products");

            // Step 2: Generate CSV file with timestamp
            System.out.println("\n📝 Step 2/4: Generating CSV report...");
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String fileName = "product_report_" + timestamp + ".csv";
            String reportPath = "reports/" + fileName;

            // Create reports directory if it doesn't exist
            java.io.File reportsDir = new java.io.File("reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }

            // Write CSV file
            try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.File(reportPath))) {
                // Write CSV header
                writer.println("Product ID,Name,Price,Quantity,Category");

                // Write product data
                for (Product product : products) {
                    writer.printf("%d,\"%s\",%.2f,%d,\"%s\"%n",
                            product.getId(),
                            product.getName().replace("\"", "\"\""), // Escape quotes
                            product.getPrice(),
                            product.getQuantity(),
                            product.getCategory().replace("\"", "\"\"")); // Escape quotes
                }

                System.out.println("✅ CSV report generated: " + reportPath);
            }

            // Step 3: Send email to current admin
            System.out.println("\n📧 Step 3/4: Sending email to admin...");

            if (currentUser == null || currentUser.getEmail() == null) {
                System.out.println("❌ Current user email not available!");
                return;
            }

            String adminEmail = currentUser.getEmail();
            String subject = "Product Management Report - " + timestamp;
            String body = "Dear " + currentUser.getFirstName() + " " + currentUser.getLastName() + ",\n\n" +
                    "Product Report generated successfully on " +
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + ".\n\n" +
                    "Total Products: " + products.size() + "\n\n" +
                    "Please find the attached CSV file with complete product details.\n\n" +
                    "Summary:\n" +
                    "- Report Name: " + fileName + "\n" +
                    "- Total Products: " + products.size() + "\n" +
                    "- Generated By: " + currentUser.getUsername() + "\n\n" +
                    "Best regards,\n" +
                    "Product Management System";

            try {
                EmailService.sendReport(adminEmail, subject, body, reportPath);
                System.out.println("✅ Email sent successfully to: " + adminEmail);
            } catch (Exception e) {
                System.out.println("❌ Failed to send email to admin: " + e.getMessage());
                System.out.println("💡 CSV file is still saved locally at: " + reportPath);
            }

            // Step 4: Ask if admin wants to send to another email
            System.out.println("\n📤 Step 4/4: Send to another email address?");
            System.out.print("Would you like to send this report to another email? (yes/no): ");
            String sendToAnother = scanner.nextLine().trim().toLowerCase();

            if (sendToAnother.equals("yes") || sendToAnother.equals("y")) {
                System.out.print("📧 Enter recipient email address: ");
                String recipientEmail = scanner.nextLine().trim();

                if (recipientEmail.isEmpty()) {
                    System.out.println("❌ Email address cannot be empty!");
                } else if (!isValidEmailFormat(recipientEmail)) {
                    System.out.println("❌ Invalid email format!");
                } else {
                    try {
                        EmailService.sendReport(recipientEmail, subject, body, reportPath);
                        System.out.println("✅ Email sent successfully to: " + recipientEmail);
                    } catch (Exception e) {
                        System.out.println("❌ Failed to send email to " + recipientEmail + ": " + e.getMessage());
                    }
                }
            }

            // Summary
            System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║              📊 REPORT GENERATION SUMMARY                    ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            System.out.println("✅ Report generated successfully!");
            System.out.println("📄 File: " + reportPath);
            System.out.println("📊 Total Products: " + products.size());
            System.out.println("📧 Sent to: " + adminEmail);
            System.out.println("💾 Report saved locally for your records");

        } catch (Exception e) {
            System.out.println("\n❌ Error generating or sending report: " + e.getMessage());
            System.out.println("💡 Please check your database connection and email configuration.");
            e.printStackTrace();
        }
    }

    /**
     * Validate email format
     */
    private static boolean isValidEmailFormat(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Check Low Stock and Send Alert (Option 8)
     * Manually trigger low-stock check and optionally send email alert
     */
    private static void checkLowStockAndAlert() {
        System.out.println("\n⚠️  ========== LOW STOCK MONITORING ========== ⚠️");

        try {
            // Step 1: Check for low-stock products
            System.out.println("\n🔍 Step 1/3: Checking inventory for low-stock products...");
            List<Product> lowStockProducts = stockAlertService.checkLowStock();

            if (lowStockProducts == null || lowStockProducts.isEmpty()) {
                System.out.println("\n✅ Great news! All products are sufficiently stocked.");
                System.out.println("📊 No items require restocking at this time.");
                return;
            }

            // Step 2: Display low-stock products
            System.out.println("\n⚠️  Step 2/3: Low-Stock Products Found!");
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println(String.format("Found %d product(s) with low stock:\n", lowStockProducts.size()));

            for (int i = 0; i < lowStockProducts.size(); i++) {
                Product product = lowStockProducts.get(i);
                System.out.printf("%d. 📦 %s (ID: %d)\n", (i + 1), product.getName(), product.getId());
                System.out.printf("   • Category: %s\n", product.getCategory());
                System.out.printf("   • Current Stock: %d units\n", product.getQuantity());
                System.out.printf("   • Threshold: %d units\n", product.getThresholdLimit());
                System.out.printf("   • Status: %s\n",
                        product.getQuantity() == 0 ? "🔴 OUT OF STOCK" : "⚠️  LOW STOCK");

                if (product.getQuantity() < product.getThresholdLimit()) {
                    System.out.printf("   • Recommended Restock: %d units\n", product.getStockDeficit());
                }
                System.out.println();
            }

            System.out.println("═══════════════════════════════════════════════════════════");

            // Step 3: Ask if admin wants to send email alert
            System.out.println("\n📧 Step 3/3: Send Email Alert?");
            System.out.print("Would you like to send a low-stock alert email? (yes/no): ");
            String sendEmail = scanner.nextLine().trim().toLowerCase();

            if (sendEmail.equals("yes") || sendEmail.equals("y")) {
                // Check email configuration
                String mailUser = System.getenv("MAIL_USER");
                String mailPass = System.getenv("MAIL_PASS");

                if (mailUser == null || mailPass == null || mailUser.isEmpty() || mailPass.isEmpty()) {
                    System.out.println("\n❌ Email credentials not configured!");
                    System.out.println("💡 Please set MAIL_USER and MAIL_PASS environment variables.");
                    return;
                }

                // Get admin email
                String adminEmail = currentUser.getEmail();

                if (adminEmail == null || adminEmail.trim().isEmpty()) {
                    System.out.print("📧 Enter admin email address: ");
                    adminEmail = scanner.nextLine().trim();

                    if (adminEmail.isEmpty() || !isValidEmailFormat(adminEmail)) {
                        System.out.println("❌ Invalid email address!");
                        return;
                    }
                }

                // Send alert
                System.out.println("\n📤 Sending low-stock alert email...");
                boolean sent = stockAlertService.sendLowStockAlert(lowStockProducts, adminEmail);

                if (sent) {
                    System.out.println("\n✅ Low-stock alert email sent successfully!");
                    System.out.println("📧 Sent to: " + adminEmail);
                    System.out.println("💾 Alert log saved for your records");
                } else {
                    System.out.println("\n❌ Failed to send alert email.");
                    System.out.println("💡 Please check your email configuration and try again.");
                }
            } else {
                System.out.println("\n📋 Alert display completed. No email sent.");
            }

        } catch (Exception e) {
            System.out.println("\n❌ Error during low-stock check: " + e.getMessage());
            System.out.println("💡 Please check your database connection and try again.");
        }
    }

    /**
     * View Stock Status Report (Option 9)
     * Display comprehensive inventory status report
     */
    private static void viewStockStatusReport() {
        System.out.println("\n📈 ========== STOCK STATUS REPORT ========== 📈");

        try {
            String report = stockAlertService.getStockStatusReport();
            System.out.println(report);

            // Additional options
            System.out.println("\n📊 Report Options:");
            System.out.println("1. 🔄 Refresh Report");
            System.out.println("2. 📧 Email This Report");
            System.out.println("3. ⚙️  Configure Automated Monitoring");
            System.out.println("0. 🔙 Back to Menu");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewStockStatusReport(); // Recursive call to refresh
                    break;

                case "2":
                    emailStockReport(report);
                    break;

                case "3":
                    configureAutomatedMonitoring();
                    break;

                case "0":
                    return;

                default:
                    System.out.println("❌ Invalid choice!");
            }

        } catch (Exception e) {
            System.out.println("\n❌ Error generating stock status report: " + e.getMessage());
        }
    }

    /**
     * Email stock status report
     */
    private static void emailStockReport(String report) {
        try {
            System.out.print("\n📧 Enter recipient email address: ");
            String recipientEmail = scanner.nextLine().trim();

            if (recipientEmail.isEmpty() || !isValidEmailFormat(recipientEmail)) {
                System.out.println("❌ Invalid email address!");
                return;
            }

            String subject = "Inventory Stock Status Report - " +
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

            EmailService.sendReport(recipientEmail, subject, report, null);
            System.out.println("✅ Report sent successfully to: " + recipientEmail);

        } catch (Exception e) {
            System.out.println("❌ Failed to send report: " + e.getMessage());
        }
    }

    /**
     * Configure automated monitoring
     */
    private static void configureAutomatedMonitoring() {
        System.out.println("\n⚙️  ========== AUTOMATED MONITORING SETUP ========== ⚙️");
        System.out.println("\nℹ️  Automated monitoring checks inventory daily and sends alerts");
        System.out.println("   when products fall below their threshold levels.");

        if (stockAlertService.isMonitoringActive()) {
            System.out.println("\n🟢 Status: ACTIVE");
            System.out.print("\nWould you like to stop automated monitoring? (yes/no): ");
            String stop = scanner.nextLine().trim().toLowerCase();

            if (stop.equals("yes") || stop.equals("y")) {
                stockAlertService.stopAutomatedMonitoring();
                System.out.println("✅ Automated monitoring stopped.");
            }
        } else {
            System.out.println("\n🔴 Status: INACTIVE");
            System.out.print("\nWould you like to start automated monitoring? (yes/no): ");
            String start = scanner.nextLine().trim().toLowerCase();

            if (start.equals("yes") || start.equals("y")) {
                // Get admin email
                String adminEmail = currentUser.getEmail();

                if (adminEmail == null || adminEmail.trim().isEmpty()) {
                    System.out.print("📧 Enter admin email for alerts: ");
                    adminEmail = scanner.nextLine().trim();
                }

                if (!isValidEmailFormat(adminEmail)) {
                    System.out.println("❌ Invalid email address!");
                    return;
                }

                // Start monitoring
                stockAlertService.startAutomatedMonitoring(adminEmail);
                System.out.println("\n✅ Automated monitoring started successfully!");
                System.out.println("📧 Alerts will be sent to: " + adminEmail);
                System.out.println("⏰ Schedule: Every 24 hours");
                System.out.println("💡 The system will check stock levels daily and send alerts automatically.");
            }
        }
    }
}

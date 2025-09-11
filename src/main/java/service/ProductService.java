package service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.Product;

/**
 * ProductService class provides business logic for managing products.
 * This class acts as a service layer between the main application and the data
 * model.
 * It uses an in-memory ArrayList to store and manage product data.
 */
public class ProductService {
    private ArrayList<Product> products;

    /**
     * Constructor initializes the product list
     */
    public ProductService() {
        this.products = new ArrayList<>();
    }

    /**
     * Adds a new product to the inventory
     * 
     * @param product The product to add
     * @return true if product was added successfully, false if product with same ID
     *         already exists
     */
    public boolean addProduct(Product product) {
        // Check if product with same ID already exists
        if (searchProductById(product.getId()) != null) {
            return false; // Product with same ID already exists
        }
        return products.add(product);
    }

    /**
     * Removes a product from inventory by its ID
     * 
     * @param id The ID of the product to remove
     * @return true if product was removed successfully, false if product not found
     */
    public boolean removeProductById(int id) {
        return products.removeIf(product -> product.getId() == id);
    }

    /**
     * Updates the quantity of a specific product
     * 
     * @param id          The ID of the product to update
     * @param newQuantity The new quantity value
     * @return true if update was successful, false if product not found or invalid
     *         quantity
     */
    public boolean updateProductQuantity(int id, int newQuantity) {
        if (newQuantity < 0) {
            return false; // Invalid quantity
        }

        Product product = searchProductById(id);
        if (product != null) {
            product.setQuantity(newQuantity);
            return true;
        }
        return false; // Product not found
    }

    /**
     * Searches for products by name (case-insensitive partial match)
     * 
     * @param name The name or partial name to search for
     * @return List of products matching the search criteria
     */
    public List<Product> searchProductByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return products.stream()
                .filter(product -> product.getName().toLowerCase()
                        .contains(name.toLowerCase().trim()))
                .collect(Collectors.toList());
    }

    /**
     * Searches for a product by its unique ID
     * 
     * @param id The ID to search for
     * @return The product if found, null otherwise
     */
    public Product searchProductById(int id) {
        return products.stream()
                .filter(product -> product.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all products in the inventory
     * 
     * @return List of all products (copy of the internal list)
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    /**
     * Gets the total number of products in inventory
     * 
     * @return Total count of products
     */
    public int getTotalProductCount() {
        return products.size();
    }

    /**
     * Calculates the total inventory value (sum of all products' total values)
     * 
     * @return Total value of all products in inventory
     */
    public double getTotalInventoryValue() {
        return products.stream()
                .mapToDouble(Product::getTotalValue)
                .sum();
    }

    /**
     * Gets products that are out of stock (quantity = 0)
     * 
     * @return List of products with zero quantity
     */
    public List<Product> getOutOfStockProducts() {
        return products.stream()
                .filter(product -> !product.isInStock())
                .collect(Collectors.toList());
    }

    /**
     * Gets products with low stock (quantity <= threshold)
     * 
     * @param threshold The threshold below which stock is considered low
     * @return List of products with low stock
     */
    public List<Product> getLowStockProducts(int threshold) {
        return products.stream()
                .filter(product -> product.getQuantity() <= threshold && product.getQuantity() > 0)
                .collect(Collectors.toList());
    }

    /**
     * Updates the price of a specific product
     * 
     * @param id       The ID of the product to update
     * @param newPrice The new price value
     * @return true if update was successful, false if product not found or invalid
     *         price
     */
    public boolean updateProductPrice(int id, double newPrice) {
        if (newPrice < 0) {
            return false; // Invalid price
        }

        Product product = searchProductById(id);
        if (product != null) {
            product.setPrice(newPrice);
            return true;
        }
        return false; // Product not found
    }

    /**
     * Checks if the inventory is empty
     * 
     * @return true if no products exist, false otherwise
     */
    public boolean isEmpty() {
        return products.isEmpty();
    }

    /**
     * Clears all products from inventory
     */
    public void clearInventory() {
        products.clear();
    }
}

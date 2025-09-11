package model;

/**
 * Product class representing a product with id, name, price, and quantity.
 * This class encapsulates all product-related data and provides methods
 * for accessing and modifying product information.
 */
public class Product {
    private int id;
    private String name;
    private double price;
    private int quantity;

    /**
     * Constructor to initialize all fields
     * 
     * @param id       Product ID - unique identifier
     * @param name     Product name - descriptive name of the product
     * @param price    Product price - cost per unit
     * @param quantity Product quantity - available stock count
     */
    public Product(int id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * Getter for product ID
     * 
     * @return Product ID
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for product ID
     * 
     * @param id New product ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for product name
     * 
     * @return Product name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for product name
     * 
     * @param name New product name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for product price
     * 
     * @return Product price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Setter for product price
     * 
     * @param price New product price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Getter for product quantity
     * 
     * @return Product quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Setter for product quantity
     * 
     * @param quantity New product quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Calculates total value of this product (price * quantity)
     * 
     * @return Total value of the product in inventory
     */
    public double getTotalValue() {
        return price * quantity;
    }

    /**
     * Checks if the product is in stock
     * 
     * @return true if quantity > 0, false otherwise
     */
    public boolean isInStock() {
        return quantity > 0;
    }

    /**
     * Override toString() to display comprehensive product details
     * 
     * @return String representation of the product
     */
    @Override
    public String toString() {
        return String.format("Product [ID=%d, Name='%s', Price=%.2f, Quantity=%d, Total Value=%.2f, In Stock=%s]",
                id, name, price, quantity, getTotalValue(), isInStock() ? "Yes" : "No");
    }

    /**
     * Override equals() for proper comparison based on product ID
     * 
     * @param obj Object to compare with
     * @return true if products have same ID, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Product product = (Product) obj;
        return id == product.id;
    }

    /**
     * Override hashCode() to work with equals()
     * 
     * @return hash code based on product ID
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}

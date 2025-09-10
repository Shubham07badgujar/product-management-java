package model;

/**
 * Product class representing a product with id, name, and price.
 */
public class Product {
    private int id;
    private String name;
    private double price;

    /**
     * Constructor to initialize all fields
     * 
     * @param id    Product ID
     * @param name  Product name
     * @param price Product price
     */
    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
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
     * Getter for product name
     * 
     * @return Product name
     */
    public String getName() {
        return name;
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
     * Override toString() to display product details
     * 
     * @return String representation of the product
     */
    @Override
    public String toString() {
        return "Product [id=" + id + ", name=" + name + ", price=" + price + "]";
    }
}

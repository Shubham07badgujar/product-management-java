package exception;

public class ProductNotFoundException extends Exception {
    private final int productId;

    public ProductNotFoundException(int productId) {
        super("Product with ID " + productId + " not found in the database");
        this.productId = productId;
    }

    public ProductNotFoundException(String message) {
        super(message);
        this.productId = -1;
    }

    public int getProductId() {
        return productId;
    }
}
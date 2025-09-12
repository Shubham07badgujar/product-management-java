package service;

import java.util.ArrayList;
import java.util.List;

import model.Product;

public class ProductService {
    private ArrayList<Product> products = new ArrayList<>();

    public boolean addProduct(Product product) {
        if (product == null || searchProductById(product.getId()) != null)
            return false;
        return products.add(product);
    }

    public boolean removeProductById(int id) {
        return products.removeIf(product -> product.getId() == id);
    }

    public boolean updateProductQuantity(int id, int newQuantity) {
        if (newQuantity < 0)
            return false;
        Product product = searchProductById(id);
        if (product != null) {
            product.setQuantity(newQuantity);
            return true;
        }
        return false;
    }

    public Product searchProductById(int id) {
        return products.stream()
                .filter(product -> product.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }
}

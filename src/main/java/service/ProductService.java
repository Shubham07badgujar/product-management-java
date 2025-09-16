package service;

import java.util.List;

import dao.ProductDao;
import dao.ProductDaoImpl;
import model.Product;

public class ProductService {
    private final ProductDao productDao;

    public ProductService() {
        this.productDao = new ProductDaoImpl();
    }

    public boolean addProduct(Product product) {
        if (product == null) {
            System.err.println("Cannot add null product");
            return false;
        }

        if (productDao.existsById(product.getId())) {
            System.err.println("Product with ID " + product.getId() + " already exists");
            return false;
        }

        return productDao.create(product);
    }

    public boolean removeProductById(int id) {
        if (!productDao.existsById(id)) {
            System.err.println("Product with ID " + id + " does not exist");
            return false;
        }

        return productDao.deleteById(id);
    }

    public boolean updateProductQuantity(int id, int newQuantity) {
        if (newQuantity < 0) {
            System.err.println("Quantity cannot be negative");
            return false;
        }

        if (!productDao.existsById(id)) {
            System.err.println("Product with ID " + id + " does not exist");
            return false;
        }

        return productDao.updateQuantity(id, newQuantity);
    }

    public Product searchProductById(int id) {
        return productDao.findById(id);
    }

    public List<Product> getAllProducts() {
        return productDao.findAll();
    }

    public boolean updateProduct(Product product) {
        if (product == null) {
            System.err.println("Cannot update null product");
            return false;
        }

        if (!productDao.existsById(product.getId())) {
            System.err.println("Product with ID " + product.getId() + " does not exist");
            return false;
        }

        return productDao.update(product);
    }

    public int getTotalProductCount() {
        return productDao.getTotalCount();
    }

    public boolean productExists(int id) {
        return productDao.existsById(id);
    }
}

package service;

import java.util.List;

import dao.ProductDao;
import dao.ProductDaoImpl;
import exception.DatabaseOperationException;
import exception.ProductNotFoundException;
import exception.ProductValidationException;
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

        try {
            if (productDao.existsById(product.getId())) {
                System.err.println("Product with ID " + product.getId() + " already exists");
                return false;
            }

            return productDao.create(product);
        } catch (DatabaseOperationException e) {
            System.err.println("Database error while adding product: " + e.getMessage());
            return false;
        } catch (ProductValidationException e) {
            System.err.println("Product validation error: " + e.getMessage());
            return false;
        }
    }

    public boolean removeProductById(int id) {
        try {
            if (!productDao.existsById(id)) {
                System.err.println("Product with ID " + id + " does not exist");
                return false;
            }

            return productDao.deleteById(id);
        } catch (DatabaseOperationException e) {
            System.err.println("Database error while removing product: " + e.getMessage());
            return false;
        } catch (ProductNotFoundException e) {
            System.err.println("Product not found: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProductQuantity(int id, int newQuantity) {
        if (newQuantity < 0) {
            System.err.println("Quantity cannot be negative");
            return false;
        }

        try {
            if (!productDao.existsById(id)) {
                System.err.println("Product with ID " + id + " does not exist");
                return false;
            }

            return productDao.updateQuantity(id, newQuantity);
        } catch (DatabaseOperationException e) {
            System.err.println("Database error while updating quantity: " + e.getMessage());
            return false;
        } catch (ProductNotFoundException e) {
            System.err.println("Product not found: " + e.getMessage());
            return false;
        } catch (ProductValidationException e) {
            System.err.println("Validation error: " + e.getMessage());
            return false;
        }
    }

    public Product searchProductById(int id) {
        try {
            return productDao.findById(id);
        } catch (ProductNotFoundException e) {
            System.err.println("Product not found: " + e.getMessage());
            return null;
        } catch (DatabaseOperationException e) {
            System.err.println("Database error while searching product: " + e.getMessage());
            return null;
        }
    }

    public List<Product> getAllProducts() {
        try {
            return productDao.findAll();
        } catch (DatabaseOperationException e) {
            System.err.println("Database error while retrieving products: " + e.getMessage());
            return null;
        }
    }

    public boolean updateProduct(Product product) {
        if (product == null) {
            System.err.println("Cannot update null product");
            return false;
        }

        try {
            if (!productDao.existsById(product.getId())) {
                System.err.println("Product with ID " + product.getId() + " does not exist");
                return false;
            }

            return productDao.update(product);
        } catch (DatabaseOperationException e) {
            System.err.println("Database error while updating product: " + e.getMessage());
            return false;
        } catch (ProductNotFoundException e) {
            System.err.println("Product not found: " + e.getMessage());
            return false;
        } catch (ProductValidationException e) {
            System.err.println("Validation error: " + e.getMessage());
            return false;
        }
    }

    public int getTotalProductCount() {
        try {
            return productDao.getTotalCount();
        } catch (DatabaseOperationException e) {
            System.err.println("Database error while getting total count: " + e.getMessage());
            return 0;
        }
    }

    public boolean productExists(int id) {
        try {
            return productDao.existsById(id);
        } catch (DatabaseOperationException e) {
            System.err.println("Database error while checking product existence: " + e.getMessage());
            return false;
        }
    }
}

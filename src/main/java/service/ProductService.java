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
            System.err.println("❌ Cannot add null product");
            return false;
        }

        try {
            // No need to check for existing ID - database auto-generates unique IDs
            return productDao.create(product);
        } catch (DatabaseOperationException e) {
            System.err.println("🔌 Database error while adding product: " + e.getMessage());
            return false;
        } catch (ProductValidationException e) {
            System.err.println("⚠️  Product validation error: " + e.getMessage());
            return false;
        }
    }

    public boolean removeProductById(int id) {
        try {
            if (!productDao.existsById(id)) {
                System.err.println("🔍 Product with ID " + id + " does not exist");
                return false;
            }

            return productDao.deleteById(id);
        } catch (DatabaseOperationException e) {
            System.err.println("🔌 Database error while removing product: " + e.getMessage());
            return false;
        } catch (ProductNotFoundException e) {
            System.err.println("🔍 Product not found: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProductQuantity(int id, int newQuantity) {
        if (newQuantity < 0) {
            System.err.println("❌ Quantity cannot be negative");
            return false;
        }

        try {
            if (!productDao.existsById(id)) {
                System.err.println("🔍 Product with ID " + id + " does not exist");
                return false;
            }

            return productDao.updateQuantity(id, newQuantity);
        } catch (DatabaseOperationException e) {
            System.err.println("🔌 Database error while updating quantity: " + e.getMessage());
            return false;
        } catch (ProductNotFoundException e) {
            System.err.println("🔍 Product not found: " + e.getMessage());
            return false;
        } catch (ProductValidationException e) {
            System.err.println("⚠️  Validation error: " + e.getMessage());
            return false;
        }
    }

    public Product searchProductById(int id) {
        try {
            return productDao.findById(id);
        } catch (ProductNotFoundException e) {
            System.err.println("🔍 Product not found: " + e.getMessage());
            return null;
        } catch (DatabaseOperationException e) {
            System.err.println("🔌 Database error while searching product: " + e.getMessage());
            return null;
        }
    }

    public List<Product> getAllProducts() {
        try {
            return productDao.findAll();
        } catch (DatabaseOperationException e) {
            System.err.println("🔌 Database error while retrieving products: " + e.getMessage());
            return null;
        }
    }

    public boolean updateProduct(Product product) {
        if (product == null) {
            System.err.println("❌ Cannot update null product");
            return false;
        }

        try {
            if (!productDao.existsById(product.getId())) {
                System.err.println("🔍 Product with ID " + product.getId() + " does not exist");
                return false;
            }

            return productDao.update(product);
        } catch (DatabaseOperationException e) {
            System.err.println("🔌 Database error while updating product: " + e.getMessage());
            return false;
        } catch (ProductNotFoundException e) {
            System.err.println("🔍 Product not found: " + e.getMessage());
            return false;
        } catch (ProductValidationException e) {
            System.err.println("⚠️  Validation error: " + e.getMessage());
            return false;
        }
    }

    public int getTotalProductCount() {
        try {
            return productDao.getTotalCount();
        } catch (DatabaseOperationException e) {
            System.err.println("🔌 Database error while getting total count: " + e.getMessage());
            return 0;
        }
    }

    public boolean productExists(int id) {
        try {
            return productDao.existsById(id);
        } catch (DatabaseOperationException e) {
            System.err.println("🔌 Database error while checking product existence: " + e.getMessage());
            return false;
        }
    }

    public List<Product> searchProductsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.err.println("🔍 Search name cannot be empty");
            return null;
        }

        try {
            return productDao.findByName(name.trim());
        } catch (DatabaseOperationException e) {
            System.err.println("🔌 Database error while searching products by name: " + e.getMessage());
            return null;
        }
    }

    public List<Product> searchProductsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            System.err.println("🏷️  Search category cannot be empty");
            return null;
        }

        try {
            return productDao.findByCategory(category.trim());
        } catch (DatabaseOperationException e) {
            System.err.println("🔌 Database error while searching products by category: " + e.getMessage());
            return null;
        }
    }

    public List<Product> searchProductsByPriceRange(double minPrice, double maxPrice) {
        if (minPrice < 0 || maxPrice < 0) {
            System.err.println("💰 Prices cannot be negative");
            return null;
        }

        if (minPrice > maxPrice) {
            System.err.println("💰 Minimum price cannot be greater than maximum price");
            return null;
        }

        try {
            return productDao.findByPriceRange(minPrice, maxPrice);
        } catch (DatabaseOperationException e) {
            System.err.println("🔌 Database error while searching products by price range: " + e.getMessage());
            return null;
        }
    }
}

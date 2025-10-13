package dao;

import java.util.List;

import exception.DatabaseOperationException;
import exception.ProductNotFoundException;
import exception.ProductValidationException;
import model.Product;

public interface ProductDao {
        boolean create(Product product) throws DatabaseOperationException, ProductValidationException;

        Product findById(int id) throws ProductNotFoundException, DatabaseOperationException;

        List<Product> findAll() throws DatabaseOperationException;

        boolean update(Product product)
                        throws ProductNotFoundException, DatabaseOperationException, ProductValidationException;

        boolean updateQuantity(int id, int quantity)
                        throws ProductNotFoundException, DatabaseOperationException, ProductValidationException;

        boolean deleteById(int id) throws ProductNotFoundException, DatabaseOperationException;

        boolean existsById(int id) throws DatabaseOperationException;

        int getTotalCount() throws DatabaseOperationException;

        List<Product> findByName(String name) throws DatabaseOperationException;

        List<Product> findByCategory(String category) throws DatabaseOperationException;

        List<Product> findByPriceRange(double minPrice, double maxPrice) throws DatabaseOperationException;
}
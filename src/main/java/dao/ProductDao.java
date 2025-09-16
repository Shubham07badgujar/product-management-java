package dao;

import java.util.List;

import model.Product;

public interface ProductDao {
    boolean create(Product product);

    Product findById(int id);

    List<Product> findAll();

    boolean update(Product product);

    boolean updateQuantity(int id, int quantity);

    boolean deleteById(int id);

    boolean existsById(int id);

    int getTotalCount();
}
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Product;
import util.DBConnection;

public class ProductDaoImpl implements ProductDao {
    // Updated to use AUTO_INCREMENT - no need to provide ID manually
    private static final String INSERT_PRODUCT = "INSERT INTO products (name, price, quantity, category) VALUES (?, ?, ?, ?)";
    private static final String SELECT_PRODUCT_BY_ID = "SELECT id, name, price, quantity, category FROM products WHERE id = ?";
    private static final String SELECT_ALL_PRODUCTS = "SELECT id, name, price, quantity, category FROM products ORDER BY id";
    private static final String UPDATE_PRODUCT = "UPDATE products SET name = ?, price = ?, quantity = ?, category = ? WHERE id = ?";
    private static final String UPDATE_PRODUCT_QUANTITY = "UPDATE products SET quantity = ? WHERE id = ?";
    private static final String DELETE_PRODUCT = "DELETE FROM products WHERE id = ?";
    private static final String EXISTS_BY_ID = "SELECT 1 FROM products WHERE id = ? LIMIT 1";
    private static final String COUNT_PRODUCTS = "SELECT COUNT(*) FROM products";
    private static final String SELECT_PRODUCTS_BY_NAME = "SELECT id, name, price, quantity, category FROM products WHERE LOWER(name) LIKE LOWER(?) ORDER BY id";
    private static final String SELECT_PRODUCTS_BY_CATEGORY = "SELECT id, name, price, quantity, category FROM products WHERE LOWER(category) LIKE LOWER(?) ORDER BY id";
    private static final String SELECT_PRODUCTS_BY_PRICE_RANGE = "SELECT id, name, price, quantity, category FROM products WHERE price BETWEEN ? AND ? ORDER BY price, id";

    @Override
    public boolean create(Product product) {
        if (product == null) {
            return false;
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            connection = DBConnection.getConnection();
            // Use RETURN_GENERATED_KEYS to get the auto-generated ID
            statement = connection.prepareStatement(INSERT_PRODUCT, PreparedStatement.RETURN_GENERATED_KEYS);

            // No need to set ID - database will auto-generate it
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice());
            statement.setInt(3, product.getQuantity());
            statement.setString(4, product.getCategory());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Retrieve the auto-generated ID and set it to the product
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    product.setId(generatedId);
                    System.out.println("✅ Product created with ID: " + generatedId);
                }

                DBConnection.commitTransaction(connection);
                return true;
            } else {
                DBConnection.rollbackTransaction(connection);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error creating product: " + e.getMessage());
            DBConnection.rollbackTransaction(connection);
            return false;
        } finally {
            if (generatedKeys != null) {
                try {
                    generatedKeys.close();
                } catch (SQLException e) {
                    System.err.println("Error closing generated keys: " + e.getMessage());
                }
            }
            closeResources(connection, statement, null);
        }
    }

    @Override
    public Product findById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(SELECT_PRODUCT_BY_ID);
            statement.setInt(1, id);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToProduct(resultSet);
            }

        } catch (SQLException e) {
            System.err.println("Error finding product by ID: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }

        return null;
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(SELECT_ALL_PRODUCTS);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Product product = mapResultSetToProduct(resultSet);
                products.add(product);
            }

        } catch (SQLException e) {
            System.err.println("Error finding all products: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }

        return products;
    }

    @Override
    public boolean update(Product product) {
        if (product == null) {
            return false;
        }

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(UPDATE_PRODUCT);

            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice());
            statement.setInt(3, product.getQuantity());
            statement.setString(4, product.getCategory());
            statement.setInt(5, product.getId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                DBConnection.commitTransaction(connection);
                return true;
            } else {
                DBConnection.rollbackTransaction(connection);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            DBConnection.rollbackTransaction(connection);
            return false;
        } finally {
            closeResources(connection, statement, null);
        }
    }

    @Override
    public boolean updateQuantity(int id, int quantity) {
        if (quantity < 0) {
            return false;
        }

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(UPDATE_PRODUCT_QUANTITY);

            statement.setInt(1, quantity);
            statement.setInt(2, id);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                DBConnection.commitTransaction(connection);
                return true;
            } else {
                DBConnection.rollbackTransaction(connection);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error updating product quantity: " + e.getMessage());
            DBConnection.rollbackTransaction(connection);
            return false;
        } finally {
            closeResources(connection, statement, null);
        }
    }

    @Override
    public boolean deleteById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(DELETE_PRODUCT);
            statement.setInt(1, id);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                DBConnection.commitTransaction(connection);
                return true;
            } else {
                DBConnection.rollbackTransaction(connection);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            DBConnection.rollbackTransaction(connection);
            return false;
        } finally {
            closeResources(connection, statement, null);
        }
    }

    @Override
    public boolean existsById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(EXISTS_BY_ID);
            statement.setInt(1, id);

            resultSet = statement.executeQuery();
            return resultSet.next();

        } catch (SQLException e) {
            System.err.println("Error checking if product exists: " + e.getMessage());
            return false;
        } finally {
            closeResources(connection, statement, resultSet);
        }
    }

    @Override
    public int getTotalCount() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(COUNT_PRODUCTS);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting product count: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }

        return 0;
    }

    private Product mapResultSetToProduct(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        double price = resultSet.getDouble("price");
        int quantity = resultSet.getInt("quantity");
        String category = resultSet.getString("category");

        return new Product(id, name, price, quantity, category);
    }

    private void closeResources(Connection connection, PreparedStatement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                System.err.println("Error closing PreparedStatement: " + e.getMessage());
            }
        }

        DBConnection.closeConnection(connection);
    }

    @Override
    public List<Product> findByName(String name) {
        List<Product> products = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(SELECT_PRODUCTS_BY_NAME);
            statement.setString(1, "%" + name + "%");
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("quantity"));
                products.add(product);
            }

        } catch (SQLException e) {
            System.err.println("Error searching products by name: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }

        return products;
    }

    @Override
    public List<Product> findByCategory(String category) {
        List<Product> products = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(SELECT_PRODUCTS_BY_CATEGORY);
            statement.setString(1, "%" + category + "%");
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("category"));
                products.add(product);
            }

        } catch (SQLException e) {
            System.err.println("Error searching products by category: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }

        return products;
    }

    @Override
    public List<Product> findByPriceRange(double minPrice, double maxPrice) {
        List<Product> products = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(SELECT_PRODUCTS_BY_PRICE_RANGE);
            statement.setDouble(1, minPrice);
            statement.setDouble(2, maxPrice);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("category"));
                products.add(product);
            }

        } catch (SQLException e) {
            System.err.println("Error searching products by price range: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }

        return products;
    }
}

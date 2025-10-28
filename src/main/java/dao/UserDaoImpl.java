package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import exception.DatabaseOperationException;
import exception.UserNotFoundException;
import exception.UserValidationException;
import model.User;
import util.DBConnection;

public class UserDaoImpl implements UserDao {
    private static final String INSERT_USER = "INSERT INTO users (id, username, email, first_name, last_name, password, phone_number, role, verified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_USER_BY_ID = "SELECT id, username, email, first_name, last_name, password, phone_number, role, verified FROM users WHERE id = ?";
    private static final String SELECT_ALL_USERS = "SELECT id, username, email, first_name, last_name, password, phone_number, role, verified FROM users ORDER BY id";
    private static final String UPDATE_USER = "UPDATE users SET username = ?, email = ?, first_name = ?, last_name = ?, password = ?, phone_number = ?, role = ?, verified = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";
    private static final String EXISTS_BY_ID = "SELECT 1 FROM users WHERE id = ? LIMIT 1";
    private static final String COUNT_USERS = "SELECT COUNT(*) FROM users";
    private static final String SELECT_USERS_BY_USERNAME = "SELECT id, username, email, first_name, last_name, password, phone_number, role, verified FROM users WHERE LOWER(username) LIKE LOWER(?) ORDER BY id";
    private static final String SELECT_USERS_BY_EMAIL = "SELECT id, username, email, first_name, last_name, password, phone_number, role, verified FROM users WHERE LOWER(email) LIKE LOWER(?) ORDER BY id";
    private static final String SELECT_USER_BY_USERNAME_AND_EMAIL = "SELECT id, username, email, first_name, last_name, password, phone_number, role, verified FROM users WHERE LOWER(username) = LOWER(?) AND LOWER(email) = LOWER(?)";
    private static final String AUTHENTICATE_USER = "SELECT id, username, email, first_name, last_name, password, phone_number, role, verified FROM users WHERE LOWER(email) = LOWER(?) AND password = ?";
    private static final String AUTHENTICATE_USER_WITH_USERNAME_EMAIL = "SELECT id, username, email, first_name, last_name, password, phone_number, role, verified FROM users WHERE LOWER(username) = LOWER(?) AND LOWER(email) = LOWER(?) AND password = ?";
    private static final String UPDATE_PASSWORD = "UPDATE users SET password = ? WHERE id = ?";
    private static final String UPDATE_VERIFIED = "UPDATE users SET verified = ? WHERE email = ?";

    @Override
    public boolean create(User user) throws DatabaseOperationException, UserValidationException {
        if (user == null) {
            return false;
        }

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(INSERT_USER);

            statement.setInt(1, user.getId());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getFirstName());
            statement.setString(5, user.getLastName());
            statement.setString(6, user.getPassword());
            statement.setString(7, user.getPhoneNumber());
            statement.setString(8, user.getRole());
            statement.setBoolean(9, user.isVerified());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                DBConnection.commitTransaction(connection);
                return true;
            } else {
                DBConnection.rollbackTransaction(connection);
                return false;
            }

        } catch (SQLException e) {
            if (connection != null) {
                DBConnection.rollbackTransaction(connection);
            }
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        } finally {
            closeResources(connection, statement, null);
        }
    }

    @Override
    public User findById(int id) throws UserNotFoundException, DatabaseOperationException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(SELECT_USER_BY_ID);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            }

        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }

        return null;
    }

    @Override
    public List<User> findAll() throws DatabaseOperationException {
        List<User> users = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(SELECT_ALL_USERS);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                User user = mapResultSetToUser(resultSet);
                users.add(user);
            }

        } catch (SQLException e) {
            System.err.println("Error finding all users: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }

        return users;
    }

    @Override
    public boolean update(User user) throws UserNotFoundException, DatabaseOperationException, UserValidationException {
        if (user == null) {
            return false;
        }

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(UPDATE_USER);

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getFirstName());
            statement.setString(4, user.getLastName());
            statement.setString(5, user.getPassword());
            statement.setString(6, user.getPhoneNumber());
            statement.setString(7, user.getRole());
            statement.setBoolean(8, user.isVerified());
            statement.setInt(9, user.getId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                DBConnection.commitTransaction(connection);
                return true;
            } else {
                DBConnection.rollbackTransaction(connection);
                return false;
            }

        } catch (SQLException e) {
            if (connection != null) {
                DBConnection.rollbackTransaction(connection);
            }
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        } finally {
            closeResources(connection, statement, null);
        }
    }

    @Override
    public boolean deleteById(int id) throws UserNotFoundException, DatabaseOperationException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(DELETE_USER);
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
            if (connection != null) {
                DBConnection.rollbackTransaction(connection);
            }
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        } finally {
            closeResources(connection, statement, null);
        }
    }

    @Override
    public boolean existsById(int id) throws DatabaseOperationException {
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
            System.err.println("Error checking user existence: " + e.getMessage());
            return false;
        } finally {
            closeResources(connection, statement, resultSet);
        }
    }

    @Override
    public int getTotalCount() throws DatabaseOperationException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(COUNT_USERS);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

            return 0;

        } catch (SQLException e) {
            System.err.println("Error counting users: " + e.getMessage());
            return 0;
        } finally {
            closeResources(connection, statement, resultSet);
        }
    }

    @Override
    public List<User> findByUsername(String username) throws DatabaseOperationException {
        if (username == null || username.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<User> users = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(SELECT_USERS_BY_USERNAME);
            statement.setString(1, "%" + username + "%");
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("password"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("role"),
                        resultSet.getBoolean("verified"));
                users.add(user);
            }

        } catch (SQLException e) {
            System.err.println("Error searching users by username: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }

        return users;
    }

    @Override
    public List<User> findByEmail(String email) throws DatabaseOperationException {
        if (email == null || email.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<User> users = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(SELECT_USERS_BY_EMAIL);
            statement.setString(1, "%" + email + "%");
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("password"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("role"),
                        resultSet.getBoolean("verified"));
                users.add(user);
            }

        } catch (SQLException e) {
            System.err.println("Error searching users by email: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }

        return users;
    }

    @Override
    public User findByUsernameAndEmail(String username, String email)
            throws UserNotFoundException, DatabaseOperationException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(SELECT_USER_BY_USERNAME_AND_EMAIL);
            statement.setString(1, username);
            statement.setString(2, email);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            }

        } catch (SQLException e) {
            System.err.println("Error finding user by username and email: " + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }

        return null;
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
    public User authenticate(String username, String password)
            throws UserNotFoundException, DatabaseOperationException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(AUTHENTICATE_USER);
            statement.setString(1, username);
            statement.setString(2, password);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            } else {
                throw new UserNotFoundException("Invalid username or password");
            }

        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            throw new DatabaseOperationException("Authentication failed", e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
    }

    @Override
    public User authenticateWithUsernameAndEmail(String username, String email, String password)
            throws UserNotFoundException, DatabaseOperationException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(AUTHENTICATE_USER_WITH_USERNAME_EMAIL);
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, password);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            } else {
                throw new UserNotFoundException("Invalid username, email or password");
            }

        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            throw new DatabaseOperationException("Authentication failed", e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
        }
    }

    @Override
    public boolean updatePassword(int userId, String newPassword)
            throws UserNotFoundException, DatabaseOperationException, UserValidationException {
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new UserValidationException("password", "Password must be at least 6 characters long");
        }

        if (!existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(UPDATE_PASSWORD);
            statement.setString(1, newPassword.trim());
            statement.setInt(2, userId);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                DBConnection.commitTransaction(connection);
                return true;
            } else {
                DBConnection.rollbackTransaction(connection);
                return false;
            }

        } catch (SQLException e) {
            if (connection != null) {
                DBConnection.rollbackTransaction(connection);
            }
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        } finally {
            closeResources(connection, statement, null);
        }
    }

    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("id"),
                resultSet.getString("username"),
                resultSet.getString("email"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getString("password"),
                resultSet.getString("phone_number"),
                resultSet.getString("role"),
                resultSet.getBoolean("verified"));
    }

    /**
     * Update user's email verification status
     * 
     * @param email    User's email
     * @param verified New verification status
     * @return true if update successful, false otherwise
     * @throws DatabaseOperationException
     */
    public boolean updateVerified(String email, boolean verified) throws DatabaseOperationException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(UPDATE_VERIFIED);
            statement.setBoolean(1, verified);
            statement.setString(2, email);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                DBConnection.commitTransaction(connection);
                return true;
            } else {
                DBConnection.rollbackTransaction(connection);
                return false;
            }

        } catch (SQLException e) {
            if (connection != null) {
                DBConnection.rollbackTransaction(connection);
            }
            System.err.println("Error updating verification status: " + e.getMessage());
            return false;
        } finally {
            closeResources(connection, statement, null);
        }
    }
}
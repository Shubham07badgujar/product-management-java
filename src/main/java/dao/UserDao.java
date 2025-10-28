package dao;

import java.util.List;

import exception.DatabaseOperationException;
import exception.UserNotFoundException;
import exception.UserValidationException;
import model.User;

public interface UserDao {
    boolean create(User user) throws DatabaseOperationException, UserValidationException;

    User findById(int id) throws UserNotFoundException, DatabaseOperationException;

    List<User> findAll() throws DatabaseOperationException;

    boolean update(User user) throws UserNotFoundException, DatabaseOperationException, UserValidationException;

    boolean deleteById(int id) throws UserNotFoundException, DatabaseOperationException;

    boolean existsById(int id) throws DatabaseOperationException;

    int getTotalCount() throws DatabaseOperationException;

    List<User> findByUsername(String username) throws DatabaseOperationException;

    List<User> findByEmail(String email) throws DatabaseOperationException;

    User findByUsernameAndEmail(String username, String email) throws UserNotFoundException, DatabaseOperationException;

    User authenticate(String username, String password) throws UserNotFoundException, DatabaseOperationException;

    User authenticateWithUsernameAndEmail(String username, String email, String password)
            throws UserNotFoundException, DatabaseOperationException;

    boolean updatePassword(int userId, String newPassword)
            throws UserNotFoundException, DatabaseOperationException, UserValidationException;
}